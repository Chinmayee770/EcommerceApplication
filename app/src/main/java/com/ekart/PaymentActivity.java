package com.ekart;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ekart.shopping.R;

public class PaymentActivity extends AppCompatActivity {
    private Button payButton,cashOnDeliveryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        String recipientUPI = "7588407332@axl";
        String totalAmount = getIntent().getStringExtra("Total Price");
        cashOnDeliveryButton = findViewById(R.id.cash_on_delivery_button);

        payButton = findViewById(R.id.send);


        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("upi://pay").buildUpon()
                        .appendQueryParameter("pa", recipientUPI)
                        .appendQueryParameter("pn", "Name")
                        .appendQueryParameter("mc", "")
                        .appendQueryParameter("tid", "")
                        .appendQueryParameter("tr", "Payment Note")
                        .appendQueryParameter("tn", "Payment Note")
                        .appendQueryParameter("am", totalAmount)
                        .appendQueryParameter("cu", "INR")
                        .build();


                Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
                upiPayIntent.setData(uri);
                Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

                if(null != chooser.resolveActivity(getPackageManager())) {
                    startActivityForResult(chooser, 1);
                } else {
                    Toast.makeText(PaymentActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
