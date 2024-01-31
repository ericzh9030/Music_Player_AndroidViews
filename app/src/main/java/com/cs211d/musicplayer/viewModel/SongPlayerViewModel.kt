package com.cs211d.musicplayer.viewModel

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cs211d.musicplayer.model.Player
import com.cs211d.musicplayer.model.Song
import com.cs211d.musicplayer.model.SongRepository


// all fragments interact with this ViewModel, and this interacts with DAO/DB

class SongPlayerViewModel (application: Application) : AndroidViewModel(application) {


    /////////////////////////// DAO database/repository part //////////////////////////

    // track update time
    val refreshTime = MutableLiveData<Long>()
    fun setRefreshTime(time: Long){
        songRepository.setRefreshTime(time)
    }
    // get singleton song database object
    private val songRepository = SongRepository.getInstance(this.getApplication(), refreshTime)

    // call repository addSong (pass by reference), it assign the generated ID to given song object
    fun addSong(song: Song) = songRepository.addSong(song)

    // get one song by its ID
    fun getSongByID(id: Long): Song? = songRepository.getSongByID(id)

    // clear all songs in database
    fun clearSongDB(){
        songRepository.clearSongDB()
    }

    // get a list of all songs from repository
    fun getAllSongByAlbum(): List<Song> = songRepository.getAllSongByAlbum()

    fun getAllSongByTitle(): List<Song> = songRepository.getAllSongByTitle()

    fun getAllSongByArtist(): List<Song> = songRepository.getAllSongByArtist()


    ////////////////////////// Music player part /////////////////////////////////
    // liveData (current selection) to notify Player_fragment when click on List_fragment
    val selectedSongID = MutableLiveData<Long>()
    fun setSelectedSongID(id: Long){
        selectedSongID.value = id
    }

    // Music player (singleton)
    val donePlaying = MutableLiveData<Boolean>() // for player_fragment to know a song play is finished or not
    private val player = Player.getInstance(donePlaying)

    fun play(){
       player.play()
    }

    fun pause(){
        player.pause()
    }

    // initiate new song to play
    fun initiate(id: Long): Boolean {
        Log.d("trigger", "init song id $id")
        return player.initiate(getApplication(), songRepository.getSongByID(id)!!.PATH.toUri())
    }

}