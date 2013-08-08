package com.ceenee.maki.songs;

import harmony.java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ceenee.maki.MyLog;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.ceenee.maki.songs.Export.ExportDriver;
import com.ceenee.q.hd.R;

public class PdfExport implements ExportDriver {
	/**
	 * Load song book task.rogress
	 * We should return some  here
	 * @author kureikain
	 *
	 */
	String TAG = "PDF_EXPORT";
	protected Activity activity;
	protected SongBook songbook;
	protected final int SONG_PER_PAGE = 37;
	protected Document doc;
	protected Font numFont, paraFont, headerFont;
	
	public class EmptySongException extends Exception {
		private static final long serialVersionUID = 1L;

		public EmptySongException(String s) {
			super(s);
		}
	}
	
	
	/**
	 * Set current activity which are calling this task.
	 * @param current activity 
	 */
	public void setActivity(Activity a) {
		activity = a;
	}
	
	/**
	 * Setter method for this{@link #songbook}
	 * 
	 * @param s
	 */
	public void setSongBook(SongBook s) {
		songbook = s;
	}
	
	/**
	 * Take the song book file name in XML format and render to PDF from there. 
	 * 
	 * @param path to xml song book
	 * @param path to where to save pdf file
	 */
	public boolean execute(String source, String target) throws Exception {
		SongBook s = new SongBook();
		songbook.load(source);
		if (songbook.getSize()>0) {
			return this.execute(s, target);	
		}
		throw new EmptySongException("Songbook is empty");
	}
	
	public PdfExport() {
		
	}
	
	void prepareFont() throws IOException, DocumentException {
	    InputStream inputStream = activity.getResources().openRawResource(R.raw.arial);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        BaseFont arial = BaseFont.createFont("arial.ttf", BaseFont.IDENTITY_H, true, false, buffer, null);
        paraFont = new Font(arial, 10);
        
        inputStream = activity.getResources().openRawResource(R.raw.helveticaneuebold);
        buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        BaseFont helvetica = BaseFont.createFont("helveticaneuebold.ttf", BaseFont.IDENTITY_H, true, false, buffer, null);
        numFont = new Font(helvetica, 10);
        
        inputStream = activity.getResources().openRawResource(R.raw.archisticosimple);
        buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        BaseFont archistico = BaseFont.createFont("archisticosimple.ttf", BaseFont.IDENTITY_H, true, false, buffer, null);
        headerFont = new Font(archistico, 25);
	}
	
	@Override
	public boolean execute(SongBook _songbook, String target)  {
		songbook = _songbook;
		try {
			doc = new Document(PageSize.A4, 10.0f,10.0f,10.0f,10.0f);
//	        FileOutputStream fOut = new FileOutputStream(target);
	        FileOutputStream fOut = activity.openFileOutput(target, android.content.Context.MODE_WORLD_READABLE);
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
            		this.printHeader(pageIndex, initChar, "Karaoke Song Book");
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
	 * @return true if successfully to write header
	 */
	protected boolean printHeader(int pageIndex, String initChar, String title) {
		PdfPTable header = new PdfPTable(3);
        try {
			header.setWidthPercentage(new float[]{75.0f, 350, 100}, PageSize.A4);
		} catch (DocumentException e1) {
			e1.printStackTrace();
			return false;
		} 
        header.setSpacingAfter(20.0f);
        PdfPCell c1 = new PdfPCell(new Phrase(initChar, headerFont));
        c1.setBorder(0);
        c1.setFixedHeight(72.0f);
        c1.setBackgroundColor(new Color(237, 247, 255));	
        c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        c1.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
        header.addCell(c1);
        
        c1 = new PdfPCell(new Phrase(title, headerFont));
        c1.setFixedHeight(70f);
        c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        c1.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
        c1.setBorder(0);
        c1.setBorderWidthRight(1);
        c1.setBorderColorRight(new Color(0, 0, 0));
        
        header.addCell(c1);
        
        c1 = new PdfPCell();
        c1.setFixedHeight(70f);
        c1.setBorder(0);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.pdf_icon);
        bitmap.compress(Bitmap.CompressFormat.PNG /* FileType */,
                                100 /* Ratio */, stream);
        try {
        	Image png = Image.getInstance(stream.toByteArray());
        	c1.setFixedHeight(70f);
        	c1.addElement(png);
        	c1.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
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
    	
        try {
        	table = new PdfPTable(2);
        	
        	table.setWidthPercentage(new float[]{60,465}, PageSize.A4);
        	int line = 0;
        	paraFont.setSize(10.0f);
            for (Integer index=page * SONG_PER_PAGE; index<(page+1)*SONG_PER_PAGE; index++) {
            	line++;
//            	table.setWidthPercentage(new float[]{50,475}, PageSize.A4);
            	Song song = songbook.songs.get(index);
            	
                PdfPCell c1 = new PdfPCell(new Phrase(song.getId(), numFont));
                styleCell(c1, line);
//                c1.setBorder(0);
////                c1.setFixedHeight(13);
//                if (index % 2 == 0) {
//                	c1.setBackgroundColor(new Color(237, 247, 255));
//                }
//                c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            	table.addCell(c1);
                
                PdfPCell c2 = new PdfPCell(new Phrase(song.getTitle(), paraFont));
                styleCell(c2, line);
                table.addCell(c2);
            }
            
            doc.add(table);	        	
        } catch (Exception e) {
        	return false;
        }
        return true;
	}
	
	/**
	 * Style a cell for song row. Set background, set height, set alignment ...
	 * 
	 * @param cell to style
	 * @param row number which this cell belongs to.
	 */
	protected void styleCell(PdfPCell c, int index) {		
		c.setBorder(0);
        c.setFixedHeight(18.0f);
        if (index % 2 == 0) {
        	c.setBackgroundColor(new Color(237, 247, 255));
        }
        
        if (index==1) {
        	//first line show we add border
        	c.setBorderWidthTop(1.0f);
        	c.setBorderColorTop(new Color(150, 150, 150));
        }
        
        c.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_LEFT);
	}
	
	/**
	 * Print header content.
	 * @return true if successfully to write footer
	 */
	protected boolean printPageFooter(int pageIndex) throws Exception {
		PdfPTable table;
    	PdfPCell c1;
		
		table = new PdfPTable(1);
        table.setWidthPercentage(new float[]{525}, PageSize.A4);
        paraFont.setSize(8.0f);
        c1 = new PdfPCell(new Phrase("Page " + pageIndex++ + " | http://ceenee.com", paraFont));
        c1.setBorder(0);
        c1.setBorderWidthTop(1);
        c1.setBorderColorTop(new Color(150, 150, 150));
        c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
        table.addCell(c1);
        
        doc.add(table);
        return true;
	}
	
}
