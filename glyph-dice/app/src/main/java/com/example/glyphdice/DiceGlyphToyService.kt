package com.example.glyphdice

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixManager
import com.nothing.ketchum.GlyphMatrixObject
import com.nothing.ketchum.GlyphToy
import kotlin.random.Random

/**
 * A Glyph Toy that shows a dice face on the Phone (4a) Pro matrix and rolls a
 * new number when the user long-presses the Glyph button.
 *
 * Registered in AndroidManifest.xml with the `com.nothing.glyph.TOY` action so
 * the system lists it under Glyph Interface > Glyph Toys.
 *
 * NOTE: This targets the documented Glyph Matrix SDK API. Once you add the real
 * GlyphMatrixSDK.aar to app/libs/, double-check these method signatures against
 * the SDK's `GlyphMatrixService.kt` wrapper in the official example project and
 * adjust if Android Studio flags anything.
 */
class DiceGlyphToyService : Service() {

    private var glyphManager: GlyphMatrixManager? = null
    private var currentFace: Int = 1
    private var registered: Boolean = false

    // ---- Service lifecycle -------------------------------------------------

    override fun onCreate() {
        super.onCreate()
        currentFace = loadFace()

        glyphManager = GlyphMatrixManager.getInstance(applicationContext)
        glyphManager?.init(object : GlyphMatrixManager.Callback {
            override fun onServiceConnected(name: ComponentName?) {
                // Tell the SDK which device matrix we are drawing to.
                glyphManager?.register(Glyph.DEVICE_25111p) // Phone (4a) Pro
                registered = true
                showFace(currentFace)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                registered = false
            }
        })
    }

    override fun onBind(intent: Intent?): IBinder = messenger.binder

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        try {
            glyphManager?.unInit()
        } catch (_: Exception) {
        }
        glyphManager = null
        super.onDestroy()
    }

    // ---- Glyph button / AOD events ----------------------------------------

    private val eventHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                GlyphToy.MSG_GLYPH_TOY -> {
                    val event = msg.data?.getString(GlyphToy.MSG_GLYPH_TOY_DATA)
                    when (event) {
                        GlyphToy.EVENT_CHANGE -> rollDice()   // long press
                        GlyphToy.EVENT_AOD -> showFace(currentFace) // AOD refresh
                    }
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private val messenger = Messenger(eventHandler)

    // ---- Behavior ----------------------------------------------------------

    private fun rollDice() {
        // A tiny "tumble" so the roll feels alive, then land on the result.
        val result = Random.nextInt(1, 7)
        val tumble = intArrayOf(
            Random.nextInt(1, 7),
            Random.nextInt(1, 7),
            result
        )
        var delay = 0L
        for (face in tumble) {
            eventHandler.postDelayed({ showFace(face) }, delay)
            delay += 120L
        }
        currentFace = result
        saveFace(result)
    }

    private fun showFace(value: Int) {
        val manager = glyphManager ?: return
        if (!registered) return

        val bitmap = DiceRenderer.renderFace(value)
        val obj = GlyphMatrixObject.Builder()
            .setImageSource(bitmap)
            .setPosition(0, 0)
            .setScale(100)
            .setBrightness(255)
            .build()

        val frame = GlyphMatrixFrame.Builder()
            .addTop(obj)
            .build(applicationContext)

        manager.setMatrixFrame(frame.render())
    }

    // ---- Persistence so AOD/relaunch shows the last roll -------------------

    private fun saveFace(value: Int) {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
            .putInt(KEY_FACE, value).apply()
    }

    private fun loadFace(): Int =
        getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_FACE, 1)

    private companion object {
        const val PREFS = "glyph_dice"
        const val KEY_FACE = "face"
    }
}
