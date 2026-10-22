package com.example.selftaughtenglish.learning

import org.junit.Assert.assertEquals
import org.junit.Test

class PronunciationPromptTest {
    @Test
    fun listenSectionBuildsSpeakablePromptFromExamples() {
        val lesson = DailyLessonProvider().lessonForDay(1)
        val section = lesson.sections.first()

        val prompt = PronunciationPrompt.from(section)

        assertEquals("sheep. ship. seat. sit. leave. live.", prompt.textToSpeak)
        assertEquals("跟读：sheep / ship、seat / sit、leave / live", prompt.displayText)
    }
}
