# DialMe — PROJECT_STATUS

সর্বশেষ হালনাগাদ: 2026-11-25

## ১. সারাংশ

| বিষয় | অবস্থা |
|---|---|
| **Phase 0 Approval Status** | **APPROVED** (মালিকের ঘোষণা অনুযায়ী) |
| Phase 1 (Foundation) | `assembleDebug` বাস্তবে সফল। CI #3 সবুজ ✓ (3m 2s, মালিকের স্ক্রিনশট)। APK ইনস্টল হয়ে চলেছে। বাকি যাচাই: ধারা ৫ক-এর C4–C6 |
| Phase 1B (Telecom Spike) | **সোর্স Implemented (2026-11-25)। এখনো build হয়নি, ফোনে পরীক্ষাও হয়নি।** T1–T15 সব Not Yet Tested |

## ২. অবস্থার সংজ্ঞা

Implemented · Build Verified · Unit Tested · Device Tested · Not Yet Tested

বাস্তব প্রমাণ ছাড়া কোনো অবস্থা এগিয়ে নেওয়া হয় না। **Implemented** মানে শুধু সোর্স কোড লেখা হয়েছে।

## ৩. Phase 0 অনুমোদিত সিদ্ধান্ত

1. minSdk 29; compileSdk ও targetSdk 36।
2. ১১টি Gradle module (ধাপে ধাপে যোগ হচ্ছে; এখন ৬টি)।
3. Phase 1B (Telecom Spike) নতুন ধাপ হিসেবে।
4. Permission পরিবর্তন: SEND_SMS যোগ (এখনো যোগ হয়নি, Phase 6); ANSWER_PHONE_CALLS, FOREGROUND_SERVICE, READ_MEDIA_IMAGES বাদ।
5. Blocking ও spam-এর সীমিত পরিধি (v1-এ cloud spam lookup নেই)।
6. Public repository, GPL-3.0।
7. Code upload পদ্ধতি: Option A (ZIP আপলোড + `inbox-unzip` workflow)। মালিক নির্দিষ্ট করে বলেননি, তাই প্রস্তাবিত পদ্ধতি ধরা হয়েছে।
8. পরীক্ষার জন্য ফোন: দুটি Redmi Note 12 (ধারা ৭)।

## ৪. সচেতনভাবে নেওয়া সিদ্ধান্ত ও Phase 0 থেকে পরিবর্তন

- **Debug signing:** public `keystore/dialme-debug.jks` (password `android`), গোপন নয়, শুধু debug build-এর স্থায়ী signature-এর জন্য। **Release-এ ব্যবহার করা যাবে না।** Release signing Phase 10-এ।
- **ZIP আপলোড:** root-এ `inbox-*.zip`। `.github/` ZIP-এ থাকে না; `ci.yml` ও `inbox-unzip.yml` হাতে তৈরি।
- `applicationId` ও package: `com.saiful.dialme`।
- **Phase 1B-র নকশা সিদ্ধান্ত (Claude-এর):**
  - নতুন module: `:core:telecom` ও `:feature:incall`।
  - `CallManager` Hilt-এর বদলে সাধারণ process-wide `object`, কারণ সিস্টেম `InCallService` বানায়।
  - In-call স্ক্রিন এখন ডিফল্ট theme ব্যবহার করে (app-wide theme Phase 7-এ)।
  - Audio route-এ `CallAudioState` (Android 14-এ deprecated, কিন্তু ন্যূনতম ঝুঁকির জন্য)।
  - Notification সাধারণ ধরনের (CallStyle নয়)।
  - লগে ফোন নম্বর মাস্ক করা হয় (শেষ দুই অঙ্ক ছাড়া সব `*`)।

## ৫. Phase 1 Foundation — বৈশিষ্ট্যভিত্তিক অবস্থা

