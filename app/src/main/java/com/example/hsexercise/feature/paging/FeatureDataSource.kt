package com.example.hsexercise.feature.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.hsexercise.feature.database.FeatureModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class FeatureDataSource(
    private val featureRepository: FeatureRepository,
    private val compositeDisposable: CompositeDisposable,
    private val initialLoadingLiveData: MutableLiveData<InitialLoadingState>,
    private val loadMoreLiveData : MutableLiveData<LoadMoreState>
) : PageKeyedDataSource<Int, FeatureModel>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, FeatureModel>
    ) {
        featureRepository.getPicturesByPage(DEFAULT_PAGE_NUMBER)
            .doOnSubscribe {
                initialLoadingLiveData.postValue(InitialLoadingState.LOADING)
            }
            .doOnSuccess {
                val loadingState = if (it.isEmpty()) {
                    InitialLoadingState.LOADED_EMPTY
                } else {
                    InitialLoadingState.LOADED
                }
                initialLoadingLiveData.postValue(loadingState)
            }
            .doOnError {
                initialLoadingLiveData.postValue(InitialLoadingState.FAILED)
            }
            .subscribe(
                { resultPage: List<FeatureModel> ->
                    val nextPageKey = if (resultPage.isEmpty()) {
                        null
                    } else {
                        DEFAULT_PAGE_NUMBER + 1
                    }
                    callback.onResult(resultPage, null, nextPageKey)
                },
                { Log.e(TAG, "loadInitial", it) }
            ).addTo(compositeDisposable)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, FeatureModel>) {
        featureRepository.getPicturesByPage(params.key)
            .doOnSubscribe {
                loadMoreLiveData.postValue(LoadMoreState.LOADING)
            }
            .doOnSuccess {
                loadMoreLiveData.postValue(LoadMoreState.LOADED)
            }
            .doOnError {
                loadMoreLiveData.postValue(LoadMoreState.FAILED)
            }
            .subscribe(
                { resultPage: List<FeatureModel> ->
                    val nextPageKey = if (resultPage.isEmpty()) {
                        null
                    } else {
                        params.key + 1
                    }
                    callback.onResult(resultPage, nextPageKey)
                },
                { Log.e(TAG, "loadAfter, requested page: ${params.key}", it) }
            ).addTo(compositeDisposable)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, FeatureModel>) {
        // ignored, because we only append new items to our initial load
    }

    private fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    enum class InitialLoadingState {
        LOADING, LOADED, LOADED_EMPTY, FAILED
    }

    enum class LoadMoreState {
        LOADING, LOADED, FAILED
    }

    private companion object {
        // Start from page 1 because PicsumApi returns data with identical ID's for 0 and 1 pages
        private const val DEFAULT_PAGE_NUMBER = 1
        private const val TAG = "FeatureDataSource"
    }
}