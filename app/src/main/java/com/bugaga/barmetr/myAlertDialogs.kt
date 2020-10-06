package com.bugaga.barmetr

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.android.synthetic.main.match_picker.view.*

class myAlertDialogs {

    @SuppressLint("CommitPrefEdits")
    fun showMatchDialog(context : Context){
        val prefs = context.getSharedPreferences("myLocalPrefs",Context.MODE_PRIVATE)
        val edit = prefs.edit()

        var dialog : AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context.applicationContext)
            .inflate(R.layout.match_picker,null)
        when(prefs.getInt("MatchProgressNr",0)) {
            0->{
                inflater.MatchProgress1.isChecked = false
                inflater.MatchProgress2.isChecked = false
                inflater.MatchProgress4.isChecked = false
            }
            1-> inflater.MatchProgress1.isChecked = true
            2-> inflater.MatchProgress2.isChecked = true
            4-> inflater.MatchProgress4.isChecked = true
        }
        inflater.MatchProgressClearAll.setOnClickListener {
            inflater.MatchProgress1.isChecked = false
            inflater.MatchProgress2.isChecked = false
            inflater.MatchProgress4.isChecked = false
            edit.putInt("MatchProgressNr",0)
            edit.apply()
            dialog?.dismiss()
        }
        inflater.MatchProgressOk.setOnClickListener { dialog?.dismiss() }
        inflater.MatchProgress1.setOnClickListener {
            edit.putInt("MatchProgressNr",1)
            edit.apply()
            Toast.makeText(context,"Comparing with first",Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        }
        inflater.MatchProgress2.setOnClickListener {
            edit.putInt("MatchProgressNr",2)
            edit.apply()
            Toast.makeText(context,"Comparing with second",Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        }
        inflater.MatchProgress4.setOnClickListener {
            edit.putInt("MatchProgressNr",4)
            edit.apply()
            Toast.makeText(context,"Comparing with fourth",Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        }
        builder.setView(inflater)
        dialog = builder.create()
        dialog.show()
    }
}