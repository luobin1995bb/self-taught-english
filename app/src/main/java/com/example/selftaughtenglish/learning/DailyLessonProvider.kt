package com.example.selftaughtenglish.learning

class DailyLessonProvider {
    fun lessonForDay(day: Int): DailyLesson {
        return DailyLesson(
            day = day,
            title = "第 1 天：长音 /i:/ 和短音 /ɪ/",
            focusSounds = listOf("/i:/", "/ɪ/"),
            sections = listOf(
                PracticeSection(
                    type = PracticeType.Listen,
                    title = "先听差别",
                    minutes = 5,
                    description = "先只听，不急着开口。重点分清长音和短音的区别。",
                    examples = listOf("sheep / ship", "seat / sit", "leave / live"),
                ),
                PracticeSection(
                    type = PracticeType.Pronounce,
                    title = "练口型",
                    minutes = 10,
                    description = "发 /i:/ 时嘴角更像微笑；发 /ɪ/ 时嘴巴放松一点、声音更短。",
                    examples = listOf("eee", "ih", "eee - ih"),
                ),
                PracticeSection(
                    type = PracticeType.WordDrill,
                    title = "单词跟读",
                    minutes = 10,
                    description = "先慢慢读每组单词，再不看文字跟读一遍。",
                    examples = listOf("see / sit", "feel / fill", "green / grin"),
                ),
                PracticeSection(
                    type = PracticeType.Review,
                    title = "标记难点",
                    minutes = 5,
                    description = "回放今天最难的一组，标记明天还要不要复习。",
                    examples = listOf("能听出来", "能说出来", "还要复习"),
                ),
            ),
        )
    }
}
