package br.com.aonsistemas.appvet;

import static br.com.aonsistemas.appvet.R.*;

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

import br.com.aonsistemas.appvet.adapter.ProductAdapter;
import br.com.aonsistemas.appvet.dao.ProductDAO;
import br.com.aonsistemas.appvet.model.Product;
import br.com.aonsistemas.appvet.util.Message;

public class ProductActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener,
        ClickRecyclerViewInterface {

    private static final int ADD_PRODUCT = 97;

    private RecyclerView lstProducts;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Objects.requireNonNull(getSupportActionBar()).setTitle(string.add_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ProgressBar progressBar = findViewById(R.id.progressBarProduct);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        SearchView edtSearchProduct = findViewById(id.edtSearchProduct);
        edtSearchProduct.setOnQueryTextListener(this);
        edtSearchProduct.setQueryHint(getString(R.string.txt_description_product));
        edtSearchProduct.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edtSearchProduct.setSubmitButtonEnabled(false);

        adapter = new ProductAdapter(null, ProductActivity.this);

        lstProducts = findViewById(id.lstProducts);
        lstProducts.setAdapter(adapter);
        lstProducts.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        lstProducts.setHasFixedSize(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            ProductDAO productDAO = new ProductDAO(ProductActivity.this);

            productDAO.open();
            List<Product> products = productDAO.getProducts();
            productDAO.close();

            handler.post(() -> {
                adapter.setListProduct(products);
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
                Intent intent = new Intent(ProductActivity.this, ProductAddActivity.class);
                startActivityForResult(intent, ADD_PRODUCT);
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

        if (( resultCode == RESULT_OK ) && ( requestCode == ADD_PRODUCT)) {

            ProductDAO dao = new ProductDAO(ProductActivity.this);

            dao.open();
            List<Product> products = dao.getProducts();
            dao.close();

            adapter.setListProduct(products);

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onCustomClick(Object object) {

    }

    @Override
    public void onCustomLongClick(Object object) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
        builder.setTitle(string.txt_options)
                .setItems(array.options_product, (dialog, which) -> {

                    Product product = (Product) object;

                    switch (which) {
                        case 0:

                            Bundle params = new Bundle();
                            params.putSerializable("product", product);

                            Intent intent = new Intent(ProductActivity.this, ProductAddActivity.class);
                            intent.putExtras(params);

                            startActivityForResult(intent, ADD_PRODUCT);

                            break;
                        case 1:
                            AlertDialog.Builder dialogAux = new AlertDialog.Builder(ProductActivity.this);
                            dialogAux.setTitle(string.app_name);
                            dialogAux.setMessage(getString(R.string.txt_delete_product)+" "+product.getDescription()+"?");
                            dialogAux.setPositiveButton(string.yes, (dialog12, which12) -> {

                                ProductDAO dao = new ProductDAO(ProductActivity.this);
                                dao.open();

                                if ( dao.isNotDeletable(product) ) {
                                    Message.showMessage(this, R.string.txt_permission_excluded_product);
                                } else {

                                    try {
                                        dao.delete(product);
                                    } catch (Exception e) {
                                        Toast.makeText(ProductActivity.this, R.string.txt_error_excluded_product, Toast.LENGTH_LONG).show();
                                    }

                                    List<Product> products = dao.getProducts();
                                    dao.close();

                                    adapter.setListProduct(products);

                                    Toast.makeText(ProductActivity.this, R.string.txt_add_product, Toast.LENGTH_LONG).show();

                                }

                            });
                            dialogAux.setNegativeButton(string.no, (dialog1, which1) -> { });
                            dialogAux.show();
                            break;
                    }

                });

        AlertDialog options = builder.create();
        options.show();

    }

}