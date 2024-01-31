package com.cs211d.musicplayer


import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.cs211d.musicplayer.model.Song
import com.cs211d.musicplayer.viewModel.FolderViewModel
import com.cs211d.musicplayer.viewModel.SongPlayerViewModel

val supportedFile = listOf("mp3", "aac", "mp4")

const val selectedFolderKey = "Selected Folder"
const val selectedPathKey = "Selected File Path"

class DirSelectorFragment :Fragment() {

    private val folderViewModel : FolderViewModel by activityViewModels()
    private val songPlayerViewModel : SongPlayerViewModel by activityViewModels()

    private lateinit var openFolderButton: Button
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_folder, container, false)

        // set up open folder button listener
        openFolderButton = rootView.findViewById(R.id.openFolderButton)
        openFolderButton.setOnClickListener{ openFolder() }

        // create or get repository instance
        //songPlayerViewModel = SongPlayerViewModel(requireActivity().application)

        // get user preferences
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // display selected folder in fragment textview
        val selectedFolder = sharedPrefs.getString(selectedFolderKey, null)
        if (selectedFolder != null){
            rootView.findViewById<TextView>(R.id.selectedFolder).text = selectedFolder
        }

        // auto reload audio file from the PATH in preference (auto-reload is on)
        if (folderViewModel.newStart){
            val autoReload = sharedPrefs.getBoolean("auto_reload", false)
            val selectedPath = sharedPrefs.getString(selectedPathKey, null)
            if (autoReload && (selectedPath != null)){
                reloadDB(selectedPath.toUri())
            }
            folderViewModel.newStart = false
        }

        return rootView
    }


    private fun openFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        activityResultLauncher.launch(intent)
    }

    // intent launcher
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){

            // get user selected folder as URI, then save the permission for future access
            val musicFolder = Uri.parse(result.data?.dataString)
            savePermission(musicFolder)

            // save selected folder path to preferences
            if (musicFolder != null){
                view!!.findViewById<TextView>(R.id.selectedFolder)!!.text = musicFolder.path.toString().split(":").last()
                val prefEditor = sharedPrefs.edit()
                prefEditor.putString(selectedPathKey, musicFolder.toString())
                prefEditor.putString(selectedFolderKey,musicFolder.path.toString().split(":").last())
                prefEditor.apply()
            }

            reloadDB(musicFolder)
        }
    }

    private fun reloadDB(uri:Uri){
        val path = DocumentFile.fromTreeUri(requireContext(), uri)
        // clear current database first, then list all audio files
        songPlayerViewModel.clearSongDB()
        if(listAllFiles(path)){
            songPlayerViewModel.setRefreshTime(System.currentTimeMillis())
            Toast.makeText(requireContext(), "Finished Reload All Songs from Folder", Toast.LENGTH_LONG)
                .show()
        }
    }

    // after open a folder and get permission, save it permanently so next time no need to ask permission
    private fun savePermission(uri: Uri){
        val contentResolver = requireContext().contentResolver
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, takeFlags)
    }

    // recursively list all files from sub-folders
    private fun listAllFiles(path: DocumentFile?):Boolean{

        path!!.listFiles().forEach {file ->
            if (file.isDirectory){
                // recursive
                listAllFiles(file)
            }else{
                // if file extension is supported, add it to DB
                if (file.uri.toString().split(".").last() in supportedFile ){
                    // extract all metadata and put song into DB
                    val song = extractMetadata(file)
                    song.ID = songPlayerViewModel.addSong(song)
                }
            }
        }
        return true
    }

    // extract metadata from file, construct a Song object
    private fun extractMetadata(file: DocumentFile): Song{
        val metaData = MediaMetadataRetriever()
        metaData.setDataSource(requireContext(), file.uri)
        val album = metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM).toString()
        var title = metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()
        val artist = metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()
        val filePath = file.uri.toString()
        val filename = file.name.toString()

        // no Title in audio file's metadata, use file name
        if(title == "null"){
            title = filename
        }

        return Song(0,album,title,artist,filename,filePath)
    }

}
