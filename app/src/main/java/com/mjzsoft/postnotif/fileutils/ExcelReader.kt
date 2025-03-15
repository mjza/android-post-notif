package com.mjzsoft.postnotif.fileutils

import android.content.Context
import com.mjzsoft.postnotif.database.DataModel
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

object ExcelReader {
    fun parseExcel(context: Context, uri: android.net.Uri): List<DataModel> {
        val dataList = mutableListOf<DataModel>()
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (row in sheet) {
            if (row.rowNum == 0) continue // Skip header
            val column1 = row.getCell(0)?.toString() ?: ""
            val column2 = row.getCell(1)?.toString() ?: ""
            val column3 = row.getCell(2)?.toString() ?: ""

            dataList.add(DataModel(unitNumber = column1, email = column2, telephone = column3))
        }
        inputStream?.close()
        return dataList
    }
}
