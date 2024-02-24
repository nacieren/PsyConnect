package com.example.psyconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Makaleler extends AppCompatActivity {

    private Button menu1btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makaleler);


        menu1btn = (Button) findViewById(R.id.menubutton2);
        menu1btn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Makaleler.this, DoktorEslesmesi.class);
                startActivity(intent);
            }
        });
    }


}