# DialMe — PROJECT_STATUS

সর্বশেষ হালনাগাদ: 2026-11-25

## ১. সারাংশ

| বিষয় | অবস্থা |
|---|---|
| **Phase 0 Approval Status** | **APPROVED** (মালিকের ঘোষণা অনুযায়ী) |
| Phase 1 (Foundation) | `assembleDebug` বাস্তবে সফল। CI #3 সবুজ ✓ (3m 2s, মালিকের স্ক্রিনশট)। APK ইনস্টল হয়ে চলেছে। বাকি যাচাই: ধারা ৫ক-এর C4–C6 |
| Phase 1B (Telecom Spike) | সোর্স Implemented। **CI Run 7 সফল ✓ (commit `16d16e9`, push, 1m 7s)।** Artifact: `DialMe-debug-7` (10.3 MB) ও `reports-7` (89.1 KB)। Run 6 (commit `f02a1f0`, ম্যানুয়াল) ব্যর্থ ছিল এবং শুধু `reports-6` ছিল। দুটি commit-এই Phase 1B-র সোর্স আছে (যাচাই: `:core:telecom`, `:feature:incall`, `TelecomSpikeScreen`)। `Upload debug APK` ধাপ চলেছে, তাই `Build debug APK` ও `Unit tests` ধাপ ব্যর্থ হয়নি (ধাপভিত্তিক log আমি দেখিনি)। `Android Lint` ধাপ এখনো ত্রুটি দেখাচ্ছে (annotation: 1 error, "Process completed with exit code 1"); `continue-on-error: true`-র কারণে run সবুজ। Lint-এর কারণ এখনো অজানা। APK Device A-তে ইনস্টল হয়েছে (স্ক্রিনশটে Telecom spike স্ক্রিন, 2026-11-25 17:46)। T1 **PASS**; T3 **PASS** (মালিকের রিপোর্ট); T2, T5 আংশিক; বাকি সব Not Yet Tested |
| Batch 1 (Phase 2–4 কোর: ডেটা স্তর ও প্রধান স্ক্রিন) | সোর্স Implemented (2026-11-26)। **CI-তে এখনো চলেনি**, তাই Build Verified নয়। বিস্তারিত ধারা ১০ক |

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

CI Run 7-এ Phase 1B-র সোর্স compile হয়েছে এবং `Unit tests` ধাপ ব্যর্থ হয়নি (`Upload debug APK` ধাপ চলেছে ও artifact আছে; ধাপভিত্তিক log দেখা হয়নি)। আগের "শর্ত"-এর নিষ্পত্তি: Run 6 ও Run 7 দুটিরই commit-এ Phase 1B-র ফাইল আছে। APK Device A-তে ইনস্টল হয়েছে; ডিভাইস পরীক্ষার অবস্থা ধারা ৮ ও ৮ক-তে।

| বৈশিষ্ট্য | Implemented | Build Verified | Unit Tested | Device Tested |
|---|---|---|---|---|
| Default Phone app role অনুরোধ ও manifest শর্ত (`ACTION_DIAL`, `tel:`, `InCallService`) | হ্যাঁ | হ্যাঁ (CI Run 7) | — | আংশিক (স্ক্রিনশট: "DialMe is the default Phone app: Yes"; Deny, restart ও ফেরার ধাপ বাকি) |
| `DialMeInCallService` ও `CallManager` | হ্যাঁ | হ্যাঁ (CI Run 7) | — | Not Yet Tested |
| কল করা (`TelecomManager.placeCall`), SIM বেছে নেওয়া | হ্যাঁ | হ্যাঁ (CI Run 7) | — | আংশিক (T3 PASS (মালিকের রিপোর্ট); SIM বেছে কল T5-এ যাচাই বাকি) |
| SIM তালিকা ও slot নম্বর শনাক্ত | হ্যাঁ | হ্যাঁ (CI Run 7) | — | আংশিক (স্ক্রিনশট: SIM 1 ও SIM 2 তালিকায় আছে; কলে সঠিক SIM যাচাই বাকি) |
| In-call স্ক্রিন: ধরা, reject, শেষ, mute, hold, audio route | হ্যাঁ | হ্যাঁ (CI Run 7) | — | Not Yet Tested |
| Incoming ও ongoing notification (full-screen intent, Answer/Decline/Hang up) | হ্যাঁ | হ্যাঁ (CI Run 7) | — | Not Yet Tested |
| Telecom spike স্ক্রিন (অবস্থা তালিকা, number ও SIM-ভিত্তিক কল বোতাম) | হ্যাঁ | হ্যাঁ (CI Run 7) | — | আংশিক (স্ক্রিনশট: অবস্থা তালিকা, SIM তালিকা ও কল বোতাম দেখা গেছে) |
| নম্বর পরিষ্কার ও মাস্ক (`DialNumbers`), call state mapping, সময় ফরম্যাট | হ্যাঁ | হ্যাঁ (CI Run 7) | `Unit tests` ধাপ ব্যর্থ হয়নি (CI Run 7; ১৩টি নতুন পরীক্ষা) | — |

