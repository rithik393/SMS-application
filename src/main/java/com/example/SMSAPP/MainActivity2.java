package com.example.SMSAPP;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.Random;


public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{

    private static BigInteger n,e,p,q,d;
    EditText txt_pnumber,txt_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button btnShow;
        int BIT_LENGTH = 64;
        Random rand = new Random();
        //Generate primes and other necessary values
         p = BigInteger.probablePrime(BIT_LENGTH , rand);
        q = BigInteger.probablePrime(BIT_LENGTH , rand);
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));

        do {
            e = new BigInteger(phi.bitLength(), rand);
        } while (e.compareTo(BigInteger.valueOf(1)) <= 0 || e.compareTo(phi) >= 0 || !e.gcd(phi).equals(BigInteger.valueOf(1)));
        d = e.modInverse(phi);


        Intent intent = getIntent();

        txt_message= findViewById(R.id.txt_message);
        txt_pnumber= findViewById(R.id.txt_phone_number);
        btnShow = findViewById(R.id.button);

        btnShow.setOnClickListener(this);
    }


    private void MyMessage(){
        String phonenumber=txt_pnumber.getText().toString().trim();
        String Message=txt_message.getText().toString().trim();
        StringBuilder a= new StringBuilder();
        BigInteger cipherText;
        // encrypt
       if(Message.length()>16)
         Message=Message.substring(0,15);
        BigInteger message = new BigInteger(Message.getBytes());
          cipherText = message.modPow(e, n);
               a.append(cipherText.toString());

        a.append("#"+p+"#"+q+"#"+d);
        if(!txt_pnumber.getText().toString().equals("")||txt_message.getText().toString().equals("")){
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(phonenumber,null, String.valueOf(a),null,null);
            Toast.makeText(this,"Message Sent",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);

        }
        else
        {
            Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MyMessage();
            } else {
                Toast.makeText(this, "you don't have permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
      @Override
    public void onClick(View v) {
        {
            int permissioncheck= ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            if(permissioncheck== PackageManager.PERMISSION_GRANTED)
            {
                MyMessage();
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},0);


            }
        }
    }

}