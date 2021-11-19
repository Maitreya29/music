package com.radiant.music.helper;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.R;
import com.radiant.music.TaskRunner;
import com.radiant.music.loaders.LoaderManager;
import com.radiant.music.model.MusicModel;
import com.radiant.music.model.TrackFileModel;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.utils.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.radiant.music.utils.LogUtils.Type.GENERAL;

public class DataModelHelper {

    /**
     * This is used to indicate the id of tracks and album id of the track
     * selected by the user via select track options
     * <p>
     * -1 as albumId is used for tinting default album art with {@link ThemeColors#getCurrentColorPrimary()}
     */
    private static int mPickedTrackId = -2;

    @Nullable
    public static List<MusicModel> getModelObjectFromId(@Nullable List<Integer> idList) {
        List<MusicModel> masterList = LoaderManager.getCachedMasterList();
        if (null == masterList || masterList.isEmpty() || null == idList || idList.isEmpty())
            return null;

        Map<Integer, MusicModel> modelMap = new HashMap<>();

        for (MusicModel musicModel : masterList)
            modelMap.put(musicModel.getId(), musicModel);

        List<MusicModel> modelList = new ArrayList<>();
        for (Integer id : idList) {
            MusicModel md = modelMap.get(id);
            if (null != md) modelList.add(md);
        }
        return modelList;
    }

    @Nullable
    public static MusicModel buildMusicModelFrom(@NonNull Context context, @Nullable Uri data) {
        if (null == data) return null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, data);
        String defText = context.getString(R.string.unknown);
        String title;
        String album;
        String artist;
        String dateAdded;
        long dateModified;
        int[] discTrackNumber = new int[2];
        int duration;
        try {
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            dateAdded = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            dateModified = null == dateAdded ? 0 : Long.parseLong(dateAdded);
            duration = getNumber(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            discTrackNumber[0] = getNumber(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER));
            discTrackNumber[1] = getNumber(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
            mmr.release();
        } catch (Exception e) {
            LogUtils.logException(GENERAL, "DataModelHelper", "at buildMusicModelFrom()", e);
            return null;
        }
        return new MusicModel(
                mPickedTrackId,
                null == title ? defText : title,
                null == album ? defText : album,
                mPickedTrackId--,
                null == artist ? defText : artist,
                data.toString(),
                "",
                dateModified,
                dateModified,
                discTrackNumber[0],
                discTrackNumber[1],
                duration);
    }

    public static void getItemIndexInPlaylist(@NonNull List<MusicModel> playlist, @NonNull MusicModel item,
                                              @NonNull TaskRunner.Callback<Integer> callback) {
        TaskRunner.executeAsync(() -> {
            int index = -1;
            for (int i = 0; i < playlist.size(); i++) {
                MusicModel md = playlist.get(i);
                if (md.equals(item)) {
                    index = i;
                    break;
                }
            }
            int finalIndex = index;
            TaskRunner.getMainHandler().post(() -> callback.onComplete(finalIndex));
        });
    }

    static void getTrackInfo(@NonNull Context context, @NonNull MusicModel musicModel, @NonNull TaskRunner.Callback<TrackFileModel> callback) {
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
                    LogUtils.logException(GENERAL, "DataModelHelper", "at getTrackInfo#settingDataSource", e);
                }

                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                String displayName = cursor.getString(nameIndex);
                long fileSize = cursor.getLong(sizeIndex);
                String mimeType = context.getContentResolver().getType(uri);

                MediaFormat mediaFormat = mediaExtractor.getTrackFormat(0);
                Integer bitRate = null;
                Integer sampleRate = null;
                Integer channelCount = null;
                try {
                    bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE);
                    sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                } catch (NullPointerException e) {
                    LogUtils.logException(GENERAL, "DataModelHelper", "at getTrackInfo#extractingInfo", e);
                }
                cursor.close();
                TrackFileModel trackFileModel = new TrackFileModel(
                        displayName,
                        mimeType,
                        fileSize,
                        bitRate == null ? 0 : bitRate,
                        sampleRate == null ? 0 : sampleRate,
                        channelCount == null ? 0 : channelCount);
                callback.onComplete(trackFileModel);
            }
        });
    }

    private static int getNumber(@Nullable String str) {
        if (null == str || str.length() == 0) return 1;
        try {
            if (str.contains("/"))
                return Integer.parseInt(str.substring(0, str.indexOf("/")));
            else if (str.matches("[0-9]+"))
                return Integer.parseInt(str);
        } catch (Exception e) {
            LogUtils.logException(GENERAL, "DataModelHelper", "at getNumber() with input:" + str, e);
        }
        return 1;
    }
}