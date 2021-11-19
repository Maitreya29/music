package com.radiant.music.activities.details;

import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.Preferences;
import com.radiant.music.R;
import com.radiant.music.activities.details.base.BaseDetailsActivity;
import com.radiant.music.adapters.main.AlbumsAdapter;
import com.radiant.music.dialog.MenuDetailsDialog;
import com.radiant.music.dialog.ToolbarContextMenuDialog;
import com.radiant.music.interfaces.OptionsMenuListener;
import com.radiant.music.loaders.LoaderManager;
import com.radiant.music.loaders.SortOrder;
import com.radiant.music.model.AlbumModel;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.themes.TintHelper;
import com.radiant.music.utils.AppSettings;
import com.radiant.music.utils.DimensionsUtil;
import com.radiant.music.utils.NavigationUtil;
import com.radiant.music.utils.ToolbarMenuBuilder;
import com.radiant.music.views.MediaArtImageView;

import java.util.List;

public class ArtistDetailsActivity extends BaseDetailsActivity implements OptionsMenuListener {

    public static final String KEY_ARTIST_ID = "ArtistId";
    public static final String KEY_ARTIST_TITLE = "ArtistTitle";
    private AlbumsAdapter mAdapter;
    private SortOrder.ALBUMS mSortOrder;

    @Override
    public void onViewCreated() {
        long artistId = getIntent().getLongExtra(KEY_ARTIST_ID, -1);
        String artistTitle = getIntent().getStringExtra(KEY_ARTIST_TITLE);

        setUpToolbar(artistTitle, v -> showOptionsMenu());

        loadImage();

        int sortOrder = AppSettings.getSortOrder(this, Preferences.SORT_ORDER_ARTIST_DETAILS_KEY);
        setCurrentSortOrder(sortOrder);
        mSortOrder = resolveSortOrder(sortOrder);

        LoaderManager.loadArtistAlbums(getContentResolver(), artistId, artistTitle, mSortOrder, this::loadItems);
    }

    private void showOptionsMenu() {
        ToolbarContextMenuDialog.Builder builder = new ToolbarContextMenuDialog.Builder();
        builder.addGroup(Preferences.MENU_GROUP_TYPE_SORT, getString(R.string.sort_title), R.drawable.ic_sort);
        builder.setMenuSelectedListener(groupItem -> {
            MenuDetailsDialog detailsDialog = ToolbarMenuBuilder.buildSortOrderDialog(
                    this,
                    Preferences.SORT_ORDER_GROUP_ARTISTS_DETAILS,
                    this);
            detailsDialog.show(getSupportFragmentManager(), MenuDetailsDialog.TAG);
        });
        ToolbarContextMenuDialog contextMenuDialog = builder.build();
        contextMenuDialog.show(getSupportFragmentManager(), ToolbarContextMenuDialog.TAG);
    }

    @Override
    public void onMenuDetailsDialogCreated(int groupId, @NonNull MenuDetailsDialog detailsDialog) {
        if (groupId == Preferences.SORT_ORDER_GROUP_ARTISTS_DETAILS)
            detailsDialog.setSelectedItemId(getCurrentSortOrder());
    }

    @Override
    public void onItemSelected(int groupId, int selectedItemId) {
        if (groupId == Preferences.SORT_ORDER_GROUP_ARTISTS_DETAILS)
            onChangeSortOrder(selectedItemId);
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        AppSettings.saveSortOrder(this, Preferences.SORT_ORDER_ARTIST_DETAILS_KEY, newSortOrder);
        if (null == mAdapter) return;
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
        sharedImageView.setTransitionName(transitionName);
        sharedImageView.setBackgroundColor(ThemeColors.getCurrentColorBackgroundHighlight());
        sharedImageView.setImageResource(R.drawable.ic_artist_art);
        TintHelper.setAccentTintTo(sharedImageView);

        supportStartPostponedEnterTransition();
    }

    private void loadItems(@Nullable List<AlbumModel> list) {
        if (list == null || list.isEmpty()) return;

        MaterialTextView sub = findViewById(R.id.details_activity_title_sub);
        sub.setText(getString(R.string.artist_tracks_count, list.size()));

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