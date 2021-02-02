package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.model.AccentsModel;

public class PresetColors {

    // Pulse music accent color
    public static final int SLATE_BLUE = Color.parseColor("#6c5ce7");
    private static final int AZURE_RADIANCE = Color.parseColor("#0984e3");
    private static final int PERSIAN_MINT = Color.parseColor("#00b894");
    private static final int VALENCIA = Color.parseColor("#d63031");
    private static final int MILLBROOK = Color.parseColor("#594031");
    private static final int CORAL = Color.parseColor("#FF7F50");
    private static final int SUNKIST = Color.parseColor("#f2994a");
    private static final int CORNFLOUR_BLUE = Color.parseColor("#5F78FF");

    public static int getPresetAccentColorById(int accentId) {
        switch (accentId) {
            case Preferences.ACCENT_AZURE_RADIANCE:
                return PresetColors.AZURE_RADIANCE;
            case Preferences.ACCENT_PERSIAN_MINT:
                return PresetColors.PERSIAN_MINT;
            case Preferences.ACCENT_VALENCIA:
                return PresetColors.VALENCIA;
            case Preferences.ACCENT_MILLBROOK:
                return PresetColors.MILLBROOK;
            case Preferences.ACCENT_CORAL:
                return PresetColors.CORAL;
            case Preferences.ACCENT_SUNKIST:
                return PresetColors.SUNKIST;
            case Preferences.ACCENT_CORNFLOUR_BLUE:
                return PresetColors.CORNFLOUR_BLUE;
            default:
                return PresetColors.SLATE_BLUE;
        }
    }

    @NonNull
    public static AccentsModel[] getPresetColorsModel(@NonNull Context context) {
        AccentsModel[] accentsModels = new AccentsModel[8];
        accentsModels[0] = new AccentsModel(
                Preferences.ACCENT_SLATE_BLUE,
                context.getString(R.string.exodus_fruit),
                SLATE_BLUE);

        accentsModels[1] = new AccentsModel(
                Preferences.ACCENT_AZURE_RADIANCE,
                context.getString(R.string.electron_blue),
                AZURE_RADIANCE);

        accentsModels[2] = new AccentsModel(
                Preferences.ACCENT_PERSIAN_MINT,
                context.getString(R.string.mint_leaf),
                PERSIAN_MINT);

        accentsModels[3] = new AccentsModel(
                Preferences.ACCENT_VALENCIA,
                context.getString(R.string.chi_gong),
                VALENCIA);

        accentsModels[4] = new AccentsModel(
                Preferences.ACCENT_MILLBROOK,
                context.getString(R.string.sei_bar),
                MILLBROOK);

        accentsModels[5] = new AccentsModel(
                Preferences.ACCENT_CORAL,
                context.getString(R.string.coral),
                CORAL);

        accentsModels[6] = new AccentsModel(
                Preferences.ACCENT_SUNKIST,
                context.getString(R.string.sunkist),
                SUNKIST);

        accentsModels[7] = new AccentsModel(
                Preferences.ACCENT_CORNFLOUR_BLUE,
                context.getString(R.string.blue_pink),
                CORNFLOUR_BLUE);

        return accentsModels;
    }
}