
package com.valdi.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;
import java.io.File;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(12, 15, 14);
    private final int GREEN = Color.rgb(0, 255, 120);
    private TextView ramText;
    private TextView refreshText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(25), dp(20), dp(25));

        scroll.addView(layout);
        setContentView(scroll);

        TextView title = new TextView(this);
        title.setText("VALDI TOOLS");
        title.setTextColor(GREEN);
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(15), 0, dp(5));
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("GAMING UTILITY • ANDROID 15");
        subtitle.setTextColor(Color.WHITE);
        subtitle.setTextSize(12);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, dp(25));
        layout.addView(subtitle);

        addHeading(layout, "DEVICE MONITOR");

        ramText = addInfo(layout, "RAM: Memuat...");
        refreshText = addInfo(layout, "Refresh Rate: Memuat...");

        addButton(layout, "REFRESH DEVICE INFO", () -> updateInfo());

        addHeading(layout, "GAME LAUNCHER");

        addButton(layout, "BUKA FREE FIRE", () ->
                launchGame("com.dts.freefireth"));

        addButton(layout, "BUKA FREE FIRE MAX", () ->
                launchGame("com.dts.freefiremax"));

        addHeading(layout, "DEVICE TOOLS");

        addButton(layout, "BERSIHKAN CACHE VALDI TOOLS", () -> {
            clearOwnCache();
        });

        addButton(layout, "PENGATURAN LAYAR", () -> {
            openSettings(Settings.ACTION_DISPLAY_SETTINGS);
        });

        addButton(layout, "PENGATURAN BATERAI", () -> {
            openSettings(Settings.ACTION_BATTERY_SAVER_SETTINGS);
        });

        addButton(layout, "PENGATURAN APLIKASI", () -> {
            Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName())
            );
            startActivity(intent);
        });

        addHeading(layout, "INFORMATION");

        TextView info = new TextView(this);
        info.setText(
            "VALDI TOOLS\n\n" +
            "• Membuka Free Fire dan Free Fire MAX.\n" +
            "• Menampilkan perkiraan penggunaan RAM perangkat.\n" +
            "• Menampilkan refresh rate layar.\n" +
            "• Membersihkan cache aplikasi VALDI TOOLS sendiri.\n\n" +
            "Catatan: Refresh rate layar bukan FPS game. " +
            "Pengaturan grafis dan FPS game tetap diatur melalui " +
            "pengaturan resmi di dalam game."
        );
        info.setTextColor(Color.LTGRAY);
        info.setTextSize(13);
        info.setPadding(dp(14), dp(15), dp(14), dp(15));
        info.setBackgroundColor(Color.rgb(27, 34, 30));
        layout.addView(info);

        updateInfo();
    }

    private void addHeading(LinearLayout layout, String text) {
        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextColor(GREEN);
        heading.setTextSize(16);
        heading.setPadding(0, dp(22), 0, dp(10));
        layout.addView(heading);
    }

    private TextView addInfo(LinearLayout layout, String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(Color.WHITE);
        view.setTextSize(15);
        view.setPadding(dp(14), dp(15), dp(14), dp(15));
        view.setBackgroundColor(Color.rgb(27, 34, 30));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                -1, -2
            );
        params.bottomMargin = dp(8);
        layout.addView(view, params);

        return view;
    }

    private void addButton(
        LinearLayout layout,
        String text,
        Runnable action
    ) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.BLACK);
        button.setTextSize(13);
        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(GREEN)
        );

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, dp(55));
        params.bottomMargin = dp(10);

        layout.addView(button, params);
        button.setOnClickListener(v -> action.run());
    }

    private void updateInfo() {
        ActivityManager manager =
            (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memory =
            new ActivityManager.MemoryInfo();

        manager.getMemoryInfo(memory);

        long total = memory.totalMem / (1024 * 1024);
        long available = memory.availMem / (1024 * 1024);

        ramText.setText(
            "RAM TERPAKAI (PERKIRAAN): " +
            (total - available) + " MB / " + total + " MB"
        );

        float rate = getWindowManager()
            .getDefaultDisplay()
            .getRefreshRate();

        refreshText.setText(
            "REFRESH RATE LAYAR: " + rate + " Hz"
        );
    }

    private void launchGame(String packageName) {
        Intent intent =
            getPackageManager().getLaunchIntentForPackage(packageName);

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(
                this,
                "Game belum terpasang di perangkat.",
                Toast.LENGTH_LONG
            ).show();
        }
    }

    private void clearOwnCache() {
        long before = getCacheSize(getCacheDir());
        deleteContents(getCacheDir());

        Toast.makeText(
            this,
            "Cache VALDI TOOLS dibersihkan: " +
            (before / 1024) + " KB",
            Toast.LENGTH_LONG
        ).show();
    }

    private long getCacheSize(File file) {
        long size = 0;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    size += getCacheSize(child);
                }
            }
        } else if (file.isFile()) {
            size += file.length();
        }

        return size;
    }

    private void deleteContents(File file) {
        File[] files = file.listFiles();

        if (files != null) {
            for (File child : files) {
                if (child.isDirectory()) {
                    deleteContents(child);
                }
                child.delete();
            }
        }
    }

    private void openSettings(String action) {
        try {
            startActivity(new Intent(action));
        } catch (Exception e) {
            Toast.makeText(
                this,
                "Pengaturan tidak tersedia.",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    private int dp(int value) {
        return (int) (value *
            getResources().getDisplayMetrics().density);
    }
}
