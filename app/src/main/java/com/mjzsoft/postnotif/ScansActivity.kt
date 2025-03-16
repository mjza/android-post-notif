package com.mjzsoft.postnotif

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.mjzsoft.postnotif.databinding.ActivityScansBinding

class ScansActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScansBinding
    private lateinit var adapter: ImageGridAdapter
    private val imageList = mutableListOf<Any>() // Can contain both Uri and Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set title
        binding.root.findViewById<TextView>(R.id.tvHeaderTitle).text = getString(R.string.scans)

        // Set back button
        binding.root.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Initialize RecyclerView
        adapter = ImageGridAdapter(imageList, ::onImageSelected)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = adapter

        // Add first item (Camera/Media option)
        imageList.add(R.drawable.ic_add_photo) // Placeholder for camera/gallery
        adapter.notifyItemInserted(0)
    }

    // Click handler for the first tile
    private fun onImageSelected(position: Int) {
        if (position == 0) {
            openImageSelection()
        }
    }

    // Opens options for selecting an image (Camera/Gallery/File)
    private fun openImageSelection() {
        val options = arrayOf("Take Picture", "Select from Gallery", "Select from Files")

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
                2 -> openFilePicker()
            }
        }
        builder.show()
    }

    // Opens Camera
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageList.add(imageBitmap)
                adapter.notifyItemInserted(imageList.size - 1)
            }
        }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(intent)
    }

    // Opens Gallery
    private val selectGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageList.add(it)
                adapter.notifyItemInserted(imageList.size - 1)
            }
        }

    private fun openGallery() {
        selectGalleryLauncher.launch("image/*")
    }

    // Opens File Picker
    private val selectFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                imageList.add(it)
                adapter.notifyItemInserted(imageList.size - 1)
            }
        }

    private fun openFilePicker() {
        selectFileLauncher.launch(arrayOf("image/*"))
    }
}
