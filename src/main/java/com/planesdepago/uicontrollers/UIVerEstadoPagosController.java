package com.planesdepago.uicontrollers;

import static com.planesdepago.uiutils.DateUtils.myDateFormatter;
import static com.planesdepago.util.CuotasYPagosUtils.obtenerTablaDeCuotas;

import com.planesdepago.dao.CompraDao;
import com.planesdepago.dao.PagoDao;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;
import com.planesdepago.tablerows.CuotasYpagos;
import com.planesdepago.uiutils.Constantes;
import com.planesdepago.uiutils.ListaTiposDePago;
import com.planesdepago.util.ApplicationContext;
import com.planesdepago.util.CuotasYPagosUtils;

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

  @FXML
  private Button btnVerDetallesAnticipo;

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
    tvCuotasPagos.getItems().addAll(obtenerTablaDeCuotas(compra));
  }


  @FXML
  private void onActionBtnNuevoPago(ActionEvent event) {
    AbstractController cont = cambiarEscena("Nuevo Pago", "/UI_NuevoPago.fxml", Modality.WINDOW_MODAL, this, event);

    ((UINuevoPagoController) cont).init(compraSeleccionada, new BigDecimal(this.tfSaldoRestante.getText()), false);


  }

  /*
  abro el mismo componente que el nuevo pago, pero read-only
   */
  @FXML
  private void onActionBtnVerDetallesAnticipo(ActionEvent event) {
    AbstractController cont =
        cambiarEscena("Detalles Anticipo", "/UI_NuevoPago.fxml", Modality.WINDOW_MODAL, this, event);
    PagoDao pagoDao = new PagoDao(context.getEntityManager());
    Pago anticipo = pagoDao.getAnticipo(compraSeleccionada);
    if ((anticipo.getMontoPagado().compareTo(BigDecimal.ZERO) == 0) || (anticipo.getTipoPago() == null)) {
      anticipo.setTipoPago(ListaTiposDePago.EFECTIVO);
    }
    ((UINuevoPagoController) cont).configurarSoloLectura(anticipo);


  }

  @FXML
  public void onActionBtnImprimirPlanPagos(ActionEvent event) {

    CuotasYPagosUtils
        .imprimirEstadoPlanPagos(tfRazonSocialCliente.getText(), tfDescripcionCompra.getText(), tfMontoCompra.getText(),
            tfAnticipo.getText(), tfSaldoRestante.getText(), tvCuotasPagos);
  }
}
