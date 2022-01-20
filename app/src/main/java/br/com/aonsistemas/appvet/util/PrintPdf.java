package br.com.aonsistemas.appvet.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.print.PrintAttributes;

import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.aonsistemas.appvet.R;

public class PrintPdf {

    public static final float MARGIN_LEFT = 20F;
    public static final float MARGIN_RIGTH = 575.0F;

    private final Context context;

    private PdfDocument document;
    private PdfDocument.PageInfo pageInfo;
    private PdfDocument.Page page;
    private Canvas canvas;
    private Paint paint;

    private int currentPage = 1;

    // tamanho da pagina (A4)
    private final int pageHeight = PrintAttributes.MediaSize.ISO_A4.getHeightMils () * 72 / 1000;
    private final int pageWidth = PrintAttributes.MediaSize.ISO_A4.getWidthMils () * 72 / 1000;

    private final float textSize = 10F;
    private float lineSize = 12F;
    private final float rodapeSize = 40F;

    private float line = textSize;

    private boolean cabecalho;
    private boolean rodape;
    private File logo;
    private String cabecalhoLinha1;
    private String cabecalhoLinha2;
    private String cabecalhoLinha3;
    private String titulo;

    public PrintPdf(Context context) {
        this.context = context;
        this.cabecalho = false;
        this.rodape = false;
    }

    public void abrir() {

        this.document = new PdfDocument();

        this.pageInfo = new PdfDocument.PageInfo.Builder(this.pageWidth, this.pageHeight, this.currentPage).create();
        this.page = this.document.startPage(this.pageInfo);

        this.canvas = this.page.getCanvas();

        this.paint = new Paint();
        this.paint.setColor(Color.BLACK);
        this.paint.setTextSize(textSize);
        this.paint.setTypeface(ResourcesCompat.getFont(context, R.font.dejavu));

        if (this.cabecalho)
            impCabecalho();

        if (this.rodape)
            impRodape();

    }

    private void linhaHorizontal(float startX, float startY, float stopX, float stopY) {

        // Ajusta o tamnho da pagina quando têm rodape
        float tamanho_pagina = (float) this.pageHeight;
        if (this.rodape)
            tamanho_pagina -= this.rodapeSize - this.lineSize;
        // *******************************

        if (startY < tamanho_pagina && stopY < tamanho_pagina)
            this.canvas.drawLine(startX, startY, stopX, stopY, this.paint);

    }

    private void linhaHorizontal(float linha) {
        linhaHorizontal(MARGIN_LEFT, linha, MARGIN_RIGTH, linha);
    }

    public void linhaHorizontal() {
        linhaHorizontal(this.line);
    }

    public void linhaHorizontalM() {
        linhaHorizontal(MARGIN_LEFT+100F, this.line, MARGIN_RIGTH-100F, this.line);
    }

    public boolean novaLinha() {

        this.line += lineSize;

        // Ajusta o tamnho da pagina quando têm rodape
        float tamanho_pagina = (float) this.pageHeight;
        if (this.rodape)
            tamanho_pagina -= this.rodapeSize;
        // *******************************

        if ( this.line > tamanho_pagina ) {

            this.document.finishPage(this.page);

            this.currentPage++;

            this.pageInfo = new PdfDocument.PageInfo.Builder(this.pageWidth, this.pageHeight, this.currentPage).create();

            this.page = this.document.startPage(this.pageInfo);

            this.canvas = this.page.getCanvas();

            this.line = this.textSize;

            if (this.cabecalho)
                impCabecalho();

            if (this.rodape)
                impRodape();

            return true;

        }

        return false;

    }

    public void novaLinha(int count) {

        for (int i = 0; i < count; i++) {
            novaLinha();
        }

    }

