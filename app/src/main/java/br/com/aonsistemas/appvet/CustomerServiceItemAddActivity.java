package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import br.com.aonsistemas.appvet.fragment.ProductSearchDialogFragment;
import br.com.aonsistemas.appvet.model.CustomerServiceItem;
import br.com.aonsistemas.appvet.model.Product;
import br.com.aonsistemas.appvet.util.DecimalDigitsInputFilter;
import br.com.aonsistemas.appvet.util.FormatValue;
import br.com.aonsistemas.appvet.util.Message;

public class CustomerServiceItemAddActivity extends AppCompatActivity
        implements ProductSearchDialogFragment.SearchProductDialogFragmentListener {

    private Button btnProductItem;
    private EditText txtValueItem, txtAmountItem, txtTotalItem;

    private int product_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_item_add);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.customer_service_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        btnProductItem = findViewById(R.id.btnProductItem);
        btnProductItem.setOnClickListener(v -> selectProduct());

        txtValueItem = findViewById(R.id.txtValueItem);
        txtValueItem.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(11,2)});
        txtValueItem.setOnFocusChangeListener((v, hasFocus) -> updateTotal());

        txtAmountItem = findViewById(R.id.txtAmountItem);
        txtAmountItem.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(11,3)});
        txtAmountItem.setOnFocusChangeListener((v, hasFocus) -> updateTotal());

        txtTotalItem = findViewById(R.id.txtTotalItem);

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
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.salvar_cadastro:
                saveItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onFinishEditProductDialog(int id, String description, Double valor, String unity) {

        btnProductItem.setText(description);
        txtValueItem.setText(returnValue2(valor));
        txtAmountItem.setText("");
        txtTotalItem.setText("");

        product_id = id;

    }

    private Double returnValue(String valor) {

        if (!valor.trim().equals("")) {
            try {
                return Double.parseDouble(valor);
            } catch (Exception ignored) { }
        }

        return 0.0;

    }

    private String returnValue2(Double valor) {
        String val = FormatValue.value_2_decimal(valor);
        String[] part = val.split("[,]");
        return part[0]+"."+part[1];
    }

    private void selectProduct() {
        ProductSearchDialogFragment searchProduct = ProductSearchDialogFragment.newInstance();
        searchProduct.openDialog(getSupportFragmentManager());
    }

    private void updateTotal() {

        double total = 0.0;

        try {
            total = returnValue(txtValueItem.getText().toString()) * returnValue(txtAmountItem.getText().toString());
        } catch (Exception ignored) { }

        txtTotalItem.setText(returnValue2(total));

    }

    private void saveItem() {

        updateTotal();

        if ( product_id <= 0 || returnValue(txtValueItem.getText().toString()) <= 0.0 || returnValue(txtAmountItem.getText().toString()) <= 0.0 ) {

            String msg = getString(R.string.txt_check_inconsistencies) + " \n\n";

            if ( product_id <= 0 )
                msg += getString(R.string.txt_check_product_service) + " \n";
            if ( returnValue(txtValueItem.getText().toString()) <= 0.0 )
                msg += getString(R.string.txt_check_value) + " \n";
            if ( returnValue(txtAmountItem.getText().toString()) <= 0.0 )
                msg += getString(R.string.txt_check_amount) +  " \n";

            Message.showMessage(this, msg);
            return;

        }

        hideKeyboard(this);

        CustomerServiceItem item = new CustomerServiceItem();

        item.setIdProduct(product_id);
        item.setValue(returnValue(txtValueItem.getText().toString()));
        item.setAmount(returnValue(txtAmountItem.getText().toString()));
        item.setTotalValue(returnValue(txtTotalItem.getText().toString()));

        Product product = new Product();
        product.setId(product_id);
        product.setDescription(btnProductItem.getText().toString());

        item.setProduct(product);

        Intent intent = new Intent();
        intent.putExtra("item", item);

        setResult(RESULT_OK, intent);
        finish();

    }

    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

}