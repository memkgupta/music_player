package com.example.musicplayerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS->prevNextSong(false,context = context!!)
            ApplicationClass.NEXT->prevNextSong(true,context = context!!)
            ApplicationClass.PLAY->if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.EXIT-> {
                run {
                   exitApplication()

                }
            }



        }

    }
    private fun playMusic(){
        PlayerActivity.isPlaying=true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.btPlayPause.setIconResource(R.drawable.pause_icon)
        NowPlaying.binding.btPlayPauseNp.setIconResource(R.drawable.pause_icon)

    }
    private fun pauseMusic(){
        PlayerActivity.isPlaying=false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.btPlayPause.setIconResource(R.drawable.play_icon)
        NowPlaying.binding.btPlayPauseNp.setIconResource(R.drawable.play_icon)
    }
    private fun prevNextSong(increment:Boolean,context:Context){
        setSongPosition(increment)
      PlayerActivity.musicService!!.createMediaPlayer()

        try {
            Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.songPosition!!].artUri).apply(
                RequestOptions().placeholder(R.drawable.icon)).centerCrop().into(PlayerActivity.binding.cover);
        }
        catch (e:Exception){
            Log.e("error",e.message.toString())
        }
        PlayerActivity.binding.songName.setText(PlayerActivity.musicListPA[PlayerActivity.songPosition!!].title);
        try {
            Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri).apply(
                RequestOptions().placeholder(R.drawable.icon)).centerCrop().into(NowPlaying.binding.coverNp);
        }
        catch (e:Exception){
            Log.e("error",e.message.toString())
        }
        NowPlaying.binding.songNameNp.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        playMusic()
    }
}