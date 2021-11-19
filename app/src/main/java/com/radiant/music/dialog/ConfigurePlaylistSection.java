package com.radiant.music.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.Preferences;
import com.radiant.music.R;
import com.radiant.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.radiant.music.interfaces.OnDialogDismiss;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.utils.AppSettings;
import com.radiant.music.utils.DimensionsUtil;
import com.radiant.music.views.AccentColorSwitch;

public class ConfigurePlaylistSection extends RoundedCustomBottomSheetFragment {

    public static final String TAG = ConfigurePlaylistSection.class.getSimpleName();
    private OnDialogDismiss mDismissListener;
    private Context mContext;
    private boolean mIsTopAlbumsEnabled;
    private boolean mIsForYouEnabled;
    private boolean mIsRediscoverEnabled;
    private boolean mIsNewInLibraryEnabled;
    private boolean mIsTopArtistEnabled;

    @NonNull
    public static ConfigurePlaylistSection getInstance(OnDialogDismiss dismissListener) {
        ConfigurePlaylistSection sectionSelector = new ConfigurePlaylistSection();
        sectionSelector.mDismissListener = dismissListener;
        return sectionSelector;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_configure_home_playlist_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = requireContext();
        MaterialTextView topAlbumsTitle = view.findViewById(R.id.playlist_top_albums);
        MaterialTextView forYouTitle = view.findViewById(R.id.playlist_for_you);

        MaterialTextView rediscoverTitle = view.findViewById(R.id.playlist_rediscover);
        String rediscoverText = getString(R.string.rediscover);
        String rediscoverDesc = getString(R.string.rediscover_section_desc);

        SpannableString spannableString = new SpannableString(rediscoverText);
        spannableString.setSpan(
                new AbsoluteSizeSpan(DimensionsUtil.getDimensionPixelSize(mContext, 10)),
                rediscoverText.length(),
                spannableString.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ThemeColors.getCurrentSecondaryTextColor()),
                rediscoverText.length(),
                spannableString.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        rediscoverTitle.setText(spannableString);

        MaterialTextView newInLibraryTitle = view.findViewById(R.id.playlist_new_in_library);
        MaterialTextView topArtistTitle = view.findViewById(R.id.playlist_top_artist);

        AccentColorSwitch topAlbums = view.findViewById(R.id.switch_top_albums);
        AccentColorSwitch forYou = view.findViewById(R.id.switch_for_you);
        AccentColorSwitch rediscover = view.findViewById(R.id.switch_rediscover);
        AccentColorSwitch newInLibrary = view.findViewById(R.id.switch_new_in_library);
        AccentColorSwitch topArtist = view.findViewById(R.id.switch_top_artist);

        mIsTopAlbumsEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_TOP_ALBUMS);
        mIsForYouEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_FOR_YOU);
        mIsRediscoverEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_REDISCOVER);
        mIsNewInLibraryEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_NEW_IN_LIBRARY);
        mIsTopArtistEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_TOP_ARTIST);

        topAlbums.setChecked(mIsTopAlbumsEnabled);
        forYou.setChecked(mIsForYouEnabled);
        rediscover.setChecked(mIsRediscoverEnabled);
        newInLibrary.setChecked(mIsNewInLibraryEnabled);
        topArtist.setChecked(mIsTopArtistEnabled);

        topAlbumsTitle.setOnClickListener(v ->
                topAlbums.setChecked(!topAlbums.isChecked()));

        forYouTitle.setOnClickListener(v ->
                forYou.setChecked(!forYou.isChecked()));

        rediscoverTitle.setOnClickListener(v ->
                rediscover.setChecked(!rediscover.isChecked()));

        newInLibraryTitle.setOnClickListener(v ->
                newInLibrary.setChecked(!newInLibrary.isChecked()));

        topArtistTitle.setOnClickListener(v ->
                topArtist.setChecked(!topArtist.isChecked()));


        topAlbums.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_TOP_ALBUMS, isChecked)
        );

        forYou.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_FOR_YOU, isChecked)
        );

        rediscover.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_REDISCOVER, isChecked)
        );

        newInLibrary.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_NEW_IN_LIBRARY, isChecked)
        );

        topArtist.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_TOP_ARTIST, isChecked)
        );
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        boolean isTopAlbumsEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_TOP_ALBUMS);
        boolean isForYouEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_FOR_YOU);
        boolean isRediscoverEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_REDISCOVER);
        boolean isNewInLibraryEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_NEW_IN_LIBRARY);
        boolean isTopArtistEnabled = AppSettings.isPlaylistSectionEnabled(mContext, Preferences.KEY_HOME_PLAYLIST_TOP_ARTIST);

        mDismissListener.onDismissed(isTopAlbumsEnabled != mIsTopAlbumsEnabled ||
                isForYouEnabled != mIsForYouEnabled ||
                isRediscoverEnabled != mIsRediscoverEnabled ||
                isNewInLibraryEnabled != mIsNewInLibraryEnabled ||
                isTopArtistEnabled != mIsTopArtistEnabled);
    }
}