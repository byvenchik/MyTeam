package com.example.myteam.utilits

import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AppVoiceRecorder {

    private val mMediaRecorder = MediaRecorder()

    //Файл для хранения данных
    private lateinit var mFile: File
    private lateinit var mMessageKey: String

    fun startRecord(messageKey: String) {
        try {
            mMessageKey = messageKey
            createFileForRecord()
            prepareMediaRecorder()
            mMediaRecorder.start()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }

    }

    private fun prepareMediaRecorder() {    //Будет подготавливать медиа рекордер к записи
        mMediaRecorder.apply {
            reset()  //Сбросил старый
            //От куда получать данные
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            //Как можно кодировать этот файл
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            //Нужно указать где будем хранить файл
            setOutputFile(mFile.absolutePath)
            prepare()    //Подготовка
        }

    }

    private fun createFileForRecord() {     //Создание файла
        mFile = File(APP_ACTIVITY.filesDir, mMessageKey)
        mFile.createNewFile()
    }

    fun stopRecord(onSuccess: (file: File, messageKey: String) -> Unit) {
        try {
            mMediaRecorder.stop()
            onSuccess(mFile, mMessageKey) //Готовое сообщение
        } catch (e: Exception) {
            showToast(e.message.toString())
            mFile.delete()  //Если не закончился правильно, то он не нужен
        }
    }

    //Будем удалять экземпляр из памяти
    fun releaseRecorder() {
        try {
            mMediaRecorder.release()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }
}

