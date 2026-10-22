package com.example.selftaughtenglish.learning

data class PronunciationPrompt(
    val textToSpeak: String,
    val displayText: String,
) {
    companion object {
        fun from(section: PracticeSection): PronunciationPrompt {
            val words = section.examples
                .flatMap { it.split("/") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            return PronunciationPrompt(
                textToSpeak = words.joinToString(". ", postfix = "."),
                displayText = "跟读：${section.examples.joinToString("、")}",
            )
        }
    }
}
