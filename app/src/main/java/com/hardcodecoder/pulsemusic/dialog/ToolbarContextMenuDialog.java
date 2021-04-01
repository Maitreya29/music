package com.hardcodecoder.pulsemusic.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.model.MenuType;

import java.util.ArrayList;
import java.util.List;

public class ToolbarContextMenuDialog extends RoundedCustomBottomSheetFragment {

    public static final String TAG = ToolbarContextMenuDialog.class.getSimpleName();
    private final List<MenuType> mGroupItemList;
    private final OnMenuSelectedListener mGroupSelectedListener;

    public ToolbarContextMenuDialog(@NonNull List<MenuType> groupItemList,
                                    @NonNull OnMenuSelectedListener menuGroupListener) {
        mGroupItemList = groupItemList;
        mGroupSelectedListener = menuGroupListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_context_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout root = view.findViewById(R.id.context_menu_root);

        MaterialTextView dialogTitle = view.findViewById(R.id.context_menu_title);
        dialogTitle.setText(getString(R.string.context_menu_title));

        final Context context = requireContext();
        final LayoutInflater inflater = LayoutInflater.from(context);

        for (final MenuType menuType : mGroupItemList) {

            View item = inflater.inflate(R.layout.item_menu_type, root, false);
            root.addView(item);
            item.setOnClickListener(v -> {
                mGroupSelectedListener.onMenuSelected(menuType);
                dismiss();
            });

            MaterialTextView textView = item.findViewById(R.id.menu_type_title);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    menuType.getIconId(),
                    0,
                    0,
                    0);
            textView.setText(menuType.getTitle());
        }
    }

    public interface OnMenuSelectedListener {

        void onMenuSelected(@NonNull MenuType groupItem);
    }

    public static class Builder {

        private final List<MenuType> mGroupList = new ArrayList<>();
        private OnMenuSelectedListener mGroupSelectedListener;

        public void setMenuSelectedListener(@NonNull OnMenuSelectedListener menuGroupListener) {
            mGroupSelectedListener = menuGroupListener;
        }

        public void addGroup(int type, @NonNull String title, @DrawableRes int drawableId) {
            mGroupList.add(new MenuType(type, title, drawableId));
        }

        public ToolbarContextMenuDialog build() {
            return new ToolbarContextMenuDialog(mGroupList, mGroupSelectedListener);
        }
    }
}