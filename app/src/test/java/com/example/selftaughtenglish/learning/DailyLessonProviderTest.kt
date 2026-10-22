package com.example.selftaughtenglish.learning

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyLessonProviderTest {

    private val provider = DailyLessonProvider()

    @Test
    fun firstLessonBuildsThirtyMinutePronunciationRoutine() {
        val lesson = provider.lessonForDay(1)

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

    @Test
    fun everyDayProvidesACompleteThirtyMinuteRoutine() {
        assertTrue(provider.totalDays >= 6)

        for (day in 1..provider.totalDays) {
            val lesson = provider.lessonForDay(day)

            assertEquals(day, lesson.day)
            assertEquals(30, lesson.totalMinutes)
            assertEquals(4, lesson.sections.size)
            assertTrue(lesson.focusSounds.isNotEmpty())
            assertTrue(lesson.sections.all { it.examples.isNotEmpty() })
        }
    }

    @Test
    fun everyDayHasItsOwnLesson() {
        val titles = (1..provider.totalDays).map { provider.lessonForDay(it).title }

        assertEquals(titles.size, titles.toSet().size)
    }

    @Test
    fun dayParameterIsNoLongerIgnored() {
        val first = provider.lessonForDay(1)
        val second = provider.lessonForDay(2)

        assertTrue(first.title != second.title)
        assertTrue(first.focusSounds != second.focusSounds)
    }

    @Test
    fun outOfRangeDayIsClampedToValidRange() {
        assertEquals(provider.lessonForDay(1).title, provider.lessonForDay(0).title)
        assertEquals(provider.lessonForDay(1).title, provider.lessonForDay(-5).title)
        assertEquals(
            provider.lessonForDay(provider.totalDays).title,
            provider.lessonForDay(provider.totalDays + 10).title,
        )
    }

    @Test
    fun hasDayRejectsOutOfRangeValues() {
        assertFalse(provider.hasDay(0))
        assertFalse(provider.hasDay(provider.totalDays + 1))
        assertTrue(provider.hasDay(1))
        assertTrue(provider.hasDay(provider.totalDays))
    }
}
