package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.com.aonsistemas.appvet.adapter.CustomServiceItemAdapter;
import br.com.aonsistemas.appvet.dao.CustomerServiceDAO;
import br.com.aonsistemas.appvet.dao.CustomerServiceItemDAO;
import br.com.aonsistemas.appvet.fragment.AnimalSearchDialogFragment;
import br.com.aonsistemas.appvet.fragment.ClientSearchDialogFragment;
import br.com.aonsistemas.appvet.model.CustomerService;
import br.com.aonsistemas.appvet.model.CustomerServiceItem;
import br.com.aonsistemas.appvet.util.DateTime;
import br.com.aonsistemas.appvet.util.FormatValue;
import br.com.aonsistemas.appvet.util.Message;

public class CustomerServiceAddActivity extends AppCompatActivity
        implements ClientSearchDialogFragment.SearchClientDialogFragmentListener,
        AnimalSearchDialogFragment.AnimalSearchDialogFragmentListener {

    private static final int ADD_ITEM = 90;

    private EditText txtDateCustomerService, txtNoteCustomerService;
    private Button btnClientCustomerService, btnAnimalCustomerService;
    private TextView txtTotalCustomerService;

    private List<CustomerServiceItem> items;

    private CustomServiceItemAdapter adapter;

    private CustomerService customerService;

    private boolean alter = false;

    private int client_id = 0;
    private int animal_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_add);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.customer_service);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.items = new ArrayList<>();

        txtDateCustomerService = findViewById(R.id.txtDateCustomerService);
        txtDateCustomerService.setText(DateTime.dateNowBR());
        txtDateCustomerService.setOnClickListener(v -> {

            int mYear, mMonth, mDay;

            if ( txtDateCustomerService.getText().toString().equals("") ) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
            } else {
                mYear = DateTime.getYearDate(txtDateCustomerService.getText().toString());
                mMonth = DateTime.getMonthDate(txtDateCustomerService.getText().toString());
                mDay = DateTime.getDayDate(txtDateCustomerService.getText().toString());
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String _day = new DecimalFormat("00", new DecimalFormatSymbols(new Locale("pt", "BR"))).format(dayOfMonth);
                        String _month = new DecimalFormat("00", new DecimalFormatSymbols(new Locale("pt", "BR"))).format(monthOfYear+1);
                        String _year = new DecimalFormat("0000", new DecimalFormatSymbols(new Locale("pt", "BR"))).format(year);
                        String date = _day + "/" + _month + "/" + _year;
                        txtDateCustomerService.setText(date);
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();

        });

        btnClientCustomerService = findViewById(R.id.btnClientCustomerService);
        btnClientCustomerService.setOnClickListener(v -> selectClient());

        btnAnimalCustomerService = findViewById(R.id.btnAnimalCustomerService);
        btnAnimalCustomerService.setOnClickListener(v -> selectAnimal());

        ImageView imgAnimalCustomerService = findViewById(R.id.imgAnimalCustomerService);
        imgAnimalCustomerService.setOnClickListener(v -> {
            btnAnimalCustomerService.setText("");
            this.animal_id = 0;
        });

        txtNoteCustomerService = findViewById(R.id.txtNoteCustomerService);
        txtTotalCustomerService = findViewById(R.id.txtTotalCustomerService);

        ListView lstProductService = findViewById(R.id.lstProductService);
        lstProductService.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerServiceAddActivity.this);
            builder.setTitle(R.string.txt_options)
                    .setItems(R.array.options_customer_service_item, (dialog, which) -> {

                        CustomerServiceItem item = (CustomerServiceItem) view.getTag();

                        if (which == 0) {
                            AlertDialog.Builder dialogAux = new AlertDialog.Builder(CustomerServiceAddActivity.this);
                            dialogAux.setTitle(R.string.app_name);
                            dialogAux.setMessage(getString(R.string.txt_delete_customer_service_item)+" "+item.getProduct().getDescription()+"?");
                            dialogAux.setPositiveButton(R.string.yes, (dialog12, which12) -> {
                                items.remove(item);
                                adapter.setListItems(items);
                                somaTotal();
                                Toast.makeText(CustomerServiceAddActivity.this, R.string.txt_excluded_customer_service_item, Toast.LENGTH_LONG).show();
                            });
                            dialogAux.setNegativeButton(R.string.no, (dialog1, which1) -> {
                            });
                            dialogAux.show();
                        } else {
                            throw new IllegalStateException(getString(R.string.txt_unexpected_value) + which);
                        }

                    });

            AlertDialog options = builder.create();
            options.show();

            return false;

        });

        try {

            Intent intent = getIntent();
            Bundle params = intent.getExtras();

            this.customerService = (CustomerService) params.getSerializable("customerService");

        } catch (Exception e) {
            this.customerService = null;
        }

        if ( this.customerService != null ) {

            this.alter = true;

            txtDateCustomerService.setText(DateTime.formatDate(this.customerService.getData()));
            btnClientCustomerService.setText(this.customerService.getClient().getName());
            btnAnimalCustomerService.setText(this.customerService.getAnimal().getName());
            txtNoteCustomerService.setText(this.customerService.getNote());

            client_id = this.customerService.getId_client();
            animal_id = this.customerService.getId_animal();

            CustomerServiceItemDAO customerServiceItemDAO = new CustomerServiceItemDAO(this);

            customerServiceItemDAO.open();
            this.customerService.setItems(customerServiceItemDAO.getCustomerServiceItems(this.customerService.getId()));
            customerServiceItemDAO.close();

            this.items.addAll(this.customerService.getItems());

            somaTotal();

        }

        adapter = new CustomServiceItemAdapter(CustomerServiceAddActivity.this, R.layout.adapter_customer_service_item, items);
        lstProductService.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_item, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder dialog = new AlertDialog.Builder(CustomerServiceAddActivity.this);
                dialog.setTitle(R.string.app_name);
                dialog.setMessage(R.string.txt_cancel_customer_service);
                dialog.setPositiveButton(R.string.yes, (dialog1, which) -> finish());
                dialog.setNegativeButton(R.string.no, (dialog12, which) -> { });
                dialog.show();
                return true;
            case R.id.add_item:
                addItem();
                return true;
            case R.id.save:
                saveCustomerService();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void selectClient() {
        ClientSearchDialogFragment searchClient = ClientSearchDialogFragment.newInstance();
        searchClient.openDialog(getSupportFragmentManager());
    }

    private void selectAnimal() {
        AnimalSearchDialogFragment searchAnimal = AnimalSearchDialogFragment.newInstance();
        searchAnimal.openDialog(getSupportFragmentManager(), client_id);
    }

    private void addItem() {
        Intent intent = new Intent(this, CustomerServiceItemAddActivity.class);
        startActivityForResult(intent, ADD_ITEM);
    }

    private void saveCustomerService() {

        if ((txtDateCustomerService.getText().toString().trim().equals("")) || (client_id <= 0) || (this.items.size() <= 0)) {

            String msg = getString(R.string.txt_check_inconsistencies) + " \n\n";

            if ( txtDateCustomerService.getText().toString().trim().equals("") )
                msg += getString(R.string.txt_check_date) + " \n";
            if ( client_id <= 0 )
                msg += getString(R.string.txt_check_client) + " \n";
            if ( this.items.size() <= 0 )
                msg += getString(R.string.txt_check_product_service_item) + " \n";

            Message.showMessage(this, msg);
            return;

        }

        try {

            CustomerService customer = new CustomerService();

            customer.setId(this.alter && this.customerService != null ? this.customerService.getId() : 0);
            customer.setId_client(client_id);
            customer.setId_animal(animal_id);
            customer.setData(DateTime.formatDateUS(txtDateCustomerService.getText().toString()));
            customer.setNote(txtNoteCustomerService.getText().toString());
            customer.setTotal(FormatValue.valueStringBrToDouble(txtTotalCustomerService.getText().toString()));
            customer.setItems(this.items);

            CustomerServiceDAO customerServiceDAO = new CustomerServiceDAO(this);

            customerServiceDAO.open();

            if (this.alter)
                customerServiceDAO.update(customer);
            else
                customerServiceDAO.insert(customer);

            customerServiceDAO.close();

            setResult(RESULT_OK);

            if (this.alter)
                Toast.makeText(this, R.string.txt_alter_customer_service, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, R.string.txt_add_customer_service, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, R.string.txt_error_add_customer_service, Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
        }

        finish();

    }

    @Override
    public void onFinishEditAnimalDialog(int id, int client_id, String nome, String client_name) {
        btnClientCustomerService.setText(client_name);
        btnAnimalCustomerService.setText(nome);
        this.client_id = client_id;
        this.animal_id = id;
    }

    @Override
    public void onFinishEditClientDialog(int id, String nome) {
        btnClientCustomerService.setText(nome);
        btnAnimalCustomerService.setText("");
        this.client_id = id;
        this.animal_id = 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (( resultCode == RESULT_OK ) && ( requestCode == ADD_ITEM )) {

            Bundle params = data != null ? data.getExtras() : null;

            if ( params != null && params.containsKey("item")) {

                CustomerServiceItem item = (CustomerServiceItem) params.getSerializable("item");
                this.items.add(item);
                adapter.setListItems(this.items);

                somaTotal();

            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void somaTotal() {

        Double total = 0.0;

        for (CustomerServiceItem item : items) {
            total += item.getTotalValue();
        }

        txtTotalCustomerService.setText(FormatValue.value_2_decimal(total));

    }

}