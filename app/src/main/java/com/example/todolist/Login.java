package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button botonLogin;
    TextView botonRegistro;
    EditText emailText;
    EditText passText;

    LayoutInflater inflater;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.cajaCorreo);
        passText = findViewById(R.id.cajaPass);

        botonLogin = findViewById(R.id.botonLogin);
        botonLogin.setOnClickListener(view -> {
            String email = emailText.getText().toString();
            String password = passText.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Intent intent = new Intent(Login.this, MainActivity.class); //Esto te pasa a la siguiente activity indicada
                                startActivity(intent); //Esto inicia la siguiente activity
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });

        botonRegistro = findViewById(R.id.botonRegistro);
        botonRegistro.setOnClickListener(view -> {
            String email = emailText.getText().toString();
            String password = passText.getText().toString();

            if (email.isEmpty()) {
                toastError("El campo de email está vacío");
            } else if (!email.matches("^[\\w-]{3,}(\\.[\\w-]+)*@[A-Za-z0-9]{3,}(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                toastError("El formato de email no es válido");
            } else if(password.isEmpty()){
                toastError("El password debe contener al menos 6 caracteres");
            }else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    toastOk("Usuario registrado"); //Esto crea un mensaje con el texto de las comillas
                                    Intent intent = new Intent(Login.this, MainActivity.class); //Esto te pasa a la siguiente activity indicada
                                    startActivity(intent); //Esto inicia la siguiente activity
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    public void toastError(String mensaje){
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.toast_fallo_signin, (ViewGroup) findViewById(R.id.toastFalloSignin));

        TextView txtMensaje = view.findViewById(R.id.toastFallo);
        txtMensaje.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
    public void toastOk(String mensaje){
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.toast_ok_signin, (ViewGroup) findViewById(R.id.toastOkSignin));

        TextView txtMensaje = view.findViewById(R.id.toastOk);
        txtMensaje.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
}