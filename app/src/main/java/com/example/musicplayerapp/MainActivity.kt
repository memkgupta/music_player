package com.example.musicplayerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerapp.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    override fun onDestroy() {
        super.onDestroy()
     if(!PlayerActivity.isPlaying && PlayerActivity.musicService!=null){
         exitApplication()
     }

    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter
    companion object{
       lateinit var MusicListMA :ArrayList<Music>
       lateinit var musicListSearch:ArrayList<Music>
       var search:Boolean = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)


        toggle = ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
      if(requestRuntimePermission()){
          initLayout();
          FavouriteActivity.favouriteSongs = ArrayList()
          val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
          val jsonString = editor.getString("FavouriteSongs",null)
          val typeToken = object : TypeToken<ArrayList<Music>>(){}.type

          if(jsonString!=null){
              val data:ArrayList<Music> = GsonBuilder().create().fromJson(jsonString,typeToken)
              FavouriteActivity.favouriteSongs.addAll(data)
          }

      }
binding.btShuffle.setOnClickListener {
    Toast.makeText(this@MainActivity,"Songs are playing",Toast.LENGTH_SHORT).show()
    MusicListMA.shuffle()
    val intent = Intent(this,PlayerActivity::class.java)
    intent.putExtra("index",0);
    intent.putExtra("class","MainActivity")
    startActivity(intent)
}
        binding.btFav.setOnClickListener {
            val intent = Intent(this@MainActivity,FavouriteActivity::class.java)
            startActivity(intent)
        }
        binding.btPlayList.setOnClickListener {
            val intent = Intent(this@MainActivity,PlayListActivity::class.java)
            startActivity(intent)
        }


        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navFeedback -> Toast.makeText(baseContext,"Feedback",Toast.LENGTH_SHORT).show()
            R.id.navSetting -> Toast.makeText(baseContext,"Settings",Toast.LENGTH_SHORT).show()
                R.id.navAbout->Toast.makeText(baseContext,"About",Toast.LENGTH_SHORT).show()
                R.id.navExit-> exitProcess(1)
            }
            true
        }
    }
    private fun requestRuntimePermission(): Boolean {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)

            return false;
        }
       return true;
    }
 private fun initLayout(){
     search = false
     binding.musicRV.setHasFixedSize(true)
     binding.musicRV.setItemViewCacheSize(13)
     MusicListMA = getAllAudio()
     binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
     musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
     binding.totalSongs.text = "Total Songs :" + MusicListMA.size.toString()
     binding.musicRV.adapter = musicAdapter
 }
    @SuppressLint("Range")
    private fun getAllAudio():ArrayList<Music>{
val tempList = ArrayList<Music>();
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,
            MediaStore.Audio.Media.DATE_ADDED+" DESC",null)
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
val  titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val  albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val  artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val  durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri,albumIdC).toString()
                    val music = Music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC, artUri = artUriC)

               val file = File(music.path)
                if(file.exists()){
                    tempList.add(music)
                }
                }
                    while (cursor.moveToNext())
                    cursor.close()
            }
        }
        return  tempList;
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==13){
            if(grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                MusicListMA = getAllAudio();
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu,menu)
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
             if(newText!=null){
                 val userInput = newText.lowercase()
                 for(song in MusicListMA)
                     if(song.title.lowercase().contains(userInput))
                         musicListSearch.add(song)


                 search = true
                 musicAdapter.updateMusicList(searchList = musicListSearch)
             }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)

    }

    override fun onResume() {
        super.onResume()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSongs)
        editor.putString("FavouriteSongs",jsonString)
        editor.apply()
    }
}