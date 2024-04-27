package com.example.musicplayerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerapp.databinding.FragmentNowPlayingBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NowPlaying.newInstance] factory method to
 * create an instance of this fragment.
 */
class NowPlaying : Fragment() {
    @SuppressLint("StaticFieldLeak")
    companion object{
        lateinit var binding:FragmentNowPlayingBinding
    }
    // TODO: Rename and change types of parameters




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.btPlayPauseNp.setOnClickListener {
            if(PlayerActivity.isPlaying){
                pauseMusic()
            }
            else{
                playMusic()
            }
        }

        binding.nextBtnNp.setOnClickListener {
            setSongPosition(true)
            PlayerActivity.musicService!!.createMediaPlayer()


            PlayerActivity.binding.songName.setText(PlayerActivity.musicListPA[PlayerActivity.songPosition!!].title);
            try {
                Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri).apply(
                    RequestOptions().placeholder(R.drawable.icon)).centerCrop().into(NowPlaying.binding.coverNp);
            }
            catch (e:Exception){
                Log.e("error",e.message.toString())
            }
            NowPlaying.binding.songNameNp.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)

            playMusic()
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(),PlayerActivity::class.java)
            intent.putExtra("index",PlayerActivity.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        // Inflate the layout for this fragment
        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.musicService!=null){
            binding.root.visibility = View.VISIBLE
            binding.songNameNp.isSelected  = true
            try {
                Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri).apply(
                    RequestOptions().placeholder(R.drawable.icon)).centerCrop().into(binding.coverNp);
            }
            catch (e:Exception){
                Log.e("error",e.message.toString())
            }
            binding.songNameNp.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
           if(PlayerActivity.isPlaying){
               binding.btPlayPauseNp.setIconResource(R.drawable.pause_icon)
           }
            else{
               binding.btPlayPauseNp.setIconResource(R.drawable.play_icon)
           }

        }
    }

    private fun playMusic(){
    PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.btPlayPauseNp.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.btPlayPause.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying = true
    }
    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.btPlayPauseNp.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.btPlayPause.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying = false
    }
}