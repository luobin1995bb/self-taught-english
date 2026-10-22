package com.example.selftaughtenglish.learning

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeSessionTest {

    private val lesson = DailyLessonProvider().lessonForDay(1)

    @Test
    fun startSessionSelectsFirstPracticeSection() {
        val session = PracticeSession.start(lesson)

        assertEquals("先听差别", session.currentSection.title)
        assertEquals("正在练习：先听差别", session.statusText)
        assertEquals("第 1 / 4 节", session.sectionProgressText)
        assertTrue(session.isFirstSection)
    }

    @Test
    fun nextMovesToTheFollowingSection() {
        val session = PracticeSession.start(lesson).next()

        assertEquals("练口型", session.currentSection.title)
        assertEquals("第 2 / 4 节", session.sectionProgressText)
        assertTrue(!session.isFirstSection)
    }

    @Test
    fun nextStopsAtTheLastSection() {
        var session = PracticeSession.start(lesson)

        repeat(lesson.sections.size + 3) { session = session.next() }

        assertEquals(lesson.sections.last().title, session.currentSection.title)
        assertTrue(session.isLastSection)
        assertEquals("第 4 / 4 节", session.sectionProgressText)
    }

    @Test
    fun previousStopsAtTheFirstSection() {
        var session = PracticeSession.start(lesson).next().next()

        repeat(lesson.sections.size + 3) { session = session.previous() }

        assertEquals(lesson.sections.first().title, session.currentSection.title)
        assertTrue(session.isFirstSection)
    }

    @Test
    fun nextThenPreviousReturnsToSameSection() {
        val started = PracticeSession.start(lesson)

        val roundTrip = started.next().previous()

        assertEquals(started.sectionIndex, roundTrip.sectionIndex)
        assertEquals(started.currentSection.title, roundTrip.currentSection.title)
    }
}
