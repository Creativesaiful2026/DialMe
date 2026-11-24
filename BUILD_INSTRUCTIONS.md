# DialMe — ফোন থেকে Build করার নির্দেশনা

PC বা Android Studio লাগবে না। শুধু ফোনের ব্রাউজার ও GitHub অ্যাকাউন্ট।

## ক. একবারের প্রস্তুতি

1. GitHub-এ নতুন **Public** repository বানান, নাম `DialMe`। "Add a README file" চালু রাখুন (যাতে `main` branch তৈরি হয়)।
2. Repository-র **Settings → Actions → General → Workflow permissions**-এ **Read and write permissions** বেছে Save করুন।
3. হাতে দুটি workflow ফাইল তৈরি করুন:
   1. Repository-তে **Add file → Create new file**।
   2. নামের ঘরে লিখুন `.github/workflows/ci.yml` (স্ল্যাশ লিখলে ফোল্ডার নিজে তৈরি হয়)।
   3. `ci.yml` ফাইলের পুরো লেখা কপি করে পেস্ট করুন। **Commit changes** চাপুন।
   4. একইভাবে `.github/workflows/inbox-unzip.yml` তৈরি করে `inbox-unzip.yml`-এর লেখা পেস্ট করুন।

## খ. প্রজেক্ট আপলোড

1. **Add file → Upload files** চাপুন। `inbox-phase1-foundation.zip` ফাইলটি **repository-র root-এ** আপলোড করে Commit করুন। নাম বদলাবেন না। `inbox-` দিয়ে শুরু হতে হবে।
2. **Actions** ট্যাবে **Inbox Unzip** চলবে। ZIP খুলে ফাইলগুলো commit করবে, ZIP মুছে ফেলবে এবং **CI** চালু করবে।
3. `403` বা push ব্যর্থ হলে ধাপ ক-২ আবার দেখুন।
4. **CI** নিজে থেকে না চললে: Actions → **CI** → **Run workflow**।

## গ. APK নামানো

1. Actions → সর্বশেষ **CI** run খুলুন।
2. সফল হলে নিচে **Artifacts**-এ `DialMe-debug-<সংখ্যা>` পাবেন। নামান, ফাইল ম্যানেজারে খুলুন (ZIP-এর ভেতরে `.apk`)।
3. ইনস্টল করুন। "Install unknown apps" এবং Xiaomi-র নিরাপত্তা স্ক্যানের সতর্কতা আসতে পারে। কী দেখলেন লিখে রাখুন।
4. একই signature থাকায় নতুন APK আগেরটির উপর আপডেট হিসেবে ইনস্টল হবে।

## ঘ. ব্যর্থ হলে

1. ব্যর্থ run খুলে লাল চিহ্নের ধাপ চাপুন।
2. ত্রুটির লেখাসহ ওই ধাপের পুরো log কপি করুন। বা run-এর পাতায় ⚙ মেনু → **Download log archive**।
3. Claude-কে পাঠান। কোনো কিছু অনুমান করে ঠিক করা হবে না, log পড়ে মূল কারণ খোঁজা হবে।

## ঙ. অ্যাপ চালানোর পর

Settings → **View app log**-এ লগ দেখা ও share করা যায়। কোনো crash হলে সেটিও এখানে থাকে।

## সতর্কতা

- এই ধাপে DialMe default dialer হয় না এবং কল করে না। আপনার ফোনের আগের Phone app অপরিবর্তিত থাকে।
- `keystore/dialme-debug.jks` একটি **public debug key**। গোপন নয়, শুধু debug build-এর জন্য। এটি দিয়ে কোনো release APK সই করবেন না।
