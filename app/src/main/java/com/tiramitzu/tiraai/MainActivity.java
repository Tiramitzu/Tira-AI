package com.tiramitzu.tiraai;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.PowerManager;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView txvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txvResult = (TextView) findViewById(R.id.txvResult);
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (resultCode == RESULT_OK && data != null) {
                    if (result.get(0).contains("lock the screen")) {
                        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                        if (pm.isInteractive()) {
                            DevicePolicyManager policy = (DevicePolicyManager)
                                    getSystemService(Context.DEVICE_POLICY_SERVICE);
                            try {
                                txvResult.setText("Screen was Locked");
                                policy.lockNow();
                            } catch (SecurityException ex) {
                                Context context = this;
                                Toast.makeText(
                                        this,
                                        "Must enable device administrator",
                                        Toast.LENGTH_LONG).show();
                                ComponentName admin = new ComponentName(context, AdminReceiver.class);
                                Intent intent = new Intent(
                                        DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                                                DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
                                context.startActivity(intent);
                                txvResult.setText("Failed to lock the screen");
                            }
                        }
                    } else {
                        txvResult.setText(result.get(0));
                    }
                }
                break;
        }
    }
}