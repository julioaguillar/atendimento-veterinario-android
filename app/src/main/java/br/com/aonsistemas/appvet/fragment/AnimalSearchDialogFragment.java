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
import br.com.aonsistemas.appvet.adapter.AnimalAdapter;
import br.com.aonsistemas.appvet.dao.AnimalDAO;
import br.com.aonsistemas.appvet.model.Animal;

public class AnimalSearchDialogFragment extends DialogFragment
        implements ClickRecyclerViewInterface {

    private static final String DIALOG_TAG = "DialogAnimal";

    private AnimalAdapter adapter;

    private int client_id = 0;
    private boolean showOnly = false;

    public interface AnimalSearchDialogFragmentListener {
        void onFinishEditAnimalDialog(int id, int client_id, String name, String client_name);
    }

    public static AnimalSearchDialogFragment newInstance() {
        return new AnimalSearchDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_animal_search, container);

        if ( showOnly )
            Objects.requireNonNull(getDialog()).setTitle(R.string.txt_animals_client);
        else
            Objects.requireNonNull(getDialog()).setTitle(R.string.txt_sel_animals);

        Objects.requireNonNull(getDialog()).setCancelable(true);

        adapter = new AnimalAdapter(null, this);

        RecyclerView lstAnimalsAdapter = view.findViewById(R.id.lstAnimalsAdapter);
        lstAnimalsAdapter.setAdapter(adapter);
        lstAnimalsAdapter.setLayoutManager(new LinearLayoutManager(getContext()));
        lstAnimalsAdapter.setHasFixedSize(true);

        ProgressBar progressBar = view.findViewById(R.id.pbAnimalSearchAdapter);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        SearchView svAnimalSearchAdapter = view.findViewById(R.id.svAnimalSearchAdapter);
        svAnimalSearchAdapter.setVisibility(showOnly ? SearchView.GONE : SearchView.VISIBLE);
        svAnimalSearchAdapter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                AnimalSearchDialogFragment.this.adapter.getFilter().filter(newText);
                return false;
            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            AnimalDAO animalDAO = new AnimalDAO(inflater.getContext());
            animalDAO.open();

            List<Animal> listAnimals;

            if (client_id > 0)
                listAnimals = animalDAO.getAnimals(client_id);
            else
                listAnimals = animalDAO.getAnimals();

            animalDAO.close();

            handler.post(() -> {
                adapter.setListAnimal(listAnimals);
                progressBar.setVisibility(ProgressBar.GONE);
            });

        });

        return view;

    }

    public void openDialog(FragmentManager fm, int client_id) {
        this.client_id = client_id;
        if (fm.findFragmentByTag(DIALOG_TAG) == null) {
            show(fm, DIALOG_TAG);
        }
    }

    public void openDialog(FragmentManager fm, int client_id, boolean showOnly) {
        this.showOnly = showOnly;
        openDialog(fm, client_id);
    }

    @Override
    public void onCustomClick(Object object) {
        AnimalSearchDialogFragmentListener activity = (AnimalSearchDialogFragmentListener) getActivity();
        if (activity != null) {
            Animal animal = (Animal) object;
            activity.onFinishEditAnimalDialog(animal.getId(),animal.getClient_id(), animal.getName(), animal.getClient().getName());
        }
        dismiss();
    }

    @Override
    public void onCustomLongClick(Object object) {

    }

}
