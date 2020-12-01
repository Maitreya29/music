package com.hardcodecoder.pulsemusic.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class MediaArtCollageLoader implements Callable<Bitmap> {

    private final Context mContext;
    private final List<MusicModel> mList;

    public MediaArtCollageLoader(@NonNull Context context, @Nullable List<MusicModel> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public Bitmap call() {
        if (null == mList || mList.isEmpty()) return null;
        if (mList.size() < 4) {
            for (int i = 0; i < mList.size(); i++) {
                String albumArtUrl = mList.get(i).getAlbumArtUrl();
                if (albumArtUrl == null || albumArtUrl.trim().isEmpty()) continue;
                Bitmap bm = decodeSampledBitmapFromUri(Uri.parse(albumArtUrl), 512, 512);
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

            String albumArtUrl = mList.get(index).getAlbumArtUrl();
            // Calculate gap from remaining length
            int gap = (len - index) / (4 - count);

            if (albumArtUrl == null || albumArtUrl.trim().isEmpty()) {
                // fetch new index to load in next iteration
                index = index + gap > len ? getUnusedIndex(usedIndices) : index + gap;
                continue;
            }

            Uri uri = Uri.parse(albumArtUrl);
            Bitmap bm = decodeSampledBitmapFromUri(uri, 256, 256);

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

    @Nullable
    private Bitmap decodeSampledBitmapFromUri(@NonNull Uri uri, int reqWidth, int reqHeight) {
        try {
            InputStream stream = mContext.getContentResolver().openInputStream(uri);
            if (null == stream) {
                return null;
            }

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, null, options);

            // Calculate inSampleSize
            options.inSampleSize = ImageUtil.calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            stream = mContext.getContentResolver().openInputStream(uri);
            return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(stream, null, options), reqWidth, reqHeight, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}