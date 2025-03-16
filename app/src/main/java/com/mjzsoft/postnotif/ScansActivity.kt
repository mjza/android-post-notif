package com.mjzsoft.postnotif

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mjzsoft.postnotif.databinding.ActivityScansBinding

class ScansActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScansBinding
    private lateinit var adapter: ImageGridAdapter
    private val imageList = mutableListOf<Pair<Any, String>>() // Image + Extracted Text

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set back button
        binding.root.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set title
        binding.root.findViewById<TextView>(R.id.tvHeaderTitle).text = getString(R.string.scans)

        // Initialize RecyclerView
        adapter = ImageGridAdapter(imageList, ::onImageSelected)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = adapter

        // Add first item (Camera/Media option)
        imageList.add(Pair(R.drawable.ic_add_photo, "")) // Placeholder for camera/gallery
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
                processImageOCR(imageBitmap) // Perform OCR on taken image
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
                processImageOCR(it) // Perform OCR on selected image
            }
        }

    private fun openGallery() {
        selectGalleryLauncher.launch("image/*")
    }

    // Opens File Picker
    private val selectFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                processImageOCR(it) // Perform OCR on file-picked image
            }
        }

    private fun openFilePicker() {
        selectFileLauncher.launch(arrayOf("image/*"))
    }

    // Process OCR on Image (Bitmap or URI)
    private fun processImageOCR(image: Any) {
        // Show loading icon
        binding.loadingOverlay.visibility = View.VISIBLE

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = when (image) {
            is Bitmap -> InputImage.fromBitmap(image, 0)
            is Uri -> InputImage.fromFilePath(this, image)
            else -> return
        }

        recognizer.process(inputImage)
            .addOnSuccessListener { text ->
                Log.i("OCR", text.text)
                val extractedUnit = extractUnitNumber(text.text) // Extract unit number
                imageList.add(Pair(image, extractedUnit))
                adapter.notifyItemInserted(imageList.size - 1)
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "Failed to recognize text: ${e.message}")
                imageList.add(Pair(image, ""))
                adapter.notifyItemInserted(imageList.size - 1)
            }
            .addOnCompleteListener {
                // Hide loading icon after processing (success or failure)
                binding.loadingOverlay.visibility = View.GONE
            }
    }


    // Extract unit number from text
    //private fun extractUnitNumber(text: String): String {
    //    val regex = Regex("\\b(Unit)?\\s?(\\d+)\\s?-?\\s?(1111 6 Avenve SW)|(1111 6 Ave SW)", RegexOption.IGNORE_CASE) // Looks for "Unit 123"
    //    val match = regex.find(text)
    //    return match?.groups?.get(1)?.value ?: "?"
    //}

    private fun extractUnitNumber(text: String): String {
        val standardizedText = text.lowercase()
            .replace("th", "", true) // Normalize ordinal numbers
            .replace(Regex("ave[^ ]* sw", RegexOption.IGNORE_CASE), "ave sw") // Normalize address format
            .replace("[^a-zA-Z0-9 \r\n-]".toRegex(), "") // Remove unwanted symbols
        Log.i("CLN", standardizedText)
        val targetAddress = "1111 6 ave sw"

        // Regex to extract unit number
        val regex = Regex("\\b(\\d+)\\s?-?\\s?($targetAddress)\\b|\\bUnit\\s?(\\d+)\\s+($targetAddress)\\b", RegexOption.IGNORE_CASE)
        val match = regex.find(standardizedText)

        return match?.groups?.get(1)?.value ?: match?.groups?.get(2)?.value ?: "?"
    }

}
