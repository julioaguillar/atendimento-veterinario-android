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
import br.com.aonsistemas.appvet.model.CustomerService;
import br.com.aonsistemas.appvet.util.DateTime;
import br.com.aonsistemas.appvet.util.FormatValue;
import br.com.aonsistemas.appvet.util.Text;

public class CustomerServiceAdapter extends RecyclerView.Adapter<CustomerServiceAdapter.CustomerServiceAdapterViewHolder>
        implements Filterable {

    private final ClickRecyclerViewInterface clickRecyclerViewInterface;
    private List<CustomerService> customerServices;
    private List<CustomerService> customerServicesFilter;

    public CustomerServiceAdapter(List<CustomerService> customerServices, ClickRecyclerViewInterface clickRecyclerViewInterface) {
        this.customerServices = customerServices;
        this.customerServicesFilter = customerServices;
        this.clickRecyclerViewInterface = clickRecyclerViewInterface;
    }

    @NonNull
    @Override
    public CustomerServiceAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_customer_service, parent, false);
        return new CustomerServiceAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerServiceAdapterViewHolder holder, int position) {

        CustomerService customerService = customerServices.get(position);

        holder.txtDateCustomerServiceAdapter.setText(customerService.getData() != null ? DateTime.formatDate(customerService.getData()) : "");
        holder.txtValueCustomerServiceAdapter.setText(FormatValue.value_2_decimal(customerService.getTotal()));
        holder.txtClientCustomerServiceAdapter.setText(customerService.getClient().getName() != null ? customerService.getClient().getName() : "");
        holder.txtAnimalCustomerServiceAdapter.setText(customerService.getAnimal().getName() != null ? customerService.getAnimal().getName() : "");

        String obs = customerService.getNote() != null ? customerService.getNote() : "";
        holder.txtNoteCustomerServiceAdapter.setText(obs);

        int visibility = obs.trim().equals("") ? TextView.GONE : TextView.VISIBLE;
        holder.txtNoteCustomerServiceAdapter.setVisibility(visibility);
        holder.txtDescNoteCustomerServiceAdapter.setVisibility(visibility);

        if (position % 2 == 0)
            holder.layoutAdapterCustomerServices.setBackgroundResource(R.color.grid_green);

    }

    @Override
    public int getItemCount() {

        if (customerServices != null)
            return customerServices.size();
        else
            return 0;

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListCustomerService(List<CustomerService> customerServices) {
        this.customerServices = customerServices;
        this.customerServicesFilter = customerServices;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if ((constraint != null) && (constraint.length() > 0)) {
                    List<CustomerService> customerServiceAux = new ArrayList<>();
                    for (CustomerService customerService : customerServicesFilter) {
                        if (
                                ( Text.comparar(DateTime.formatDate(customerService.getData()), constraint.toString()) ) ||
                                ( Text.comparar(FormatValue.value_2_decimal(customerService.getTotal()), constraint.toString()) ) ||
                                ( Text.comparar(customerService.getClient().getName(), constraint.toString()) ) ||
                                ( Text.comparar(customerService.getAnimal().getName(), constraint.toString()) )
                        ) {
                            customerServiceAux.add(customerService);
                        }
                    }
                    filterResults.values = customerServiceAux;
                    filterResults.count = customerServiceAux.size();
                } else {
                    filterResults.values = customerServicesFilter;
                    filterResults.count = customerServicesFilter.size();
                }
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                customerServices = (ArrayList<CustomerService>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    protected class CustomerServiceAdapterViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout layoutAdapterCustomerServices;
        protected TextView txtDateCustomerServiceAdapter, txtValueCustomerServiceAdapter,
                txtClientCustomerServiceAdapter, txtAnimalCustomerServiceAdapter,
                txtNoteCustomerServiceAdapter, txtDescNoteCustomerServiceAdapter;

        public CustomerServiceAdapterViewHolder(final View itemView) {
            super(itemView);

            layoutAdapterCustomerServices = itemView.findViewById(R.id.layoutCustomerServiceAdapter);

            txtDateCustomerServiceAdapter = itemView.findViewById(R.id.txtDateCustomerServiceAdapter);
            txtValueCustomerServiceAdapter = itemView.findViewById(R.id.txtValueCustomerServiceAdapter);
            txtClientCustomerServiceAdapter = itemView.findViewById(R.id.txtClientCustomerServiceAdapter);
            txtAnimalCustomerServiceAdapter = itemView.findViewById(R.id.txtAnimalCustomerServiceAdapter);
            txtNoteCustomerServiceAdapter = itemView.findViewById(R.id.txtNoteCustomerServiceAdapter);
            txtDescNoteCustomerServiceAdapter = itemView.findViewById(R.id.txtDescNoteCustomerServiceAdapter);

            itemView.setOnClickListener(v -> clickRecyclerViewInterface.onCustomClick(customerServices.get(getLayoutPosition())));

            itemView.setOnLongClickListener(v -> {
                clickRecyclerViewInterface.onCustomLongClick(customerServices.get(getLayoutPosition()));
                return false;
            });

        }
    }

}
