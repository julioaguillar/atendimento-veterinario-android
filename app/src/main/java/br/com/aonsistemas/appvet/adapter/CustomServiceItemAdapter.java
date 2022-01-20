package br.com.aonsistemas.appvet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import br.com.aonsistemas.appvet.R;
import br.com.aonsistemas.appvet.model.CustomerServiceItem;
import br.com.aonsistemas.appvet.util.FormatValue;

public class CustomServiceItemAdapter extends ArrayAdapter<CustomerServiceItem>  {

    private final LayoutInflater inflater;
    private final int resource;

    private List<CustomerServiceItem> items;

    public CustomServiceItemAdapter(@NonNull Context context, int resource, List<CustomerServiceItem> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.items = objects;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = inflater.inflate(resource, parent, false);

        CustomerServiceItem model = getItem(position);

        TextView txtProductCustomerServiceItemAdapter = convertView.findViewById(R.id.txtProductCustomerServiceItemAdapter);
        txtProductCustomerServiceItemAdapter.setText(model != null ? model.getProduct().getDescription() : "");

        TextView txtValueCustomerServiceItemAdapter = convertView.findViewById(R.id.txtValueCustomerServiceItemAdapter);
        txtValueCustomerServiceItemAdapter.setText(FormatValue.value_2_decimal(model != null ? model.getValue() : 0.0));

        TextView txtAmountCustomerServiceItemAdapter = convertView.findViewById(R.id.txtAmountCustomerServiceItemAdapter);
        txtAmountCustomerServiceItemAdapter.setText(FormatValue.value_3_decimal(model != null ? model.getAmount() : 0.0));

        TextView txtTotalCustomerServiceItemAdapter = convertView.findViewById(R.id.txtTotalCustomerServiceItemAdapter);
        txtTotalCustomerServiceItemAdapter.setText(FormatValue.value_2_decimal(model != null ? model.getTotalValue() : 0.0));

        convertView.setTag(model);

        if (position % 2 == 0)
            convertView.setBackgroundResource(R.color.grid_green);

        return convertView;

    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Nullable
    @Override
    public CustomerServiceItem getItem(int position) {
        return this.items.get(position);
    }

    public void setListItems(List<CustomerServiceItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }
    
}
