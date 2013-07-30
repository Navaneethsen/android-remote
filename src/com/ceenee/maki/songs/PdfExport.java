package com.ceenee.maki.songs;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Element;

import com.ceenee.maki.MyLog;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Header;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPage;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.ceenee.maki.songs.Export.ExportDriver;
import com.ceenee.q.R;

public class PdfExport implements ExportDriver {
	/**
	 * Load song book task.
	 * We should return some progess here
	 * @author kureikain
	 *
	 */
	String TAG = "PDF_EXPORT";
	protected Activity activity;
	protected SongBook songbook;
	protected final int SONG_PER_PAGE = 37;
	protected Document doc;
	protected Font paraFont, headerFont;
	
	public void setActivity(Activity a) {
		activity = a;
	}
	public boolean execute(String source, String target) throws Exception {
		throw new Exception("Not implemeted yet");
	}
	
	public PdfExport() {
		
	}
	
	void prepareFont() throws IOException, DocumentException {
	    InputStream inputStream = activity.getResources().openRawResource(R.raw.arial);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        BaseFont arial = BaseFont.createFont("arial.ttf", BaseFont.IDENTITY_H, true, false, buffer, null);
        paraFont = new Font(arial, 12);
        
        inputStream = activity.getResources().openRawResource(R.raw.archisticosimple);
        buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        BaseFont archistico = BaseFont.createFont("archisticosimple.ttf", BaseFont.IDENTITY_H, true, false, buffer, null);
        headerFont = new Font(archistico, 12);
	}
	
	@Override
	public boolean execute(SongBook _songbook, String target)  {
		songbook = _songbook;
		try {
			doc = new Document(PageSize.A4,10.0f,10.0f,10.0f,10.0f);
	        FileOutputStream fOut = new FileOutputStream(target);
        	PdfWriter.getInstance(doc, fOut);
            doc.open();
            this.prepareFont();
            
            Integer pageQuantity = (songbook.getSize() -  songbook.getSize() % SONG_PER_PAGE) / SONG_PER_PAGE;
            if ( songbook.getSize() % SONG_PER_PAGE > 0) {
            	pageQuantity++;
            }
            		
            String initChar;
            for (int pageIndex=0; pageIndex<pageQuantity; pageIndex++) {
            	try {
            		initChar = songbook.songs.get(pageIndex * SONG_PER_PAGE+1).getTitle();
            		initChar = initChar.substring(0, 1);
            		if (initChar.matches("[0-9]")) {
            			initChar = "0-9";
            		}
            		this.printHeader(pageIndex, initChar, "CeeNee Karaoke Book");
                	this.printPageContent(pageIndex);
                    this.printPageFooter(pageIndex);	
            	} catch (Exception e) {
            		//continue and ignore error, however. try to log it
            		e.printStackTrace();
            		MyLog.e("PDF_EXPORT", e.getStackTrace().toString());
            		continue;
            	}
            	doc.newPage();
            }
            
         } catch (DocumentException de) {
        	 de.printStackTrace();
             MyLog.e(TAG, "DocumentException:" + de);
             return false;
         } catch (IOException e) {
        	 e.printStackTrace();
             MyLog.e(TAG, "ioException:" + e);
             return false;
         } catch (Exception e) {
        	 e.printStackTrace();
             MyLog.e(TAG, "ioException:" + e);
             return false;
         }
         finally
         {
             doc.close();
         }
         return true;
	}
	
	/**
	 * Only print out header
	 * @param initChar
	 * @param title
	 * @return true if succesfully to write header
	 */
	protected boolean printHeader(int pageIndex, String initChar, String title) {
		PdfPTable header = new PdfPTable(3);
        try {
			header.setWidthPercentage(new float[]{50, 375, 100}, PageSize.A4);
		} catch (DocumentException e1) {
			e1.printStackTrace();
			return false;
		} 
        PdfPCell c1 = new PdfPCell(new Phrase(initChar, headerFont));
        
        c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        header.addCell(c1);
        
        c1 = new PdfPCell(new Phrase("CeeNee Karaoke Songbook", headerFont));
        c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        header.addCell(c1);
        
        c1 = new PdfPCell();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.pdf_icon);
        bitmap.compress(Bitmap.CompressFormat.PNG /* FileType */,
                                100 /* Ratio */, stream);
        try {
        	Image png = Image.getInstance(stream.toByteArray());
        	c1.addElement(png);
            header.addCell(c1);
            doc.add(header);
        } catch (Exception e) {
        	return false;
        }
        return true;
	}
	
	/**
	 * Printing page content 
	 * 
	 * @param page
	 * @return true if writing to document successfully
	 * 
	 */
	protected boolean printPageContent(int page)  {
		PdfPTable table;
    	
		table = new PdfPTable(2);
        try {
        	table.setWidthPercentage(new float[]{50,475}, PageSize.A4);
            for (Integer index=page * SONG_PER_PAGE; index<(page+1)*SONG_PER_PAGE; index++) {
            	table.setWidthPercentage(new float[]{50,475}, PageSize.A4);
                Song song = songbook.songs.get(index);
            	PdfPCell c = new PdfPCell(new Phrase(song.getId(), paraFont));
                c.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                table.addCell(c);
                
                c = new PdfPCell(new Phrase(song.getTitle(), paraFont));
                c.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                table.addCell(c);
            }
            doc.add(table);	        	
        } catch (Exception e) {
        	return false;
        }
        return true;
	}
	
	/**
	 * Print header content.
	 * @return true if successfully to write footer
	 */
	protected boolean printPageFooter(int pageIndex) throws Exception {
		PdfPTable table;
    	PdfPCell c1;
		
		table = new PdfPTable(2);
        table.setWidthPercentage(new float[]{50,475}, PageSize.A4);
        c1 = new PdfPCell(new Phrase("Page" + pageIndex));
        c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        table.addCell(c1);
        
        c1 = new PdfPCell(new Phrase("http://ceenee.com"));
        c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        table.addCell(c1);
        
        doc.add(table);
        return true;
	}
	
}
