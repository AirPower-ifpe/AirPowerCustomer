package com.ifpe.edu.br.viewmodel.util;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.ifpe.edu.br.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AirPowerUtil {

    private static String TAG = AirPowerUtil.class.getSimpleName();

    public static Drawable getDrawable(String name, Context context) {
        if (context == null) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.e(TAG, "getDrawable: context is null");
            return null;
        }
        Resources resources = context.getResources();
        Drawable drawable;
        try {
            int idDrawable = resources.getIdentifier(name, AirPowerConstants.KEY_COD_DRAWABLE,
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

    public static ProgressDialog getProgressDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;
    }

    public static ProgressDialog getProgressDialog(Context context, String message, boolean isDismissible) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(isDismissible);
        return dialog;
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


    @Deprecated
    private static void requestPermission(Context context, Activity activity) {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "requestPermission");
        final int FINE_LOCATION_REQUEST = 10;
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            boolean locationGrantedByUser = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            if (locationGrantedByUser) return;
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_REQUEST);
        } catch (Exception e) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.e(TAG, "error while getting permission");
        }
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
        return true;//BuildConfig.DEBUG;
    }


    public static void launchActivity(Context context,
                                      Class<? extends Activity> activity,
                                      Bundle options) {
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent, options);
    }
}