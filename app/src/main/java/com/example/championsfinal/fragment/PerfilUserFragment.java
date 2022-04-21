package com.example.championsfinal.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.championsfinal.R;
import com.example.championsfinal.activity.EditarPerfilActivity;
import com.example.championsfinal.config.UserFirebase;
import com.example.championsfinal.model.Users;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;


public class PerfilUserFragment extends Fragment {

    private TextView textNamePerfil, contadorSeguindo, contadorSeguidores, contadorCurtidas, textUserName, textBio, buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    public GridView gridViewPerfil;
    public ProgressBar progressBar;
    private String identificadorUsuario;
    private Users usuarioLogado;


    public PerfilUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_user, container, false);


        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        progressBar = view.findViewById(R.id.progressBar);
        textBio = view.findViewById(R.id.textBioId);
        textUserName = view.findViewById(R.id.textUserName);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        imagePerfil = view.findViewById(R.id.imageEditarPerfil);

        //Recuperar dados do usuario;
        FirebaseUser usuarioPerfil = UserFirebase.getUsuarioAtual();
        textUserName.setText(usuarioPerfil.getDisplayName());
        //textBio.setText(usuarioPerfil.getDisplayName());

        //recuperar foto
        Uri url = usuarioPerfil.getPhotoUrl();
        if (url != null ){
            Glide.with(PerfilUserFragment.this)
                    .load(url)
                    .into(imagePerfil);
        }else {
            imagePerfil.setImageResource(R.drawable.avatar);

        }



        //abrir tela perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        return view;
    }


}