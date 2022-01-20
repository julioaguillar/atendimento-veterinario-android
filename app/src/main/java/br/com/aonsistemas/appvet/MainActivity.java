package br.com.aonsistemas.appvet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCustomerService = findViewById(R.id.btnCustomerService);
        btnCustomerService.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CustomerServiceActivity.class)));

        Button btnClient = findViewById(R.id.btnClient);
        btnClient.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ClientActivity.class)));

        Button btnAnimal = findViewById(R.id.btnAnimal);
        btnAnimal.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AnimalActivity.class)));

        Button btnProductService = findViewById(R.id.btnProductService);
        btnProductService.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProductActivity.class)));

        Button btnReport = findViewById(R.id.btnReport);
        btnReport.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ReportActivity.class)));

        TextView btnConfiguration = findViewById(R.id.btnConfiguration);
        btnConfiguration.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        TextView btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle(R.string.app_name);
            dialog.setMessage(R.string.txt_exit);
            dialog.setPositiveButton(R.string.yes, (dialog1, which) -> finish());
            dialog.setNegativeButton(R.string.no, (dialog12, which) -> { });
            dialog.show();
        });

    }

}