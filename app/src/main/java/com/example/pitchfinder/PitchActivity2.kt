package com.example.pitchfinder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import com.example.pitchfinder.views.CustomView
import com.example.pitchfinder.views.ResultView
import com.gusakov.library.PulseCountDown
import org.greenrobot.eventbus.EventBus
import org.puredata.android.io.PdAudio
import org.puredata.core.PdBase
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class PitchActivity2 : Activity() {

    private var score = 0
    private var epsilon = 0.001
    //private val size = 10
    private var gamesCount = 4 //5
    private var indexTest = 0
    //private val colors = arrayOf<String>("#6BFD00", "#ADFD00", "#D7FD00", "#EEFD00", "#FDF500",
    // "#FDDE00", "#FDC000", "#FDA500", "#FD6400", "#FD0000")

    private var dataString: java.lang.StringBuilder? = java.lang.StringBuilder("Action, Value, Time \n")
    private var randomFreq :String = ""
    private var bands = 0
    // private var stringRows: List<String> = listOf()
    // private var timeDelay = 0L
    // private var bangString: String = ""
    // private var freqString: String = ""
    // private var gainString: String = ""

    // TODO!
    //private var panning = false
    //private var doubletone = false
    //private var delayInt = 1
    private var timbreInt = 100
    private var testBool = false
    //private var minFreq = 1 //250
    //private var maxFreq = 8000 //4000
    //private var frequenciesTotal = 250
    // var testTotal = 3
    //private var freqTest1 = 750
    //private var freqTest2 = 1000
    //private var freqTest3 = 2000
    //private var freqTest4 = 1250
    //private var freqTest5 = 1500
    //private var testsPerFreq = 5
    private var testRandom : List<Int> = emptyList()

    var trialsCounter = 0

    var FINAL_SCORE_DRUM_VALUE_EXTRA = "FINAL_SCORE_DRUM_VALUE_EXTRA"
    var FINAL_SCORE_PITCH_VALUE_EXTRA = "FINAL_SCORE_PITCH_VALUE_EXTRA"
    //var FINAL_SCORE_VALUE_EXTRA = "FINAL_SCORE_VALUE_EXTRA"

    private var diaryDialog: Dialog? = null
    //private var exitDialog: Dialog? = null

    //var sharingIsActive = false

    class SaveStringBuilderEvent(var sb: java.lang.StringBuilder?)


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /*
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }*/

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reset -> {
                val fragmentPresent = supportFragmentManager.findFragmentById(android.R.id.content)
                if (fragmentPresent != null) {
                    loadSettings()
                    onBackPressed()
                    //true
                }
            }

            R.id.settingsbar -> {
                supportFragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack("ToDoFragment")
                    .replace(android.R.id.content, SettingsFragment())
                    .commit()
            }
            R.id.exit -> {
                showExitDialog()
                //showAlertDialogButtonClicked()
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    /*
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
    }*/

    /*
    @SuppressLint("SetTextI18n")
    fun showAlertDialogButtonClicked() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Ti suona bene?")
        builder.setMessage("Sei sicuro di averne sentite abbastanza?")
        // var mCustomView = findViewById<CustomView>(R.id.customView)
        // ATTENZIONE! AGGIUNTO x3!!!
        // var gain = 1f/90f as Float
        // var gain =  90f

        builder.setPositiveButton("Sì, Invia i dati"
        ) { _, _ ->
            Log.e("Total String", dataString.toString())
            score = 0
            // var initTime = 0L
            // var finalTime = 0L
            val progressBar = findViewById<ProgressBar>(R.id.determinateBar)
            progressBar.progress = 0
            // PLAY THE MELODY
            /*
            var rowList: List<String> = dataString.toString().split("\n")
            initTime = rowList[1].split(", ")[2].split(":")[0].toLong()*60*60*1000+
                    rowList[1].split(", ")[2].split(":")[1].toLong()*60*1000+
                    rowList[1].split(", ")[2].split(":")[2].split(".")[0].toLong()*1000+
                    rowList[1].split(", ")[2].split(":")[2].split(".")[1].toLong()
            finalTime = rowList[rowList.count()-2].split(", ")[2].split(":")[0].toLong()*60*60*1000+
                    rowList[rowList.count()-2].split(", ")[2].split(":")[1].toLong()*60*1000+
                    rowList[rowList.count()-2].split(", ")[2].split(":")[2].split(".")[0].toLong()*1000+
                    rowList[rowList.count()-2].split(", ")[2].split(":")[2].split(".")[1].toLong()
            Log.i("finalTime-initTime", (finalTime-initTime).toString())
            for (row in 1..rowList.count()-2) {
                stringRows = rowList[row].split(", ")
                val equaldB: Float = mCustomView.equalLoudness(stringRows[1].toFloat(), gain)
                val dateFormatCurrent = stringRows[2].split(":")
                val secsMillisecsCurrent = dateFormatCurrent[2].split(".")
                var timeInMillisCurrent = secsMillisecsCurrent[1].toLong()+
                        secsMillisecsCurrent[0].toLong()*1000+
                        dateFormatCurrent[1].toLong()*60*1000+
                        dateFormatCurrent[0].toLong()*60*60*1000
                var timeInMillisFormer = 0L
                if (row == 1) {
                    timeInMillisFormer = initTime
                }
                else {
                    val dateFormatFormer = rowList[row - 1].split(", ")[2].split(":")
                    val secsMillisecsFormer = dateFormatFormer[2].split(".")
                    timeInMillisFormer = secsMillisecsFormer[1].toLong() +
                            secsMillisecsFormer[0].toLong() * 1000 +
                            dateFormatFormer[1].toLong() * 60 * 1000 +
                            dateFormatFormer[0].toLong() * 60 * 60 * 1000
                }
                timeDelay = timeInMillisCurrent - timeInMillisFormer
                if (stringRows[0] == "Frequency"){
                    bangString = "bangMyTone"
                    freqString = "freq"
                    gainString = "gainTotal"

                }
                else {
                    bangString = "bangTest"
                    freqString = "freqTest"
                    gainString = "gainTotalTest"
                }
                Log.i("Time difference", timeDelay.toString())
                Handler(Looper.getMainLooper()).postDelayed({
                    PdBase.sendMessage("bangInit", "bang")
                    PdBase.sendFloat(freqString, stringRows[1].toFloat())
                    PdBase.sendFloat("tauTest", mCustomView.timbreTau)
                    PdBase.sendFloat("tau", mCustomView.timbreTau)
                    PdBase.sendFloat(gainString, equaldB)
                    PdBase.sendMessage(bangString, "bang")
                }, timeDelay/(finalTime-initTime)*10000)
            }*/
            //Handler(Looper.getMainLooper()).postDelayed({
            //    export()
            //}, 1000)
        }
        builder.setNeutralButton("Ripeti senza salvare"
        ) { _, _ ->
            Log.e("Total String", dataString.toString())
            score = 0
            dataString = null
            dataString = java.lang.StringBuilder("Action, Value, Time \n")
            val nextButton = findViewById<Button>(com.example.pitchfinder.R.id.nextButton)
            val progressBar = findViewById<ProgressBar>(R.id.determinateBar)
            progressBar.progress = 0
            nextButton.text = "Next" //"Start"
        }
        //builder.setNegativeButton("Quit") { _, _ ->
        //    finish()
        //    exitProcess(0)
        //}
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }*/


    private fun showDiaryDialog() {
        val scoreTxt = findViewById<TextView>(R.id.scoreTxt)
        val finalScore = scoreTxt.text.toString()
        diaryDialog?.setContentView(R.layout.game_rules_dialog_layout)
        diaryDialog?.show()
        //val closeDiary = diaryDialog?.findViewById<ImageButton>(R.id.closeRulesBtn)
        val radioSoundGroup = diaryDialog?.findViewById<RadioGroup>(R.id.radioGroupSound)
        var soundString = ""
        val radioMoodGroup = diaryDialog?.findViewById<RadioGroup>(R.id.radioGroupMood)
        var moodString = ""
        val radioVolumeGroup = diaryDialog?.findViewById<RadioGroup>(R.id.radioGroupVolume)
        var volumeString = ""
        val radioWhereGroup = diaryDialog?.findViewById<RadioGroup>(R.id.radioGroupWhere)
        var whereString = ""
        val btnSubmit = diaryDialog?.findViewById<TextView>(R.id.submitText)
        val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
        btnSubmit?.startAnimation(bounce)
        radioSoundGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.SoundHeadphones -> {
                    val buttonSoundHeadphones = diaryDialog?.findViewById<RadioButton>(R.id.SoundHeadphones)
                    if (buttonSoundHeadphones != null) {
                        soundString = buttonSoundHeadphones.text.toString()
                    }
                }
                R.id.SoundOther -> {
                    val buttonSoundOther = diaryDialog?.findViewById<RadioButton>(R.id.SoundOther)
                    if (buttonSoundOther != null) {
                        soundString = buttonSoundOther.text.toString()
                    }
                }
                R.id.SoundTablet -> {
                    val buttonSoundTablet = diaryDialog?.findViewById<RadioButton>(R.id.SoundTablet)
                    if (buttonSoundTablet != null) {
                        soundString = buttonSoundTablet.text.toString()
                    }
                }
            }
        }
        radioMoodGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.Happy -> {
                    val buttonHappy = diaryDialog?.findViewById<RadioButton>(R.id.Happy)
                    if (buttonHappy != null) {
                        moodString = buttonHappy.text.toString()
                    }
                }
                R.id.Angry -> {
                    val buttonAngry = diaryDialog?.findViewById<RadioButton>(R.id.Angry)
                    if (buttonAngry != null) {
                        moodString = buttonAngry.text.toString()
                    }
                }
                R.id.Bored -> {
                    val buttonBored = diaryDialog?.findViewById<RadioButton>(R.id.Bored)
                    if (buttonBored != null) {
                        moodString = buttonBored.text.toString()
                    }
                }
            }
        }
        radioVolumeGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.VolumeHigh -> {
                    val buttonHigh = diaryDialog?.findViewById<RadioButton>(R.id.VolumeHigh)
                    if (buttonHigh != null) {
                        volumeString = buttonHigh.text.toString()
                    }
                }
                R.id.VolumeMedium -> {
                    val buttonMedium = diaryDialog?.findViewById<RadioButton>(R.id.VolumeMedium)
                    if (buttonMedium != null) {
                        volumeString = buttonMedium.text.toString()
                    }
                }
                R.id.VolumeLow -> {
                    val buttonLow = diaryDialog?.findViewById<RadioButton>(R.id.VolumeLow)
                    if (buttonLow != null) {
                        volumeString = buttonLow.text.toString()
                    }
                }
            }
        }
        radioWhereGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.BigRoom -> {
                    val buttonBig = diaryDialog?.findViewById<RadioButton>(R.id.BigRoom)
                    if (buttonBig != null) {
                        whereString = buttonBig.text.toString()
                    }
                }
                R.id.SmallRoom -> {
                    val buttonSmall = diaryDialog?.findViewById<RadioButton>(R.id.SmallRoom)
                    if (buttonSmall != null) {
                        whereString = buttonSmall.text.toString()
                    }
                }
                R.id.MediumRoom -> {
                    val buttonMediumRoom = diaryDialog?.findViewById<RadioButton>(R.id.MediumRoom)
                    if (buttonMediumRoom != null) {
                        whereString = buttonMediumRoom.text.toString()
                    }
                }
            }
        }
        btnSubmit?.setOnClickListener {
            if (btnSubmit.text == "Invia!") {
                val currentTimeSettings: String = SimpleDateFormat(
                    "HH:mm:ss.SSS",
                    Locale.getDefault()
                ).format(Date())
                dataString?.append("Sound setting, $soundString, $currentTimeSettings\n")
                dataString?.append("Mood setting, $moodString, $currentTimeSettings\n")
                dataString?.append("Volume setting, $volumeString, $currentTimeSettings\n")
                dataString?.append("Room setting, $whereString, $currentTimeSettings\n")
                Log.e("dataString FINALE", dataString.toString())
                export()
                val textSubmit = diaryDialog?.findViewById<TextView>(R.id.submitText)
                textSubmit?.text = "Fatto"
            } else {startResultActivity(finalScore)}
            //startResultActivity(finalScore)
            //Handler(Looper.getMainLooper()).postDelayed({
            //    //TODO BACK HOME
            //    //Log.e("ACTIVITY", sharingIsActive.toString())
            //    startResultActivity(finalScore)
            //}, 10000)
        }
        //closeDiary?.setOnClickListener { diaryDialog!!.dismiss() }
    }

    /*
    @SuppressLint("SetTextI18n")
    fun loadSettings(){
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        panning = sp.getBoolean("panning", false)
        doubletone = sp.getBoolean("doubletone", false)
        delayInt = sp.getInt("delay", 1)
        timbreInt = sp.getInt("timbre", 100)
        testBool = sp.getBoolean("test", false)
        minFreq = sp.getInt("minfrequency", 500) //125)
        maxFreq = sp.getInt("maxfrequency", 4000) //8000)
        frequenciesTotal = sp.getInt("frequencies", 250)
        freqTest1 = sp.getString("frequency1", 750.toString())!!.toInt()
        freqTest2 = sp.getString("frequency2", 1000.toString())!!.toInt()
        freqTest3 = sp.getString("frequency3", 2000.toString())!!.toInt()
        freqTest4 = sp.getString("frequency4", 1250.toString())!!.toInt()
        freqTest5 = sp.getString("frequency5", 1500.toString())!!.toInt()

        testsPerFreq = sp.getInt("tests_number", 5)

        val mCustomView = findViewById<CustomView>(R.id.customView)
        if (panning) {
            PdBase.sendFloat("pan", 1f)
            PdBase.sendMessage("pan", "bang")
        }
        else{
            PdBase.sendFloat("pan", 0f)
            PdBase.sendMessage("pan", "bang")
        }
        if (doubletone) {
            //binding.playSound.visibility = View.GONE
            //val playSound = findViewById<Button>(R.id.playSound)
            //playSound.visibility = View.GONE
            //binding.playTest.visibility = View.GONE
            val playTest = findViewById<Button>(com.example.pitchfinder.R.id.playTest)
            playTest.visibility = View.GONE
            mCustomView.delayDouble = delayInt*250
            mCustomView.doubletone = "bangDouble"
            if (score == 0) {
                mCustomView.doubleToneInit = 0
            }
            else {
                mCustomView.doubleToneInit = 1
            }
        }
        else {
            //binding.playSound.visibility = View.VISIBLE
            //binding.playTest.visibility = View.VISIBLE
            //val playSound = findViewById<Button>(R.id.playSound)
            //playSound.visibility = View.VISIBLE
            val playTest = findViewById<Button>(R.id.playTest)
            playTest.visibility = View.VISIBLE
            mCustomView.doubletone = "bangMyTone"
        }
        if (testBool)
        {
            mCustomView.lowestFreq = minFreq.toFloat()
            mCustomView.highestFreq = maxFreq.toFloat()
            randomFreq = mCustomView.randomTestFreq()
            //testRandom = mCustomView.customTestFreq(testsPerFreq, freqTest1, freqTest2, freqTest3, freqTest4, freqTest5) as List<Int>

            // IF TESTBOOL THEN CALL ANOTHER METHOD WRT mCustomView.randomTestFreq()
            // SET THE TEST FREQUENCIES AND ADD THEM ONE BY ONE
            // SET THE TOTAL NUMBER OF TESTS
        }
        else {
            // RESTART STANDARD TEST
            // PROGRESSIVE NUMBER OF BANDS TRUE
            mCustomView.lowestFreq = 500f //62.5f
            mCustomView.highestFreq = 4000f //8000f
        }
        //score = 0
        gamesCount = 4 //5
        mCustomView.startNumberBands = 3
        mCustomView.init()
        //binding.nextButton.setText("Start", null)
        val nextButton = findViewById<Button>(com.example.pitchfinder.R.id.nextButton)
        nextButton.setText("Start", null)
        mCustomView.timbreTau = timbreInt*0.001f

    }*/

    @SuppressLint("SetTextI18n")
    private fun initUiElements() {

        diaryDialog = Dialog(this@PitchActivity2)

        val mCustomView = findViewById<CustomView>(R.id.customView)
        bands = mCustomView.startNumberBands
        //PdBase.sendMessage("bangInit", "bang")
        //PdBase.sendFloat("pan", 0f)
        //PdBase.sendMessage("pan", "bang")
        if (testBool) {
            randomFreq = testRandom[indexTest].toString()
            indexTest += 1
        } else {
            randomFreq = mCustomView.randomTestFreq()
        }
        mCustomView.doubleToneInit = 0

        val scoreTxt = findViewById<TextView>(R.id.scoreTxt)

        val resultView = findViewById<ResultView>(R.id.resultView)
        resultView.visibility = View.GONE

        val playTest = findViewById<Button>(R.id.playTest)
        playTest.setOnClickListener {
            //if (score != 0) {
            val currentTimeTestFreq: String = SimpleDateFormat(
                "HH:mm:ss.SSS",
                Locale.getDefault()
            ).format(Date())
            dataString?.append("Test frequency, $randomFreq, $currentTimeTestFreq\n")
            //Log.e("Test Frequency:", "$randomFreq $currentTimeTestFreq")
            // TODO CHECK GAIN
            val gainTotalTest = mCustomView.equalLoudness(
                randomFreq.toFloat(),
                9f*65f
            )
            PdBase.sendMessage("bangInit", "bang")
            PdBase.sendFloat("tauTest", timbreInt * 0.001f)
            // TIMBRE FROM INT TO PROPER RANGE
            PdBase.sendFloat("freqTest", randomFreq.toFloat())
            //PdBase.sendFloat("gainTotalTest", 90f)
            PdBase.sendFloat("gainTotalTest", gainTotalTest)
            PdBase.sendFloat("delayTime", 250f)
            PdBase.sendFloat("gainTotal", 0f)
            PdBase.sendMessage("bangMyTone", "bang")
            PdBase.sendMessage("bangTest", "bang")
            //Log.e("AmplitudeTest:", gainTotalTest.toString())
            //Log.e("freqTest:", randomFreq)

            //} else {
                //PdBase.sendFloat("gainTotalTest", 0f)
            //}
        }

        /*
        val playSound = findViewById<Button>(R.id.playSound)
        playSound.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val currentTimeFreqAmp: String = SimpleDateFormat("HH:mm:ss.SSS",
                    Locale.getDefault()).format(Date())
                PdBase.sendMessage("bangInit", "bang")
                PdBase.sendFloat("tau", mCustomView.timbreTau)
                PdBase.sendFloat("freq", mCustomView.stepFreq)
                PdBase.sendFloat("gainTotal",
                    mCustomView.equalLoudness(mCustomView.stepFreq, mCustomView.stepAmp))
                PdBase.sendMessage("bangMyTone", "bang")
                dataString?.append("Frequency," + mCustomView.stepFreq.toString() +
                        ", " + currentTimeFreqAmp + "\n")
                //dataString?.append("Amplitude, " + mCustomView.stepAmp.toString() +
                //        ", " + currentTimeFreqAmp + "\n")
                Log.i("Frequency:", mCustomView.stepFreq.toString() + " " +
                        currentTimeFreqAmp)
                //Log.i("Amplitude:", mCustomView.stepAmp.toString() + " " +
                //        currentTimeFreqAmp)
            }
        })
         */

        val nextButton = findViewById<Button>(R.id.nextButton)
        val progressBar = findViewById<ProgressBar>(R.id.determinateBar)
        nextButton.setOnClickListener {
            val currentTimeNextTestFreq: String = SimpleDateFormat("HH:mm:ss.SSS",
                Locale.getDefault()).format(Date())
            var nextString = nextButton.text
            //Log.e("Score", score.toString())
            if (nextString == "End") {
                // ANIMATION RIGHT-WRONG
                resultView.isCorrect = (abs(mCustomView.stepFreq.toDouble() - randomFreq.toDouble())
                        < epsilon)
                if (resultView.isCorrect) {
                    scoreTxt.text = (scoreTxt.text.toString().toInt() + 10).toString()
                }
                //Log.i("difference", abs(mCustomView.stepFreq.toDouble() - randomFreq.toDouble()).toBigDecimal().toPlainString())
                //Log.i("boolean difference", resultView.isCorrect.toString())
                progressBar.progress = 100
                resultView.visibility = View.VISIBLE
                mCustomView.visibility = View.GONE
                Handler(Looper.getMainLooper()).postDelayed({
                    //resultView.visibility = View.GONE
                    if (trialsCounter == 5) {
                        //showAlertDialogButtonClicked()
                        showDiaryDialog()
                    } else {startGameActivity()}}, 200)
                dataString?.append(mCustomView.dataStringView)
                //mCustomView.dataStringView.setLength(0)
                //mCustomView.startNumberBands = bands
                //mCustomView.mScaleFactor = 1f
                //mCustomView.mPosX = 0f
                //mCustomView.zoomingColors()
                //mCustomView.doubleToneInit = 0
            } else {
                //if (nextString == "Start") {
                //    nextButton.text = "Next"
                //    mCustomView.mScaleFactor = 1f
                //    mCustomView.mPosX = 0f
                //    mCustomView.zoomingColors()
                //    mCustomView.doubleToneInit = 1
                //}
                //if (nextString != "Start") {
                mCustomView.startNumberBands += 1
                mCustomView.mScaleFactor = 1f
                mCustomView.mPosX = 0f
                mCustomView.zoomingColors()
                mCustomView.doubleToneInit = 1
                if (score < 1) {
                    indexTest += 1
                }
                //mCustomView.lowestFreq = minFreq.toFloat()
                //mCustomView.highestFreq = maxFreq.toFloat()
                //testRandom = mCustomView.customTestFreq(testsPerFreq, freqTest1, freqTest2, freqTest3, freqTest4, freqTest5) as List<Int>
                //randomFreq = testRandom[indexTest].toString()
                //indexTest += 1
                //progressBar.progress = 0
                //mCustomView.doubleToneInit = 0
                //} else {
                    //Log.e("Score:", (score * 100 / gamesCount).toString())
                    //progressBar.progress = score * 100 / gamesCount
                //}
                //Log.e("Score:", (score * 100 / gamesCount).toString())
                //progressBar.progress = score * 100 / gamesCount
                // ANIMATION RIGHT-WRONG
                resultView.isCorrect = (abs(mCustomView.stepFreq.toDouble() - randomFreq.toDouble()) < epsilon)
                if (resultView.isCorrect) {
                    scoreTxt.text = (scoreTxt.text.toString().toInt() + 10).toString()
                }
                //Log.i("difference", abs(mCustomView.stepFreq.toDouble() - randomFreq.toDouble()).toBigDecimal().toPlainString())
                resultView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    resultView.visibility = View.GONE
                }, 1500)
                //}
                // TODO!
                randomFreq = mCustomView.randomTestFreq()
                //if (testBool) {
                //    gamesCount = testsPerFreq - 1
                //    randomFreq = testRandom[indexTest].toString()
                //    indexTest += 1
                //} else {
                //    randomFreq = mCustomView.randomTestFreq()
                //}
                score = ++score
                //Log.e("Score:", (score * 100 / (gamesCount + 1)).toString())
                progressBar.progress = score * 100 / (gamesCount + 1)
                dataString?.append(mCustomView.dataStringView)
                mCustomView.dataStringView.setLength(0)
                dataString?.append("Test frequency, $randomFreq, $currentTimeNextTestFreq\n")
                nextString = if (score in 1 until gamesCount) {"Next"} else {"End"}
                //if (score == 0) {
                //    //PdBase.sendFloat("freq0", 0.1f)
                //}
                nextButton.text = nextString
            }
        }


