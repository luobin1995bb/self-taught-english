package com.example.selftaughtenglish.learning

/**
 * 一次练习会话的状态：记录当前在第几节。
 *
 * 到首尾边界时 [next] / [previous] 返回自身，UI 可以直接据此禁用按钮。
 */
data class PracticeSession(
    val lesson: DailyLesson,
    val sectionIndex: Int,
) {
    init {
        require(sectionIndex in lesson.sections.indices) {
            "sectionIndex 超出范围：$sectionIndex"
        }
    }

    val currentSection: PracticeSection = lesson.sections[sectionIndex]

    val statusText: String = "正在练习：${currentSection.title}"

    val sectionProgressText: String = "第 ${sectionIndex + 1} / ${lesson.sections.size} 节"

    val isFirstSection: Boolean = sectionIndex == 0

    val isLastSection: Boolean = sectionIndex == lesson.sections.lastIndex

    fun next(): PracticeSession {
        return if (isLastSection) this else copy(sectionIndex = sectionIndex + 1)
    }

    fun previous(): PracticeSession {
        return if (isFirstSection) this else copy(sectionIndex = sectionIndex - 1)
    }

    companion object {
        fun start(lesson: DailyLesson): PracticeSession {
            return PracticeSession(lesson = lesson, sectionIndex = 0)
        }
    }
}
