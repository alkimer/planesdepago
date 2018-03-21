package com.planesdepago.util;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import javax.print.Doc;


public class PDFeventListener extends PdfPageEventHelper
{

  @Override
  public void onEndPage(PdfWriter writer, Document document)
  {
    //Agrego marca de agua
    PdfContentByte canvas = writer.getDirectContentUnder();
    Phrase watermark = new Phrase("MI SUEÑO HOGAR", new Font(FontFamily.TIMES_ROMAN, 100, Font.NORMAL, BaseColor
        .LIGHT_GRAY));
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 337, 500, 45);


  //Agrego número de Pagina
addFooter(writer,document);
  }

  private void addFooter(PdfWriter writer, Document document){
    PdfPTable footer = new PdfPTable(1);
  //  try {
      // set defaults
    //  footer.setWidths(new int[]{24, 2});
      footer.setTotalWidth(PageSize.A4.getWidth());
      footer.setLockedWidth(true);
      footer.getDefaultCell().setFixedHeight(40);
      footer.getDefaultCell().setBorder(Rectangle.TOP);
      footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

      // add copyright
     // footer.addCell(new Phrase("\u00A9 Memorynotfound.com", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

      // add current page count
      footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
      footer.addCell(new Phrase(String.format("Página %d", writer.getPageNumber()), new Font(Font.FontFamily
          .HELVETICA, 8)));

      // add placeholder for total page count
      /*
      PdfPCell totalPageCount = new PdfPCell(total);
      totalPageCount.setBorder(Rectangle.TOP);
      totalPageCount.setBorderColor(BaseColor.LIGHT_GRAY);
      footer.addCell(totalPageCount);*/

      // write page
      PdfContentByte canvas = writer.getDirectContent();
      canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
    //  footer.writeSelectedRows(0, -1, 34, 50, canvas);

    footer.writeSelectedRows(0, -1, 170, 30, canvas);
      canvas.endMarkedContentSequence();
  //  } catch(DocumentException de) {
   //   throw new ExceptionConverter(de);
    //}
  }
}