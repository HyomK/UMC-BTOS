package com.likefirst.btos.data.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import androidx.room.TypeConverter
import com.likefirst.btos.data.entities.Plant


@Database(entities = [Plant::class], version = 3)
abstract class PlantDatabase: RoomDatabase() {
        abstract fun plantDao(): PlantDao
        companion object{
            private  var instance: PlantDatabase?=null

            @Synchronized
            fun getInstance(context: Context):  PlantDatabase? {
                if (instance == null) {
                    synchronized( PlantDatabase::class){
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            PlantDatabase::class.java,
                            "plant-database"//다른 데이터 베이스랑 이름겹치면 꼬임
                        ).allowMainThreadQueries().build()
                    }
                }
                return instance
            }
        }
    }

