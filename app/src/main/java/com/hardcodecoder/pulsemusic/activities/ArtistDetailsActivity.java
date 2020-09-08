package com.hardcodecoder.pulsemusic.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.activities.base.BaseDetailsActivity;
import com.hardcodecoder.pulsemusic.adapters.AlbumsAdapter;
import com.hardcodecoder.pulsemusic.loaders.ArtistTracksLoader;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;
import java.util.Locale;

public class ArtistDetailsActivity extends BaseDetailsActivity {

    public static final String KEY_ARTIST_TITLE = "AlbumTitle";
    private AlbumsAdapter mAdapter;
    private String mArtistTitle;
    private List<AlbumModel> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setUpTransitions();

        mArtistTitle = getIntent().getStringExtra(KEY_ARTIST_TITLE);
        loadImage();

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

    private SortOrder.ALBUMS resolveSortOrder(int sortOrder) {
        switch (sortOrder) {
            case Preferences.SORT_ORDER_DESC:
                return SortOrder.ALBUMS.TITLE_DESC;
            case Preferences.SORT_ORDER_ASC:
            default:
                return SortOrder.ALBUMS.TITLE_ASC;
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
        TaskRunner.executeAsync(new ArtistTracksLoader(getContentResolver(), mArtistTitle, resolveSortOrder(mCurrentSortOrder)), (data) -> {
            if (null != data && data.size() > 0) {
                mList = data;
                MaterialTextView sub = findViewById(R.id.details_activity_title_sub);
                sub.setText(String.format(Locale.ENGLISH, "%s %d %s", getString(R.string.num_artist_tracks), mList.size(), getString(R.string.album)));

                RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_details_activity_rv)).inflate();
                int padding = (int) DimensionsUtil.convertToPixels(this, 16f);
                rv.setPadding(padding, padding, 0, 0);
                rv.setHasFixedSize(true);
                rv.setVerticalFadingEdgeEnabled(true);
                GridLayoutManager layoutManager = new GridLayoutManager(rv.getContext(), 2);
                rv.setLayoutManager(layoutManager);
                mAdapter = new AlbumsAdapter(mList, getLayoutInflater(), (sharedView, position) -> {
                    AlbumModel am = mList.get(position);
                    NavigationUtil.goToAlbum(ArtistDetailsActivity.this, sharedView, am.getAlbumName(), am.getAlbumId(), am.getAlbumArt());
                }, null);
                rv.setAdapter(mAdapter);
            }
        });
    }
}