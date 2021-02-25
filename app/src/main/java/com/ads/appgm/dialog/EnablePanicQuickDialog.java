package com.ads.appgm.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ads.appgm.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class EnablePanicQuickDialog extends DialogFragment implements View.OnClickListener {

    View rootView;
    TextView tvPermissionNote, tvPermissionEnable, tvPermissionLater, tvPermissionDetailed;

    public EnablePanicQuickDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_permission, container, false);
        try {
            requireDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } catch (IllegalStateException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        tvPermissionNote = rootView.findViewById(R.id.tv_permission_note);
        tvPermissionEnable = rootView.findViewById(R.id.tv_permission_enable);
        tvPermissionLater = rootView.findViewById(R.id.tv_permission_later);
        tvPermissionDetailed = rootView.findViewById(R.id.tv_permission_detail);

        tvPermissionDetailed.setMovementMethod(LinkMovementMethod.getInstance());
        tvPermissionNote.setMovementMethod(LinkMovementMethod.getInstance());
        tvPermissionEnable.setOnClickListener(this);
        tvPermissionLater.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == tvPermissionEnable) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(() -> {
                openAccessibilitySettings();
                dismiss();
            }, 350L);

        } else if (v == tvPermissionLater) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(this::dismiss, 350L);
        }
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}
