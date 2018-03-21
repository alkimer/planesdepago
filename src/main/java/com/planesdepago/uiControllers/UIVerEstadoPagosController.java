package com.planesdepago.uiControllers;

import static com.planesdepago.uiControllers.UIMainIntegradoController.hostServices;
import static com.planesdepago.uiUtils.DateUtils.myDateFormatter;
import static com.planesdepago.util.CuotasYPagosUtils.obtenerTablaDeCuotas;
import static com.planesdepago.util.PdfUtils.crearEncabezadoMiSuenioHogar;
import static com.planesdepago.util.PdfUtils.crearHeader;
import static com.planesdepago.util.PdfUtils.crearRandomPDFFileName;
import static com.planesdepago.util.PdfUtils.crearTitulo;
import static com.planesdepago.util.PdfUtils.mostrarPdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.planesdepago.dao.CompraDao;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Cuota;
import com.planesdepago.entities.Pago;
import com.planesdepago.tableRows.CuotasYpagos;
import com.planesdepago.uiUtils.Constantes;
import com.planesdepago.uiUtils.DateUtils;
import com.planesdepago.util.ApplicationContext;
import com.planesdepago.util.PDFeventListener;
import com.planesdepago.util.PdfUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIVerEstadoPagosController extends AbstractController implements Initializable {


  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;
  private Compra compraSeleccionada;

  @FXML
  private TextField tfRazonSocialCliente;

  @FXML
  private TableView<CuotasYpagos> tvCuotasPagos;
  @FXML
  private TableColumn<CuotasYpagos, String> tcCuota;

  @FXML
  private TableColumn<CuotasYpagos, String> tcMontoCuota;
  @FXML
  private TableColumn<CuotasYpagos, LocalDate> tcFechaVencimiento;

  @FXML
  private TableColumn<CuotasYpagos, String> tcCuotaPaga;
  @FXML
  private TableColumn<CuotasYpagos, String> tcDescripcion;

  @FXML
  private Button btnNuevoPago;

  @FXML
  private TextField tfSaldoRestante;

  @FXML
  private TextField tfTotalPagado;

  @FXML
  private TextField tfMontoCompra;
  @FXML
  private TextField tfAnticipo;
  @FXML
  private TextField tfInteres;
  @FXML
  private TextField tfDescripcionCompra;

  @FXML
  private Button onActionBtnImprimirPlanPagos;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(Compra compra) {

    this.compraSeleccionada = compra;

    tcCuota.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, String>("nroCuota"));
    tcMontoCuota.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, String>("montoCuota"));

    tcDescripcion.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, String>("descripcion"));

    tcFechaVencimiento.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, LocalDate>("fechaVencimiento"));
    tcFechaVencimiento.setCellFactory(column -> {
      TableCell<CuotasYpagos, LocalDate> cell = new TableCell<CuotasYpagos, LocalDate>() {

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

    tcCuotaPaga.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, String>("cuotaPaga"));

    this.buscarCuotasYpagos();


  }

  public void buscarCuotasYpagos() {
    BigDecimal anticipo = new BigDecimal("0");
    entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);

    compraSeleccionada = (Compra) compraDao.find(compraSeleccionada.getIdTransaccion());
  //  List<Pago> listaPagos = compraSeleccionada.getPagos();
   // List<Cuota> listaCuotas = compraSeleccionada.getCuotas();
    createTableCuotasYpagos(this.compraSeleccionada);

    this.tfRazonSocialCliente.setText(this.compraSeleccionada.getIDCliente().getRazonSocial());

    //Busco anticipo de la lista de pagos porque no está en el objeto compra.
    for (Pago pago : compraSeleccionada.getPagos()) {
      if (pago.getDescripcionPago().equals(Constantes.ANTICIPO)) {
        anticipo = anticipo.add(pago.getMontoPagado());
      }
    }

    this.tfAnticipo.setText(String.valueOf(anticipo));
    this.tfDescripcionCompra.setText(this.compraSeleccionada.getDescripcion());
    this.tfInteres.setText(String.valueOf(this.compraSeleccionada.getInteres()));
    this.tfMontoCompra.setText(String.valueOf(this.compraSeleccionada.getMontoCompra()));
    compraDao.close();

  }

  private void createTableCuotasYpagos(
       Compra compra) {

    BigDecimal totalPagos = compra.getMontoAFinanciar().
        subtract(compra.getSaldoRestante());

    tfTotalPagado.setText(String.valueOf(totalPagos));
    tfSaldoRestante.setText(String.valueOf(compra.getSaldoRestante()));


    if (compra.getSaldoRestante().compareTo(BigDecimal.ZERO) == 0) {
      btnNuevoPago.setDisable(true);
    }

    tvCuotasPagos.getItems().clear();
    tvCuotasPagos.getItems().addAll(obtenerTablaDeCuotas( compra));
  }



  @FXML
  private void onActionBtnNuevoPago(ActionEvent event) {
    AbstractController cont = cambiarEscena("Nuevo Pago", "/UI_NuevoPago.fxml", Modality.WINDOW_MODAL, this, event);
    ((UINuevoPagoController) cont).init(compraSeleccionada, new BigDecimal(this.tfSaldoRestante.getText()));


  }

  @FXML
  private void onActionBtnImprimirPlanPagos(ActionEvent event) {
    Document document = new Document(PageSize.A4);
    String fileName = crearRandomPDFFileName("ReporteDeudores");
    PdfWriter writer = new PdfUtils().createWriter(document, fileName);
    document.open();
    crearEncabezadoMiSuenioHogar(document);
    crearTitulo(document, "Plan de Pagos");

    PdfPTable tablePlanPagos = new PdfPTable(4);
    tablePlanPagos.setTotalWidth(PageSize.A4.getWidth() * 0.9f);
    tablePlanPagos.setLockedWidth(true);

    crearHeader(tablePlanPagos, new String[]{"Nombre", "Compra", "Monto Compra", "Anticipo"});
    tablePlanPagos.addCell(tfRazonSocialCliente.getText());
    tablePlanPagos.addCell(tfDescripcionCompra.getText());
    tablePlanPagos.addCell(tfMontoCompra.getText());
    tablePlanPagos.addCell(tfAnticipo.getText());


    crearHeader(tablePlanPagos, new String[]{"Nro.Cuota", "Monto Cuota", "Vencimiento", "Descripción"});
    ObservableList<CuotasYpagos> ol = tvCuotasPagos.getItems();
    for (CuotasYpagos cuotasYpagos : ol) {


      tablePlanPagos.addCell(String.valueOf(cuotasYpagos.getNroCuota()));
      tablePlanPagos.addCell(String.valueOf(cuotasYpagos.getMontoCuota()));
      tablePlanPagos.addCell(DateUtils.formatLocalDate2StringPattern(cuotasYpagos.getFechaVencimiento()));
      tablePlanPagos.addCell(cuotasYpagos.getDescripcion());
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
