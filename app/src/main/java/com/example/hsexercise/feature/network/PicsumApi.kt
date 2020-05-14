package com.example.hsexercise.feature.network

import com.example.hsexercise.feature.database.FeatureModel
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApi {

    @GET("v2/list")
    fun getPictures(@Query("page") pageNumber: Int): Maybe<List<FeatureModel>>
}