    public void impCabecalho() {

        novaLinha();
        linhaHorizontal();
        this.line += 5.0F;

        if ( ( logo != null ) && ( logo.exists() ) ) {
            Bitmap bitmap = BitmapFactory.decodeFile(logo.getPath());
            //Bitmap resize = Bitmap.createScaledBitmap(bitmap, 210, 115, false);
            Bitmap resize = Bitmap.createScaledBitmap(bitmap, 190, 102, false);
            this.canvas.drawBitmap(resize, MARGIN_LEFT, line, this.paint);
        }

        novaLinha();
        this.line += 6.0F;
        this.paint.setTextSize(16F);

        escreverNegrito(this.cabecalhoLinha1, 225F, line, 35);
        novaLinha(2);

        escreverNegrito(this.cabecalhoLinha2, 225F, line, 35);
        novaLinha(2);

        this.paint.setTextSize(12F);
        escrever(this.cabecalhoLinha3, 225F, line, 49);
        novaLinha(2);
        this.line += 6.0F;

        this.paint.setTextSize(16F);
        escreverNegrito(this.titulo, 225F, line, 35);
        novaLinha();

        this.paint.setTextSize(textSize);

        linhaHorizontal();
        novaLinha();
    }

    public void impRodape() {
        float linha = ((float)this.pageHeight) - this.rodapeSize;
        linhaHorizontal(linha);

        String app = context.getString(R.string.app_name);
        escrever(app, MARGIN_LEFT, linha+20F, app.length());

        String pagina = "Pag. " + currentPage;
        escreverDireita(pagina, MARGIN_RIGTH, linha+20F, pagina.length());

    }

    public void escrever(String text, float coluna, float linha, int tamanhoTexto) {
        String t = text != null ? text : "";
        int count = t.length() > tamanhoTexto ? tamanhoTexto : t.length();
        this.canvas.drawText(t, 0, count, coluna, linha, this.paint);
    }

    public void escrever(String text, float coluna) {
        escrever(text, coluna, this.line, text.length());
    }

    public void escrever(String text, float coluna, int tamanhoTexto) {
        escrever(text, coluna, this.line, tamanhoTexto);
    }

    public void escreverDireita(String text, float coluna, float linha, int tamanhoTexto) {
        this.paint.setTextAlign(Paint.Align.RIGHT);
        escrever(text, coluna, linha, tamanhoTexto);
        this.paint.setTextAlign(Paint.Align.LEFT);
    }

    public void escreverDireita(String text, float coluna) {
        escreverDireita(text, coluna, this.line, text.length());
    }

    public void escreverNegrito(String text, float coluna, float linha, int tamanhoTexto) {
        this.paint.setTypeface(ResourcesCompat.getFont(context, R.font.dejavub));
        escrever(text, coluna, linha, tamanhoTexto);
        this.paint.setTypeface(ResourcesCompat.getFont(context, R.font.dejavu));
    }

    public void escreverNegrito(String text, float coluna) {
        escreverNegrito(text, coluna, this.line, text.length());
    }

    public void escreverNegritoDireita(String text, float coluna) {
        this.paint.setTextAlign(Paint.Align.RIGHT);
        escreverNegrito(text, coluna, this.line, text.length());
        this.paint.setTextAlign(Paint.Align.LEFT);
    }

    public void finalizarPagina() {
        this.document.finishPage(this.page);
    }

    public void salvar(File pdf) {

        try {
            this.document.writeTo(new FileOutputStream(pdf));
        } catch (IOException ignored) { }

        this.document.close();

    }

    public void setCabecalho(boolean cabecalho) {
        this.cabecalho = cabecalho;
    }

    public void setRodape(boolean rodape) {
        this.rodape = rodape;
    }

    public float getLine() {
        return line;
    }

    public void setLogo(File logo) {
        this.logo = logo;
    }

    public void setCabecalhoLinha1(String cabecalhoLinha1) {
        this.cabecalhoLinha1 = cabecalhoLinha1;
    }

    public void setCabecalhoLinha2(String cabecalhoLinha2) {
        this.cabecalhoLinha2 = cabecalhoLinha2;
    }

    public void setCabecalhoLinha3(String cabecalhoLinha3) {
        this.cabecalhoLinha3 = cabecalhoLinha3;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
