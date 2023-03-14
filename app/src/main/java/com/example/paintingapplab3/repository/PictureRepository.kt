package com.example.paintingapplab3.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.example.paintingapplab3.db.Database
import com.example.paintingapplab3.db.dao.pictureDao
import com.example.paintingapplab3.db.model.PictureEntity

class PictureRepository(application: Application) {

        private var pictureDao : pictureDao
        init {
            val db = Database.getDatabase(application)
            pictureDao = db.pictureDao()
        }

        fun insert(picture : PictureEntity) : Long
        {
            return InsertHeroAsyncTask(pictureDao).execute(picture).get()
        }

        fun delete(picture : PictureEntity)
        {
            DeleteHeroAsyncTask(pictureDao).execute(picture)
        }

        fun update(picture : PictureEntity)
        {
            UpdateHeroAsyncTask(pictureDao).execute(picture)
        }

        fun getPictures() : LiveData<List<PictureEntity>>
        {
            return pictureDao.getAllPictures()
        }

        fun getItemsFiltered(filter: String): LiveData<List<PictureEntity>> {
            return pictureDao.getItemsFiltered(filter)
        }

        fun getPictureById(id : Long) : PictureEntity?
        {
            return GetPictureByIdAsyncTask(pictureDao).execute(id).get()
        }


        companion object {
            private class InsertHeroAsyncTask(private var pictureDao: pictureDao) :
                AsyncTask<PictureEntity, Void, Long>()
            {

                override fun doInBackground(vararg params: PictureEntity?): Long {
                    var id : Long = 0
                    params[0]?.let { id = pictureDao.insertPicture(it) }
                    return id
                }
            }

            private class GetPictureByIdAsyncTask(private  var pictureDao: pictureDao ) :
                AsyncTask<Long, Void, PictureEntity>()
            {

                override fun doInBackground(vararg params: Long?): PictureEntity? {
                   return pictureDao.getPictureById(params[0]!!)
                }
            }

            private class DeleteHeroAsyncTask(private var pictureDao: pictureDao) :
                AsyncTask<PictureEntity, Void, Void>()
            {

                override fun doInBackground(vararg params: PictureEntity?): Void? {
                    params[0]?.let { pictureDao.deletePicture(it) }
                    return null
                }
            }

            private class UpdateHeroAsyncTask(private var pictureDao: pictureDao) :
                AsyncTask<PictureEntity, Void, Void>()
            {

                override fun doInBackground(vararg params: PictureEntity?): Void? {
                    params[0]?.let { pictureDao.updatePicture(it) }
                    return null
                }
            }

        }
}