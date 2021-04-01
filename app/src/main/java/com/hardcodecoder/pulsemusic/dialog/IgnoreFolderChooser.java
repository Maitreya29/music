package com.hardcodecoder.pulsemusic.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.main.MediaFolderChooserActivity;
import com.hardcodecoder.pulsemusic.adapters.bottomsheet.IgnoredFoldersAdapter;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheet;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IgnoreFolderChooser extends RoundedCustomBottomSheetFragment {

    public static final String TAG = IgnoreFolderChooser.class.getSimpleName();
    private static final int REQUEST_CODE_FOLDER_SELECT = 44;
    private static final int REQUEST_CODE_FOLDER_SELECT_Q = 45;
    private IgnoredFoldersAdapter mAdapter;
    private MaterialTextView mEmptyListText;
    private DialogDismissCallback mCallback;
    private boolean mHasChanged = false;

    @NonNull
    public static IgnoreFolderChooser getInstance(DialogDismissCallback callback) {
        IgnoreFolderChooser folderChooser = new IgnoreFolderChooser();
        folderChooser.mCallback = callback;
        return folderChooser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_item_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialTextView title = view.findViewById(R.id.bottom_dialog_picker_title);
        title.setText(R.string.ignored_folder_picker_title);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            ImageView manualPath = view.findViewById(R.id.bottom_dialog_picker_edit);
            manualPath.setVisibility(View.VISIBLE);
            manualPath.setImageResource(R.drawable.ic_edit);
            manualPath.setOnClickListener(v -> createPathInputDialog());
        }

        ImageView addBtn = view.findViewById(R.id.bottom_dialog_picker_add_btn);
        addBtn.setImageResource(R.drawable.ic_folder_add);
        addBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startActivityForResult(new Intent(requireActivity(), MediaFolderChooserActivity.class), REQUEST_CODE_FOLDER_SELECT_Q);
            } else {
                // Open folder picker
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, REQUEST_CODE_FOLDER_SELECT);
            }
        });

        ProviderManager.getIgnoredListProvider().getIgnoredList(this::setUpRecyclerView);
    }

    private void createPathInputDialog() {
        View layout = View.inflate(requireContext(), R.layout.bsd_edit_text, null);
        BottomSheetDialog sheetDialog = new RoundedCustomBottomSheet(layout.getContext(), RoundedCustomBottomSheet::setDefaultBehaviour);
        sheetDialog.setContentView(layout);
        sheetDialog.show();

        ((MaterialTextView) layout.findViewById(R.id.header)).setText(getResources().getString(R.string.ignored_folder_enter_path_title));
        ((TextInputLayout) layout.findViewById(R.id.edit_text_container)).setHint(getResources().getString(R.string.hint_ignored_folder_enter_path));
        TextInputEditText et = layout.findViewById(R.id.text_input_field);

        layout.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            if (et.getText() == null) return;
            String path = et.getText().toString().trim();
            if (path.length() == 0) return;
            handleSelectedFolderPath(path);
            if (sheetDialog.isShowing()) sheetDialog.dismiss();
        });

        layout.findViewById(R.id.cancel_btn).setOnClickListener(v -> {
            if (sheetDialog.isShowing()) sheetDialog.dismiss();
        });
    }

    private void setUpRecyclerView(List<String> foldersList) {
        View rootView = getView();
        if (null == rootView) return;
        if (null == foldersList || foldersList.size() <= 0) {
            mEmptyListText = (MaterialTextView) ((ViewStub) rootView.findViewById(R.id.stub_empty_list_text)).inflate();
            mEmptyListText.setText(R.string.ignored_folder_picker_empty_text);
            return;
        }

        if (null != mEmptyListText) mEmptyListText.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView) ((ViewStub) rootView.findViewById(R.id.bottom_dialog_picker_stub_rv)).inflate();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
        mAdapter = new IgnoredFoldersAdapter(
                foldersList,
                getLayoutInflater(),
                position -> {
                    // Remove folder when clicked on remove btn
                    ProviderManager.getIgnoredListProvider().removeFromIgnoreList(foldersList.get(position));
                    mAdapter.deleteItem(position);
                    mHasChanged = true;
                    Toast.makeText(requireContext(), getString(R.string.toast_removed_folder_from_ignored_list), Toast.LENGTH_SHORT).show();
                });
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOLDER_SELECT) handleSelectedFolders(data);
            else if (requestCode == REQUEST_CODE_FOLDER_SELECT_Q) handleSelectedFoldersQ(data);
        }
    }

    private void handleSelectedFolders(@Nullable Intent data) {
        if (null == data || null == data.getData()) {
            Toast.makeText(requireContext(), getString(R.string.toast_failed_to_add_folder), Toast.LENGTH_SHORT).show();
            return;
        }
        String completePath = getPath(data);
        if (null == completePath) {
            Toast.makeText(requireContext(), getString(R.string.toast_failed_to_add_folder), Toast.LENGTH_SHORT).show();
            return;
        }
        handleSelectedFolderPath(completePath);
    }

    private void handleSelectedFoldersQ(@Nullable Intent data) {
        if (null == data) {
            Toast.makeText(requireContext(), getString(R.string.toast_failed_to_add_folder), Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> ignoredFoldersLList = data.getStringArrayListExtra(MediaFolderChooserActivity.RESULT_SELECTED_FOLDERS);
        if (null == ignoredFoldersLList) return;
        if (null == mAdapter) {
            setUpRecyclerView(ignoredFoldersLList);
        } else {
            mAdapter.addItems(ignoredFoldersLList);
        }
        mHasChanged = true;
        Toast.makeText(requireContext(), getString(R.string.toast_folder_added_to_ignored_list), Toast.LENGTH_SHORT).show();
        ProviderManager.getIgnoredListProvider().addToIgnoreList(ignoredFoldersLList);
    }

    private void handleSelectedFolderPath(@NonNull String path) {
        if (!path.startsWith(File.separator)) path = "" + File.separator + path;
        if (!path.endsWith(File.separator)) path += File.separator;

        LogUtils.logInfo(TAG, "Final Path: " + path);
        if (null == mAdapter) {
            List<String> list = new ArrayList<>();
            list.add(path);
            setUpRecyclerView(list);
        } else {
            mAdapter.addItem(path);
        }
        mHasChanged = true;
        Toast.makeText(requireContext(), getString(R.string.toast_folder_added_to_ignored_list), Toast.LENGTH_SHORT).show();
        ProviderManager.getIgnoredListProvider().addToIgnoreList(path);
    }

    @Nullable
    private String getPath(@NonNull Intent data) {
        Uri folder = data.getData();
        if (folder == null || folder.getPath() == null) return null;

        String[] paths = folder.getPath().split(":");
        String completePath = null;

        if (paths.length > 0) {
            // Length of "/tree/" = 5, we want everything from index 6
            String volumeIdentifier = paths[0].substring(6);
            if (volumeIdentifier.contains("primary")) volumeIdentifier = "0";
            completePath = File.separator + volumeIdentifier + File.separator
                    // paths[1] can be null if user selected root directory
                    + (paths.length == 1 ? "" : paths[1] + File.separator);
        }
        String logText = "Received path: " + folder.getPath() + "\nPaths: " + Arrays.toString(paths) + "\nCompletePath: " + completePath;
        LogUtils.logInfo(TAG, logText);
        return completePath;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mCallback.onDismissed(mHasChanged);
    }

    public interface DialogDismissCallback {
        void onDismissed(boolean hasChanged);
    }
}