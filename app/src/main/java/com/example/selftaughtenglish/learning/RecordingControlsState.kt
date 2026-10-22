package com.example.selftaughtenglish.learning

data class RecordingControlsState(
    val isRecording: Boolean = false,
    val hasRecording: Boolean = false,
) {
    val recordButtonText: String
        get() = when {
            isRecording -> "停止录音"
            hasRecording -> "重新录音"
            else -> "开始录音"
        }

    val playbackButtonText: String
        get() = if (hasRecording) "播放我的录音" else "还没有录音"

    val canPlayRecording: Boolean
        get() = hasRecording && !isRecording
}
