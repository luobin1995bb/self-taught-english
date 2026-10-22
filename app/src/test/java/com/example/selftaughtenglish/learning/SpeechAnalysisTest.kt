package com.example.selftaughtenglish.learning

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpeechAnalysisTest {

    @Test
    fun countWordsSplitsOnPunctuationAndSeparators() {
        assertEquals(6, countWords("sheep. ship. seat. sit. leave. live."))
        assertEquals(2, countWords("eee - ih"))
        assertEquals(1, countWords("ooo"))
    }

    @Test
    fun normalizeAmplitudesReturnsRequestedBucketCountWhenSamplesArePlenty() {
        val samples = listOf(100, 200, 400, 800, 1600, 3200, 2400, 1200)

        val levels = normalizeAmplitudes(samples, buckets = 4)

        assertEquals(4, levels.size)
        assertTrue(levels.all { it in 0f..1f })
    }

    @Test
    fun normalizeAmplitudesKeepsOneBarPerSampleWhenSamplesAreFew() {
        val levels = normalizeAmplitudes(listOf(100, 200, 400), buckets = 8)

        assertEquals(3, levels.size)
        assertTrue(levels.all { it in 0f..1f })
    }

    @Test
    fun normalizeAmplitudesPreservesLoudnessOrder() {
        val levels = normalizeAmplitudes(listOf(100, 200, 400), buckets = 3)

        assertTrue(levels[0] < levels[1])
        assertTrue(levels[1] < levels[2])
    }

    @Test
    fun normalizeAmplitudesNeverReturnsZeroHeightBars() {
        val levels = normalizeAmplitudes(listOf(0, 0, 0), buckets = 2)

        assertEquals(2, levels.size)
        assertTrue(levels.all { it > 0f })
    }

    @Test
    fun analyzeSpeechFlagsFastPace() {
        val feedback = analyzeSpeech(
            durationMs = 600,
            amplitudes = listOf(9000, 12_000, 8000),
            wordCount = 6,
        )

        assertTrue(feedback.paceText.contains("快"))
        assertEquals("0.6 秒", feedback.durationText)
    }

    @Test
    fun analyzeSpeechFlagsSlowPace() {
        val feedback = analyzeSpeech(
            durationMs = 12_000,
            amplitudes = listOf(9000, 12_000, 8000),
            wordCount = 6,
        )

        assertTrue(feedback.paceText.contains("慢"))
    }

    @Test
    fun analyzeSpeechAcceptsClosePace() {
        // 6 个词按每词 0.55 秒估算，目标约 3.3 秒
        val feedback = analyzeSpeech(
            durationMs = 3_300,
            amplitudes = listOf(9000, 12_000, 8000),
            wordCount = 6,
        )

        assertTrue(feedback.paceText.contains("接近"))
        assertTrue(feedback.volumeText.contains("正常"))
    }

    @Test
    fun analyzeSpeechWarnsAboutQuietRecording() {
        val feedback = analyzeSpeech(
            durationMs = 3_300,
            amplitudes = listOf(100, 200, 150),
            wordCount = 6,
        )

        assertTrue(feedback.volumeText.contains("音量偏小"))
    }

    @Test
    fun analyzeSpeechHandlesMissingSamples() {
        val feedback = analyzeSpeech(durationMs = 3_300, amplitudes = emptyList(), wordCount = 6)

        assertTrue(feedback.volumeText.contains("没有采集到"))
    }
}
