package com.example.hsexercise.feature.ui

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.core.view.isVisible
import com.example.hsexercise.R
import com.example.hsexercise.common.BaseActivity
import com.example.hsexercise.databinding.ActivityFeatureBinding
import com.example.hsexercise.feature.paging.FeatureDataSource.InitialLoadingState
import com.example.hsexercise.feature.paging.FeatureDataSource.LoadMoreState

class FeatureActivity : BaseActivity<FeatureViewModel>() {

    override val viewModelClass = FeatureViewModel::class.java

    private lateinit var binding: ActivityFeatureBinding

    private val recyclerAdapter = FeatureAdapter()

    override fun provideViewModelFactory() = FeatureViewModel.Factory(application)

    override fun setContentView() {
        binding = ActivityFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onViewLoad(savedInstanceState: Bundle?) {
        binding.recycler.adapter = recyclerAdapter

        viewModel.initialLoadingState.observe(this, Observer {
            binding.initialProgress.isVisible = (it == InitialLoadingState.LOADING)
            binding.initialLoadEmptyGroup.isVisible = (it == InitialLoadingState.LOADED_EMPTY)
            binding.initialLoadErrorGroup.isVisible = (it == InitialLoadingState.FAILED)
            binding.recycler.isVisible = (it == InitialLoadingState.LOADED)
        })
        viewModel.loadMoreState.observe(this, Observer {
            binding.loadMoreGroup.isVisible = (it == LoadMoreState.LOADING)

            // TODO Send error from VM as Single live event to not trigger
            //  toast on every screen rotation.
            if (it == LoadMoreState.FAILED) {
                showLoadMoreError()
            }
        })
        viewModel.livePagedList.observe(this, Observer {
            recyclerAdapter.submitList(it)
        })
    }

    private fun showLoadMoreError() {
        Toast.makeText(this, R.string.load_more_failed_error_message, Toast.LENGTH_SHORT).show()
    }
}
