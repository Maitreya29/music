package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.adapters.TrackPickerAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewSelectorHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemSelectorListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.SearchQueryLoader;
import com.hardcodecoder.pulsemusic.themes.TintHelper;

import java.util.ArrayList;
import java.util.Stack;

public class TrackPickerActivity extends PMBActivity implements ItemSelectorListener {

    public static final String ID_PICKED_TRACKS = "picked_tracks";
    public static final int REQUEST_CODE = 100;
    private final Stack<String> pendingUpdates = new Stack<>();
    private RecyclerViewSelectorHelper mSelectorHelper;
    @Nullable
    private TrackPickerAdapter mAdapter;
    private String mQuery = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overrideActivityTransition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_picker);

        findViewById(R.id.track_picker_activity_close_btn).setOnClickListener(v -> onBackPressed());

        FloatingActionButton fab = findViewById(R.id.btn_done);
        TintHelper.setAccentTintTo(fab);
        fab.setOnClickListener(v -> {
            Intent i = new Intent();
            if (null != mAdapter)
                i.putExtra(ID_PICKED_TRACKS, new ArrayList<>(mAdapter.getSelectedTracks()));
            setResult(RESULT_OK, i);
            finishActivity(REQUEST_CODE);
            finish();
            overrideActivityTransition();
        });

        setUpSearchUi();
        displayTracks();
    }

    private void setUpSearchUi() {
        AppCompatEditText editText = findViewById(R.id.track_picker_activity_edit_text);
        TintHelper.setAccentTintToCursor(editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mAdapter) return;
                if (!mQuery.equalsIgnoreCase(s.toString())) {
                    if (s.toString().isEmpty()) {
                        mAdapter.updateItems(LoaderCache.getAllTracksList());
                    } else searchResult(s.toString());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchResult(String query) {
        mQuery = query;
        pendingUpdates.push(query);
        if (pendingUpdates.size() == 1)
            TaskRunner.executeAsync(new SearchQueryLoader(query), result -> {
                pendingUpdates.remove(0);
                if (null == result) return;
                if (mAdapter != null)
                    mAdapter.updateItems(result);

                if (pendingUpdates.size() > 0) {
                    searchResult(pendingUpdates.pop());
                    pendingUpdates.clear();
                }
            });
    }

    private void displayTracks() {
        if (null == LoaderCache.getAllTracksList()) {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        } else {
            RecyclerView recyclerView = (RecyclerView) ((ViewStub) findViewById(R.id.stub_track_picker_rv)).inflate();
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mAdapter = new TrackPickerAdapter(LoaderCache.getAllTracksList(), getLayoutInflater(), this);
            recyclerView.setAdapter(mAdapter);
            mSelectorHelper = new RecyclerViewSelectorHelper(mAdapter);
        }
    }

    @Override
    public void onItemClick(@NonNull RecyclerView.ViewHolder viewHolder, int position, boolean selected) {
        if (selected) mSelectorHelper.onItemSelected(viewHolder, position);
        else mSelectorHelper.onItemUnSelected(viewHolder, position);
    }

    private void overrideActivityTransition() {
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overrideActivityTransition();
    }
}