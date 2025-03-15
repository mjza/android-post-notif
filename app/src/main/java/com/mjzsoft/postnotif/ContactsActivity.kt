package com.mjzsoft.postnotif

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjzsoft.postnotif.database.AppDatabase
import com.mjzsoft.postnotif.database.DataModel
import com.mjzsoft.postnotif.databinding.ActivityContactsBinding
import com.mjzsoft.postnotif.fileutils.CsvReader
import com.mjzsoft.postnotif.fileutils.ExcelReader
import kotlinx.coroutines.launch

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

        type = intent.getStringExtra("type") ?: "Emails"
        // Set title dynamically
        binding.root.findViewById<TextView>(R.id.tvHeaderTitle).text = type // Set title dynamically

        // Back button functionality
        binding.root.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        database = AppDatabase.getDatabase(this)
        adapter = ContactsAdapter(emptyList(), type)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        loadDatabaseEntries()

        binding.btnLoad.setOnClickListener {
            filePickerLauncher.launch("*/*") // Open file picker
        }

        binding.btnClear.setOnClickListener {
            clearDatabaseEntries()
        }
    }

    private fun processFile(uri: Uri) {
        lifecycleScope.launch {
            val dataList: List<DataModel> = when {
                uri.toString().endsWith(".csv") -> CsvReader.parseCsv(this@ContactsActivity, uri)
                uri.toString().endsWith(".xls") || uri.toString().endsWith(".xlsx") -> ExcelReader.parseExcel(this@ContactsActivity, uri)
                else -> emptyList()
            }
            database.dataDao().deleteAll()
            dataList.forEach { database.dataDao().insert(it) }
            loadDatabaseEntries()
        }
    }

    private fun loadDatabaseEntries() {
        lifecycleScope.launch {
            val dataList = database.dataDao().getAllData()
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
