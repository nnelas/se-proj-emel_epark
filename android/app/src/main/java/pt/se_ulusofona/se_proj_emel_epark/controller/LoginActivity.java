package pt.se_ulusofona.se_proj_emel_epark.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import pt.se_ulusofona.se_proj_emel_epark.R;

/**
 * Created by nunonelas on 01/05/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nome;
    private EditText password;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nome = (EditText) findViewById(R.id.str_nome);
        password = (EditText) findViewById(R.id.str_password);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(final View view) {

        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Por favor aguarde...", "A Processar...", true);
        String emailSufixo = "@testulht.pt";

        switch (view.getId()) {
            //Handle Clicks
            case R.id.btn_login:
                String strNome = nome.getText().toString();
                String email = strNome + emailSufixo;

                (firebaseAuth.signInWithEmailAndPassword(email, password.getText().toString())).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();

                                    if (task.isSuccessful()) {
                                        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.putExtra("Username", firebaseAuth.getCurrentUser().getEmail());
                                        startActivity(intent);
                                    } else {
                                        Log.e("ERROR", task.getException().toString());
                                        Toast.makeText(getApplicationContext(),
                                                "Credênciais inválidas!", Toast.LENGTH_SHORT).show();
                                        clearScreen(view);
                                    }
                                }
                            });
                        //}
                break;
            default:
                break;
        }
    }

    public void clearScreen (View view){
        nome.setText("");
        password.setText("");
    }
}
