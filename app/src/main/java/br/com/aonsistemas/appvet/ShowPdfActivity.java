package br.com.aonsistemas.appvet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.aonsistemas.appvet.dao.ClientDAO;
import br.com.aonsistemas.appvet.model.Client;
import br.com.aonsistemas.appvet.util.Constants;
import br.com.aonsistemas.appvet.util.Message;

public class ShowPdfActivity extends AppCompatActivity {

    private LinearLayout layoutPdf;
    private View divider1;

    private ProgressBar progressBarPdf;

    private ImageView imageView;
    private View separation;

    private int client_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdf);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.show_pdf);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        layoutPdf = findViewById(R.id.layoutPdf);
        divider1 = findViewById(R.id.divider0);
        progressBarPdf = findViewById(R.id.progressBarPdf);

        try {

            Intent intent = getIntent();
            Bundle params = intent.getExtras();

            this.client_id = params.getInt("client_id");

        } catch (Exception ignored) { }

        String PATH_LOCAL_PDF = getExternalFilesDir(null).getAbsolutePath() + "/pdf";
        final File pdf = new File(PATH_LOCAL_PDF, Constants.NOME_FILE_PDF);

        if ( pdf.exists() ) {
            showPdf(pdf);
        } else {
            Message.showMessage(this, R.string.txt_error_load_pdf);
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                sharePdf();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share, menu);
        return true;
    }

    private void sharePdf() {

        String PATH_LOCAL_PDF = getExternalFilesDir(null).getAbsolutePath() + "/pdf";
        final File pdf = new File(PATH_LOCAL_PDF, Constants.NOME_FILE_PDF);

        if ( pdf.exists() ) {

            try {

                Uri uri = FileProvider.getUriForFile(
                        ShowPdfActivity.this,
                        getString(R.string.file_provider),
                        pdf);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_print));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.txt_attachment));

                if ( this.client_id > 0 ) {

                    ClientDAO clientDAO = new ClientDAO(ShowPdfActivity.this);

                    clientDAO.open();
                    Client client = clientDAO.getClient(this.client_id);
                    clientDAO.close();

                    if ( !client.getEmail().trim().equals("") )
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{client.getEmail().trim()});

                }

                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("application/pdf");

                try {
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_share)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ShowPdfActivity.this, R.string.txt_error_read_pdf, Toast.LENGTH_LONG).show();
                }

            } catch (IllegalArgumentException e) {
                Toast.makeText(ShowPdfActivity.this, R.string.txt_share_pdf, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(ShowPdfActivity.this, R.string.txt_error_read_pdf, Toast.LENGTH_LONG).show();
            }

        } else
            Message.showMessage(this, R.string.txt_not_found_pdf);

    }

    private void showPdf(File file) {

        if (file == null || !file.exists() ) {
            Toast.makeText(ShowPdfActivity.this, R.string.txt_error_read_pdf, Toast.LENGTH_LONG).show();
            return;
        }

        progressBarPdf.setVisibility(ProgressBar.VISIBLE);

        List<RendererPdf> list = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            try {

                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

                int count = 1;

                Bitmap bitmap;

                final int pageCount = renderer.getPageCount();

                for (int i = 0; i < pageCount; i++) {

                    PdfRenderer.Page page = renderer.openPage(i);

                    int width = page.getWidth();
                    int height = page.getHeight();

                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    imageView = new ImageView(ShowPdfActivity.this);
                    imageView.setId(count);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setImageBitmap(bitmap);

                    separation = new View(ShowPdfActivity.this);
                    separation.setId(count+1);
                    separation.setBackgroundResource(R.color.dark_grey);
                    separation.setLayoutParams(divider1.getLayoutParams());

                    list.add(new RendererPdf(imageView, separation));

                    count += 2;

                    page.close();

                }

                renderer.close();

            } catch (Exception ignored) { }

            handler.post(() -> {

                for (RendererPdf view : list) {
                    layoutPdf.addView(view.getImageView());
                    layoutPdf.addView(view.getView());
                }

                progressBarPdf.setVisibility(ProgressBar.GONE);

            });
        });

    }

}