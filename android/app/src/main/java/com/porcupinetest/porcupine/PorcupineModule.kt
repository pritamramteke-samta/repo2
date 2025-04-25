package com.porcupinetest

import android.content.Context
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.turbomodule.core.interfaces.TurboModule
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

@ReactModule(name = PorcupineModule.NAME)
class PorcupineModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), TurboModule {

    companion object {
        const val NAME = "PorcupineModule"
    }

    private val porcupineManager = PorcupineManagerWrapper(reactContext.applicationContext)

    override fun getName(): String = NAME

    @ReactMethod
    fun initialize(keywordPath: String, modelPath: String, sensitivity: Double, promise: Promise) {
        try {
            porcupineManager.initialize(keywordPath, modelPath, sensitivity.toFloat())
            promise.resolve("Porcupine initialized successfully")
        } catch (e: Exception) {
            promise.reject("INIT_ERROR", "Failed to initialize Porcupine", e)
        }
    }

    @ReactMethod
    fun start(promise: Promise) {
        try {
            porcupineManager.start()
            promise.resolve("Porcupine started")
        } catch (e: Exception) {
            promise.reject("START_ERROR", "Failed to start Porcupine", e)
        }
    }

    @ReactMethod
    fun stop(promise: Promise) {
        try {
            porcupineManager.stop()
            promise.resolve("Porcupine stopped")
        } catch (e: Exception) {
            promise.reject("STOP_ERROR", "Failed to stop Porcupine", e)
        }
    }

    @ReactMethod
    fun delete(promise: Promise) {
        try {
            porcupineManager.delete()
            promise.resolve("Porcupine deleted")
        } catch (e: Exception) {
            promise.reject("DELETE_ERROR", "Failed to delete Porcupine", e)
        }
    }
}