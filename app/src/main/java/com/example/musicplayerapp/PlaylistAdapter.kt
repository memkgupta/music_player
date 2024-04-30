package com.example.musicplayerapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.databinding.PlaylistViewBinding

class PlaylistAdapter(private val context: Context, private var playlistList:ArrayList<String>):RecyclerView.Adapter<PlaylistAdapter.MyHolder>() {
    class MyHolder(binding:PlaylistViewBinding):RecyclerView.ViewHolder(binding.root) {
val image = binding.imagePV
        val name = binding.namePV
        val delBt = binding.btDeletePV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
      return playlistList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
       holder.name.text = playlistList[position]
    }
}