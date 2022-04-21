package com.example.championsfinal.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.championsfinal.R;
import com.example.championsfinal.config.ConfigFirebase;
import com.example.championsfinal.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginEmailActivity extends AppCompatActivity {

    private EditText campoemail, campoSenha;
    private Button botaoentrar;
    private Users usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();
       // inicializarComponentes();

        campoemail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.logPass);
        botaoentrar = findViewById(R.id.btLogar);

        botaoentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textSenha = campoSenha.getText().toString();
                String textEmail = campoemail.getText().toString();

                {
                    if (!textEmail.isEmpty()) {
                        if (!textSenha.isEmpty()) {

                            usuario = new Users();
                            usuario.setEmail(textEmail);
                            usuario.setSenha(textSenha);
                            validarLogin();

                        } else {
                            Toast.makeText(LoginEmailActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginEmailActivity.this, "Preencha o E-mail", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
    public  void validarLogin(){

        autenticacao = ConfigFirebase.getFirebaseAutenticacao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful()) {

                    HomeActivity();

                }else {
                    String exececao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        exececao = "Usuario nao encontrado!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exececao = "Email ou senha errados!";
                    }catch (Exception e) {
                        exececao = "Erro ao cadastrar usuario:" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginEmailActivity.this, exececao, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void verificarUsuarioLogado(){ autenticacao = ConfigFirebase.getFirebaseAutenticacao();
        if (autenticacao.getCurrentUser() !=null){
            HomeActivity();

        }
    }

    public void  HomeActivity(){ startActivity(new Intent(this, HomeActivity.class));
        finish();
    }


}