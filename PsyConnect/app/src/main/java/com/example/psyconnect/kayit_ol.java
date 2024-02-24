package com.example.psyconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class kayit_ol extends AppCompatActivity {

    EditText firstName, lastName, birthYear, email, password;
    Button bttn;
    Button btnSelectPDF;
    private Uri pdfUri;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);
        db = FirebaseFirestore.getInstance();
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        birthYear = findViewById(R.id.birthYear);
        bttn = (Button)findViewById(R.id.buttonUye);
        btnSelectPDF= findViewById(R.id.belge);

        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email != null && firstName != null && lastName != null && password != null && birthYear != null && pdfUri != null ){
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String Firstname = firstName.getText().toString();
                String Lastname = lastName.getText().toString();
                String Age = birthYear.getText().toString();

                // Kullanıcı verilerini bir Map'e yerleştirin
                Map<String,Object> Student = new HashMap<>();
                Student.put("Email",Email);
                Student.put("Password",Password);
                Student.put("First Name",Firstname);
                Student.put("Last Name",Lastname);
                Student.put("Age",Age);
                Student.put("Point",0);
                Student.put("Doctor",null);
                Student.put("Document",null);

                // Firestore'da 'Student' koleksiyonunda kullanıcı verilerini 'Email' alanı olarak saklayın
                db.collection("Student").document(Email)
                        .set(Student) // 'add()' yerine 'set()' kullanıyorsunuz, çünkü belirli bir belgeyi belirli bir anahtarla ekliyorsunuz
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(kayit_ol.this,"Successful",Toast.LENGTH_SHORT).show();
                                uploadPDFToFirestore(pdfUri);
                                Intent intent = new Intent(kayit_ol.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(kayit_ol.this,"Failed",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
                else{
                    Toast.makeText(kayit_ol.this, "Please fill in the blank fields.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btnSelectPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(kayit_ol.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(kayit_ol.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    pdfIntent.setType("application/pdf");
                    startActivityForResult(Intent.createChooser(pdfIntent, "Select PDF"), 2);
                }
            }
        });

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            // İlgili izinler alındığında ve PDF seçildiğinde çalışacak kodlar
            pdfUri = data.getData();
            Toast.makeText(kayit_ol.this, "PDF selected successfully", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadPDFToFirestore(Uri pdfUri) {
        // Fotoğrafı Firestore'e yüklemek için bir storageReference oluşturun
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("student_document/" + email.getText().toString() + ".pdf");

        // Storage'a fotoğrafı yükleyin
        storageRef.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Yükleme başarılı olduğunda, fotoğrafın indirme URL'sini alın
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // URL'yi alın ve Firestore'da kullanılabilir
                                String pdfUrl = uri.toString();

                                // Firestore'da kullanıcı belgesini güncelleyin ve Profile Picture alanını ekleyin
                                db.collection("Student").document(email.getText().toString())
                                        .update("Document", pdfUrl)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(kayit_ol.this, "Doc added successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(kayit_ol.this, "Failed to add Doc", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(kayit_ol.this, "Failed to upload Doc", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}