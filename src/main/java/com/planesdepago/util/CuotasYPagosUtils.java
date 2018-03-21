package com.planesdepago.util;


import static com.planesdepago.util.PdfUtils.crearEncabezadoMiSuenioHogar;
import static com.planesdepago.util.PdfUtils.crearHeader;
import static com.planesdepago.util.PdfUtils.crearRandomPDFFileName;
import static com.planesdepago.util.PdfUtils.crearTitulo;
import static com.planesdepago.util.PdfUtils.mostrarPdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.planesdepago.dao.CompraDao;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Cuota;
import com.planesdepago.tableRows.CuotasYpagos;
import com.planesdepago.uiUtils.Constantes;
import com.planesdepago.uiUtils.DateUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;

public class CuotasYPagosUtils {

  /*
  Computo los pagos realizados y lo comparo con las cuotas, para ver el estado del cliente
  con respecto al plan de pagos
   */
  public static ObservableList<CuotasYpagos> obtenerTablaDeCuotas(
      Compra compra) {
    ApplicationContext context = ApplicationContext.getInstance();
    EntityManager entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);

    compra = (Compra) compraDao.find(compra.getIdTransaccion());

    ObservableList<CuotasYpagos> olCuotasYpagos = FXCollections.observableArrayList();
    List<Cuota> listaCuotas = compra.getCuotas();
    BigDecimal totalPagos = compra.getMontoAFinanciar().
        subtract(compra.getSaldoRestante());
    for (Cuota cuota : listaCuotas) {

      CuotasYpagos cuotasYpagos = new CuotasYpagos();
      //Del total que se pagó, voy calculando a qué cuotas correspondería
      if (totalPagos.compareTo(cuota.getMontoCuota()) >= 0) {
        totalPagos = totalPagos.subtract(cuota.getMontoCuota());
        cuotasYpagos.setCuotaPaga(Constantes.CUOTA_PAGA);
      } else if ((totalPagos.compareTo(cuota.getMontoCuota()) == -1) && (totalPagos.compareTo(BigDecimal.ZERO) != 0)) {
        cuotasYpagos.setCuotaPaga(Constantes.CUOTA_PAGO_PARCIAL + totalPagos);
        totalPagos = BigDecimal.ZERO;
      } else {
        cuotasYpagos.setCuotaPaga(Constantes.CUOTA_NO_PAGA);
      }
      cuotasYpagos.setMontoCuota(cuota.getMontoCuota());
      cuotasYpagos.setNroCuota(cuota.getNroCuota());
      cuotasYpagos.setFechaVencimiento(cuota.getFechaVencimiento());
      cuotasYpagos.setDescripcion(cuota.getDescripcion());
      olCuotasYpagos.add(cuotasYpagos);

    }
    compraDao.close();
    return olCuotasYpagos;
  }

  public static void imprimirPlanPagos(String razonSocial, String descripcion, String monto, String anticipo, String
      saldoRestante,
      TableView<Cuota> tvCuotas) {
    Document document = new Document(PageSize.A4);
    String fileName = crearRandomPDFFileName("PlanDePagos");
    PdfWriter writer = new PdfUtils().createWriter(document, fileName);
    document.open();
    crearEncabezadoMiSuenioHogar(document);
    crearTitulo(document, "Plan de Pagos");

    PdfPTable tablePlanPagos = new PdfPTable(4);
    tablePlanPagos.setTotalWidth(PageSize.A4.getWidth() * 0.9f);
    tablePlanPagos.setLockedWidth(true);

    crearHeader(tablePlanPagos, new String[]{"Nombre", "Compra", "Monto Compra", "Anticipo"});
    tablePlanPagos.addCell(razonSocial);
    tablePlanPagos.addCell(descripcion);
    tablePlanPagos.addCell(monto);
    tablePlanPagos.addCell(anticipo);



    crearHeader(tablePlanPagos, new String[]{"Saldo a Financiar", "", "", ""});
    tablePlanPagos.addCell(saldoRestante);
    tablePlanPagos.addCell("");
    tablePlanPagos.addCell("");
    tablePlanPagos.addCell("");


    crearHeader(tablePlanPagos, new String[]{"Nro.Cuota", "Monto Cuota", "Vencimiento", "Descripción"});
    ObservableList<Cuota> ol = tvCuotas.getItems();
    for (Cuota cuota : ol) {


      tablePlanPagos.addCell(String.valueOf(cuota.getNroCuota()));
      tablePlanPagos.addCell(String.valueOf(cuota.getMontoCuota()));
      tablePlanPagos.addCell(DateUtils.formatLocalDate2StringPattern(cuota.getFechaVencimiento()));
      tablePlanPagos.addCell(cuota.getDescripcion());
    }


    try {
      document.add(tablePlanPagos);

    } catch (DocumentException e) {
      e.printStackTrace();
    }


    document.close();
    mostrarPdf(fileName);
  }


  /*
  Imprime el ESTADO del plan según las cuotas y los pagos
   */
  public static void imprimirEstadoPlanPagos(String razonSocial, String descripcion, String monto, String anticipo,
      String
      saldoRestante,
      TableView<CuotasYpagos> tvCuotas) {
    Document document = new Document(PageSize.A4);
    String fileName = crearRandomPDFFileName("EstadoPlanDePagos");
    PdfWriter writer = new PdfUtils().createWriter(document, fileName);
    document.open();
    crearEncabezadoMiSuenioHogar(document);
    crearTitulo(document, "Estado de Plan de Pagos");

    PdfPTable tablePlanPagos = new PdfPTable(5);
    tablePlanPagos.setTotalWidth(PageSize.A4.getWidth() * 0.9f);
    tablePlanPagos.setLockedWidth(true);

    crearHeader(tablePlanPagos, new String[]{"Nombre", "Compra", "Monto Compra", "Anticipo", "Saldo restante"});
    tablePlanPagos.addCell(razonSocial);
    tablePlanPagos.addCell(descripcion);
    tablePlanPagos.addCell(monto);
    tablePlanPagos.addCell(anticipo);
    tablePlanPagos.addCell(saldoRestante);




    crearHeader(tablePlanPagos, new String[]{"Nro.Cuota", "Monto Cuota", "Vencimiento", "Descripción", "Cuota Paga?"});
    ObservableList<CuotasYpagos> ol = tvCuotas.getItems();
    for (CuotasYpagos cuotaYpagos : ol) {


      tablePlanPagos.addCell(String.valueOf(cuotaYpagos.getNroCuota()));
      tablePlanPagos.addCell(String.valueOf(cuotaYpagos.getMontoCuota()));
      tablePlanPagos.addCell(DateUtils.formatLocalDate2StringPattern(cuotaYpagos.getFechaVencimiento()));
      tablePlanPagos.addCell(cuotaYpagos.getDescripcion());
      tablePlanPagos.addCell(cuotaYpagos.getCuotaPaga());
    }


    try {
      document.add(tablePlanPagos);

    } catch (DocumentException e) {
      e.printStackTrace();
    }


    document.close();
    mostrarPdf(fileName);
  }
}
