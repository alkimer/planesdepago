package com.planesdepago.uiControllers;


import static com.planesdepago.util.CuotasYPagosUtils.obtenerTablaDeCuotas;
import static com.planesdepago.util.PdfUtils.crearHeader;
import static com.planesdepago.util.PdfUtils.crearRandomPDFFileName;
import static com.planesdepago.util.PdfUtils.crearTitulo;
import static com.planesdepago.util.PdfUtils.mostrarPdf;
import static com.planesdepago.util.PdfUtils.normalTableCellStyle;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.planesdepago.dao.CompraDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Cuota;
import com.planesdepago.tableRows.CuotasYpagos;
import com.planesdepago.uiUtils.DateUtils;
import com.planesdepago.uiUtils.ListaLocalidades;
import com.planesdepago.util.ApplicationContext;
import com.planesdepago.util.PdfUtils;

import org.controlsfx.control.table.TableFilter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIReporteDeudoresTodosController extends AbstractController implements Initializable {
  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;
  TableFilter.Builder<Compra> filtro = null;
  @FXML
  private Button btnImprimirConDetalles;

  @FXML
  private Button btnImprimirResumido;

  @FXML
  private TableView<Compra> tvDeudores;

  @FXML
  private TableColumn<Compra, String> tcRazonSocial;
  @FXML
  private TableColumn<Compra, String> tcLocalidad;
  @FXML
  private TableColumn<Compra, String> tcDireccion;
  @FXML
  private TableColumn<Compra, String> tcTelefono;
  @FXML
  private TableColumn<Compra, String> tcDescripcionCompra;
  @FXML
  private TableColumn<Compra, String> tcSaldo;


  @Override
  public void initialize(URL location, ResourceBundle resources) {

    entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);

    tcLocalidad
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Compra, String>, ObservableValue<String>>() {

          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Compra, String> param) {
            Cliente cliente = param.getValue().getIDCliente();
            return new SimpleStringProperty(ListaLocalidades.valueOf(cliente.getLocalidad()).displayName());
          }
        });
    tcDireccion
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Compra, String>, ObservableValue<String>>() {

          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Compra, String> param) {
            Cliente cliente = param.getValue().getIDCliente();
            return new SimpleStringProperty(cliente.getDireccion());
          }
        });

    tcTelefono
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Compra, String>, ObservableValue<String>>() {

          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Compra, String> param) {
            Cliente cliente = param.getValue().getIDCliente();
            return new SimpleStringProperty(cliente.getCelular() + "-" + cliente.getTelefonoFijo());
          }
        });

    tcSaldo.setCellValueFactory(new PropertyValueFactory<Compra, String>("saldoRestante"));
    tcDescripcionCompra.setCellValueFactory(new PropertyValueFactory<Compra, String>("descripcion"));
    tcRazonSocial
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Compra, String>, ObservableValue<String>>() {

          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Compra, String> param) {
            Cliente cliente = param.getValue().getIDCliente();

            return new SimpleStringProperty(cliente.getRazonSocial());
          }
        });


    compraDao.close();
    this.generarReporteUIDeudores();
  }


  /*
  Imprimir con detalles del plan de pago
   */
  @FXML
  private void onActionBtnImprimir(ActionEvent event) {
    this.generarReportePDFDeudoresTodos(true);
  }

  /*
  Imprimir resumido
   */
  @FXML
  private void onActionImprimirResumido(ActionEvent event) {
    this.generarReportePDFDeudoresTodos(false);
  }


  /*
  Se genera un reporte PDF de TODOS los deudores
   */
  private void generarReportePDFDeudoresTodos(boolean conDetallesPlanPago) {
    entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);
    Document document = new Document(PageSize.A4.rotate());
    String fileName = crearRandomPDFFileName("ReporteDeudoresTodos");
    PdfWriter writer = new PdfUtils().createWriter(document, fileName);
    document.open();
    crearTitulo(document, "REPORTE TODOS LOS DEUDORES");

    //  Font font = FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK);
    PdfPTable tableCompra = null;
    PdfPTable tableDetalleCompra;
    List<Cuota> listaCuotas;


    //Si no necesito los detalles solo creo un header y no lo vuevlo a replicar más

    if (!conDetallesPlanPago) {
      tableCompra = new PdfPTable(6);
      tableCompra.setTotalWidth(PageSize.A4.getHeight());
      tableCompra.setLockedWidth(true);
      crearHeader(tableCompra,
          new String[]{"Nombre", "Localidad", "Dirección", "Teléfono", "Desc. compra", "Saldo " + "Restante"});
      try {
        document.add(tableCompra);
        if (conDetallesPlanPago) {
          document.add(tableCompra);
        }

      } catch (DocumentException e) {
        e.printStackTrace();
      }
    }


    for (Compra compra : filtro.apply().getBackingList()) {

      tableCompra = new PdfPTable(6);
      tableCompra.setTotalWidth(PageSize.A4.getHeight());
      tableCompra.setLockedWidth(true);
      if (conDetallesPlanPago) {
        crearHeader(tableCompra,
            new String[]{"Nombre", "Localidad", "Dirección", "Teléfono", "Desc. compra", "Saldo " + "Restante"});
      }
      tableCompra.addCell(normalTableCellStyle(new PdfPCell(new Phrase(compra.getIDCliente().getRazonSocial()))));
      tableCompra.addCell(normalTableCellStyle(
          new PdfPCell(new Phrase(ListaLocalidades.valueOf(compra.getIDCliente().getLocalidad()).displayName()))));
      tableCompra.addCell(normalTableCellStyle(new PdfPCell(new Phrase(compra.getIDCliente().getDireccion()))));
      tableCompra.addCell(normalTableCellStyle(new PdfPCell(
          new Phrase(compra.getIDCliente().getCelular() + "/" + compra.getIDCliente().getTelefonoFijo()))));
      tableCompra.addCell(normalTableCellStyle(new PdfPCell(new Phrase(compra.getDescripcion()))));
      tableCompra.addCell(normalTableCellStyle(new PdfPCell(new Phrase(String.valueOf(compra.getSaldoRestante())))));
      compra = (Compra) compraDao.find(compra.getIdTransaccion());

      tableDetalleCompra = new PdfPTable(5);
      tableDetalleCompra.setTotalWidth(PageSize.A4.getWidth());
      tableDetalleCompra.setLockedWidth(true);
      if (conDetallesPlanPago) {
        AgregarTableDetalleCuotas(tableDetalleCompra, obtenerTablaDeCuotas(compra));
      }

      try {
        document.add(tableCompra);
        if (conDetallesPlanPago) {
          document.add(tableDetalleCompra);
        }

      } catch (DocumentException e) {
        e.printStackTrace();
        compraDao.close();
      }

    }

    compraDao.close();
    document.close();
    mostrarPdf(fileName.substring(1));


  }


  private void AgregarTableDetalleCuotas(PdfPTable tableDetalleCompra, ObservableList<CuotasYpagos> cuotasYpagoss) {
    crearHeader(tableDetalleCompra,
        new String[]{"Nro.Cuota", "Monto Cuota", "Descripción", "Vencimiento", "Cuota" + " paga?"});

    for (CuotasYpagos cuotasYpagos : cuotasYpagoss) {
      tableDetalleCompra
          .addCell(normalTableCellStyle(new PdfPCell(new Phrase(String.valueOf(cuotasYpagos.getNroCuota())))));
      tableDetalleCompra
          .addCell(normalTableCellStyle(new PdfPCell(new Phrase(String.valueOf(cuotasYpagos.getMontoCuota())))));
      tableDetalleCompra
          .addCell(normalTableCellStyle(new PdfPCell(new Phrase(String.valueOf(cuotasYpagos.getDescripcion())))));
      tableDetalleCompra.addCell(normalTableCellStyle(
          new PdfPCell(new Phrase(DateUtils.formatLocalDate2StringPattern(cuotasYpagos.getFechaVencimiento())))));
      tableDetalleCompra.addCell(normalTableCellStyle(new PdfPCell(new Phrase(cuotasYpagos.getCuotaPaga()))));
    }
  }


  public void generarReporteUIDeudores() {
    entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);
    List<Compra> comprasConDeuda = compraDao.getComprasConDeuda();

    //Inicialmente muestro ordenado por nombre
    comprasConDeuda.sort(Comparator.comparing(compra -> {
      return compra.getIDCliente().getRazonSocial();
    }));
    tvDeudores.getItems().clear();

    tvDeudores.getItems().addAll(comprasConDeuda);

    compraDao.close();

    //Aplico el filtro para las localidades
    impl.org.controlsfx.i18n.Localization.setLocale(Locale.forLanguageTag("es-ES"));
    filtro = TableFilter.forTableView(tvDeudores);
    filtro.apply();
  }

}