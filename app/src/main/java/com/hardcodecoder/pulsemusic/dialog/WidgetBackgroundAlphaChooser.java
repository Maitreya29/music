package com.hardcodecoder.pulsemusic.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.ValueSlider;
import com.hardcodecoder.pulsemusic.widgets.PulseWidgetsHelper;

public class WidgetBackgroundAlphaChooser extends RoundedCustomBottomSheetFragment {

    public static final String TAG = WidgetBackgroundAlphaChooser.class.getSimpleName();

    @NonNull
    public static WidgetBackgroundAlphaChooser getInstance() {
        return new WidgetBackgroundAlphaChooser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_widget_background_alpha_chooser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final int currentAlphaPercent = AppSettings.getWidgetBackgroundAlpha(requireContext());

        ValueSlider alphaSlider = view.findViewById(R.id.widget_alpha_slider);
        alphaSlider.setSliderValue(currentAlphaPercent);

        view.findViewById(R.id.set_btn).setOnClickListener(v -> {
            AppSettings.setWidgetBackgroundAlpha(requireContext(), alphaSlider.getSliderValue());
            if (AppSettings.isWidgetEnable(requireContext()))
                PulseWidgetsHelper.notifyWidgets(requireContext());
            dismiss();
        });

        view.findViewById(R.id.cancel_btn).setOnClickListener(v -> dismiss());
    }
}