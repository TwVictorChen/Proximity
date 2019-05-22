package com.example.j6s;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {
    ComponentName admin;
    private String string;
    private Intent serviceintent;
    private DevicePolicyManager pm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        pm.isAdminActive(MainActivity.this.admin);
        enableAdminIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean active = pm.isAdminActive(admin);
        if (!active) {
            enableAdminIntent();
        } else {
            Toast.makeText(this, "Start Service", Toast.LENGTH_LONG).show();
            serviceintent = new Intent(this, J6S.class);
            string = "pocketproximity";
            serviceintent.putExtra("action", string);
            startService(this.serviceintent);
            finish();
        }
    }

    private void enableAdminIntent() {
        this.admin = new ComponentName(this, Admin.class);
        try {
            try {
                Method _setActiveAdmin_= DevicePolicyManager.class.getMethod("setActiveAdmin", ComponentName.class, boolean.class);
                try{
                    _setActiveAdmin_.invoke(pm,admin,true);
                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                }catch (InvocationTargetException e){
                    e.printStackTrace();
                }
            } catch ( NoSuchMethodException e ) {
                e.printStackTrace();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != -1) {
            finish();
        }
    }
}
