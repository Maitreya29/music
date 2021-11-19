package com.radiant.music.fragments.main.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.R;

public abstract class CardGridFragment extends PMBGridFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initialize();
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }
}