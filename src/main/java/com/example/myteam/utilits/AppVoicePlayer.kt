package com.example.myteam.utilits

import android.media.MediaPlayer
import com.example.myteam.database.REF_STORAGE_ROOT
import com.example.myteam.database.getFileFromStorage
import java.io.File
import java.lang.Exception

class AppVoicePlayer {

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mFile: File

    fun play(messageKey: String, fileUrl: String, function: () -> Unit) {
        mFile = File(APP_ACTIVITY.filesDir, messageKey)
        //Если существует и длина > 0 и целостность
        if (mFile.exists() && mFile.length() > 0 && mFile.isFile) {
            startPlay {
                function()
            }
        } else {
            mFile.createNewFile()
            getFileFromStorage(mFile, fileUrl) {
                startPlay {
                    function()
                }
            }
        }
    }


    private fun startPlay(function: () -> Unit) {
        try {
            mMediaPlayer.setDataSource(mFile.absolutePath)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
            //Теперь нужно получить значение, когда музыка до конца проигралась
            mMediaPlayer.setOnCompletionListener {
                stop {
                    function()
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    fun stop(function: () -> Unit) {
        try {
            mMediaPlayer.stop()
            mMediaPlayer.reset()    //Сбросил
            function()
        } catch (e: Exception) {
            showToast(e.message.toString())
            function()
        }
    }

    //Просто удаляем
    fun release() {
        mMediaPlayer.release()
    }

    fun init() {
        mMediaPlayer = MediaPlayer()
    }
}