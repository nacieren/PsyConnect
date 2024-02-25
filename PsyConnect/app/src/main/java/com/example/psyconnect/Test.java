package com.example.psyconnect;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Test extends AppCompatActivity {

    private EditText puan1, puan2,puan3,sorunlar;
    private Button toplaButton;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        puan1 = findViewById(R.id.puan1);
        puan2 = findViewById(R.id.puan2);
        puan3 = findViewById(R.id.puan3);
        sorunlar=findViewById(R.id.sorunlar);
        toplaButton = findViewById(R.id.toplaButton);

        db = FirebaseFirestore.getInstance();

        toplaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText'lerden değerleri al
                String puan1Str = puan1.getText().toString();
                String puan2Str = puan2.getText().toString();
                String puan3Str = puan3.getText().toString();
                String sorunlarStr = sorunlar.getText().toString();

                // Değerleri integer'a dönüştür
                int puan1 = Integer.parseInt(puan1Str);
                int puan2 = Integer.parseInt(puan2Str);
                int puan3 = Integer.parseInt(puan3Str);

                // Toplamı hesapla
                int toplam = puan1 + puan2 + puan3;

                // Kullanıcının e-posta adresini al
                String userEmail = MainActivity.UserInformation.userEmail;

                // Firestore'da belgeyi güncelle
                if (userEmail != null) {
                    DocumentReference userRef = db.collection("Student").document(userEmail);
                    Map<String, Object> updates = new HashMap<>();
                    selectRandomDoctor();
                    updates.put("Point", toplam);
                    updates.put("Patient's Problems", sorunlarStr);
                    userRef.update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Document updated successfully");
                                    Intent intent = new Intent(Test.this, DoktorEslesmesi.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Document update error", e);
                                    Toast.makeText(Test.this, "Document update error", Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            }
        });
    }
    private void selectRandomDoctor() {
        // Firestore'dan doktor koleksiyonunu al
        db.collection("Doctor")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> doctorNames = new ArrayList<>();

                            // Koleksiyonun her bir doktor belgesini listeye ekle
                            for (DocumentSnapshot document : task.getResult()) {
                                String email = document.getString("Email");
                                doctorNames.add(email);
                            }

                            // Rastgele bir indeks seç
                            Random random = new Random();
                            int randomIndex = random.nextInt(doctorNames.size());

                            // Seçilen indeksteki doktorun adını Toast mesajında göster
                            String selectedDoctorName = doctorNames.get(randomIndex);
                            Toast.makeText(Test.this, "Your doctor: " + selectedDoctorName, Toast.LENGTH_SHORT).show();

                            // Kullanıcının e-posta adresini al
                            String userEmail = MainActivity.UserInformation.userEmail;

                            // Firestore'da belgeyi güncelle
                            if (userEmail != null) {
                                DocumentReference userRef = db.collection("Student").document(userEmail);
                                userRef.update("Doctor", selectedDoctorName)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Belge başarıyla güncellendi");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Belge güncelleme hatası", e);
                                                Toast.makeText(Test.this, "Belge güncelleme hatası", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Firestore'dan doktor koleksiyonu alınırken bir hata oluştu
                            Toast.makeText(Test.this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
