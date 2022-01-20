package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.aonsistemas.appvet.adapter.ClientAdapter;
import br.com.aonsistemas.appvet.dao.ClientDAO;
import br.com.aonsistemas.appvet.fragment.AnimalSearchDialogFragment;
import br.com.aonsistemas.appvet.model.Client;
import br.com.aonsistemas.appvet.util.Message;
import br.com.aonsistemas.appvet.util.Text;

public class ClientActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener,
        AnimalSearchDialogFragment.AnimalSearchDialogFragmentListener,
        ClickRecyclerViewInterface {

    private static final int ADD_CLIENT = 98;

    private RecyclerView lstClients;
    private ClientAdapter adapter;

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.add_client);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ProgressBar progressBar = findViewById(R.id.progressBarClient);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        SearchView svSearchClient = findViewById(R.id.svSearchClient);
        svSearchClient.setOnQueryTextListener(this);
        svSearchClient.setQueryHint(getString(R.string.txt_name_client));
        svSearchClient.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        svSearchClient.setSubmitButtonEnabled(false);

        adapter = new ClientAdapter(null, ClientActivity.this);

        lstClients = findViewById(R.id.lstClients);
        lstClients.setAdapter(adapter);
        lstClients.setLayoutManager(new LinearLayoutManager(ClientActivity.this));
        lstClients.setHasFixedSize(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            ClientDAO clientDAO = new ClientDAO(ClientActivity.this);

            clientDAO.open();
            List<Client> clients = clientDAO.getClients();
            clientDAO.close();

            handler.post(() -> {
                adapter.setListClient(clients);
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
                Intent intent = new Intent(ClientActivity.this, ClientAddActivity.class);
                startActivityForResult(intent, ADD_CLIENT);
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

        if (( resultCode == RESULT_OK ) && ( requestCode == ADD_CLIENT)) {

            ClientDAO dao = new ClientDAO(ClientActivity.this);

            dao.open();
            List<Client> clients = dao.getClients();
            dao.close();

            adapter.setListClient(clients);

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onFinishEditAnimalDialog(int id, int client_id, String nome, String client_name) {

    }

    @Override
    public void onCustomClick(Object object) {

    }

    @Override
    public void onCustomLongClick(Object object) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ClientActivity.this);
        builder.setTitle(R.string.txt_options)
                .setItems(R.array.options_client, (dialog, which) -> {

                    Client client = (Client) object;

                    switch (which) {
                        case 0:
                            Bundle params = new Bundle();
                            params.putSerializable("client", client);

                            Intent intent = new Intent(ClientActivity.this, ClientAddActivity.class);
                            intent.putExtras(params);

                            startActivityForResult(intent, ADD_CLIENT);
                            break;
                        case 1:
                            AlertDialog.Builder dialogAux = new AlertDialog.Builder(ClientActivity.this);
                            dialogAux.setTitle(R.string.app_name);
                            dialogAux.setMessage(getString(R.string.txt_delete_client)+" "+client.getName()+"?");
                            dialogAux.setPositiveButton(R.string.yes, (dialog12, which12) -> {

                                ClientDAO dao = new ClientDAO(ClientActivity.this);
                                dao.open();

                                if ( dao.isNotDeletable(client) ) {
                                    Message.showMessage(this, R.string.txt_permission_excluded_client);
                                } else {

                                    try {
                                        dao.delete(client);
                                    } catch (Exception e) {
                                        Toast.makeText(ClientActivity.this, R.string.txt_error_excluded_client, Toast.LENGTH_LONG).show();
                                    }

                                    List<Client> clients = dao.getClients();
                                    dao.close();

                                    adapter.setListClient(clients);

                                    Toast.makeText(ClientActivity.this, R.string.txt_excluded_client, Toast.LENGTH_LONG).show();

                                }

                            });
                            dialogAux.setNegativeButton(R.string.no, (dialog1, which1) -> { });
                            dialogAux.show();
                            break;
                        case 2:
                            AnimalSearchDialogFragment searchAnimal = AnimalSearchDialogFragment.newInstance();
                            searchAnimal.openDialog(getSupportFragmentManager(), client.getId(), true);
                            break;
                        case 3:

                            Intent Mail = new Intent(Intent.ACTION_SEND);
                            Mail.setType("text/plain");
                            Mail.putExtra(Intent.EXTRA_TEXT, getString(R.string.send_to));

                            if (!client.getEmail().equals(""))
                                Mail.putExtra(Intent.EXTRA_EMAIL, new String[]{client.getEmail()});

                            try {
                                startActivity(Intent.createChooser(Mail, getString(R.string.email)));
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(this, R.string.txt_error_send_email, Toast.LENGTH_LONG).show();
                            }

                            break;
                        case 4:

                            String phone = Text.somenteNumeros(client.getTelephone());
                            Intent call = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));

                            try {
                                startActivity(call);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(this, R.string.txt_error_call, Toast.LENGTH_LONG).show();
                            }

                            break;
                    }

                });

        AlertDialog options = builder.create();
        options.show();

    }

}