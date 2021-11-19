package com.radiant.music.activities.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.R;
import com.radiant.music.activities.base.ThemeActivity;
import com.radiant.music.adapters.main.FoldersListAdapter;
import com.radiant.music.helper.RecyclerViewSelectorHelper;
import com.radiant.music.interfaces.ItemSelectorListener;
import com.radiant.music.loaders.LoaderManager;
import com.radiant.music.model.Folder;
import com.radiant.music.themes.TintHelper;
import com.radiant.music.views.CustomToolbar;

import java.util.List;

public class MediaFolderChooserActivity extends ThemeActivity implements ItemSelectorListener {

    public static final String RESULT_SELECTED_FOLDERS = "selected_folders";
    private FoldersListAdapter mAdapter;
    private RecyclerViewSelectorHelper mSelectorHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_chooser);

        CustomToolbar toolbar = findViewById(R.id.material_toolbar);
        toolbar.setTitle(R.string.select_folders);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(v -> finish());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            LoaderManager.loadTracksRelativePath(this, this::loadData);
        else
            loadData(null);

        FloatingActionButton fab = findViewById(R.id.fab_done);
        TintHelper.setAccentTintTo(fab);
        fab.setOnClickListener(v -> {
            if (null != mAdapter) {
                Intent i = new Intent();
                i.putExtra(RESULT_SELECTED_FOLDERS, mAdapter.getSelectedFoldersPath());
                setResult(RESULT_OK, i);
            }
            finish();
        });
    }

    private void loadData(@Nullable List<Folder> foldersList) {
        if (foldersList == null || foldersList.isEmpty()) {
            MaterialTextView tv = (MaterialTextView) ((ViewStub) findViewById(R.id.stub_no_data_found)).inflate();
            String text = getString(R.string.message_empty_folders_list);
            int len = text.length();
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
            stringBuilder.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.spannable_text_absolute_size_span)),
                    len - 1,
                    len,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tv.setText(stringBuilder);
        } else {
            RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_folder_chooser_rv)).inflate();
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL, false));
            mAdapter = new FoldersListAdapter(getLayoutInflater(), foldersList, this);
            rv.setAdapter(mAdapter);
            mSelectorHelper = new RecyclerViewSelectorHelper(mAdapter);
        }
    }

    @Override
    public void onItemClick(@NonNull RecyclerView.ViewHolder viewHolder, int position, boolean isSelected) {
        if (isSelected) mSelectorHelper.onItemSelected(viewHolder, position);
        else mSelectorHelper.onItemUnSelected(viewHolder, position);
    }
}