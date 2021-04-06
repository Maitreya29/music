package com.nezukoos.music.loaders;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.nezukoos.music.TaskRunner;
import com.nezukoos.music.TaskRunner.Callback;
import com.nezukoos.music.model.AlbumModel;
import com.nezukoos.music.model.ArtistModel;
import com.nezukoos.music.model.Folder;
import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.model.TopAlbumModel;
import com.nezukoos.music.model.TopArtistModel;
import com.nezukoos.music.providers.ProviderManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LoaderManager {

    private static List<MusicModel> sMaster = null;
    private static List<MusicModel> sRediscover = null;
    private static List<MusicModel> sSuggestions = null;
    private static List<MusicModel> sLatest = null;

    public static void loadMaster(@NonNull Context context, @NonNull Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            // Since we are loading the master list
            // Any cached tracks of previous session must be made invalid
            clearCache();
            Callable<List<MusicModel>> libraryLoader;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                libraryLoader = new LibraryLoaderQ(context, SortOrder.TITLE_ASC);
            else
                libraryLoader = new LibraryLoader(context, SortOrder.TITLE_ASC);

            TaskRunner.executeAsync(libraryLoader, master -> {
                if (null != sMaster) {
                    // Let gc do its work
                    sMaster.clear();
                    sMaster = null;
                }
                if (null != master) sMaster = new ArrayList<>(master);

                ProviderManager.getHistoryProvider().deleteObsoleteHistoryFiles(success ->
                        callback.onComplete(!isMasterListEmpty()));
            });
        });
    }

    @Nullable
    public static List<MusicModel> getCachedMasterList() {
        return sMaster;
    }

    public static boolean isMasterListEmpty() {
        return sMaster == null || sMaster.isEmpty();
    }

    public static void getSuggestionsList(@NonNull Callback<List<MusicModel>> callback) {
        if (null == sSuggestions) TaskRunner.executeAsync(new SuggestionsLoader(), suggestions -> {
            if (null != sSuggestions) {
                // Let gc do its work
                sSuggestions.clear();
                sSuggestions = null;
            }
            if (null != suggestions) sSuggestions = new ArrayList<>(suggestions);
            callback.onComplete(sSuggestions);
        });
        else callback.onComplete(sSuggestions);
    }

    public static void getRediscoverList(@Nullable List<MusicModel> exclusionsList,
                                         @NonNull Callback<List<MusicModel>> callback) {
        if (null == sRediscover)
            TaskRunner.executeAsync(new RediscoverLoader(exclusionsList), rediscover -> {
                if (null != sRediscover) {
                    // Let gc do its work
                    sRediscover.clear();
                    sRediscover = null;
                }
                if (null != rediscover) sRediscover = new ArrayList<>(rediscover);
                callback.onComplete(sRediscover);
            });
        else callback.onComplete(sRediscover);
    }

    public static void getLatestTracksList(@NonNull Callback<List<MusicModel>> callback) {
        if (null == sLatest) TaskRunner.executeAsync(new LatestTracksLoader(), latestTracks -> {
            if (null != sLatest) {
                // Let gc do its work
                sLatest.clear();
                sLatest = null;
            }
            if (null != latestTracks) sLatest = new ArrayList<>(latestTracks);
            callback.onComplete(sLatest);
        });
        else callback.onComplete(sLatest);
    }

    public static void loadAlbumsList(@NonNull ContentResolver contentResolver,
                                      @Nullable SortOrder.ALBUMS sortOrder,
                                      @NonNull Callback<List<AlbumModel>> callback) {
        TaskRunner.executeAsync(new AlbumsLoader(contentResolver, sortOrder), callback);
    }

    public static void loadArtistsList(@NonNull ContentResolver contentResolver,
                                       @Nullable SortOrder.ARTIST sortOrder,
                                       @NonNull Callback<List<ArtistModel>> callback) {
        TaskRunner.executeAsync(new ArtistsLoader(contentResolver, sortOrder), callback);
    }

    public static void loadTopAlbums(@NonNull Callback<List<TopAlbumModel>> callback) {
        TaskRunner.executeAsync(new TopAlbumsLoader(), callback);
    }

    public static void loadTopArtist(@NonNull Callback<List<TopArtistModel>> callback) {
        TaskRunner.executeAsync(new TopArtistsLoader(), callback);
    }

    public static void loadAlbumTracks(long albumId,
                                       @NonNull SortOrder sortOrder,
                                       @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(new AlbumTracksLoader(albumId, sortOrder), callback);
    }

    public static void loadArtistAlbums(@NonNull ContentResolver contentResolver,
                                        long artistId,
                                        @NonNull String artistTitle,
                                        @NonNull SortOrder.ALBUMS sortOrder,
                                        @NonNull Callback<List<AlbumModel>> callback) {
        TaskRunner.executeAsync(new ArtistAlbumsLoader(contentResolver, artistId, artistTitle, sortOrder), callback);
    }

    public static void loadRecentTracks(@NonNull Callback<List<MusicModel>> callback) {
        ProviderManager.getHistoryProvider().getHistoryTracks(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void loadTracksRelativePath(@NonNull Context context,
                                              @NonNull Callback<List<Folder>> callback) {
        TaskRunner.executeAsync(new TracksPathLoader(context), callback);
    }

    public static void clearCache() {
        if (sSuggestions != null) sSuggestions.clear();
        sSuggestions = null;
        if (sLatest != null) sLatest.clear();
        sLatest = null;
        if (null != sRediscover) sRediscover.clear();
        sRediscover = null;
        if (sMaster != null) sMaster.clear();
        sMaster = null;
    }
}