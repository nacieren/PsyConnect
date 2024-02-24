package com.example.psyconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Hosgeldiniz extends AppCompatActivity {

    FirebaseFirestore db;
    private TextView hosgeldinText;
    private Button testButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosgeldiniz);
        hosgeldinText = findViewById(R.id.hosgeldinText);
// Firestore'da kullanıcının email adresini al
        String userEmail = MainActivity.UserInformation.userEmail;
        db = FirebaseFirestore.getInstance();
// Firestore'da kullanıcı belgesini almak için sorguyu oluşturun
        db.collection("Student").document(MainActivity.UserInformation.userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                                // Firestore'daki belgeden kullanıcının ismini alın
                            if (document.exists()) {
                                // Kullanıcı Firestore'da kayıtlı, şifresini kontrol et
                                String userFirstName = document.getString("First Name");
                                // TextView'e kullanıcının ismini yerleştirin
                                hosgeldinText.setText("Welcome, " + userFirstName + "!\n\n\n"
                                        + "Welcome to the psychologist app!\n"
                                        + "I am excited to work with you and accompany you on your journey of inner discovery.\n\n"
                                        + "Here, we will work together to help improve your emotional and mental health.\n\n"
                                        + "First of all, we need to do a leveling test so that we can understand it better.\n\n"
                                        + "This test will guide us to better determine their needs and goals\n\n\n"
                                        + "If you are ready, you can start the test by clicking the button below.\n"
                                        + "Thank you from now, " + userFirstName + "!");
                            }
                        } else {
                            // Firestore'dan belge alınırken bir hata oluştu
                            Toast.makeText(Hosgeldiniz.this, "Mistake: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        testButton = (Button) findViewById(R.id.test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Hosgeldiniz.this, Test.class);
                startActivity(intent);
            }
        });


    }
}