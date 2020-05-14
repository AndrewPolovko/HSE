package com.example.hsexercise.feature.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.hsexercise.R
import com.example.hsexercise.databinding.RecyclerItemBinding
import com.example.hsexercise.feature.database.FeatureModel

class FeatureAdapter : PagedListAdapter<FeatureModel, FeatureAdapter.ViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding = RecyclerItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(
        private val viewBinding: RecyclerItemBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(picture: FeatureModel) {
            viewBinding.apply {
                val context = image.context
                creator.text = picture.author
                dimensions.text =
                    context.getString(R.string.image_dimensions, picture.width, picture.height)

                Glide.with(context)
                    .load(picture.generateDownloadLink())
                    .apply(DefaultGlideRequestOptions)
                    .into(image)
            }
        }

        private fun FeatureModel.generateDownloadLink(): String {
            return "$PICTURE_BASE_URL/${this.id}/$DEFAULT_IMAGE_WIDTH/$DEFAULT_IMAGE_HEIGHT"
        }

        private companion object {
            private const val PICTURE_BASE_URL = "https://picsum.photos/id"
            private const val DEFAULT_IMAGE_WIDTH = 400
            private const val DEFAULT_IMAGE_HEIGHT = 300

            private val DefaultGlideRequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_cloud_download_gray)
                .error(R.drawable.ic_broken_image_gray)
        }
    }

    private companion object {
        private val itemComparator = object : DiffUtil.ItemCallback<FeatureModel>() {

            override fun areItemsTheSame(oldItem: FeatureModel, newItem: FeatureModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FeatureModel, newItem: FeatureModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}