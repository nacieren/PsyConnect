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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button girişButton;
    private Button kayıtButton;
    private Button doktorButon;
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
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);


        girişButton = (Button) findViewById(R.id.buttonLogin);
        girişButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Emailaa: " + email);
                if(email != null && password != null){
                // Giriş butonunun onClickListener'ı içinde
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();


                // Firestore'da kullanıcıyı bulma
                db.collection("Student").document(userEmail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (userEmail == null) {
                                    // Kullanıcı adı girilmezse uygulama hata veriyor!!
                                    Toast.makeText(MainActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                                }

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // Kullanıcı Firestore'da kayıtlı, şifresini kontrol et
                                        String passwordFromDB = document.getString("Password");
                                        String Doctor = document.getString("Doctor");
                                        if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                                            // Şifre doğru, giriş başarılı
                                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            UserInformation.userEmail = userEmail; // Kullanıcının email adresini sakla
                                            if (Doctor == null) {
                                                Intent intent = new Intent(MainActivity.this, Hosgeldiniz.class);
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(MainActivity.this, DoktorEslesmesi.class);
                                                startActivity(intent);
                                            }
                                        } else {
                                            // Şifre yanlış, kullanıcıya bilgi ver
                                            Toast.makeText(MainActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Kullanıcı Firestore'da kayıtlı değil
                                        Toast.makeText(MainActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Firestore'dan belge alınırken bir hata oluştu
                                    Toast.makeText(MainActivity.this, "Mistake: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
                else{
                    Toast.makeText(MainActivity.this, "Please fill in the blank fields.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        kayıtButton = (Button) findViewById(R.id.buttonSignup);
        kayıtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, kayit_ol.class);
                startActivity(intent);
            }
        });

        doktorButon = (Button) findViewById(R.id.doktorButon);
        doktorButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, doktor_main.class);
                startActivity(intent);
            }
        });
    }


}
