package com.company.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private CircleImageView imageViewCircle;
    private TextInputEditText editTextEmailSignup,editTextPasswordSignup,editTextUserNameSignup;
    private Button buttonRegister;
    boolean imageControl = false;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        imageViewCircle = findViewById(R.id.imageViewCircle);
        editTextEmailSignup = findViewById(R.id.editTextEmailSignup);
        editTextPasswordSignup = findViewById(R.id.editTextPasswordSignup);
        editTextUserNameSignup = findViewById(R.id.editTextUserNameSignup);
        buttonRegister = findViewById(R.id.buttonRegister);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        imageViewCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmailSignup.getText().toString();
                String password = editTextPasswordSignup.getText().toString();
                String userName = editTextUserNameSignup.getText().toString();

                if (!email.equals("") && !password.equals("") && !userName.equals(""))
                {
                    signup(email,password,userName);
                }
            }
        });
    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("images/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            // 'com.squareup.picasso:picasso:2.71828'
            Picasso.get().load(imageUri).into(imageViewCircle);
            imageControl =  true;
        } else {
            imageControl = false;
        }
    }

    public void signup(String email, String password, final String userName)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    reference.child("Users").child(auth.getUid()).child("userName").setValue(userName);

                    if (imageControl) {
                        
                    } else {
                        reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.putExtra("Username", userName);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "There is a problem.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}