# DialMe

**Your Dialer. Your Style.**

DialMe একটি open-source Android SIM-ভিত্তিক Phone/Dialer অ্যাপ। লক্ষ্য: পুরোপুরি কাস্টমাইজযোগ্য, offline-first, বিজ্ঞাপন ও ট্র্যাকিং ছাড়া একটি ডায়ালার।

- Platform: Android (minSdk 29, targetSdk 36)
- ভাষা: Kotlin, Jetpack Compose (Material 3)
- License: GNU GPL v3 (`LICENSE`)
- কোনো VoIP বা Internet calling নেই। শুধু SIM-ভিত্তিক কল।

## অবস্থা

প্রকল্প প্রাথমিক পর্যায়ে। **Phase 1 (Foundation)** build হয়েছে ও ইনস্টল করে চালানো গেছে। **Phase 1B (Telecom Spike)** সোর্স লেখা হয়েছে, কিন্তু এখনো build বা ফোনে যাচাই হয়নি। বিস্তারিত: `PROJECT_STATUS.md`।

## ডকুমেন্টেশন

| ফাইল | বিষয় |
|---|---|
| `BUILD_INSTRUCTIONS.md` | ফোন থেকে GitHub Actions দিয়ে APK বানানো |
| `PROJECT_STATUS.md` | বর্তমান অবস্থা, পরীক্ষার ফলাফল |
| `HANDOFF.md` | নতুন কথোপকথনে কাজ চালিয়ে যাওয়ার নির্দেশনা |
| `CHANGELOG.md` | প্রকল্পের ইতিহাস |
| `CONTRIBUTING.md` | অবদান রাখার নিয়ম |
| `docs/ARCHITECTURE.md` | স্থাপত্যের সারসংক্ষেপ |
| `docs/TELECOM.md` | Telecom Spike-এর নকশা ও অনিশ্চিত বিষয় |
| `docs/DEPENDENCIES.md` | ব্যবহৃত library ও সংস্করণ |

## গোপনীয়তা

অ্যাপ কোনো ডেটা সার্ভারে পাঠায় না। কোনো analytics, crash-reporting বা বিজ্ঞাপন library নেই। লগ শুধু অ্যাপের ব্যক্তিগত স্টোরেজে থাকে এবং আপনি নিজে share করলে তবেই বাইরে যায়।
