package com.porcupinetest

import android.content.Context
import android.util.Log
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.facebook.react.ReactApplication
import java.io.File
import java.io.FileOutputStream

class PorcupineManagerWrapper(private val context: Context) {

    private var porcupineManager: PorcupineManager? = null

    fun initialize(keywordAssetName: String, modelAssetName: String, sensitivity: Float) {
        Log.d("PorcupineManager", "🚀 Initializing Porcupine...")
        val keywordPath = getAssetFilePath(context, keywordAssetName)
        val modelPath = getAssetFilePath(context, modelAssetName)

        porcupineManager = PorcupineManager.Builder()
            .setAccessKey("ILvB3KckbjtjYK9zGJ+zQmZ0QjXMWAramFqmqOMuwNjBQLX4bZ77JQ==") // Replace with valid key
            .setKeywordPath(keywordPath)
            .setModelPath(modelPath)
            .setSensitivity(sensitivity)
            .build(context, PorcupineManagerCallback {
                Log.d("PorcupineManager", "🔥 Wake word detected!")
                sendWakeEventToJS()
            })

        Log.d("PorcupineManager", "✅ Initialized successfully")
    }

    fun start() {
        Log.d("PorcupineManager", "▶️ Starting mic...")
        porcupineManager?.start() ?: throw IllegalStateException("PorcupineManager not initialized")
    }

    fun stop() {
        Log.d("PorcupineManager", "⏹️ Stopping mic...")
        porcupineManager?.stop() ?: throw IllegalStateException("PorcupineManager not initialized")
    }

    fun delete() {
        Log.d("PorcupineManager", "🧹 Deleting Porcupine instance...")
        porcupineManager?.delete()
        porcupineManager = null
    }

    // Extracts .ppn/.pv from assets to internal storage
    private fun getAssetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists()) return file.absolutePath

        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
            }
        }

        Log.d("PorcupineManager", "📦 Extracted asset: $assetName → ${file.absolutePath}")
        return file.absolutePath
    }

    // JS Event emitter
    private fun sendWakeEventToJS() {
        try {
            val reactContext = (context.applicationContext as ReactApplication)
                .reactNativeHost.reactInstanceManager.currentReactContext

            reactContext?.getJSModule(RCTDeviceEventEmitter::class.java)
                ?.emit("WakeWordDetected", null)

        } catch (e: Exception) {
            Log.e("PorcupineManager", "❌ Failed to send event to JS", e)
        }
    }
}
