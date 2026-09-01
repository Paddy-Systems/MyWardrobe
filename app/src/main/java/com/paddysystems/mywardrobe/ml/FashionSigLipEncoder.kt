package com.paddysystems.mywardrobe.ml

import android.content.Context
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.io.File

class FashionSigLipEncoder(
    private val context: Context
) : AutoCloseable {

    private val environment = OrtEnvironment.getEnvironment()

    private val session: OrtSession by lazy {
        val modelFile = prepareModelFile()

        environment.createSession(
            modelFile.absolutePath,
            OrtSession.SessionOptions()
        )
    }

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

    override fun close() {
//        if (sessionInitialized()) {
//            session.close()
//        }
    }

//    private fun sessionInitialized(): Boolean {
//        return try {
//            session
//            true
//        } catch (_: Exception) {
//            false
//        }
//    }
}