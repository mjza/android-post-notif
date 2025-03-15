package com.mjzsoft.postnotif

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mjzsoft.postnotif.database.AppDatabase
import com.mjzsoft.postnotif.database.DataModel
import com.mjzsoft.postnotif.databinding.ActivityMainBinding
import com.mjzsoft.postnotif.fileutils.CsvReader
import com.mjzsoft.postnotif.fileutils.ExcelReader
import kotlinx.coroutines.launch
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                processFile(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = AppDatabase.getDatabase(this)

        //binding.btnSelectFile.setOnClickListener {
          //  filePickerLauncher.launch("*/*") // Opens file picker
        //}
        binding.tileEmails.setOnClickListener {
            Toast.makeText(this, "Emails Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.tileTelephones.setOnClickListener {
            Toast.makeText(this, "Telephones Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.tileScans.setOnClickListener {
            Toast.makeText(this, "Scans Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.tileSettings.setOnClickListener {
            Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processFile(uri: Uri) {
        lifecycleScope.launch {
            val dataList: List<DataModel> = when {
                uri.toString().endsWith(".csv") -> CsvReader.parseCsv(this@MainActivity, uri)
                uri.toString().endsWith(".xls") || uri.toString().endsWith(".xlsx") -> ExcelReader.parseExcel(this@MainActivity, uri)
                else -> emptyList()
            }
            database.dataDao().deleteAll()
            dataList.forEach { database.dataDao().insert(it) }
        }
    }

}
