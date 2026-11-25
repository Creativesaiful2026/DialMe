# DialMe — HANDOFF

সর্বশেষ হালনাগাদ: 2026-11-25

নতুন Claude কথোপকথনে কাজ চালিয়ে যাওয়ার আগে এই ফাইল এবং `PROJECT_STATUS.md` পুরো পড়ুন। আগের কাজ সম্পন্ন ধরে নেবেন না। repository-তে যা আছে তা যাচাই করুন।

## ১. এখন কোথায় আছি

- **Phase 0 (Planning): সম্পন্ন এবং অনুমোদিত।**
- **Phase 1 (Foundation): বাস্তবে সফল।** CI #3 সবুজ ✓ (3m 2s), APK ইনস্টল হয়ে চলেছে, লগ ও share কাজ করেছে (2026-11-25)। বাকি যাচাই: `PROJECT_STATUS.md` ধারা ৫ক-এর C4 (বাংলা), C5 (Dark/AMOLED), C6 (restart)।
- **Phase 1B (Telecom Spike): সোর্স লেখা হয়েছে (2026-11-25), কিন্তু একবারও build হয়নি এবং ফোনে পরীক্ষাও হয়নি।** ১৫টি পরীক্ষা (T1–T15) সবগুলো **Not Yet Tested**।

## ২. মালিকের নির্দেশ (ক্রম মেনে)

1. Foundation বাস্তবে build যাচাই, তারপর রিপোর্ট। **সম্পন্ন।**
2. তারপর Phase 1B Telecom Spike। **কোড লেখা হয়েছে; এখন build যাচাই বাকি।**
3. কোনো পরীক্ষার ফল অনুমান করে Passed করা যাবে না।
4. Phase 1B-র ফল ঘোষণার আগে ডিভাইস পরীক্ষা (Device A ও Device B) বাস্তবে হতে হবে।

## ৩. মালিকের কাজের পরিবেশ

- মালিক Android ফোন দিয়েই সব করেন। PC বা Android Studio ধরে নেবেন না।
- Claude এই চ্যাটে compile বা Gradle চালাতে পারে না (Google ও Maven repository নাগালের বাইরে)। build-এর সত্যিকারের ফলাফল শুধু GitHub Actions log থেকে আসবে।
- ZIP আপলোড পদ্ধতি: repository root-এ `inbox-*.zip`, তারপর `inbox-unzip.yml` workflow খুলে commit করে। ZIP-এর নাম `inbox-` দিয়ে শুরু হতে হবে। `.github/` ZIP-এ থাকে না; workflow ফাইল হাতে তৈরি করতে হয়।
- নতুন ধাপের ZIP-এর নাম হবে `inbox-phaseX-বিবরণ.zip` ধাঁচের।

## ৪. Phase 1 Foundation-এ কী আছে

- Modules: `:app`, `:core:model`, `:core:common`, `:core:designsystem`, এবং Phase 1B-তে `:core:telecom`, `:feature:incall`। বাকিগুলো যে ধাপে লাগবে সে ধাপে যোগ হবে।
- `applicationId` ও package: `com.saiful.dialme`।
- Stack: Kotlin 2.2.20, AGP 8.13.2, Gradle 8.14.3, Compose BOM, Material 3, Navigation Compose, Hilt (শুধু `:app`, ViewModel `by viewModels()` দিয়ে), kotlinx.serialization, JDK 17।
- Theme system skeleton: `ThemeSpec`, `ThemeCodec`, `DialMeTheme`। স্থায়ী সংরক্ষণ নেই (Phase 7)।
- Diagnostics: in-app log (`DialMeLog`), crash handler, log share। কোনো PC ছাড়া সমস্যা পাঠাতে এটি ব্যবহার হয়।
- Debug signing: public `keystore/dialme-debug.jks`। এটি গোপন নয়। **কখনো release-এ ব্যবহার করবেন না।**
- minSdk 29, compileSdk ও targetSdk 36। পুরো দলিল `docs/DEPENDENCIES.md`-এ।

## ৪ক. সংস্করণ সম্পর্কে নোট

AndroidX ও Compose BOM-এর সংস্করণ প্রথম build-এ resolve হয়েছে (assembleDebug সফল)। ভবিষ্যতে নতুন সংস্করণ যোগ করলে "Could not find ..." ত্রুটি এলে `gradle/libs.versions.toml`-এ সংশ্লিষ্ট সংস্করণ ঠিক করুন। অনুমান করে একসাথে অনেক সংস্করণ বদলাবেন না।

## ৫. পরীক্ষার ডিভাইস

