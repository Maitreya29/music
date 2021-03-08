package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.MenuDetailsDialog;
import com.hardcodecoder.pulsemusic.dialog.ToolbarContextMenuDialog;
import com.hardcodecoder.pulsemusic.interfaces.OptionsMenuListener;
import com.hardcodecoder.pulsemusic.model.MenuItem;

public class ToolbarMenuBuilder {

    @NonNull
    public static ToolbarContextMenuDialog buildDefaultOptionsMenu(
            @NonNull FragmentActivity activity,
            @NonNull OptionsMenuListener listener,
            int sortOrderId,
            int columnCountId) {
        ToolbarContextMenuDialog.Builder builder = new ToolbarContextMenuDialog.Builder();
        builder.setMenuSelectedListener(groupItem -> {
            MenuDetailsDialog menuDialog = null;
            final int groupType = groupItem.getType();

            if (groupType == Preferences.MENU_GROUP_TYPE_SORT)
                menuDialog = buildSortOrderDialog(activity, sortOrderId, listener);

            else if (groupType == Preferences.MENU_GROUP_TYPE_COLUMN_COUNT)
                menuDialog = buildColumnCountDialog(activity, columnCountId, listener);

            if (null != menuDialog)
                menuDialog.show(activity.getSupportFragmentManager(), MenuDetailsDialog.TAG);
            else
                Toast.makeText(activity, activity.getString(R.string.toast_invalid_options_group), Toast.LENGTH_SHORT).show();
        });

        builder.addGroup(Preferences.MENU_GROUP_TYPE_SORT,
                activity.getString(R.string.sort_title), R.drawable.ic_sort);
        builder.addGroup(Preferences.MENU_GROUP_TYPE_COLUMN_COUNT,
                activity.getString(R.string.column_title), R.drawable.ic_columns);
        return builder.build();
    }

    @NonNull
    public static MenuDetailsDialog buildSortOrderDialog(@NonNull Context context, int groupId, @NonNull OptionsMenuListener listener) {
        MenuDetailsDialog itemsDialog = new MenuDetailsDialog(context.getString(R.string.sort_by_title), groupId, listener);

        // Sort by name
        MenuItem nameItem1 = new MenuItem(Preferences.SORT_ORDER_ASC,
                context.getString(R.string.alphabet_asc));
        MenuItem nameItem2 = new MenuItem(Preferences.SORT_ORDER_DESC,
                context.getString(R.string.alphabet_desc));

        itemsDialog.addCategory(context.getString(R.string.sort_category_title),
                new MenuItem[]{nameItem1, nameItem2});

        if (groupId == Preferences.SORT_ORDER_GROUP_LIBRARY) {
            // Sort by duration
            MenuItem durationItem1 = new MenuItem(Preferences.SORT_ORDER_DURATION_ASC,
                    context.getString(R.string.duration_asc));
            MenuItem durationItem2 = new MenuItem(Preferences.SORT_ORDER_DURATION_DESC,
                    context.getString(R.string.duration_desc));

            itemsDialog.addCategory(context.getString(R.string.sort_category_duration),
                    new MenuItem[]{durationItem1, durationItem2});

            // Sort by date added
            MenuItem dateAddedItem1 = new MenuItem(Preferences.SORT_ORDER_DATE_ADDED_ASC,
                    context.getString(R.string.date_added_asc));
            MenuItem dateAddedItem2 = new MenuItem(Preferences.SORT_ORDER_DATE_ADDED_DESC,
                    context.getString(R.string.date_added_desc));

            // Sort by date modified
            MenuItem dateModifiedItem1 = new MenuItem(Preferences.SORT_ORDER_DATE_MODIFIED_ASC,
                    context.getString(R.string.date_modified_asc));
            MenuItem dateModifiedItem2 = new MenuItem(Preferences.SORT_ORDER_DATE_MODIFIED_DESC,
                    context.getString(R.string.date_modified_desc));

            itemsDialog.addCategory(context.getString(R.string.sort_category_date),
                    new MenuItem[]{dateAddedItem1, dateAddedItem2, dateModifiedItem1, dateModifiedItem2});

        } else if (groupId == Preferences.SORT_ORDER_GROUP_ALBUMS) {
            // Sort by album artist
            MenuItem artistAsc = new MenuItem(Preferences.SORT_ORDER_ALBUM_ARTIST_ASC,
                    context.getString(R.string.album_artist_asc));
            MenuItem artistDesc = new MenuItem(Preferences.SORT_ORDER_ALBUM_ARTIST_DESC,
                    context.getString(R.string.album_artist_desc));

            itemsDialog.addCategory(context.getString(R.string.sort_category_artist),
                    new MenuItem[]{artistAsc, artistDesc});

            // Sort by album year
            MenuItem yearAsc = new MenuItem(Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_ASC,
                    context.getString(R.string.album_year_asc));
            MenuItem yearDesc = new MenuItem(Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_DESC,
                    context.getString(R.string.album_year_desc));

            itemsDialog.addCategory(context.getString(R.string.sort_category_year),
                    new MenuItem[]{yearAsc, yearDesc});

        } else if (groupId == Preferences.SORT_ORDER_GROUP_ALBUMS_DETAILS) {
            MenuItem trackNumberAsc = new MenuItem(Preferences.SORT_ORDER_ALBUM_TRACK_NUMBER_ASC,
                    context.getString(R.string.album_track_number_asc));
            MenuItem trackNumberDesc = new MenuItem(Preferences.SORT_ORDER_ALBUM_TRACK_NUMBER_DESC,
                    context.getString(R.string.album_track_number_desc));

            itemsDialog.addCategory(context.getString(R.string.sort_category_track_number),
                    new MenuItem[]{trackNumberAsc, trackNumberDesc});
        }

        return itemsDialog;
    }

