package com.cs211d.musicplayer


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.cs211d.musicplayer.viewModel.SongPlayerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

private lateinit var settingMenu : Menu

class MainActivity : AppCompatActivity() {

    private val songPlayerViewModel : SongPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set up navigation actions
        val navMenuView = findViewById<BottomNavigationView>(R.id.nav_menu)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration.Builder(navController.graph).build()

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)
        NavigationUI.setupWithNavController(navMenuView, navController)

    }


    // go back button
    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // inflates the setting menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        settingMenu = menu!!
        //return super.onCreateOptionsMenu(menu)
        return true
    }

    // what to do when select a menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.more_info -> {
                showFileName()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // use AlertDialog to show current playing file name
    private fun showFileName(){
        val showFileNameDialog = AlertDialog.Builder(this)
        showFileNameDialog.setTitle("Current Audio File Name")
        // get file name
        val id = songPlayerViewModel.selectedSongID.value
        val filename = id?.let { songPlayerViewModel.getSongByID(it)?.FILENAME ?: "No File" }
        showFileNameDialog.setMessage(filename)
        showFileNameDialog.setPositiveButton("OK"){ dialog, which ->
        }
        showFileNameDialog.show()
    }

}
