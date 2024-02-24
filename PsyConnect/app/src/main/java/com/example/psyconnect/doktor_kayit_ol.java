package com.example.psyconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.psyconnect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import android.Manifest;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class doktor_kayit_ol extends AppCompatActivity {

    EditText firstName, birthYear, email, password, profession;
    Button bttn;
    Button btnSelectPDF;
    Button btnSelectJPG;
    FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private Uri pdfUri;

    private static final int REQUEST_CODE_SELECT_PDF = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doktor_kayit_ol);
        db = FirebaseFirestore.getInstance();
        firstName = findViewById(R.id.firstName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        birthYear = findViewById(R.id.birthYear);
        profession= findViewById(R.id.uzmanlıkAlanı);
        bttn = (Button)findViewById(R.id.buttonUye);
        btnSelectJPG = findViewById(R.id.belgeJPG);
        btnSelectPDF= findViewById(R.id.belgePDF);

        btnSelectJPG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(doktor_kayit_ol.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(doktor_kayit_ol.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3);

                }
                else{
                    Intent photo=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(photo,2);
                }
            }
        });

        btnSelectPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(doktor_kayit_ol.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(doktor_kayit_ol.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    pdfIntent.setType("application/pdf");
                    startActivityForResult(Intent.createChooser(pdfIntent, "Select PDF"), 3);
                }
            }
        });

        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email != null && firstName != null && profession != null && password != null && birthYear != null && pdfUri != null && imageUri != null ){
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String Firstname = firstName.getText().toString();
                String Age = birthYear.getText().toString();
                String Profession = profession.getText().toString();

                // Kullanıcı verilerini bir Map'e yerleştirin
                Map<String, Object> Doctor = new HashMap<>();
                Doctor.put("Email", Email);
                Doctor.put("Password", Password);
                Doctor.put("First Name", Firstname);
                Doctor.put("Age", Age);
                Doctor.put("Profession", Profession);
                Doctor.put("Profile Picture", null);
                Doctor.put("Certificate", null);

                // Firestore'da 'Doctor' koleksiyonunda kullanıcı verilerini 'Email' alanı olarak saklayın
                db.collection("Doctor").document(Email)
                        .set(Doctor) // 'add()' yerine 'set()' kullanıyorsunuz, çünkü belirli bir belgeyi belirli bir anahtarla ekliyorsunuz
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                uploadImageToFirestore(imageUri);
                                uploadPDFToFirestore(pdfUri);
                                Toast.makeText(doktor_kayit_ol.this, "Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(doktor_kayit_ol.this, doktor_main.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(doktor_kayit_ol.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
                else{
                    Toast.makeText(doktor_kayit_ol.this, "Please fill in the blank fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Intent photo=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media. EXTERNAL_CONTENT_URI);
                startActivityForResult(photo, 2);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            // İlgili izinler alındığında ve resim seçildiğinde çalışacak kodlar
            imageUri = data.getData();
            Toast.makeText(doktor_kayit_ol.this, "Image selected successfully", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            // İlgili izinler alındığında ve PDF seçildiğinde çalışacak kodlar
            pdfUri = data.getData();
            Toast.makeText(doktor_kayit_ol.this, "PDF selected successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // Firestore'e fotoğrafı yükleyen fonksiyon
    private void uploadImageToFirestore(Uri imageUri) {
        // Fotoğrafı Firestore'e yüklemek için bir storageReference oluşturun
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + email.getText().toString() + ".jpg");

        // Storage'a fotoğrafı yükleyin
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Yükleme başarılı olduğunda, fotoğrafın indirme URL'sini alın
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // URL'yi alın ve Firestore'da kullanılabilir
                                String imageUrl = uri.toString();

                                // Firestore'da kullanıcı belgesini güncelleyin ve Profile Picture alanını ekleyin
                                db.collection("Doctor").document(email.getText().toString())
                                        .update("Profile Picture", imageUrl)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(doktor_kayit_ol.this, "Profile Picture added successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(doktor_kayit_ol.this, "Failed to add Profile Picture", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(doktor_kayit_ol.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //

    public void onRequestPermissionsResult1(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 2);
            }
        }
    }

    private void uploadPDFToFirestore(Uri pdfUri) {
        // Fotoğrafı Firestore'e yüklemek için bir storageReference oluşturun
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("doctor_profession/" + email.getText().toString() + ".pdf");

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
                                db.collection("Doctor").document(email.getText().toString())
                                        .update("Certificate", pdfUrl)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(doktor_kayit_ol.this, "Certificate added successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(doktor_kayit_ol.this, "Failed to add Certificate", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(doktor_kayit_ol.this, "Failed to upload certificate", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}