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
import com.hardcodecoder.pulsemusic.dialog.ToolbarContextMenuDialog;
import com.hardcodecoder.pulsemusic.fragments.main.base.CardGridFragment;
import com.hardcodecoder.pulsemusic.fragments.main.base.PulseFragment;
import com.hardcodecoder.pulsemusic.interfaces.GridAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ARTIST;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;
import com.hardcodecoder.pulsemusic.utils.ToolbarMenuBuilder;

import java.util.List;

public class ArtistFragment extends CardGridFragment
        implements SimpleTransitionClickListener, GridAdapterCallback, PulseFragment.OptionsMenuListener {

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
        LoaderHelper.loadArtistsList(requireActivity().getContentResolver(),
                mSortOrder,
                list -> {
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
    public void onItemSelected(int groupId, int selectedItemId) {
        if (groupId == Preferences.SORT_ORDER_GROUP_ARTISTS)
            changeSortOrder(selectedItemId);
        else if (groupId == Preferences.COLUMN_COUNT_GROUP_ARTISTS)
            updateGridSpanCount(getCurrentOrientation(), selectedItemId);
    }

    private ARTIST resolveSortOrder(int sortOrder) {
        if (sortOrder == Preferences.SORT_ORDER_ASC)
            return ARTIST.TITLE_ASC;
        return ARTIST.TITLE_DESC;
    }

    @Override
    public int getSortOrder() {
        if (null == getContext())
            return super.getSortOrder();
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
    public int getPortraitModeSpanCount() {
        if (null == getContext())
            return super.getPortraitModeSpanCount();
        return AppSettings.getPortraitModeGridSpanCount(
                getContext(),
                Preferences.ARTIST_SPAN_COUNT_PORTRAIT_KEY,
                Preferences.SPAN_COUNT_PORTRAIT_DEF_VALUE);
    }

    @Override
    public int getLandscapeModeSpanCount() {
        if (null == getContext())
            return super.getLandscapeModeSpanCount();
        return AppSettings.getLandscapeModeGridSpanCount(
                getContext(),
                Preferences.ARTIST_SPAN_COUNT_LANDSCAPE_KEY,
                Preferences.SPAN_COUNT_LANDSCAPE_DEF_VALUE);
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
            AppSettings.savePortraitModeGridSpanCount(requireContext(), Preferences.ARTIST_SPAN_COUNT_PORTRAIT_KEY, spanCount);
        else if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE)
            AppSettings.saveLandscapeModeGridSpanCount(requireContext(), Preferences.ARTIST_SPAN_COUNT_LANDSCAPE_KEY, spanCount);
        if (mLayoutManager == null) return;
        mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        mAdapter.updateSpanCount(currentOrientation, spanCount);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager.setSpanCount(spanCount);
        mLayoutManager.scrollToPosition(mFirstVisibleItemPosition);
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
            mLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), getCurrentSpanCount());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new ArtistAdapter(
                    getLayoutInflater(),
                    list,
                    this,
                    this,
                    getCurrentOrientation(),
                    getCurrentSpanCount());

            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(mRecyclerView.getContext(), R.anim.item_enter_slide_up);
            mRecyclerView.setLayoutAnimation(controller);
            mRecyclerView.setAdapter(mAdapter);
        });
    }
}