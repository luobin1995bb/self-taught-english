package com.example.selftaughtenglish.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class PracticeAudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var recordingStartedAt: Long = 0L
    private var lastDurationMs: Long = 0L
    private val outputFile: File = File(context.cacheDir, "latest-pronunciation-practice.m4a")

    fun startRecording() {
        stopPlayback()
        outputFile.delete()
        recordingStartedAt = System.currentTimeMillis()
        lastDurationMs = 0L

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
        lastDurationMs = currentRecordingMillis()
        recordingStartedAt = 0L
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
        recordingStartedAt = 0L
        stopPlayback()
    }

    /** 录音过程中轮询当前振幅（0..32767），未在录音时返回 0。 */
    fun currentAmplitude(): Int {
        return runCatching { recorder?.maxAmplitude ?: 0 }.getOrDefault(0)
    }

    /** 上一次录音的时长，毫秒。 */
    fun lastRecordingDurationMs(): Long = lastDurationMs

    private fun currentRecordingMillis(): Long {
        if (recordingStartedAt == 0L) return 0L
        return System.currentTimeMillis() - recordingStartedAt
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
