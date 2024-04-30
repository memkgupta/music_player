package com.example.musicplayerapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerapp.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity(),ServiceConnection,MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var  musicListPA:ArrayList<Music>
        lateinit var binding: ActivityPlayerBinding
        var nowPlayingSongId:String = ""
        var songPosition : Int = 0
//        var mediaPlayer:MediaPlayer ? = null;
        var isPlaying = false
        var musicService:MusicService?=null
        var repeat:Boolean = false
        var min15:Boolean = false
        var min30:Boolean = false;
        var min60:Boolean = false;
        var isFav : Boolean = false
        var fIndex:Int = -1
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(binding.root)
        when(intent.getStringExtra("class")){
            "FavouriteActivity"->{
                songPosition = intent.getIntExtra("index",0);
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favouriteSongs)
                startMusicService()
                setLayout()
            }
            "FavouriteAdapter"->{
                songPosition = intent.getIntExtra("index",0);
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favouriteSongs)
                startMusicService()
                setLayout()
            }
            "NowPlaying"->{
                setLayout()
                binding.seekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.seekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBar.max = musicService!!.mediaPlayer!!.duration
                if(isPlaying){
                    binding.btPlayPause.setIconResource(R.drawable.pause_icon)
                }
                else{
                    binding.btPlayPause.setIconResource(R.drawable.play_icon)
                }
            }
            "MusicAdapterSearch"->{
                songPosition = intent.getIntExtra("index",0);
                Log.d("debug", songPosition.toString())
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                startMusicService()
                setLayout()

            }
            "MusicAdapter"->{
                songPosition = intent.getIntExtra("index",0);
Log.d("debug", songPosition.toString())
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
startMusicService()
                setLayout()

            }
            "MainActivity"->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
startMusicService()

                setLayout()
            }

        }

        binding.btShare.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Sharing Music file"))
        }
        binding.btPrev.setOnClickListener {
            if(songPosition>0){
                prevNextSong(false)
            }

        }
        binding.btNext.setOnClickListener {
            binding.btNext.setOnClickListener {
                if(songPosition< musicListPA.size){
                    prevNextSong(true)

                }
            }
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, fromUser: Boolean) {
               if(fromUser){
                   musicService!!.mediaPlayer!!.seekTo(p1);
                   binding.seekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
               }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit

        })
        binding.btRepeat.setOnClickListener {
            if(!repeat){
                repeat = true
                binding.btRepeat.setColorFilter(ContextCompat.getColor(this,R.color.cool_blue))
                Toast.makeText(this,"This song will be repeated",Toast.LENGTH_SHORT).show();
            }
            else{
                repeat = false
                Toast.makeText(this,"This song will not be repeated",Toast.LENGTH_SHORT).show();
                binding.btRepeat.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            }
        }
        binding.btBack.setOnClickListener { finish() }

        binding.btEqualizer.setOnClickListener {
            val eqIntent  = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
            eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
            eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
            eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
           try {
               startForEqualizer.launch(eqIntent);
           }
           catch (e:Exception){
               Toast.makeText(this,"Equalizer not supported",Toast.LENGTH_SHORT).show();
           }
        }

//   songPosition=intent.getIntExtra("index",0);
binding.favBtnPA.setOnClickListener {
    if(isFav){
        isFav = false
        binding.favBtnPA.setImageResource(R.drawable.heart_outline)
        FavouriteActivity.favouriteSongs.removeAt(fIndex)
    }
    else{
        isFav = true
        binding.favBtnPA.setImageResource(R.drawable.favourite_icon)
        FavouriteActivity.favouriteSongs.add(musicListPA[songPosition])
    }
}


