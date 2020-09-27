package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewStub;

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
import com.hardcodecoder.pulsemusic.interfaces.TrackPickerListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.SearchQueryLoader;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.themes.TintHelper;

import java.util.ArrayList;
import java.util.List;

public class TrackPickerActivity extends PMBActivity implements TrackPickerListener {

    public static final String ID_PICKED_TRACKS = "picked_tracks";
    public static final int REQUEST_CODE = 100;
    private RecyclerViewSelectorHelper mSelectorHelper;
    private List<MusicModel> mMainList;
    private String mQuery = "";
    private List<String> pendingUpdates = new ArrayList<>();
    private TrackPickerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overrideActivityTransition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_picker);

        findViewById(R.id.trackpicker_activity_close_btn).setOnClickListener(v -> onBackPressed());
        
        FloatingActionButton fab = findViewById(R.id.btn_done);
        TintHelper.setAccentTintTo(fab);
        fab.setOnClickListener(v -> {
            Intent i = new Intent();
            i.putExtra(ID_PICKED_TRACKS, new ArrayList<>(mAdapter.getSelectedTracks()));
            setResult(RESULT_OK, i);
            finishActivity(REQUEST_CODE);
            finish();
            overrideActivityTransition();
        });

        if (LoaderCache.getAllTracksList().size() > 0) {
            mMainList = new ArrayList<>(LoaderCache.getAllTracksList());
            displayTracks();
        } else {
            mMainList = new ArrayList<>();
        }
        setUpSearchUi();
    }

    private void setUpSearchUi() {
        AppCompatEditText editText = findViewById(R.id.trackpicker_activity_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mQuery.equalsIgnoreCase(s.toString())) {
                    if (s.toString().isEmpty()) {
                        mMainList.clear();
                        mMainList.addAll(LoaderCache.getAllTracksList());
                        mAdapter.updateSelection();
                        mAdapter.updateItems(mMainList);
                    } else searchResult(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editText.requestFocus();
    }

    private void searchResult(String query) {
        mQuery = query;
        pendingUpdates.add(query);
        if (pendingUpdates.size() == 1)
            TaskRunner.executeAsync(new SearchQueryLoader(query), result -> {
                pendingUpdates.remove(0);
                this.mMainList = result;
                mAdapter.updateItems(result);

                if (pendingUpdates.size() > 0) {
                    searchResult(pendingUpdates.get(pendingUpdates.size() - 1));
                    pendingUpdates.clear();
                }
            });
    }

    private void displayTracks() {
        if (mMainList.isEmpty()) {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        } else {
            RecyclerView recyclerView = (RecyclerView) ((ViewStub) findViewById(R.id.stub_track_picker_rv)).inflate();
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mAdapter = new TrackPickerAdapter(mMainList, getLayoutInflater(), this);
            recyclerView.setAdapter(mAdapter);
            mSelectorHelper = new RecyclerViewSelectorHelper(mAdapter);
        }
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, int position, boolean isSelected) {
        if (isSelected) {
            mAdapter.removeSelection(mMainList.get(position));
            mSelectorHelper.onItemUnSelected(viewHolder, position);
        } else {
            mAdapter.addSelection(mMainList.get(position));
            mSelectorHelper.onItemSelected(viewHolder, position);
        }
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
