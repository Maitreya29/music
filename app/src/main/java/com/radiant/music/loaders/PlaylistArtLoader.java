package com.radiant.music.loaders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.model.MusicModel;
import com.radiant.music.utils.ImageUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class PlaylistArtLoader implements Callable<Bitmap> {

    private final Context mContext;
    private final List<MusicModel> mList;

    public PlaylistArtLoader(@NonNull Context context, @Nullable List<MusicModel> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public Bitmap call() {
        if (null == mList || mList.isEmpty()) return null;
        if (mList.size() < 4) {
            for (int i = 0; i < mList.size(); i++) {
                String albumArtUri = mList.get(i).getAlbumArtUrl();
                if (albumArtUri == null || albumArtUri.trim().isEmpty()) continue;
                Bitmap bm = decodeSampledBitmapFromUri(albumArtUri, 512, 512);
                if (null != bm) return bm;
            }
            return null;
        }

        final int[] usedIndices = new int[mList.size()];
        Arrays.fill(usedIndices, -1);

        // Load 4 images
        final int len = mList.size();
        int count = 0;
        int index = 0;

        final Bitmap[] parts = new Bitmap[4];

        while (count < 4 && index < len) {
            // No more indices left
            if (index == -1) break;
            // Add loaded index to used indices array
            usedIndices[index] = index;

            String albumArtUri = mList.get(index).getAlbumArtUrl();
            // Calculate gap from remaining length
            int gap = (len - index) / (4 - count);

            if (albumArtUri == null || albumArtUri.trim().isEmpty()) {
                // fetch new index to load in next iteration
                index = index + gap > len ? getUnusedIndex(usedIndices) : index + gap;
                continue;
            }

            Bitmap bm = decodeSampledBitmapFromUri(albumArtUri, 256, 256);

            if (null != bm) {
                // We have a bitmap, add to array
                parts[count] = bm;
                // Increase count by 1
                count++;
                // fetch new index to load in next iteration
                index = index + gap > len ? getUnusedIndex(usedIndices) : index + gap;
            } else {
                // Bitmap is null, try to fetch album art from the consecutive next track if possible
                index = (index < len - 1) ? index + 1 : getUnusedIndex(usedIndices);
            }
        }

        Bitmap result = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();

        for (int z = 0; z < parts.length; z++) {
            Bitmap bitmap = parts[z];
            if (null == bitmap) {
                // Unfortunately we couldn't get 4 bitmaps to make the art
                return null;
            }
            float left = bitmap.getWidth() * (z % 2);
            int top = bitmap.getHeight() * (z / 2);
            canvas.drawBitmap(bitmap, left, top, paint);
        }
        return result;
    }

    private int getUnusedIndex(@NonNull final int[] usedIndices) {
        for (int i = 0; i < usedIndices.length; i++) {
            if (usedIndices[i] == -1) return i;
        }
        return -1;
    }

    @SuppressLint("NewApi")
    @Nullable
    private Bitmap decodeSampledBitmapFromUri(@NonNull String uriString, int reqWidth, int reqHeight) {
        try {
            Uri uri = Uri.parse(uriString);
            return ImageUtil.getScaledBitmap(mContext.getContentResolver().openInputStream(uri), reqWidth, reqHeight);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}