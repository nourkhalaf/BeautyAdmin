package com.example.beautyadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout skin, hair, lips, body, hand, makeup,quotes;
    private TextView exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        exit = (TextView) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                System.exit(0);

            }
        });

        skin = (LinearLayout) findViewById(R.id.skin_category);

        hair = (LinearLayout) findViewById(R.id.hair_category);
        lips = (LinearLayout) findViewById(R.id.lips_category);
        hand = (LinearLayout) findViewById(R.id.hand_category);
        body = (LinearLayout) findViewById(R.id.body_category);
        makeup = (LinearLayout) findViewById(R.id.makeup_category);
        quotes = (LinearLayout) findViewById(R.id.quotes_category);



        skin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, AdminShowItems.class);
                intent.putExtra("category", "العناية بالوجه");
                startActivity(intent);
            }
        });

        hair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, AdminShowItems.class);
                intent.putExtra("category", "العناية بالشعر");
                startActivity(intent);
            }
        });

        lips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, AdminShowItems.class);
                intent.putExtra("category", "العناية بالشفاه");
                startActivity(intent);
            }
        });

        hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,  AdminShowItems.class);
                intent.putExtra("category", "العناية باليدين والقدمين");
                startActivity(intent);
            }
        });

        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, AdminShowItems.class);
                intent.putExtra("category", "العناية بالجسم");
                startActivity(intent);
            }
        });

        makeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,  AdminShowItems.class);
                intent.putExtra("category", "المكياج والموضة");
                startActivity(intent);
            }
        });


        quotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminAddQuote.class);
                startActivity(intent);
            }
        });
    }
}