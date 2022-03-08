package com.hedipo.projetofirebase.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hedipo.projetofirebase.R;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {

    private EditText edit_nome, edit_email, edit_cpf, edit_cpf_confir;
    private Button btn_cadastrar;
    String[] mesgagens = {
            "Preencha todos os campos",
            "CPFs não são iguais",
            "E-mail inválido",
            "Cadastro realizado com sucesso"};
    String usuarioID;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro2);

        getSupportActionBar().hide();

        Iniciarcomponentes();



        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = edit_nome.getText().toString();
                String email = edit_email.getText().toString();
                String cpf = edit_cpf.getText().toString();
                String number_confir = edit_cpf_confir.getText().toString();

                if (nome.trim().isEmpty() || email.trim().isEmpty() || cpf.trim().isEmpty() || number_confir.trim().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mesgagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else if(!email.trim().contains("@")){
                    Snackbar snackbar = Snackbar.make(v, mesgagens[2], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else if(!cpf.trim().equals(number_confir)) {
                    Snackbar snackbar = Snackbar.make(v, mesgagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    CadastrarUsuario(v, nome, email, cpf);

                }
            }
        });
    }

    private void CadastrarUsuario(View v, String nome, String email, String cpf) {


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, cpf).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {


                    Snackbar snackbar = Snackbar.make(v, mesgagens[3], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    SalvarDadosUsuario(nome, email, cpf);

                } else {
                    String erro;
                    try {
                        throw task.getException();


                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = " Esta conta ja foi cadastrda";

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "E-mail invalido";

                    } catch (Exception e) {
                        erro = "Erro ao cadastrar usuario";

                    }
                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }

        });


    }
        private void SalvarDadosUsuario(String nome, String email, String cpf) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> usuarios = new HashMap<>();
            usuarios.put("nome", nome);
            usuarios.put("cpf", cpf);

            usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
            documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("db","Sucesso ao salvar os dados");


                    Intent intent = new Intent(FormCadastro.this, TelaPrincipal.class);
                    startActivity(intent);
                    finish();

                }
            })
              .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Log.d("db","Erro ao salvar os dados" + e.toString());

                  }
              });
    }

        private void Iniciarcomponentes () {;

            edit_nome = findViewById(R.id.edit_nome_cadastro);
            edit_email = findViewById(R.id.edit_email_cadastro);
            edit_cpf = findViewById(R.id.edit_cpf_cadastro);
            edit_cpf_confir = findViewById(R.id.edit_cpf_confir_cadastro);
            btn_cadastrar = findViewById(R.id.btn_cadastrar_cadastro);


        }


    }
