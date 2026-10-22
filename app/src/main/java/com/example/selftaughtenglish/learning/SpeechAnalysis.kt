package com.example.selftaughtenglish.learning

import java.util.Locale

/**
 * 录音的客观反馈。
 *
 * 这里不做发音打分 —— 没有音素级别的比对就不该给分。只给出两个可以验证的事实：
 * 录音时长与示范文本的节奏差异、录音音量是否正常。
 */
data class SpeechFeedback(
    val durationText: String,
    val paceText: String,
    val volumeText: String,
)

private const val MIN_BAR_HEIGHT = 0.06f

/** 示范音语速固定为 0.75x，实测每个词约 0.55 秒。 */
private const val MILLIS_PER_WORD = 550L

fun analyzeSpeech(
    durationMs: Long,
    amplitudes: List<Int>,
    wordCount: Int,
): SpeechFeedback {
    val safeWords = wordCount.coerceAtLeast(1)
    val expectedMs = (safeWords * MILLIS_PER_WORD).coerceAtLeast(600L)
    val ratio = durationMs.toFloat() / expectedMs

    val paceText = when {
        ratio < 0.6f -> "比示范快很多，试着把每个词读完整"
        ratio < 0.85f -> "比示范略快，节奏可以再放慢一点"
        ratio <= 1.3f -> "节奏接近示范"
        ratio <= 1.7f -> "比示范略慢，词与词之间别拖太长"
        else -> "比示范慢很多，先连贯读完再追求准确"
    }

    val peak = amplitudes.maxOrNull() ?: 0
    val volumeText = when {
        amplitudes.isEmpty() -> "没有采集到音量数据"
        peak < 1500 -> "音量偏小，靠近麦克风再试一次"
        peak > 30000 -> "音量过大，离麦克风远一点"
        else -> "音量正常"
    }

    val seconds = durationMs / 1000f
    val durationText = String.format(Locale.US, "%.1f 秒", seconds)

    return SpeechFeedback(
        durationText = durationText,
        paceText = paceText,
        volumeText = volumeText,
    )
}

/** 从示范文本里数出要读的词数，标点与分隔符都算切分。 */
fun countWords(text: String): Int {
    return text
        .split(' ', '.', ',', '|', '/', '-')
        .count { it.isNotBlank() }
}

/**
 * 把任意长度的振幅采样压成固定数量的柱子。
 *
 * 采样点多于柱子时按区间取平均，少于柱子时直接归一化，短录音也不会画成一根。
 */
fun normalizeAmplitudes(samples: List<Int>, buckets: Int): List<Float> {
    if (buckets <= 0) return emptyList()
    if (samples.isEmpty()) return List(buckets) { MIN_BAR_HEIGHT }

    val peak = samples.max().coerceAtLeast(1)

    if (samples.size <= buckets) {
        return samples.map { (it.toFloat() / peak).coerceAtLeast(MIN_BAR_HEIGHT) }
    }

    return (0 until buckets).map { index ->
        val from = index * samples.size / buckets
        val to = ((index + 1) * samples.size / buckets).coerceIn(from + 1, samples.size)
        val average = samples.subList(from, to).average()
        (average / peak).toFloat().coerceAtLeast(MIN_BAR_HEIGHT)
    }
}
