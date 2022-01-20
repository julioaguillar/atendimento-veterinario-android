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
import br.com.aonsistemas.appvet.model.Animal;
import br.com.aonsistemas.appvet.util.Text;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalAdapterViewHolder>
        implements Filterable {

    private final ClickRecyclerViewInterface clickRecyclerViewInterface;
    private List<Animal> animals;
    private List<Animal> animalsFilter;

    public AnimalAdapter(List<Animal> animals, ClickRecyclerViewInterface clickRecyclerViewInterface) {
        this.animals = animals;
        this.animalsFilter = animals;
        this.clickRecyclerViewInterface = clickRecyclerViewInterface;
    }

    @NonNull
    @Override
    public AnimalAdapter.AnimalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_animal, parent, false);
        return new AnimalAdapter.AnimalAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalAdapter.AnimalAdapterViewHolder holder, int position) {

        Animal animal = animals.get(position);

        holder.txtNameAnimalAdapter.setText(animal.getName() != null ? animal.getName() : "");
        holder.txtNameClientAnimalAdapter.setText(animal.getClient().getName() != null ? animal.getClient().getName() : "");

        if (position % 2 == 0)
           holder.layoutAdapterAnimal.setBackgroundResource(R.color.grid_green);

    }

    @Override
    public int getItemCount() {

        if (animals != null)
            return animals.size();
        else
            return 0;

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListAnimal(List<Animal> animals) {
        this.animals = animals;
        this.animalsFilter = animals;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if ((constraint != null) && (constraint.length() > 0)) {
                    List<Animal> animalAux = new ArrayList<>();
                    for (Animal animal : animalsFilter) {
                        if ( ( Text.comparar(animal.getName(), constraint.toString()) ) ||
                                ( Text.comparar(animal.getClient().getName(), constraint.toString()) ) ) {
                            animalAux.add(animal);
                        }
                    }
                    filterResults.values = animalAux;
                    filterResults.count = animalAux.size();
                } else {
                    filterResults.values = animalsFilter;
                    filterResults.count = animalsFilter.size();
                }

                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                animals = (ArrayList<Animal>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    protected class AnimalAdapterViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout layoutAdapterAnimal;
        protected TextView txtNameAnimalAdapter, txtNameClientAnimalAdapter;

        public AnimalAdapterViewHolder(final View itemView) {
            super(itemView);

            layoutAdapterAnimal = itemView.findViewById(R.id.layoutAdapterAnimal);

            txtNameAnimalAdapter = itemView.findViewById(R.id.txtNameAnimalAdapter);
            txtNameClientAnimalAdapter = itemView.findViewById(R.id.txtNameClientAnimalAdapter);

            itemView.setOnClickListener(v -> clickRecyclerViewInterface.onCustomClick(animals.get(getLayoutPosition())));

            itemView.setOnLongClickListener(v -> {
                clickRecyclerViewInterface.onCustomLongClick(animals.get(getLayoutPosition()));
                return false;
            });

        }
    }

}