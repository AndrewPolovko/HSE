package com.example.hsexercise.feature.database

import androidx.room.*
import io.reactivex.Maybe

@Dao
interface FeatureTableDao {
    @Query("SELECT * FROM feature WHERE pageNumber = :pageNumber")
    fun getPage(pageNumber: Int): Maybe<List<FeatureModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(models: List<FeatureModel>)
}

@Entity(tableName = "feature")
data class FeatureModel(
    @PrimaryKey
    val id: String,
    val author: String,
    val url: String,
    val width: Int,
    val height: Int,
    var pageNumber: Int? = null
)
