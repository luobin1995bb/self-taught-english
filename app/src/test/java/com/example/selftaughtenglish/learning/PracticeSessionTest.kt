package com.example.selftaughtenglish.learning

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeSessionTest {
    @Test
    fun startSessionSelectsFirstPracticeSection() {
        val lesson = DailyLessonProvider().lessonForDay(1)

        val session = PracticeSession.start(lesson)

        assertTrue(session.started)
        assertEquals("先听差别", session.currentSection.title)
        assertEquals("正在练习：先听差别", session.statusText)
    }
}
