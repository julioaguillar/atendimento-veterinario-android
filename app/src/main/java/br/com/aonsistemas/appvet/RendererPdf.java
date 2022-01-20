package br.com.aonsistemas.appvet;

import android.view.View;
import android.widget.ImageView;

public class RendererPdf {

    private ImageView imageView;
    private View view;

    public RendererPdf(ImageView imageView, View view) {
        this.imageView = imageView;
        this.view = view;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public View getView() {
        return view;
    }
}
