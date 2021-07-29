package com.example.SMSAPP;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.math.*;
import java.util.*;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Button sendb, ref;
    ListView listView;

    private static BigInteger pe;
    private static BigInteger qe;
    private static BigInteger n;
    private static BigInteger phi;
    private static BigInteger e;
    private static BigInteger de;
    //blocksize in byte

    private Random r;

    private static BigInteger msgback;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> smslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendb = (Button) findViewById(R.id.buttons);
        ref = (Button) findViewById(R.id.refe);
        sendb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openactivity2();
            }
        });
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh(1000);
            }
        });
        listView = findViewById(R.id.idlist);
        int permissioncheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (permissioncheck == PackageManager.PERMISSION_GRANTED) {
            showcontacts();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_CONTACTS);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showcontacts();
            } else {
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void showcontacts() {
        Uri inboxuri = Uri.parse("content://sms/inbox");
        smslist = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(inboxuri, null, null, null, null);
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
            StringBuilder a = new StringBuilder();
            BigInteger decryptedText;
            BigInteger message;
            String pattern = "(.*)#(.*)#(.*)#(.*)";

            boolean matches = body.matches(pattern);
            if(matches) {
                String[] arrOfStr = body.split("#");
                body = arrOfStr[0];
                pe = new BigInteger(arrOfStr[1]);
                qe = new BigInteger(arrOfStr[2]);
                de = new BigInteger(arrOfStr[3]);
                message = new BigInteger(body);
                BigInteger n = pe.multiply(qe);
                decryptedText = message.modPow(de, n);
                smslist.add("number :" + number + "\n\n Decrypted :" + new String(decryptedText.toByteArray()) + "\n");
                Log.d(TAG, "showcontacts: number: " + number + " Encrypted :" + body + "Decrypted :" + new String(decryptedText.toByteArray()));

            }
           // else{
             //   smslist.add("number :" + number + "\n Encrypted : " + body + "\n Decrypted :" + body + "\n");
               // Log.d(TAG, "showcontacts: number: " + number + " Encrypted :" + body + "Decrypted :" + body);


            //}
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smslist);
        listView.setAdapter(adapter);

    }

    public void openactivity2() {

        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    private void refresh(int milliseconds) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showcontacts();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

}