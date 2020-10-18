package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentUris;
import android.net.Uri;

import com.hardcodecoder.pulsemusic.model.HistoryRecord;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TopAlbumsLoader implements Callable<List<TopAlbumModel>> {

    @Override
    public List<TopAlbumModel> call() {
        List<HistoryRecord> records = ProviderManager.getHistoryProvider().getHistoryRecords();
        if (null == records || records.isEmpty()) return null;

        Map<String, Integer> frequency = new HashMap<>();
        Map<String, HistoryRecord> modelMap = new HashMap<>();

        for (HistoryRecord hr : records) {
            Integer count = frequency.get(hr.getAlbum());
            frequency.put(hr.getAlbum(), (null == count) ? hr.getPlayCount() : count + hr.getPlayCount());
            modelMap.put(hr.getAlbum(), hr);
        }

        List<TopAlbumModel> topAlbums = new ArrayList<>();

        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            HistoryRecord hr = modelMap.get(entry.getKey());
            if (null != hr) {
                String albumArt = ContentUris.withAppendedId(sArtworkUri, hr.getAlbumId()).toString();
                topAlbums.add(new TopAlbumModel(hr.getAlbum(), albumArt, hr.getAlbumId(), entry.getValue()));
            }
        }

        Collections.sort(topAlbums, (o1, o2) -> {
            int count = o2.getPlayCount() - o1.getPlayCount();
            if (count == 0) {
                HistoryRecord h1 = modelMap.get(o1.getAlbumName());
                HistoryRecord h2 = modelMap.get(o2.getAlbumName());
                return Long.compare(h2.getLastModified(), h1.getLastModified());
            }
            return count;
        });
        return topAlbums;
    }
}