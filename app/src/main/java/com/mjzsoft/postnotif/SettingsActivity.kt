package com.mjzsoft.postnotif

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mjzsoft.postnotif.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = "Settings"
    }
}
