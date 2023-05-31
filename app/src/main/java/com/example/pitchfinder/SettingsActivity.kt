package com.example.pitchfinder

//import android.util.Log

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
//import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat


class SettingsActivity : Activity() {


    //@RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        //val fragment: Fragment = fragmentManager.findFragmentById(R.id.fragmentId) as Fragment


        val minFreqBar = findViewById<ProgressBar>(R.id.minFreqBar) as SeekBar
        val maxFreqBar = findViewById<ProgressBar>(R.id.maxFreqBar) as SeekBar

        minFreqBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                maxFreqBar.max = minFreqBar.progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        /*var minFreqBar = findViewById<ProgressBar>(R.id.minFreqBar) as SeekBar
        var maxFreqBar = findViewById<ProgressBar>(R.id.maxFreqBar) as SeekBar
        var freqBar = findViewById<ProgressBar>(R.id.freqBar) as SeekBar
        var numberTestBar = findViewById<ProgressBar>(R.id.numberTestBar) as SeekBar
        maxFreqBar.min = minFreqBar.progress
        minFreqBar.
        */



    }



    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val view = super.onCreateView(inflater, container, savedInstanceState)
            view.setBackgroundColor(resources.getColor(android.R.color.white))
            return view
        }
    }
}