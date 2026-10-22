package com.example.selftaughtenglish.learning

data class DailyLesson(
    val day: Int,
    val title: String,
    val focusSounds: List<String>,
    val sections: List<PracticeSection>,
) {
    val totalMinutes: Int = sections.sumOf { it.minutes }
}

data class PracticeSection(
    val type: PracticeType,
    val title: String,
    val minutes: Int,
    val description: String,
    val examples: List<String> = emptyList(),
)

enum class PracticeType {
    Listen,
    Pronounce,
    WordDrill,
    Review,
}
