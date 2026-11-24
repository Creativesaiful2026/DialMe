# অবদান রাখার নিয়ম

DialMe GNU GPL v3 license-এর অধীনে। আপনার অবদানও একই license-এ যুক্ত হবে।

## মূল নীতি

- কোনো VoIP, Internet calling, analytics, বিজ্ঞাপন বা ট্র্যাকিং library নয়।
- ফোনের contacts ও call log সিস্টেমের নিজস্ব প্রোভাইডারেই থাকে। DialMe নিজের database-এ কপি করে না।
- জরুরি নম্বরের কল কখনো আটকানো বা কাস্টমাইজ করা যাবে না।
- অসম্পূর্ণ code, TODO placeholder বা ভুয়া implementation নয়।

## Pull request-এর আগে

1. `./gradlew assembleDebug testDebugUnitTest lintDebug` সফল হতে হবে। CI-তেও এটাই চলে।
2. নতুন user-facing লেখা `strings.xml` (English) ও `values-bn/strings.xml` (বাংলা) দুটোতেই যোগ করুন।
3. কোনো permission যোগ করলে কেন দরকার তা PR-এ লিখুন।
4. `CHANGELOG.md` ও `PROJECT_STATUS.md` হালনাগাদ করুন। পরীক্ষা না করে কিছুকে Passed বা Build Verified লিখবেন না।
