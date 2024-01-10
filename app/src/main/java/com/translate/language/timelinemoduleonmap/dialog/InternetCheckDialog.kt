package com.translate.language.timelinemoduleonmap.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.translate.language.timelinemoduleonmap.databinding.DialogInternetCheckBinding


class InternetCheckDialog(private var mContext: Context) :
    Dialog(mContext) {
    private lateinit var binding: DialogInternetCheckBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding= DialogInternetCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lottiAnim.enableMergePathsForKitKatAndAbove(true)

        binding.enableBtn.setOnClickListener {
            dismiss()
        }
        setOnCancelListener {
            dismiss()
        }
    }
}