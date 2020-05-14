package com.example.hsexercise.feature.paging

import androidx.paging.DataSource
import com.example.hsexercise.common.DataSourceCallback
import com.example.hsexercise.feature.database.FeatureModel
import io.reactivex.disposables.CompositeDisposable

class FeatureDataSourceFactory(
    private val featureRepository: FeatureRepository,
    private val compositeDisposable: CompositeDisposable,
    private val dataSourceCallback: DataSourceCallback
) : DataSource.Factory<Int, FeatureModel>() {

    override fun create(): DataSource<Int, FeatureModel> {
        return FeatureDataSource(featureRepository, compositeDisposable, dataSourceCallback)
    }
}