package com.example.paintingapplab3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.paintingapplab3.databinding.ActivityCreateNewPictureBinding

class createNewPictureActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateNewPictureBinding
    private var id : Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewPictureBinding.inflate(layoutInflater)
        if(intent != null)
        {
            binding.etName.setText(intent.getStringExtra("title"))
            binding.etDescription.setText(intent.getStringExtra("description"))
            id = intent.getLongExtra("id", -1)
        }
        setContentView(binding.root)

        val activityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
        }
        binding.createPicture.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("title", binding.etName.text.toString())
            intent.putExtra("description", binding.etDescription.text.toString())
            if (id!!.toInt() != -1)
            {
                intent.putExtra("id",id)
            }

            activityLauncher.launch(intent)

            finish()
        }


    }
}