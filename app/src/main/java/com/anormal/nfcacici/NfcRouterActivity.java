package com.anormal.nfcacici;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

/**
 * NFC kart okununca Android bu ekranı açar. Ekran görünmez:
 * linki alır, doğru uygulamaya (Instagram, TikTok, ...) yollar ve hemen kapanır.
 * Uygulama yüklü değilse linki normal tarayıcıda açar.
 */
public class NfcRouterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = extractUri(getIntent());
        if (uri != null) {
            route(uri);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    private Uri extractUri(Intent intent) {
        if (intent == null) return null;
        if (intent.getData() != null) return intent.getData();

        Parcelable[] raw;
        if (Build.VERSION.SDK_INT >= 33) {
            raw = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage.class);
        } else {
            raw = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        }
        if (raw == null) return null;
        for (Parcelable p : raw) {
            for (NdefRecord r : ((NdefMessage) p).getRecords()) {
                Uri u = r.toUri();
                if (u != null) return u;
            }
        }
        return null;
    }

    private void route(Uri uri) {
        // http -> https (uygulamalar https linklerini daha iyi tanıyor)
        if ("http".equalsIgnoreCase(uri.getScheme())) {
            uri = uri.buildUpon().scheme("https").build();
        }

        PackageManager pm = getPackageManager();
        Platforms.Platform platform = Platforms.forHost(uri.getHost());

        if (platform != null) {
            for (String pkg : platform.packages) {
                if (!Platforms.isInstalled(pm, pkg)) continue;

                // 1) Linki doğrudan uygulamanın içinde aç (profil, sohbet vb.)
                Intent view = new Intent(Intent.ACTION_VIEW, uri)
                        .setPackage(pkg)
                        .addCategory(Intent.CATEGORY_BROWSABLE)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (tryStart(view)) return;

                // 2) Olmazsa en azından uygulamayı aç
                Intent launch = pm.getLaunchIntentForPackage(pkg);
                if (launch != null) {
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (tryStart(launch)) return;
                }
            }
        }

        // 3) Uygulama yoksa: normal tarayıcıda aç
        Intent browser = new Intent(Intent.ACTION_VIEW, uri)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!tryStart(browser)) {
            Toast.makeText(this, "Link açılamadı: " + uri, Toast.LENGTH_LONG).show();
        }
    }

    private boolean tryStart(Intent intent) {
        try {
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException | SecurityException e) {
            return false;
        }
    }
}
