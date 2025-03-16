package com.mjzsoft.postnotif

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImageGridAdapter(
    private val imageList: List<Any>,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<ImageGridAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = imageList[position]

        if (item is Int) {
            // Show placeholder icon for first tile
            holder.imageView.setImageResource(item)
        } else if (item is Uri) {
            holder.imageView.setImageURI(item)
        } else if (item is Bitmap) {
            holder.imageView.setImageBitmap(item)
        }

        holder.imageView.setOnClickListener {
            onImageClick(position)
        }
    }

    override fun getItemCount() = imageList.size
}
