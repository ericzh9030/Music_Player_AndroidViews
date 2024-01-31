package com.cs211d.musicplayer.model

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.io.IOError

class Player private constructor(liveData: MutableLiveData<Boolean>){

    // singleton
    companion object{
        private var instance: Player? = null

        fun getInstance(liveData: MutableLiveData<Boolean>): Player {
            if (instance == null){
                instance = Player(liveData)
            }
            return instance!!
        }
    }

    private val donePlaying = liveData

    private val mediaPlayer = create()
    // create single Media Player object, even listener to handle after prep() and finish play
    private fun create(): MediaPlayer {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener { this.play() }
        mediaPlayer.setOnCompletionListener { donePlaying.value = true }
        return mediaPlayer
    }

    // initialize given song and play
    fun initiate(context: Context, uri: Uri):Boolean{
        try {
            if(isPlaying()){
                stop()
            }
            donePlaying.value = false
            mediaPlayer.reset()
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build())
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.prepareAsync()
        }catch (e:IOError){
            return false
        }
        return true
    }

    // check if is currently playing song
    private fun isPlaying():Boolean{
        var status = false
        try {
            status = mediaPlayer.isPlaying
        }catch (e:IllegalStateException){
            Log.d("Player_Log", "Wrong Action: isPlaying()")
        }
        return status
    }

    // playback controls
    fun play(){
        mediaPlayer.start()
    }

    fun pause(){
        mediaPlayer.pause()
    }

    private fun stop(){
        mediaPlayer.stop()
    }

}