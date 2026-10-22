package com.example.selftaughtenglish.learning

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordingControlsTest {
    @Test
    fun initialStatePromptsUserToRecord() {
        val state = RecordingControlsState()

        assertEquals("开始录音", state.recordButtonText)
        assertEquals("还没有录音", state.playbackButtonText)
        assertFalse(state.canPlayRecording)
    }

    @Test
    fun finishedRecordingCanBePlayedBack() {
        val state = RecordingControlsState(
            isRecording = false,
            hasRecording = true,
        )

        assertEquals("重新录音", state.recordButtonText)
        assertEquals("播放我的录音", state.playbackButtonText)
        assertTrue(state.canPlayRecording)
    }
}
