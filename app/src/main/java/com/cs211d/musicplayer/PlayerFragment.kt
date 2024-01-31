package com.cs211d.musicplayer


import com.cs211d.musicplayer.viewModel.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager


class PlayerFragment : Fragment() {

    // shared ViewModel
    private val songPlayerViewModel: SongPlayerViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_player, container, false)

        // set button onClick listener
        val playPauseButton = rootView.findViewById<Button>(R.id.playPauseButton)
        playPauseButton.setOnClickListener { playPause(playPauseButton) }

        // add observer to viewModel LiveData, when update, trigger lambda code: play()
        val selectedSongObserver = Observer<Long> { id ->
            // currently playing music, update the button from "Play" to "Pause"
            play(playPauseButton, true)
            showMetadata(rootView, id)
        }
        songPlayerViewModel.selectedSongID.observe(viewLifecycleOwner, selectedSongObserver)

        // observer when current song is done, update button from "Pause" to "play"
        songPlayerViewModel.donePlaying.observe(viewLifecycleOwner) { if (it){pause(playPauseButton)} }

        // auto-reload enable, go to folder_fragment when first time run this APP
        if (playerViewModel.newStart){
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val autoReload = sharedPrefs.getBoolean("auto_reload", false)
            // pop up Toast alert that Auto listing song from folder enable, require extra step
            if (autoReload){
                Toast.makeText(requireContext(), "Auto Reload Enable, Please Go to Folder Tab.", Toast.LENGTH_LONG)
                    .show()
            }
            songPlayerViewModel.pause()
            playerViewModel.newStart = false
        }

        return rootView
    }


    // determine which new state the button need to change
    private fun playPause(button: Button) {
        if (songPlayerViewModel.selectedSongID.value != null){
            if (button.text == getString(R.string.play)){
                play(button)
            }else{
                pause(button)
            }
        }
    }

    private fun play(button:Button, isPlaying:Boolean = false){
        if (!isPlaying){
            songPlayerViewModel.play()
        }
        button.text = getString(R.string.pause)
    }

    private fun pause(button: Button){
        songPlayerViewModel.pause()
        button.text = getString(R.string.play)
    }

    // display song metadata on player screen
    private fun showMetadata(view: View, id:Long){
        val song = songPlayerViewModel.getSongByID(id)
        view.findViewById<TextView>(R.id.song_title).text = song?.TITLE
        view.findViewById<TextView>(R.id.song_artist).text = song?.ARTIST
        view.findViewById<TextView>(R.id.song_album).text = song?.ALBUM
    }

}