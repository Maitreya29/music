package com.hardcodecoder.pulsemusic.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UserInfo {

    private static final String USER_INFO = "UserInfo";
    private static final String NAME_KEY = "UserName";
    private static final String PROFILE_PICTURE = "profilePic.jpg";

    public static void saveUserName(@NonNull Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
        editor.putString(NAME_KEY, name);
        editor.apply();
    }

    public static String getUserName(@NonNull Context context) {
        return context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).getString(NAME_KEY, context.getString(R.string.def_user_name));
    }

    public static void saveUserProfilePic(@NonNull Context context, @NonNull Uri uri, @Nullable TaskRunner.Callback<File> callback) {
        Handler handler = new Handler();
        TaskRunner.executeAsync(() -> {
            File file = new File(context.getFilesDir().getAbsolutePath(), PROFILE_PICTURE);
            try {
                ContentResolver resolver = context.getContentResolver();
                InputStream inputStream = resolver.openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, new FileOutputStream(file));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (null != callback) handler.post(() -> callback.onComplete(file));
        });
    }

    @NonNull
    public static File getUserProfilePic(@NonNull Context context) {
        return new File(context.getFilesDir().getAbsolutePath(), PROFILE_PICTURE);
    }
}