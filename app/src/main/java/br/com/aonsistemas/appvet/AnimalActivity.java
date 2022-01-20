package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.aonsistemas.appvet.adapter.AnimalAdapter;
import br.com.aonsistemas.appvet.dao.AnimalDAO;
import br.com.aonsistemas.appvet.model.Animal;
import br.com.aonsistemas.appvet.util.Message;

public class AnimalActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, ClickRecyclerViewInterface {

    private static final int ADD_ANIMAL = 96;

    private RecyclerView lstAnimals;
    private AnimalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.add_animal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ProgressBar progressBar = findViewById(R.id.progressBarAnimal);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        SearchView svAnimalSearch = findViewById(R.id.svAnimalSearch);
        svAnimalSearch.setOnQueryTextListener(this);
        svAnimalSearch.setQueryHint(getString(R.string.txt_name_animal_client));
        svAnimalSearch.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        svAnimalSearch.setSubmitButtonEnabled(false);

        adapter = new AnimalAdapter(null, AnimalActivity.this);

        lstAnimals = findViewById(R.id.lstAnimals);
        lstAnimals.setAdapter(adapter);
        lstAnimals.setLayoutManager(new LinearLayoutManager( AnimalActivity.this));
        lstAnimals.setHasFixedSize(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            AnimalDAO animalDAO = new AnimalDAO(AnimalActivity.this);

            animalDAO.open();
            List<Animal> animals = animalDAO.getAnimals();
            animalDAO.close();

            handler.post(() -> {
                adapter.setListAnimal(animals);
                progressBar.setVisibility(ProgressBar.GONE);
            });

        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add:
                Intent intent = new Intent(AnimalActivity.this, AnimalAddActivity.class);
                startActivityForResult(intent, ADD_ANIMAL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.insert, menu);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (( resultCode == RESULT_OK ) && ( requestCode == ADD_ANIMAL )) {

            AnimalDAO dao = new AnimalDAO(AnimalActivity.this);

            dao.open();
            List<Animal> animals = dao.getAnimals();
            dao.close();

            adapter.setListAnimal(animals);

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onCustomClick(Object object) {

    }

    @Override
    public void onCustomLongClick(Object object) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AnimalActivity.this);
        builder.setTitle(R.string.txt_options)
                .setItems(R.array.options_animal, (dialog, which) -> {

                    Animal animal = (Animal) object;

                    switch (which) {
                        case 0:
                            Bundle params = new Bundle();
                            params.putSerializable("animal", animal);

                            Intent intent = new Intent(AnimalActivity.this, AnimalAddActivity.class);
                            intent.putExtras(params);

                            startActivityForResult(intent, ADD_ANIMAL);
                            break;
                        case 1:
                            AlertDialog.Builder dialogAux = new AlertDialog.Builder(AnimalActivity.this);
                            dialogAux.setTitle(R.string.app_name);
                            dialogAux.setMessage(getString(R.string.txt_del_animal)+" "+animal.getName()+"?");
                            dialogAux.setPositiveButton(R.string.yes, (dialog12, which12) -> {

                                AnimalDAO dao = new AnimalDAO(AnimalActivity.this);
                                dao.open();

                                if ( dao.isNotDeletable(animal) ) {
                                    Message.showMessage(this, R.string.txt_permission_excluded_animal);
                                } else {

                                    try {
                                        dao.delete(animal);
                                    } catch (Exception e) {
                                        Toast.makeText(AnimalActivity.this, R.string.txt_error_excluded_animal, Toast.LENGTH_LONG).show();
                                    }

                                    List<Animal> animals = dao.getAnimals();
                                    dao.close();

                                    adapter.setListAnimal(animals);

                                    Toast.makeText(AnimalActivity.this, R.string.txt_excluded_animal, Toast.LENGTH_LONG).show();

                                }

                            });
                            dialogAux.setNegativeButton(R.string.no, (dialog1, which1) -> { });
                            dialogAux.show();
                            break;
                    }

                });

        AlertDialog options = builder.create();
        options.show();

    }

}