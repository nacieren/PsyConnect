package com.example.psyconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class doktor_main extends AppCompatActivity {

    private Button girişButton;
    private Button kayıtButton;

    private Button studentButton;
    private EditText email;
    private EditText password;
    FirebaseFirestore db;

    public static class UserInformation {
        public static String userEmail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doktor_main);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);


        girişButton = (Button) findViewById(R.id.buttonLogin);
        girişButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email != null && password != null){
                // Giriş butonunun onClickListener'ı içinde
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();


                // Firestore'da kullanıcıyı bulma
                db.collection("Doctor").document(userEmail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // Kullanıcı Firestore'da kayıtlı, şifresini kontrol et
                                        String passwordFromDB = document.getString("Password");
                                        if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                                            // Şifre doğru, giriş başarılı
                                            Toast.makeText(doktor_main.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            UserInformation.userEmail = userEmail; // Kullanıcının email adresini sakla
                                        } else {
                                            // Şifre yanlış, kullanıcıya bilgi ver
                                            Toast.makeText(doktor_main.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Kullanıcı Firestore'da kayıtlı değil
                                        Toast.makeText(doktor_main.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Firestore'dan belge alınırken bir hata oluştu
                                    Toast.makeText(doktor_main.this, "Mistake: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
                else{
                Toast.makeText(doktor_main.this, "Please fill in the blank fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        kayıtButton = (Button) findViewById(R.id.buttonSignup);
        kayıtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doktor_main.this, doktor_kayit_ol.class);
                startActivity(intent);
            }
        });

        studentButton = (Button) findViewById(R.id.studentButton);
        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doktor_main.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


}
