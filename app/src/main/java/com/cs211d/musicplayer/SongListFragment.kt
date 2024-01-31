package com.cs211d.musicplayer

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.cs211d.musicplayer.model.Song
import com.cs211d.musicplayer.viewModel.SongListViewModel
import com.cs211d.musicplayer.viewModel.SongPlayerViewModel

const val sortByPreferKey = "sorted_by"

class SongListFragment : Fragment() {

    private val songPlayerViewModel : SongPlayerViewModel by activityViewModels()
    private val songListViewModel : SongListViewModel by activityViewModels()

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_song_list, container,false)

        // add observer to viewModel LiveData, when update, trigger lambda code: getSongSortBy()
        val refreshDBObserver = Observer<Long> { time ->
            // DB has been reload, refresh the song list
            getSongSortBy(songListViewModel.sortedBy)
            constructRecycleView(rootView)
        }
        songPlayerViewModel.refreshTime.observe(viewLifecycleOwner, refreshDBObserver)

        // get saved preference: sort by
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val sortBy = sharedPrefs.getString(sortByPreferKey, "Title")

        // if first time run the APP or update "sort by" from preference, get all songs from DB
        if (songListViewModel.newStart || (sortBy != songListViewModel.sortedBy)){

            if (sortBy != null){
                songListViewModel.sortedBy = sortBy
            }
            // get all song from DB sorted by
            getSongSortBy(sortBy)
            songListViewModel.newStart = false
        }

        constructRecycleView(rootView)

        return rootView
    }

    private fun constructRecycleView(rootView:View){
        // onClick even listener for each song in list,
        // when click, initiate(play) audio file, and update current song ID in shared ViewModel
        val onClickListener = View.OnClickListener { itemView: View ->
            // notify player_fragment we select a song to play
            if(songPlayerViewModel.initiate(itemView.tag as Long)){
                songPlayerViewModel.setSelectedSongID(itemView.tag as Long)
            }else{
                Toast.makeText(requireContext(), "Cannot Initiate Audio File", Toast.LENGTH_LONG)
                    .show()
            }
        }

        // create RecycleView
        val songRecyclerView: RecyclerView = rootView.findViewById(R.id.song_list_recycle_view)
        songRecyclerView.adapter = ListAdapter(songListViewModel.songList, onClickListener)

        // add separate line between songs
        val divider = DividerItemDecoration(songRecyclerView.context, DividerItemDecoration.VERTICAL)
        songRecyclerView.addItemDecoration(divider)
    }


    // parameter: a list to be displayed in RecycleView
    // return: a RecycleView that knows where to display and already setup holder for each item
    private class ListAdapter(val songList: List<Song>, val onClickListener: View.OnClickListener) : RecyclerView.Adapter<SongHolder>(){

        // inflate View that includes using "WHAT" (item_song) layout and "WHAT" (songList) content to display RecycleView
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item_song, parent, false)

            // return holder that knows where to display item, and iterate the list properly to replace/show text
            return  SongHolder(view)
        }

        // bind each item(song) (guess something will call bind() and iterate whole list)
        override fun onBindViewHolder(holder: SongHolder, position: Int) {
            val song = songList[position]
            holder.bind(song)
            holder.itemView.tag = song.ID
            holder.itemView.setOnClickListener(onClickListener)
        }

        // size of given list
        override fun getItemCount(): Int {
            return songList.size
        }

    }

    /* parameter: an inflater for later inflate as a View inside RecycleView.ViewHolder()
       RecycleView takes an inflated (detail) View that describes what(songList) and where(list_fragment) to display RecycleView
       bind each item(song) to textView (many)
     */
    private class SongHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        // choose (item_song) as base TextView for individual(single) item to display
        private val songListView:TextView = itemView.findViewById(R.id.item_song)

        // replace TextView's text
        fun bind(song: Song){
            songListView.text = song.TITLE
        }
    }

    // get all song from DB sorted by
    private fun getSongSortBy(order:String?){
        Log.d("trigger", "sort by $order")
       songListViewModel.songList = when(order){
            "Title" -> {
                songPlayerViewModel.getAllSongByTitle()
            }
            "Album" -> {
                songPlayerViewModel.getAllSongByAlbum()
            }
            else -> {
                songPlayerViewModel.getAllSongByArtist()
            }
        }
    }

}