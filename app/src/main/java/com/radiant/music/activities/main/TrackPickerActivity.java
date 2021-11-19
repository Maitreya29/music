package com.radiant.music.activities.main;

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
import com.radiant.music.R;
import com.radiant.music.TaskRunner;
import com.radiant.music.activities.base.ThemeActivity;
import com.radiant.music.adapters.main.TracksSelectorAdapter;
import com.radiant.music.helper.RecyclerViewSelectorHelper;
import com.radiant.music.interfaces.ItemSelectorListener;
import com.radiant.music.loaders.LoaderManager;
import com.radiant.music.loaders.SearchQueryLoader;
import com.radiant.music.model.MusicModel;
import com.radiant.music.themes.TintHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TrackPickerActivity extends ThemeActivity implements ItemSelectorListener {

    public static final String ID_PICKED_TRACKS = "picked_tracks";
    public static final int REQUEST_CODE = 100;
    private final Stack<String> pendingUpdates = new Stack<>();
    private RecyclerViewSelectorHelper mSelectorHelper;
    @Nullable
    private TracksSelectorAdapter mAdapter;
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
                i.putExtra(ID_PICKED_TRACKS, new ArrayList<>(mAdapter.getSelectedData()));
            setResult(RESULT_OK, i);
            finishActivity(REQUEST_CODE);
            finish();
            overrideActivityTransition();
        });

        setUpSearchUi();
        displayTracks();
    }

    private void setUpSearchUi() {
        AppCompatEditText editText = findViewById(R.id.search_edit_text);
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
                        mAdapter.updateItems(LoaderManager.getCachedMasterList());
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
        List<MusicModel> masterList = LoaderManager.getCachedMasterList();
        if (null == masterList || masterList.isEmpty()) {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
            return;
        }
        RecyclerView recyclerView = (RecyclerView) ((ViewStub) findViewById(R.id.stub_track_picker_rv)).inflate();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new TracksSelectorAdapter(getLayoutInflater(), masterList, this);
        recyclerView.setAdapter(mAdapter);
        mSelectorHelper = new RecyclerViewSelectorHelper(mAdapter);
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