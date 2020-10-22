package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.HistoryRecord;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TopArtistsLoader implements Callable<List<TopArtistModel>> {


    @Override
    public List<TopArtistModel> call() {
        List<HistoryRecord> records = ProviderManager.getHistoryProvider().getHistoryRecords();
        if (null == records || records.isEmpty()) return null;

        Map<String, Integer> frequency = new HashMap<>();
        Map<String, HistoryRecord> modelMap = new HashMap<>();

        for (HistoryRecord hr : records) {
            Integer count = frequency.get(hr.getAlbum());
            frequency.put(hr.getArtist(), (null == count) ? hr.getPlayCount() : count + hr.getPlayCount());
            modelMap.put(hr.getArtist(), hr);
        }

        List<TopArtistModel> topArtistList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : frequency.entrySet())
            topArtistList.add(new TopArtistModel(entry.getKey(), entry.getValue()));

        Collections.sort(topArtistList, (o1, o2) -> {
            int count = o2.getNumOfPlays() - o1.getNumOfPlays();
            if (count == 0) {
                HistoryRecord h1 = modelMap.get(o1.getArtistName());
                HistoryRecord h2 = modelMap.get(o2.getArtistName());
                return Long.compare(h2.getLastModified(), h1.getLastModified());
            }
            return count;
        });

        // Limit to 20 TopAlbums
        if (topArtistList != null)
            topArtistList = topArtistList.subList(0, Math.min(topArtistList.size(), 20));
        return topArtistList;
    }
}