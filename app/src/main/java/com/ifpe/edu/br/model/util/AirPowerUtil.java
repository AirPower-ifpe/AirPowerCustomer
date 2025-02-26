package com.ifpe.edu.br.model.util;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;

import com.ifpe.edu.br.BuildConfig;
import com.ifpe.edu.br.R;
import com.ifpe.edu.br.model.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AirPowerUtil {

    private static final String TAG = AirPowerUtil.class.getSimpleName();

    public static Drawable getDrawable(String name, Context context) {
        if (context == null) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.e(TAG, "getDrawable: context is null");
            return null;
        }
        Resources resources = context.getResources();
        Drawable drawable;
        try {
            int idDrawable = resources.getIdentifier(name, Constants.KEY_COD_DRAWABLE,
                    context.getPackageName());
            drawable = ResourcesCompat.getDrawable(resources, idDrawable, null);
        } catch (Exception e) {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.w(TAG, "can't retrieve drawable resource: getting default");
            drawable = ResourcesCompat
                    .getDrawable(resources, R.drawable.app_icon, null);
        }
        return drawable;
    }

    public static String kelvinToCelsius(float temperatureKelvin) {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "kelvinToCelsius");
        float temperatureCelsius = temperatureKelvin - 273.15f;
        return String.format(Locale.getDefault(), "%.1f°C", temperatureCelsius);
    }

    public static String getCurrentDateTime() {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "getCurrentDateTime");
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    public static class Text {
        public static boolean isNullOrEmpty(String text) {
            return text == null || text.isEmpty();
        }
    }

    @Deprecated
    public interface ILocationCallback {
        void onSuccess(String localization);
    }

    public static boolean isDebugVersion() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.w(TAG, "isDebugVersion: FORCE");
        return BuildConfig.DEBUG;
    }

    public static void launchActivity(Context context,
                                      Class<? extends Activity> activity,
                                      Bundle options) {
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent, options);
    }
}