package com.example.championsfinal.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.championsfinal.R;
import com.example.championsfinal.config.ConfigFirebase;
import com.example.championsfinal.config.UserFirebase;
import com.example.championsfinal.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imgPerfil;
    private TextView textAlterarFoto, textEditEmail;
    private TextInputEditText userNomeTxt, textEditNomeUser, bioTXT;
    private Button btSalvarPerfil;
    private Users usuarioLogado;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef;
    private String identificadorUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Configuraçoes Inciais
        usuarioLogado =        UserFirebase.getDadosUsuarioLogado();
        storageRef =           ConfigFirebase.getFirebaseStorage();
        identificadorUsuario = UserFirebase.getIdentificadorUsuario();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrinciapal);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        //Inicilizar Componemtes
        incilizarComponentes();

        //Recuperar dados do usuario;
        FirebaseUser usuarioPerfil = UserFirebase.getUsuarioAtual();
        textEditNomeUser.setText(usuarioPerfil.getDisplayName());
        textEditEmail.setText(usuarioPerfil.getEmail());
        userNomeTxt.setText(usuarioLogado.getNome());
        bioTXT.setText(usuarioLogado.getBioTxt());
        //userNomeTxt.setText(usuarioPerfil.get);


        //recuperar foto
        Uri url = usuarioPerfil.getPhotoUrl();
        if (url != null ){
            Glide.with(EditarPerfilActivity.this)
                    .load(url)
                    .into(imgPerfil);
            }else {
            imgPerfil.setImageResource(R.drawable.avatar);

        }


        //Salva  dados do usuario no firebase
        btSalvarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeAtualizado = Objects.requireNonNull(textEditNomeUser.getText()).toString();
                String email = Objects.requireNonNull(textEditEmail.getText()).toString();
                String userName = Objects.requireNonNull(userNomeTxt.getText()).toString().toLowerCase();
                String txtBio = Objects.requireNonNull(bioTXT.getText()).toString();

                //atualiza Nome no Perfil
                UserFirebase.atulizarNomeUsuario(nomeAtualizado);

                //atualiza  Banco de dados
                usuarioLogado.setNome(nomeAtualizado);
                usuarioLogado.setUserNome(userName);
                usuarioLogado.setEmail(email);
                usuarioLogado.setBioTxt(txtBio);
                usuarioLogado.atualizar();

                Toast.makeText(EditarPerfilActivity.this, "Atualizado com sucesso", Toast.LENGTH_SHORT).show();

            }
        });


        //Alterar foto de Usuario
        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i,SELECAO_GALERIA);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                //Selecao apenas da galeria de imagens
                switch (requestCode){
                    case SELECAO_GALERIA:
                        assert data != null;
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagemSelecionada);
                        break;
                }

                //Caso tenha sido escolhido uma imagem
                if (imagem != null){

                    //Configura imagem na tela
                    imgPerfil.setImageBitmap(imagem);

                    //Recuperar Imagem do firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos  );
                    byte[] dadosImagem = baos.toByteArray();


                    //salvar Imagem Firebase
                    StorageReference imagemRef = storageRef
                            .child("Imagens")
                            .child("perfil")
                            .child(identificadorUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(EditarPerfilActivity.this,
                                    "Erro  ao fazer Upload da imagem ",
                                    Toast.LENGTH_SHORT).show();


                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Recupera local da foto.
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    Uri url = task.getResult();
                                    atualizarFotoUsuario(url);

                                }
                            });

                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void atualizarFotoUsuario(Uri url){
        //atulizar foto no perfil
        UserFirebase.atulizarFotoUsuario(url);

        //atualizar foto no firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();

        Toast.makeText(EditarPerfilActivity.this,
                "Sua foto foi atulizada! ",
                Toast.LENGTH_SHORT).show();

    }


    public void incilizarComponentes(){

        imgPerfil  = findViewById(R.id.imgPerfil);
        textAlterarFoto = findViewById(R.id.texAlterarFoto);
        btSalvarPerfil = findViewById(R.id.btSalvarPerfil);
        textEditEmail = findViewById(R.id.textEditEmail);
        userNomeTxt = findViewById(R.id.userNomeTxt);
        textEditNomeUser = findViewById(R.id.textEditNomeUser);
        bioTXT =  findViewById(R.id.bioTXT);
        //textEditEmail.setFocusable(false);;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;

    }
}