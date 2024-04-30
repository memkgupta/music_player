package com.example.musicplayerapp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayerapp.databinding.ActivityPlayListBinding

class PlayListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayListBinding
    private lateinit var adapter:PlaylistAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityPlayListBinding.inflate(layoutInflater);


        setContentView(binding.root)
binding.btBack.setOnClickListener { finish() }

        binding.playListRv.setHasFixedSize(true)
        binding.playListRv.setItemViewCacheSize(13)
        binding.playListRv.layoutManager = GridLayoutManager(this,3)
        adapter = PlaylistAdapter(this, playlistList = ArrayList())
        binding.playListRv.adapter = adapter
    }

    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialogue,binding.root,false)

    }
}