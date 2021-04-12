package com.nezukoos.music.activities.details;

import android.graphics.drawable.Drawable;
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
import com.nezukoos.music.Preferences;
import com.nezukoos.music.R;
import com.nezukoos.music.activities.details.base.BaseDetailsActivity;
import com.nezukoos.music.adapters.details.AlbumTracksAdapter;
import com.nezukoos.music.dialog.MenuDetailsDialog;
import com.nezukoos.music.dialog.ToolbarContextMenuDialog;
import com.nezukoos.music.glide.GlideApp;
import com.nezukoos.music.helper.DialogHelper;
import com.nezukoos.music.helper.MasterListUpdater;
import com.nezukoos.music.helper.MediaArtHelper;
import com.nezukoos.music.interfaces.OptionsMenuListener;
import com.nezukoos.music.interfaces.SimpleItemClickListener;
import com.nezukoos.music.loaders.LoaderManager;
import com.nezukoos.music.loaders.SortOrder;
import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.themes.ThemeColors;
import com.nezukoos.music.utils.AppSettings;
import com.nezukoos.music.utils.ToolbarMenuBuilder;
import com.nezukoos.music.views.MediaArtImageView;

import java.util.List;

public class AlbumDetailsActivity extends BaseDetailsActivity implements OptionsMenuListener {

    public static final String KEY_ALBUM_TITLE = "AlbumTitle";
    public static final String KEY_ALBUM_ID = "AlbumId";
    public static final String KEY_ALBUM_ART_URL = "AlbumArtUrl";
    private AlbumTracksAdapter mAdapter;
    private SortOrder mSortOrder;
    private long mAlbumId;

    @Override
    public void onViewCreated() {
        String albumTitle = getIntent().getStringExtra(KEY_ALBUM_TITLE);
        mAlbumId = getIntent().getLongExtra(KEY_ALBUM_ID, 0);

        setUpToolbar(albumTitle == null ? "" : albumTitle, v -> showOptionsMenu());

        loadImage();

        int sortOrder = AppSettings.getSortOrder(this, Preferences.SORT_ORDER_ALBUM_DETAILS_KEY);
        setCurrentSortOrder(sortOrder);
        mSortOrder = resolveSortOrder(sortOrder);

        LoaderManager.loadAlbumTracks(mAlbumId, mSortOrder, this::loadItems);
    }

    private void showOptionsMenu() {
        ToolbarContextMenuDialog.Builder builder = new ToolbarContextMenuDialog.Builder();
        builder.addGroup(Preferences.MENU_GROUP_TYPE_SORT, getString(R.string.sort_title), R.drawable.ic_sort);
        builder.setMenuSelectedListener(groupItem -> {
            MenuDetailsDialog detailsDialog = ToolbarMenuBuilder.buildSortOrderDialog(
                    this,
                    Preferences.SORT_ORDER_GROUP_ALBUMS_DETAILS,
                    this);
            detailsDialog.show(getSupportFragmentManager(), MenuDetailsDialog.TAG);
        });
        ToolbarContextMenuDialog contextMenuDialog = builder.build();
        contextMenuDialog.show(getSupportFragmentManager(), ToolbarContextMenuDialog.TAG);
    }

    @Override
    public void onMenuDetailsDialogCreated(int groupId, @NonNull MenuDetailsDialog detailsDialog) {
        if (groupId == Preferences.SORT_ORDER_GROUP_ALBUMS_DETAILS)
            detailsDialog.setSelectedItemId(getCurrentSortOrder());
    }

    @Override
    public void onItemSelected(int groupId, int selectedItemId) {
        if (groupId == Preferences.SORT_ORDER_GROUP_ALBUMS_DETAILS)
            onChangeSortOrder(selectedItemId);
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        AppSettings.saveSortOrder(this, Preferences.SORT_ORDER_ALBUM_DETAILS_KEY, newSortOrder);
        if (null == mAdapter) return;
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
                        sharedImageView.setBackgroundColor(R.color.card);
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
        sub.setText(getString(R.string.album_tracks_count, list.size()));

        RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_details_activity_rv)).inflate();
        rv.setHasFixedSize(true);
        rv.setHorizontalFadingEdgeEnabled(true);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL, false));

        mAdapter = new AlbumTracksAdapter(
                getLayoutInflater(),
                list,
                new SimpleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        mPulseController.getQueueManager().setPlaylist(list, position);
                        mRemote.play();
                    }

                    @Override
                    public void onOptionsClick(int position) {
                        DialogHelper.showMenuForAlbumDetails(AlbumDetailsActivity.this, list.get(position));
                    }
                },
                mSortOrder);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.item_falls_down_animation);
        rv.setLayoutAnimation(controller);
        rv.setAdapter(mAdapter);
        MasterListUpdater.getInstance().addMasterListListener(mAdapter);
    }

    @Override
    protected void onDestroy() {
        MasterListUpdater.getInstance().removeMasterListListener(mAdapter);
        super.onDestroy();
    }
}