package com.chin.circlelayout

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchBtn.setOnClickListener {
            normal.visibility = normalWithRange.visibility.also { normalWithRange.visibility = normal.visibility }
        }

        numSB.setOnSeekBarChangeListener (object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(sb: SeekBar, v: Int, b: Boolean) { }

            override fun onStartTrackingTouch(sb: SeekBar) { }

            override fun onStopTrackingTouch(sb: SeekBar) {
                val layout = when (normal.visibility) {
                    View.VISIBLE -> normal
                    else -> normalWithRange
                }
                for (i in 0..(layout.childCount - 1)) {
                    layout.getChildAt(i).visibility = when (i < sb.progress) {
                        true -> View.VISIBLE
                        else -> View.GONE
                    }
                }
            }
        })
    }

}
