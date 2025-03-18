package com.mjzsoft.postnotif

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjzsoft.postnotif.database.AppDatabase
import com.mjzsoft.postnotif.database.DataModel
import com.mjzsoft.postnotif.databinding.ActivityContactsBinding
import com.mjzsoft.postnotif.fileutils.CsvReader
import com.mjzsoft.postnotif.fileutils.ExcelReader
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactsBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: ContactsAdapter
    private var type: String = "Emails" // Default to Emails

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { processFile(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the title dynamically
        type = intent.getStringExtra("type") ?: "Emails"
        binding.root.findViewById<TextView>(R.id.tvHeaderTitle).text = type

        // Handle Back Button Click
        val backButton = binding.root.findViewById<ImageView>(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set Header Row Text (Bold without background)
        var tvRow = binding.root.findViewById<LinearLayout>(R.id.tvRow)
        val tvUnitHeader = binding.root.findViewById<TextView>(R.id.tvUnit)
        val tvValueHeader = binding.root.findViewById<TextView>(R.id.tvValue)

        tvRow.setBackgroundColor(ContextCompat.getColor(this, R.color.light_bg))
        tvUnitHeader.text = getString(R.string.unit)
        tvValueHeader.text = if (type == "Emails") getString(R.string.email) else getString(R.string.phone)
        tvUnitHeader.textSize = 18f
        tvValueHeader.textSize = 18f
        tvUnitHeader.setTypeface(null, android.graphics.Typeface.BOLD)
        tvValueHeader.setTypeface(null, android.graphics.Typeface.BOLD)

        // Load Database
        database = AppDatabase.getDatabase(this)
        adapter = ContactsAdapter(emptyList(), type)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        loadDatabaseEntries() // Load saved data

        binding.btnImport.setOnClickListener {
            filePickerLauncher.launch("*/*") // Open file picker
        }

        binding.btnClear.setOnClickListener {
            clearDatabaseEntries()
        }
    }

    private fun processFile(uri: Uri) {
        Log.i("URI", "Selected URI: $uri")
        lifecycleScope.launch {
            try {
                val cachedFileUri = copyFileToCache(uri)

                if (cachedFileUri != null) {
                    val dataList: List<DataModel> = when {
                        cachedFileUri.toString().endsWith(".csv") -> CsvReader.parseCsv(this@ContactsActivity, cachedFileUri)
                        cachedFileUri.toString().endsWith(".xls") || cachedFileUri.toString().endsWith(".xlsx") -> ExcelReader.parseExcel(this@ContactsActivity, cachedFileUri)
                        else -> emptyList()
                    }

                    //if (dataList.size == 0){
                    //    Toast.makeText(this.coroutineContext, "Emails Clicked", Toast.LENGTH_SHORT).show()
                    //}

                    // Clear and insert new data
                    database.dataDao().deleteAll()
                    dataList.forEach { database.dataDao().insert(it) }

                    // Reload UI
                    loadDatabaseEntries()
                } else {
                    Log.e("File Read Error", "Failed to store the file temporarily: $uri")
                }
            } catch (e: Exception) {
                Log.e("File Read Error", "Error processing file: ${e.message}")
            }
        }
    }


    private fun copyFileToCache(uri: Uri): Uri? {
        return try {
            val mimeType = contentResolver.getType(uri) // Get MIME type
            val fileExtension = getFileExtensionFromMimeType(mimeType) // Determine extension

            // Create a file in the cache directory with the correct extension
            val tempFile = File(cacheDir, "imported_file.$fileExtension")

            // Copy data from InputStream to the temp file
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            // Return a valid URI using FileProvider
            FileProvider.getUriForFile(this, "com.mjzsoft.postnotif.provider", tempFile)
        } catch (e: Exception) {
            Log.e("File Copy Error", "Failed to copy file: ${e.message}")
            null
        }
    }



    private fun getFileExtensionFromMimeType(mimeType: String?): String {
        return when (mimeType) {
            "text/csv" -> "csv"
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"
            "application/vnd.ms-excel" -> "xls"
            else -> "txt" // Default to text if unknown
        }
    }


    private fun loadDatabaseEntries() {
        lifecycleScope.launch {
            val dataList = database.dataDao().getAllData()
            Log.i("DATA", dataList.toString())
            adapter.updateData(dataList)
        }
    }

    private fun clearDatabaseEntries() {
        lifecycleScope.launch {
            database.dataDao().deleteAll()
            adapter.updateData(emptyList())
        }
    }
}
