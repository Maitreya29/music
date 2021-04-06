package com.nezukoos.music.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.R;
import com.nezukoos.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.nezukoos.music.utils.AppSettings;
import com.nezukoos.music.views.ValueSlider;

public class CornerRadiusChooser extends RoundedCustomBottomSheetFragment {

    public static final String TAG = CornerRadiusChooser.class.getSimpleName();

    @NonNull
    public static CornerRadiusChooser getInstance() {
        return new CornerRadiusChooser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_configure_corner_radius, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ValueSlider topLeft = view.findViewById(R.id.radius_slider_tl);
        ValueSlider topRight = view.findViewById(R.id.radius_slider_tr);
        ValueSlider bottomLeft = view.findViewById(R.id.radius_slider_bl);
        ValueSlider bottomRight = view.findViewById(R.id.radius_slider_br);

        int[] radiusValues = AppSettings.getNowPlayingAlbumCardCornerRadius(requireContext());

        topLeft.setSliderValue(radiusValues[0]);
        topRight.setSliderValue(radiusValues[1]);
        bottomLeft.setSliderValue(radiusValues[2]);
        bottomRight.setSliderValue(radiusValues[3]);

        view.findViewById(R.id.radius_changer_set_btn).setOnClickListener(v -> {
            AppSettings.setNowPlayingAlbumCardCornerRadius(
                    requireContext(),
                    topLeft.getSliderValue(),
                    topRight.getSliderValue(),
                    bottomLeft.getSliderValue(),
                    bottomRight.getSliderValue());
            dismiss();
        });
    }
}