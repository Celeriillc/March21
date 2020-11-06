package com.celerii.celerii.helperClasses;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.celerii.celerii.R;

/**
 * Created by DELL on 12/2/2018.
 */

public class CustomToast {
    private static Toast toast;

    public static void blueBackgroundToast(Context context, String message){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        double actionBarHeight;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        } else {
            actionBarHeight = 100;
        }
        toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, (int)actionBarHeight + 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void primaryBackgroundToast(Context context, String message){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_primary, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        double actionBarHeight;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        } else {
            actionBarHeight = 100;
        }
        toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, (int)actionBarHeight + 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void accentBackgroundToast(Context context, String message){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_accent, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        double actionBarHeight;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        } else {
            actionBarHeight = 100;
        }
        toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, (int)actionBarHeight + 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void whiteBackgroundBottomToast(Context context, String message){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_general_toast_white, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void cancelToast(){
        toast.cancel();
    }
}
