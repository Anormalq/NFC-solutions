package com.anormal.nfcacici;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ScrollView;
import android.widget.TextView;

/** Bilgi ekranı: hangi uygulamaların bulunduğunu ve NFC durumunu gösterir. */
public class MainActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();

        PackageManager pm = getPackageManager();
        StringBuilder sb = new StringBuilder();
        sb.append("NFC Link Açıcı çalışıyor ✅\n\n");
        sb.append("NFC kartı okuttuğunda Instagram, TikTok, Facebook, WhatsApp ve Telegram ")
          .append("linkleri tarayıcıya uğramadan doğrudan uygulamada açılır.\n\n");

        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc == null) {
            sb.append("⚠️ Bu telefonda NFC yok.\n\n");
        } else if (!nfc.isEnabled()) {
            sb.append("⚠️ NFC kapalı. Ayarlardan NFC'yi aç.\n\n");
        } else {
            sb.append("NFC açık ✅\n\n");
        }

        sb.append("Bulunan uygulamalar:\n");
        for (Platforms.Platform p : Platforms.ALL) {
            String found = null;
            for (String pkg : p.packages) {
                if (Platforms.isInstalled(pm, pkg)) { found = pkg; break; }
            }
            sb.append(found != null ? "✅ " : "❌ ").append(p.name);
            if (found == null) sb.append(" (yüklü değil, tarayıcıda açılır)");
            sb.append('\n');
        }

        sb.append("\nİlk okutmada \"Şununla aç\" menüsü çıkarsa ")
          .append("\"NFC Link Açıcı\"yı seçip \"Her zaman\"a bas.\n\n")
          .append("Bu ekranı kapatabilirsin, uygulama arka planda çalışmak zorunda değil.");

        TextView tv = new TextView(this);
        tv.setText(sb.toString());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                getResources().getDisplayMetrics());
        tv.setPadding(pad, pad, pad, pad);

        ScrollView sv = new ScrollView(this);
        sv.addView(tv);
        setContentView(sv);
    }
}
