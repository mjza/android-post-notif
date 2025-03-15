package com.mjzsoft.postnotif

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mjzsoft.postnotif.databinding.ActivityScansBinding

class ScansActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScansBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = "Scans"
    }
}
