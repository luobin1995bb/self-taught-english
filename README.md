# SelfTaughtEnglish

> 一个用 Kotlin + Jetpack Compose 写的英语发音自学 App —— 每天跟着课程听、跟读、录音回放，自己给自己当陪练。

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.02.01-4285F4?logo=jetpackcompose&logoColor=white)
![minSdk](https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)

## 这是什么

自学英语最难的是「没人纠正你的发音」。这个 App 的思路很朴素：**给你一段 30 分钟的发音训练流程，用系统 TTS 提供示范音，用麦克风录下你自己的声音，回放对比。**

没有 AI 打分，没有云服务，不联网，所有音频只留在手机本地。

## 功能

- **每日课程**：内置 6 天课程，按「先听差别 → 练口型 → 单词跟读 → 标记难点」四段式编排，每天 30 分钟
- **课程库**：覆盖 /i:/ 与 /ɪ/、/æ/ 与 /e/、/u:/ 与 /ʊ/、/θ/ 与 /s/、/v/ 与 /w/ 五组易混音，第 6 天练连读与句尾
- **分节流转**：单节课内可「上一节 / 下一节」走完全部四个环节，首尾自动禁用按钮
- **按天切换**：首页可在「上一天 / 下一天」之间切换，走完一天会记录本次完成状态
- **示范音播放**：调用系统 `TextToSpeech`，美式发音，语速压到 0.75x 便于跟读
- **录音回放**：`MediaRecorder` 录 AAC 到应用缓存目录，`MediaPlayer` 立即回放，和示范音对照
- **权限处理**：首次录音时申请 `RECORD_AUDIO`，拒绝后给出提示而不是崩溃
- **纯本地**：无网络权限、无第三方 SDK、无数据上传

## 技术栈

| 项 | 版本 / 说明 |
| --- | --- |
| 语言 | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3（BOM 2026.02.01） |
| 构建 | AGP 9.2.1，Gradle Kotlin DSL，Version Catalog |
| SDK | compileSdk 36 / minSdk 24 / targetSdk 36 |
| Java | 11 |
| 音频 | `android.speech.tts.TextToSpeech`、`MediaRecorder`、`MediaPlayer` |

无 Room、无 Retrofit、无 Navigation —— 依赖刻意保持最小。

## 快速开始

```bash
git clone https://github.com/luobin1995bb/self-taught-english.git
cd self-taught-english
```

用 Android Studio 打开工程根目录，等待 Gradle 同步完成即可运行。

- `local.properties` 由 Android Studio 自动生成，**不要提交**
- 命令行构建：`./gradlew assembleDebug`，产物在 `app/build/outputs/apk/debug/`
- 录音功能需要真机，或在模拟器中开启麦克风输入（Extended Controls → Microphone → Enable）
- 示范音依赖设备已安装 TTS 引擎；若无声，检查「设置 → 语言和输入法 → 文字转语音」的输出引擎

运行单元测试：

```bash
./gradlew test
```

## 项目结构

```
app/src/main/java/com/example/selftaughtenglish/
├── MainActivity.kt              # 入口 Activity，承载 TTS 生命周期与主界面
├── learning/
│   ├── LessonModels.kt          # DailyLesson / PracticeSection 数据模型
│   ├── LessonLibrary.kt         # 内置课程库（6 天，每天四节）
│   ├── DailyLessonProvider.kt   # 按天取课，越界自动夹取
│   ├── PracticeSession.kt       # 练习会话状态与上一节/下一节流转
│   ├── PronunciationPrompt.kt   # 从课程段落推导要朗读的文本
│   └── RecordingControlsState.kt# 录音按钮的状态机
├── audio/
│   └── PracticeAudioRecorder.kt # 录音与回放封装
└── ui/theme/                    # Compose 主题
```

`app/src/test/` 下有针对课程生成、会话、发音提示提取、录音按钮状态的单元测试。

## 当前进度

课程库已有 6 天内容，按天推进与单节课内的分节流转都已可用。

Roadmap：

- [x] 课程内容抽成数据源，支持按天推进
- [x] 单个课程内可切换到下一节
- [x] 补充课程库（5 组易混音 + 连读）
- [ ] 用 DataStore 记录打卡进度、连续天数、待复习清单（完成状态目前只在内存里，重启即重置）
- [ ] 录音波形与时长可视化
- [ ] 自动评分或波形对比，替代纯人耳对照

## 参与贡献

Issue 和 PR 都欢迎。提交前请确认：

1. `./gradlew test` 全部通过
2. 新增课程内容放在 `learning/` 下的数据源中，不要写进 UI 层
3. 不引入网络依赖 —— 本项目的定位是离线可用

## License

[MIT](LICENSE)
