package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.hardcodecoder.pulsemusic.model.Folder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static android.os.Build.VERSION_CODES.Q;

@RequiresApi(api = Q)
public class TracksPathLoader implements Callable<List<Folder>> {

    private final ContentResolver mContentResolver;

    public TracksPathLoader(@NonNull Context context) {
        mContentResolver = context.getContentResolver();
    }

    @Override
    public List<Folder> call() {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {
                MediaStore.Audio.Media.VOLUME_NAME,       // 0
                MediaStore.Audio.Media.RELATIVE_PATH      // 1
        };

        final Cursor cursor = mContentResolver.query(
                uri,
                cursor_cols,
                null,
                null,
                null);

        List<Folder> relativePaths = null;
        if (cursor != null && cursor.moveToFirst()) {
            Set<Folder> uniquePaths = new HashSet<>();
            do {
                String vol = cursor.getString(0);
                String path = cursor.getString(1);
                uniquePaths.add(new Folder(vol, path));

            } while (cursor.moveToNext());

            if (!uniquePaths.isEmpty()) relativePaths = new ArrayList<>(uniquePaths);
            cursor.close();
        }

        if (relativePaths != null) {
            Collections.sort(relativePaths, (o1, o2) -> {
                int c1 = o1.getPaths().split(File.separator).length;
                int c2 = o2.getPaths().split(File.separator).length;
                if (c1 == c2) return o1.toString().compareTo(o2.toString());
                else return Integer.compare(c1, c2);
            });
        }
        return relativePaths;
    }
}