| বৈশিষ্ট্য | Implemented | Build Verified (`assembleDebug`) | Unit Tested | Device Tested |
|---|---|---|---|---|
| Gradle প্রজেক্ট, wrapper (8.14.3), version catalog | হ্যাঁ | হ্যাঁ | — | — |
| Modules: `:app`, `:core:model`, `:core:common`, `:core:designsystem` | হ্যাঁ | হ্যাঁ | — | — |
| ৫-ট্যাব navigation ও placeholder স্ক্রিন | হ্যাঁ | হ্যাঁ | — | আংশিক (স্ক্রিনশটে ৫-ট্যাব bar দেখা গেছে) |
| Theme system skeleton | হ্যাঁ | হ্যাঁ | CI #3-এ সফল (run সবুজ) | আংশিক (স্ক্রিনশটে নীল accent, Light, গোলাকার card) |
| Settings: theme mode, dynamic color, accent, corner radius | হ্যাঁ | হ্যাঁ | — | আংশিক (স্ক্রিনশট) |
| Diagnostics: in-app log, crash handler | হ্যাঁ | হ্যাঁ | `RotatingLogFile` পরীক্ষা: CI #3-এ সফল | হ্যাঁ: লগে শুরুর লাইন দেখা গেছে |
| লগ share | হ্যাঁ | হ্যাঁ | — | হ্যাঁ (মালিকের রিপোর্ট: "শেয়ার হয়") |
| English ও বাংলা string | হ্যাঁ | হ্যাঁ | — | বাংলা Not Yet Tested |
| Hilt (শুধু `:app`) | হ্যাঁ | হ্যাঁ | — | হ্যাঁ (অ্যাপ চালু হয়েছে) |
| GitHub Actions: `ci.yml`, `inbox-unzip.yml` | হ্যাঁ | CI #3 সফল (Inbox Unzip #1 সফল, 10s) | — | — |

**নোট:** CI #3 "Manually run by github-actions[bot]" হিসেবে দেখা গেছে, অর্থাৎ Inbox Unzip থেকে CI চালু করার ধাপও কাজ করেছে। CI #1 ও #2 লাল ছিল, কারণ তখনো প্রজেক্ট আপলোড হয়নি (প্রত্যাশিত)। CI-র ধাপভিত্তিক log আমি দেখিনি; run সবুজ হওয়ায় সব ধাপ (build, unit test, lint) সফল ধরা হচ্ছে, কারণ workflow-তে কোনো ধাপের ব্যর্থতা উপেক্ষা করা হয় না।

## ৫ক. Foundation চূড়ান্ত যাচাই তালিকা

