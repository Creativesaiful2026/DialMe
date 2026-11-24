# DialMe — HANDOFF

সর্বশেষ হালনাগাদ: 2026-11-23

নতুন Claude কথোপকথনে কাজ চালিয়ে যাওয়ার আগে এই ফাইল এবং `PROJECT_STATUS.md` পুরো পড়ুন। আগের কাজ সম্পন্ন ধরে নেবেন না। repository-তে যা আছে তা যাচাই করুন।

## ১. এখন কোথায় আছি

- **Phase 0 (Planning): সম্পন্ন এবং অনুমোদিত।** মালিক আটটি সিদ্ধান্ত অনুমোদন করেছেন (তালিকা `PROJECT_STATUS.md` ধারা ৩)।
- **Phase 1 (Foundation): মালিক শুরুর স্পষ্ট অনুমতি দিয়েছেন (2026-11-23)।** সোর্স কোড লেখা হয়েছে, কিন্তু **GitHub Actions-এ এখনো কোনো build চালানো হয়নি**। তাই কিছুই Build Verified নয়।
- Phase 1B (Telecom Spike): শুরু হয়নি। শুধু ১৫টি পরীক্ষার পদ্ধতি তৈরি আছে। সবগুলো **Not Yet Tested**।

## ২. মালিকের নির্দেশ (ক্রম মেনে)

1. পুরো Foundation তৈরি করা এবং GitHub Actions দিয়ে বাস্তব build যাচাই।
2. Foundation সফল হলে মালিককে রিপোর্ট দেওয়া।
3. তারপর Phase 1B Telecom Spike শুরু। এর আগে নয়।
4. কোনো পরীক্ষার ফল অনুমান করে Passed করা যাবে না।

## ৩. মালিকের কাজের পরিবেশ

- মালিক Android ফোন দিয়েই সব করেন। PC বা Android Studio ধরে নেবেন না।
- Claude এই চ্যাটে compile বা Gradle চালাতে পারে না (Google ও Maven repository নাগালের বাইরে)। build-এর সত্যিকারের ফলাফল শুধু GitHub Actions log থেকে আসবে।
- ZIP আপলোড পদ্ধতি: repository root-এ `inbox-*.zip`, তারপর `inbox-unzip.yml` workflow খুলে commit করে। ZIP-এর নাম `inbox-` দিয়ে শুরু হতে হবে। `.github/` ZIP-এ থাকে না; workflow ফাইল হাতে তৈরি করতে হয়।
- নতুন ধাপের ZIP-এর নাম হবে `inbox-phaseX-বিবরণ.zip` ধাঁচের।

## ৪. Phase 1 Foundation-এ কী আছে

- Modules: `:app`, `:core:model`, `:core:common`, `:core:designsystem`। বাকিগুলো যে ধাপে লাগবে সে ধাপে যোগ হবে।
- `applicationId` ও package: `com.saiful.dialme`।
- Stack: Kotlin 2.2.20, AGP 8.13.2, Gradle 8.14.3, Compose BOM, Material 3, Navigation Compose, Hilt (শুধু `:app`, ViewModel `by viewModels()` দিয়ে), kotlinx.serialization, JDK 17।
- Theme system skeleton: `ThemeSpec`, `ThemeCodec`, `DialMeTheme`। স্থায়ী সংরক্ষণ নেই (Phase 7)।
- Diagnostics: in-app log (`DialMeLog`), crash handler, log share। কোনো PC ছাড়া সমস্যা পাঠাতে এটি ব্যবহার হয়।
- Debug signing: public `keystore/dialme-debug.jks`। এটি গোপন নয়। **কখনো release-এ ব্যবহার করবেন না।**
- minSdk 29, compileSdk ও targetSdk 36। পুরো দলিল `docs/DEPENDENCIES.md`-এ।

## ৪ক. সংস্করণ নিয়ে সতর্কতা

AndroidX ও Compose BOM-এর সংস্করণ এই পরিবেশে যাচাই করা যায়নি। প্রথম build-এ "Could not find ..." ত্রুটি এলে `gradle/libs.versions.toml`-এ সংশ্লিষ্ট সংস্করণ ঠিক করুন। অনুমান করে একসাথে অনেক সংস্করণ বদলাবেন না।

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

## ৮. পরবর্তী কাজ

1. মালিক repository বানিয়ে ZIP আপলোড করে Actions চালাবেন (`BUILD_INSTRUCTIONS.md`)।
2. build-এর ফলাফল (সফল হলে নিশ্চিতকরণ, ব্যর্থ হলে পুরো log) দেখে Claude রিপোর্ট দেবে বা ঠিক করবে।
3. Foundation Build Verified হলে রিপোর্টের পর মালিকের নির্দেশে Phase 1B।
