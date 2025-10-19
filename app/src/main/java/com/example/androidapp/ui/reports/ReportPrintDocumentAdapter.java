package com.example.androidapp.ui.reports;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportPrintDocumentAdapter extends PrintDocumentAdapter {

    private Context context;
    private List<ReportItem> reportItems;
    private String reportTitle;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;

    public ReportPrintDocumentAdapter(Context context, List<ReportItem> reportItems, String reportTitle) {
        this.context = context;
        this.reportItems = reportItems;
        this.reportTitle = reportTitle;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd", new Locale("ar", "SA"));
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                        CancellationSignal cancellationSignal, LayoutResultCallback callback,
                        Bundle extras) {
        
        // Create a new PdfDocument with the new attributes
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                .Builder(reportTitle + ".pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1);

        PrintDocumentInfo info = builder.build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                       CancellationSignal cancellationSignal, WriteResultCallback callback) {
        
        // Create a new PdfDocument
        PdfDocument document = new PdfDocument();
        
        try {
            // Create a page description
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            
            // Start a page
            PdfDocument.Page page = document.startPage(pageInfo);
            
            // Draw content on the page
            Canvas canvas = page.getCanvas();
            drawReportContent(canvas);
            
            // Finish the page
            document.finishPage(page);
            
            // Write the document content
            FileOutputStream fos = new FileOutputStream(destination.getFileDescriptor());
            document.writeTo(fos);
            
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
        } finally {
            document.close();
        }
    }

    private void drawReportContent(Canvas canvas) {
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(24);
        titlePaint.setFakeBoldText(true);
        
        Paint headerPaint = new Paint();
        headerPaint.setTextSize(16);
        headerPaint.setFakeBoldText(true);
        
        Paint bodyPaint = new Paint();
        bodyPaint.setTextSize(12);
        
        int y = 50;
        
        // Draw title
        canvas.drawText(reportTitle, 50, y, titlePaint);
        y += 40;
        
        // Draw date
        canvas.drawText("تاريخ التوليد: " + dateFormatter.format(new Date()), 50, y, bodyPaint);
        y += 30;
        
        // Draw headers
        canvas.drawText("التاريخ", 50, y, headerPaint);
        canvas.drawText("العميل", 150, y, headerPaint);
        canvas.drawText("المبلغ", 300, y, headerPaint);
        canvas.drawText("الحالة", 450, y, headerPaint);
        y += 25;
        
        // Draw line
        canvas.drawLine(50, y, 545, y, headerPaint);
        y += 15;
        
        // Draw data
        for (ReportItem item : reportItems) {
            if (y > 800) break; // Prevent overflow
            
            canvas.drawText(dateFormatter.format(item.getDate()), 50, y, bodyPaint);
            canvas.drawText(item.getCustomer(), 150, y, bodyPaint);
            canvas.drawText(currencyFormatter.format(item.getAmount()), 300, y, bodyPaint);
            canvas.drawText(item.getStatus(), 450, y, bodyPaint);
            y += 20;
        }
        
        // Draw summary
        y += 20;
        canvas.drawLine(50, y, 545, y, headerPaint);
        y += 20;
        
        double totalAmount = reportItems.stream().mapToDouble(ReportItem::getAmount).sum();
        canvas.drawText("إجمالي السجلات: " + reportItems.size(), 50, y, headerPaint);
        y += 20;
        canvas.drawText("إجمالي المبلغ: " + currencyFormatter.format(totalAmount), 50, y, headerPaint);
    }
}