package com.example.hsexercise.feature.paging

import com.example.hsexercise.feature.database.FeatureModel
import com.example.hsexercise.feature.network.PicsumApi
import io.reactivex.Maybe

class FeatureRepository(
    private val networkApi: PicsumApi
) {

    fun getPicturesFromNetwork(pageNumber: Int): Maybe<List<FeatureModel>> {
        return networkApi.getPictures(pageNumber)
    }
}