package com.anormal.nfcacici;

import android.content.pm.PackageManager;

import java.util.Locale;

/** Link adresine göre hangi uygulamanın açılacağını belirler. */
final class Platforms {

    static final class Platform {
        final String name;
        final String[] hosts;
        final String[] packages; // öncelik sırasına göre

        Platform(String name, String[] hosts, String[] packages) {
            this.name = name;
            this.hosts = hosts;
            this.packages = packages;
        }
    }

    static final Platform[] ALL = {
            new Platform("Instagram",
                    new String[]{"instagram.com", "instagr.am"},
                    new String[]{"com.instagram.android", "com.instagram.lite"}),
            new Platform("TikTok",
                    new String[]{"tiktok.com"},
                    new String[]{"com.zhiliaoapp.musically", "com.ss.android.ugc.trill",
                            "com.zhiliaoapp.musically.go"}),
            new Platform("Facebook",
                    new String[]{"facebook.com", "fb.com", "fb.me", "fb.watch"},
                    new String[]{"com.facebook.katana", "com.facebook.lite"}),
            new Platform("WhatsApp",
                    new String[]{"wa.me", "whatsapp.com"},
                    new String[]{"com.whatsapp", "com.whatsapp.w4b"}),
            new Platform("Telegram",
                    new String[]{"t.me", "telegram.me", "telegram.dog"},
                    new String[]{"org.telegram.messenger", "org.telegram.messenger.web",
                            "org.thunderdog.challegram"}),
    };

    private Platforms() {}

    /** "vm.tiktok.com" gibi alt alan adlarını da yakalar. */
    static Platform forHost(String host) {
        if (host == null) return null;
        String h = host.toLowerCase(Locale.ROOT);
        for (Platform p : ALL) {
            for (String base : p.hosts) {
                if (h.equals(base) || h.endsWith("." + base)) return p;
            }
        }
        return null;
    }

    static boolean isInstalled(PackageManager pm, String pkg) {
        try {
            pm.getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
