package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.model.AccentsModel;

public class PresetColors {

    // Pulse music accent color
    public static final int EXODUS_FRUIT = Color.parseColor("#6c5ce7");
    private static final int ELECTRON_BLUE = Color.parseColor("#0984e3");
    private static final int MINT_LEAF = Color.parseColor("#00b894");
    private static final int CHI_GONG = Color.parseColor("#d63031");
    private static final int SEI_BAR = Color.parseColor("#594031");
    private static final int CORAL = Color.parseColor("#FF7F50");
    private static final int SUNKIST = Color.parseColor("#f2994a");
    private static final int BLUE_PINK = Color.parseColor("#5F78FF");

    public static int getPresetAccentColorById(int accentId) {
        switch (accentId) {
            case Preferences.ACCENT_ELECTRON_BLUE:
                return PresetColors.ELECTRON_BLUE;
            case Preferences.ACCENT_MINT_LEAF:
                return PresetColors.MINT_LEAF;
            case Preferences.ACCENT_CHI_GONG:
                return PresetColors.CHI_GONG;
            case Preferences.ACCENT_SEI_BAR:
                return PresetColors.SEI_BAR;
            case Preferences.ACCENT_CORAL:
                return PresetColors.CORAL;
            case Preferences.ACCENT_SUNKIST:
                return PresetColors.SUNKIST;
            case Preferences.ACCENT_BLUE_PINK:
                return PresetColors.BLUE_PINK;
            default:
                return PresetColors.EXODUS_FRUIT;
        }
    }

    @NonNull
    public static AccentsModel[] getPresetColorsModel(@NonNull Context context) {
        AccentsModel[] accentsModels = new AccentsModel[8];
        accentsModels[0] = new AccentsModel(
                Preferences.ACCENT_EXODUS_FRUIT,
                context.getString(R.string.exodus_fruit),
                EXODUS_FRUIT);

        accentsModels[1] = new AccentsModel(
                Preferences.ACCENT_ELECTRON_BLUE,
                context.getString(R.string.electron_blue),
                ELECTRON_BLUE);

        accentsModels[2] = new AccentsModel(
                Preferences.ACCENT_MINT_LEAF,
                context.getString(R.string.mint_leaf),
                MINT_LEAF);

        accentsModels[3] = new AccentsModel(
                Preferences.ACCENT_CHI_GONG,
                context.getString(R.string.chi_gong),
                CHI_GONG);

        accentsModels[4] = new AccentsModel(
                Preferences.ACCENT_SEI_BAR,
                context.getString(R.string.sei_bar),
                SEI_BAR);

        accentsModels[5] = new AccentsModel(
                Preferences.ACCENT_CORAL,
                context.getString(R.string.coral),
                CORAL);

        accentsModels[6] = new AccentsModel(
                Preferences.ACCENT_SUNKIST,
                context.getString(R.string.sunkist),
                SUNKIST);

        accentsModels[7] = new AccentsModel(
                Preferences.ACCENT_BLUE_PINK,
                context.getString(R.string.blue_pink),
                BLUE_PINK);

        return accentsModels;
    }
}
