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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.selftaughtenglish.audio.PracticeAudioRecorder
import com.example.selftaughtenglish.learning.DailyLesson
import com.example.selftaughtenglish.learning.DailyLessonProvider
import com.example.selftaughtenglish.learning.PracticeSession
import com.example.selftaughtenglish.learning.PracticeSection
import com.example.selftaughtenglish.learning.PronunciationPrompt
import com.example.selftaughtenglish.learning.RecordingControlsState
import com.example.selftaughtenglish.ui.theme.SelfTaughtEnglishTheme
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var textToSpeech: TextToSpeech? = null
    private lateinit var audioRecorder: PracticeAudioRecorder
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
                        lesson = DailyLessonProvider().lessonForDay(1),
                        onSpeak = ::speak,
                        hasRecordAudioPermission = ::hasRecordAudioPermission,
                        onStartRecording = audioRecorder::startRecording,
                        onStopRecording = audioRecorder::stopRecording,
                        onPlayRecording = audioRecorder::playRecording,
                        modifier = Modifier.padding(innerPadding)
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
    lesson: DailyLesson,
    onSpeak: (String) -> Unit,
    hasRecordAudioPermission: () -> Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Boolean,
    onPlayRecording: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var session by remember { mutableStateOf<PracticeSession?>(null) }
    var recordingState by remember { mutableStateOf(RecordingControlsState()) }
    var recordingHint by remember { mutableStateOf("录音后回放，对比自己和示范音的差别。") }
    var pendingStartAfterPermission by remember { mutableStateOf(false) }
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
            text = "今日练习：${lesson.title}",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "${lesson.totalMinutes} 分钟 · 重点 ${lesson.focusSounds.joinToString(" 对比 ")}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        lesson.sections.forEach { section ->
            PracticeSectionCard(section = section)
        }

        session?.let { activeSession ->
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
                        text = activeSession.statusText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = activeSession.currentSection.description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    val prompt = PronunciationPrompt.from(activeSession.currentSection)
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
                            onClick = {
                                if (recordingState.isRecording) {
                                    val saved = onStopRecording()
                                    recordingState = RecordingControlsState(hasRecording = saved)
                                    recordingHint = if (saved) {
                                        "录好了。现在可以播放自己的录音。"
                                    } else {
                                        "这次录音太短，没有保存成功。再试一次。"
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
                }
            }
        }

        Button(
            onClick = { session = PracticeSession.start(lesson) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(if (session == null) "开始练习" else "今日练习已开始")
        }
    }
}

@Composable
private fun PracticeSectionCard(section: PracticeSection) {
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
                text = "${section.minutes} min · ${section.title}",
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
    SelfTaughtEnglishTheme {
        DailyLessonScreen(
            lesson = DailyLessonProvider().lessonForDay(1),
            onSpeak = {},
            hasRecordAudioPermission = { true },
            onStartRecording = {},
            onStopRecording = { true },
            onPlayRecording = {},
        )
    }
}
