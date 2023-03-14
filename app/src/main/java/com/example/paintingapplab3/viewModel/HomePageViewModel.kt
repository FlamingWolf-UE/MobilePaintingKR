package com.example.paintingapplab3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.paintingapplab3.db.dao.pictureDao
import com.example.paintingapplab3.db.model.PictureEntity
import com.example.paintingapplab3.repository.PictureRepository

class HomePageViewModel(application : Application) : AndroidViewModel(application)
{

        private var repository: PictureRepository
        private lateinit var pictures : LiveData<List<PictureEntity>>
        private var filter = MutableLiveData("%")

        init {

            repository = PictureRepository(application)
            pictures = Transformations.switchMap(filter) { filter -> repository.getItemsFiltered(filter) }

        }

        fun setFilter(newFilter: String) {

            val f = when {
                newFilter.isEmpty() -> "%"
                else -> "%$newFilter%"
            }
            filter.postValue(f)
        }

        fun insertHero(picture : PictureEntity)
        {
            repository.insert(picture)
        }

        fun updateHero(picture : PictureEntity)
        {
            repository.update(picture)
        }

        fun deleteHero(picture : PictureEntity)
        {
            repository.delete(picture)
        }

        fun getAllHeroesWithFraction() : LiveData<List<PictureEntity>>
        {
            return pictures
        }

        fun getAllPictures() : LiveData<List<PictureEntity>>
        {
            return repository.getPictures()
        }
}
