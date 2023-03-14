package com.example.paintingapplab3.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.paintingapplab3.db.dao.pictureDao
import com.example.paintingapplab3.db.model.PictureEntity


@Database(entities = [PictureEntity::class], version = 3)
    abstract class Database : RoomDatabase() {

    abstract fun pictureDao(): pictureDao

        companion object {
            @Volatile
            private var INSTANCE: com.example.paintingapplab3.db.Database? = null

            fun getDatabase(context: Context): com.example.paintingapplab3.db.Database {
                val tempInstance = INSTANCE
                if (tempInstance != null) {
                    return tempInstance
                }
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        com.example.paintingapplab3.db.Database::class.java,
                        "paintings_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                    return instance
                }
            }
        }
    }
