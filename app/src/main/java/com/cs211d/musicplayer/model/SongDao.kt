package com.cs211d.musicplayer.model

import androidx.room.*

// "fun" is interface for kotlin code, the actual implementation is annotation SQL code
// when use "addSong(song)", is actually doing
// "INSERT INTO SongDB (ID,ALBUM,TITLE,ARTIST,PATH,...) VALUE ('01','album','title','path',...)"

@Dao
interface SongDao{

    @Insert(onConflict= OnConflictStrategy.IGNORE)
    fun addSong(song: Song): Long

    @Query("SELECT * FROM Song ORDER BY ALBUM")
    fun getAllSongSortedByAlbum(): List<Song>

    @Query("SELECT * FROM Song ORDER BY TITLE")
    fun getAllSongSortedByTitle(): List<Song>

    @Query("SELECT * FROM Song ORDER BY ARTIST")
    fun getAllSongSortedByArtist(): List<Song>

    // get one song by its ID
    @Query("SELECT * FROM Song WHERE ID = :id")
    fun getSongByID(id: Long): Song?

    @Query("DELETE FROM Song")
    fun clearSongDB()

}