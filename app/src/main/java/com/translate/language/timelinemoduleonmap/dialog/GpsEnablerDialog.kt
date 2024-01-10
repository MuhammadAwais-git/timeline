package com.translate.language.timelinemoduleonmap.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import com.translate.language.timelinemoduleonmap.databinding.DialogGpsCtEnablerBinding

class GpsEnablerDialog(private var mContext: Context) :
    Dialog(mContext) {
    lateinit var binding: DialogGpsCtEnablerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding= DialogGpsCtEnablerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.enableBtn.setOnClickListener {
            dismiss()
            try {
                val callGPSSettingIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                mContext.startActivity(callGPSSettingIntent)
            } catch (e: Exception) {
            }
        }
        setOnCancelListener {
            dismiss()
        }
    }
}