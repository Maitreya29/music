package com.nezukoos.music.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.R;
import com.nezukoos.music.fragments.settings.base.SettingsBaseFragment;

public class SettingsDonationFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsDonationFragment.class.getSimpleName();

    @NonNull
    public static SettingsDonationFragment getInstance() {
        return new SettingsDonationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.bmc_frame)
                .setOnClickListener(v -> openLink("https://www.buymeacoffee.com/HardcodeCoder"));
        view.findViewById(R.id.pay_pal_frame)
                .setOnClickListener(v -> openLink("https://paypal.me/HardcodeCoder"));
        view.findViewById(R.id.upi_frame).setOnClickListener(v -> {
            Uri uri = Uri.parse("upi://pay").buildUpon()
                    .appendQueryParameter("pa", "ashutoshpatoa3105@oksbi")
                    .appendQueryParameter("pn", "Music")
                    .appendQueryParameter("cu", "INR")
                    .build();

            Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
            upiPayIntent.setData(uri);

            // will always show a dialog to user to choose an app
            Intent chooser = Intent.createChooser(upiPayIntent, getString(R.string.donate_options_title));

            // check if intent resolves
            if (null != chooser.resolveActivity(requireContext().getPackageManager())) {
                startActivity(chooser);
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_upi_not_found), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.support_development;
    }

    private void openLink(String link) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);
    }
}