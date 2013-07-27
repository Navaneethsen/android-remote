package com.ceenee.maki.songs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.ceenee.maki.MyLog;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ceenee.maki.songs.Export.ExportDriver;
import android.content.Context;

public class PdfExport implements ExportDriver {
	/**
	 * Load song book task.
	 * We should return some progess here
	 * @author kureikain
	 *
	 */
	String TAG = "PDF_EXPORT";
	
	@Override
	public boolean execute(String source, String target) {
		String[] part = source.split("\\.");
        String filename = part[0];
        MyLog.i(TAG, "filename: " + filename);
        
    	com.lowagie.text.Document doc = new com.lowagie.text.Document(PageSize.A4,10.0f,10.0f,10.0f,10.0f);
         try {
        	FileOutputStream fOut = new FileOutputStream(target);
            PdfWriter.getInstance(doc, fOut);
            doc.open();
            
            Paragraph title = new Paragraph("Karaoke Song Book \n\n");
            Font titleFont= new Font(Font.TIMES_ROMAN,20,Font.BOLD,harmony.java.awt.Color.RED);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setFont(titleFont);
            doc.add(title);
            
            PdfPTable table = new PdfPTable(2);
            
            table.setWidthPercentage(new float[]{50,475}, PageSize.A4);
            PdfPCell c1 = new PdfPCell(new Phrase("Song Number"));
            c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(c1);
            
            c1 = new PdfPCell(new Phrase("Song Name"));
            c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(c1);
            
//            Integer quantity = songs.size();
//            for (Integer index=0; index<quantity; index++) {
//            	table.setWidthPercentage(new float[]{50,475}, PageSize.A4);
//	            PdfPCell c = new PdfPCell(new Phrase("Song Number"));
//	            c.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
//	            table.addCell(c);
//	            
//	            c = new PdfPCell(new Phrase("Song Name"));
//	            c.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
//	            table.addCell(c);
//            }
            
         } catch (DocumentException de) {
             MyLog.e(TAG, "DocumentException:" + de);
             return false;
         } catch (IOException e) {
             MyLog.e(TAG, "ioException:" + e);
             return false;
         }
         finally
         {
             doc.close();
         }
         return true;
	}
	
}
