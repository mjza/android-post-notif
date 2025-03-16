package com.mjzsoft.postnotif

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ImageGridAdapter(
    private val imageList: List<Pair<Any, String>>, // Image + Extracted Text
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<ImageGridAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val badgeText: TextView = view.findViewById(R.id.badgeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (item, extractedText) = imageList[position]

        if (item is Int) {
            holder.imageView.setImageResource(item) // Show default add icon
        } else if (item is Uri) {
            holder.imageView.setImageURI(item)
        } else if (item is Bitmap) {
            holder.imageView.setImageBitmap(item)
        }

        // Show extracted text badge (if available)
        holder.badgeText.text = extractedText
        holder.badgeText.visibility = if (extractedText.isNotEmpty()) View.VISIBLE else View.GONE

        holder.imageView.setOnClickListener {
            onImageClick(position)
        }
    }

    override fun getItemCount() = imageList.size
}
