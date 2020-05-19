package com.example.hsexercise.feature.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.hsexercise.common.NetworkProvider
import com.example.hsexercise.feature.database.FeatureDatabase
import com.example.hsexercise.feature.database.FeatureModel
import com.example.hsexercise.feature.paging.FeatureDataSource
import com.example.hsexercise.feature.paging.FeatureDataSourceFactory
import com.example.hsexercise.feature.paging.FeatureRepository
import io.reactivex.disposables.CompositeDisposable

class FeatureViewModel(
    application: Application
) : AndroidViewModel(application) {

    val initialLoadingState: LiveData<FeatureDataSource.LoadingState> get() = _initialLoadingState
    val loadMoreState: LiveData<FeatureDataSource.LoadingState> get() = _loadMoreState
    val livePagedList: LiveData<PagedList<FeatureModel>> get() = _livePagedList

    private val _initialLoadingState = MutableLiveData<FeatureDataSource.LoadingState>()
    private val _loadMoreState = MutableLiveData<FeatureDataSource.LoadingState>()

    private val repository = FeatureRepository(
        NetworkProvider.networkApi,
        FeatureDatabase.getDatabase(application).featureTableDao()
    )
    private val compositeDisposable = CompositeDisposable()
    private val sourceFactory = FeatureDataSourceFactory(
        repository,
        compositeDisposable,
        _initialLoadingState,
        _loadMoreState
    )
    private val _livePagedList = sourceFactory.toLiveData(DEFAULT_PAGE_SIZE)

    // TODO Invalidate data source when network connectivity is regained.

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = FeatureViewModel(
            application
        ) as T
    }

    private companion object {
        private const val DEFAULT_PAGE_SIZE = 30
    }
}
