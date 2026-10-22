package com.example.selftaughtenglish.learning

data class PracticeSession(
    val started: Boolean,
    val currentSection: PracticeSection,
) {
    val statusText: String = "正在练习：${currentSection.title}"

    companion object {
        fun start(lesson: DailyLesson): PracticeSession {
            return PracticeSession(
                started = true,
                currentSection = lesson.sections.first(),
            )
        }
    }
}
