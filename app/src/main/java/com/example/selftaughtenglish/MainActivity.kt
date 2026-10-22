package com.example.selftaughtenglish

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.selftaughtenglish.audio.PracticeAudioRecorder
import com.example.selftaughtenglish.learning.DailyLessonProvider
import com.example.selftaughtenglish.learning.PracticeSection
import com.example.selftaughtenglish.learning.PracticeSession
import com.example.selftaughtenglish.learning.PracticeType
import com.example.selftaughtenglish.learning.ProgressStore
import com.example.selftaughtenglish.learning.PronunciationPrompt
import com.example.selftaughtenglish.learning.RecordingControlsState
import com.example.selftaughtenglish.learning.SpeechFeedback
import com.example.selftaughtenglish.learning.analyzeSpeech
import com.example.selftaughtenglish.learning.countWords
import com.example.selftaughtenglish.ui.WaveformBars
import com.example.selftaughtenglish.ui.theme.SelfTaughtEnglishTheme
import java.util.Locale
import kotlinx.coroutines.delay

private const val IDLE_RECORDING_HINT = "录音后回放，对比自己和示范音的差别。"
private const val MAX_AMPLITUDE_SAMPLES = 240
private const val AMPLITUDE_POLL_MILLIS = 120L

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var textToSpeech: TextToSpeech? = null
    private lateinit var audioRecorder: PracticeAudioRecorder
    private val lessonProvider = DailyLessonProvider()
    private val progressStore by lazy { ProgressStore(this) }
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioRecorder = PracticeAudioRecorder(this)
        textToSpeech = TextToSpeech(this, this)
        enableEdgeToEdge()
        setContent {
            SelfTaughtEnglishTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DailyLessonScreen(
                        lessonProvider = lessonProvider,
                        progressStore = progressStore,
                        onSpeak = ::speak,
                        hasRecordAudioPermission = ::hasRecordAudioPermission,
                        onStartRecording = audioRecorder::startRecording,
                        onStopRecording = audioRecorder::stopRecording,
                        onPlayRecording = audioRecorder::playRecording,
                        onSampleAmplitude = audioRecorder::currentAmplitude,
                        onLastRecordingDuration = audioRecorder::lastRecordingDurationMs,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.US
            textToSpeech?.setSpeechRate(0.75f)
            ttsReady = true
        }
    }

    private fun speak(text: String) {
        if (ttsReady) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "daily-practice")
        }
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        audioRecorder.release()
        super.onDestroy()
    }

    private fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun DailyLessonScreen(
    lessonProvider: DailyLessonProvider,
    progressStore: ProgressStore,
    onSpeak: (String) -> Unit,
    hasRecordAudioPermission: () -> Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Boolean,
    onPlayRecording: () -> Unit,
    onSampleAmplitude: () -> Int,
    onLastRecordingDuration: () -> Long,
    modifier: Modifier = Modifier,
) {
    var selectedDay by remember { mutableStateOf(1) }
    var session by remember { mutableStateOf<PracticeSession?>(null) }
    var finishedDay by remember { mutableStateOf<Int?>(null) }
    var completedDays by remember { mutableStateOf(progressStore.completedDays()) }
    var streak by remember { mutableStateOf(progressStore.streak()) }
    var reviewFlags by remember { mutableStateOf(progressStore.reviewFlags()) }
    var recordingState by remember { mutableStateOf(RecordingControlsState()) }
    var recordingHint by remember { mutableStateOf(IDLE_RECORDING_HINT) }
    var amplitudes by remember { mutableStateOf<List<Int>>(emptyList()) }
    var feedback by remember { mutableStateOf<SpeechFeedback?>(null) }
    var pendingStartAfterPermission by remember { mutableStateOf(false) }

    val lesson = remember(selectedDay) { lessonProvider.lessonForDay(selectedDay) }
    val currentLessonDay = selectedDay
    val isCurrentDayCompleted = completedDays.contains(currentLessonDay)
    val isReviewMarked = reviewFlags.contains(ProgressStore.reviewKey(currentLessonDay))

    // 录音期间按固定间隔采样振幅，用于绘制波形；录音结束后自动停止。
    LaunchedEffect(recordingState.isRecording) {
        if (recordingState.isRecording) {
            amplitudes = emptyList()
            feedback = null
            while (true) {
                val sample = onSampleAmplitude()
                amplitudes = (amplitudes + sample).takeLast(MAX_AMPLITUDE_SAMPLES)
                delay(AMPLITUDE_POLL_MILLIS)
            }
        }
    }

    val resetRecording: () -> Unit = {
        recordingState = RecordingControlsState()
        recordingHint = IDLE_RECORDING_HINT
        amplitudes = emptyList()
        feedback = null
    }

    val changeDay: (Int) -> Unit = { target ->
        val clamped = target.coerceIn(1, lessonProvider.totalDays)
        if (clamped != selectedDay) {
            selectedDay = clamped
            session = null
            resetRecording()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted && pendingStartAfterPermission) {
            onStartRecording()
            recordingState = RecordingControlsState(isRecording = true)
            recordingHint = "正在录音：读出上面的单词，读完点停止。"
        } else if (!granted) {
            recordingHint = "需要允许麦克风权限，才能录下你的发音。"
        }
        pendingStartAfterPermission = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "自学英语",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "已完成 ${completedDays.size} 天 · 连续 $streak 天",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { changeDay(selectedDay - 1) },
                enabled = selectedDay > 1,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("上一天")
            }
            Text(
                text = "第 $selectedDay / ${lessonProvider.totalDays} 天",
                style = MaterialTheme.typography.titleSmall,
            )
            Button(
                onClick = { changeDay(selectedDay + 1) },
                enabled = selectedDay < lessonProvider.totalDays,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("下一天")
            }
        }

        Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "${lesson.totalMinutes} 分钟 · 重点：${lesson.focusSounds.joinToString(" · ")}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (isCurrentDayCompleted) {
            Text(
                text = "这一天已完成，可以再练一遍",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        lesson.sections.forEachIndexed { index, section ->
            PracticeSectionCard(
                section = section,
                index = index,
                isCurrent = session?.sectionIndex == index,
            )
        }

        val activeSession = session
        if (activeSession != null) {
            PracticePanel(
                session = activeSession,
                recordingState = recordingState,
                recordingHint = recordingHint,
                amplitudes = amplitudes,
                feedback = feedback,
                isReviewMarked = isReviewMarked,
                onSpeak = onSpeak,
                onRecordClick = {
                    if (recordingState.isRecording) {
                        val saved = onStopRecording()
                        recordingState = RecordingControlsState(hasRecording = saved)
                        if (saved) {
                            val prompt = PronunciationPrompt.from(activeSession.currentSection)
                            feedback = analyzeSpeech(
                                durationMs = onLastRecordingDuration(),
                                amplitudes = amplitudes,
                                wordCount = countWords(prompt.textToSpeak),
                            )
                            recordingHint = "录好了。下面是这次录音的客观数据。"
                        } else {
                            recordingHint = "这次录音太短，没有保存成功。再试一次。"
                        }
                    } else if (hasRecordAudioPermission()) {
                        onStartRecording()
                        recordingState = RecordingControlsState(isRecording = true)
                        recordingHint = "正在录音：读出上面的单词，读完点停止。"
                    } else {
                        pendingStartAfterPermission = true
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onPlayRecording = onPlayRecording,
                onPrevious = {
                    session = activeSession.previous()
                    resetRecording()
                },
                onNext = {
                    session = activeSession.next()
                    resetRecording()
                },
                onFinish = {
                    session = null
                    finishedDay = selectedDay
                    streak = progressStore.markDayCompleted(selectedDay)
                    completedDays = progressStore.completedDays()
                    resetRecording()
                },
                onToggleReview = {
                    progressStore.toggleReview(currentLessonDay)
                    reviewFlags = progressStore.reviewFlags()
                },
            )
        } else if (finishedDay == selectedDay) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "第 $selectedDay 天练习已完成",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "四个环节都走完了。进度已保存，关掉 App 也不会丢。",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "累计完成 ${completedDays.size} 天 · 连续 $streak 天",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { session = PracticeSession.start(lesson) },
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("再练一遍")
                        }
                        Button(
                            onClick = { changeDay(selectedDay + 1) },
                            enabled = selectedDay < lessonProvider.totalDays,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("下一天")
                        }
                    }
                }
            }
        } else {
            Button(
                onClick = { session = PracticeSession.start(lesson) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("开始今日练习")
            }
        }
    }
}

