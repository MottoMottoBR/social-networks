package com.example.championsfinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.championsfinal.R;
import com.example.championsfinal.config.ConfigFirebase;
import com.example.championsfinal.config.UserFirebase;
import com.example.championsfinal.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    private Users usuarioSelecionado;
    private Users usuarioLogado;
    private TextView buttomAcaoPerfil;
    private CircleImageView imageEditarPerfil;

    private DatabaseReference usuarioRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuarioVisitaRef;

    private ValueEventListener valueEventListenerPerfilAMigo;
    private TextView  contadorSeguindo, contadorSeguidores, contadorPostagem;
    private String idUsuarioLogado;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Configuraçoes Iniciais
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        usuarioRef = firebaseRef.child("UsersID");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UserFirebase.getIdentificadorUsuario();

        //Inicilizar componentes
        incilizarComponentes();


        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrinciapal);
        toolbar.setTitle("perfil");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        //recupera o usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            usuarioSelecionado = (Users) bundle.getSerializable("usuarioSelecionado");

            //configura o nome do usuario na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNome());

            //Recupera a foto do usuario
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if (caminhoFoto != null){
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilActivity.this)
                        .load(url)
                        .into(imageEditarPerfil);
            }

        }

    }

    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef = usuarioRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //recupera dados de usuario logado
                        usuarioLogado = snapshot.getValue(Users.class);

                        //Verifica se o usuario ja esta seguindo amigo selecionado
                        verificarSegueUsuarioAmigo();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

    }

    private void verificarSegueUsuarioAmigo(){
        DatabaseReference seguidorRef = seguidoresRef
                .child(idUsuarioLogado)
                .child(usuarioSelecionado.getId());

        seguidorRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            //Já esta Seguindo
                            habilitarBotaoseguir(true);
                        }else {
                            //Não esta Seguindo
                            habilitarBotaoseguir(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

    }

    private void habilitarBotaoseguir (boolean segueUsuario){

        if (segueUsuario){
            buttomAcaoPerfil.setTextColor(getApplication().getResources().getColor(R.color.black));
            buttomAcaoPerfil.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.blue_bg_light), PorterDuff.Mode.MULTIPLY);
            buttomAcaoPerfil.setText("Seguindo");
        }else {
            buttomAcaoPerfil.setTextColor(getApplication().getResources().getColor(R.color.white));
            buttomAcaoPerfil.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.blue_active), PorterDuff.Mode.MULTIPLY);
            buttomAcaoPerfil.setText("Seguir");
            //adiciona Evento para Seguir usuario
            buttomAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Salvar Seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });
        }
    }
    private void salvarSeguidor(Users uLogado, Users uSelecionado){
        /*
         * Estrutura de seguir
         *   id_  Usario que esta logado
         *      id_ quem vai ser seguido
         *            dados  seguindo da pessoa que vou seguir
         */

        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put("nome", uSelecionado.getNome());
        dadosAmigo.put("caminhoFoto", uSelecionado.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child(uLogado.getId())
                .child(uSelecionado.getId());
        seguidorRef.setValue(dadosAmigo);

        //Alterar botao acao para seguindo
        buttomAcaoPerfil.setText("Seguindo");
        buttomAcaoPerfil.setOnClickListener(null);

        //Incrementar seguindo do usuario Logado
        int seguindo = uLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);
        DatabaseReference usuarioSeguindo = usuarioRef
                .child(uLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);

        //Incrementar seguidores do amigo
        int seguidores = uSelecionado.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);
        DatabaseReference usuarioSeguidores = usuarioRef
                .child( uSelecionado.getId() );
        usuarioSeguidores.updateChildren( dadosSeguidores );


    }

    /*
    private void apagarseguidor(final Users uLogado, final Users uAmigo){

        seguidoresRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ulogado = dataSnapshot.getKey();
                seguidoresRef.child(idUsuarioLogado)
                        .child(uAmigo.getId()).removeValue();


                //Incrementar seguindo do usuario Logado
                int seguindo = uLogado.getSeguindo() - 1;
                HashMap<String, Object> dadosSeguindo = new HashMap<>();
                dadosSeguindo.put("seguindo", seguindo);
                DatabaseReference usuarioSeguindo = usuarioRef
                        .child(uLogado.getId());
                usuarioSeguindo.updateChildren(dadosSeguindo);

                //Incrementar seguidores do amigo
                int seguidores = uAmigo.getSeguidores() - 1;
                HashMap<String, Object> dadosSeguidores = new HashMap<>();
                dadosSeguidores.put("seguidores", seguidores);
                DatabaseReference usuarioSeguidores = usuarioRef
                        .child( uAmigo.getId() );
                usuarioSeguidores.updateChildren( dadosSeguidores );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

     */


    @Override
    protected void onStart() {
        super.onStart();

        //Recupera os Dados de perfil selecionado
        recuperarDadosPerfilUsuarios();

        //Recuperar dados usuario Logado
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioVisitaRef.removeEventListener(valueEventListenerPerfilAMigo);
    }

    private void recuperarDadosPerfilUsuarios(){

        usuarioVisitaRef = usuarioRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAMigo = usuarioVisitaRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Users users = snapshot.getValue(Users.class);
                        assert users != null;
                        String postagens = String.valueOf(users.getPostagens());
                        String seguindo = String.valueOf(users.getSeguindo());
                        String seguidores = String.valueOf(users.getSeguidores());

                        //Configura valores recuperado
                        contadorPostagem.setText(postagens);
                        contadorSeguindo.setText(seguindo);
                        contadorSeguidores.setText(seguidores);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );


    }

    private void incilizarComponentes() {

        imageEditarPerfil = findViewById(R.id.imageEditarPerfil);
        buttomAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        contadorSeguindo = findViewById(R.id.contadorSeguindo);
        contadorSeguidores = findViewById(R.id.contadorSeguidores);
        contadorPostagem = findViewById(R.id.contadorPostagem);
        buttomAcaoPerfil.setText("Carregando");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;


    }
}