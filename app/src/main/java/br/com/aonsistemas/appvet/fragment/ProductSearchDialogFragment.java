package br.com.aonsistemas.appvet.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.aonsistemas.appvet.ClickRecyclerViewInterface;
import br.com.aonsistemas.appvet.R;
import br.com.aonsistemas.appvet.adapter.ProductAdapter;
import br.com.aonsistemas.appvet.dao.ProductDAO;
import br.com.aonsistemas.appvet.model.Product;

public class ProductSearchDialogFragment extends DialogFragment
    implements ClickRecyclerViewInterface {

    private static final String DIALOG_TAG = "DialogProduct";

    private ProductAdapter adapter;

    public interface SearchProductDialogFragmentListener {
        void onFinishEditProductDialog(int id, String description, Double value, String unity);
    }

    public static ProductSearchDialogFragment newInstance() {
        return new ProductSearchDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_product_search, container);

        Objects.requireNonNull(getDialog()).setTitle(R.string.txt_product_service);
        Objects.requireNonNull(getDialog()).setCancelable(true);

        adapter = new ProductAdapter(null, this);

        RecyclerView lstProductsAdapter = view.findViewById(R.id.lstProductsAdapter);
        lstProductsAdapter.setAdapter(adapter);
        lstProductsAdapter.setLayoutManager(new LinearLayoutManager(getContext()));
        lstProductsAdapter.setHasFixedSize(true);

        ProgressBar progressBar = view.findViewById(R.id.pbProductSearchAdapter);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        SearchView edtSearchProductAdapter = view.findViewById(R.id.edtSearchProductAdapter);
        edtSearchProductAdapter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                ProductSearchDialogFragment.this.adapter.getFilter().filter(newText);
                return false;
            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            ProductDAO productDAO = new ProductDAO(inflater.getContext());

            productDAO.open();
            List<Product> listProducts = productDAO.getProducts();
            productDAO.close();

            handler.post(() -> {
                adapter.setListProduct(listProducts);
                progressBar.setVisibility(ProgressBar.GONE);
            });

        });

        return view;

    }

    public void openDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(DIALOG_TAG) == null) {
            show(fm, DIALOG_TAG);
        }
    }

    @Override
    public void onCustomClick(Object object) {
        SearchProductDialogFragmentListener activity = (SearchProductDialogFragmentListener) getActivity();
        if (activity != null) {
            Product product = (Product) object;
            activity.onFinishEditProductDialog(product.getId(), product.getDescription(), product.getValor(), product.getUnity());
        }
        dismiss();
    }

    @Override
    public void onCustomLongClick(Object object) {

    }

}
