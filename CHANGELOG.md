# CHANGELOG

এই ফাইলে DialMe-র প্রকল্প-ইতিহাস লেখা থাকে। ধাঁচ: [Keep a Changelog](https://keepachangelog.com/)। কোনো সংস্করণ এখনো release হয়নি।

## [Unreleased]

### 2026-11-25 — Phase 1B: Telecom Spike (সোর্স লেখা হয়েছে, Build Verified নয়)

#### যোগ হয়েছে
- `:core:telecom`: `DialMeInCallService`, `CallManager`, `CallPlacer` (SIM বেছে কল), `SimAccountProvider` (SIM তালিকা ও slot), `DialerRole`, `TelecomStatus`, `DialNumbers`, incoming/ongoing notification ও `CallActionReceiver`।
- `:feature:incall`: `InCallActivity` ও সাদামাটা `InCallScreen` (ধরা, reject, শেষ, mute, hold, audio route, সময় গণনা)।
- `:app`: `ACTION_DIAL` ও `tel:` intent filter, Settings → Telecom spike স্ক্রিন, `tel:` লিংক থেকে নম্বর আগে থেকে বসানো (কখনো নিজে ডায়াল করে না)।
- Permission: `CALL_PHONE`, `READ_PHONE_STATE`, `POST_NOTIFICATIONS`, `USE_FULL_SCREEN_INTENT`।
- ১৩টি নতুন unit test (লেখা হয়েছে, চালানো হয়নি): `DialNumbersTest`, `CallStateMapperTest`, `DurationFormatterTest`।
- `docs/TELECOM.md`।

#### সংশোধন
- Foundation রিপোর্টে unit test সংখ্যা "১৮টি" লেখা হয়েছিল। সঠিক সংখ্যা ২২টি (ThemeSpec 5, ThemeCodec 8, RotatingLogFile 5, InMemoryThemeRepository 4)।

### 2026-11-25 — Phase 1: Foundation build ও প্রথম ডিভাইস পর্যবেক্ষণ

- মালিক জানিয়েছেন Actions-এর artifact থেকে debug APK নামিয়ে ইনস্টল করা গেছে, অর্থাৎ `assembleDebug` বাস্তবে সফল।
- স্ক্রিনশটে অ্যাপ চালু অবস্থায় দেখা গেছে: Settings স্ক্রিন, ৫-ট্যাব bottom bar, theme mode, dynamic color সুইচ, accent রঙ, corner radius slider।
- CI #3 সবুজ ✓ (3m 2s)। লগে শুরুর লাইন দেখা গেছে (Xiaomi 22101317C, Android 14), log share কাজ করেছে (মালিকের রিপোর্ট)।
- বাকি যাচাই: বাংলা লেখা, Dark/AMOLED, restart (`PROJECT_STATUS.md` ধারা ৫ক)।

### 2026-11-23 — Phase 1: Foundation (সোর্স লেখা হয়েছে, Build Verified নয়)

#### যোগ হয়েছে
- Gradle প্রজেক্ট (Gradle 8.14.3 wrapper, AGP 8.13.2, Kotlin 2.2.20) ও `gradle/libs.versions.toml`।
- Modules: `:app`, `:core:model`, `:core:common`, `:core:designsystem`।
- Compose অ্যাপ কাঠামো, ৫-ট্যাব bottom navigation (Favorites, Contacts, Recents, Dialpad, Settings), এখনো তৈরি না হওয়া অংশের জন্য সৎ placeholder স্ক্রিন।
- Theme system skeleton: `ThemeSpec`, `ThemeCodec` (JSON import/export ও যাচাই), `DialMeTheme`।
- Settings স্ক্রিন: theme mode, dynamic color, accent color, corner radius (শুধু memory-তে)।
- Diagnostics: `DialMeLog`, `RotatingLogFile`, `CrashHandler`, অ্যাপের ভেতরে log দেখা ও share।
- English ও বাংলা string resources।
- Hilt (শুধু `:app`)।
- Unit test: `ThemeSpecTest`, `ThemeCodecTest`, `RotatingLogFileTest`, `InMemoryThemeRepositoryTest` (লেখা হয়েছে, এখনো চালানো হয়নি)।
- GitHub Actions: `ci.yml`, `inbox-unzip.yml`।
- ডকুমেন্টেশন: README, BUILD_INSTRUCTIONS, CONTRIBUTING, `docs/ARCHITECTURE.md`, `docs/DEPENDENCIES.md`, PROJECT_STATUS, HANDOFF।
- LICENSE: GNU GPL v3।

#### পরিবর্তন (Phase 0 পরিকল্পনা থেকে)
- Debug signing: Secrets-এর বদলে public `keystore/dialme-debug.jks` (গোপন নয়, শুধু debug)। Release signing Phase 10-এ।
- ZIP আপলোড: `inbox/` ফোল্ডারের বদলে root-এ `inbox-*.zip`।
- `release.yml` ও `keystore-setup.yml` Phase 10-এ সরানো হয়েছে।

### 2026-11-23 — Phase 0 অনুমোদন

- মালিক Phase 0-এর আটটি সিদ্ধান্ত অনুমোদন করেছেন এবং Phase 1 Foundation শুরুর অনুমতি দিয়েছেন।
- সিদ্ধান্তের তালিকা: `PROJECT_STATUS.md` ধারা ৩।

### 2026-11-23 — Phase 1B পরীক্ষার পরিকল্পনা

- দুই-ডিভাইস Telecom পরীক্ষার পদ্ধতি (T1–T15) তৈরি। Device A ও Device B: Redmi Note 12, Android 14, HyperOS 2.0.5.0, প্রতিটিতে ২টি Physical SIM।
- T5-তে চারটি Outgoing SIM সমন্বয় ও T7-তে দুই SIM-এ Incoming পরীক্ষা অন্তর্ভুক্ত।
- সব পরীক্ষা Not Yet Tested।

### 2026-11-23 — Phase 0: পরিকল্পনা

- Master Project Document বিশ্লেষণ ও Technical Architecture খসড়া: architecture, module, SDK, Telecom strategy, permission, database schema, Android সীমাবদ্ধতা, dependency, GitHub Actions, roadmap, ঝুঁকি।
