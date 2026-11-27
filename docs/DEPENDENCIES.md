# DialMe — Dependency ও সংস্করণ

সব সংস্করণ `gradle/libs.versions.toml`-এ এক জায়গায়। `assembleDebug` GitHub Actions-এ সফল (মালিকের রিপোর্ট, 2026-11-25), তাই নিচের সব সংস্করণ resolve হয়েছে।

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
| Compose BOM | 2025.09.01 | build-এ resolve হয়েছে |
| androidx.core:core-ktx | 1.17.0 | build-এ resolve হয়েছে |
| androidx.activity:activity-compose | 1.11.0 | build-এ resolve হয়েছে |
| androidx.lifecycle (runtime-compose) | 2.9.4 | build-এ resolve হয়েছে |
| androidx.navigation:navigation-compose | 2.9.4 | build-এ resolve হয়েছে |
| JUnit | 4.13.2 | — |
| androidx.datastore:datastore-preferences | 1.2.0 | developer.android.com-এর release পাতায় stable (2025-11-19)। CI-তে resolve হয়নি। নোট: 1.3.0 stable (2026-10-21) আছে, কিন্তু নতুন lifecycle টেনে আনতে পারে বলে রক্ষণশীলভাবে 1.2.0 |
| androidx.hilt:hilt-navigation-compose | 1.3.0 | release পাতায় stable (2025-09-10; Kotlin 2.0 ও KSP2 সমর্থন)। CI-তে resolve হয়নি |

Claude-এর পরিবেশ Google-এর Maven repository-তে পৌঁছাতে পারে না, তাই এই সংস্করণগুলো আগে যাচাই করা যায়নি। এখন Actions build সফল হওয়ায় সেটি নিশ্চিত।

## ইচ্ছাকৃতভাবে বাদ

- `androidx.core.telecom` (VoIP অ্যাপের জন্য)।
- Analytics, crash-reporting ও বিজ্ঞাপন library।
- Room, WorkManager (পরের Batch-এ, দরকার হলে)।