/*
        scoreTxt = findViewById(R.id.scoreTxt)
        val mCustomView = findViewById<CustomView>(R.id.customView)
        bands = mCustomView.startNumberBands
        mCustomView.randomTestFreq()
        val resultView = findViewById<ResultView>(R.id.resultView)
        resultView.visibility = View.GONE
        val scoreTxt = findViewById<TextView>(R.id.scoreTxt)
        val playTest = findViewById<Button>(R.id.playTest)
        playTest.setOnClickListener {
            if (score != 0) {
                val currentTimeTestFreq: String = SimpleDateFormat(
                    "HH:mm:ss.SSS",
                    Locale.getDefault()
                ).format(Date())
                dataString?.append("Test frequency, $randomFreq, $currentTimeTestFreq\n")
                Log.i("Test Frequency:", "$randomFreq $currentTimeTestFreq")
                val gainTotalTest = mCustomView.equalLoudness(
                    randomFreq.toFloat(),
                    mCustomView.stepAmp
                )
                PdBase.sendMessage("bangInit", "bang")
                PdBase.sendFloat("tauTest", timbreInt * 0.001f)
                // TIMBRE FROM INT TO PROPER RANGE
                PdBase.sendFloat("Log.i(\"RIGHT ANSWER\", String.valueOf(getRootView().getWidth()));", randomFreq.toFloat())
                //PdBase.sendFloat("gainTotalTest", 90f)
                PdBase.sendFloat("gainTotalTest", gainTotalTest)
                PdBase.sendMessage("bangTest", "bang")
            } else {
                //PdBase.sendFloat("gainTotalTest", 0f)
            }
        }
        val playSound = findViewById<Button>(R.id.playSound)
        playSound.setOnClickListener {
            val currentTimeFreqAmp: String = SimpleDateFormat(
                "HH:mm:ss.SSS",
                Locale.getDefault()
            ).format(Date())
            PdBase.sendMessage("bangInit", "bang")
            PdBase.sendFloat("tau", mCustomView.timbreTau)
            PdBase.sendFloat("freq", mCustomView.stepFreq)
            PdBase.sendFloat(
                "gainTotal",
                mCustomView.equalLoudness(mCustomView.stepFreq, mCustomView.stepAmp)
            )
            PdBase.sendMessage("bangMyTone", "bang")
            dataString?.append(
                "Frequency," + mCustomView.stepFreq.toString() +
                        ", " + currentTimeFreqAmp + "\n"
            )
            //dataString?.append("Amplitude, " + mCustomView.stepAmp.toString() +
            //        ", " + currentTimeFreqAmp + "\n")
            Log.i("Frequency:", mCustomView.stepFreq.toString() + " " + currentTimeFreqAmp)
            Log.i("Amplitude:", mCustomView.stepAmp.toString() + " " + currentTimeFreqAmp)
        }
        val nextButton = findViewById<Button>(R.id.nextButton)
        val progressBar = findViewById<ProgressBar>(R.id.determinateBar)
        nextButton.setOnClickListener {
            val currentTimeNextTestFreq: String = SimpleDateFormat(
                "HH:mm:ss.SSS",
                Locale.getDefault()
            ).format(Date())
            var nextString = nextButton.text
            Log.i("Score", score.toString())
            if (nextString == "End") {
                // ANIMATION RIGHT-WRONG
                resultView.isCorrect = (abs(mCustomView.stepFreq.toDouble() - randomFreq.toDouble()) < epsilon)
                scoreTxt.text = (scoreTxt.text.toString().toInt() + 1).toString()
                Log.i("difference", abs(mCustomView.stepFreq.toDouble() - randomFreq.toDouble()).toBigDecimal().toPlainString())
                Log.i("boolean difference", resultView.isCorrect.toString())
                progressBar.progress = 100
                resultView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    resultView.visibility = View.GONE
                    if (trialsCounter == 5) {
                        showAlertDialogButtonClicked()
                    } else {
                        startGameActivity()
                    }
                }, 200)
                dataString?.append(mCustomView.dataStringView)
                mCustomView.dataStringView.setLength(0)
                mCustomView.startNumberBands = bands
                mCustomView.mScaleFactor = 1f
                mCustomView.mPosX = 0f
                mCustomView.zoomingColors()
                mCustomView.doubleToneInit = 0
            } else {
                if (nextString == "Start") {
                    nextButton.text = "Next"
                    mCustomView.mScaleFactor = 1f
                    mCustomView.mPosX = 0f
                    mCustomView.zoomingColors()
                    mCustomView.doubleToneInit = 1
                }
                if (nextString != "Start") {
                    // ANIMATION RIGHT-WRONG
                    resultView.isCorrect = (abs(mCustomView.stepFreq.toDouble() - randomFreq.toDouble()) < epsilon)
                    scoreTxt.text = (scoreTxt.text.toString().toInt() + 1).toString()
                    Log.i("difference", abs(mCustomView.stepFreq.toDouble() - randomFreq.toDouble()).toBigDecimal().toPlainString())
                    resultView.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        resultView.visibility = View.GONE
                    }, 1500)
                    mCustomView.startNumberBands += 1
                    mCustomView.mScaleFactor = 1f
                    mCustomView.mPosX = 0f
                    mCustomView.zoomingColors()
                    if (score < 1) {
                        progressBar.progress = 0
                        mCustomView.doubleToneInit = 0
                    } else {
                        Log.i("Score:", (score * 100 / gamesCount).toString())
                        progressBar.progress = score * 100 / gamesCount
                    }
                }
                if (testBool) {
                    gamesCount = testsPerFreq * 3
                    randomFreq = testRandom[indexTest].toString()
                    indexTest += 1
                } else {
                    randomFreq = mCustomView.randomTestFreq()
                    score = ++score
                }
                dataString?.append(mCustomView.dataStringView)
                mCustomView.dataStringView.setLength(0)
                dataString?.append("Test frequency, $randomFreq, $currentTimeNextTestFreq\n")
                nextString = if (score in 1 until gamesCount) {
                    "Next"
                } else {
                    "End"
                }
                if (score == 0) {
                    //PdBase.sendFloat("freq0", 0.1f)
                }
                nextButton.text = nextString
            }
        }*/
    }

    @SuppressLint("SetTextI18n")
    private fun getValuesFromGameActivity() {
        val bundle = intent.extras
        if (bundle != null) {
            FINAL_SCORE_DRUM_VALUE_EXTRA = bundle.getString("FINAL_SCORE_DRUM_VALUE_EXTRA").toString()
            FINAL_SCORE_PITCH_VALUE_EXTRA = bundle.getString("FINAL_SCORE_PITCH_VALUE_EXTRA").toString()
            //val pitchScoreFromDrum = bundle.getString("FINAL_SCORE_PITCH_VALUE_EXTRA")?.toInt()
            //if (pitchScoreFromDrum != null) {
            //    FINAL_SCORE_PITCH_VALUE_EXTRA = pitchScoreFromDrum.toString()
            //}
            val scoreTxt = findViewById<TextView>(R.id.scoreTxt)
            scoreTxt.text = (
                    scoreTxt.text.toString().toInt() +
                    FINAL_SCORE_DRUM_VALUE_EXTRA.toInt()).toString()
            trialsCounter = bundle.getInt("trials")
            dataString = EventBus.getDefault().removeStickyEvent(SaveStringBuilderEvent::class.java).sb
        }
        //Log.e("FINAL_DRUM FROM DRUM", FINAL_SCORE_DRUM_VALUE_EXTRA)
        //Log.e("FINAL_PITCH FROM DRUM", FINAL_SCORE_PITCH_VALUE_EXTRA)
    }

    private fun startGameActivity() {
        val intent = Intent(this@PitchActivity2, GameActivity::class.java)
        EventBus.getDefault().postSticky(SaveStringBuilderEvent(dataString))
        //Log.e("dataString FROM PITCH", dataString.toString())
        //Create the bundle
        //val bundle = Bundle()
        //Add your data to bundle
        //bundle.putInt("trials", trialsCounter)
        //bundle.putString("FINAL_SCORE_DRUM_VALUE_EXTRA", FINAL_SCORE_DRUM_VALUE_EXTRA)
        //bundle.putString("FINAL_SCORE_PITCH_VALUE_EXTRA", scoreTxt!!.text.toString())
        intent.putExtra("trials", trialsCounter)
        intent.putExtra("FINAL_SCORE_DRUM_VALUE_EXTRA", FINAL_SCORE_DRUM_VALUE_EXTRA)
        val scoreTxt = findViewById<TextView>(R.id.scoreTxt)
        intent.putExtra("FINAL_SCORE_PITCH_VALUE_EXTRA",scoreTxt.text.toString())
        //Add the bundle to the intent
        //intent.putExtras(bundle)
        this@PitchActivity2.startActivity(intent)
    }

    private fun startResultActivity(score: String) {
        val intent = Intent(this@PitchActivity2, ResultActivity::class.java)
        intent.putExtra("FINAL_SCORE_VALUE_EXTRA", score)
        this@PitchActivity2.startActivity(intent)
    }


    @SuppressLint("QueryPermissionsNeeded")
    fun export() {
        try {
            //saving the file into device
            val out: FileOutputStream = openFileOutput("data.csv", Context.MODE_PRIVATE)
            out.write(dataString.toString().toByteArray())
            out.close()
            val currentTimeFileSaved: String = SimpleDateFormat(
                "HH:mm:ss.SSS",
                Locale.getDefault()
            ).format(Date())
            var timeString = currentTimeFileSaved.toString();
            timeString = timeString.replace(".", "_").replace(":", "_")
            val context: Context = applicationContext
            val fileLocation = File(filesDir, timeString + "_data.csv")
            val textSubmit = diaryDialog?.findViewById<TextView>(R.id.submitText)
            textSubmit?.text = "Fatto" //fileLocation.toString()
            val path: Uri = FileProvider.getUriForFile(context,  "com.example.pitchfinder.provider", fileLocation)
            val fileIntent = Intent(Intent.ACTION_SEND)
            val chooser = Intent.createChooser(fileIntent, "Share File")
            val resInfoList =
                this.packageManager.queryIntentActivities(fileIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                this.grantUriPermission(packageName, path,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            fileIntent.type = "text/csv"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            startActivity(fileIntent)
            // TODO GET NOTIFICATION
            //if () {
            //    sharingIsActive = true}

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        val startGameCountDownTimer = findViewById<PulseCountDown>(R.id.pulseCountDown)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        startGameCountDownTimer!!.start {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        PdAudio.startAudio(this)
    }

    override fun onPause() {
        super.onPause()
        PdAudio.stopAudio()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pitch2)

        // Disable landscape mode
        this@PitchActivity2.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply()
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, true)

        PdAudio.startAudio(this)

        initUiElements()

        getValuesFromGameActivity()

        trialsCounter++
        //Log.e("trials", trialsCounter.toString())

        val startGameCountDownTimer = findViewById<PulseCountDown>(R.id.pulseCountDown)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        startGameCountDownTimer!!.start {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}