- Device A (DialMe চলবে) ও Device B (সহায়ক): দুটিই Redmi Note 12, Android 14, Xiaomi HyperOS 2.0.5.0।
- প্রতিটিতে ২টি সক্রিয় Physical SIM। eSIM নেই। নাম: A-SIM1, A-SIM2, B-SIM1, B-SIM2।
- **ফোন নম্বর চাইবেন না এবং কোথাও লিখবেন না।**
- Device A-র বর্তমান default Phone app-এর নাম শুধু Telecom Testing শুরুর আগে সংগ্রহ করবেন। এটি Phase 1-এর বাধা নয়।
- T5-এর চারটি Outgoing সমন্বয় (A-SIM1→B-SIM1, A-SIM1→B-SIM2, A-SIM2→B-SIM1, A-SIM2→B-SIM2) এবং T7-এর দুই SIM-এ Incoming পরীক্ষা অন্তর্ভুক্ত। সবগুলো Not Yet Tested।
- HyperOS-এর Autostart, Battery, background pop-up, lock screen অনুমতি T11-এ বাস্তবে পরীক্ষা হবে। সেটিংয়ের নাম অনুমান করবেন না।

## ৬. বাধ্যতামূলক নিয়ম

- প্রজেক্টের নাম DialMe, license GNU GPL v3, কোনো VoIP বা Internet calling নয়।
- অসম্পূর্ণ code, TODO placeholder বা ভুয়া implementation দেবেন না।
- প্রতিটি ফাইলের জন্য: সঠিক destination path, NEW বা REPLACE, সম্পূর্ণ code।
- প্রমাণ ছাড়া build সফল বলবেন না। ব্যর্থ হলে পুরো log পড়ুন, মূল কারণ খুঁজুন, সংশ্লিষ্ট ফাইল দেখুন।
- পরীক্ষার ফলাফল অনুমান করে বদলাবেন না।
- প্রতিটি ধাপ শেষে `PROJECT_STATUS.md`, `HANDOFF.md`, `CHANGELOG.md` হালনাগাদ করুন।
- ভাষা: মালিকের সাথে সব আলোচনা ও ডকুমেন্টেশন বাংলায়। Kotlin, Gradle, Android API, class বা function নাম, file path, package নাম ও code ইংরেজিতে অপরিবর্তিত।

## ৭. নিরাপত্তা সতর্কতা

- DialMe default dialer হলে এর ত্রুটিতে ফোনে কল করা অসম্ভব হতে পারে। আগের Phone app রাখতে বলুন। ফেরার পথ: Settings → Apps → Default apps → Phone app।
- জরুরি নম্বরে পরীক্ষার জন্য কল করতে বলবেন না। জরুরি কল সবসময় Telecom দিয়ে যাবে।

## ৮. Phase 1B-র নকশা (সংক্ষেপে; বিস্তারিত `docs/TELECOM.md`)

- `CallManager` সাধারণ `object` (Hilt নয়), `DialMeInCallService` তাকে খবর দেয়। UI শুধু `CallManager.calls` ও `CallManager.audio` পড়ে।
- `TelecomSpikeScreen` (Settings → Telecom spike) default dialer অবস্থা, permission, full-screen notification অনুমতি, SIM তালিকা ও কল বোতাম দেখায়। এটি নিজে থেকে কখনো ডায়াল করে না।
- লগে ফোন নম্বর মাস্ক করা থাকে। Call-এর প্রতিটি অবস্থা বদল `CallManager` tag-এ লেখা হয়।
- অনিশ্চিত: outgoing কলে activity খোলা, full-screen intent, HyperOS pop-up, SIM slot শনাক্ত, `CallAudioState` (deprecated)। এগুলো অনুমান নয়, পরীক্ষায় দেখতে হবে।
- ডিফল্ট থিম ব্যবহার হয় in-call স্ক্রিনে। App-wide theme Phase 7-এ।

## ৯. পরবর্তী কাজ

1. মালিক `inbox-phase1b-telecom-spike.zip` আপলোড করবেন। CI-র ফল (সবুজ বা ব্যর্থ log) দেখুন।
2. compile-ত্রুটি এলে পুরো log পড়ে মূল কারণ খুঁজুন। অনুমান করে অনেক ফাইল বদলাবেন না।
3. Build সফল হলে মালিককে Phase 1B পরীক্ষা (T1–T15) পরিচালনা করান। প্রথমে Spike স্ক্রিনের অবস্থা ও SIM তালিকা।
4. Device A কোন ফোন, এবং তার default Phone app কোনটি: Spike স্ক্রিনই দেখাবে। মালিক Device A নিশ্চিত করবেন।
