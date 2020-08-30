package com.hardcodecoder.pulsemusic.dialog;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.graphics.Color;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.hardcodecoder.pulsemusic.R;
import com.google.android.material.slider.Slider;

import java.util.Objects;

public class CustomAccentChooserDialogFragment extends RoundedBottomSheetDialogFragment {

    public static final String TAG = "CustomAccentChooser";
    // Create component variables
    private MaterialButton mPresets;
    private EditText mHexCode;
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

    private void createHexCode() {
        int r = Math.round(mSliders[0].getValue());
        int g = Math.round(mSliders[1].getValue());
        int b = Math.round(mSliders[2].getValue());

        int rgb = Color.rgb(r, g, b);
        mSelectedColor = rgb;
        mColorPreview.getDrawable().setColorFilter(mSelectedColor, PorterDuff.Mode.MULTIPLY);


        mHexCode.setText(String.format("#%06X", (0xFFFFFF & rgb)));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Get component paths
        mPresets = view.findViewById(R.id.custom_accents_presets_btn);
        mHexCode = view.findViewById(R.id.hex_code);
        mColorPreview = view.findViewById(R.id.color_preview);
        mSliders = new Slider[]{view.findViewById(R.id.red_slider), view.findViewById(R.id.green_slider), view.findViewById(R.id.blue_slider)};

        // Button sends user back to presets
        mPresets.setOnClickListener(v -> {
            AccentsChooserDialogFragment dialogFragment = AccentsChooserDialogFragment.getInstance();
            dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), AccentsChooserDialogFragment.TAG);
            dismiss();
        });

        // Add sliders' OnChangeListeners
        for (Slider hexSlider : mSliders) {
            hexSlider.addOnChangeListener((slider, value, fromUser) -> {
                createHexCode();
            });
        }

        // Hex text field
        // This is probably inefficient, but it's the only way I knew
        mHexCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    mSelectedColor = Color.parseColor(s.toString());
                } catch (Exception e) {
                    mHexCode.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.md_red_A400, null));
                    return;
                }
                mHexCode.setBackgroundColor(Color.argb(0, 0, 0, 0));
                mColorPreview.getDrawable().setColorFilter(mSelectedColor, PorterDuff.Mode.MULTIPLY);
            }
        });

        for (Slider hexSlider : mSliders) {
            hexSlider.setLabelFormatter(value -> String.valueOf(Math.round(value)));
        }


        createHexCode();

    }

}