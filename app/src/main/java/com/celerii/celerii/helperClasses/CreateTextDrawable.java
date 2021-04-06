package com.celerii.celerii.helperClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.celerii.celerii.R;

import java.util.concurrent.ThreadLocalRandom;

public class CreateTextDrawable {
    public static Drawable createTextDrawable(Context context, String text) {
        int color;
        String letter = String.valueOf(text.charAt(0));
        final int randomNum = ThreadLocalRandom.current().nextInt(0, 8 + 1);

        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);

        if (randomNum == 0) { color = R.color.colorPrimaryPurple; }
        else if (randomNum == 1) { color = R.color.accent; }
        else if (randomNum == 2) { color = R.color.colorInstagramBlue; }
        else if (randomNum == 3) { color = R.color.colorTealGreen; }
        else if (randomNum == 4) { color = R.color.colorKilogarmOrange; }
        else if (randomNum == 5) { color = R.color.colorKilogarmYellow; }
        else if (randomNum == 6) { color = R.color.colorTealGreen; }
        else if (randomNum == 7) { color = R.color.colorDarkGray; }
        else { color = R.color.colorAccentSecondary; }

        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(60)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, color));

        return textDrawable;
    }

    public static Drawable createTextDrawable(Context context, String text, int fontSize) {
        int color;
        String letter = String.valueOf(text.charAt(0));
        final int randomNum = ThreadLocalRandom.current().nextInt(0, 8 + 1);

        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);

        if (randomNum == 0) { color = R.color.colorPrimaryPurple; }
        else if (randomNum == 1) { color = R.color.accent; }
        else if (randomNum == 2) { color = R.color.colorInstagramBlue; }
        else if (randomNum == 3) { color = R.color.colorTealGreen; }
        else if (randomNum == 4) { color = R.color.colorKilogarmOrange; }
        else if (randomNum == 5) { color = R.color.colorKilogarmYellow; }
        else if (randomNum == 6) { color = R.color.colorTealGreen; }
        else if (randomNum == 7) { color = R.color.colorDarkGray; }
        else { color = R.color.colorAccentSecondary; }

        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(fontSize)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, color));

        return textDrawable;
    }

    public static Drawable createTextDrawable(Context context, String text, String textTwo) {
        int color;
        String letter = String.valueOf(text.charAt(0)) + String.valueOf(textTwo.charAt(0));
        final int randomNum = ThreadLocalRandom.current().nextInt(0, 8 + 1);

        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);

        if (randomNum == 0) { color = R.color.colorPrimaryPurple; }
        else if (randomNum == 1) { color = R.color.accent; }
        else if (randomNum == 2) { color = R.color.colorInstagramBlue; }
        else if (randomNum == 3) { color = R.color.colorTealGreen; }
        else if (randomNum == 4) { color = R.color.colorKilogarmOrange; }
        else if (randomNum == 5) { color = R.color.colorKilogarmYellow; }
        else if (randomNum == 6) { color = R.color.colorTealGreen; }
        else if (randomNum == 7) { color = R.color.colorDarkGray; }
        else { color = R.color.colorAccentSecondary; }

        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(60)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, color));

        return textDrawable;
    }

    public static Drawable createTextDrawable(Context context, String text, String textTwo, int fontSize) {
        int color;
        String letter = String.valueOf(text.charAt(0)) + String.valueOf(textTwo.charAt(0));
        final int randomNum = ThreadLocalRandom.current().nextInt(0, 8 + 1);

        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);

        if (randomNum == 0) { color = R.color.colorPrimaryPurple; }
        else if (randomNum == 1) { color = R.color.accent; }
        else if (randomNum == 2) { color = R.color.colorInstagramBlue; }
        else if (randomNum == 3) { color = R.color.colorTealGreen; }
        else if (randomNum == 4) { color = R.color.colorKilogarmOrange; }
        else if (randomNum == 5) { color = R.color.colorKilogarmYellow; }
        else if (randomNum == 6) { color = R.color.colorButtonGreen; }
        else if (randomNum == 7) { color = R.color.colorDarkGray; }
        else { color = R.color.colorAccentSecondary; }

        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(fontSize)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, color));

        return textDrawable;
    }

    public static Drawable createTextDrawableTransparent(Context context, String text) {
        String letter = String.valueOf(text.charAt(0));
        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);
        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(60)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, R.color.colorTransparent));

        return textDrawable;
    }

    public static Drawable createTextDrawableTransparent(Context context, String text, int fontSize) {
        String letter = String.valueOf(text.charAt(0));
        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);
        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(fontSize)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, R.color.colorTransparent));

        return textDrawable;
    }

    public static Drawable createTextDrawableTransparent(Context context, String text, String textTwo) {
        String letter = String.valueOf(text.charAt(0)) + String.valueOf(textTwo.charAt(0));
        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);
        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(60)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, R.color.colorTransparent));

        return textDrawable;
    }

    public static Drawable createTextDrawableTransparent(Context context, String text, String textTwo, int fontSize) {
        String letter = String.valueOf(text.charAt(0)) + String.valueOf(textTwo.charAt(0));
        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);
        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(fontSize)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, R.color.colorTransparent));

        return textDrawable;
    }

    public static Drawable createTextDrawableColor(Context context, String text, int randomNum) {
        int color;
        String letter = String.valueOf(text.charAt(0));

        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);

        if (randomNum == 0) { color = R.color.colorPrimaryPurple; }
        else if (randomNum == 1) { color = R.color.accent; }
        else if (randomNum == 2) { color = R.color.colorInstagramBlue; }
        else if (randomNum == 3) { color = R.color.colorTealGreen; }
        else if (randomNum == 4) { color = R.color.colorKilogarmOrange; }
        else if (randomNum == 5) { color = R.color.colorKilogarmYellow; }
        else if (randomNum == 6) { color = R.color.colorTealGreen; }
        else if (randomNum == 7) { color = R.color.colorDarkGray; }
        else { color = R.color.colorAccentSecondary; }

        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(60)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, color));

        return textDrawable;
    }

    public static Drawable createTextDrawableColor(Context context, String text, int fontSize, int randomNum) {
        int color;
        String letter = String.valueOf(text.charAt(0));

        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);

        if (randomNum == 0) { color = R.color.colorPrimaryPurple; }
        else if (randomNum == 1) { color = R.color.accent; }
        else if (randomNum == 2) { color = R.color.colorInstagramBlue; }
        else if (randomNum == 3) { color = R.color.colorTealGreen; }
        else if (randomNum == 4) { color = R.color.colorKilogarmOrange; }
        else if (randomNum == 5) { color = R.color.colorKilogarmYellow; }
        else if (randomNum == 6) { color = R.color.colorTealGreen; }
        else if (randomNum == 7) { color = R.color.colorDarkGray; }
        else { color = R.color.colorAccentSecondary; }

        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(fontSize)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, color));

        return textDrawable;
    }

    public static Drawable createTextDrawableColor(Context context, String text, String textTwo, int randomNum) {
        int color;
        String letter = String.valueOf(text.charAt(0)) + String.valueOf(textTwo.charAt(0));

        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);

        if (randomNum == 0) { color = R.color.colorPrimaryPurple; }
        else if (randomNum == 1) { color = R.color.accent; }
        else if (randomNum == 2) { color = R.color.colorInstagramBlue; }
        else if (randomNum == 3) { color = R.color.colorTealGreen; }
        else if (randomNum == 4) { color = R.color.colorKilogarmOrange; }
        else if (randomNum == 5) { color = R.color.colorKilogarmYellow; }
        else if (randomNum == 6) { color = R.color.colorTealGreen; }
        else if (randomNum == 7) { color = R.color.colorDarkGray; }
        else { color = R.color.colorAccentSecondary; }

        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(60)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, color));

        return textDrawable;
    }

    public static Drawable createTextDrawableColor(Context context, String text, String textTwo, int fontSize, int randomNum) {
        int color;
        String letter = String.valueOf(text.charAt(0)) + String.valueOf(textTwo.charAt(0));

        Typeface font = ResourcesCompat.getFont(context, R.font.fractul_alt_regular);

        if (randomNum == 0) { color = R.color.colorPrimaryPurple; }
        else if (randomNum == 1) { color = R.color.accent; }
        else if (randomNum == 2) { color = R.color.colorInstagramBlue; }
        else if (randomNum == 3) { color = R.color.colorTealGreen; }
        else if (randomNum == 4) { color = R.color.colorKilogarmOrange; }
        else if (randomNum == 5) { color = R.color.colorKilogarmYellow; }
        else if (randomNum == 6) { color = R.color.colorButtonGreen; }
        else if (randomNum == 7) { color = R.color.colorDarkGray; }
        else { color = R.color.colorAccentSecondary; }

        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .useFont(font)
                .fontSize(fontSize)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, ContextCompat.getColor(context, color));

        return textDrawable;
    }
}
