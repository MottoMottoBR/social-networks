package com.example.championsfinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.championsfinal.R;
import com.example.championsfinal.config.ConfigFirebase;
import com.example.championsfinal.fragment.HomeFragment;
import com.example.championsfinal.fragment.PerfilUserFragment;
import com.example.championsfinal.fragment.PesquisaFragment;
import com.example.championsfinal.fragment.WordFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    TextView newPost;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        newPost = findViewById(R.id.newPost);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrinciapal);
        toolbar.setTitle(R.string.tituloToolbar);
        setSupportActionBar(toolbar);

        //Configuraçao de Objetos
        autenticacao = ConfigFirebase.getFirebaseAutenticacao();

        //this line hide statusbar
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Navigation Bottom
        navigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new HomeFragment()).commit();
        navigationView.setSelectedItemId(R.id.nav_home);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){

                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;

                    case R.id.nav_word:
                        fragment = new WordFragment();
                        break;

                    case R.id.nav_perfil:
                        fragment = new PerfilUserFragment();
                        break;

                    case R.id.nav_pesquisa:
                        fragment = new PesquisaFragment();
                        break;

                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.body_container, fragment)
                        .commit();

                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.newPost:
                novaPostagem();
                break;

            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void novaPostagem(){
        //BotaoShee de nova postagem
        View botaoSheet =  (View)findViewById(R.id.newPost);
        botaoSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        HomeActivity.this, R.style.BottomSheetDialogTheme
                );
                View bottomShetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet_post,
                                (LinearLayout)findViewById(R.id.bottomSheetContainer)
                        );

                bottomSheetDialog.setContentView(bottomShetView);
                bottomSheetDialog.show();

            }
        });

    }

}