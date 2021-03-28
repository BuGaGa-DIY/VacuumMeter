package com.bugaga.barmetr

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.trace_line_layout.view.*

class DialogOptionsMenu(context: Context) {
    private val mainContext = context
    init {
        val builder = AlertDialog.Builder(mainContext)
        val prefs = mainContext.getSharedPreferences("OptionsPrefs",Context.MODE_PRIVATE)
        var traceLineIndex = -1
        val inflater = LayoutInflater.from(mainContext.applicationContext).inflate(R.layout.trace_line_layout,null)
        inflater.traceLineSwitch.isChecked = prefs.getBoolean("OptionTraceLineSwitch",false)
        val isVisible= if(prefs.getBoolean("OptionTraceLineSwitch",false)) View.VISIBLE
        else View.GONE
        inflater.traceLineBar1.visibility = isVisible
        inflater.traceLineBar2.visibility = isVisible
        inflater.traceLineBar3.visibility = isVisible
        inflater.traceLineBar4.visibility = isVisible
        inflater.traceLineSpaceView.visibility = isVisible
        inflater.traceLineSwitch.setOnCheckedChangeListener { _, isChecked ->
            val isVisible= if(isChecked) View.VISIBLE
            else View.GONE
            inflater.traceLineBar1.visibility = isVisible
            inflater.traceLineBar2.visibility = isVisible
            inflater.traceLineBar3.visibility = isVisible
            inflater.traceLineBar4.visibility = isVisible
            inflater.traceLineSpaceView.visibility = isVisible
            if (isVisible == View.VISIBLE) inflater.traceLineBar1.isChecked = true
            with( prefs.edit()){
                putInt("traceLineIndex",if (isChecked) 0 else -1)
                putBoolean("OptionTraceLineSwitch",isChecked)
                apply()
            }
        }
        inflater.traceLineBarGroup.setOnCheckedChangeListener { _, checkedId ->
            traceLineIndex = when(checkedId){
                R.id.traceLineBar1 -> 0
                R.id.traceLineBar2 -> 1
                R.id.traceLineBar3 -> 2
                R.id.traceLineBar4 -> 3
                else -> -1
            }
            with(prefs.edit()){
                putInt("traceLineIndex",traceLineIndex)
                apply()
            }
        }
        when(prefs.getInt("traceLineIndex",-1)){
            0 -> inflater.traceLineBar1.isChecked = true
            1 -> inflater.traceLineBar2.isChecked = true
            2 -> inflater.traceLineBar3.isChecked = true
            3 -> inflater.traceLineBar4.isChecked = true
        }
        var intProg = prefs.getFloat("MaxProgress",4096f)
        intProg = intProg / 4096f * 100f
        inflater.maxSeekBar.progress = intProg.toInt()
        intProg = prefs.getFloat("MinProgress",0f)
        intProg = intProg / 4096f * 100f
        inflater.minSeekBar.progress = intProg.toInt()
        inflater.maxTextView.text = inflater.maxSeekBar.progress.toString() + "%"
        inflater.minTextView.text = inflater.minSeekBar.progress.toString() + "%"
        inflater.maxSeekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                inflater.maxTextView.text = "$progress%"
                inflater.minSeekBar.max = progress
                with(prefs.edit()){
                    val deviceProgress = 4096f / 100 * progress
                    putFloat("MaxProgress",deviceProgress)
                    apply()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {           }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {            }
        })
        inflater.minSeekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                inflater.minTextView.text = "$progress%"
                inflater.maxSeekBar.min = progress
                with(prefs.edit()){
                    val deviceProgress = 4096f / 100 * progress
                    putFloat("MinProgress",deviceProgress)
                    apply()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {           }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {            }
        })
        builder.setView(inflater)
        var dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()
    }
}