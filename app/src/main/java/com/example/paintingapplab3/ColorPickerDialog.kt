package com.example.paintingapplab3

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.paintingapplab3.viewModel.MainActivityViewModel
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener


class ColorPickerDialogs(val viewModel : MainActivityViewModel) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = ColorPickerDialog.Builder(context)
            .setTitle("ColorPicker Dialog")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("Ok",
                 ColorEnvelopeListener() { colorEnvelope: ColorEnvelope, b: Boolean ->
                        viewModel.pickedColor.value = colorEnvelope.color
                 })
            .attachBrightnessSlideBar(true)  // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.

        return builder.create()
    }
}