package com.hardcodecoder.pulsemusic.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;

import java.util.Objects;

public class CustomAccentChooserDialogFragment extends RoundedBottomSheetDialogFragment {

    public static final String TAG = CustomAccentChooserDialogFragment.class.getSimpleName();
    private TextInputEditText mHexCode;
    private ImageView mColorPreview;
    private Slider[] mSliders;
    private int mSelectedColor;

    public static CustomAccentChooserDialogFragment getInstance() {
        return new CustomAccentChooserDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_custom_accent_chooser, container, false);
    }

    private void compileColor() {
        int r = Math.round(mSliders[0].getValue());
        int g = Math.round(mSliders[1].getValue());
        int b = Math.round(mSliders[2].getValue());

        int rgb = Color.rgb(r, g, b);
        if (mSelectedColor != rgb) {
            mSelectedColor = rgb;
            updateColorCodeAndPreview();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextInputLayout hexCodeLayout = view.findViewById(R.id.hex_code_layout);
        mHexCode = view.findViewById(R.id.hex_code);
        mColorPreview = view.findViewById(R.id.color_preview);
        mSliders = new Slider[]{view.findViewById(R.id.red_slider), view.findViewById(R.id.green_slider), view.findViewById(R.id.blue_slider)};

        // Initialize current accent color
        // Make sure to initialize before setting up listeners
        mSelectedColor = ThemeColors.getSelectedAccentColor();
        updateSliders();
        updateColorCodeAndPreview();

        // Add sliders' OnChangeListeners
        for (Slider hexSlider : mSliders) {
            hexSlider.addOnChangeListener((slider, value, fromUser) -> {
                if (fromUser) compileColor();
            });
        }

        // Add int label formatter
        for (Slider hexSlider : mSliders) {
            hexSlider.setLabelFormatter(value -> String.valueOf(Math.round(value)));
        }

        // Hex text field
        mHexCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (/*!s.toString().startsWith("#") ||*/ s.length() < 6) {
                    // Don't try to parse color if incomplete hex code is passed
                    hexCodeLayout.setError(getString(R.string.hex_color_error));
                    return;
                }
                hexCodeLayout.setError(null);
                try {
                    String hex = "#".concat(s.toString());
                    int rgb = Color.parseColor(hex);
                    if (mSelectedColor != rgb) {
                        mSelectedColor = rgb;
                        // Update slider values to match hex code
                        updateSliders();
                    }
                    mColorPreview.getDrawable().setTint(mSelectedColor);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    hexCodeLayout.setError(getString(R.string.hex_color_error));
                }
            }
        });

        // Button sends user back to presets
        view.findViewById(R.id.custom_accents_presets_btn).setOnClickListener(v -> {
            AccentsChooserDialogFragment dialogFragment = AccentsChooserDialogFragment.getInstance();
            dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), AccentsChooserDialogFragment.TAG);
            dismiss();
        });

        // Button applies selected selected color
        view.findViewById(R.id.custom_accents_apply_btn).setOnClickListener(v -> {
            boolean needsRestart = ThemeManagerUtils.setSelectedCustomAccentColor(v.getContext(), mSelectedColor);
            if (needsRestart && null != getActivity()) {
                ThemeManagerUtils.init(getActivity(), true);
                getActivity().recreate();
            }
            dismiss();
        });
    }

    private void updateColorCodeAndPreview() {
        mHexCode.setText(String.format("%06X", (0xFFFFFF & mSelectedColor)));
        mColorPreview.getDrawable().setTint(mSelectedColor);
    }

    private void updateSliders() {
        int redChannel = Color.red(mSelectedColor);
        int greenChannel = Color.green(mSelectedColor);
        int blueChannel = Color.blue(mSelectedColor);

        mSliders[0].setValue(redChannel);
        mSliders[1].setValue(greenChannel);
        mSliders[2].setValue(blueChannel);
    }
}