binding.btPlayPause.setOnClickListener{
    if(musicService!!.mediaPlayer!=null){
        if(isPlaying){
musicService!!.mediaPlayer!!.pause()
            binding.btPlayPause.setIconResource(R.drawable.play_icon)
            musicService!!.showNotification(R.drawable.play_icon)
            isPlaying=false
        }
        else{
            musicService!!.mediaPlayer!!.start()
            binding.btPlayPause.setIconResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)
            isPlaying = true
        }
    }

}
binding.btTimer.setOnClickListener {
    val timer = min15|| min30||min60;
    if(!timer){
        showBottomDialog()

    }
    else{
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Do You Want to close the timer")
            .setPositiveButton("Yes"){_,_ -> min15=false
            min30 = false
            min60=false
                binding.btTimer.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            }
            .setNegativeButton("No"){dialog,_ ->
                dialog.dismiss()


            }
        val customDialog = builder.create()
        customDialog.show()
        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

}
    }
    private var startForEqualizer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK){
            return@registerForActivityResult
        }
    }
    private fun setLayout(){
        fIndex = favouriteChecker(musicListPA[songPosition].id)
        try {
            Glide.with(this@PlayerActivity).load(musicListPA[songPosition].artUri).apply(RequestOptions().placeholder(R.drawable.icon)).centerCrop().into(binding.cover);
        }
        catch (e:Exception){
            Log.e("error",e.message.toString())
        }
        binding.songName.setText(musicListPA[songPosition].title);
        val timer = min15|| min30||min60;
if(timer){
    binding.btTimer.setColorFilter(ContextCompat.getColor(this,R.color.cool_blue))
}
if(isFav) binding.favBtnPA.setImageResource(R.drawable.favourite_icon)
        else{
    binding.favBtnPA.setImageResource(R.drawable.heart_outline)
        }
    }

    private fun startMusicService(){
        val intent = Intent(this,MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent);
    }

    private fun createMediaPlayer(){
        if(musicService!!.mediaPlayer==null){
            musicService!!.mediaPlayer = MediaPlayer()
        }

Log.d("debug", "mediaplayer func $songPosition")
        musicService!!.mediaPlayer!!.reset();
        musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)

        musicService!!.mediaPlayer!!.prepare()
        musicService!!.mediaPlayer!!.start()
        isPlaying = true
        repeat = false;
        musicService!!.showNotification(R.drawable.pause_icon)
        binding.seekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
        binding.seekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
        binding.seekBar.progress = 0

        binding.seekBar.max = musicService!!.mediaPlayer!!.duration
        musicService!!.mediaPlayer!!.setOnCompletionListener (this)
        nowPlayingSongId = musicListPA[songPosition].id
    }
    private fun prevNextSong(increment:Boolean){
       setSongPosition(increment);
        setLayout()
        createMediaPlayer()
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
val binder =p1 as MusicService.MyBinder
        Log.d("debug", songPosition.toString())
        musicService = binder.currentService()
        createMediaPlayer()
musicService!!.seekBarSetup()
        musicService!!.audioManager =  getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)

    }

    override fun onServiceDisconnected(p0: ComponentName?) {

        musicService = null;
    }

    override fun onCompletion(p0: MediaPlayer?) {
        if(repeat){
            createMediaPlayer()
            return;
        }
        setSongPosition(true)

        createMediaPlayer()
        try {
            setLayout()
        }
        catch (e:Exception){
            return
        }
    }

private fun showBottomDialog(){
    val dialg = BottomSheetDialog(this)
    dialg.setContentView(R.layout.bottom_sheet_dialogue)
    dialg.show()
    dialg.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
        if(min15){
            min15 = false
            binding.btTimer.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            Toast.makeText(this,"Timer removed",Toast.LENGTH_SHORT).show()

        }
        else{
            min15 = true
            min30 = false
            min60 = false
            binding.btTimer.setColorFilter(ContextCompat.getColor(this,R.color.cool_blue))
        }
        Thread{Thread.sleep(1000*60*15)
        if(min15){
           exitApplication()
        }
        }.start()

        dialg.dismiss()
    }
    dialg.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
        if(min30){
            min30 = false
            binding.btTimer.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            Toast.makeText(this,"Timer removed",Toast.LENGTH_SHORT)

        }
        else{
            min30 = true
            min60 = false
            min15 = false
            binding.btTimer.setColorFilter(ContextCompat.getColor(this,R.color.cool_blue))
        }
        Thread{Thread.sleep(1000*60*30)
            if(min15){
                exitApplication()
            }
        }.start()
        dialg.dismiss()
    }
    dialg.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
       if(min60){
           min60 = false
           binding.btTimer.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
           Toast.makeText(this,"Timer removed",Toast.LENGTH_SHORT)

       }
        else{
           min15 = false
           min30 = false
           min60 = true
           binding.btTimer.setColorFilter(ContextCompat.getColor(this,R.color.cool_blue))
       }
        Thread{Thread.sleep(1000*60*60)
            if(min15){
                exitApplication()
            }
        }.start()
        dialg.dismiss()
    }
}
}