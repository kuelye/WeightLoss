package com.levkorol.weightloss.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.levkorol.weightloss.R
import com.levkorol.weightloss.model.SongInfo
import com.levkorol.weightloss.util.dp
import com.levkorol.weightloss.util.getSongInfo
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


// CTRL + ALT + L
// SHIFT + F6

// 1. переменные: константы и обычные переменные
// 2. методы: public override > public > private

// ProgressBar: progress и max

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MY_PERMISSIONS_REQUEST = 1
        private const val REQUEST_CODE = 2
    }

    private var mp: MediaPlayer? = null
    private var totalTime: Int = 0

    private var originalUris: List<Uri>? = null
    private var uris: List<Uri>? = null
    private var songIndex: Int = -1

    private var shuffleMode: Boolean = false
    // TODO 26.02 #5 флаг для репита

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what

            //Update positionBar
            positionBar.progress = currentPosition

            //Update Labeles
            val elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = elapsedTime

            val remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.levkorol.weightloss.R.layout.activity_main)
        //Volume Bar
        volumeBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        var volumeNum = progress / 100.0f
                        mp?.setVolume(volumeNum, volumeNum)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )

        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mp?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )

        Thread(Runnable {
            while (true) {
                try {
                    if (mp != null) {
                        val msg = Message()
                        msg.what = mp?.currentPosition ?: 0
                        handler.sendMessage(msg)
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()
    }


    //zaprashivaet razrreshenie na chtenie faylov
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestAudioFiles()
                } else {
                    Toast.makeText(
                        this, "razreshite prilozheniu chtenie audio s ustroystva",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    val uris = mutableListOf<Uri>()
                    if (intent?.data != null) {
                        val uri = intent?.data
                        play(uri)
                    } else {
                        val clipData = intent?.clipData
                        if (clipData != null) {
                            for (i in 0 until clipData.itemCount) {
                                uris.add(clipData.getItemAt(i).uri)
                            }
                        }
                        originalUris = uris
                        play(uris)
                    }
                }

            }
            else -> {

            }
        }
    }

    fun playBtnOnClick(v: View) {
        if (mp?.isPlaying == true) {
            //Stop
            mp?.pause()
            playBtn.setBackgroundResource(com.levkorol.weightloss.R.drawable.playbutton)
        } else {
            //Start
            mp?.start()
            playBtn.setBackgroundResource(com.levkorol.weightloss.R.drawable.pause)
        }
    }

    fun addSongs(v: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST
            )
        } else {
            requestAudioFiles()
        }
    }

    fun goToMenu(v: View) {
        val intent = Intent(this@MainActivity, LoginPasswordActivity::class.java)
        val songInfos: ArrayList<SongInfo> = arrayListOf()
        intent.putExtra("as", songInfos)
        startActivity(intent)
    }

    fun listSongs(v: View) {
        if (songsLayout.translationY == 0f) {
            songsLayout.translationY = -dp(100).toFloat()
        } else {
            songsLayout.translationY = 0f
        }
        weightLossModeLayout.translationY = songsLayout.translationY + dp(50)
//        val intent = Intent(this@MainActivity, SongsActivity::class.java)
//        val list = arrayListOf<Uri>()
//        if (uris != null) list.addAll(uris!!)
//        intent.putExtra("as", list)
//        startActivity(intent)
    }
    
    private fun play(uris: List<Uri>?) {
        mp?.release()
        mp = null
        this.uris = uris
        if (uris != null) {
            songIndex = 0
            play(uris[songIndex])
        }
    }

    fun playNextBtnOnClick(view: View) {
        if (uris != null) {
            songIndex++
            if (songIndex >= uris!!.size) songIndex = 0
            mp?.stop()
            play(uris!![songIndex])
        }
    }


    fun playPrevBtnOnClick(view: View) {
        if (songIndex == 0) songIndex++
        if (uris != null) {
            songIndex--
            if (songIndex >= uris!!.size) songIndex.minus(1)
            mp?.stop()
            play(uris!![songIndex])
        }
    }

    // TODO 26.02 #4c меняем фон у кнопки
    fun shufleBtnOnClick(view: View) {
        mp?.stop()
        if (shuffleMode) {
            shuffleMode = false
            play(originalUris)
        } else {
            shuffleMode = true
            play(uris?.shuffled())
        }
    }

    // TODO 26.02 #5 по аналогии с shuffle только тут просто меняем флаг и меняем фон у кнопки
    fun repeatBtnOnClick(view: View) {

    }

    fun shareBtnOnClick(view: View) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, "ali")
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, "send to"))
    }


    private fun play(uri: Uri) {
        mp = MediaPlayer().apply {
            setDataSource(applicationContext, uri)
            //isLooping = true
            setVolume(0.5f, 0.5f)
            try {
                prepareAsync()
            } catch (e: Exception) {
            }
        }

        mp?.setOnPreparedListener { mp ->
            mp.start()
            totalTime = mp.duration
            positionBar.max = totalTime
        }

        mp?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            // TODO 26.02 #5 если репит включён то не нужно переходить к следующей
            songIndex++
            if (songIndex >= uris!!.size) songIndex = 0
            mp?.stop()
            play(uris!![songIndex])
        })

        val songInfo = getSongInfo(this, uri)
        if (songInfo == null) {
            Toast.makeText(
                this, "ne udalos zagruzit pesn",
                Toast.LENGTH_LONG
            ).show()
        } else {
            titleSongTextView.text = songInfo.title
            titleArtistTextView.text = songInfo.artist
            if (songInfo.albumBitmap == null) {
                albumImageView.setImageResource(R.drawable.photoalbum)
            } else {
                albumImageView.setImageBitmap(songInfo.albumBitmap)
            }
        }
    }

    private fun createTimeLabel(time: Int): String {
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        return "${"%02d".format(min)}:${"%02d".format(sec)}"
    }

    private fun requestAudioFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            intent,
            REQUEST_CODE
        )
    }

}


