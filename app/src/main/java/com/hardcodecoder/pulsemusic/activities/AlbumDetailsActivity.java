package com.hardcodecoder.pulsemusic.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.BaseDetailsActivity;
import com.hardcodecoder.pulsemusic.adapters.LibraryAdapter;
import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;
import java.util.Locale;

public class AlbumDetailsActivity extends BaseDetailsActivity {

    public static final String KEY_ALBUM_TITLE = "AlbumTitle";
    public static final String KEY_ALBUM_ID = "AlbumId";
    public static final String KEY_ALBUM_ART_URL = "AlbumArtUrl";
    private LibraryAdapter mAdapter;
    private TrackManager tm;
    private SortOrder mSortOrder;
    private Long mAlbumId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setUpTransitions();

        String mAlbumTitle = getIntent().getStringExtra(KEY_ALBUM_TITLE);
        mAlbumId = getIntent().getLongExtra(KEY_ALBUM_ID, 0);

        tm = TrackManager.getInstance();

        setUpToolbar(findViewById(R.id.details_activity_toolbar), mAlbumTitle == null ? "" : mAlbumTitle);

        loadImage();

        mSortOrder = resolveSortOrder(getCurrentSortOrder());
        LoaderHelper.loadAlbumTracks(this, mSortOrder, mAlbumId, this::loadItems);
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
            case R.id.menu_action_sort_track_num_asc:
                onChangeSortOrder(Preferences.SORT_ORDER_ALBUM_TRACK_NUMBER_ASC);
                break;
            case R.id.menu_action_sort_track_num_desc:
                onChangeSortOrder(Preferences.SORT_ORDER_ALBUM_TRACK_NUMBER_DESC);
                break;
        }
        return true;
    }

    @Override
    public int getSortOrder() {
        return AppSettings.getSortOrder(this, Preferences.SORT_ORDER_ALBUM_DETAILS_KEY);
    }

    @Override
    public int getMenuId() {
        return R.menu.menu_album_details;
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        AppSettings.saveSortOrder(this, Preferences.SORT_ORDER_ALBUM_DETAILS_KEY, newSortOrder);
        mSortOrder = resolveSortOrder(newSortOrder);
        mAdapter.updateSortOrder(mSortOrder);
    }

    private SortOrder resolveSortOrder(int sortOrder) {
        switch (sortOrder) {
            case Preferences.SORT_ORDER_DESC:
                return SortOrder.TITLE_DESC;
            case Preferences.SORT_ORDER_ALBUM_TRACK_NUMBER_ASC:
                return SortOrder.TRACK_NUMBER_ASC;
            case Preferences.SORT_ORDER_ALBUM_TRACK_NUMBER_DESC:
                return SortOrder.TRACK_NUMBER_DESC;
            case Preferences.SORT_ORDER_ASC:
            default:
                return SortOrder.TITLE_ASC;
        }
    }

    private void loadImage() {
        String transitionName = getIntent().getStringExtra(KEY_TRANSITION_NAME);
        String artUrl = getIntent().getStringExtra(KEY_ALBUM_ART_URL);

        MediaArtImageView sharedImageView = findViewById(R.id.details_activity_art);
        sharedImageView.setTransitionName(transitionName);
        GlideApp.with(sharedImageView)
                .load(artUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        sharedImageView.setBackgroundColor(Color.parseColor("#1E1E1E"));
                        sharedImageView.setImageDrawable(MediaArtHelper.getDefaultAlbumArt(sharedImageView.getContext(), mAlbumId));
                        supportStartPostponedEnterTransition();
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        sharedImageView.setImageDrawable(resource);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                })
                .into(sharedImageView);
    }

    private void loadItems(@Nullable final List<MusicModel> list) {
        if (null == list || list.isEmpty()) return;

        MaterialTextView sub = findViewById(R.id.details_activity_title_sub);
        sub.setText(String.format(Locale.getDefault(), "%s %d %s", getString(R.string.num_album_tracks), list.size(), getString(R.string.tracks)));

        RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_details_activity_rv)).inflate();
        rv.setHasFixedSize(true);
        rv.setHorizontalFadingEdgeEnabled(true);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL, false));

        mAdapter = new LibraryAdapter(
                list,
                getLayoutInflater(),
                mSortOrder,
                new SimpleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tm.buildDataList(list, position);
                        playMedia();
                    }

                    @Override
                    public void onOptionsClick(int position) {
                        UIHelper.showMenuForAlbumDetails(AlbumDetailsActivity.this, getSupportFragmentManager(), list.get(position));
                    }
                }, null);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.item_falls_down_animation);
        rv.setLayoutAnimation(controller);
        rv.setAdapter(mAdapter);
    }
}