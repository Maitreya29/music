package com.hardcodecoder.pulsemusic.fragments.main.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class PulseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.postOnAnimation(() -> setUpContent(view));
    }

    public abstract String getFragmentTitle(@NonNull Context context);

    public abstract void setUpContent(@NonNull View view);
}