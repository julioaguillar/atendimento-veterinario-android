package br.com.aonsistemas.appvet.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.aonsistemas.appvet.ClickRecyclerViewInterface;
import br.com.aonsistemas.appvet.R;
import br.com.aonsistemas.appvet.model.Product;
import br.com.aonsistemas.appvet.util.FormatValue;
import br.com.aonsistemas.appvet.util.Text;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder>
        implements Filterable {

    private final ClickRecyclerViewInterface clickRecyclerViewInterface;
    private List<Product> products;
    private List<Product> productsFilter;

    public ProductAdapter(List<Product> products, ClickRecyclerViewInterface clickRecyclerViewInterface) {
        this.products = products;
        this.productsFilter = products;
        this.clickRecyclerViewInterface = clickRecyclerViewInterface;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product, parent, false);
        return new ProductAdapter.ProductAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductAdapterViewHolder holder, int position) {

        Product product = products.get(position);

        holder.txtDescriptionProductAdapter.setText(product.getDescription() != null ? product.getDescription() : "");
        holder.txtUnityProductAdapter.setText(product.getUnity() != null ? product.getUnity() : "");
        holder.txtValueProductAdapter.setText(FormatValue.value_2_decimal(product.getValor()));

        String obs = product.getNote() != null ? product.getNote() : "";
        holder.txtNoteProductAdapter.setText(obs);

        int visibility = obs.trim().equals("") ? TextView.GONE : TextView.VISIBLE;
        holder.txtDescNoteProductAdapter.setVisibility(visibility);
        holder.txtNoteProductAdapter.setVisibility(visibility);

        if (position % 2 == 0)
            holder.layoutAdapterProducts.setBackgroundResource(R.color.grid_green);

    }

    @Override
    public int getItemCount() {

        if (products != null)
            return products.size();
        else
            return 0;

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListProduct(List<Product> products) {
        this.products = products;
        this.productsFilter = products;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if ((constraint != null) && (constraint.length() > 0)) {
                    List<Product> productsAux = new ArrayList<>();
                    for (Product product : productsFilter) {
                        if (Text.comparar(product.getDescription(), constraint.toString())) {
                            productsAux.add(product);
                        }
                    }
                    filterResults.values = productsAux;
                    filterResults.count = productsAux.size();
                } else {
                    filterResults.values = productsFilter;
                    filterResults.count = productsFilter.size();
                }
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                products = (ArrayList<Product>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    protected class ProductAdapterViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout layoutAdapterProducts;
        protected TextView txtDescriptionProductAdapter, txtUnityProductAdapter,
                txtValueProductAdapter, txtNoteProductAdapter, txtDescNoteProductAdapter;

        public ProductAdapterViewHolder(final View itemView) {
            super(itemView);

            layoutAdapterProducts = itemView.findViewById(R.id.layoutAdapterProducts);

            txtDescriptionProductAdapter = itemView.findViewById(R.id.txtDescriptionProductAdapter);
            txtUnityProductAdapter = itemView.findViewById(R.id.txtUnityProductAdapter);
            txtValueProductAdapter = itemView.findViewById(R.id.txtValueProductAdapter);
            txtNoteProductAdapter = itemView.findViewById(R.id.txtNoteProductAdapter);
            txtDescNoteProductAdapter = itemView.findViewById(R.id.txtDescNoteProductAdapter);

            itemView.setOnClickListener(v -> clickRecyclerViewInterface.onCustomClick(products.get(getLayoutPosition())));

            itemView.setOnLongClickListener(v -> {
                clickRecyclerViewInterface.onCustomLongClick(products.get(getLayoutPosition()));
                return false;
            });

        }
    }

}