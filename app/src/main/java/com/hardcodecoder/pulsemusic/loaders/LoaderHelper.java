package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.utils.SortUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LoaderHelper {

    public static void loadAllTracks(@NonNull ContentResolver contentResolver, @NonNull Callback<List<MusicModel>> callback) {
        // Since we are loading the master list
        // Any previous cached tracks must be made invalid
        LoaderCache.releaseCache();
        TaskRunner.executeAsync(new LibraryLoader(contentResolver, SortOrder.TITLE_ASC), result -> {
            LoaderCache.setAllTracksList(result);
            callback.onComplete(LoaderCache.getAllTracksList());
        });
    }

    public static void loadAlbumsList(ContentResolver contentResolver, SortOrder.ALBUMS sortOrder, @NonNull Callback<List<AlbumModel>> callback) {
        TaskRunner.executeAsync(new AlbumsLoader(contentResolver, sortOrder), callback);
    }

    public static void loadArtistsList(@NonNull ContentResolver contentResolver, SortOrder.ARTIST sortOrder, @NonNull Callback<List<ArtistModel>> callback) {
        TaskRunner.executeAsync(new ArtistsLoader(contentResolver, sortOrder), callback);
    }

    public static void loadSuggestionsList(@NonNull Callback<List<MusicModel>> callback) {
        if (null != LoaderCache.getSuggestions())
            callback.onComplete(LoaderCache.getSuggestions());
        else {
            if (null != LoaderCache.getAllTracksList()) {
                TaskRunner.executeAsync(() -> {
                    List<MusicModel> list = new ArrayList<>(LoaderCache.getAllTracksList());
                    if (list.size() > 0) {
                        Collections.shuffle(list);
                        int listSize = list.size();
                        // Consider 30 of 20% of listSize whichever is smaller
                        int minTwentyPercent = Math.min((int) (0.2 * listSize), 30);
                        // Find a random start index such that startIndex + minTwentyPercent < listSize
                        int startIndex = new Random().nextInt(listSize - minTwentyPercent);
                        // sublist the list from startIndex to startIndex + minTwentyPercent
                        final List<MusicModel> suggestionsList = list.subList(startIndex, startIndex + minTwentyPercent);
                        LoaderCache.setSuggestions(suggestionsList);
                        suggestionsList.clear();
                        list.clear();
                        callback.onComplete(LoaderCache.getSuggestions());
                    }
                });
            } else {
                // We have reached here means LoaderCache.getAllTracks() is null
                // Which indicated that the device has no media tracks
                // Return null to trigger any updates for the UI
                callback.onComplete(null);
            }
        }
    }

    public static void loadRecentTracks(@NonNull Callback<List<MusicModel>> callback) {
        ProviderManager.getHistoryProvider().getHistoryTracks(callback);
    }

    public static void loadLatestTracks(@NonNull Callback<List<MusicModel>> callback) {
        if (null != LoaderCache.getLatestTracks()) {
            callback.onComplete(LoaderCache.getLatestTracks());
        } else {
            if (null != LoaderCache.getAllTracksList()) {
                List<MusicModel> latestTracks = new ArrayList<>(LoaderCache.getAllTracksList());
                SortUtil.sortLibraryList(latestTracks, SortOrder.DATE_MODIFIED_DESC);
                latestTracks = latestTracks.subList(0, (int) (0.2 * latestTracks.size()));
                LoaderCache.setLatestTracks(latestTracks);
                latestTracks.clear();
                callback.onComplete(LoaderCache.getLatestTracks());
            } else {
                // We have reached here means LoaderCache.getAllTracks() is null
                // Which indicated that the device has no media tracks
                // Return null to trigger any updates for the UI
                callback.onComplete(null);
            }
        }
    }

    public static void loadTopAlbums(@NonNull Callback<List<TopAlbumModel>> callback) {
        TaskRunner.executeAsync(new TopAlbumsLoader(), callback);
    }

    public static void loadTopArtist(@NonNull Callback<List<TopArtistModel>> callback) {
        TaskRunner.executeAsync(new TopArtistsLoader(), callback);
    }
}