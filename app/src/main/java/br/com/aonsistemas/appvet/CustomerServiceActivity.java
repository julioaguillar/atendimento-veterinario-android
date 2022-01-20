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

import br.com.aonsistemas.appvet.adapter.CustomerServiceAdapter;
import br.com.aonsistemas.appvet.dao.CustomerServiceDAO;
import br.com.aonsistemas.appvet.model.CustomerService;

public class CustomerServiceActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, ClickRecyclerViewInterface {

    private static final int ADD_CUSTOMER_SERVICE = 95;

    private RecyclerView lstCustomerService;
    private CustomerServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.customer_service);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ProgressBar progressBar = findViewById(R.id.progressBarCustomer);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        SearchView svSearchCustomerService = findViewById(R.id.svSearchCustomerService);
        svSearchCustomerService.setOnQueryTextListener(this);
        svSearchCustomerService.setQueryHint(getString(R.string.txt_client_animal_note));
        svSearchCustomerService.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        svSearchCustomerService.setSubmitButtonEnabled(false);

        adapter = new CustomerServiceAdapter(null, CustomerServiceActivity.this);

        lstCustomerService = findViewById(R.id.lstCustomerService);
        lstCustomerService.setAdapter(adapter);
        lstCustomerService.setLayoutManager(new LinearLayoutManager(CustomerServiceActivity.this));
        lstCustomerService.setHasFixedSize(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            CustomerServiceDAO customerServiceDAO = new CustomerServiceDAO(CustomerServiceActivity.this);

            customerServiceDAO.open();
            List<CustomerService> customerServices = customerServiceDAO.getCustomerServices();
            customerServiceDAO.close();

            handler.post(() -> {
                adapter.setListCustomerService(customerServices);
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
                Intent intent = new Intent(CustomerServiceActivity.this, CustomerServiceAddActivity.class);
                startActivityForResult(intent, ADD_CUSTOMER_SERVICE);
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

        if (( resultCode == RESULT_OK ) && ( requestCode == ADD_CUSTOMER_SERVICE)) {

            CustomerServiceDAO dao = new CustomerServiceDAO(CustomerServiceActivity.this);

            dao.open();
            List<CustomerService> customerServices = dao.getCustomerServices();
            dao.close();

            adapter.setListCustomerService(customerServices);

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onCustomClick(Object object) {

    }

    @Override
    public void onCustomLongClick(Object object) {

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerServiceActivity.this);
        builder.setTitle(R.string.txt_options)
                .setItems(R.array.options_customer_service, (dialog, which) -> {

                    CustomerService customerService = (CustomerService) object;

                    switch (which) {
                        case 0:
                            Bundle params = new Bundle();
                            params.putSerializable("customerService", customerService);

                            Intent intent = new Intent(CustomerServiceActivity.this, CustomerServiceAddActivity.class);
                            intent.putExtras(params);

                            startActivityForResult(intent, ADD_CUSTOMER_SERVICE);
                            break;
                        case 1:
                            AlertDialog.Builder dialogAux = new AlertDialog.Builder(CustomerServiceActivity.this);
                            dialogAux.setTitle(R.string.app_name);
                            dialogAux.setMessage(R.string.txt_delete_customer_service+"?");
                            dialogAux.setPositiveButton(R.string.yes, (dialog12, which12) -> {

                                CustomerServiceDAO dao = new CustomerServiceDAO(CustomerServiceActivity.this);
                                dao.open();

                                try {
                                    dao.delete(customerService);
                                } catch (Exception e) {
                                    Toast.makeText(CustomerServiceActivity.this, R.string.txt_error_excluded_customer_service, Toast.LENGTH_LONG).show();
                                }

                                List<CustomerService> customerServices = dao.getCustomerServices();
                                dao.close();

                                adapter.setListCustomerService(customerServices);

                                Toast.makeText(CustomerServiceActivity.this, R.string.txt_excluded_customer_service, Toast.LENGTH_LONG).show();

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