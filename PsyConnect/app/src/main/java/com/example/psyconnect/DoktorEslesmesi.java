package com.example.psyconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DoktorEslesmesi extends AppCompatActivity {

    FirebaseFirestore db;

    // RatingBar for user rating
    RatingBar ratingBar;

    // EditText for user comments
    EditText commentEditText;

    // Button to submit rating and comment
    Button submitButton;

    // SharedPreferences to store user feedback
    SharedPreferences sharedPreferences;


    private Button psybutton;

    private Button dytbutton;

    private Button articlebutton1;

    private Button articlebutton2;

    TextView textViewFirstName;
    TextView textViewProfession;

    // Doktor resmi için ImageView
    ImageView imageViewProfilePicture;

    // Randevu butonu için Button
    Button appointmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doktor_eslesmesi);

        // TextView ve ImageView elemanlarını ilgili id'lere sahip öğelerle eşleştirme
        textViewFirstName = findViewById(R.id.DoctorName);
        textViewProfession = findViewById(R.id.DoctorProfession);
        imageViewProfilePicture = findViewById(R.id.DoctorPicture);

        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitButton = findViewById(R.id.submitButton);

        // Randevu butonunu ilgili id'ye sahip öğeyle eşleştirme
        appointmentButton = findViewById(R.id.randevu);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        // Submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RatingBar ve EditText nesnelerini tanımla
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                EditText commentEditText = findViewById(R.id.commentEditText);

                // RatingBar'dan değeri al
                float rating = ratingBar.getRating();
                // EditText'ten yorumu al
                String comment = commentEditText.getText().toString();

                // Veriyi Firestore'a ekleme işlemi
                Map<String, Object> data = new HashMap<>();
                // Yorum ve rating'i Firestore'a ekle
                data.put("rating", rating);
                data.put("comment", comment);

                // Firestore'a ekleme işlemi
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                String userEmail = MainActivity.UserInformation.userEmail;
                // Firestore'a veri ekleme
                firestore.collection("Application")
                        .document(userEmail) // Doküman adı olarak kullanıcının e-posta adresini kullanabilirsiniz
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DoktorEslesmesi.this,"Thank you for your comment",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Firestore'a eklerken hata oluştuğunda yapılacak işlemler
                            }
                        });
            }
        });

        String userEmail = MainActivity.UserInformation.userEmail;
        DocumentReference userRef = db.collection("Student").document(userEmail);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String doctorEmail = documentSnapshot.getString("Doctor");
                    if (doctorEmail != null) {
                        DocumentReference doctorRef = db.collection("Doctor").document(doctorEmail);
                        doctorRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String firstName = documentSnapshot.getString("First Name");
                                    String profession = documentSnapshot.getString("Profession");
                                    String profilePictureUrl = documentSnapshot.getString("Profile Picture");

                                    // TextView'lere doktor bilgilerini yerleştirme
                                    textViewFirstName.setText(firstName);
                                    textViewProfession.setText(profession);

                                    // ImageView'e doktor resmini yükleme
                                    if (profilePictureUrl != null) {
                                        Picasso.get().load(profilePictureUrl).into(imageViewProfilePicture);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Firestore'dan doktor bilgileri alınırken hata oluştu
                            }
                        });
                    } else {
                        // Kullanıcının bir doktoru yok
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Firestore'dan kullanıcı bilgileri alınırken hata oluştu
            }
        });

        // Randevu butonuna tıklama işlemi
        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(DoktorEslesmesi.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                int mHour = c.get(Calendar.HOUR_OF_DAY);
                                int mMinute = c.get(Calendar.MINUTE);

                                // time picker dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(DoktorEslesmesi.this,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {

                                                // Tarih ve saat bilgilerini burada işleyebilirsiniz.
                                                showNotification("Appointment Made", "your appointment " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year +
                                                        " " + hourOfDay + ":" + minute + " set to time.");
                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        psybutton = (Button) findViewById(R.id.psybtn);
        psybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoktorEslesmesi.this, Psikologlar.class);
                startActivity(intent);
            }
        });

        articlebutton1 = (Button) findViewById(R.id.aricle1btn);
        articlebutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoktorEslesmesi.this, Makaleler.class);
                startActivity(intent);
            }
        });

        articlebutton2 = (Button) findViewById(R.id.article2btn);
        articlebutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoktorEslesmesi.this, Makaleler2.class);
                startActivity(intent);
            }
        });

        dytbutton = (Button) findViewById(R.id.dytbtn);
        dytbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoktorEslesmesi.this, Diyetisyenler.class);
                startActivity(intent);
            }
        });
    }

    private void showNotification(String title, String message) {
        // Burada bildirim gösterme işlemleri yapılabilir, örneğin Toast mesajı kullanabilirsiniz.
        Toast.makeText(DoktorEslesmesi.this, title + ": " + message, Toast.LENGTH_SHORT).show();
    }
}
