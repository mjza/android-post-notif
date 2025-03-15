package com.mjzsoft.postnotif

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mjzsoft.postnotif.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tileEmails.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            intent.putExtra("type", "Emails") // Pass type
            startActivity(intent)
        }

        binding.tileTelephones.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            intent.putExtra("type", "Telephones") // Pass type
            startActivity(intent)
        }

        binding.tileScans.setOnClickListener {
            startActivity(Intent(this, ScansActivity::class.java))
        }

        binding.tileSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
