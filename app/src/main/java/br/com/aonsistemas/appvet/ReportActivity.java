package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.aonsistemas.appvet.dao.CustomerServiceDAO;
import br.com.aonsistemas.appvet.dao.SettingsDAO;
import br.com.aonsistemas.appvet.fragment.AnimalSearchDialogFragment;
import br.com.aonsistemas.appvet.fragment.ClientSearchDialogFragment;
import br.com.aonsistemas.appvet.model.CustomerService;
import br.com.aonsistemas.appvet.model.CustomerServiceItem;
import br.com.aonsistemas.appvet.model.Settings;
import br.com.aonsistemas.appvet.util.Constants;
import br.com.aonsistemas.appvet.util.DateTime;
import br.com.aonsistemas.appvet.util.FormatValue;
import br.com.aonsistemas.appvet.util.Message;
import br.com.aonsistemas.appvet.util.PrintPdf;

public class ReportActivity extends AppCompatActivity
        implements ClientSearchDialogFragment.SearchClientDialogFragmentListener,
        AnimalSearchDialogFragment.AnimalSearchDialogFragmentListener {

    private static final int REQUEST_PERMISSIONS = 1;

    private ProgressBar progressBarReport;

    private EditText txtFirstDateReport, txtLastDateReport;

    private Button btnClientReport, btnAnimalReport;

    private String PATH_LOCAL_PDF;

    private int client_id = 0;
    private int animal_id = 0;

    private int mYear, mMonth, mDay;

    private Settings config;

    private String message;
    private boolean mkdir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.report_print);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressBarReport = findViewById(R.id.progressBarReport);

        btnClientReport = findViewById(R.id.btnClientReport);
        btnClientReport.setOnClickListener(v -> selectClient());

        ImageView imgClientReport = findViewById(R.id.imgClientReport);
        imgClientReport.setOnClickListener(v -> {
            btnClientReport.setText("");
            btnAnimalReport.setText("");
            this.client_id = 0;
            this.animal_id = 0;
        });

        btnAnimalReport = findViewById(R.id.btnAnimalReport);
        btnAnimalReport.setOnClickListener(v -> selectAnimal());

        ImageView imgAnimalReport = findViewById(R.id.imgAnimalReport);
        imgAnimalReport.setOnClickListener(v -> {
            btnAnimalReport.setText("");
            this.animal_id = 0;
        });

        txtFirstDateReport = findViewById(R.id.txtFirstDateReport);
        txtFirstDateReport.setOnClickListener(v -> {

            if ( txtFirstDateReport.getText().toString().equals("") ) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
            } else {
                mYear = DateTime.getYearDate(txtFirstDateReport.getText().toString());
                mMonth = DateTime.getMonthDate(txtFirstDateReport.getText().toString());
                mDay = DateTime.getDayDate(txtFirstDateReport.getText().toString());
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String _day = new DecimalFormat("00", new DecimalFormatSymbols(new Locale("pt", "BR"))).format(dayOfMonth);
                        String _month = new DecimalFormat("00", new DecimalFormatSymbols(new Locale("pt", "BR"))).format(monthOfYear+1);
                        String _year = new DecimalFormat("0000", new DecimalFormatSymbols(new Locale("pt", "BR"))).format(year);
                        String date = _day + "/" + _month + "/" + _year;
                        txtFirstDateReport.setText(date);
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();

        });

        txtLastDateReport = findViewById(R.id.txtLastDateReport);
        txtLastDateReport.setOnClickListener(v -> {

            if ( txtLastDateReport.getText().toString().equals("") ) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
            } else {
                mYear = DateTime.getYearDate(txtLastDateReport.getText().toString());
                mMonth = DateTime.getMonthDate(txtLastDateReport.getText().toString());
                mDay = DateTime.getDayDate(txtLastDateReport.getText().toString());
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String _day = new DecimalFormat("00", new DecimalFormatSymbols(new Locale("pt", "BR"))).format(dayOfMonth);
                        String _month = new DecimalFormat("00", new DecimalFormatSymbols(new Locale("pt", "BR"))).format(monthOfYear+1);
                        String _year = new DecimalFormat("0000", new DecimalFormatSymbols(new Locale("pt", "BR"))).format(year);
                        String date = _day + "/" + _month + "/" + _year;
                        txtLastDateReport.setText(date);
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();

        });

        txtFirstDateReport.setText(DateTime.firstDay());
        txtLastDateReport.setText(DateTime.lastDay());

        SettingsDAO settingsDAO = new SettingsDAO(this);

        settingsDAO.open();
        config = settingsDAO.getConfiguration();
        settingsDAO.close();

        PATH_LOCAL_PDF = getExternalFilesDir(null).getAbsolutePath();

        permissionPdf();

        Button btnVisualizarRelatorio = findViewById(R.id.btnVisualizarRelatorio);
        btnVisualizarRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPdf();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.relatorio:
                generateReport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onFinishEditClientDialog(int id, String name) {
        btnClientReport.setText(name);
        btnAnimalReport.setText("");
        this.client_id = id;
        this.animal_id = 0;
    }

    @Override
    public void onFinishEditAnimalDialog(int id, int client_id, String name, String client_name) {
        btnClientReport.setText(client_name);
        btnAnimalReport.setText(name);
        this.client_id = client_id;
        this.animal_id = id;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void selectClient() {
        ClientSearchDialogFragment searchClient = ClientSearchDialogFragment.newInstance();
        searchClient.openDialog(getSupportFragmentManager());
    }

    private void selectAnimal() {
        AnimalSearchDialogFragment searchAnimal = AnimalSearchDialogFragment.newInstance();
        searchAnimal.openDialog(getSupportFragmentManager(), client_id);
    }


    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    private void permissionPdf() {

        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((!ActivityCompat.shouldShowRequestPermissionRationale(ReportActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
                ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }

            if ((!ActivityCompat.shouldShowRequestPermissionRationale(ReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }

        }

    }

    private void viewPdf() {
        Intent showPdfActivity = new Intent(ReportActivity.this, ShowPdfActivity.class);
        showPdfActivity.putExtra("client_id", client_id);
        startActivity(showPdfActivity);
    }

    private void generateReport() {

        message = "";
        mkdir = true;

        if ( !isExternalStorageAvailable() ) {
            Message.showMessage(ReportActivity.this, R.string.txt_external_storage);
            return;
        }

        progressBarReport.setVisibility(ProgressBar.VISIBLE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            CustomerServiceDAO customerServiceDAO = new CustomerServiceDAO(ReportActivity.this);

            customerServiceDAO.open();
            List<CustomerService> customerServices = customerServiceDAO.getCustomerServices(
                    DateTime.formatDateUS(txtFirstDateReport.getText().toString()),
                    DateTime.formatDateUS(txtLastDateReport.getText().toString()),
                    client_id,
                    animal_id);
            customerServiceDAO.close();

            if ( customerServices.size() > 0 ) {

                try {

                    File folderRoot = new File(PATH_LOCAL_PDF);

                    if (!folderRoot.exists()) {
                        mkdir = folderRoot.mkdirs();
                    }

                    if (mkdir) {

                        File folder = new File(folderRoot, "pdf");

                        if (!folder.exists()) {
                            mkdir = folder.mkdir();
                        }

                        if (mkdir) {

                            final File pdf = new File(folder, Constants.NOME_FILE_PDF);

                            if (pdf.exists()) {
                                pdf.delete();
                            }

                            final boolean newFile = pdf.createNewFile();

                            if (newFile) {

                                PrintPdf printPdf = new PrintPdf(this);

                                printPdf.setCabecalho(true);
                                printPdf.setRodape(true);
                                printPdf.setLogo(config.getLogo() != null ? new File(config.getLogo()) : null);
                                printPdf.setCabecalhoLinha1(config.getNome() != null ? config.getNome() : "");
                                printPdf.setCabecalhoLinha2(config.getAddress() != null ? config.getAddress() : "");
                                printPdf.setCabecalhoLinha3(config.getContact() != null ? config.getContact() : "");
                                printPdf.setTitulo(getString(R.string.txt_report));

                                printPdf.abrir();

                                int client = 0;

                                double totalClient = 0.0;
                                double totalGeneral = 0.0;

                                for (CustomerService customerService : customerServices) {

                                    if (client == 0 || client != customerService.getClient().getId()) {

                                        if (client > 0) {

                                            printPdf.escreverNegritoDireita(getString(R.string.txt_total_client), PrintPdf.MARGIN_RIGTH - 140F);
                                            printPdf.escreverNegritoDireita(FormatValue.value_2_decimal(totalClient), PrintPdf.MARGIN_RIGTH);
                                            printPdf.novaLinha(2);

                                            printPdf.linhaHorizontalM();
                                            printPdf.novaLinha();

                                        }

                                        totalClient = 0.0;

                                        printPdf.escrever(getString(R.string.client), PrintPdf.MARGIN_LEFT);
                                        printPdf.escreverNegrito(customerService.getClient().getName(), 75F);
                                        client = customerService.getClient().getId();
                                        printPdf.novaLinha();

                                    } else if (client > 0) {
                                        printPdf.novaLinha();
                                    }

                                    printPdf.escrever(getString(R.string.date), PrintPdf.MARGIN_LEFT);
                                    printPdf.escreverNegrito(DateTime.formatDate(customerService.getData()), 55F);

                                    printPdf.escrever(getString(R.string.animal), 200F);
                                    printPdf.escreverNegrito(customerService.getAnimal().getName(), 250F);
                                    printPdf.novaLinha();

                                    printPdf.escrever(getString(R.string.products_services), PrintPdf.MARGIN_LEFT);
                                    printPdf.escrever(getString(R.string.value_item), PrintPdf.MARGIN_RIGTH - 240F);
                                    printPdf.escrever(getString(R.string.amount_item), PrintPdf.MARGIN_RIGTH - 140F);
                                    printPdf.escrever(getString(R.string.total_item), PrintPdf.MARGIN_RIGTH - 40F);
                                    printPdf.novaLinha();

                                    double totalCustomerService = 0.0;

                                    for (CustomerServiceItem item : customerService.getItems()) {

                                        printPdf.escrever(item.getProduct().getDescription(), PrintPdf.MARGIN_LEFT, 50);
                                        printPdf.escreverDireita(FormatValue.value_2_decimal(item.getValue()), PrintPdf.MARGIN_RIGTH - 200F);
                                        printPdf.escreverDireita(FormatValue.value_3_decimal(item.getAmount()), PrintPdf.MARGIN_RIGTH - 100F);
                                        printPdf.escreverDireita(FormatValue.value_2_decimal(item.getTotalValue()), PrintPdf.MARGIN_RIGTH);
                                        printPdf.novaLinha();

                                        totalCustomerService += item.getTotalValue();

                                    }

                                    totalClient += totalCustomerService;
                                    totalGeneral += totalCustomerService;

                                    printPdf.escreverNegritoDireita(getString(R.string.total), PrintPdf.MARGIN_RIGTH - 140F);
                                    printPdf.escreverNegritoDireita(FormatValue.value_2_decimal(totalCustomerService), PrintPdf.MARGIN_RIGTH);
                                    printPdf.novaLinha();

                                }

                                printPdf.escreverNegritoDireita(getString(R.string.txt_total_client), PrintPdf.MARGIN_RIGTH - 140F);
                                printPdf.escreverNegritoDireita(FormatValue.value_2_decimal(totalClient), PrintPdf.MARGIN_RIGTH);
                                printPdf.novaLinha(2);

                                printPdf.escreverNegritoDireita(getString(R.string.txt_total_general), PrintPdf.MARGIN_RIGTH - 140F);
                                printPdf.escreverNegritoDireita(FormatValue.value_2_decimal(totalGeneral), PrintPdf.MARGIN_RIGTH);

                                printPdf.finalizarPagina();
                                printPdf.salvar(pdf);

                                message = getString(R.string.txt_create_report_success);

                            } else {
                                message = getString(R.string.txt_create_report_error);
                            }

                        } else {
                            message = getString(R.string.txt_not_create_folder);
                        }

                    } else {
                        message = getString(R.string.txt_not_create_folder);
                    }

                } catch (Exception e) {
                    message = getString(R.string.txt_create_report_error);
                }

            } else {
                message = getString(R.string.txt_not_search_customer_service);
            }

            handler.post(() -> {
                progressBarReport.setVisibility(ProgressBar.GONE);
                if (!message.equals("")) {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
                viewPdf();
            });

        });

    }

}