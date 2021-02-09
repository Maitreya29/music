package com.hardcodecoder.pulsemusic.interfaces;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.dialog.MenuDetailsDialog;

public interface OptionsMenuListener {

    void onMenuDetailsDialogCreated(int groupId, @NonNull MenuDetailsDialog detailsDialog);

    void onItemSelected(int groupId, int selectedItemId);
}