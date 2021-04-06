package com.nezukoos.music.loaders;

import com.nezukoos.music.model.MusicModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class SuggestionsLoader implements Callable<List<MusicModel>> {

    @Override
    public List<MusicModel> call() {
        List<MusicModel> master = LoaderManager.getCachedMasterList();
        if (master == null || master.isEmpty()) return null;

        List<MusicModel> list = new ArrayList<>(master);

        Collections.shuffle(list);
        int listSize = list.size();
        // Consider 30 of 20% of listSize whichever is smaller
        int minTwentyPercent = Math.min((int) (0.2 * listSize), 30);
        // Find a random start index such that startIndex + minTwentyPercent < listSize
        int startIndex = new Random().nextInt(listSize - minTwentyPercent);
        // sublist the list from startIndex to startIndex + minTwentyPercent
        return list.subList(startIndex, startIndex + minTwentyPercent);
    }
}