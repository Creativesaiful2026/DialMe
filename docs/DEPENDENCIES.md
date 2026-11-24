# DialMe — Dependency ও সংস্করণ

সব সংস্করণ `gradle/libs.versions.toml`-এ এক জায়গায়। **Build Verified: না** (এখনো GitHub Actions-এ চালানো হয়নি)।

## Build

| উপাদান | সংস্করণ | যাচাইয়ের অবস্থা |
|---|---|---|
| Gradle | 8.14.3 | release tag GitHub-এ পাওয়া গেছে |
| Android Gradle Plugin | 8.13.2 | AGP 8.13 release note দেখা গেছে (ন্যূনতম Gradle 8.13, JDK 17, API 36 সমর্থন) |
| Kotlin | 2.2.20 | release tag GitHub-এ পাওয়া গেছে |
| KSP | 2.2.20-2.0.3 | release tag GitHub-এ পাওয়া গেছে |
| Hilt (Dagger) | 2.57.1 | release tag GitHub-এ পাওয়া গেছে |
| JDK (CI) | Temurin 17 | — |

## Runtime

| Library | সংস্করণ | যাচাইয়ের অবস্থা |
|---|---|---|
| kotlinx.coroutines | 1.10.2 | release tag পাওয়া গেছে |
| kotlinx.serialization | 1.9.0 | release tag পাওয়া গেছে |
| Compose BOM | 2025.09.01 | **যাচাই করা যায়নি** |
| androidx.core:core-ktx | 1.17.0 | **যাচাই করা যায়নি** |
| androidx.activity:activity-compose | 1.11.0 | **যাচাই করা যায়নি** |
| androidx.lifecycle (runtime-compose) | 2.9.4 | **যাচাই করা যায়নি** |
| androidx.navigation:navigation-compose | 2.9.4 | **যাচাই করা যায়নি** |
| JUnit | 4.13.2 | — |

"যাচাই করা যায়নি" মানে এই পরিবেশ Google-এর Maven repository-তে পৌঁছাতে পারে না। প্রথম Actions build-এ সংস্করণ না পাওয়া গেলে "Could not find ..." ত্রুটি আসবে, তখন শুধু সংশ্লিষ্ট সংস্করণ ঠিক করা হবে।

## ইচ্ছাকৃতভাবে বাদ

- `androidx.core.telecom` (VoIP অ্যাপের জন্য)।
- Analytics, crash-reporting ও বিজ্ঞাপন library।
- `hilt-navigation-compose` (Phase 1-এ দরকার নেই)।
- Room, DataStore, WorkManager (Phase 2 ও পরে)।
