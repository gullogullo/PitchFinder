package com.example.pitchfinder
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
//import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.example.pitchfinder.databinding.ActivityMainBinding
import org.puredata.android.io.AudioParameters
import org.puredata.android.io.PdAudio
import org.puredata.android.utils.PdUiDispatcher
import org.puredata.core.PdBase
import org.puredata.core.utils.IoUtils
import java.io.File
import java.io.IOException


class MainActivity : Activity(), View.OnClickListener {
    private var startGame: Button? = null
    //private var rules: Button? = null
    private var bestRecordTxt: TextView? = null
    private var exitApp: ImageButton? = null
    private var exitDialog: Dialog? = null
    //private var rulesDialog: Dialog? = null

    override fun onBackPressed() {
        showExitDialog()
    }

    @Throws(IOException::class)
    private fun initPD() {
        val sampleRate = AudioParameters.suggestSampleRate()
        PdAudio.initAudio(sampleRate, 0, 2, 8, true)
        val dispatcher = PdUiDispatcher()
        PdBase.setReceiver(dispatcher)
    }

    @Throws(IOException::class)
    private fun loadPDPatch() {
        val dir = filesDir
        IoUtils.extractZipResource(resources.openRawResource(R.raw.pitchfindernopan), dir, true)
        val pdPatch = File(dir, "pitchfinderNoPan.pd")
        PdBase.openPatch(pdPatch.absolutePath)
        PdBase.sendMessage("bangInit", "bang")
        PdBase.sendFloat("pan", 0f)
        PdBase.sendMessage("pan", "bang")
    }

    //var trialsCounter = 0
    //var dataString: java.lang.StringBuilder? = java.lang.StringBuilder("Action, Value, Time \n")
    //private lateinit var binding: ActivityMainBinding


    override fun onClick(view: View) {
        when (view.id) {
            R.id.startGameBtn -> startPitchActivity()
            R.id.exitAppBtn -> showExitDialog()
            //R.id.gameRulesBtn -> showRulesDialog()
            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //MainActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //var binding = ActivityMainBinding.inflate(layoutInflater)
        //val view = binding.root
        //setContentView(view)
        setContentView(R.layout.activity_main)

        // Disable landscape mode
        this@MainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //hideSystemUI();
        initUiElements()
        checkBestRecord()
        startGame!!.setOnClickListener(this)
        exitApp!!.setOnClickListener(this)
        //rules!!.setOnClickListener(this)
        //var dataString: java.lang.StringBuilder? = java.lang.StringBuilder("Action, Value, Time \n")
        try {
            initPD()
            loadPDPatch()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    // Initialization of UI elements
    private fun initUiElements() {
        startGame = findViewById(R.id.startGameBtn)
        //rules = findViewById(R.id.gameRulesBtn)
        exitApp = findViewById(R.id.exitAppBtn)
        bestRecordTxt = findViewById(R.id.recordTxt)
        exitDialog = Dialog(this)
        //rulesDialog = Dialog(this@MainActivity)
    }

    // Procedure starts pitch activity
    private fun startPitchActivity() {
        val intent = Intent(this@MainActivity, PitchActivity2::class.java)
        this@MainActivity.startActivity(intent)
    }

    // Check if we have best record now
    @SuppressLint("SetTextI18n")
    private fun checkBestRecord() {

        //val sharedPreferences = getSharedPreferences(ResultActivity.SHARED_PREFS, MODE_PRIVATE)
        //val editor: SharedPreferences.Editor = sharedPreferences.edit()
        //editor.clear().commit()

        val bestRecord = loadSharedPreferences()
        if (loadSharedPreferences() != "") {
            bestRecordTxt!!.visibility = View.VISIBLE
            bestRecordTxt!!.text = bestRecordTxt!!.text.toString() + bestRecord
            return
        }
    }

    // Load best game record from prefs
    private fun loadSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences(
            ResultActivity.SHARED_PREFS,
            MODE_PRIVATE
        )
        return sharedPreferences.getString(ResultActivity.BEST_GAME_RECORD, "")
    }

    // Procedure shows exit dialog
    private fun showExitDialog() {
        exitDialog?.setContentView(R.layout.app_exit_dialog_layout)
        exitDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val exitNo = exitDialog!!.findViewById<Button>(R.id.noExitBtn)
        val exitYes = exitDialog!!.findViewById<Button>(R.id.yesExitBtn)
        exitDialog!!.show()
        exitNo.setOnClickListener { exitDialog!!.dismiss() }
        exitYes.setOnClickListener {
            exitDialog!!.dismiss()
            finishAffinity()
        }
    }

    // Show rules dialog
}