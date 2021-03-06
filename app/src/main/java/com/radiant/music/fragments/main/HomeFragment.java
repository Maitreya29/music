package com.radiant.music.fragments.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.Preferences;
import com.radiant.music.R;
import com.radiant.music.TaskRunner;
import com.radiant.music.activities.playlist.FavoritesActivity;
import com.radiant.music.activities.playlist.RecentActivity;
import com.radiant.music.adapters.main.HomeSectionAdapter;
import com.radiant.music.adapters.main.TopAlbumsAdapter;
import com.radiant.music.adapters.main.TopArtistsAdapter;
import com.radiant.music.fragments.main.base.RadiantFragment;
import com.radiant.music.helper.DataModelHelper;
import com.radiant.music.helper.DialogHelper;
import com.radiant.music.helper.MasterListUpdater;
import com.radiant.music.interfaces.SimpleItemClickListener;
import com.radiant.music.loaders.LoaderManager;
import com.radiant.music.model.MusicModel;
import com.radiant.music.model.TopAlbumModel;
import com.radiant.music.model.TopArtistModel;
import com.radiant.music.playback.RadiantController;
import com.radiant.music.playback.QueueManager;
import com.radiant.music.service.PMS;
import com.radiant.music.utils.AppSettings;
import com.radiant.music.utils.NavigationUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends RadiantFragment {

    public static final String TAG = "Home";
    private static final int PICK_MUSIC = 1600;
    private final QueueManager mQueueManager = RadiantController.getInstance().getQueueManager();
    private final RadiantController.RadiantRemote mRemote = RadiantController.getInstance().getRemote();
    private final MasterListUpdater mMasterListUpdater = MasterListUpdater.getInstance();
    private HomeSectionAdapter mForYouAdapter;
    private HomeSectionAdapter mRediscoverAdapter;
    private HomeSectionAdapter mLatestAdapter;
    private String mFragmentTitle = null;

    @NonNull
    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasContextMenu(false);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void setUpContent(@NonNull View view) {
        if (LoaderManager.isMasterListEmpty()) {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        } else {
            TaskRunner.executeAsync(() -> {
                boolean isTopAlbumsEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.KEY_HOME_PLAYLIST_TOP_ALBUMS);
                boolean isForYouEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.KEY_HOME_PLAYLIST_FOR_YOU);
                boolean isRediscoverEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.KEY_HOME_PLAYLIST_REDISCOVER);
                boolean isNewInLibraryEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.KEY_HOME_PLAYLIST_NEW_IN_LIBRARY);
                boolean isTopArtistEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.KEY_HOME_PLAYLIST_TOP_ARTIST);

                if (isTopAlbumsEnabled)
                    LoaderManager.loadTopAlbums(result -> loadTopAlbums(view, result));

                if (isForYouEnabled) {
                    LoaderManager.getSuggestionsList(suggestionsList -> {
                        loadSuggestions(view, suggestionsList);
                        if (isRediscoverEnabled) {
                            // We kick start rediscover load after loading suggestions
                            // so that we can use Suggestions as exclusion list
                            LoaderManager.getRediscoverList(suggestionsList,
                                    rediscoverList -> loadRediscoverSection(view, rediscoverList));
                        }
                    });
                } else if (isRediscoverEnabled) {
                    LoaderManager.getRediscoverList(null,
                            rediscoverList -> loadRediscoverSection(view, rediscoverList));
                }

                if (isNewInLibraryEnabled)
                    LoaderManager.getLatestTracksList(result -> loadLatestTracks(view, result));

                if (isTopArtistEnabled)
                    LoaderManager.loadTopArtist(result -> loadTopArtists(view, result));
            });
        }

        view.findViewById(R.id.ic_recent)
                .setOnClickListener(v -> startActivity(new Intent(requireActivity(), RecentActivity.class)));

        view.findViewById(R.id.ic_folder)
                .setOnClickListener(v -> pickMedia());

        view.findViewById(R.id.ic_favorite)
                .setOnClickListener(v -> startActivity(new Intent(requireActivity(), FavoritesActivity.class)));

        view.findViewById(R.id.ic_shuffle)
                .setOnClickListener(v -> {
                    Intent intent = new Intent(requireActivity(), PMS.class);
                    intent.setAction(PMS.ACTION_DEFAULT_PLAY);
                    intent.putExtra(PMS.KEY_DEFAULT_PLAY, PMS.DEFAULT_ACTION_PLAY_SHUFFLE);
                    requireActivity().startService(intent);
                });
    }

    @Override
    public String getFragmentTitle(@NonNull Context context) {
        if (null == mFragmentTitle) mFragmentTitle = context.getString(R.string.nav_homescreen);
        return mFragmentTitle;
    }

    private void loadTopAlbums(@NonNull View view, @Nullable List<TopAlbumModel> list) {
        if (list == null || list.isEmpty()) return;
        view.postOnAnimation(() -> {
            MaterialTextView topAlbumsTitle = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_top_albums_title)).inflate();
            topAlbumsTitle.setText(getString(R.string.top_albums));
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_top_albums_list)).inflate();
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
            rv.setHasFixedSize(true);
            TopAlbumsAdapter adapter = new TopAlbumsAdapter(getLayoutInflater(), list, (sharedView, position) -> {
                TopAlbumModel albumModel = list.get(position);
                NavigationUtil.goToAlbum(requireActivity(), sharedView, albumModel.getAlbumName(), albumModel.getAlbumId(), albumModel.getAlbumArt());
            });
            SnapHelper helper = new PagerSnapHelper();
            helper.attachToRecyclerView(rv);
            rv.setAdapter(adapter);
        });
    }

    private void loadSuggestions(@NonNull View view, @Nullable List<MusicModel> list) {
        if (list == null || list.isEmpty()) return;
        view.postOnAnimation(() -> {
            MaterialTextView suggestionsTitle = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_suggestions_title)).inflate();
            suggestionsTitle.setText(getString(R.string.for_you));
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_suggested_list)).inflate();
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
            rv.setHasFixedSize(true);
            mForYouAdapter = new HomeSectionAdapter(getLayoutInflater(), list, new SimpleItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mQueueManager.setPlaylist(list, position);
                    mRemote.play();
                }

                @Override
                public void onOptionsClick(int position) {
                    DialogHelper.showMenuForLibraryTracks(requireActivity(), list.get(position));
                }
            });
            rv.setAdapter(mForYouAdapter);
            mMasterListUpdater.addMasterListListener(mForYouAdapter);
        });
    }

    private void loadRediscoverSection(@NonNull View view, @Nullable List<MusicModel> list) {
        if (list == null || list.isEmpty()) return;
        view.postOnAnimation(() -> {
            MaterialTextView suggestionsTitle = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_rediscover_title)).inflate();
            suggestionsTitle.setText(getString(R.string.rediscover));
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_rediscover_list)).inflate();
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
            rv.setHasFixedSize(true);
            mRediscoverAdapter = new HomeSectionAdapter(getLayoutInflater(), list, new SimpleItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mQueueManager.setPlaylist(list, position);
                    mRemote.play();
                }

                @Override
                public void onOptionsClick(int position) {
                    DialogHelper.showMenuForLibraryTracks(requireActivity(), list.get(position));
                }
            });
            rv.setAdapter(mRediscoverAdapter);
            mMasterListUpdater.addMasterListListener(mRediscoverAdapter);
        });
    }

    private void loadLatestTracks(@NonNull View view, @Nullable List<MusicModel> list) {
        if (list == null || list.isEmpty()) return;
        view.postOnAnimation(() -> {
            MaterialTextView newInStoreTitle = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_new_in_store_title)).inflate();
            newInStoreTitle.setText(getString(R.string.new_in_library));
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_new_in_store_list)).inflate();
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
            rv.setHasFixedSize(true);
            mLatestAdapter = new HomeSectionAdapter(getLayoutInflater(), list, new SimpleItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mQueueManager.setPlaylist(list, position);
                    mRemote.play();
                }

                @Override
                public void onOptionsClick(int position) {
                    DialogHelper.showMenuForLibraryTracks(requireActivity(), list.get(position));
                }
            });
            rv.setAdapter(mLatestAdapter);
            mMasterListUpdater.addMasterListListener(mLatestAdapter);
        });
    }

    private void loadTopArtists(@NonNull View view, @Nullable List<TopArtistModel> list) {
        if (list == null || list.isEmpty()) return;
        view.postOnAnimation(() -> {
            MaterialTextView topAlbumsTitle = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_top_artists_title)).inflate();
            topAlbumsTitle.setText(getString(R.string.top_artist));
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_top_artists_list)).inflate();
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.HORIZONTAL, false));
            TopArtistsAdapter adapter = new TopArtistsAdapter(getLayoutInflater(), list, (sharedView, position) ->
                    NavigationUtil.goToArtist(requireActivity(), sharedView, list.get(position).getArtistName(), -1));
            rv.setAdapter(adapter);
        });
    }

    private void pickMedia() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        intent_upload.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent_upload, PICK_MUSIC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_MUSIC) {
            MusicModel md = DataModelHelper.buildMusicModelFrom(requireContext(), data.getData());
            if (md != null) {
                List<MusicModel> singlePickedItemList = new ArrayList<>();
                singlePickedItemList.add(md);
                mQueueManager.setPlaylist(singlePickedItemList, 0);
                mRemote.play();
            } else
                Toast.makeText(requireContext(), getString(R.string.toast_selected_track_load_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (null != mForYouAdapter) mMasterListUpdater.removeMasterListListener(mForYouAdapter);
        if (null != mLatestAdapter) mMasterListUpdater.removeMasterListListener(mLatestAdapter);
        if (null != mRediscoverAdapter)
            mMasterListUpdater.removeMasterListListener(mRediscoverAdapter);
        super.onDestroy();
    }
}