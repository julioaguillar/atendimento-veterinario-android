package br.com.aonsistemas.appvet.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import br.com.aonsistemas.appvet.adapter.ClientAdapter;
import br.com.aonsistemas.appvet.dao.ClientDAO;
import br.com.aonsistemas.appvet.model.Client;

public class ClientSearchDialogFragment extends DialogFragment
    implements ClickRecyclerViewInterface {

    private static final String DIALOG_TAG = "DialogClient";

    private ClientAdapter adapter;

    public interface SearchClientDialogFragmentListener {
        void onFinishEditClientDialog(int id, String name);
    }

    public static ClientSearchDialogFragment newInstance() {
        return new ClientSearchDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_client_search, container, false);

        Objects.requireNonNull(getDialog()).setTitle(R.string.txt_sel_client);

        adapter = new ClientAdapter(null, this);

        RecyclerView lstClientsAdapter = view.findViewById(R.id.lstClientsAdapter);
        lstClientsAdapter.setAdapter(adapter);
        lstClientsAdapter.setLayoutManager(new LinearLayoutManager(getContext()));
        lstClientsAdapter.setHasFixedSize(true);

        ProgressBar progressBar = view.findViewById(R.id.pbClientSearchAdapter);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        SearchView svSearchClientAdapter = view.findViewById(R.id.svSearchClientAdapter);
        svSearchClientAdapter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                ClientSearchDialogFragment.this.adapter.getFilter().filter(newText);
                return false;
            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            ClientDAO clientDAO = new ClientDAO(inflater.getContext());

            clientDAO.open();
            List<Client> listClients = clientDAO.getClients();
            clientDAO.close();

            handler.post(() -> {
                adapter.setListClient(listClients);
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
        SearchClientDialogFragmentListener activity = (SearchClientDialogFragmentListener) getActivity();
        if (activity != null) {
            Client client = (Client) object;
            activity.onFinishEditClientDialog(client.getId(), client.getName());
        }
        dismiss();
    }

    @Override
    public void onCustomLongClick(Object object) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
