package com.example.hsexercise.feature.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.core.view.isVisible
import com.example.hsexercise.common.BaseActivity
import com.example.hsexercise.databinding.ActivityFeatureBinding

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
        binding.recycler.visibility = View.VISIBLE
        binding.recycler.adapter = recyclerAdapter

        viewModel.initialProgressVisibility.observe(this, Observer {
            binding.initialProgress.isVisible = it
        })
        viewModel.loadMoreProgressVisibility.observe(this, Observer {
            binding.loadMoreGroup.isVisible = it
        })
        viewModel.pictures.observe(this, Observer {
            recyclerAdapter.submitList(it)
        })
    }
}
