package com.hardcodecoder.pulsemusic.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.AccentAdapter;
import com.hardcodecoder.pulsemusic.model.AccentsModel;
import com.hardcodecoder.pulsemusic.themes.PresetColors;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.Objects;

public class AccentsChooserDialogFragment extends RoundedBottomSheetDialogFragment {

    public static final String TAG = "AccentsChooser";
    private int newAccentId;

    public static AccentsChooserDialogFragment getInstance() {
        return new AccentsChooserDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_accents_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AccentsModel[] mAccentsModels = PresetColors.getPresetColorsModel(view.getContext());
        int currentId = -1;
        if (ThemeManagerUtils.isUsingPresetColors())
            currentId = AppSettings.getSelectedAccentId(view.getContext());

        if (mAccentsModels.length > 0) {
            RecyclerView recyclerView = view.findViewById(R.id.accents_display_rv);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
            AccentAdapter adapter = new AccentAdapter(mAccentsModels, getLayoutInflater(), currentId, position -> {
                newAccentId = mAccentsModels[position].getId();
                dismiss();
                if (ThemeManagerUtils.setSelectedPresetAccentColor(view.getContext(), newAccentId))
                    if (null != getActivity()) {
                        ThemeManagerUtils.init(getActivity());
                        getActivity().recreate();
                    }
            });
            recyclerView.setAdapter(adapter);
        }
        view.findViewById(R.id.choose_accents_cancel_btn).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.choose_accents_custom_btn).setOnClickListener(v -> {
            CustomAccentChooserDialogFragment dialogFragment = CustomAccentChooserDialogFragment.getInstance();
            dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), CustomAccentChooserDialogFragment.TAG);
            dismiss();
        });
    }
}
