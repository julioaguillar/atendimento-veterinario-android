package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Objects;

import br.com.aonsistemas.appvet.dao.SettingsDAO;
import br.com.aonsistemas.appvet.model.Settings;

public class SettingsActivity extends AppCompatActivity {

    private static final int SEL_IMG = 112;
    private static final int REQUEST_PERMISSIONS = 101;

    private EditText txtKeySettings, txtNameSettings, txtAddressSettings, txtContactSettings;
    private ImageView imgLogo;

    private String uri_image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txtKeySettings = findViewById(R.id.txtKeySettings);
        txtNameSettings = findViewById(R.id.txtNameSettings);
        txtAddressSettings = findViewById(R.id.txtAddressSettings);
        txtContactSettings = findViewById(R.id.txtContactSettings);
        imgLogo = findViewById(R.id.imgLogo);

        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.txt_sel_image)), SEL_IMG);
        });

        Button btnClearImage = findViewById(R.id.btnClearImage);
        btnClearImage.setOnClickListener(v -> {
            imgLogo.setImageBitmap(null);
            uri_image = "";
        });

        SettingsDAO settingsDAO = new SettingsDAO(this);

        settingsDAO.open();
        Settings settings = settingsDAO.getConfiguration();
        settingsDAO.close();

        if (settings.getSerial() != null && !settings.getSerial().equals(""))
            txtKeySettings.setText(settings.getSerial());

        if (settings.getNome() != null && !settings.getNome().equals(""))
            txtNameSettings.setText(settings.getNome());

        if (settings.getAddress() != null && !settings.getAddress().equals(""))
            txtAddressSettings.setText(settings.getAddress());

        if (settings.getContact() != null && !settings.getContact().equals(""))
            txtContactSettings.setText(settings.getContact());

        if (settings.getLogo() != null && !settings.getLogo().equals("")) {
            File file = new File(settings.getLogo());
            if (file.exists()) {
                Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
                imgLogo.setImageBitmap(img);
                uri_image = settings.getLogo();
            }
        }

        permission();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.salvar_cadastro:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void moveFile(File sourceFile, File destFile) throws IOException {

        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = new FileInputStream(sourceFile).getChannel();
        FileChannel destination = new FileOutputStream(destFile).getChannel();

        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }

        if (source != null) {
            source.close();
        }

        if (destination != null) {
            destination.close();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SEL_IMG) {

            try {

                boolean _mkdirs = true;

                Uri uriSelected = data != null ? data.getData() : null;

                final File selected = new File(getRealPathFromURI(uriSelected));

                String PATH_LOCAL_IMG = getExternalFilesDir(null).getAbsolutePath() + "/img";
                final File path = new File(PATH_LOCAL_IMG);

                if (!path.exists()) {
                    _mkdirs = path.mkdirs();
                }

                if (_mkdirs) {

                    final File newImage = new File(path, selected.getName());

                    moveFile(selected, newImage);

                    if (newImage.exists()) {

                        Bitmap img = BitmapFactory.decodeFile(newImage.getAbsolutePath());
                        imgLogo.setImageBitmap(img);

                        this.uri_image = newImage.getAbsolutePath();

                    }

                } else {
                    Toast.makeText(this, R.string.txt_error_load_image, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, R.string.txt_error_load_image, Toast.LENGTH_LONG).show();
            }

        }

    }

    private void save() {

        Settings settings = new Settings();

        settings.setSerial(txtKeySettings.getText().toString());
        settings.setNome(txtNameSettings.getText().toString());
        settings.setAddress(txtAddressSettings.getText().toString());
        settings.setContact(txtContactSettings.getText().toString());
        settings.setLogo(this.uri_image);

        try {

            SettingsDAO settingsDAO = new SettingsDAO(this);

            settingsDAO.open();
            settingsDAO.update(settings);
            settingsDAO.close();

            Toast.makeText(this, R.string.txt_add_configurations, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, R.string.txt_error_add_configurations, Toast.LENGTH_LONG).show();
        }

        finish();

    }

    private void permission() {

        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((!ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
                ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }

            if ((!ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

}