    @NonNull
    public static MenuDetailsDialog buildColumnCountDialog(@NonNull Context context, int groupId, @NonNull OptionsMenuListener listener) {
        final int orientation = context.getResources().getConfiguration().orientation;

        MenuDetailsDialog menuDialog = new MenuDetailsDialog(context.getString(R.string.column_title), groupId, listener);

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            MenuItem one = new MenuItem(Preferences.COLUMN_COUNT_ONE, context.getString(R.string.one));
            MenuItem two = new MenuItem(Preferences.COLUMN_COUNT_TWO, context.getString(R.string.two));

            if (groupId == Preferences.COLUMN_COUNT_GROUP_LIBRARY) {
                menuDialog.addCategory(context.getString(R.string.column_category_count), new MenuItem[]{one, two});
            } else if (groupId == Preferences.COLUMN_COUNT_GROUP_ALBUMS || groupId == Preferences.COLUMN_COUNT_GROUP_ARTISTS) {
                MenuItem three = new MenuItem(Preferences.COLUMN_COUNT_THREE, context.getString(R.string.three));
                MenuItem four = new MenuItem(Preferences.COLUMN_COUNT_FOUR, context.getString(R.string.four));

                menuDialog.addCategory(context.getString(R.string.column_category_count),
                        new MenuItem[]{one, two, three, four});
            }
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MenuItem four = new MenuItem(Preferences.COLUMN_COUNT_FOUR, context.getString(R.string.four));

            if (groupId == Preferences.COLUMN_COUNT_GROUP_LIBRARY) {
                MenuItem two = new MenuItem(Preferences.COLUMN_COUNT_TWO, context.getString(R.string.two));
                MenuItem three = new MenuItem(Preferences.COLUMN_COUNT_THREE, context.getString(R.string.three));

                menuDialog.addCategory(context.getString(R.string.column_category_count),
                        new MenuItem[]{two, three, four});
            } else if (groupId == Preferences.COLUMN_COUNT_GROUP_ALBUMS || groupId == Preferences.COLUMN_COUNT_GROUP_ARTISTS) {
                MenuItem five = new MenuItem(Preferences.COLUMN_COUNT_FIVE, context.getString(R.string.five));
                MenuItem six = new MenuItem(Preferences.COLUMN_COUNT_SIX, context.getString(R.string.six));

                menuDialog.addCategory(context.getString(R.string.column_category_count),
                        new MenuItem[]{four, five, six});
            }
        }
        return menuDialog;
    }
}