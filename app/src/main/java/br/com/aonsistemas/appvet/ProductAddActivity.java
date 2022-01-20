package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import br.com.aonsistemas.appvet.dao.ProductDAO;
import br.com.aonsistemas.appvet.model.Product;
import br.com.aonsistemas.appvet.util.DecimalDigitsInputFilter;
import br.com.aonsistemas.appvet.util.Message;

public class ProductAddActivity extends AppCompatActivity {

    private EditText txtDescriptionProduct, txtUnityProduct, txtValueProduct, txtNoteProduct;

    private Product product;

    private String alter = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.product_service);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        txtDescriptionProduct = findViewById(R.id.txtDescriptionProduct);
        txtUnityProduct = findViewById(R.id.txtUnityProduct);

        txtValueProduct = findViewById(R.id.txtValueProduct);
        txtValueProduct.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(11,2)});

        txtNoteProduct = findViewById(R.id.txtNoteProduct);

        try {

            Intent intent = getIntent();
            Bundle params = intent.getExtras();

            this.product = (Product) params.getSerializable("product");

        } catch (Exception e) {
            this.product = null;
        }

        if ( this.product != null ) {
            this.alter = "yes";
            txtDescriptionProduct.setText(this.product.getDescription());
            txtUnityProduct.setText(this.product.getUnity());
            txtNoteProduct.setText(this.product.getNote());
            txtValueProduct.setText(String.valueOf(this.product.getValor()));
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
                saveProduct();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveProduct() {

        double value = 0.0;

        if (txtDescriptionProduct.getText().toString().equals("")) {
            Message.showMessage(ProductAddActivity.this, R.string.txt_mandatory_description);
            txtDescriptionProduct.requestFocus();
            return;
        }

        if ( !txtValueProduct.getText().toString().equals("") ) {
            try {
                value = Double.parseDouble(txtValueProduct.getText().toString());
            } catch (Exception e) {
                Message.showMessage(ProductAddActivity.this, R.string.txt_invalid_value);
                txtValueProduct.requestFocus();
                return;
            }
        }

        try {

            if ( product == null ) {
                product = new Product();
            }

            product.setDescription(txtDescriptionProduct.getText().toString());
            product.setUnity(txtUnityProduct.getText().toString());
            product.setNote(txtNoteProduct.getText().toString());
            product.setValor(value);

            ProductDAO dao = new ProductDAO(ProductAddActivity.this);

            dao.open();

            if (alter.equals("yes"))
                dao.update(product);
            else
                dao.insert(product);

            dao.close();

            setResult(RESULT_OK);

            if (alter.equals("yes"))
                Toast.makeText(ProductAddActivity.this, R.string.txt_alter_product, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(ProductAddActivity.this, R.string.txt_add_product, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            setResult(RESULT_CANCELED);
            Toast.makeText(ProductAddActivity.this, R.string.txt_error_add_product, Toast.LENGTH_LONG).show();
        }

        finish();

    }

}