বিস্তারিত নকশা ও অনিশ্চিত বিষয়: `docs/TELECOM.md`।

**CI run তালিকা (উৎস: GitHub-এর পাবলিক run পাতা, 2026-11-25):**

| Run | Commit | কীভাবে চলল | ফল | Artifact |
|---|---|---|---|---|
| #6 | `f02a1f0` | ম্যানুয়াল (github-actions[bot]) | ব্যর্থ ✗ (1m 45s) | `reports-6` |
| #7 | `16d16e9` | push ("Allow Android Lint step to continue on error") | সফল ✓ (1m 7s) | `DialMe-debug-7` (10.3 MB), `reports-7` (89.1 KB) |
| #8 | `37897f7` | push ("Enhance CI workflow with logging and diagnostics") | সফল ✓ (1m 26s) | `DialMe-debug-8`, `reports-8` (129 KB) |
| #9 | `ee40cc3` | ম্যানুয়াল (github-actions[bot]) | সফল ✓ (49s) | `DialMe-debug-9`, `reports-9` (129 KB) |

**অমীমাংসিত সমস্যা (Lint):** `Android Lint (report only)` ধাপে এখনো 1 error ("Process completed with exit code 1") দেখাচ্ছে। `continue-on-error: true`-র কারণে run সবুজ এবং APK আপলোড হয়েছে। আসল ত্রুটির লেখা আমি দেখিনি (log ও artifact ডাউনলোডে লগইন লাগে)। কারণ **অজানা**। করণীয়: `reports-7` artifact-এর lint রিপোর্ট বা Lint ধাপের log পাঠান। কোড পড়ে পাওয়া অযাচাইকৃত সূত্র `HANDOFF.md` ধারা ৯-এ আছে; এগুলো প্রমাণ নয়।

**Lint নির্ণয়ের অবস্থা (2026-11-25, GitHub-এর পাবলিক run পাতা থেকে যাচাই):** Diagnostic `ci.yml` (Job summary ধাপসহ) `main`-এ বসেছে। Run 8 (commit `37897f7`, push, 1m 26s) ও Run 9 (commit `ee40cc3`, ম্যানুয়াল, 49s) দুটিই সফল; Artifact: `DialMe-debug-8`/`reports-8` (10.3 MB / 129 KB) ও `DialMe-debug-9`/`reports-9`। দুটি run-এই Annotations: 1 error ("Process completed with exit code 1", Lint ধাপ) ও 2 warning (Node.js 20 deprecation, setup-java v4 deprecation)। Job summary-র লেখা পাতার স্ট্যাটিক HTML-এ আসে না, তাই Lint ত্রুটির কারণ ও unit test সংখ্যা আমি **এখনো দেখিনি**। Log ও artifact ডাউনলোডে লগইন লাগে (HTTP 404)। সমাধান: summary স্ক্রিপ্টে Annotation যোগ করা নতুন `ci.yml` (Lint Error, ব্যর্থ ধাপের মূল লাইন ও মডিউলভিত্তিক unit test সংখ্যা Annotation হিসেবে আসবে, যা পাতায় পড়া যায়)। স্ক্রিপ্ট শুধু নকল ডেটায় স্থানীয়ভাবে পরীক্ষিত; **CI-তে এখনো চলেনি**। হাতে বসানো বাকি: `.github/workflows/ci.yml` (REPLACE)।

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

