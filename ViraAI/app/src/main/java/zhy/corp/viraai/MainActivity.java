package zhy.corp.viraai;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Device Kamu Tidak Mendukung Speech Input :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result.get(0).contains("Vira")) {
                    if (result.get(0).contains("kunci") && result.get(0).contains("layar")) {
                        lockScreen();
                    } else if (result.get(0).contains("cari") && result.get(0).contains("di") && result.get(0).contains("browser")) {
                        String rQuery1 = result.get(0).replace("Vira ", "");
                        String rQuery2 = rQuery1.replace("cari ", "");
                        String rQuery3 = rQuery2.replace("di ", "");
                        String query = rQuery3.replace("browser ", "");
                        Uri uri = Uri.parse("https://www.google.com/search?q=" + query);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } else if (result.get(0).contains("cari") && result.get(0).contains("di") && result.get(0).contains("YouTube")) {
                        String rQuery1 = result.get(0).replace("Vira ", "");
                        String rQuery2 = rQuery1.replace("cari ", "");
                        String rQuery3 = rQuery2.replace("di ", "");
                        String query = rQuery3.replace("YouTube ", "");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.youtube.com/results?search_query=" + query));
                        intent.setPackage("com.google.android.youtube");
                        startActivity(intent);
                    } else {
                        String query = result.get(0).replace("Vira", "");
                        Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + query)));
                    }
                } else {
                    String query = result.get(0);
                    Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + query)));
                }
            }
        }
    }

    private void lockScreen() {
        Context context = this;
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isInteractive()) {
            DevicePolicyManager policy = (DevicePolicyManager)
                    getSystemService(Context.DEVICE_POLICY_SERVICE);
            try {
                policy.lockNow();
            } catch (SecurityException ex) {
                Toast.makeText(
                        this,
                        "Harus Mengaktifkan Admin Perangkat",
                        Toast.LENGTH_LONG).show();
                ComponentName admin = new ComponentName(context, AdminReceiver.class);
                Intent intent = new Intent(
                        DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                        DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
                context.startActivity(intent);
            }
        }
    }
}