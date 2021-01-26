package com.hardcodecoder.pulsemusic.fragments.main;

import android.app.Activity;
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
import com.hardcodecoder.pulsemusic.PMS;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.activities.playlist.FavoritesActivity;
import com.hardcodecoder.pulsemusic.activities.playlist.RecentActivity;
import com.hardcodecoder.pulsemusic.adapters.main.HomeSectionAdapter;
import com.hardcodecoder.pulsemusic.adapters.main.TopAlbumsAdapter;
import com.hardcodecoder.pulsemusic.adapters.main.TopArtistsAdapter;
import com.hardcodecoder.pulsemusic.fragments.main.base.SmoothTransactionFragments;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends SmoothTransactionFragments {

    private static final int PICK_MUSIC = 1600;
    private final PulseController mPulseController = PulseController.getInstance();
    private final PulseController.PulseRemote mRemote = mPulseController.getRemote();

    @NonNull
    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void setUpContent(@NonNull View view) {
        if (null != LoaderCache.getAllTracksList() && !LoaderCache.getAllTracksList().isEmpty()) {
            TaskRunner.executeAsync(() -> {
                boolean isTopAlbumsEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.HOME_PLAYLIST_TOP_ALBUMS);
                boolean isForYouEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.HOME_PLAYLIST_FOR_YOU);
                boolean isRediscoverEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.HOME_PLAYLIST_REDISCOVER);
                boolean isNewInLibraryEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.HOME_PLAYLIST_NEW_IN_LIBRARY);
                boolean isTopArtistEnabled = AppSettings.isPlaylistSectionEnabled(requireContext(), Preferences.HOME_PLAYLIST_TOP_ARTIST);

                if (isTopAlbumsEnabled)
                    LoaderHelper.loadTopAlbums(result -> loadTopAlbums(view, result));

                if (isForYouEnabled) {
                    LoaderHelper.loadSuggestionsList(result -> {
                        loadSuggestions(view, result);
                        if (isRediscoverEnabled) {
                            // We kick start rediscover load after loading suggestions
                            // so that we can use Suggestions as exclusion list
                            LoaderHelper.loadRediscoverSection(LoaderCache.getSuggestions(),
                                    rediscoverList -> loadRediscoverSection(view, rediscoverList));
                        }
                    });
                } else if (isRediscoverEnabled) {
                    LoaderHelper.loadRediscoverSection(null,
                            rediscoverList -> loadRediscoverSection(view, rediscoverList));
                }

                if (isNewInLibraryEnabled)
                    LoaderHelper.loadLatestTracks(result -> loadLatestTracks(view, result));

                if (isTopArtistEnabled)
                    LoaderHelper.loadTopArtist(result -> loadTopArtists(view, result));

            });
        } else {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
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
                    intent.putExtra(PMS.PLAY_KEY, PMS.PLAY_SHUFFLE);
                    requireActivity().startService(intent);
                });
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
            suggestionsTitle.setText(getString(R.string.random));
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_suggested_list)).inflate();
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
            rv.setHasFixedSize(true);
            HomeSectionAdapter adapter = new HomeSectionAdapter(getLayoutInflater(), list, new SimpleItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mPulseController.setPlaylist(list, position);
                    mRemote.play();
                }

                @Override
                public void onOptionsClick(int position) {
                    UIHelper.showMenuForLibraryTracks(requireActivity(), requireFragmentManager(), list.get(position));
                }
            });
            rv.setAdapter(adapter);
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
            HomeSectionAdapter adapter = new HomeSectionAdapter(getLayoutInflater(), list, new SimpleItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mPulseController.setPlaylist(list, position);
                    mRemote.play();
                }

                @Override
                public void onOptionsClick(int position) {
                    UIHelper.showMenuForLibraryTracks(requireActivity(), requireFragmentManager(), list.get(position));
                }
            });
            rv.setAdapter(adapter);
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
            HomeSectionAdapter adapter = new HomeSectionAdapter(getLayoutInflater(), list, new SimpleItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mPulseController.setPlaylist(list, position);
                    mRemote.play();
                }

                @Override
                public void onOptionsClick(int position) {
                    UIHelper.showMenuForLibraryTracks(requireActivity(), requireFragmentManager(), list.get(position));
                }
            });
            rv.setAdapter(adapter);
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
                    NavigationUtil.goToArtist(requireActivity(), sharedView, list.get(position).getArtistName()));
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
                mPulseController.setPlaylist(singlePickedItemList, 0);
                mRemote.play();
            } else
                Toast.makeText(requireContext(), getString(R.string.selected_track_load_failed_toast), Toast.LENGTH_SHORT).show();
        }
    }
}