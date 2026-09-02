package com.paddysystems.wearfolio.ml

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import java.io.File
import java.nio.FloatBuffer
import kotlin.math.max
import kotlin.math.roundToInt

class IsNetBackgroundRemover(
    private val context: Context
) : AutoCloseable {

    companion object {
        private const val MODEL_SIZE =
            1024

        /*
         * Outfit rendering does not need
         * a 12 MP transparent PNG.
         *
         * Keep enough resolution for
         * excellent phone/tablet rendering.
         */
        private const val MAX_CUTOUT_SIZE =
            1600
    }

    private val environment =
        OrtEnvironment.getEnvironment()

    private val session: OrtSession =
        environment.createSession(
            prepareModelFile()
                .absolutePath,

            OrtSession.SessionOptions()
        )

    fun createCutout(
        imageFile: File,
        itemId: String
    ): File {

        val sourceBitmap =
            loadScaledBitmap(
                file = imageFile,
                maxDimension =
                    MAX_CUTOUT_SIZE
            )

        val modelBitmap =
            Bitmap.createScaledBitmap(
                sourceBitmap,
                MODEL_SIZE,
                MODEL_SIZE,
                true
            )

        val input =
            createInputTensorData(
                modelBitmap
            )

        val mask =
            runModel(input)

        val cutout =
            applyMask(
                sourceBitmap =
                    sourceBitmap,
                mask = mask
            )

        val directory =
            File(
                context.filesDir,
                "wardrobe_cutouts"
            )

        directory.mkdirs()

        val outputFile =
            File(
                directory,
                "$itemId.png"
            )

        outputFile.outputStream()
            .use { output ->
                check(
                    cutout.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        output
                    )
                ) {
                    "Could not save cut-out PNG"
                }
            }

        if (
            modelBitmap !==
            sourceBitmap
        ) {
            modelBitmap.recycle()
        }

        sourceBitmap.recycle()
        cutout.recycle()

        return outputFile
    }

    private fun createInputTensorData(
        bitmap: Bitmap
    ): FloatArray {

        val pixelCount =
            MODEL_SIZE *
                    MODEL_SIZE

        val pixels =
            IntArray(pixelCount)

        bitmap.getPixels(
            pixels,
            0,
            MODEL_SIZE,
            0,
            0,
            MODEL_SIZE,
            MODEL_SIZE
        )

        val input =
            FloatArray(
                pixelCount * 3
            )

        pixels.forEachIndexed {
                index,
                pixel ->

            /*
             * IS-Net preprocessing:
             *
             * RGB
             * x / 255 - 0.5
             */

            input[index] =
                Color.red(pixel) /
                        255f -
                        0.5f

            input[
                pixelCount +
                        index
            ] =
                Color.green(pixel) /
                        255f -
                        0.5f

            input[
                (pixelCount * 2) +
                        index
            ] =
                Color.blue(pixel) /
                        255f -
                        0.5f
        }

        return input
    }

    private fun runModel(
        input: FloatArray
    ): FloatArray {

        OnnxTensor.createTensor(
            environment,
            FloatBuffer.wrap(input),
            longArrayOf(
                1,
                3,
                MODEL_SIZE.toLong(),
                MODEL_SIZE.toLong()
            )
        ).use { inputTensor ->

            session.run(
                mapOf(
                    "input_image" to
                            inputTensor
                )
            ).use { results ->

                val output =
                    results
                        .get(
                            "output_image"
                        )
                        .orElseThrow()

                val outputTensor =
                    output as
                            OnnxTensor

                val buffer =
                    outputTensor
                        .floatBuffer
                        ?: error(
                            "IS-Net output is not float32"
                        )

                val values =
                    FloatArray(
                        buffer.remaining()
                    )

                buffer.get(values)

                return normalizeMask(
                    values
                )
            }
        }
    }

    private fun normalizeMask(
        values: FloatArray
    ): FloatArray {

        var minimum =
            Float.POSITIVE_INFINITY

        var maximum =
            Float.NEGATIVE_INFINITY

        values.forEach { value ->
            if (value < minimum) {
                minimum = value
            }

            if (value > maximum) {
                maximum = value
            }
        }

        val range =
            maximum - minimum

        if (range <= 0.000001f) {
            return FloatArray(
                values.size
            )
        }

        return FloatArray(
            values.size
        ) { index ->

            (
                    (
                            values[index] -
                                    minimum
                            ) /
                            range
                    ).coerceIn(
                    0f,
                    1f
                )
        }
    }

    private fun applyMask(
        sourceBitmap: Bitmap,
        mask: FloatArray
    ): Bitmap {

        val maskPixels =
            IntArray(
                MODEL_SIZE *
                        MODEL_SIZE
            )

        mask.forEachIndexed {
                index,
                value ->

            val alpha =
                (
                        value *
                                255f
                        )
                    .roundToInt()
                    .coerceIn(
                        0,
                        255
                    )

            maskPixels[index] =
                Color.argb(
                    alpha,
                    255,
                    255,
                    255
                )
        }

        val maskBitmap =
            Bitmap.createBitmap(
                maskPixels,
                MODEL_SIZE,
                MODEL_SIZE,
                Bitmap.Config.ARGB_8888
            )

        val scaledMask =
            Bitmap.createScaledBitmap(
                maskBitmap,
                sourceBitmap.width,
                sourceBitmap.height,
                true
            )

        val pixelCount =
            sourceBitmap.width *
                    sourceBitmap.height

        val sourcePixels =
            IntArray(pixelCount)

        val scaledMaskPixels =
            IntArray(pixelCount)

        sourceBitmap.getPixels(
            sourcePixels,
            0,
            sourceBitmap.width,
            0,
            0,
            sourceBitmap.width,
            sourceBitmap.height
        )

        scaledMask.getPixels(
            scaledMaskPixels,
            0,
            sourceBitmap.width,
            0,
            0,
            sourceBitmap.width,
            sourceBitmap.height
        )

        val outputPixels =
            IntArray(pixelCount)

        for (
        index in
        0 until pixelCount
        ) {
            val source =
                sourcePixels[index]

            val alpha =
                Color.alpha(
                    scaledMaskPixels[
                        index
                    ]
                )

            outputPixels[index] =
                Color.argb(
                    alpha,
                    Color.red(source),
                    Color.green(source),
                    Color.blue(source)
                )
        }

        val output =
            Bitmap.createBitmap(
                outputPixels,
                sourceBitmap.width,
                sourceBitmap.height,
                Bitmap.Config.ARGB_8888
            )

        maskBitmap.recycle()

        if (
            scaledMask !==
            maskBitmap
        ) {
            scaledMask.recycle()
        }

        return output
    }

    private fun loadScaledBitmap(
        file: File,
        maxDimension: Int
    ): Bitmap {

        val bounds =
            BitmapFactory.Options().apply {
                inJustDecodeBounds =
                    true
            }

        BitmapFactory.decodeFile(
            file.absolutePath,
            bounds
        )

        check(
            bounds.outWidth > 0 &&
                    bounds.outHeight > 0
        ) {
            "Could not decode source image"
        }

        var sampleSize = 1

        while (
            max(
                bounds.outWidth,
                bounds.outHeight
            ) /
            (
                    sampleSize * 2
                    ) >=
            maxDimension
        ) {
            sampleSize *= 2
        }

        val options =
            BitmapFactory.Options().apply {
                inSampleSize =
                    sampleSize

                inPreferredConfig =
                    Bitmap.Config.ARGB_8888
            }

        val decoded =
            BitmapFactory.decodeFile(
                file.absolutePath,
                options
            )
                ?: error(
                    "Could not decode source image"
                )

        val currentMax =
            max(
                decoded.width,
                decoded.height
            )

        if (
            currentMax <=
            maxDimension
        ) {
            return decoded
        }

        val scale =
            maxDimension.toFloat() /
                    currentMax.toFloat()

        val resized =
            Bitmap.createScaledBitmap(
                decoded,

                (
                        decoded.width *
                                scale
                        ).roundToInt(),

                (
                        decoded.height *
                                scale
                        ).roundToInt(),

                true
            )

        if (
            resized !== decoded
        ) {
            decoded.recycle()
        }

        return resized
    }

    private fun prepareModelFile():
            File {

        val directory =
            File(
                context.filesDir,
                "models"
            )

        directory.mkdirs()

        val file =
            File(
                directory,
                "isnet_general_use_quantized.onnx"
            )

        if (!file.exists()) {
            context.assets
                .open(
                    "models/isnet_general_use_quantized.onnx"
                )
                .use { input ->

                    file.outputStream()
                        .use { output ->
                            input.copyTo(
                                output
                            )
                        }
                }
        }

        return file
    }

    override fun close() {
        session.close()
    }
}