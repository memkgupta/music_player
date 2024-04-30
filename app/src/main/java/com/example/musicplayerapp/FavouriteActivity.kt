package com.example.musicplayerapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayerapp.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter
   companion object
   {
       var favouriteSongs:ArrayList<Music> = ArrayList()
   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityFavouriteBinding.inflate(layoutInflater);
if(favouriteSongs.size<1){
    binding.btShuffle.visibility = View.INVISIBLE
}
        setContentView(binding.root)
binding.btBack.setOnClickListener { finish() }
        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        binding.favouriteRV.layoutManager = GridLayoutManager(this,4)
        adapter = FavouriteAdapter(this, musicList = favouriteSongs)
        binding.favouriteRV.adapter = adapter

        binding.btShuffle.setOnClickListener {
           favouriteSongs.shuffle()
            val intent = Intent(this,PlayerActivity::class.java)
            intent.putExtra("index",0);
            intent.putExtra("class","FavouriteActivity")
            startActivity(intent)
        }
    }
}