package com.example.paintingapplab3.viewModel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.paintingapplab3.PaintViewMode
import com.example.paintingapplab3.db.model.PictureEntity
import com.example.paintingapplab3.repository.PictureRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
     var brushSize = MutableLiveData(12f)
     var eraserSize = MutableLiveData(12f)
     var pickedColor = MutableLiveData(Color.BLACK)
     var mode = MutableLiveData(PaintViewMode.DRAW)
     var drawableCanvas = MutableLiveData<Bitmap?>()


     private var repository: PictureRepository

     init {
          repository = PictureRepository(application)
     }

     var id : Long = -1
     var title : String? = null
     var description : String? = null

     fun savePicture(picture: ByteArray)
     {
          if (id.toInt() == -1) {
               id = repository.insert(PictureEntity(0, title!!, description!!, picture))
          }
          else
          {
               repository.update(PictureEntity(id, title!!, description!!, picture))
          }
     }

     fun loadPicture(id : Long) : Bitmap
     {
          var bytearray = repository.getPictureById(id)!!.data
          return BitmapFactory.decodeByteArray(bytearray, 0, bytearray.size)
     }


}