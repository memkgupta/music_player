package com.example.musicplayerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerapp.databinding.ActivityPlayListBinding

class PlayListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityPlayListBinding.inflate(layoutInflater);


        setContentView(binding.root)
binding.btBack.setOnClickListener { finish() }
    }
}