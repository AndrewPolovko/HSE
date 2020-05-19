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
    private val initialLoadingLiveData: MutableLiveData<LoadingState>,
    private val loadMoreLiveData: MutableLiveData<LoadingState>
) : PageKeyedDataSource<Int, FeatureModel>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, FeatureModel>
    ) {
        fetchPageFromRepository(
            requestedPage = DEFAULT_PAGE_NUMBER,
            loadingStateLiveData = initialLoadingLiveData
        ) { resultPage, nextPageKey ->
            callback.onResult(resultPage, null, nextPageKey)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, FeatureModel>) {
        fetchPageFromRepository(
            requestedPage = params.key,
            loadingStateLiveData = loadMoreLiveData
        ) { resultPage, nextPageKey ->
            callback.onResult(resultPage, nextPageKey)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, FeatureModel>) {
        // ignored, because we only append new items to our initial load
    }

    private fun fetchPageFromRepository(
        requestedPage: Int,
        loadingStateLiveData: MutableLiveData<LoadingState>,
        resultFunction: (resultPage: List<FeatureModel>, nextPageKey: Int?) -> Unit
    ) {
        featureRepository.getPicturesByPage(requestedPage)
            .doOnSubscribe {
                loadingStateLiveData.postValue(LoadingState.LOADING)
            }
            .doOnSuccess {
                val loadingState = if (it.isEmpty()) {
                    LoadingState.LOADED_EMPTY
                } else {
                    LoadingState.LOADED
                }
                loadingStateLiveData.postValue(loadingState)
            }
            .doOnError {
                loadingStateLiveData.postValue(LoadingState.FAILED)
            }
            .subscribe(
                { resultPage: List<FeatureModel> ->
                    val nextPageKey = if (resultPage.isEmpty()) {
                        null
                    } else {
                        requestedPage + 1
                    }
                    resultFunction(resultPage, nextPageKey)
                },
                { Log.e(TAG, "fetchPageFromRepository, requested page: $requestedPage", it) }
            ).addTo(compositeDisposable)
    }

    private fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    enum class LoadingState {
        LOADING, LOADED, LOADED_EMPTY, FAILED
    }

    private companion object {
        // Start from page 1 because PicsumApi returns data with identical ID's for 0 and 1 pages
        private const val DEFAULT_PAGE_NUMBER = 1
        private const val TAG = "FeatureDataSource"
    }
}