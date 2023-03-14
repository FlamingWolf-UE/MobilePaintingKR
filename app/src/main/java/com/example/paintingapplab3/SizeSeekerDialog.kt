package com.example.paintingapplab3

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface


import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import com.example.paintingapplab3.databinding.DialogBinding
import com.example.paintingapplab3.viewModel.MainActivityViewModel


class SizeSeekerDialog(val viewModel : MainActivityViewModel) : androidx.fragment.app.DialogFragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val binding = DialogBinding
            .inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        binding.seekBar.max = 200

        binding.seekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener{
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.statusDescription.text = "Power: " + binding.seekBar.progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        } )
            binding.seekBar.progress = viewModel.eraserSize.value!!.toInt()
            binding.statusTitle.text = "Brush size"
            binding.statusDescription.text = "Power: " + viewModel.eraserSize.value!!.toInt().toString()
            builder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which -> viewModel.eraserSize.value =
                (binding.seekBar.progress).toFloat()
            })





        return builder.create()
    }
}