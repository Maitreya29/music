package com.radiant.music.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.radiant.music.R;
import com.radiant.music.adapters.bottomsheet.AccentAdapter;
import com.radiant.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.radiant.music.model.AccentsModel;
import com.radiant.music.themes.PresetColors;
import com.radiant.music.themes.ThemeManagerUtils;
import com.radiant.music.utils.AppSettings;

public class PresetAccentsChooser extends RoundedCustomBottomSheetFragment {

    public static final String TAG = PresetAccentsChooser.class.getSimpleName();
    private int newAccentId;

    @NonNull
    public static PresetAccentsChooser getInstance() {
        return new PresetAccentsChooser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_choose_preset_accents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AccentsModel[] mAccentsModels = PresetColors.getPresetColorsModel(requireContext());
        int currentId = -1;
        if (ThemeManagerUtils.isUsingPresetColors())
            currentId = AppSettings.getSelectedAccentId(requireContext());

        if (mAccentsModels.length > 0) {
            RecyclerView recyclerView = view.findViewById(R.id.accents_display_rv);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
            AccentAdapter adapter = new AccentAdapter(mAccentsModels, getLayoutInflater(), currentId, position -> {
                newAccentId = mAccentsModels[position].getId();

                if (ThemeManagerUtils.setSelectedPresetAccentColor(requireContext(), newAccentId))
                    if (null != getActivity()) {
                        ThemeManagerUtils.init(getActivity(), true);
                        getActivity().recreate();
                    }
            });
            recyclerView.setAdapter(adapter);
        }
        view.findViewById(R.id.choose_accents_cancel_btn).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.choose_accents_custom_btn).setOnClickListener(v -> {
            CustomAccentChooser customAccentChooser = CustomAccentChooser.getInstance();
            customAccentChooser.show(requireFragmentManager(), CustomAccentChooser.TAG);
            dismiss();
        });
    }
}