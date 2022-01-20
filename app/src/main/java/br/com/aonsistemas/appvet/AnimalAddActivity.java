package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

import br.com.aonsistemas.appvet.dao.AnimalDAO;
import br.com.aonsistemas.appvet.fragment.ClientSearchDialogFragment;
import br.com.aonsistemas.appvet.model.Animal;
import br.com.aonsistemas.appvet.util.Message;

public class AnimalAddActivity extends AppCompatActivity
        implements ClientSearchDialogFragment.SearchClientDialogFragmentListener {

    private EditText txtNameAnimal;
    Button btnClientAddAnimal;

    private Animal animal;
    private int client_id;

    private String alter = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_add);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.animal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        txtNameAnimal = findViewById(R.id.txtNameAnimal);

        ImageView imgClientAddAnimal = findViewById(R.id.imgClientAddAnimal);
        imgClientAddAnimal.setOnClickListener(v -> selClient());

        btnClientAddAnimal = findViewById(R.id.btnClientAddAnimal);
        btnClientAddAnimal.setOnClickListener(v -> selClient());

        try {

            Intent intent = getIntent();
            Bundle params = intent.getExtras();

            this.animal = (Animal) params.getSerializable("animal");

        } catch (Exception e) {
            this.animal = null;
        }

        if ( this.animal != null ) {
            this.alter = "yes";
            txtNameAnimal.setText(this.animal.getName());
            btnClientAddAnimal.setText(this.animal.getClient().getName());
            client_id = this.animal.getClient_id();
        }

    }

    public void selClient() {
        ClientSearchDialogFragment searchClient = ClientSearchDialogFragment.newInstance();
        searchClient.openDialog(getSupportFragmentManager());
    }

    @Override
    public void onFinishEditClientDialog(int id, String nome) {
        btnClientAddAnimal.setText(nome);
        client_id = id;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.salvar_cadastro:
                saveAnimal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveAnimal() {

        if (txtNameAnimal.getText().toString().equals("")) {
            Message.showMessage(AnimalAddActivity.this, R.string.txt_mandatory_name);
            txtNameAnimal.requestFocus();
            return;
        }

        if ( client_id <= 0 ) {
            Message.showMessage(AnimalAddActivity.this, R.string.txt_mandatory_client);
            return;
        }

        try {

            if ( animal == null ) {
                animal = new Animal();
            }

            animal.setName(txtNameAnimal.getText().toString());
            animal.setClient_id(client_id);

            AnimalDAO dao = new AnimalDAO(AnimalAddActivity.this);

            dao.open();

            if (alter.equals("yes"))
                dao.update(animal);
            else
                dao.insert(animal);

            dao.close();

            setResult(RESULT_OK);

            if (alter.equals("yes"))
                Toast.makeText(AnimalAddActivity.this, R.string.txt_alter_animal, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(AnimalAddActivity.this, R.string.txt_add_animal, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            setResult(RESULT_CANCELED);
            Toast.makeText(AnimalAddActivity.this, R.string.txt_error_excluded_animal, Toast.LENGTH_LONG).show();
        }

        finish();

    }

}