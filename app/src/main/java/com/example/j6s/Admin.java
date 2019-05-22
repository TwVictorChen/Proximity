package com.example.j6s;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Admin extends DeviceAdminReceiver {
    void showToast(Context context, CharSequence msg) { Toast.makeText(context, msg, 0).show();}

    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Enabled");
    }

    public void onDisabled(Context context, Intent intent) {
        showToast(context, "Disabled");
    }
}
