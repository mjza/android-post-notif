package com.mjzsoft.postnotif.fileutils

import android.content.Context
import com.mjzsoft.postnotif.database.DataModel
import com.opencsv.CSVReader
import java.io.InputStreamReader

object CsvReader {
    fun parseCsv(context: Context, uri: android.net.Uri): List<DataModel> {
        val dataList = mutableListOf<DataModel>()
        val inputStream = context.contentResolver.openInputStream(uri)
        val reader = CSVReader(InputStreamReader(inputStream))

        var line: Array<String>?
        reader.readNext() // Skip header
        while (reader.readNext().also { line = it } != null) {
            if (line!!.size >= 3) {
                dataList.add(DataModel(unitNumber = line!![0], email = line!![1], telephone = line!![2]))
            }
        }
        reader.close()
        return dataList
    }
}