**Device A নিশ্চিত (মালিক, 2026-11-25):** Foundation APK ও Phase 1B APK যে ফোনে চলেছে সেটিই Device A। লগ অনুযায়ী `Xiaomi 22101317C`, Android 14 (API 34); মালিকের ভাষায় Redmi Note 12 5G।

Device A-র **আগের** default Phone app: **Google Dialer** (মালিক, 2026-11-25)। T2 ধাপ ৬ ও T15-এ এটিতে ফিরতে হবে।

## ৮. Phase 1B পরীক্ষার অবস্থা (Device A-তে)

Device A-তে পরীক্ষা শুরু হয়েছে (2026-11-25)। নিচে যেগুলোর পাশে ফল লেখা নেই, সেগুলো **Not Yet Tested**। শুধু প্রমাণ (স্ক্রিনশট বা log) পাওয়া ধাপই এগিয়েছে।

| পরীক্ষা | বিষয় | অবস্থা |
|---|---|---|
| T1 | ইনস্টল ও প্রথম চালু | **PASS** (স্ক্রিনশট ২টি, 17:46: Telecom spike স্ক্রিন crash ছাড়া খুলেছে ও default dialer অবস্থা দেখিয়েছে। APK: `DialMe-debug-7` ধরা হচ্ছে, কারণ Phase 1B-র APK শুধু run #7-এই আছে; মালিক আলাদা করে নিশ্চিত করেননি) |
| T2 | Default dialer পরিবর্তন | আংশিক: role পাওয়া গেছে (স্ক্রিনশটে default: Yes)। Cancel/Deny, restart ও আগের app-এ ফেরার ধাপ Not Yet Tested |
| T3 | Outgoing call (SIM বাছাই ছাড়া) | **PASS** (মালিকের মৌখিক রিপোর্ট: "T3 পাশ করেছে"; ডিফল্ট কলের SIM = Always ask)। ধাপভিত্তিক পর্যবেক্ষণ (কলার আইডি, শব্দ, call log) ও স্ক্রিনশট বা log সংযুক্ত নেই |
| T4 | Outgoing কলের বিভিন্ন পরিণতি | Not Yet Tested |
| T5 | SIM নির্বাচন করে Outgoing (৪টি সমন্বয়) | আংশিক: SIM তালিকা (SIM 1, SIM 2) দেখা গেছে। চারটি কল Not Yet Tested |
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

## ৮ক. Device A-র প্রাথমিক অবস্থা (স্ক্রিনশট, 2026-11-25 17:46, ব্যাটারি 85%)

- Device A: মালিকের নিজের ফোন (লগে `Xiaomi 22101317C`, মালিকের ভাষায় Redmi Note 12 5G)।
- ডিফল্ট কলের SIM (Android সেটিং): **Always ask** (মালিক)।
- DialMe-র আগের default Phone app: **Google Dialer** (মালিক)।
- Telecom spike স্ক্রিন: DialMe default Phone app = Yes · make calls = Yes · read SIM information = Yes · Notifications allowed = Yes · **Full-screen call notifications allowed = No**।
- SIM তালিকা: SIM 1, SIM 2 (ক্যারিয়ারের নামসহ দেখা গেছে)।
- T6, T7, T10-এর প্রথম চক্র এই সেটিং যেমন আছে (Full-screen = No) তেমনই চালানো হবে। সেটিং বদলের পরীক্ষা T11-এ।
- Device A-তে ইনস্টল করা APK: `DialMe-debug-7` ধরা হচ্ছে। নতুন APK #8 ও #9 তৈরি আছে; পরীক্ষায় যে APK ইনস্টল থাকবে তার run নম্বর লিখে রাখতে হবে।
- HyperOS-এর অন্য সেটিং (Autostart, Battery, background pop-up, lock screen) এখনো লেখা হয়নি।

## ৯. পরিচিত বাগ

কোনো বাগ রেকর্ড হয়নি। Lint ত্রুটি অমীমাংসিত (ধারা ৫খ)। Phase 1B-র কোড CI Run 7-এ compile হয়েছে, কিন্তু ডিভাইসে চলেনি।

**ছোট UI-পর্যবেক্ষণ (স্ক্রিনশট):** Telecom spike স্ক্রিনে "Full-screen call notifications allowed" ও "No" মাঝে ফাঁক ছাড়া লেগে আছে। শুধু দেখতে; কাজে সমস্যা নয়। Phase 1B-র পরে ঠিক করা যাবে।

## ১০. পরিচিত ঝুঁকি

- Phase 1B-র কোড CI-তে compile হয়েছে, কিন্তু কোনো ডিভাইসে চলেনি; runtime আচরণ অজানা।
- Outgoing কলে service থেকে activity খোলা, full-screen intent ও HyperOS-এর pop-up সীমাবদ্ধতা অনিশ্চিত (`docs/TELECOM.md`)।
- DialMe default dialer হলে এর ত্রুটিতে কল করা যাবে না। আগের Phone app রাখতে হবে। ফেরার পথ: Settings → Apps → Default apps → Phone app।
- জরুরি নম্বরে পরীক্ষার জন্য কখনো কল করা যাবে না।

## ১০ক. মালিকের সিদ্ধান্ত: পুরো অ্যাপ বানিয়ে একসাথে পরীক্ষা (2026-11-26)

- **সিদ্ধান্ত (মালিক):** "একেবারে পুরো এপ বানিয়ে টেস্ট করতে চাই। বাকী কাজ শুরু করো।" Dialme.md ধারা ১০.৫ ("বর্তমান ফেজের acceptance শেষ না করে পরের ফেজ নয়") মালিকের অনুমোদনে এই কাজের জন্য শিথিল। Phase 1B-র T4–T15 ডিভাইস পরীক্ষা Device B হাতে এলে পরে একসাথে হবে।
- **কাজের পদ্ধতি:** কাজ Batch-এ ভাগ করা। প্রতিটি Batch আলাদা ZIP। প্রতিটির পর CI যাচাই (Claude নিজে run পাতা ও Annotations পড়বে), যাতে compile ত্রুটি জমে না যায়। ডিভাইস পরীক্ষা সব বা অনেক Batch শেষে, মালিকের ইচ্ছামতো।
- **ঝুঁকি (Claude-এর মূল্যায়ন):** Phase 5 (কলিং) HyperOS-এ কেমন চলে, তা T4–T15-এর ফলের উপর নির্ভর করে। সেই ফল আসার আগে কলিং-নির্ভর UI বানালে rework লাগতে পারে। তাই কলিং-নির্ভর Batch পরে রাখা হয়েছে।

**Batch পরিকল্পনা (ক্রম বদলাতে পারে):**

| Batch | বিষয় (Dialme.md-র ফেজ) | অবস্থা |
|---|---|---|
| 1 | ডেটা স্তর (`:core:data`), প্রধান স্ক্রিন: Dialpad, Contacts, Favorites, Recents, থিম সংরক্ষণ (Phase 2–4-এর কোর) | সোর্স লেখা হয়েছে, CI-তে চলেনি |
| 2 | Settings সংগঠন (ধারা ৪.৪), Call History বিস্তার (তারিখ ফিল্টার, বিস্তারিত, পরিসংখ্যান), Contacts উন্নয়ন | পরিকল্পিত |
| 3 | Phase 5: আসল কলিং UI (incoming, active call, audio routing, hold, waiting) | পরিকল্পিত (T4–T15-এর ফলের উপর নির্ভর) |
| 4 | Phase 6 ও 7: caller screen, quick response, call notes, reminders; theme engine, font, icon, layout, theme import/export | পরিকল্পিত |
| 5 | Phase 8: blocking, privacy mode, smart suggestions, automation, statistics (Room লাগবে) | পরিকল্পিত |
| 6 | Phase 9: backup/restore, optimization, accessibility, localization | পরিকল্পিত |
| 7 | Phase 10: পূর্ণ পরীক্ষা, ডকুমেন্টেশন, GPL যাচাই, release | পরিকল্পিত |

**Batch 1-এর অবস্থা (2026-11-26):**

| বিষয় | অবস্থা |
|---|---|
| Implemented | হ্যাঁ। নতুন `:core:data` (Contacts ও CallLog পড়া, T9 ও সার্চ, কল-গ্রুপিং, DataStore-এ থিম সংরক্ষণ); `:app`-এ DialpadScreen, ContactsScreen, FavoritesScreen, RecentsScreen, কল চালু করা ও SIM বাছাই |
| Build Verified | **না।** CI-তে এখনো চলেনি |
| Unit Tested | স্থানীয়ভাবে ১৮টি test পাস (T9, ContactSearch, CallLogGrouping, PhoneKeys)। Kotlin 2.4.x কম্পাইলারে ও নকল JUnit রানারে চলেছে; প্রজেক্টের Kotlin 2.2.20 ও আসল JUnit-এ নয়, তাই CI-র প্রমাণ নয় |
| স্থানীয় কম্পাইল | `ContactsQuery`, `CallLogQuery` `android-36.jar`-এর বিরুদ্ধে ত্রুটি ছাড়া কম্পাইল হয়েছে। Flow, DataStore, Hilt ও Compose অংশ কম্পাইল করা যায়নি (library নেই) |
| Device Tested | না |

**Batch 1-এর নকশা-সিদ্ধান্ত:**
- UI `:app`-এর `ui/` প্যাকেজে রাখা হয়েছে (আলাদা `:feature:*` মডিউল নয়), যাতে Gradle ত্রুটির ক্ষেত্র কমে। পরে সরানো যাবে।
- Contacts ও Call log শুধু পড়া হয় (`READ_CONTACTS`, `READ_CALL_LOG`)। `WRITE_*` অনুমতি এখনো নেই, তাই contact সম্পাদনা, তারকা বদল ও call log মোছা নেই। Favorites = সিস্টেম Contacts-এ তারকা দেওয়া কন্টাক্ট।
- T9 শুধু ইংরেজি (Latin) অক্ষরে। বাংলা নাম T9-তে মেলে না; নম্বর দিয়ে ও Contacts স্ক্রিনের টেক্সট সার্চে মেলে।
- Dialpad: portrait-প্রথম; কী-সাউন্ড ও ভাইব্রেশন নেই (Phase 7); টাইপের সময় নম্বর যেমন, তালিকায় ফরম্যাট করা।
- দুই SIM ও সিস্টেমে ডিফল্ট কলের SIM সেট না থাকলে DialMe নিজে SIM বাছাইয়ের ডায়ালগ দেখায়।
- Dependency: DataStore 1.2.0 ও `hilt-navigation-compose` 1.3.0 (যাচাই: developer.android.com-এর release পাতা; CI-তে resolve হয়নি)।
- থিম এখন DataStore-এ সংরক্ষিত (আগে শুধু মেমরিতে)। Settings-এর নোটের লেখা সেভাবে বদলেছে।

**Batch 1 পরীক্ষার তালিকা (ডিভাইসে, পরে):** কন্টাক্ট অনুমতি দিলে তালিকা আসে; সার্চ (নাম ও নম্বর); Favorites-এ তারকা দেওয়া কন্টাক্ট; কল ইতিহাস অনুমতি ও তালিকা, Missed ফিল্টার; Dialpad-এ নম্বর, T9 পরামর্শ, পেস্ট, দীর্ঘ চাপে `0`→`+` ও মুছুন→সব মোছা; Dialpad থেকে কল (দুই SIM-এ SIM বাছাই); থিম বদলে অ্যাপ বন্ধ করে খুললে থিম থাকে; `tel:` লিংক থেকে Dialpad-এ নম্বর বসে।

## ১১. পরবর্তী কাজ

1. মালিক: আগে `.github/workflows/ci.yml` (Annotation-সংস্করণ) বসান, তারপর `inbox-batch1-core-data-main-ui.zip` আপলোড করে `inbox-unzip.yml` চালান।
2. Claude: নতুন CI run-এর Annotations পড়ে compile ত্রুটি ঠিক করা; সবুজ না হওয়া পর্যন্ত Batch 1 "Build Verified" নয়।
3. Batch 1 সবুজ হলে Batch 2। Lint ত্রুটির কারণ Annotation থেকে পড়া।
4. Device B হাতে এলে Phase 1B-র T4–T15 একসাথে।
