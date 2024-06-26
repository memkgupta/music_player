package com.example.musicplayerapp

import android.app.Service
import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(val id:String,val title:String,val album:String,val artist:String,val duration:Long =0, val path:String,val artUri:String)
fun getImageArt(path:String):ByteArray?{
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
   return retriever.embeddedPicture
}
fun setSongPosition(increment:Boolean){
    if(increment){
        if(PlayerActivity.musicListPA.size - 1 == PlayerActivity.songPosition){
            PlayerActivity.songPosition=0;
        }
        else PlayerActivity.songPosition = PlayerActivity.songPosition!! + 1
    }
    else{
        if(0== PlayerActivity.songPosition){
            PlayerActivity.songPosition = PlayerActivity.musicListPA.size-1;
        }
        else{
            PlayerActivity.songPosition = PlayerActivity.songPosition!! - 1
        }
    }
}
 fun formatDuration( duration: Long ):String{
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)-minutes* TimeUnit.SECONDS.convert(1,
        TimeUnit.MINUTES));
    return String.format("%02d:%02d",minutes,seconds);
}
fun exitApplication(){
    if(PlayerActivity.musicService!=null){
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(Service.STOP_FOREGROUND_REMOVE)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null

    }
    exitProcess(1);
}
fun favouriteChecker(id:String):Int{
    PlayerActivity.isFav = false
    FavouriteActivity.favouriteSongs.forEachIndexed{index, music ->

         if(id== music.id){
            PlayerActivity.isFav = true
          return  index
        }

    }
    return -1;
}