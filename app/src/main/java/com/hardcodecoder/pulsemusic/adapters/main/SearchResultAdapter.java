package com.hardcodecoder.pulsemusic.adapters.main;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.DiffCb;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SearchResultAdapter extends TracksAdapter {

    private final Deque<List<MusicModel>> pendingUpdates = new ArrayDeque<>();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    public SearchResultAdapter(@NonNull LayoutInflater inflater,
                               @NonNull SimpleItemClickListener listener) {
        super(inflater, new ArrayList<>(), listener, null, null);
    }

    public void updateItems(final List<MusicModel> newItems) {
        pendingUpdates.push(newItems);
        if (pendingUpdates.size() > 1) return;
        updateItemsInternal(newItems);
    }

    private void updateItemsInternal(final List<MusicModel> newItems) {
        TaskRunner.executeAsync(() -> {
            final List<MusicModel> oldItems = new ArrayList<>(getDataList());
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCb(oldItems, newItems));
            mMainHandler.post(() -> applyDiffResult(newItems, diffResult));
        });
    }

    private void applyDiffResult(List<MusicModel> newItems, DiffUtil.DiffResult diffResult) {
        pendingUpdates.remove(newItems);
        dispatchUpdates(newItems, diffResult);
        if (pendingUpdates.size() > 0) {
            List<MusicModel> latest = pendingUpdates.pop();
            pendingUpdates.clear();
            updateItemsInternal(latest);
        }
    }

    private void dispatchUpdates(List<MusicModel> newItems, @NonNull DiffUtil.DiffResult diffResult) {
        diffResult.dispatchUpdatesTo(this);
        getDataList().clear();
        getDataList().addAll(newItems);
    }
}