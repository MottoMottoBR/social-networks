package com.example.championsfinal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.championsfinal.R;
import com.example.championsfinal.config.ConfigFirebase;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    /*
    *ESTA  SERA A PRIMEIRA ACTIVITY A SER CHAMADA
    * Aqui tem os Slides Inicial
    * botoes para as paginas de login
    * Verifica se o usuario esta logado e manda ele pra pagina Home
    * setContentView(R.layout.activity_main); desabilatado pra os Slides aparecer
     */

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth  autenticacao;



    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //start SLides - Inicia aqui o slider principal
        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_bright)
                .backgroundDark(R.color.white)
                .fragment(R.layout.intro_01)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_bright)
                .backgroundDark(R.color.white)
                .fragment(R.layout.intro_02)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_bright)
                .backgroundDark(R.color.white)
                .fragment(R.layout.intro_03)
                .canGoBackward(true)
                .canGoForward(false)
                .build());
    }

    public void verificarUsuarioLogado(){ autenticacao = ConfigFirebase.getFirebaseAutenticacao();
        if (autenticacao.getCurrentUser() !=null){
           HomeActivity();
        }
    }


    //Login Google Activiy inicia A pagina de telas e o login do google
    public void  entraButtom (View view){ startActivity(new Intent(this, LoginGoogleActivity.class));}

    //Chama a Activity de Registrar o Usuario por Email
    public void buttomRegistar(View view){ startActivity(new Intent(this, RegisterActivity.class));}

    //Chama a Activity que loga  o Usuario por Email
    public void  btEntrar(View view){ startActivity(new Intent(this, LoginEmailActivity.class));}

    //Se o usuario estiver logado chamada esto o manda para a Activity Principal
    public void  HomeActivity(){ startActivity(new Intent(this, HomeActivity.class));}

}

