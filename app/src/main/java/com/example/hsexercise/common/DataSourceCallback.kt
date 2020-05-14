package com.example.hsexercise.common

import androidx.paging.DataSource

/**
 * Provides loading status to view from all DataSources
 * that are inherited from paging library's [DataSource].
 */
interface DataSourceCallback {

    fun onInitialItemsLoaded(isEmpty: Boolean)

    fun onInitialLoadError()

    fun onLoadMoreStarted()

    fun onLoadMoreEnded(isSuccessful: Boolean)
}