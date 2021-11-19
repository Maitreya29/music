package com.radiant.music.interfaces;

import androidx.annotation.NonNull;

import com.radiant.music.dialog.MenuDetailsDialog;

public interface OptionsMenuListener {

    void onMenuDetailsDialogCreated(int groupId, @NonNull MenuDetailsDialog detailsDialog);

    void onItemSelected(int groupId, int selectedItemId);
}