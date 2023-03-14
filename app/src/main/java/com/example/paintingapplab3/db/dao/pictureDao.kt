package com.example.paintingapplab3.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.paintingapplab3.db.model.PictureEntity

@Dao
interface pictureDao {

    @Query("SELECT * FROM pictures")
    fun getAllPictures(): LiveData<List<PictureEntity>>

    @Query("SELECT * FROM pictures WHERE id =:id")
    fun getPictureById(id: Long): PictureEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPicture(picture: PictureEntity) : Long

    @Update
    fun updatePicture(picture: PictureEntity)

    @Delete
    fun deletePicture(picture: PictureEntity)

    @Query("SELECT * from pictures WHERE name LIKE :filter")
    fun getItemsFiltered(filter: String): LiveData<List<PictureEntity>>
}