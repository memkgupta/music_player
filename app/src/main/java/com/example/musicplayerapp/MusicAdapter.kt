package com.example.musicplayerapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerapp.databinding.MusicViewBinding
import java.util.concurrent.TimeUnit

class MusicAdapter(private val context:Context,private var musicList:ArrayList<Music>) : RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    class MyHolder(binding:MusicViewBinding):RecyclerView.ViewHolder(binding.root) {
val title = binding.songName
        val cover = binding.cover
        val album = binding.songAlbum
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
  return musicList.size;
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
val music = musicList[position]
      holder.title.text = music.title;
        holder.album.text = music.album
        holder.duration.text = parseDuration(music.duration)
Glide.with(context).load(music.artUri).apply(RequestOptions().placeholder(R.drawable.icon)).centerCrop().into(holder.cover);
        holder.root.setOnClickListener {
when{
    MainActivity.search->setIntent(position,"MusicAdapterSearch")
    musicList[position].id==PlayerActivity.nowPlayingSongId->setIntent(PlayerActivity.songPosition,"NowPlaying")
    else->setIntent(position, reference = "MusicAdapter")
}
        }
    }
    private fun parseDuration( duration: Long ):String{
        val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS);
        val seconds = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES));
        return String.format("%02d:%02d",minutes,seconds);
    }
    fun updateMusicList(searchList:ArrayList<Music>){
        musicList = ArrayList()
      musicList.addAll(searchList)
        notifyDataSetChanged()
    }
private fun setIntent(position:Int,reference:String){
    val intent = Intent(context,PlayerActivity::class.java)
    intent.putExtra("index",position);
    intent.putExtra("class",reference)

    ContextCompat.startActivity(context,intent,null)
}
}