package com.hardcodecoder.pulsemusic.activities.base;

import android.media.session.MediaController;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.MediaSessionActivity;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.List;

public abstract class BasePlaylistActivity extends MediaSessionActivity {

    protected TrackManager mTrackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks);
        mTrackManager = TrackManager.getInstance();
    }

    protected void setUpToolbar(String title) {
        MaterialToolbar toolbar = findViewById(R.id.material_toolbar);
        toolbar.setTitle(title);
        //setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    protected SpannableStringBuilder getEmptyListStyledText() {
        String text = getString(R.string.no_recent_tracks);
        int len = text.length();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        stringBuilder.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.spannable_text_absolute_size_span)),
                len - 1,
                len,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }

    protected void setTrackAndPlay(List<MusicModel> playlist, int startIndex) {
        mTrackManager.buildDataList(playlist, startIndex);
        playMedia();
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
