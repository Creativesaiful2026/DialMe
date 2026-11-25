# DialMe — Telecom Spike (Phase 1B) নকশা

**অবস্থা:** সোর্স Implemented। এখনো Build Verified নয়, Device Tested নয়। কোনো আচরণ বাস্তবে দেখা হয়নি, নিচের "প্রত্যাশা" মানে নকশার উদ্দেশ্য, প্রমাণিত ফল নয়।

## উদ্দেশ্য

DialMe default Phone app হতে পারে কিনা, এবং বাস্তব SIM দিয়ে কল করা, কল ধরা, reject, শেষ করা, mute, speaker/audio route বদলানো যায় কিনা, তা Device A-তে যাচাই করা। UI ইচ্ছাকৃতভাবে সাদামাটা।

## কাঠামো

| অংশ | কাজ |
|---|---|
| `:core:telecom` | Telecom-এর সাথে সব যোগাযোগ। `DialMeInCallService`, `CallManager`, `CallPlacer`, `SimAccountProvider`, `DialerRole`, `TelecomStatus`, notification |
| `:feature:incall` | `InCallActivity` ও সাদামাটা `InCallScreen` |
| `:app` | Settings → Telecom spike (পরীক্ষামূলক) স্ক্রিন (`TelecomSpikeScreen`), `tel:` ও `ACTION_DIAL` intent filter |

`CallManager` একটি process-wide `object`। সিস্টেম `DialMeInCallService` বানায়, তাই Hilt দিয়ে inject করা যায় না। UI শুধু `CallManager.calls` ও `CallManager.audio` (StateFlow) পড়ে এবং `answer`, `reject`, `disconnect`, `hold`, `setMuted`, `setAudioRoute` ডাকে। UI কখনো `android.telecom.Call` ছোঁয় না।

## Default Phone app হওয়ার শর্ত (manifest-এ আছে)

- `MainActivity`-তে `ACTION_DIAL` (data ছাড়া এবং `tel:` সহ) ও `ACTION_VIEW` (`tel:`) intent filter।
- `DialMeInCallService`: `BIND_INCALL_SERVICE` permission ও `android.telecom.IN_CALL_SERVICE_UI` metadata।
- Role চাওয়া হয় `RoleManager.createRequestRoleIntent(ROLE_DIALER)` দিয়ে।

## কলের প্রবাহ

- **Outgoing:** `CallPlacer.placeCall` → `TelecomManager.placeCall` (SIM বাছাই করলে `EXTRA_PHONE_ACCOUNT_HANDLE` সহ) → সিস্টেম service-কে bind করে → `onCallAdded` → `CallManager` → service সরাসরি `InCallActivity` খোলার চেষ্টা করে।
- **Incoming:** `onCallAdded` → incoming notification (full-screen intent ও Answer/Decline বোতামসহ)। ringtone বাজায় Telecom নিজে।
- **Call log:** Telecom নিজে লেখে, DialMe দ্বিগুণ লেখে না।

## অনিশ্চিত বিষয় (T-পরীক্ষায় দেখা হবে, অনুমান নয়)

1. Outgoing কলে service থেকে সরাসরি activity খোলা background-launch নিয়মে আটকাতে পারে (HyperOS-এ আরও কঠোর)। ব্যর্থ হলে লগে "Could not open the in-call screen directly" আসবে এবং ongoing notification থেকে খোলা যাবে।
2. Android 14-এ full-screen call notification-এর অনুমতি আলাদা সেটিং হতে পারে। Spike স্ক্রিন এটি দেখায় (`canUseFullScreenIntent`)।
3. SIM slot নম্বর (SIM 1/SIM 2) বের করা হয় `createForPhoneAccountHandle` → subscription id → slot দিয়ে। কোনো ধাপ ব্যর্থ হলে শুধু carrier-এর নাম দেখাবে।
4. Audio route-এ `CallAudioState` ব্যবহার হয়েছে, যা Android 14-এ deprecated কিন্তু ন্যূনতম ঝুঁকির জন্য বেছে নেওয়া হয়েছে। Android 14 ফোনে কাজ করে কিনা পরীক্ষা-সাপেক্ষ।
5. Incoming UI এখনো app-wide theme পায় না (ডিফল্ট theme)। Phase 7-এ ঠিক হবে।

## গোপনীয়তা

লগে ফোন নম্বর পূর্ণ লেখা হয় না। `DialNumbers.mask` শেষের দুই অঙ্ক রেখে বাকি `*` করে। Spike স্ক্রিন SIM-এর নিজের নম্বর দেখায় না। তবে notification ও in-call স্ক্রিনে কলার নম্বর দেখানো হয় (সেটাই কাজ)।

## লগ কীভাবে পড়বেন

Settings → View app log। গুরুত্বপূর্ণ tag: `TelecomSpike` (অবস্থা), `CallPlacer`, `CallManager` (call added/status বদল/action), `CallNotifier`, `InCall`। Call-এর প্রতিটি অবস্থা বদল এভাবে লেখা হয়: `Call 1: DIALING -> ACTIVE (OUTGOING, SIM 2, *********78)`।
