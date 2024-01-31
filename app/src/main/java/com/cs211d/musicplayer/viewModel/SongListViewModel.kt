package com.cs211d.musicplayer.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.cs211d.musicplayer.model.Song

class SongListViewModel (application: Application) : AndroidViewModel(application) {

    var newStart = true

    lateinit var songList : List<Song>
    var sortedBy = "Title"
}