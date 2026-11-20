# NFC Link Açıcı

NFC kart okutulunca Instagram, TikTok, Facebook, WhatsApp ve Telegram linklerini
tarayıcıya (Opera vb.) uğramadan **doğrudan uygulamada** açan küçük bir Android uygulaması.
Sadece kurulduğu telefonu etkiler, karta dokunmaz.

## Kurulum
1. **Releases** bölümünden en son `NFC-Link-Acici.apk` dosyasını telefona indir.
2. Aç, "Bilinmeyen kaynaklardan yükleme" izni sorarsa izin ver ve kur.
3. Uygulamayı bir kere aç (bilgi ekranı NFC ve yüklü uygulamaları gösterir).
4. Kartı okut. "Şununla aç" menüsü çıkarsa **NFC Link Açıcı** → **Her zaman**.

## Nasıl çalışır
Android, NFC'den gelen linki `NDEF_DISCOVERED` olarak dağıtır. Uygulama bu linkleri
sadece belirli alan adları için yakalar, uygun uygulamaya `setPackage` ile gönderir.
Uygulama yüklü değilse link normal tarayıcıda açılır.

Yeni platform eklemek için: `Platforms.java` ve `AndroidManifest.xml` içindeki host listesi.

## Derleme
Her `main` push'unda GitHub Actions APK'yı derler ve Release olarak yayınlar.
