package com.planesdepago.uicontrollers;

import static com.planesdepago.uiutils.DateUtils.myDateFormatter;
import static com.planesdepago.uiutils.PagosUtils.obtenerDescripcion;
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
import com.planesdepago.dao.ClienteDao;
import com.planesdepago.dao.CompraDao;
import com.planesdepago.dao.PagoDao;
import com.planesdepago.entities.Cheque;
import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiutils.Constantes;
import com.planesdepago.uiutils.DateUtils;
import com.planesdepago.util.ApplicationContext;
import com.planesdepago.util.PdfUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIReportePagosDiarios extends AbstractController implements Initializable {

  ApplicationContext context = ApplicationContext.getInstance();
  @FXML
  private DatePicker dpFecha;

  @FXML
  private TextField tfTotalIngresos;

  @FXML
  private Button btnGenerarReporte;

  @FXML
  private TableView<Pago> tvPagos;
  @FXML
  private TableColumn<Pago, LocalDate> tcFecha;

  @FXML
  private TableColumn<Pago, String> tcMonto;
  @FXML
  private TableColumn<Pago, String> tcDescripcion;

  @FXML
  private TableColumn<Pago, String> tcRazonSocial;

  @FXML
  private TableColumn<Pago, String> tcModalidadPago;

  @FXML
  private TextField tfTotalCheques;

  @FXML
  private TextField tfTotalTarjetas;

  @FXML
  private TextField tfTotalEfectivo;

  @FXML
  private Button btnImprimir;


  @Override
  public void initialize(URL location, ResourceBundle resources) {

    tcFecha.setCellValueFactory(new PropertyValueFactory<Pago, LocalDate>("fechaPago"));
    tcFecha.setCellFactory(column -> {
      TableCell<Pago, LocalDate> cell = new TableCell<Pago, LocalDate>() {

        @Override
        protected void updateItem(LocalDate item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
            setStyle("");
          } else {
            // Format date.
            setText(myDateFormatter.format(item));
          }
        }
      };
      return cell;
    });
    tcMonto.setCellValueFactory(new PropertyValueFactory<Pago, String>("montoPagado"));
    tcDescripcion
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pago, String>, ObservableValue<String>>() {
          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Pago, String> param) {
            String texto = param.getValue().getDescripcionPago();
       /*     if (param.getValue().getTipoPago() != null) {
              switch (param.getValue().getTipoPago()) {
                case CHEQUE:
                  Cheque cheque = param.getValue().getCheque();
                  texto +=
                      " Nro.cheque: " + cheque.getNroCheque() + " Vencimiento: " + cheque.getFechaVencimiento() + ""
                          + " Banco: " + cheque.getBanchoEmisor(); + " Retenciones: " + cheque.getRetenciones();
                  break;
                case TARJETA:
                  texto += " Tarjeta: " + param.getValue().getTarjeta();
                  break;

                case RETENCIONES:

                  break;
              }

            }*/
            return new SimpleStringProperty(obtenerDescripcion(param.getValue()));
          }
        });
    tcRazonSocial
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pago, String>, ObservableValue<String>>() {

          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Pago, String> param) {
            EntityManager entityManager = context.getEntityManager();
            ClienteDao clienteDao = new ClienteDao(entityManager);
            PagoDao pagoDao = new PagoDao(entityManager);
            CompraDao compraDao = new CompraDao(entityManager);

            Pago pago = (Pago) pagoDao.find(param.getValue().getIdPago());
            Compra compra = (Compra) compraDao.find(pago.getCompraID().getIdTransaccion());
            Cliente cliente = clienteDao.find(compra.getIDCliente().getCuit());

            //Parece que al cerrar este em se cierran los demás (el de pago y cliente)
            pagoDao.close();
            return new SimpleStringProperty(cliente.getRazonSocial());
          }
        });


    tcModalidadPago
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pago, String>, ObservableValue<String>>() {

          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Pago, String> param) {
            String texto = "";
            texto = param.getValue().getTipoPago() == null ? texto = Constantes.TEXT_TIPO_PAGO_NO_INDICADO
                : param.getValue().getTipoPago().toString();
            return new SimpleStringProperty(texto);
          }
        });

    this.dpFecha.setValue(LocalDate.now());
    this.dpFecha.setConverter(DateUtils.formateadorLocalDate());

    this.generarReporte();
  }

  @FXML
  private void onActionBtnGenerarReporte(ActionEvent event) {
    this.generarReporte();
  }

  private void generarReporte() {
    PagoDao pagoDao;
    EntityManager entityManager;
    entityManager = context.getEntityManager();
    pagoDao = new PagoDao(entityManager);

    List<Pago> listaPagosDiarios = pagoDao.getPagosDiarios(dpFecha.getValue());
    this.createTablePagos(listaPagosDiarios);
    pagoDao.close();

    this.calcularTotalDiario(listaPagosDiarios);

  }

  private void createTablePagos(List<Pago> listaPagos) {

    ObservableList<Pago> pagos = FXCollections.observableArrayList();
    pagos.addAll(listaPagos);

    tvPagos.getItems().clear();
    tvPagos.getItems().addAll(pagos);
  }

  private void calcularTotalDiario(List<Pago> listaPagos) {
    BigDecimal sumaTotalPagos = BigDecimal.ZERO;
    BigDecimal sumaTarjetas = BigDecimal.ZERO;
    BigDecimal sumaCheques = BigDecimal.ZERO;
    BigDecimal sumaEfectivo = BigDecimal.ZERO;

    for (Pago pago : listaPagos) {
      sumaTotalPagos = sumaTotalPagos.add(pago.getMontoPagado());
      if (pago.getTipoPago() != null) {
        switch (pago.getTipoPago()) {
          case TARJETA:
            sumaTarjetas = sumaTarjetas.add(pago.getMontoPagado());
            break;
          case CHEQUE:
            sumaCheques = sumaCheques.add(pago.getMontoPagado());
            break;
          case EFECTIVO:
            sumaEfectivo = sumaEfectivo.add(pago.getMontoPagado());
            break;
        }
      }
    }
    tfTotalIngresos.setText(String.valueOf(sumaTotalPagos));
    tfTotalEfectivo.setText(String.valueOf(sumaEfectivo));
    tfTotalCheques.setText(String.valueOf(sumaCheques));
    tfTotalTarjetas.setText(String.valueOf(sumaTarjetas));
  }

  @FXML
  private void onActionBtnImprimir(ActionEvent event) {
    EntityManager entityManager = context.getEntityManager();
    Document document = new Document(PageSize.A4);
    String fileName = crearRandomPDFFileName("ReporteDiarioPagos");
    PdfWriter writer = new PdfUtils().createWriter(document, fileName);
    document.open();
    crearEncabezadoMiSuenioHogar(document);
    crearTitulo(document, "Reporte ingresos del dia: " + dpFecha.getValue() + " generado el ");

    PdfPTable tableReporteDiario = new PdfPTable(new float[]{1,2,1,1});
    tableReporteDiario.setTotalWidth(PageSize.A4.getWidth() * 0.7f);
    tableReporteDiario.setLockedWidth(true);

    crearHeader(tableReporteDiario, new String[]{"Nombre", "Descripción", "Modalidad", "Monto"});

    ClienteDao clienteDao = new ClienteDao(entityManager);
    PagoDao pagoDao = new PagoDao(entityManager);
    CompraDao compraDao = new CompraDao(entityManager);

    for (Pago pago : tvPagos.getItems()) {


      pago = (Pago) pagoDao.find(pago.getIdPago());
      Compra compra = (Compra) compraDao.find(pago.getCompraID().getIdTransaccion());
      Cliente cliente = clienteDao.find(compra.getIDCliente().getCuit());
      tableReporteDiario.addCell(cliente.getRazonSocial());
      tableReporteDiario.addCell(obtenerDescripcion(pago));
      tableReporteDiario
          .addCell(pago.getTipoPago() != null ? pago.getTipoPago().toString() : Constantes.TEXT_TIPO_PAGO_NO_INDICADO);
      tableReporteDiario.addCell(String.valueOf(pago.getMontoPagado()));


    }
    //Parece que al cerrar este em se cierran los demás (el de pago y cliente)
    pagoDao.close();

    crearHeader(tableReporteDiario, new String[]{"Total Cheques", "Total Tarjeta", "Total Efectivo", "Total Ingresos"});
    tableReporteDiario.addCell(String.valueOf(tfTotalCheques.getText()));
    tableReporteDiario.addCell(String.valueOf(tfTotalTarjetas.getText()));
    tableReporteDiario.addCell(String.valueOf(tfTotalEfectivo.getText()));
    tableReporteDiario.addCell(String.valueOf(tfTotalIngresos.getText()));

    try {
      document.add(tableReporteDiario);

    } catch (DocumentException e) {
      e.printStackTrace();
    }


    document.close();
    mostrarPdf(fileName);
  }


}