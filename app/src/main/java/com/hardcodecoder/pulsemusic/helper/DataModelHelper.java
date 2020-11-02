package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TrackFileModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataModelHelper {

    private static int mPickedTrackId = -1;

    @Nullable
    public static List<MusicModel> getModelObjectFromId(List<Integer> idList) {
        if (null == idList || idList.isEmpty()) return null;

        Map<Integer, MusicModel> modelMap = new HashMap<>();

        for (MusicModel musicModel : LoaderCache.getAllTracksList())
            modelMap.put(musicModel.getId(), musicModel);

        List<MusicModel> modelList = new ArrayList<>();
        for (Integer id : idList) {
            MusicModel md = modelMap.get(id);
            if (null != md) modelList.add(md);
        }
        return modelList;
    }

    @Nullable
    public static MusicModel getModelFromId(int id) {
        for (MusicModel md : LoaderCache.getAllTracksList()) {
            if (md.getId() == id) return md;
        }
        return null;
    }

    public static MusicModel buildMusicModelFrom(Context context, Intent data) {
        String path = data.getDataString();
        if (null == path) return null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, data.getData());
        String defText = context.getString(R.string.def_track_title);
        String title;
        String album;
        String artist;
        String dateAdded;
        long dateModified;
        int duration;
        try {
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            dateAdded = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            dateModified = null == dateAdded ? 0 : Long.parseLong(dateAdded);
            duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            mmr.release();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new MusicModel(
                mPickedTrackId,
                null == title ? defText : title,
                null == album ? defText : album,
                mPickedTrackId--,
                null == artist ? defText : artist,
                data.getDataString(),
                null,
                dateModified,
                dateModified,
                0,
                duration);
    }

    static void getTrackInfo(Context context, MusicModel musicModel, TaskRunner.Callback<TrackFileModel> callback) {
        TaskRunner.executeAsync(() -> {
            Uri uri = Uri.parse(musicModel.getTrackPath());
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri, null, null, null, null);

            if (null != cursor && cursor.moveToFirst()) {
                MediaExtractor mediaExtractor = new MediaExtractor();
                try {
                    mediaExtractor.setDataSource(context, uri, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                String displayName = cursor.getString(nameIndex);
                long fileSize = cursor.getLong(sizeIndex);
                String mimeType = context.getContentResolver().getType(uri);

                MediaFormat mediaFormat = mediaExtractor.getTrackFormat(0);
                int bitRate = 0;
                int sampleRate = 0;
                int channelCount = 1;
                try {
                    bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE);
                    sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                cursor.close();
                TrackFileModel trackFileModel = new TrackFileModel(displayName, mimeType, fileSize, bitRate, sampleRate, channelCount);
                callback.onComplete(trackFileModel);
            }
        });
    }
}