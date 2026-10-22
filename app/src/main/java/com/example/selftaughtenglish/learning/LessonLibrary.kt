package com.example.selftaughtenglish.learning

/**
 * 内置课程库。
 *
 * 每天固定四段：先听差别 5 分钟、练口型 10 分钟、单词跟读 10 分钟、标记难点 5 分钟。
 * 内容刻意只覆盖最容易混淆的音对，一天只解决一组，避免贪多。
 */
internal object LessonLibrary {

    val lessons: List<DailyLesson> = listOf(
        DailyLesson(
            day = 1,
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
        ),
        DailyLesson(
            day = 2,
            title = "第 2 天：大口 /æ/ 和小口 /e/",
            focusSounds = listOf("/æ/", "/e/"),
            sections = listOf(
                PracticeSection(
                    type = PracticeType.Listen,
                    title = "先听差别",
                    minutes = 5,
                    description = "两个音都在口腔前部，区别在嘴巴张开的幅度。",
                    examples = listOf("bad / bed", "man / men", "sat / set"),
                ),
                PracticeSection(
                    type = PracticeType.Pronounce,
                    title = "练口型",
                    minutes = 10,
                    description = "/æ/ 下巴放低、嘴张大；/e/ 只张开一半，嘴角稍微往两边拉。",
                    examples = listOf("aah", "eh", "aah - eh"),
                ),
                PracticeSection(
                    type = PracticeType.WordDrill,
                    title = "单词跟读",
                    minutes = 10,
                    description = "先慢慢读，再遮住文字跟读一遍，用耳朵确认没读混。",
                    examples = listOf("had / head", "band / bend", "pack / peck"),
                ),
                PracticeSection(
                    type = PracticeType.Review,
                    title = "标记难点",
                    minutes = 5,
                    description = "回放最难的一组，看嘴巴张合幅度够不够明显。",
                    examples = listOf("能听出来", "能说出来", "还要复习"),
                ),
            ),
        ),
        DailyLesson(
            day = 3,
            title = "第 3 天：长音 /u:/ 和短音 /ʊ/",
            focusSounds = listOf("/u:/", "/ʊ/"),
            sections = listOf(
                PracticeSection(
                    type = PracticeType.Listen,
                    title = "先听差别",
                    minutes = 5,
                    description = "长音要把音拉满，短音收得干脆，中间不要拖。",
                    examples = listOf("fool / full", "pool / pull", "Luke / look"),
                ),
                PracticeSection(
                    type = PracticeType.Pronounce,
                    title = "练口型",
                    minutes = 10,
                    description = "/u:/ 嘴唇收圆往前送气；/ʊ/ 嘴唇放松，声音短促。",
                    examples = listOf("ooo", "uh", "ooo - uh"),
                ),
                PracticeSection(
                    type = PracticeType.WordDrill,
                    title = "单词跟读",
                    minutes = 10,
                    description = "长音数两拍、短音数一拍，用手打拍子配合。",
                    examples = listOf("food / foot", "suit / soot", "cooed / could"),
                ),
                PracticeSection(
                    type = PracticeType.Review,
                    title = "标记难点",
                    minutes = 5,
                    description = "回放最难的一组，确认长音没有读成短音。",
                    examples = listOf("能听出来", "能说出来", "还要复习"),
                ),
            ),
        ),
        DailyLesson(
            day = 4,
            title = "第 4 天：咬舌音 /θ/ 和 /s/",
            focusSounds = listOf("/θ/", "/s/"),
            sections = listOf(
                PracticeSection(
                    type = PracticeType.Listen,
                    title = "先听差别",
                    minutes = 5,
                    description = "中文里没有 /θ/，很容易直接读成 /s/，先听出气流位置。",
                    examples = listOf("think / sink", "thick / sick", "mouth / mouse"),
                ),
                PracticeSection(
                    type = PracticeType.Pronounce,
                    title = "练口型",
                    minutes = 10,
                    description = "舌尖轻放在上下齿之间送气，不要咬紧；/s/ 舌尖在齿后，不碰牙。",
                    examples = listOf("th", "ss", "th - ss"),
                ),
                PracticeSection(
                    type = PracticeType.WordDrill,
                    title = "单词跟读",
                    minutes = 10,
                    description = "对着镜子练，确认读 /θ/ 时能看到舌尖。",
                    examples = listOf("thin / sin", "path / pass", "worth / worse"),
                ),
                PracticeSection(
                    type = PracticeType.Review,
                    title = "标记难点",
                    minutes = 5,
                    description = "回放录音，检查 /θ/ 有没有偷懒读成 /s/。",
                    examples = listOf("能听出来", "能说出来", "还要复习"),
                ),
            ),
        ),
        DailyLesson(
            day = 5,
            title = "第 5 天：咬唇 /v/ 和圆唇 /w/",
            focusSounds = listOf("/v/", "/w/"),
            sections = listOf(
                PracticeSection(
                    type = PracticeType.Listen,
                    title = "先听差别",
                    minutes = 5,
                    description = "两个音的嘴型完全不同，一个用牙，一个用唇。",
                    examples = listOf("vest / west", "vine / wine", "vet / wet"),
                ),
                PracticeSection(
                    type = PracticeType.Pronounce,
                    title = "练口型",
                    minutes = 10,
                    description = "/v/ 上齿轻咬下唇、出声震动；/w/ 双唇收圆，牙齿完全不碰唇。",
                    examples = listOf("vvv", "wuh", "vvv - wuh"),
                ),
                PracticeSection(
                    type = PracticeType.WordDrill,
                    title = "单词跟读",
                    minutes = 10,
                    description = "先摸着下巴读，/v/ 能感到震动，/w/ 没有。",
                    examples = listOf("van / wan", "vary / wary", "veal / wheel"),
                ),
                PracticeSection(
                    type = PracticeType.Review,
                    title = "标记难点",
                    minutes = 5,
                    description = "回放录音，确认 /v/ 没有读成 /w/。",
                    examples = listOf("能听出来", "能说出来", "还要复习"),
                ),
            ),
        ),
        DailyLesson(
            day = 6,
            title = "第 6 天：连读与句尾收干净",
            focusSounds = listOf("连读", "句尾辅音"),
            sections = listOf(
                PracticeSection(
                    type = PracticeType.Listen,
                    title = "先听差别",
                    minutes = 5,
                    description = "听句子里词和词是怎么粘在一起的，哪里没有停顿。",
                    examples = listOf("last night", "an apple", "turn it off"),
                ),
                PracticeSection(
                    type = PracticeType.Pronounce,
                    title = "练口型",
                    minutes = 10,
                    description = "前一个词以辅音结尾、后一个词以元音开头时，把两个音连起来读。",
                    examples = listOf("an apple", "turn it off", "pick it up"),
                ),
                PracticeSection(
                    type = PracticeType.WordDrill,
                    title = "单词跟读",
                    minutes = 10,
                    description = "整句跟读，句尾的 /t/ /d/ 要读出来但不加额外的元音。",
                    examples = listOf("not at all", "take it easy", "find out"),
                ),
                PracticeSection(
                    type = PracticeType.Review,
                    title = "标记难点",
                    minutes = 5,
                    description = "录一整句，回放听词与词之间有没有多余断开。",
                    examples = listOf("能连起来", "能读顺", "还要复习"),
                ),
            ),
        ),
    )
}
