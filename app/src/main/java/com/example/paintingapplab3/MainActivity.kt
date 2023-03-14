package com.example.paintingapplab3


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paintingapplab3.Interfaces.ToolsListener
import com.example.paintingapplab3.adapters.ToolsAdapter
import com.example.paintingapplab3.databinding.ActivityMainBinding
import com.example.paintingapplab3.db.model.PictureEntity
import com.example.paintingapplab3.model.Operation
import com.example.paintingapplab3.model.ToolsItem
import com.example.paintingapplab3.viewModel.MainActivityViewModel
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), ToolsListener {
    lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainActivityViewModel
    lateinit var getImageLauncher : ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        if (intent != null) {

            viewModel.title = intent.getStringExtra("title")
            viewModel.description = intent.getStringExtra("description")
            viewModel.id = intent.getLongExtra("id", -1)
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        initTools()
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        viewModel.brushSize.observe(this) {
            binding.paintView.setSizeBrush(it)
        }
        viewModel.eraserSize.observe(this) {
            binding.paintView.setSizeEraser(it)
        }
        viewModel.pickedColor.observe(this) {
            binding.paintView.setBrushColor(it)
        }
        binding.checkButton.visibility = View.INVISIBLE;
        binding.crossButton.visibility = View.INVISIBLE;
        if (viewModel.id.toInt() != -1)
        {
            binding.paintView.setBtmView(viewModel.loadPicture(viewModel.id))
        }







        getImageLauncher= registerForActivityResult(ActivityResultContracts.GetContent()) {

                uri: Uri? ->
            uri?.let {
                var options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                contentResolver.openInputStream(it)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                    binding.checkButton.visibility = View.VISIBLE;
                    binding.crossButton.visibility = View.VISIBLE;
                    binding.paintView.setMode(PaintViewMode.IMAGE_CAPTURING)
                    binding.paintView.setImage(bitmap)
                    binding.recyclerViewTools.visibility = View.INVISIBLE
                }
            }
        }
        binding.checkButton.setOnClickListener{
            binding.paintView.captureDrawableImage()
            binding.checkButton.visibility = View.INVISIBLE;
            binding.crossButton.visibility = View.INVISIBLE;
            binding.recyclerViewTools.visibility = View.VISIBLE

        }
        binding.crossButton.setOnClickListener{
            binding.paintView.cancelCapturing()
            binding.checkButton.visibility = View.INVISIBLE;
            binding.crossButton.visibility = View.INVISIBLE;
            binding.recyclerViewTools.visibility = View.VISIBLE
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.drawableCanvas.value = binding.paintView.getBtmView()
        super.onSaveInstanceState(outState )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        binding.paintView.setBtmView(viewModel.drawableCanvas.value!!)

        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun initTools()
    {

        binding.paintView.setSizeBrush(viewModel.brushSize.value!!)
        binding.paintView.setBrushColor(viewModel.pickedColor.value!!)
        binding.paintView.setSizeEraser(viewModel.eraserSize.value!!)
        binding.recyclerViewTools.setHasFixedSize(true)
        binding.recyclerViewTools.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.recyclerViewTools.adapter = ToolsAdapter(this,
            listOf(ToolsItem(R.drawable.ic_brush, "Brush", Operation.DRAW, true),
                ToolsItem(R.drawable.ic_palette, "Palette", Operation.PALETTE, false),
                ToolsItem(R.drawable.ic_size, "Size", Operation.BRUSH_SIZE_CHANGING, false),
                ToolsItem(R.drawable.ic_eraser, "Eraser", Operation.ERASE, true),
                ToolsItem(R.drawable.ic_zoom, "Pan&Zoom", Operation.PAN_AND_ZOOM, true),
                ToolsItem(R.drawable.ic_fill, "Fill", Operation.FLOODFILL, true),
                ToolsItem(R.drawable.ic_image, "Image", Operation.IMAGE_CAPTURING, false),
                ToolsItem(R.drawable.ic_image, "Save", Operation.SAVE, false),
                ))
    }

    override fun onBackPressed() {

        finish()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewModel.drawableCanvas.observe(this){
            it?.recycle()
        }


    }

    override fun onSelected(operation: Operation) {
        when(operation)
        {
            Operation.DRAW -> {
                    binding.paintView.setBrushColor(viewModel.pickedColor.value!!)
                    binding.paintView.setMode(PaintViewMode.DRAW)
            }
            Operation.BRUSH_SIZE_CHANGING ->
            {
                val newFragment: DialogFragment = SizeSeekerDialog( viewModel)
                newFragment.show(supportFragmentManager, "BrushSize")
            }

            Operation.ERASE -> {

                    binding.paintView.setMode(PaintViewMode.ERASE)
            }
            Operation.PALETTE ->  {
                    val newFragment: DialogFragment = ColorPickerDialogs(viewModel)
                    newFragment.show(supportFragmentManager, "palette_dialog")
            }
            Operation.PAN_AND_ZOOM ->  { binding.paintView.setMode(PaintViewMode.PAN_AND_ZOOM) }
            Operation.FLOODFILL -> { binding.paintView.setMode(PaintViewMode.FLOODFILL)}
            Operation.IMAGE_CAPTURING -> getImageLauncher.launch("image/*")
            Operation.SAVE -> savePicture()



        }

    }

    fun savePicture()
    {
        val outputStream = ByteArrayOutputStream()
        binding.paintView.getBtmView().compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        viewModel.savePicture(byteArray)
        outputStream.close()
        Toast.makeText(this, "Picture saved", Toast.LENGTH_SHORT).show()
    }





}