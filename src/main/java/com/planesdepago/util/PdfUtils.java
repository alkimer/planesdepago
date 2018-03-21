package com.planesdepago.util;

import static com.planesdepago.uiControllers.UIMainIntegradoController.hostServices;
import static com.planesdepago.uiUtils.DateUtils.formatLocalDate2StringPattern;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.planesdepago.uiUtils.Constantes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

public class PdfUtils {
  private static Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 15, BaseColor.BLACK);

  public static PdfPCell headerCellStyleGray(PdfPCell cell) {

    // alignment
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);

    // padding
    cell.setPaddingTop(0.7f);
    cell.setPaddingBottom(0.7f);


    // background color
    cell.setBackgroundColor(new BaseColor(220, 220, 220));

    // border
    cell.setBorder(0);
    //   cell.setBorderWidthBottom(2f);

    return cell;
  }

  public static PdfPCell normalTableCellStyle(PdfPCell cell) {

    // alignment
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);

    // padding
    cell.setPaddingTop(0.7f);
    cell.setPaddingBottom(0.7f);


    // background color
  //  cell.setBackgroundColor(new BaseColor(0, 220, 220));

    // border
    cell.setBorder(0);
    cell.setBorderWidthBottom(0f);
    cell.setBorderWidthLeft(0.2f);
    cell.setBorderWidthRight(0.2f);

    //   cell.setBorderWidthBottom(2f);

    return cell;
  }

  public static void crearHeader(PdfPTable table, String[] headerTexts) {

    for (int i = 0; i < headerTexts.length; i++) {

      table.addCell(headerCellStyleGray(new PdfPCell(new Phrase(headerTexts[i]))));
    }


  }

  public static void crearEncabezadoMiSuenioHogar(Document document) {
    PdfPTable tableEncabezado = new PdfPTable(1);
    tableEncabezado.setTotalWidth(PageSize.A4.getWidth() * 0.9f);
    tableEncabezado.setLockedWidth(true);

    Phrase frase2 = new Phrase("Mi Sueño Hogar");
    frase2.setFont(font);
    PdfPCell cell2 = new PdfPCell(frase2);
    cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableEncabezado.addCell(cell2);
    try {
      document.add(tableEncabezado);
    } catch (DocumentException e) {
      e.printStackTrace();
    }

  }

  public static void crearTitulo(Document document, String titulo) {
    PdfPTable tableTitulo = new PdfPTable(1);
    tableTitulo.setTotalWidth(PageSize.A4.getWidth() * 0.9f);
    tableTitulo.setLockedWidth(true);


    Phrase frase = new Phrase(titulo + " :" + formatLocalDate2StringPattern(LocalDate.now()));
    frase.setFont(font);
    PdfPCell cell = new PdfPCell(frase);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableTitulo.addCell(cell);
    try {
   /*   Chunk tituloChunk = new Chunk(titulo);

      tituloChunk.setFont(font);
      document.add(tituloChunk);
      Paragraph paragraph1 = new Paragraph(" ");
      paragraph1.setSpacingAfter(72f);
      */
      document.add(tableTitulo);
      Paragraph paragraph1 = new Paragraph(" ");
      paragraph1.setSpacingAfter(10f);
      document.add(paragraph1);
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

  //Creo un nuevo writer y controlo la creación de archivos: que se borren los que ya existen y
  //agregarle un numero random como posfijo al nuevo
  public PdfWriter createWriter(Document document, String fileName) {
    deletePdfFiles();
    PdfWriter writer = null;

    try {
      writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
      writer.setPageEvent(new PDFeventListener());

    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return writer;
  }

  public static String crearRandomPDFFileName(String fileName) {
    Random randomGenerator = new Random();

    int randomInt = randomGenerator.nextInt(10000000);
    return Constantes.PATH_PDFS +fileName + randomInt + ".pdf";
  }

  public static void deletePdfFiles() {

    Arrays.stream(new File(Constantes.PATH_PDFS).listFiles()).forEach(File::delete);

  }

  public static void mostrarPdf(String fileName) {

    //Busco el pdf y lo abro con la app standard que tenga asignada en windows.
    String pathPDF = System.getProperty("user.dir") + fileName;
    //System.out.print(pathPDF);
    hostServices.showDocument(pathPDF);
  }
}
