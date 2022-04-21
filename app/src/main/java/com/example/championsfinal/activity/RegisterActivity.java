package com.example.championsfinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.championsfinal.R;
import com.example.championsfinal.config.ConfigFirebase;
import com.example.championsfinal.config.UserFirebase;
import com.example.championsfinal.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText getCampo, campoNome, campoEmail, campoSenha, userName;
    private Button botaoCadastrar;
    private Users usuario;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inicializarComponentes();

        //Cadastrar usuario
        progressBar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome  = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textosenha = campoSenha.getText().toString();

                if( !textoNome.isEmpty() ){
                    if( !textoEmail.isEmpty() ){
                        if( !textosenha.isEmpty() ){

                            usuario = new Users();
                            usuario.setNome( textoNome );
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textosenha );
                            cadastrar( usuario );

                        }else{
                            Toast.makeText(RegisterActivity.this,
                                    "Preencha a senha!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this,
                                "Preencha o email!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
    /**
     * Método responsável por cadastrar usuário com e-mail e senha
     * e fazer validações ao fazer o cadastro
     */
    public void cadastrar(final Users usuario){

        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth autenticacao = ConfigFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( task.isSuccessful() ){

                            try {

                                progressBar.setVisibility(View.GONE);

                                //Salvar dados no firebase
                                String idUsuario = Objects.requireNonNull(task.getResult().getUser()).getUid();
                                usuario.setId( idUsuario );
                                usuario.salvar();

                                //Salvar  dados no profile do Firebase
                                UserFirebase.atulizarNomeUsuario(usuario.getNome());

                                Toast.makeText(RegisterActivity.this,
                                        "Cadastro com sucesso",
                                        Toast.LENGTH_SHORT).show();

                                startActivity( new Intent(getApplicationContext(), HomeActivity.class));
                                finish();

                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }else {

                            progressBar.setVisibility( View.GONE );

                            String erroExcecao = "";
                            try{
                                throw Objects.requireNonNull(task.getException());
                            }catch (FirebaseAuthWeakPasswordException e){
                                erroExcecao = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcecao = "Por favor, digite um e-mail válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Este conta já foi cadastrada";
                            } catch (Exception e) {
                                erroExcecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(RegisterActivity.this,
                                    "Erro: " + erroExcecao ,
                                    Toast.LENGTH_SHORT).show();


                        }

                    }
                }
        );

    }

    public void inicializarComponentes(){

        campoNome       = findViewById(R.id.editCadastroNome);
        campoEmail      = findViewById(R.id.editCadastroEmail);
        campoSenha      = findViewById(R.id.editCadastroSenha);
        botaoCadastrar  = findViewById(R.id.buttonEntrar);
        progressBar     = findViewById(R.id.progressCadastro);
        campoNome.requestFocus();

    }

}

