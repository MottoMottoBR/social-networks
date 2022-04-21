package com.example.championsfinal.config;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.championsfinal.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebase {

    public static FirebaseUser getUsuarioAtual(){

        FirebaseAuth usuario =  ConfigFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();

    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }

    public static void atulizarNomeUsuario(String nome ) {


        try {

            //Usuario Logado no App
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar objeto para alteracao do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(nome)
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar perfil. ");
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void atulizarFotoUsuario(Uri url ) {

        try {

            //Usuario Logado no App
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar obejto para alteracao do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder().setPhotoUri(url)
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar a foto do perfil. ");
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Users getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Users  usuario = new Users();
        usuario.setUserNome(firebaseUser.getDisplayName());
        usuario.setBioTxt(firebaseUser.getTenantId());
        usuario.setId(firebaseUser.getUid());

        if (firebaseUser.getPhotoUrl() == null){
            usuario.setCaminhoFoto("");

        }else {
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());

        }

        return usuario;


    }
}
