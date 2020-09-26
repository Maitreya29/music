package com.hardcodecoder.pulsemusic.activities.base;

import android.media.session.MediaController;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.MediaSessionActivity;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.themes.TintHelper;

import java.util.ArrayList;
import java.util.Collections;
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
        Toolbar toolbar = findViewById(R.id.material_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
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

    protected void setShuffleButtonAction(View.OnClickListener listener) {
        MaterialButton shuffleBtn = findViewById(R.id.playlist_shuffle_btn);
        TintHelper.setAccentTintToMaterialButton(shuffleBtn);
        shuffleBtn.setOnClickListener(listener);
    }

    protected void setUpDynamicButton(@StringRes int stringId, @DrawableRes int drawableId, View.OnClickListener listener) {
        MaterialButton dynamicBtn = findViewById(R.id.playlist_dynamic_btn);
        TintHelper.setAccentTintToMaterialOutlineButton(dynamicBtn);
        dynamicBtn.setText(stringId);
        dynamicBtn.setIcon(ContextCompat.getDrawable(this, drawableId));
        dynamicBtn.setOnClickListener(listener);
    }

    protected void setTrackAndPlay(List<MusicModel> playlist, int startIndex) {
        mTrackManager.buildDataList(playlist, startIndex);
        playMedia();
    }

    protected void shuffleTrackAndPlay(List<MusicModel> playlist) {
        if (null == playlist || playlist.size() <= 0)
            return;
        List<MusicModel> playListToPlay = new ArrayList<>(playlist);
        Collections.shuffle(playListToPlay);
        mTrackManager.buildDataList(playListToPlay, 0);
        playMedia();
        Toast.makeText(this, getString(R.string.playlist_shuffled_success_toast), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}