package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import br.com.aonsistemas.appvet.dao.ClientDAO;
import br.com.aonsistemas.appvet.model.Client;
import br.com.aonsistemas.appvet.util.Mask;
import br.com.aonsistemas.appvet.util.Message;

public class ClientAddActivity extends AppCompatActivity {

    private EditText txtNameClient, txtAddressClient, txtTelephoneClient, txtEmailClient;

    private Client client;

    private String alter = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_add);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.client);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        txtNameClient = findViewById(R.id.txtNameClient);
        txtAddressClient = findViewById(R.id.txtAddressClient);

        txtTelephoneClient = findViewById(R.id.txtTelephoneClient);
        TextWatcher telMask = Mask.insert("(##)#####-####", txtTelephoneClient);
        txtTelephoneClient.addTextChangedListener(telMask);

        txtEmailClient = findViewById(R.id.txtEmailClient);

        try {

            Intent intent = getIntent();
            Bundle params = intent.getExtras();

            this.client = (Client) params.getSerializable("client");

        } catch (Exception e) {
            this.client = null;
        }

        if ( this.client != null ) {
            this.alter = "yes";
            txtNameClient.setText(this.client.getName());
            txtAddressClient.setText(this.client.getAddress());
            txtTelephoneClient.setText(this.client.getTelephone());
            txtEmailClient.setText(this.client.getEmail());
        }

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
                saveClient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveClient() {

        if (txtNameClient.getText().toString().equals("")) {
            Message.showMessage(ClientAddActivity.this, R.string.txt_mandatory_name);
            txtNameClient.requestFocus();
            return;
        }

        try {

            if ( client == null ) {
                client = new Client();
            }

            client.setName(txtNameClient.getText().toString());
            client.setAddress(txtAddressClient.getText().toString());
            client.setTelephone(txtTelephoneClient.getText().toString());
            client.setEmail(txtEmailClient.getText().toString());

            ClientDAO dao = new ClientDAO(ClientAddActivity.this);

            dao.open();

            if (alter.equals("yes"))
                dao.update(client);
            else
                dao.insert(client);

            dao.close();

            setResult(RESULT_OK);

            if (alter.equals("yes"))
                Toast.makeText(ClientAddActivity.this, R.string.txt_alter_client, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(ClientAddActivity.this, R.string.txt_add_client, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            setResult(RESULT_CANCELED);
            Toast.makeText(ClientAddActivity.this, R.string.txt_error_add_client, Toast.LENGTH_LONG).show();
        }

        finish();

    }

}