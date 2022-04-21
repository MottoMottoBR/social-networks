package com.example.championsfinal.model;

import com.example.championsfinal.config.ConfigFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Users implements Serializable {

    private String nome;
    private String userNome;
    private String senha;
    private String email;
    private String id;
    private String caminhoFoto;
    private String bioTxt ;
    private int seguidores = 0;
    private int seguindo = 0;
    private int postagens = 0;




    public Users() {
    }
    public void salvar(){
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef.child("UsersID").child( getId() );
        usuariosRef.setValue( this );
    }

    public void atualizar(){
        DatabaseReference firebaseRef =  ConfigFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef
                .child("UsersID")
                .child(getId());
        Map<String, Object> valoresUsuario =  converterParaMap();
        usuariosRef.updateChildren( valoresUsuario);

    }

    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("userName", getUserNome());
        usuarioMap.put("nome", getNome() );
        usuarioMap.put("email", getEmail() );
        usuarioMap.put("id", getId() );
        usuarioMap.put("caminhoFoto", getCaminhoFoto() );
        usuarioMap.put("seguidores", getSeguidores() );
        usuarioMap.put("seguindo", getSeguindo() );
        usuarioMap.put("postagens", getPostagens() );
        usuarioMap.put("bioTxT", getBioTxt() );

        return usuarioMap;

    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }

    public String getUserNome() {
        return userNome;
    }

    public void setUserNome(String userNome) {
        this.userNome = userNome;
    }

    public String getBioTxt() {
        return bioTxt;
    }

    public void setBioTxt(String bioTxt) {
        this.bioTxt = bioTxt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }


}
