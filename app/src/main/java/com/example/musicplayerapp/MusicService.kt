package com.example.musicplayerapp

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.util.Arrays

class MusicService : Service(),AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder();
    var mediaPlayer : MediaPlayer?=null
    private lateinit var mediaSession:MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager
    override fun onDestroy() {
        super.onDestroy()
        exitApplication()

    }

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext,"My Music")
        return myBinder
    }
    inner class MyBinder:Binder(){
        fun currentService():MusicService{
            return this@MusicService;
        }
    }
    fun showNotification(playPauseBtn:Int){
val intent = Intent(baseContext,PlayerActivity::class.java)
        intent.putExtra("index",PlayerActivity.songPosition)
        intent.putExtra("class","NowPlaying")
        val contentIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE);
        val prevIntent = Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_MUTABLE);
        val nextIntent = Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_MUTABLE);
        val exitIntent = Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_MUTABLE);
        val playIntent = Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_MUTABLE);
        val imageArt = getImageArt(PlayerActivity.musicListPA[PlayerActivity.songPosition!!].path)
        val img =
        if(imageArt!=null){
            BitmapFactory.decodeByteArray(imageArt,0,imageArt.size)
        }
        else{
            BitmapFactory.decodeResource(resources,R.drawable.icon)
        }
        val notification = NotificationCompat.Builder(baseContext,ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
    .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition!!].title)
    .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition!!].artist)
    .setSmallIcon(R.drawable.playlist_icon)
    .setLargeIcon(img)
    .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
    .setPriority(NotificationCompat.PRIORITY_HIGH)
    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    .setOnlyAlertOnce(true)
    .addAction(R.drawable.previous_icon,"Previous",prevPendingIntent)
    .addAction(playPauseBtn,"Play",playPendingIntent)
    .addAction(R.drawable.next_icon,"Next",nextPendingIntent)
    .addAction(R.drawable.exit,"exit",exitPendingIntent)
    .build()

        startForeground(13,notification)
    }
     fun createMediaPlayer(){
         try {
             if(PlayerActivity.musicService!!.mediaPlayer==null){
                 PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
             }


             PlayerActivity.musicService!!.mediaPlayer!!.reset();
             PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition!!].path)
             PlayerActivity.musicService!!.mediaPlayer!!.prepare()
             PlayerActivity.binding.seekBarStart.text = formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
             PlayerActivity.binding.seekBarEnd.text = formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.duration.toLong())

             PlayerActivity.binding.seekBar.progress = 0
             PlayerActivity.nowPlayingSongId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id
         }
         catch (e:Exception){
            Log.e("error", e.stackTrace.contentToString())
             return
         }



    }

    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.seekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onAudioFocusChange(p0: Int) {
        if(p0<=0){
            //pause music
            PlayerActivity.musicService!!.mediaPlayer!!.pause()
            NowPlaying.binding.btPlayPauseNp.setIconResource(R.drawable.play_icon)
            PlayerActivity.binding.btPlayPause.setIconResource(R.drawable.play_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
            PlayerActivity.isPlaying =false

            }

        else{
            PlayerActivity.musicService!!.mediaPlayer!!.start()
            NowPlaying.binding.btPlayPauseNp.setIconResource(R.drawable.pause_icon)
            PlayerActivity.binding.btPlayPause.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.isPlaying = true
        }
    }
}