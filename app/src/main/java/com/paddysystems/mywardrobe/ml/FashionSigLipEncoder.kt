package com.paddysystems.mywardrobe.ml

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import java.io.File
import java.nio.FloatBuffer

class FashionSigLipEncoder(
    private val context: Context
) : AutoCloseable {

    private val environment = OrtEnvironment.getEnvironment()

    private val sessionDelegate = lazy {
        val modelFile = prepareModelFile()

        environment.createSession(
            modelFile.absolutePath,
            OrtSession.SessionOptions()
        )
    }

    private val session: OrtSession
        get() = sessionDelegate.value

    private fun prepareModelFile(): File {
        val modelDirectory = File(
            context.filesDir,
            "models"
        )

        modelDirectory.mkdirs()

        val modelFile = File(
            modelDirectory,
            "vision_model_fp16.onnx"
        )

        if (!modelFile.exists()) {
            context.assets
                .open("models/vision_model_fp16.onnx")
                .use { input ->
                    modelFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
        }

        return modelFile
    }

    fun modelInfo(): String {
        return buildString {
            appendLine("Inputs:")

            session.inputNames.forEach { name ->
                appendLine("- $name")
            }

            appendLine("Outputs:")

            session.outputNames.forEach { name ->
                appendLine("- $name")
            }
        }
    }

    fun encode(imageUri: Uri): FloatArray {
        val bitmap = context.contentResolver
            .openInputStream(imageUri)
            ?.use { input ->
                BitmapFactory.decodeStream(input)
            }
            ?: error("Could not decode image")

        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            224,
            224,
            true
        )

        val pixelsPerChannel = 224 * 224
        val input = FloatArray(
            3 * pixelsPerChannel
        )

        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = resizedBitmap.getPixel(x, y)
                val pixelIndex = y * 224 + x

                val red = Color.red(pixel) / 255f
                val green = Color.green(pixel) / 255f
                val blue = Color.blue(pixel) / 255f

                input[pixelIndex] =
                    (red - 0.5f) / 0.5f

                input[pixelsPerChannel + pixelIndex] =
                    (green - 0.5f) / 0.5f

                input[(2 * pixelsPerChannel) + pixelIndex] =
                    (blue - 0.5f) / 0.5f
            }
        }

        bitmap.recycle()

        if (resizedBitmap !== bitmap) {
            resizedBitmap.recycle()
        }

        OnnxTensor.createTensor(
            environment,
            FloatBuffer.wrap(input),
            longArrayOf(
                1,
                3,
                224,
                224
            )
        ).use { inputTensor ->

            session.run(
                mapOf(
                    "pixel_values" to inputTensor
                )
            ).use { results ->

                val output = results
                    .get("image_embeds")
                    .orElseThrow()

                val outputTensor =
                    output as OnnxTensor

                val buffer =
                    outputTensor.floatBuffer
                        ?: error("image_embeds is not a float tensor")

                val embedding =
                    FloatArray(buffer.remaining())

                buffer.get(embedding)

                return embedding
            }
        }
    }

    override fun close() {
        if (sessionDelegate.isInitialized()) {
            sessionDelegate.value.close()
        }
    }
}
