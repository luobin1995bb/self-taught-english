package com.example.selftaughtenglish.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class PracticeAudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val outputFile: File = File(context.cacheDir, "latest-pronunciation-practice.m4a")

    fun startRecording() {
        stopPlayback()
        outputFile.delete()

        recorder = newRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44_100)
            setAudioEncodingBitRate(96_000)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording(): Boolean {
        val activeRecorder = recorder ?: return outputFile.exists()
        return runCatching {
            activeRecorder.stop()
            activeRecorder.release()
            recorder = null
            outputFile.exists() && outputFile.length() > 0
        }.getOrElse {
            activeRecorder.release()
            recorder = null
            outputFile.delete()
            false
        }
    }

    fun playRecording() {
        if (!outputFile.exists()) return

        stopPlayback()
        player = MediaPlayer().apply {
            setDataSource(outputFile.absolutePath)
            setOnCompletionListener { stopPlayback() }
            prepare()
            start()
        }
    }

    fun release() {
        runCatching { recorder?.release() }
        recorder = null
        stopPlayback()
    }

    private fun stopPlayback() {
        runCatching {
            player?.stop()
            player?.release()
        }
        player = null
    }

    private fun newRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }
}
