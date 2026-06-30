package com.example.glyphdice

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

/**
 * Tiny companion screen. Its only job is to send you to the Glyph Toys manager
 * so you can activate the Dice toy. The toy itself lives in
 * [DiceGlyphToyService] and runs on the Glyph Matrix, not on screen.
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_add_toy).setOnClickListener {
            openGlyphToysManager()
        }
    }

    /** Opens the system Glyph Toys manager (system version 20250829+). */
    private fun openGlyphToysManager() {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.nothing.thirdparty",
                    "com.nothing.thirdparty.matrix.toys.manager.ToysManagerActivity"
                )
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.add_toy_fallback),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
