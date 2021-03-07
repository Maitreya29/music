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
import com.hardcodecoder.pulsemusic.adapters.main.ArtistAdapter;
import com.hardcodecoder.pulsemusic.dialog.MenuDetailsDialog;
import com.hardcodecoder.pulsemusic.dialog.ToolbarContextMenuDialog;
import com.hardcodecoder.pulsemusic.fragments.main.base.CardGridFragment;
import com.hardcodecoder.pulsemusic.interfaces.GridAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.OptionsMenuListener;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderManager;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ARTIST;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;
import com.hardcodecoder.pulsemusic.utils.ToolbarMenuBuilder;

import java.util.List;

public class ArtistFragment extends CardGridFragment
        implements SimpleTransitionClickListener, GridAdapterCallback, OptionsMenuListener {

    public static final String TAG = "Artists";
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private ArtistAdapter mAdapter;
    private ARTIST mSortOrder;
    private String mFragmentTitle = null;
    private int mFirstVisibleItemPosition;

    @NonNull
    public static ArtistFragment getInstance() {
        return new ArtistFragment();
    }

    @Override
    public void setUpContent(@NonNull View view) {
        mSortOrder = resolveSortOrder(getCurrentSortOrder());
        LoaderManager.loadArtistsList(requireActivity().getContentResolver(),
                mSortOrder, list -> {
                    if (list == null || list.isEmpty()) {
                        MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
                        noTracksText.setText(getString(R.string.tracks_not_found));
                    } else loadArtistsList(view, list);
                });
    }

    @Override
    public String getFragmentTitle(@NonNull Context context) {
        if (null == mFragmentTitle) mFragmentTitle = context.getString(R.string.nav_artists);
        return mFragmentTitle;
    }

    @Override
    public void showOptionsMenu() {
        ToolbarContextMenuDialog toolbarMenuDialog = ToolbarMenuBuilder.buildDefaultOptionsMenu(
                requireActivity(),
                this,
                Preferences.SORT_ORDER_GROUP_ARTISTS,
                Preferences.COLUMN_COUNT_GROUP_ARTISTS);
        toolbarMenuDialog.show(requireFragmentManager(), ToolbarContextMenuDialog.TAG);
    }

    @Override
    public void onMenuDetailsDialogCreated(int groupId, @NonNull MenuDetailsDialog detailsDialog) {
        if (groupId == Preferences.SORT_ORDER_GROUP_ARTISTS)
            detailsDialog.setSelectedItemId(getCurrentSortOrder());
        else if (groupId == Preferences.COLUMN_COUNT_GROUP_ARTISTS)
            detailsDialog.setSelectedItemId(getCurrentColumnCount());
    }

    @Override
    public void onItemSelected(int groupId, int selectedItemId) {
        if (groupId == Preferences.SORT_ORDER_GROUP_ARTISTS)
            changeSortOrder(selectedItemId);
        else if (groupId == Preferences.COLUMN_COUNT_GROUP_ARTISTS)
            changeColumnCount(getCurrentOrientation(), selectedItemId);
    }

    private ARTIST resolveSortOrder(int sortOrder) {
        if (sortOrder == Preferences.SORT_ORDER_ASC)
            return ARTIST.TITLE_ASC;
        return ARTIST.TITLE_DESC;
    }

    @Override
    public int getSortOrder() {
        if (null == getContext())
            return Preferences.SORT_ORDER_ASC;
        return AppSettings.getSortOrder(getContext(), Preferences.SORT_ORDER_ARTIST_KEY);
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        if (mAdapter == null) return;
        mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        mSortOrder = resolveSortOrder(newSortOrder);
        mAdapter.updateSortOrder(mSortOrder);
        AppSettings.saveSortOrder(requireContext(), Preferences.SORT_ORDER_ARTIST_KEY, newSortOrder);
    }

    @Override
    public int getPortraitModeColumnCount() {
        if (null == getContext())
            return Preferences.COLUMN_COUNT_TWO;
        return AppSettings.getPortraitModeColumnCount(
                getContext(),
                Preferences.COLUMN_COUNT_ARTISTS_PORTRAIT_KEY,
                Preferences.COLUMN_COUNT_TWO);
    }

    @Override
    public int getLandscapeModeColumnCount() {
        if (null == getContext())
            return Preferences.COLUMN_COUNT_FOUR;
        return AppSettings.getLandscapeModeColumnCount(
                getContext(),
                Preferences.COLUMN_COUNT_ARTISTS_LANDSCAPE_KEY,
                Preferences.COLUMN_COUNT_FOUR);
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
            AppSettings.savePortraitModeColumnCount(requireContext(), Preferences.COLUMN_COUNT_ARTISTS_PORTRAIT_KEY, spanCount);
        else if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE)
            AppSettings.saveLandscapeModeColumnCount(requireContext(), Preferences.COLUMN_COUNT_ARTISTS_LANDSCAPE_KEY, spanCount);
        if (mLayoutManager == null) return;
        mAdapter.updateColumnCount(currentOrientation, spanCount);
        mLayoutManager.setSpanCount(spanCount);
    }

    @Override
    public void onSortUpdateComplete() {
        mLayoutManager.scrollToPosition(mFirstVisibleItemPosition);
    }

    @Override
    public void onItemClick(View sharedView, int position) {
        NavigationUtil.goToArtist(requireActivity(), sharedView, mAdapter.getDataList().get(position).getArtistName());
    }

    private void loadArtistsList(@NonNull View view, @NonNull List<ArtistModel> list) {
        view.postOnAnimation(() -> {
            mRecyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_grid_rv)).inflate();
            mLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), getCurrentColumnCount());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new ArtistAdapter(
                    getLayoutInflater(),
                    list,
                    this,
                    this,
                    getCurrentOrientation(),
                    getCurrentColumnCount());

            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(mRecyclerView.getContext(), R.anim.item_enter_slide_up);
            mRecyclerView.setLayoutAnimation(controller);
            mRecyclerView.setAdapter(mAdapter);
        });
    }
}