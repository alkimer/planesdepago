package com.planesdepago.uiControllers;

import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Cuota;
import com.planesdepago.entities.Pago;
import com.planesdepago.tableRows.CuotasYpagos;
import com.planesdepago.util.ApplicationContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIVerPagosYcuotasController extends AbstractController implements Initializable {


  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;
  private Compra compraSeleccionada;

  @FXML
  private TableView<CuotasYpagos> tvCuotasPagos;
  @FXML
  private TableColumn<CuotasYpagos, String> tcCuota;

  @FXML
  private TableColumn<CuotasYpagos, String> tcMontoCuota;
  @FXML
  private TableColumn<CuotasYpagos, String> tcFechaVencimiento;

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


  @Override
  public void initialize(URL location, ResourceBundle resources) {

/*
    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      public void handle(WindowEvent we) {
        System.out.println("Stage is closing");
      }
    });
    */
  }

  public void init(Compra compra) {

    this.compraSeleccionada = compra;

    tcCuota.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, String>("nroCuota"));
    tcMontoCuota.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, String>("montoCuota"));

    tcDescripcion.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, String>("descripcion"));

    tcFechaVencimiento.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, String>("fechaVencimiento"));
    tcCuotaPaga.setCellValueFactory(new PropertyValueFactory<CuotasYpagos, String>("cuotaPaga"));

    this.buscarCuotasYpagos();

  }

  public void buscarCuotasYpagos() {

    // entityManager = context.getEntityManager();
    //CompraDao compraDao = new CompraDao(entityManager);
    List<Pago> listaPagos = compraSeleccionada.getPagos();
    List<Cuota> listaCuotas = compraSeleccionada.getCuotas();
    createTableCuotasYpagos(listaPagos, listaCuotas);

    //TODO: si ya no hay deuda el botón de NUEVO PAGO DEBERÍA ESTAR GRISADO.

  }

  private void createTableCuotasYpagos(List<Pago> listaPagos, List<Cuota> listaCuotas) {
    BigDecimal totalPagos = compraSeleccionada.getMontoAFinanciar().
        subtract(compraSeleccionada.getSaldoRestante());
    ObservableList<CuotasYpagos> olCuotasYpagos = FXCollections.observableArrayList();


    //Calculo el total que se ha pagado
 /*   for (Pago pago : listaPagos) {
      totalPagos = totalPagos.add(pago.getMontoPagado());
    }
    tfTotalPagado.setText(String.valueOf(totalPagos));
    tfSaldoRestante.setText(String.valueOf(compraSeleccionada.getMontoAFinanciar().subtract(totalPagos)));
*/

    tfTotalPagado.setText(String.valueOf(totalPagos));
    tfSaldoRestante.setText(String.valueOf(compraSeleccionada.getSaldoRestante()));

    for (Cuota cuota : listaCuotas) {

      CuotasYpagos cuotasYpagos = new CuotasYpagos();

      //Del total que se pagó, voy calculando a qué cuotas correspondería
      if (totalPagos.compareTo(cuota.getMontoCuota()) >= 0) {
        totalPagos = totalPagos.subtract(cuota.getMontoCuota());
        cuotasYpagos.setCuotaPaga("Si");
      } else if ((totalPagos.compareTo(cuota.getMontoCuota()) == -1) && (totalPagos.compareTo(BigDecimal.ZERO) != 0)) {
        cuotasYpagos.setCuotaPaga("Parcial, se abonó $" + totalPagos);
        totalPagos = BigDecimal.ZERO;
      } else {
        cuotasYpagos.setCuotaPaga("No");
      }

      cuotasYpagos.setMontoCuota(cuota.getMontoCuota());
      cuotasYpagos.setNroCuota(cuota.getNroCuota());
      cuotasYpagos.setFechaVencimiento(cuota.getFechaVencimiento());
      cuotasYpagos.setDescripcion(cuota.getDescripcion());
      olCuotasYpagos.add(cuotasYpagos);

    }
    tvCuotasPagos.getItems().clear();
    tvCuotasPagos.getItems().addAll(olCuotasYpagos);
  }

  @FXML
  private void onActionBtnNuevoPago(ActionEvent event) {
    AbstractController cont = cambiarEscena("Nuevo Pago", "/UI_NuevoPago.fxml", Modality.WINDOW_MODAL, this);


    ((UINuevoPagoController) cont).init(compraSeleccionada, new BigDecimal(this.tfSaldoRestante.getText()));

  }

}
