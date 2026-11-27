# DialMe — HANDOFF

সর্বশেষ হালনাগাদ: 2026-11-25

নতুন Claude কথোপকথনে কাজ চালিয়ে যাওয়ার আগে এই ফাইল এবং `PROJECT_STATUS.md` পুরো পড়ুন। আগের কাজ সম্পন্ন ধরে নেবেন না। repository-তে যা আছে তা যাচাই করুন।

## ১. এখন কোথায় আছি

- **Phase 0 (Planning): সম্পন্ন এবং অনুমোদিত।**
- **Phase 1 (Foundation): বাস্তবে সফল।** CI #3 সবুজ ✓ (3m 2s), APK ইনস্টল হয়ে চলেছে, লগ ও share কাজ করেছে (2026-11-25)। বাকি যাচাই: `PROJECT_STATUS.md` ধারা ৫ক-এর C4 (বাংলা), C5 (Dark/AMOLED), C6 (restart)।
- **Phase 1B (Telecom Spike): সোর্স আপলোড হয়েছে; CI Run 7 সফল, debug APK তৈরি (2026-11-25)।** CI Run 7 (commit `16d16e9`, push, 1m 7s) Success। Artifact: `DialMe-debug-7` (10.3 MB) ও `reports-7` (89.1 KB)। Run 6 (commit `f02a1f0`, ম্যানুয়াল) ব্যর্থ ছিল এবং শুধু `reports-6` ছিল। দুটি commit-এই Phase 1B-র সোর্স আছে (যাচাই: `:core:telecom`, `:feature:incall`, `TelecomSpikeScreen`)। `Upload debug APK` ধাপ চলেছে, তাই `Build debug APK` ও `Unit tests` ধাপ ব্যর্থ হয়নি (ধাপভিত্তিক log আমি দেখিনি)। `Android Lint` ধাপ এখনো ত্রুটি দেখাচ্ছে (annotation: 1 error, "Process completed with exit code 1"); `continue-on-error: true`-র কারণে run সবুজ। Lint-এর কারণ এখনো অজানা। প্রমাণের উৎস: GitHub-এর পাবলিক run পাতা (Claude নিজে দেখেছে)। APK Device A-তে ইনস্টল হয়েছে (স্ক্রিনশটে Telecom spike স্ক্রিন, 17:46)। T1 **PASS**; T3 **PASS** (মালিকের রিপোর্ট); T2, T5 আংশিক; বাকি সব **Not Yet Tested**। DialMe এখন Device A-র default Phone app।

## ২. মালিকের নির্দেশ (ক্রম মেনে)

1. Foundation বাস্তবে build যাচাই, তারপর রিপোর্ট। **সম্পন্ন।**
2. তারপর Phase 1B Telecom Spike। **কোড লেখা হয়েছে, CI Run 7 সফল ও APK তৈরি (প্রমাণ: run পাতা)। ডিভাইস পরীক্ষা বাকি।**
3. কোনো পরীক্ষার ফল অনুমান করে Passed করা যাবে না।
4. (2026-11-26) মালিক: পুরো অ্যাপ বানিয়ে একসাথে পরীক্ষা করতে চান; বাকি কাজ শুরু করতে বলেছেন। কাজ Batch-এ চলছে; প্রতিটি Batch-এর পর CI যাচাই। পরিকল্পনা, ঝুঁকি ও Batch 1-এর অবস্থা `PROJECT_STATUS.md` ধারা ১০ক-তে। Dialme.md ধারা ১০.৫ মালিকের অনুমোদনে শিথিল।
4. Phase 1B-র ফল ঘোষণার আগে ডিভাইস পরীক্ষা (Device A ও Device B) বাস্তবে হতে হবে।

## ৩. মালিকের কাজের পরিবেশ

- মালিক Android ফোন দিয়েই সব করেন। PC বা Android Studio ধরে নেবেন না।
- Claude এই চ্যাটে compile বা Gradle চালাতে পারে না (Google ও Maven repository নাগালের বাইরে)। build-এর সত্যিকারের ফলাফল শুধু GitHub Actions log থেকে আসবে।
- ZIP আপলোড পদ্ধতি: repository root-এ `inbox-*.zip`, তারপর `inbox-unzip.yml` workflow খুলে commit করে। ZIP-এর নাম `inbox-` দিয়ে শুরু হতে হবে। `.github/` ZIP-এ থাকে না; workflow ফাইল হাতে তৈরি করতে হয়।
- নতুন ধাপের ZIP-এর নাম হবে `inbox-phaseX-বিবরণ.zip` ধাঁচের।
- Repository: https://github.com/Creativesaiful2026/DialMe (পাবলিক)। Claude এই চ্যাট থেকে `github.com` ও `raw.githubusercontent.com` পড়তে পারে (সীমিত rate); ধাপের log ও artifact ডাউনলোডে লগইন লাগে, তাই সেগুলো মালিককে পাঠাতে হবে।

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
- Device A: মালিকের নিজের ফোন (নিশ্চিত)। ডিফল্ট কলের SIM: Always ask। আগের default Phone app: **Google Dialer** (T2 ধাপ ৬ ও T15-এ এটিতে ফিরতে হবে)। Spike স্ক্রিনে Full-screen call notifications = No; T6, T7, T10-এর প্রথম চক্র এভাবেই চলবে।
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

## ৯. পরবর্তী কাজ (এই ক্রমে)

1. মালিক: `.github/workflows/ci.yml` (Annotation-সংস্করণ) বসান (ZIP দিয়ে হয় না), তারপর `inbox-batch1-core-data-main-ui.zip` আপলোড ও `inbox-unzip.yml` চালান।
2. Claude: CI run-এর পাবলিক পাতার Annotations পড়ে Batch 1-এর compile ত্রুটি ঠিক করা। সবুজ না হওয়া পর্যন্ত Build Verified লেখা যাবে না।
3. Lint ত্রুটির আসল কারণ এখনো **অজানা**; Annotation-সংস্করণ চললে পড়া যাবে। অযাচাইকৃত সূত্র (প্রমাণ নেই): (ক) `abortOnError = false` শুধু `:app`-এ আছে; (খ) `SimAccountProvider`-এ `getActiveSubscriptionInfo` কল `catch (e: Exception)`-এর ভেতরে; (গ) `CallNotifier`-এ `nm.notify(...)`-এর আগে সরাসরি permission যাচাই নেই।
4. Batch 1 সবুজ হলে Batch 2 (পরিকল্পনা `PROJECT_STATUS.md` ধারা ১০ক)।
5. Device B হাতে এলে Phase 1B-র T4–T15 একসাথে। Device B ছাড়াও পারা যায়: C4–C6, T2 ধাপ ৩–৫, T11 ধাপ ১, T12।
