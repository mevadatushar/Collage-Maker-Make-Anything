package com.collagemaker_makeanything.Dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.collagemaker_makeanything.R

class ColorPickerDialog(private val context: Context, private val onColorSelected: (Int) -> Unit) {

    private val colors = listOf(
        R.color.white,
        R.color.red,
        R.color.green,
        R.color.blue,
        R.color.yellow,
        R.color.black,
        R.color.cyan,
        R.color.magenta,
        R.color.gray,
        R.color.light_gray,
        R.color.dark_gray,
        R.color.light_blue,
        R.color.dark_blue,
        R.color.light_green,
        R.color.dark_green,
        R.color.orange,
        R.color.brown,
        R.color.purple,
        R.color.pink,
        R.color.gold,
        R.color.silver,
        R.color.beige,
        R.color.ivory,
        R.color.khaki,
        R.color.lavender,
        R.color.salmon,
        R.color.teal,
        R.color.navy,
        R.color.coral,
        R.color.lime,
        R.color.turquoise
    )

    fun show() {
        val colorOptionsLayout = LinearLayout(context)
        colorOptionsLayout.orientation = LinearLayout.VERTICAL

        colors.forEach { color ->
            val colorView = TextView(context).apply {
                setBackgroundColor(context.getColor(color))
                setHeight(100)
                setPadding(16, 16, 16, 16)
                setOnClickListener {
                    onColorSelected(context.getColor(color))
                }
            }
            colorOptionsLayout.addView(colorView)
        }

        AlertDialog.Builder(context)
            .setTitle("Select a Background Color")
            .setView(colorOptionsLayout)
            .setNegativeButton("Cancel", null)
            .show()
    }
}
