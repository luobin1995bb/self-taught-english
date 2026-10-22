package com.example.selftaughtenglish.learning

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyLessonProviderTest {
    @Test
    fun firstLessonBuildsThirtyMinutePronunciationRoutine() {
        val lesson = DailyLessonProvider().lessonForDay(1)

        assertEquals("第 1 天：长音 /i:/ 和短音 /ɪ/", lesson.title)
        assertEquals(30, lesson.totalMinutes)
        assertEquals(
            listOf(
                PracticeType.Listen,
                PracticeType.Pronounce,
                PracticeType.WordDrill,
                PracticeType.Review,
            ),
            lesson.sections.map { it.type },
        )
        assertTrue(lesson.focusSounds.contains("/i:/"))
        assertTrue(lesson.focusSounds.contains("/ɪ/"))
    }
}
