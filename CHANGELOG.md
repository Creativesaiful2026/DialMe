# CHANGELOG

এই ফাইলে DialMe-র প্রকল্প-ইতিহাস লেখা থাকে। ধাঁচ: [Keep a Changelog](https://keepachangelog.com/)। কোনো সংস্করণ এখনো release হয়নি।

## [Unreleased]

### 2026-11-26 — Batch 1: ডেটা স্তর ও প্রধান স্ক্রিন (CI-তে এখনো চলেনি)

- মালিকের সিদ্ধান্ত: পুরো অ্যাপ বানিয়ে একসাথে পরীক্ষা। Batch পদ্ধতিতে কাজ; পরিকল্পনা ও ঝুঁকি `PROJECT_STATUS.md` ধারা ১০ক-তে।
- নতুন মডিউল `:core:data`: Contacts ও Call log পড়া (read only), T9 ও সার্চ, কল-গ্রুপিং, DataStore-এ থিম সংরক্ষণ (`DataStoreThemeRepository`)।
- `:app`: DialpadScreen (T9 পরামর্শ, পেস্ট, দীর্ঘ চাপ, দুই SIM-এ SIM বাছাই), ContactsScreen (সার্চ, বিস্তারিত), FavoritesScreen, RecentsScreen (Missed ফিল্টার, বিস্তারিত)। `tel:` লিংক এখন Dialpad-এ নম্বর বসায়।
- নতুন dependency: DataStore 1.2.0, `hilt-navigation-compose` 1.3.0।
- নতুন অনুমতি: `READ_CONTACTS`, `READ_CALL_LOG` (শুধু পড়া)।
- ১৮টি নতুন unit test; স্থানীয়ভাবে নকল JUnit রানারে পাস (CI-র প্রমাণ নয়)। `ContactsQuery` ও `CallLogQuery` `android-36.jar`-এর বিরুদ্ধে স্থানীয়ভাবে কম্পাইল হয়েছে।
- Build Verified: **না**। Device Tested: **না**।

### 2026-11-25 — CI Run 8 ও 9 এবং Annotation-সংস্করণ

- যাচাই (GitHub-এর পাবলিক run পাতা): diagnostic `ci.yml` `main`-এ বসেছে। Run 8 (commit `37897f7`, 1m 26s) ও Run 9 (commit `ee40cc3`, ম্যানুয়াল, 49s) সফল; artifact `DialMe-debug-8`, `reports-8`, `DialMe-debug-9`, `reports-9`।
- দুটি run-এ Annotations: 1 error ("Process completed with exit code 1", Lint ধাপ) ও 2 warning (Node.js 20 deprecation, setup-java v4 deprecation)। Job summary-র লেখা স্ট্যাটিক পাতায় আসে না, তাই Lint ত্রুটির কারণ ও unit test সংখ্যা এখনো দেখা যায়নি।
- নতুন `ci.yml` তৈরি: summary স্ক্রিপ্ট Lint Error, ব্যর্থ ধাপের মূল লাইন ও মডিউলভিত্তিক unit test সংখ্যা Annotation হিসেবেও পাঠায়। স্থানীয়ভাবে নকল ডেটায় পরীক্ষিত; **CI-তে এখনো চলেনি**; অ্যাপের কোড বদলায়নি।

### 2026-11-25 — CI diagnostic `ci.yml` (বসানো বাকি)

- Run 7-এর log লগইন ছাড়া পড়া যায় না (HTTP 404), তাই Lint ত্রুটির কারণ এখনো অজানা।
- নতুন `ci.yml` তৈরি: Build, Unit tests ও Lint ধাপের output `build.log`, `tests.log`, `lint.log`-এ রাখে; Lint ধাপে `--continue` যোগ হয়েছে; নতুন "Job summary" ধাপ run-এর পাতায় Lint ত্রুটি, ব্যর্থ ধাপের মূল লাইন ও মডিউলভিত্তিক unit test সংখ্যা লেখে।
- স্ক্রিপ্ট শুধু নকল lint ও test ডেটায় স্থানীয়ভাবে পরীক্ষিত। **CI-তে এখনো চলেনি**; অ্যাপের কোড বদলায়নি।
- মালিক জানিয়েছেন Device B এখন হাতে নেই; T4 সহ B-নির্ভর পরীক্ষা পরে একসাথে হবে। অবস্থা Not Yet Tested।

### 2026-11-25 — Phase 1B: T3 ও আগের default app

- মালিক জানিয়েছেন T3 (Outgoing call, SIM বাছাই ছাড়া, ডিফল্ট কলের SIM = Always ask) পাশ করেছে। এটি মালিকের মৌখিক রিপোর্ট; ধাপভিত্তিক পর্যবেক্ষণ ও স্ক্রিনশট বা log সংযুক্ত নেই। অবস্থা: **PASS (মালিকের রিপোর্ট)**।
- Device A-তে DialMe-র আগের default Phone app: **Google Dialer** (মালিক)। T2 ধাপ ৬ ও T15-এ এটিতে ফিরতে হবে।

### 2026-11-25 — Phase 1B: Device A-তে প্রথম পর্যবেক্ষণ

- মালিকের দুটি স্ক্রিনশট (17:46): Telecom spike (Phase 1B) স্ক্রিন খুলেছে। DialMe default Phone app = Yes; make calls, read SIM information, Notifications = Yes; Full-screen call notifications = **No**; SIM 1 ও SIM 2 তালিকায় আছে; কল বোতাম আছে।
- T1: **PASS** (স্ক্রিনশট প্রমাণ)। T2, T3, T5: আংশিক, বাকি ধাপ Not Yet Tested।
- মালিক নিশ্চিত করেছেন এই ফোনই Device A। ডিফল্ট কলের SIM: Always ask। মালিকের রিপোর্ট: নম্বর বসিয়ে কল দিলে কল যায় ও SIM 1/SIM 2 বেছে কল দেওয়া যায়।
- জানা বাকি: Device A-র আগের default Phone app-এর নাম (T2 ধাপ ৬, T15)।
- ছোট UI-পর্যবেক্ষণ: Spike স্ক্রিনে একটি label ও তার মান ("allowed" ও "No") ফাঁক ছাড়া লেগে আছে।

### 2026-11-25 — CI Run 7: Phase 1B-র debug APK তৈরি

- GitHub-এর পাবলিক run পাতা থেকে যাচাই (Claude): Run 7 (commit `16d16e9`, push, "Allow Android Lint step to continue on error") **Success**, 1m 7s। Artifact: `DialMe-debug-7` (10.3 MB), `reports-7` (89.1 KB)।
- Run 6 (commit `f02a1f0`, ম্যানুয়াল) ব্যর্থ, শুধু `reports-6`। দুটি commit-এই `:core:telecom`, `:feature:incall` ও `TelecomSpikeScreen` আছে।
- `main`-এর `ci.yml`-এ Lint ধাপে `continue-on-error: true` আছে (যাচাই করা হয়েছে)। Lint ধাপ এখনো 1 error দেখায়; কারণ অজানা, ত্রুটির লেখা দেখা যায়নি।
- `Upload debug APK` ধাপ চলায় `Build debug APK` ও `Unit tests` ধাপ ব্যর্থ হয়নি। ধাপভিত্তিক log দেখা হয়নি।
- আগের নোটের "শর্তসাপেক্ষ" অংশ নিষ্পত্তি হলো: স্ক্রিনশটের ব্যর্থ run Phase 1B-র commit-এরই ছিল।
- ডিভাইস পরীক্ষা শুরু হয়নি; T1–T15 সব Not Yet Tested।

### 2026-11-25 (সন্ধ্যা) — Phase 1B প্রথম CI ফল

- মালিকের স্ক্রিনশট: `Build debug APK` ✓, `Unit tests` ✓, `Android Lint (report only)` ✗ (শুধু "Error:" দেখা গেছে), `Upload debug APK` বাদ পড়েছে।
- ফলে Phase 1B-র APK এখনো ইনস্টল হয়নি; Settings-এ Telecom spike অনুপস্থিত (পুরোনো APK)।
- সিদ্ধান্ত: lint ধাপ রিপোর্ট-মাত্র, তাই নতুন `ci.yml`-এ সেটিতে `continue-on-error: true` যোগ করা হলো (হাতে বসাতে হবে)।

### 2026-11-25 — Phase 1B: Telecom Spike (সোর্স লেখা হয়েছে, CI-তে compile ও test সবুজ, APK বাকি)

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
