package com.example.hsexercise.feature.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.hsexercise.common.DataSourceCallback
import com.example.hsexercise.common.NetworkProvider
import com.example.hsexercise.feature.database.FeatureDatabase
import com.example.hsexercise.feature.database.FeatureModel
import com.example.hsexercise.feature.paging.FeatureDataSourceFactory
import com.example.hsexercise.feature.paging.FeatureRepository
import io.reactivex.disposables.CompositeDisposable

class FeatureViewModel(
    application: Application
) : AndroidViewModel(application), DataSourceCallback {

    private val repository = FeatureRepository(
        NetworkProvider.networkApi,
        FeatureDatabase.getDatabase(application).featureTableDao()
    )
    private val compositeDisposable = CompositeDisposable()
    private val sourceFactory = FeatureDataSourceFactory(repository, compositeDisposable, this)


    private val _initialProgressVisibility = MutableLiveData<Boolean>().apply { postValue(true) }
    private val _loadMoreProgressVisibility = MutableLiveData<Boolean>()
    private val _livePagedList = sourceFactory.toLiveData(DEFAULT_PAGE_SIZE)

    val initialProgressVisibility: LiveData<Boolean> get() = _initialProgressVisibility
    val loadMoreProgressVisibility: LiveData<Boolean> get() = _loadMoreProgressVisibility
    val pictures: LiveData<PagedList<FeatureModel>> get() = _livePagedList

    override fun onInitialItemsLoaded(isEmpty: Boolean) {
        _initialProgressVisibility.postValue(false)
        // TODO empty state
    }

    override fun onInitialLoadError() {
        _initialProgressVisibility.postValue(false)
        // TODO error state
    }

    override fun onLoadMoreStarted() {
        _loadMoreProgressVisibility.postValue(true)
    }

    override fun onLoadMoreEnded(isSuccessful: Boolean) {
        _loadMoreProgressVisibility.postValue(false)
        // TODO show toast for unsuccessful cases
    }

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
