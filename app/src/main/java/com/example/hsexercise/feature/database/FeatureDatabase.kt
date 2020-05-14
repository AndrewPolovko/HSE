package com.example.hsexercise.feature.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FeatureModel::class], version = 1, exportSchema = false)
abstract class FeatureDatabase : RoomDatabase() {

    abstract fun featureTableDao(): FeatureTableDao

    companion object {

        private var DB_INSTANCE: FeatureDatabase? = null
        private const val DB_NAME = "HS_DB"

        @Synchronized
        fun getDatabase(context: Context) =
            DB_INSTANCE?.let { return it } ?: buildDatabase(context.applicationContext)

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, FeatureDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
                .also { DB_INSTANCE = it }
    }
}