# DialMe — স্থাপত্যের সারসংক্ষেপ

(পূর্ণ Phase 0 Technical Architecture ডকুমেন্টের সংক্ষিপ্ত রূপ। এই ফাইলে শুধু বাস্তবে যা আছে তা বর্ণিত।)

## স্তর

Compose UI → ViewModel (`StateFlow`) → Repository interface (`:core:model`) → implementation (`:core:data`)।

## Module

| Module | বিষয় |
|---|---|
| `:app` | Application, `MainActivity`, navigation, Settings ও Diagnostics স্ক্রিন, Hilt wiring |
| `:core:model` | `ThemeSpec`, `ThemeCodec`, `ThemeRepository` (শুধু Kotlin কোড, UI নির্ভরতা নেই) |
| `:core:common` | `DialMeLog`, `RotatingLogFile`, `CrashHandler` |
| `:core:designsystem` | `DialMeTheme`, colour scheme, shapes, typography scaling, spacing |
| `:core:telecom` | (Phase 1B) `InCallService`, `CallManager`, call placing, SIM list, default dialer role, notification |
| `:core:data` | (Batch 1) Contacts ও Call log পড়া (read only), T9 ও সার্চ, কল-গ্রুপিং, `DataStoreThemeRepository` |
| `:feature:incall` | (Phase 1B) `InCallActivity` ও সাদামাটা call স্ক্রিন |

প্রধান স্ক্রিন (Dialpad, Contacts, Favorites, Recents) এখন `:app`-এর `ui/` প্যাকেজে আছে। আলাদা `:feature:*` মডিউলে সরানো পরে, দরকার হলে।

নির্ভরতার নিয়ম: feature → core; feature থেকে feature নয়; `:app` সব জোড়া লাগায়।

## Theme system

`ThemeSpec` একটি serializable data class। রঙ, আকৃতি, spacing, font scale, animation speed, button shape তাতে থাকে। `ThemeCodec` JSON পড়া ও লেখার সময় সব মান সীমার মধ্যে আনে (`sanitized()`), তাই আমদানি করা ফাইল UI ভাঙতে পারে না। `DialMeTheme` তা Material 3 theme ও `CompositionLocal`-এ রূপান্তর করে। থিম এখন `DataStoreThemeRepository`-তে সংরক্ষিত (Batch 1; CI-তে এখনো চলেনি)।

## Diagnostics

PC ছাড়া সমস্যা ধরার জন্য `DialMeLog` Logcat ও অ্যাপের ব্যক্তিগত স্টোরেজের ফাইলে লেখে (সর্বোচ্চ প্রায় 512 KB, তারপর rotate)। `CrashHandler` অচেনা exception লগে লিখে তারপর সিস্টেমের handler-কে দেয়। Settings থেকে লগ দেখা ও share করা যায়।

## Telecom

Phase 1B-তে সোর্স লেখা হয়েছে (Build Verified নয়)। বিস্তারিত: `docs/TELECOM.md`।
