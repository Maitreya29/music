package com.hardcodecoder.pulsemusic.activities;

import android.media.session.MediaController;
import android.os.Bundle;
import android.transition.Fade;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.adapters.DetailsAdapter;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.ItemsLoader;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;
import java.util.Locale;

public class DetailsActivity extends MediaSessionActivity implements SimpleItemClickListener {

    public static final String KEY_ITEM_CATEGORY = "category";
    public static final String ALBUM_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ART_URL = "art";
    public static final String KEY_TRANSITION_NAME = "transition";
    public static final int CATEGORY_ALBUM = 1;
    public static final int CATEGORY_ARTIST = 2;
    private String title;
    private List<MusicModel> mList;
    private TrackManager tm;
    private Long mAlbumId;
    private int mCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportPostponeEnterTransition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setUpTransitions();

        tm = TrackManager.getInstance();
        mCategory = getIntent().getIntExtra(KEY_ITEM_CATEGORY, -1);
        mAlbumId = getIntent().getLongExtra(ALBUM_ID, 0);
        title = getIntent().getStringExtra(KEY_TITLE);
        loadImage();

        findViewById(R.id.details_activity_btn_close).setOnClickListener(v -> supportFinishAfterTransition());
        MaterialTextView detailsTitle = findViewById(R.id.details_activity_title);
        detailsTitle.setText(title);

        loadItems();
    }

    private void loadImage() {
        String transitionName = getIntent().getStringExtra(KEY_TRANSITION_NAME);
        String artUrl = getIntent().getStringExtra(KEY_ART_URL);
        MediaArtImageView sharedImageView = findViewById(R.id.details_activity_art);
        sharedImageView.setTransitionName(transitionName);

        if (mCategory == CATEGORY_ALBUM) sharedImageView.loadAlbumArt(artUrl, mAlbumId);
        else sharedImageView.setImageResource(R.drawable.ic_artist_art);
        supportStartPostponedEnterTransition();
    }

    private void loadItems() {
        TaskRunner.executeAsync(new ItemsLoader(title, mCategory), (data) -> {
            if (data.size() > 0) {
                mList = data;
                MaterialTextView sub = findViewById(R.id.details_activity_title_sub);
                sub.setText(String.format(Locale.ENGLISH, "%s %d %s", getString(R.string.num_tracks), mList.size(), getString(R.string.tracks)));

                RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_details_activity_rv)).inflate();
                rv.setHasFixedSize(true);
                rv.setVerticalFadingEdgeEnabled(true);
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL, false));
                DetailsAdapter adapter = new DetailsAdapter(mList, this, getLayoutInflater());
                rv.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onItemClick(int pos) {
        if (null != mList) {
            tm.buildDataList(mList, pos);
            playMedia();
        }
    }

    @Override
    public void onOptionsClick(int pos) {
        UIHelper.buildAndShowOptionsMenu(this, getSupportFragmentManager(), mList.get(pos));
    }

    private void setUpTransitions() {
        Fade enterFade = new Fade();
        enterFade.excludeTarget(android.R.id.statusBarBackground, true);
        enterFade.excludeTarget(android.R.id.navigationBarBackground, true);
        enterFade.excludeTarget(R.id.stub_details_activity_rv, true);
        enterFade.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        getWindow().setEnterTransition(enterFade);
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
