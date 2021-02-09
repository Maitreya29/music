package com.hardcodecoder.pulsemusic.fragments.main;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.main.AlbumsAdapter;
import com.hardcodecoder.pulsemusic.dialog.ToolbarContextMenuDialog;
import com.hardcodecoder.pulsemusic.fragments.main.base.CardGridFragment;
import com.hardcodecoder.pulsemusic.interfaces.GridAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.OptionsMenuListener;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ALBUMS;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;
import com.hardcodecoder.pulsemusic.utils.ToolbarMenuBuilder;

import java.util.List;

public class AlbumsFragment extends CardGridFragment
        implements SimpleTransitionClickListener, GridAdapterCallback, OptionsMenuListener {

    public static final String TAG = "Albums";
    private GridLayoutManager mLayoutManager;
    private AlbumsAdapter mAdapter;
    private ALBUMS mSortOrder;
    private String mFragmentTitle = null;
    private int mFirstVisibleItemPosition;

    @NonNull
    public static AlbumsFragment getInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void setUpContent(@NonNull View view) {
        mSortOrder = resolveSortOrder(getCurrentSortOrder());
        LoaderHelper.loadAlbumsList(requireActivity().getContentResolver(),
                mSortOrder,
                list -> {
                    if (list == null || list.isEmpty()) {
                        MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
                        noTracksText.setText(getString(R.string.tracks_not_found));
                    } else loadAlbumsList(view, list);
                });
    }

    @Override
    public String getFragmentTitle(@NonNull Context context) {
        if (null == mFragmentTitle) mFragmentTitle = context.getString(R.string.nav_albums);
        return mFragmentTitle;
    }

    @Override
    public void showOptionsMenu() {
        ToolbarContextMenuDialog toolbarMenuDialog = ToolbarMenuBuilder.buildDefaultOptionsMenu(
                requireActivity(),
                this,
                Preferences.SORT_ORDER_GROUP_ALBUMS,
                Preferences.COLUMN_COUNT_GROUP_ALBUMS);
        toolbarMenuDialog.show(requireFragmentManager(), ToolbarContextMenuDialog.TAG);
    }

    @Override
    public void onItemSelected(int groupId, int selectedItemId) {
        if (groupId == Preferences.SORT_ORDER_GROUP_ALBUMS)
            changeSortOrder(selectedItemId);
        else if (groupId == Preferences.COLUMN_COUNT_GROUP_ALBUMS)
            changeColumnCount(getCurrentOrientation(), selectedItemId);
    }

    private ALBUMS resolveSortOrder(int sortOrder) {
        switch (sortOrder) {
            case Preferences.SORT_ORDER_DESC:
                return ALBUMS.TITLE_DESC;
            case Preferences.SORT_ORDER_ALBUM_ARTIST_ASC:
                return ALBUMS.ARTIST_ASC;
            case Preferences.SORT_ORDER_ALBUM_ARTIST_DESC:
                return ALBUMS.ARTIST_DESC;
            case Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_ASC:
                return ALBUMS.ALBUM_DATE_FIRST_YEAR_ASC;
            case Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_DESC:
                return ALBUMS.ALBUM_DATE_FIRST_YEAR_DESC;
            case Preferences.SORT_ORDER_ASC:
            default:
                return ALBUMS.TITLE_ASC;
        }
    }

    @Override
    public int getSortOrder() {
        if (null == getContext())
            return Preferences.SORT_ORDER_ASC;
        return AppSettings.getSortOrder(getContext(), Preferences.SORT_ORDER_ALBUMS_KEY);
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        if (mAdapter == null) return;
        mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        mSortOrder = resolveSortOrder(newSortOrder);
        mAdapter.updateSortOrder(mSortOrder);
        AppSettings.saveSortOrder(requireContext(), Preferences.SORT_ORDER_ALBUMS_KEY, newSortOrder);
    }

    @Override
    public int getPortraitModeColumnCount() {
        if (null == getContext())
            return Preferences.COLUMN_COUNT_TWO;
        return AppSettings.getPortraitModeColumnCount(
                getContext(),
                Preferences.COLUMN_COUNT_ALBUMS_PORTRAIT_KEY,
                Preferences.COLUMN_COUNT_TWO);
    }

    @Override
    public int getLandscapeModeColumnCount() {
        if (null == getContext())
            return Preferences.COLUMN_COUNT_FOUR;
        return AppSettings.getLandscapeModeColumnCount(
                getContext(),
                Preferences.COLUMN_COUNT_ALBUMS_LANDSCAPE_KEY,
                Preferences.COLUMN_COUNT_FOUR);
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
            AppSettings.savePortraitModeColumnCount(requireContext(), Preferences.COLUMN_COUNT_ALBUMS_PORTRAIT_KEY, spanCount);
        else if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE)
            AppSettings.saveLandscapeModeColumnCount(requireContext(), Preferences.COLUMN_COUNT_ALBUMS_LANDSCAPE_KEY, spanCount);
        if (mLayoutManager == null) return;
        mLayoutManager.setSpanCount(spanCount);
    }

    @Override
    public void onSortUpdateComplete() {
        mLayoutManager.scrollToPosition(mFirstVisibleItemPosition);
    }

    @Override
    public void onItemClick(View sharedView, int position) {
        AlbumModel albumModel = mAdapter.getDataList().get(position);
        NavigationUtil.goToAlbum(requireActivity(), sharedView, albumModel.getAlbumName(), albumModel.getAlbumId(), albumModel.getAlbumArt());
    }

    private void loadAlbumsList(@NonNull View view, @NonNull List<AlbumModel> list) {
        view.postOnAnimation(() -> {
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_grid_rv)).inflate();
            mLayoutManager = new GridLayoutManager(rv.getContext(), getCurrentColumnCount());
            rv.setLayoutManager(mLayoutManager);
            rv.setHasFixedSize(true);

            mAdapter = new AlbumsAdapter(getLayoutInflater(), list, this, this, mSortOrder);

            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(rv.getContext(), R.anim.item_enter_slide_up);
            rv.setLayoutAnimation(controller);
            rv.setAdapter(mAdapter);
        });
    }
}