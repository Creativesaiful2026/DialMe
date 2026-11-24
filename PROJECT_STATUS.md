# DialMe — PROJECT_STATUS

সর্বশেষ হালনাগাদ: 2026-11-23

## ১. সারাংশ

| বিষয় | অবস্থা |
|---|---|
| **Phase 0 Approval Status** | **APPROVED** (মালিকের ঘোষণা অনুযায়ী) |
| Phase 1 (Foundation) অনুমতি | দেওয়া হয়েছে (2026-11-23) |
| Phase 1 বর্তমান অবস্থা | সোর্স কোড লেখা হয়েছে (Implemented)। GitHub Actions-এ **এখনো চালানো হয়নি, তাই Build Verified নয়** |
| Phase 1B (Telecom Spike) | Phase 1 Foundation সফল build-এর পর মালিকের কাছে রিপোর্ট দিয়ে তবেই শুরু হবে |

## ২. অবস্থার সংজ্ঞা

Implemented · Build Verified · Unit Tested · Device Tested · Not Yet Tested

বাস্তব প্রমাণ ছাড়া কোনো অবস্থা এগিয়ে নেওয়া হয় না। **Implemented** মানে শুধু সোর্স কোড লেখা হয়েছে।

## ৩. Phase 0 অনুমোদিত সিদ্ধান্ত

1. minSdk 29; compileSdk ও targetSdk 36।
2. ১১টি Gradle module (প্রয়োজন অনুযায়ী ধাপে ধাপে যোগ হবে; Phase 1-এ ৪টি)।
3. Phase 1B (Telecom Spike) নতুন ধাপ হিসেবে।
4. Permission পরিবর্তন: SEND_SMS যোগ; ANSWER_PHONE_CALLS, FOREGROUND_SERVICE, READ_MEDIA_IMAGES বাদ।
5. Blocking ও spam-এর সীমিত পরিধি (v1-এ cloud spam lookup নেই)।
6. Public repository, GPL-3.0।
7. Code upload পদ্ধতি: Option A (ZIP আপলোড + `inbox-unzip` workflow)। মালিক নির্দিষ্ট করে বলেননি, তাই প্রস্তাবিত পদ্ধতি ধরা হয়েছে।
8. পরীক্ষার জন্য ফোন: দুটি Redmi Note 12 (ধারা ৭)।

## ৪. Phase 0 থেকে সচেতনভাবে পরিবর্তিত বিষয় (মালিকের নজরে আনা হয়েছে)

- **Debug signing:** Phase 0-এ ছিল Secrets-এ keystore। এখন public repository-তে `keystore/dialme-debug.jks` কমিট করা হয়েছে। এটি জনসাধারণের জানা debug key (password `android`), গোপন নয়, শুধু debug build-এর স্থায়ী signature-এর জন্য। কারণ: ফোন থেকে নিরাপদে secret keystore বানানোর সহজ পথ নেই, আর public repo-তে artifact বা log-এ key ফাঁস হওয়ার ঝুঁকি আছে। **Release signing আলাদাভাবে Phase 10-এ ঠিক হবে। এই key release-এ ব্যবহার করা যাবে না।**
- **ZIP আপলোড:** `inbox/` ফোল্ডারের বদলে repository-র root-এ `inbox-*.zip`। কারণ GitHub মোবাইল ওয়েবে ফোল্ডার বেছে আপলোড কঠিন।
- **`.github/` ZIP-এ নেই:** GitHub Actions-এর token দিয়ে workflow ফাইল বদলানো যায় না। তাই `ci.yml` ও `inbox-unzip.yml` একবার হাতে তৈরি করতে হয়।
- `release.yml` ও `keystore-setup.yml` Phase 10-এ।
- `applicationId` ও package: `com.saiful.dialme` (Claude বেছে নিয়েছে; মালিক চাইলে Phase 1 build-এর আগেই বদলানো সহজ, পরে কঠিন)।

## ৫. Phase 1 Foundation — বৈশিষ্ট্যভিত্তিক অবস্থা

| বৈশিষ্ট্য | Implemented | Build Verified | Unit Tested | Device Tested |
|---|---|---|---|---|
| Gradle প্রজেক্ট, wrapper (8.14.3), version catalog | হ্যাঁ | না | — | — |
| Modules: `:app`, `:core:model`, `:core:common`, `:core:designsystem` | হ্যাঁ | না | — | — |
| Compose অ্যাপ কাঠামো ও ৫-ট্যাব navigation (placeholder স্ক্রিন) | হ্যাঁ | না | — | না |
| Theme system skeleton (`ThemeSpec`, `ThemeCodec`, `DialMeTheme`) | হ্যাঁ | না | লেখা হয়েছে, চালানো হয়নি | না |
| Settings: theme mode, dynamic color, accent, corner radius (শুধু memory-তে) | হ্যাঁ | না | — | না |
| Diagnostics: in-app log, crash handler, log share | হ্যাঁ | না | `RotatingLogFile` পরীক্ষা লেখা, চালানো হয়নি | না |
| English ও বাংলা string resources | হ্যাঁ | না | — | না |
| Hilt (শুধু `:app`) | হ্যাঁ | না | — | — |
| GitHub Actions: `ci.yml`, `inbox-unzip.yml` | হ্যাঁ | না | — | — |

Phase 1 acceptance criteria অনুযায়ী বাকি: `ci.yml` সফল run, debug APK ইনস্টল ও চালু, version catalog ও docs কমিট। **কোনোটিই এখনো যাচাই হয়নি।**

## ৬. Dependency যাচাইয়ের অবস্থা

`docs/DEPENDENCIES.md` দেখুন। সংক্ষেপে: Kotlin, KSP, Hilt, Gradle, kotlinx.serialization ও kotlinx.coroutines-এর release tag GitHub-এ পাওয়া গেছে, এবং AGP 8.13-এর release note পাওয়া গেছে। AndroidX ও Compose BOM-এর সংস্করণ এই পরিবেশে যাচাই করা যায়নি। প্রথম Actions build-ই সেটি প্রমাণ করবে।

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

Device A-র বর্তমান default Phone app-এর নাম **শুধু Telecom Testing শুরুর আগে** সংগ্রহ করা হবে। এটি Phase 1 Foundation-এর বাধা নয়।

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

কোনো build না চলায় এখনো কোনো বাগ রেকর্ড হয়নি। প্রথম build-এর ফলাফল এখানে লেখা হবে।

## ১০. পরিচিত ঝুঁকি

- Claude এই চ্যাটে compile করতে পারে না। প্রথম build-এ dependency বা সংস্করণজনিত ত্রুটি আসতে পারে। log পাঠালে ঠিক করা হবে।
- HyperOS-এ background ও pop-up সীমাবদ্ধতা T11-এ বাস্তবে পরীক্ষা হবে।
- DialMe default dialer হলে এর ত্রুটিতে কল করা যাবে না। আগের Phone app রাখতে হবে।

## ১১. পরবর্তী কাজ

1. মালিক `BUILD_INSTRUCTIONS.md` অনুযায়ী repository তৈরি করে ZIP আপলোড করবেন এবং Actions চালাবেন।
2. build সফল হলে (বা ব্যর্থ হলে পুরো log পাঠালে) Claude রিপোর্ট দেবে ও প্রয়োজনে ঠিক করবে।
3. সফল Foundation-এর রিপোর্টের পর মালিকের নির্দেশে Phase 1B শুরু।