@Composable
private fun PracticePanel(
    session: PracticeSession,
    recordingState: RecordingControlsState,
    recordingHint: String,
    amplitudes: List<Int>,
    feedback: SpeechFeedback?,
    isReviewMarked: Boolean,
    onSpeak: (String) -> Unit,
    onRecordClick: () -> Unit,
    onPlayRecording: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
    onToggleReview: () -> Unit,
) {
    val prompt = PronunciationPrompt.from(session.currentSection)
    val section = session.currentSection

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = session.statusText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = session.sectionProgressText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = section.description,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = prompt.displayText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Button(
                onClick = { onSpeak(prompt.textToSpeak) },
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("播放示范音")
            }
            Text(
                text = recordingHint,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onRecordClick,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(recordingState.recordButtonText)
                }
                Button(
                    onClick = onPlayRecording,
                    enabled = recordingState.canPlayRecording,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(recordingState.playbackButtonText)
                }
            }

            if (recordingState.isRecording || amplitudes.isNotEmpty()) {
                WaveformBars(amplitudes = amplitudes)
            }

            feedback?.let { result ->
                Text(
                    text = "${result.durationText} · ${result.paceText}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = result.volumeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            if (section.type == PracticeType.Review) {
                Button(
                    onClick = onToggleReview,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(if (isReviewMarked) "已标记需要复习" else "标记今天需要复习")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onPrevious,
                    enabled = !session.isFirstSection,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("上一节")
                }
                Button(
                    onClick = if (session.isLastSection) onFinish else onNext,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(if (session.isLastSection) "完成今日练习" else "下一节")
                }
            }
        }
    }
}

@Composable
private fun PracticeSectionCard(
    section: PracticeSection,
    index: Int,
    isCurrent: Boolean,
) {
    val containerColor = if (isCurrent) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${index + 1}. ${section.minutes} min · ${section.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = section.description,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = section.examples.joinToString("  |  "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyLessonPreview() {
    val context = LocalContext.current
    val previewStore = remember(context) { ProgressStore(context) }
    SelfTaughtEnglishTheme {
        DailyLessonScreen(
            lessonProvider = DailyLessonProvider(),
            progressStore = previewStore,
            onSpeak = {},
            hasRecordAudioPermission = { true },
            onStartRecording = {},
            onStopRecording = { true },
            onPlayRecording = {},
            onSampleAmplitude = { 0 },
            onLastRecordingDuration = { 0L },
        )
    }
}
