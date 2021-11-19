package com.radiant.music.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.radiant.music.activities.details.AlbumDetailsActivity;
import com.radiant.music.activities.details.ArtistDetailsActivity;

public class NavigationUtil {

    public static void goToAlbum(@NonNull Activity activity,
                                 @Nullable View sharedView,
                                 @NonNull String albumName,
                                 long albumId,
                                 @NonNull String albumArt) {
        Intent i = new Intent(activity, AlbumDetailsActivity.class);
        i.putExtra(AlbumDetailsActivity.KEY_ALBUM_ID, albumId);
        i.putExtra(AlbumDetailsActivity.KEY_ALBUM_TITLE, albumName);
        i.putExtra(AlbumDetailsActivity.KEY_ALBUM_ART_URL, albumArt);
        if (null != sharedView) {
            String transitionName = sharedView.getTransitionName();
            i.putExtra(AlbumDetailsActivity.KEY_TRANSITION_NAME, transitionName);
            Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView, transitionName).toBundle();
            activity.startActivity(i, b);
        }
    }

    /**
     * @param activity   The Activity whose window contains the shared elements.
     * @param sharedView The View to transition to the started Activity. sharedElement must
     *                   have a non-null sharedElementName.
     * @param artistName The name of the artist whose details page needs to be shown. This cannot be null
     * @param artistId   The id of the artist if available, else -1
     */
    public static void goToArtist(@NonNull Activity activity,
                                  @Nullable View sharedView,
                                  @NonNull String artistName,
                                  long artistId) {
        Intent i = new Intent(activity, ArtistDetailsActivity.class);
        i.putExtra(ArtistDetailsActivity.KEY_ARTIST_ID, artistId);
        i.putExtra(ArtistDetailsActivity.KEY_ARTIST_TITLE, artistName);
        if (null != sharedView) {
            String transitionName = sharedView.getTransitionName();
            i.putExtra(ArtistDetailsActivity.KEY_TRANSITION_NAME, transitionName);
            Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView, transitionName).toBundle();
            activity.startActivity(i, b);
        }
    }

    public static void goToArtist(@NonNull Context context, @NonNull String artistName) {
        Intent i = new Intent(context, ArtistDetailsActivity.class);
        i.putExtra(ArtistDetailsActivity.KEY_ARTIST_TITLE, artistName);
        context.startActivity(i);
    }
}