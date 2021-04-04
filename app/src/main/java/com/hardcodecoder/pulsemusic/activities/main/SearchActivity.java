package com.hardcodecoder.pulsemusic.activities.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.activities.base.ControllerActivity;
import com.hardcodecoder.pulsemusic.adapters.main.SearchResultAdapter;
import com.hardcodecoder.pulsemusic.helper.DialogHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.SearchQueryLoader;
import com.hardcodecoder.pulsemusic.themes.TintHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ControllerActivity implements SimpleItemClickListener {

    private final List<String> pendingUpdates = new ArrayList<>();
    private MaterialTextView noResultsText;
    private SearchResultAdapter mAdapter;
    private String mQuery = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overrideActivityTransition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewById(R.id.search_activity_close_btn).setOnClickListener(v -> onBackPressed());

        noResultsText = (MaterialTextView) ((ViewStub) findViewById(R.id.stub_no_result_found)).inflate();
        noResultsText.setText(getString(R.string.message_empty_search_result));
        setRecyclerView();
        setUpSearchUi();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overrideActivityTransition();
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
                if (!mQuery.equalsIgnoreCase(s.toString()))
                    searchResult(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editText.requestFocus();
    }

    private void searchResult(String query) {
        mQuery = query;
        pendingUpdates.add(mQuery);
        if (pendingUpdates.size() == 1)
            TaskRunner.executeAsync(new SearchQueryLoader(query), result -> {
                pendingUpdates.remove(0);
                if (null == result) return;

                if (result.size() <= 0) noResultsText.setVisibility(View.VISIBLE);
                else noResultsText.setVisibility(View.INVISIBLE);

                mAdapter.updateItems(result);

                if (pendingUpdates.size() > 0) {
                    searchResult(pendingUpdates.get(pendingUpdates.size() - 1));
                    pendingUpdates.clear();
                }
            });
    }

    private void setRecyclerView() {
        RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_search_rv)).inflate();
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new SearchResultAdapter(getLayoutInflater(), this);
        rv.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        if (null == mAdapter) return;
        mPulseController.getQueueManager().setPlaylist(mAdapter.getDataList(), position);
        mRemote.play();
    }

    @Override
    public void onOptionsClick(int position) {
        if (null == mAdapter) return;
        DialogHelper.showMenuForLibraryTracks(this, mAdapter.getDataList().get(position));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pendingUpdates.clear();
    }

    private void overrideActivityTransition() {
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }
}