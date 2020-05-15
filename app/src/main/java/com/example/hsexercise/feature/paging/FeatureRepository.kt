package com.example.hsexercise.feature.paging

import com.example.hsexercise.feature.database.FeatureModel
import com.example.hsexercise.feature.database.FeatureTableDao
import com.example.hsexercise.feature.network.PicsumApi
import io.reactivex.Maybe

class FeatureRepository(
    private val networkApi: PicsumApi,
    private val databaseApi: FeatureTableDao
) {

    fun getPicturesByPage(pageNumber: Int): Maybe<List<FeatureModel>> {
        return databaseApi.getPage(pageNumber)
            .flatMap { dbPage: List<FeatureModel> ->
                if (dbPage.isEmpty()) {
                    loadNewNetworkPage(pageNumber)
                } else {
                    Maybe.just(dbPage)
                }
            }
    }

    private fun loadNewNetworkPage(pageNumber: Int): Maybe<List<FeatureModel>> {
        return networkApi.getPictures(pageNumber)
            .map { mapNetworkPageToDbPage(it, pageNumber) }
            .doOnSuccess {
                databaseApi.insertAll(it)
            }
    }

    private fun mapNetworkPageToDbPage(
        networkPage: List<FeatureModel>,
        pageNumber: Int
    ): List<FeatureModel> {
        return networkPage.map {
            FeatureModel(
                it.id,
                it.author,
                it.url,
                it.width,
                it.height,
                pageNumber
            )
        }
    }
}