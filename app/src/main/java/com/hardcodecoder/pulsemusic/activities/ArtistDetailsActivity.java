package com.hardcodecoder.pulsemusic.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.BaseDetailsActivity;
import com.hardcodecoder.pulsemusic.adapters.main.AlbumsAdapter;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.themes.TintHelper;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;
import java.util.Locale;

public class ArtistDetailsActivity extends BaseDetailsActivity {

    public static final String KEY_ARTIST_TITLE = "AlbumTitle";
    private AlbumsAdapter mAdapter;
    private SortOrder.ALBUMS mSortOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setUpTransitions();

        String artistTitle = getIntent().getStringExtra(KEY_ARTIST_TITLE);
        loadImage();
        artistTitle = artistTitle == null ? "<unknown>" : artistTitle;
        setUpToolbar(findViewById(R.id.details_activity_toolbar), artistTitle);

        mSortOrder = resolveSortOrder(getSortOrder());
        LoaderHelper.loadArtistAlbums(getContentResolver(), artistTitle, mSortOrder, this::loadItems);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem sortItem = null;
        final int sortOrder = getCurrentSortOrder();

        if (sortOrder == Preferences.SORT_ORDER_ASC)
            sortItem = menu.findItem(R.id.menu_action_sort_artist_title_asc);
        else if (sortOrder == Preferences.SORT_ORDER_DESC)
            sortItem = menu.findItem(R.id.menu_action_sort_artist_title_desc);

        if (sortItem != null)
            sortItem.setChecked(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int groupId = item.getGroupId();
        if (groupId == R.id.group_artist_sort) {
            final int id = item.getItemId();
            int sortOrder;

            if (id == R.id.menu_action_sort_artist_title_asc)
                sortOrder = Preferences.SORT_ORDER_ASC;
            else
                sortOrder = Preferences.SORT_ORDER_DESC;

            onChangeSortOrder(sortOrder);
        }

        item.setChecked(true);
        return true;
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
        mSortOrder = resolveSortOrder(newSortOrder);
        mAdapter.updateSortOrder(mSortOrder);
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
        TintHelper.setAccentTintTo(sharedImageView);
        sharedImageView.setTransitionName(transitionName);
        sharedImageView.setImageResource(R.drawable.ic_artist_art);

        supportStartPostponedEnterTransition();
    }

    private void loadItems(@Nullable List<AlbumModel> list) {
        if (list == null || list.isEmpty()) return;

        MaterialTextView sub = findViewById(R.id.details_activity_title_sub);
        sub.setText(String.format(Locale.getDefault(), "%s %d %s", getString(R.string.num_artist_tracks), list.size(), getString(R.string.album)));

        RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_details_activity_rv)).inflate();
        int padding = DimensionsUtil.getDimensionPixelSize(this, 16f);
        rv.setPadding(padding, padding, 0, 0);
        rv.setHasFixedSize(true);
        rv.setVerticalFadingEdgeEnabled(true);
        GridLayoutManager layoutManager = new GridLayoutManager(rv.getContext(), 2);
        rv.setLayoutManager(layoutManager);
        mAdapter = new AlbumsAdapter(
                getLayoutInflater(),
                list,
                (sharedView, position) -> {
                    AlbumModel am = list.get(position);
                    NavigationUtil.goToAlbum(ArtistDetailsActivity.this, sharedView, am.getAlbumName(), am.getAlbumId(), am.getAlbumArt());
                },
                null,
                mSortOrder);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.item_enter_slide_up);
        rv.setLayoutAnimation(controller);
        rv.setAdapter(mAdapter);
    }
}