| ক্রম | যাচাই | অবস্থা |
|---|---|---|
| C1 | CI run সবুজ ✓ | **হ্যাঁ** (CI #3, 3m 2s, স্ক্রিনশট) |
| C2 | App log খুললে শুরুর লাইন দেখায় | **হ্যাঁ**: `DialMe 0.1.0 (build 1) started on Xiaomi 22101317C, Android 14 (API 34)` |
| C3 | লগ Share কাজ করে | **হ্যাঁ** (মালিকের রিপোর্ট) |
| C4 | ফোনের ভাষা বাংলা করলে অ্যাপের লেখা বাংলায় আসে | Not Yet Tested |
| C5 | Dark ও AMOLED black বেছে দেখা | Not Yet Tested |
| C6 | অ্যাপ বন্ধ করে আবার খুললে crash নেই | Not Yet Tested |

## ৫খ. Phase 1B — বৈশিষ্ট্যভিত্তিক অবস্থা

সবকিছু সোর্স লেখা হয়েছে মাত্র। কোনোটাই build বা ডিভাইসে যাচাই হয়নি।

| বৈশিষ্ট্য | Implemented | Build Verified | Unit Tested | Device Tested |
|---|---|---|---|---|
| Default Phone app role অনুরোধ ও manifest শর্ত (`ACTION_DIAL`, `tel:`, `InCallService`) | হ্যাঁ | না | — | Not Yet Tested |
| `DialMeInCallService` ও `CallManager` | হ্যাঁ | না | — | Not Yet Tested |
| কল করা (`TelecomManager.placeCall`), SIM বেছে নেওয়া | হ্যাঁ | না | — | Not Yet Tested |
| SIM তালিকা ও slot নম্বর শনাক্ত | হ্যাঁ | না | — | Not Yet Tested |
| In-call স্ক্রিন: ধরা, reject, শেষ, mute, hold, audio route | হ্যাঁ | না | — | Not Yet Tested |
| Incoming ও ongoing notification (full-screen intent, Answer/Decline/Hang up) | হ্যাঁ | না | — | Not Yet Tested |
| Telecom spike স্ক্রিন (অবস্থা তালিকা, number ও SIM-ভিত্তিক কল বোতাম) | হ্যাঁ | না | — | Not Yet Tested |
| নম্বর পরিষ্কার ও মাস্ক (`DialNumbers`), call state mapping, সময় ফরম্যাট | হ্যাঁ | না | পরীক্ষা লেখা হয়েছে (১৩টি), চালানো হয়নি | — |

বিস্তারিত নকশা ও অনিশ্চিত বিষয়: `docs/TELECOM.md`।

## ৬. Dependency অবস্থা

`docs/DEPENDENCIES.md` দেখুন। Phase 1B নতুন কোনো বাইরের library যোগ করেনি।

## ৭. পরীক্ষার ডিভাইস (Device A ও Device B নামেই উল্লেখ; ফোন নম্বর রেকর্ড হয় না)

| বিষয় | Device A (DialMe চলবে) | Device B (সহায়ক ফোন) |
|---|---|---|
| মডেল | Redmi Note 12 | Redmi Note 12 |
| Android | 14 | 14 |
| সফটওয়্যার | Xiaomi HyperOS 2.0.5.0 | Xiaomi HyperOS 2.0.5.0 |
| সক্রিয় SIM | ২টি | ২টি |
| SIM 1 | Physical SIM | Physical SIM |
| SIM 2 | Physical SIM | Physical SIM |
| eSIM | ব্যবহার হচ্ছে না | ব্যবহার হচ্ছে না |

SIM-এর পরিচয়: A-SIM1, A-SIM2, B-SIM1, B-SIM2।

**Foundation APK যে ফোনে চলেছে:** লগ অনুযায়ী `Xiaomi 22101317C`, Android 14 (API 34)। এই ফোনটি Device A কিনা, তা এখনো মালিক নিশ্চিত করেননি।

Device A-র বর্তমান default Phone app-এর নাম পরীক্ষার আগে সংগ্রহ হবে। Telecom spike স্ক্রিন এটি নিজেই দেখাবে ("Current default Phone app")।

## ৮. Phase 1B পরীক্ষার অবস্থা (Device A-তে)

কোনো পরীক্ষা চালানো হয়নি। সবগুলো **Not Yet Tested**।

| পরীক্ষা | বিষয় | অবস্থা |
|---|---|---|
| T1 | ইনস্টল ও প্রথম চালু | Not Yet Tested |
| T2 | Default dialer পরিবর্তন | Not Yet Tested |
| T3 | Outgoing call (SIM বাছাই ছাড়া) | Not Yet Tested |
| T4 | Outgoing কলের বিভিন্ন পরিণতি | Not Yet Tested |
| T5 | SIM নির্বাচন করে Outgoing (৪টি সমন্বয়) | Not Yet Tested |
| T6 | Incoming call (অবস্থাভেদে) | Not Yet Tested |
| T7 | দুই SIM-এ Incoming call | Not Yet Tested |
| T8 | Active call নিয়ন্ত্রণ | Not Yet Tested |
| T9 | Audio routing | Not Yet Tested |
| T10 | Lock screen ও full-screen incoming UI | Not Yet Tested |
| T11 | HyperOS অনুমতি ও background সীমাবদ্ধতা | Not Yet Tested |
| T12 | অনুমতি প্রত্যাখ্যানের আচরণ | Not Yet Tested |
| T13 | স্থিতিশীলতা | Not Yet Tested |
| T14 | Call waiting (ঐচ্ছিক, তৃতীয় ফোন লাগবে) | Not Yet Tested |
| T15 | স্বাভাবিক অবস্থায় ফেরা | Not Yet Tested |

### T5-এর চারটি Outgoing সমন্বয়

| ক্রম | Device A | Device B | অবস্থা |
|---|---|---|---|
| ১ | A-SIM1 | B-SIM1 | Not Yet Tested |
| ২ | A-SIM1 | B-SIM2 | Not Yet Tested |
| ৩ | A-SIM2 | B-SIM1 | Not Yet Tested |
| ৪ | A-SIM2 | B-SIM2 | Not Yet Tested |

## ৯. পরিচিত বাগ

কোনো বাগ রেকর্ড হয়নি। Phase 1B-র কোড এখনো build হয়নি, তাই প্রথম build-এ compile-ত্রুটি আসতে পারে (Claude এই চ্যাটে compile করতে পারে না)।

## ১০. পরিচিত ঝুঁকি

- Phase 1B-র সোর্স কোড কখনো compile হয়নি। log পাঠালে ঠিক করা হবে।
- Outgoing কলে service থেকে activity খোলা, full-screen intent ও HyperOS-এর pop-up সীমাবদ্ধতা অনিশ্চিত (`docs/TELECOM.md`)।
- DialMe default dialer হলে এর ত্রুটিতে কল করা যাবে না। আগের Phone app রাখতে হবে। ফেরার পথ: Settings → Apps → Default apps → Phone app।
- জরুরি নম্বরে পরীক্ষার জন্য কখনো কল করা যাবে না।

## ১১. পরবর্তী কাজ

1. মালিক `inbox-phase1b-telecom-spike.zip` আপলোড করবেন। CI চলবে। সফল হলে APK ইনস্টল।
2. ব্যর্থ হলে পুরো log পাঠাবেন; Claude ঠিক করবে।
3. Build সফল হলে ডিভাইস পরীক্ষা: Phase 1B পরীক্ষা-পদ্ধতি (T1–T15) অনুযায়ী। প্রথমে Spike স্ক্রিনের অবস্থা তালিকা ও SIM তালিকা পরীক্ষা (T1, T2, T5)।
