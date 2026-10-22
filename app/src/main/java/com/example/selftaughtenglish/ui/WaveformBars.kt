package com.example.selftaughtenglish.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.selftaughtenglish.learning.normalizeAmplitudes

/**
 * 录音的振幅波形。
 *
 * 刻意不用 Canvas 绘制，而是用一组等宽 Box 拼出来 —— 逻辑更简单，也不依赖绘图 API。
 */
@Composable
fun WaveformBars(
    amplitudes: List<Int>,
    modifier: Modifier = Modifier,
    barCount: Int = 40,
) {
    val levels = remember(amplitudes, barCount) {
        normalizeAmplitudes(amplitudes, barCount)
    }
    val barColor = MaterialTheme.colorScheme.primary
    val barShape = RoundedCornerShape(2.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        levels.forEach { level ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height((6 + level * 46).dp)
                    .background(color = barColor, shape = barShape),
            )
        }
    }
}
