package com.cs211d.musicplayer.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Room


// create Singleton object of DAO database, used by viewModel

class SongRepository private constructor(context: Context, liveData: MutableLiveData<Long>){

    companion object{
        private var instance: SongRepository? = null

        fun getInstance(context: Context, liveData: MutableLiveData<Long>): SongRepository{
            if (instance == null){
                instance = SongRepository(context, liveData)
            }
            return instance!!
        }
    }

    private val database: SongDatabase = Room.databaseBuilder(context.applicationContext, SongDatabase::class.java, "song.db")
        .allowMainThreadQueries()
        .build()

    private val songDao = database.songDao()

    private val refreshTime = liveData


    ///////////////////// functions to call DAO methods ///////////////////////////////

    // add one song, DAO "INSERT" return generated ID, assign to given song object
    fun addSong(song:Song): Long{
        song.ID = songDao.addSong(song)
        return song.ID
    }

    fun getAllSongByAlbum(): List<Song> {
        return songDao.getAllSongSortedByAlbum()
    }

    fun getAllSongByTitle(): List<Song>{
        return songDao.getAllSongSortedByTitle()
    }

    fun getAllSongByArtist(): List<Song>{
        return songDao.getAllSongSortedByArtist()
    }

    // get one song by its ID
    fun getSongByID(id: Long): Song? {
        return songDao.getSongByID(id)
    }

    // delete all songs from database
    fun clearSongDB(){
        songDao.clearSongDB()
    }

    fun setRefreshTime(time:Long){
        refreshTime.value = time
    }

}