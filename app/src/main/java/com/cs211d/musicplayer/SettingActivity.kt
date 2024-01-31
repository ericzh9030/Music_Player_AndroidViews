package com.cs211d.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, MyPreferencesFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class MyPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }
}