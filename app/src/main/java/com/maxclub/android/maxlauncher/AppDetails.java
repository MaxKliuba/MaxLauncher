package com.maxclub.android.maxlauncher;

import android.content.pm.ResolveInfo;

import java.io.File;
import java.text.DecimalFormat;

public class AppDetails {
    public static long getApkSize(ResolveInfo resolveInfo) {
        return new File(resolveInfo.activityInfo.applicationInfo.publicSourceDir).length();
    }

    public static String formatSize(long size) {
        if (size <= 0) {
            return "0 B";
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};

        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        String formattedSize = new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups));

        return formattedSize + " " + units[digitGroups];
    }
}
