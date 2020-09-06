package com.hardcodecoder.pulsemusic.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.activities.base.BaseDetailsActivity;
import com.hardcodecoder.pulsemusic.adapters.LibraryAdapter;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.ArtistTracksLoader;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;
import java.util.Locale;

public class ArtistDetailsActivity extends BaseDetailsActivity {

    public static final String KEY_ARTIST_TITLE = "AlbumTitle";
    private LibraryAdapter mAdapter;
    private String mArtistTitle;
    private List<MusicModel> mList;
    private TrackManager tm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setUpTransitions();

        mArtistTitle = getIntent().getStringExtra(KEY_ARTIST_TITLE);
        loadImage();

        tm = TrackManager.getInstance();

        setUpToolbar(findViewById(R.id.details_activity_toolbar), mArtistTitle);

        loadItems();
    }

    @Override
    public int getSortOrder() {
        return AppSettings.getSortOrder(this, Preferences.SORT_ORDER_ARTIST_DETAILS_KEY);
    }

    @Override
    public int getMenuId() {
        return R.menu.menu_artist_details;
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        AppSettings.saveSortOrder(this, Preferences.SORT_ORDER_ARTIST_DETAILS_KEY, newSortOrder);
        mAdapter.updateSortOrder(resolveSortOrder(newSortOrder));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_sort_asc:
                onChangeSortOrder(Preferences.SORT_ORDER_ASC);
                break;
            case R.id.menu_action_sort_desc:
                onChangeSortOrder(Preferences.SORT_ORDER_DESC);
                break;
        }
        return true;
    }

    private SortOrder resolveSortOrder(int sortOrder) {
        switch (sortOrder) {
            case Preferences.SORT_ORDER_DESC:
                return SortOrder.TITLE_DESC;
            case Preferences.SORT_ORDER_ASC:
            default:
                return SortOrder.TITLE_ASC;
        }
    }

    private void loadImage() {
        String transitionName = getIntent().getStringExtra(KEY_TRANSITION_NAME);

        MediaArtImageView sharedImageView = findViewById(R.id.details_activity_art);
        sharedImageView.setTransitionName(transitionName);
        sharedImageView.setImageResource(R.drawable.ic_artist_art);

        supportStartPostponedEnterTransition();
    }

    private void loadItems() {
        TaskRunner.executeAsync(new ArtistTracksLoader(mArtistTitle, resolveSortOrder(mCurrentSortOrder)), (data) -> {
            if (null != data && data.size() > 0) {
                mList = data;
                MaterialTextView sub = findViewById(R.id.details_activity_title_sub);
                sub.setText(String.format(Locale.ENGLISH, "%s %d %s", getString(R.string.num_tracks), mList.size(), getString(R.string.tracks)));

                RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_details_activity_rv)).inflate();
                rv.setHasFixedSize(true);
                rv.setVerticalFadingEdgeEnabled(true);
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL, false));
                mAdapter = new LibraryAdapter(mList, getLayoutInflater(), new SimpleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (null != mList) {
                            tm.buildDataList(mList, position);
                            playMedia();
                        }
                    }

                    @Override
                    public void onOptionsClick(int position) {
                        UIHelper.buildAndShowOptionsMenu(ArtistDetailsActivity.this, getSupportFragmentManager(), mList.get(position));
                    }
                }, null);
                rv.setAdapter(mAdapter);
            }
        });
    }
}