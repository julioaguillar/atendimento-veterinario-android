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
import br.com.aonsistemas.appvet.model.Client;
import br.com.aonsistemas.appvet.util.Text;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientAdapterViewHolder>
        implements Filterable {

    private final ClickRecyclerViewInterface clickRecyclerViewInterface;
    private List<Client> clients;
    private List<Client> clientsFilter;

    public ClientAdapter(List<Client> clients, ClickRecyclerViewInterface clickRecyclerViewInterface) {
        this.clients = clients;
        this.clientsFilter = clients;
        this.clickRecyclerViewInterface = clickRecyclerViewInterface;
    }

    @NonNull
    @Override
    public ClientAdapter.ClientAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_client, parent, false);
        return new ClientAdapter.ClientAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientAdapter.ClientAdapterViewHolder holder, int position) {

        Client client = clients.get(position);

        holder.txtNameClientAdapter.setText(client.getName() != null ? client.getName() : "");
        holder.txtAddressClientAdapter.setText(client.getAddress() != null ? client.getAddress() : "");
        holder.txtTelephoneClientAdapter.setText(client.getTelephone() != null ? client.getTelephone() : "");
        holder.txtEmailClientAdapter.setText(client.getEmail() != null ? client.getEmail() : "");

        if (position % 2 == 0)
            holder.layoutAdapterClient.setBackgroundResource(R.color.grid_green);

    }

    @Override
    public int getItemCount() {

        if ( clients != null )
            return clients.size();
        else
            return 0;

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListClient(List<Client> clients) {
        this.clients = clients;
        this.clientsFilter = clients;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if ((constraint != null) && (constraint.length() > 0)) {
                    List<Client> clientsAux = new ArrayList<>();
                    for (Client client : clientsFilter) {
                        if (Text.comparar(client.getName(), constraint.toString())) {
                            clientsAux.add(client);
                        }
                    }
                    filterResults.values = clientsAux;
                    filterResults.count = clientsAux.size();
                } else {
                    filterResults.values = clientsFilter;
                    filterResults.count = clientsFilter.size();
                }
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clients = (ArrayList<Client>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    protected class ClientAdapterViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout layoutAdapterClient;
        protected TextView txtNameClientAdapter, txtAddressClientAdapter, txtTelephoneClientAdapter, txtEmailClientAdapter;

        public ClientAdapterViewHolder(final View itemView) {
            super(itemView);

            layoutAdapterClient = itemView.findViewById(R.id.layoutAdapterClient);

            txtNameClientAdapter = itemView.findViewById(R.id.txtNameClientAdapter);
            txtAddressClientAdapter = itemView.findViewById(R.id.txtAddressClientAdapter);
            txtTelephoneClientAdapter = itemView.findViewById(R.id.txtTelephoneClientAdapter);
            txtEmailClientAdapter = itemView.findViewById(R.id.txtEmailClientAdapter);

            itemView.setOnClickListener(v -> clickRecyclerViewInterface.onCustomClick(clients.get(getLayoutPosition())));

            itemView.setOnLongClickListener(v -> {
                clickRecyclerViewInterface.onCustomLongClick(clients.get(getLayoutPosition()));
                return false;
            });

        }
    }

}