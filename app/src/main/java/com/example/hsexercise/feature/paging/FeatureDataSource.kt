package com.example.hsexercise.feature.paging

import androidx.paging.PageKeyedDataSource
import com.example.hsexercise.feature.database.FeatureModel
import com.example.hsexercise.common.DataSourceCallback
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class FeatureDataSource(
    private val featureRepository: FeatureRepository,
    private val compositeDisposable: CompositeDisposable,
    private val dataSourceCallback: DataSourceCallback
) : PageKeyedDataSource<Int, FeatureModel>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, FeatureModel>
    ) {
        featureRepository.getPicturesByPage(DEFAULT_PAGE_NUMBER)
            .subscribe(
                { pictures: List<FeatureModel> ->
                    dataSourceCallback.onInitialItemsLoaded(pictures.isEmpty())
                    val nextPageKey = if (pictures.isEmpty()) {
                        null
                    } else {
                        DEFAULT_PAGE_NUMBER + 1
                    }
                    callback.onResult(pictures, null, nextPageKey)
                },
                { dataSourceCallback.onInitialLoadError() }
            ).addTo(compositeDisposable)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, FeatureModel>) {
        dataSourceCallback.onLoadMoreStarted()
        featureRepository.getPicturesByPage(params.key)
            .subscribe(
                { pictures: List<FeatureModel> ->
                    dataSourceCallback.onLoadMoreEnded(isSuccessful = true)
                    val nextPageKey = if (pictures.isEmpty()) {
                        null
                    } else {
                        params.key + 1
                    }
                    callback.onResult(pictures, nextPageKey)
                },
                { dataSourceCallback.onLoadMoreEnded(isSuccessful = false) }
            ).addTo(compositeDisposable)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, FeatureModel>) {
        // ignored, because we only append new items to our initial load
    }

    private fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    private companion object {
        // Start from page 1 because PicsumApi returns data with identical ID's for 0 and 1 pages
        const val DEFAULT_PAGE_NUMBER = 1
    }
}