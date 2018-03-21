package com.planesdepago.uiControllers;

import static com.planesdepago.uiUtils.DateUtils.myDateFormatter;

import com.planesdepago.dao.CompraDao;
import com.planesdepago.dao.CuotaDao;
import com.planesdepago.dao.PagoDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Cuota;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiUtils.DatePickerCellCustom;
import com.planesdepago.uiUtils.DateUtils;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.util.ApplicationContext;
import com.planesdepago.util.BigDecimalStringConverterCustom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIModificarPlanPagosController extends AbstractController implements Initializable {
  ApplicationContext context = ApplicationContext.getInstance();

  EntityManager entityManager;

  private Cliente clienteSeleccionado;
  private Compra compraSeleccionada;

  @FXML
  TextField tfCuit;

  @FXML
  TextField tfRazonSocial;

  @FXML
  DatePicker dpFecha;

  @FXML
  TextField tfMonto;

  @FXML
  ComboBox cbCantCuotas;

  @FXML
  TextField tfInteres;

  @FXML
  TextField tfAnticipo;

  @FXML
  TextField tfDescripcion;

  @FXML
  TextField tfTotalCuotas;

  @FXML
  TextField tfImporteAFinanciar;

  @FXML
  Button btnCancelarModificacion;

  //Definiciones de la tabla de cuotas
  @FXML
  private TableView<Cuota> tvListaCalculoCuotas;
  @FXML
  private TableColumn<Cuota, String> tcNroCuota;
  @FXML
  private TableColumn<Cuota, BigDecimal> tcMontoCuota;
  @FXML
  private TableColumn tcVencimientoCuota;
  @FXML
  private TableColumn<Cuota, String> tcDescripcionCuota;
  @FXML
  private Button btnConfirmarModificacionPlanDePagos;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.dpFecha.setValue(LocalDate.now());
    this.dpFecha.setConverter(DateUtils.formateadorLocalDate());
    this.dpFecha.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        //       btnConfirmarModificacionPlanDePagos.setDisable(true);
      }
    });
    this.tfMonto.setText("0");
    this.tfMonto.setOnKeyTyped(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        btnConfirmarModificacionPlanDePagos.setDisable(true);
      }
    });


    this.tfInteres.setText("0");
    this.tfInteres.setOnKeyTyped(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        btnConfirmarModificacionPlanDePagos.setDisable(true);
      }
    });
    this.tfAnticipo.setText("0");
    this.tfAnticipo.setOnKeyTyped(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        btnConfirmarModificacionPlanDePagos.setDisable(true);
      }
    });
    for (int i = 1; i <= 24; i++) {
      this.cbCantCuotas.getItems().add(String.valueOf(i));
    }
    this.cbCantCuotas.setValue("1");
    this.cbCantCuotas.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        //  btnConfirmarModificacionPlanDePagos.setDisable(true);
      }
    });

    tcVencimientoCuota.setCellFactory(column -> {
      TableCell<Cuota, LocalDate> cell = new TableCell<Cuota, LocalDate>() {

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

  }

  @FXML
  private void onActionBtnCancelarModificacion(ActionEvent event) {
    this.cerrarVentana();
  }


  private void onActionBtnCalcularCuotas(ActionEvent event) {
    this.btnConfirmarModificacionPlanDePagos.setDisable(false);
    int numeroCuotas = Integer.valueOf((String) this.cbCantCuotas.getValue());
    LocalDate fechaVencimientoCuota;

    BigDecimal montoCompra = new BigDecimal(tfMonto.getText());
    BigDecimal anticipo = new BigDecimal(tfAnticipo.getText());

    //Si no hubo anticipo, el vencimiento de la primero cuota es hoy, si no el mes que viene
    if (anticipo.compareTo(BigDecimal.ZERO) == 0) {
      fechaVencimientoCuota = dpFecha.getValue();
    } else {
      fechaVencimientoCuota = dpFecha.getValue().plusMonths(1);
    }
    BigDecimal totalCuotas;

    //Calculo el importe a financiar con el interes ingresado
    BigDecimal interes = new BigDecimal(tfInteres.getText());
    BigDecimal importeAFinanciar = montoCompra.subtract(anticipo);
    importeAFinanciar = importeAFinanciar.add(((interes.multiply(importeAFinanciar).divide(new BigDecimal(100)))));
    importeAFinanciar = importeAFinanciar.setScale(2, BigDecimal.ROUND_DOWN);
    tfImporteAFinanciar.setText(String.valueOf(importeAFinanciar));

    ObservableList<Cuota> listaCuotas = FXCollections.observableArrayList();
    Cuota cuota = null;
    BigDecimal montoCuota =
        new BigDecimal(tfImporteAFinanciar.getText()).divide(new BigDecimal(numeroCuotas), 2, BigDecimal.ROUND_DOWN);

    for (int i = 0; i < numeroCuotas; i++) {


      cuota = new Cuota();
      //cuota.setDescripcion("descricpción " + i);
      cuota.setFechaVencimiento(fechaVencimientoCuota);
      fechaVencimientoCuota = fechaVencimientoCuota.plusMonths(1);


      cuota.setMontoCuota(montoCuota);
      cuota.setNroCuota(i + 1);
      listaCuotas.add(i, cuota);
    }
    totalCuotas = calcularTotalCuotas(listaCuotas);

    //Si encuentro que por tema de redondeo, el total de las cuotas difiere del total financiado, entonces agrego esa
    // diferencia en la última cuota.
    if (importeAFinanciar.compareTo(totalCuotas) == 1) {

      cuota.setMontoCuota(cuota.getMontoCuota().add(importeAFinanciar.subtract(totalCuotas)));
      totalCuotas = calcularTotalCuotas(listaCuotas);

    }

    tfTotalCuotas.setText(String.valueOf(totalCuotas));
    tvListaCalculoCuotas.getItems().clear();
    tvListaCalculoCuotas.getItems().addAll(listaCuotas);
  }

  @FXML
  private void onActionBtnConfirmarModificacionPlanDePagos(ActionEvent event) {
    if (camposObligatoriosValidosParaModificarPlan()) {
      if (new BigDecimal(tfTotalCuotas.getText()).compareTo(new BigDecimal(tfImporteAFinanciar.getText())) != 0) {
        DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Eror", "Revise las cuotas",
            "Ud. modificó manualmente el monto de las cuotas y éstas no coinciden con el monto a financiar.");

      } else {

        entityManager = context.getEntityManager();
        //   int numeroCuotas = Integer.valueOf((String) this.cbCantCuotas.getValue());
        //   Pago pago = new Pago();
        CompraDao compraDao = new CompraDao(entityManager);
        CuotaDao cuotaDao = new CuotaDao(entityManager);
        // ClienteDao clienteDao = new ClienteDao(entityManager);

        //  Compra comp = new Compra();
        Compra comp = compraSeleccionada;
        //  comp.setCantCuotas(numeroCuotas);
        //  comp.setFecha(dpFecha.getValue());
        comp.setDescripcion(tfDescripcion.getText());
        //  comp.setInteres(new BigDecimal(tfInteres.getText()));
        //  comp.setMontoCompra(new BigDecimal(tfMonto.getText()));
        //  comp.setMontoAFinanciar(new BigDecimal(tfImporteAFinanciar.getText()));
        //  comp.setSaldoRestante(new BigDecimal(tfImporteAFinanciar.getText()));

        ObservableList<Cuota> listaCuotas = tvListaCalculoCuotas.getItems();
        comp.setCuotas(listaCuotas);

/*
      //Si hubo anticipo lo cargo en forma de Pago para que aparezca en el histórico
          if (new BigDecimal(tfMonto.getText()).compareTo(new BigDecimal(tfAnticipo.getText())) == -1) {
        DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Anticipo erróneo",
            "El anticipo no puede ser mayor que el monto de la compraSeleccionada");
      } else {if (new BigDecimal(tfAnticipo.getText()).compareTo(BigDecimal.ZERO) == 1) {
        //Si el anticipo es mayor a 0 , lo guardo como pago
        pago.setMontoPagado(new BigDecimal(tfAnticipo.getText()));
        pago.setDescripcionPago(Constantes.ANTICIPO);
        pago.setFechaPago(LocalDate.now());
        comp.addPago(pago);
      }
*/

        //      clienteSeleccionado = clienteDao.find(clienteSeleccionado.getCuit());
        //       clienteSeleccionado.addCompra(comp);

        //     clienteDao.edit(clienteSeleccionado);
        //    clienteDao.close();
      /*
      for  (Cuota cuota: listaCuotas) {
        cuotaDao.edit(cuota);
      }
*/
        compraDao.edit(comp);
        compraDao.close();
        DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Confirmación", "Modificación exitosa",
            "Se realizó la modificación en el plan de pagos.");
        ((UIListadoComprasClienteController) mainController).buscarTodasLasCompras();

        this.cerrarVentana();
      }
    }
  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnConfirmarModificacionPlanDePagos.getScene().getWindow();
    stage.close();
  }

  //inicializo el clienteSeleccionado
  void init(Cliente cliente, Compra compra) {
    this.clienteSeleccionado = cliente;
    this.compraSeleccionada = compra;

    entityManager = context.getEntityManager();
    PagoDao pagoDao = new PagoDao(entityManager);

    tfDescripcion.setText(compra.getDescripcion());
    dpFecha.setValue(compra.getFecha());
    tfMonto.setText(String.valueOf(compra.getMontoCompra()));
    tfAnticipo.setText(String.valueOf(((Pago) pagoDao.getAnticipo(compraSeleccionada)).getMontoPagado()));
    cbCantCuotas.setValue(compra.getCantCuotas());
    tfInteres.setText(String.valueOf(compra.getInteres()));
    tfCuit.setText(cliente.getCuit());
    tfRazonSocial.setText(cliente.getRazonSocial());
    tfImporteAFinanciar.setText(String.valueOf(compra.getMontoAFinanciar()));

    //Inicializo propiedades de la tabla de cuotas
    tcNroCuota.setCellValueFactory(new PropertyValueFactory<Cuota, String>("nroCuota"));
    tcMontoCuota.setCellValueFactory(new PropertyValueFactory<Cuota, BigDecimal>("montoCuota"));
    tcMontoCuota.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverterCustom()));
    tcMontoCuota.setOnEditCommit(data -> {
      Cuota cuota = data.getRowValue();
      cuota.setMontoCuota(data.getNewValue());
      btnConfirmarModificacionPlanDePagos.setDisable(false);
      calcularTotalCuotasV2();
    });

    tcMontoCuota.setOnEditStart(data -> {
      tfTotalCuotas.setText("");
      btnConfirmarModificacionPlanDePagos.setDisable(true);
    });

    tcVencimientoCuota.setCellValueFactory(new PropertyValueFactory<Cuota, LocalDate>("fechaVencimiento"));
    tcVencimientoCuota.setCellFactory(new Callback<TableColumn, TableCell>() {
      @Override
      public TableCell call(TableColumn p) {
        DatePickerCellCustom datePick = new DatePickerCellCustom(tvListaCalculoCuotas.getItems());
        return datePick;
      }
    });


    tcVencimientoCuota.setOnEditCommit(data -> {
      btnConfirmarModificacionPlanDePagos.setDisable(false);
    });
    tcVencimientoCuota.setOnEditStart(data -> {
      btnConfirmarModificacionPlanDePagos.setDisable(true);
    });

    tcDescripcionCuota.setCellValueFactory(new PropertyValueFactory<Cuota, String>("descripcion"));
    tcDescripcionCuota.setCellFactory(TextFieldTableCell.forTableColumn());
    tcDescripcionCuota.setOnEditCommit(data -> {
      Cuota cuota = data.getRowValue();
      cuota.setDescripcion(data.getNewValue());
      btnConfirmarModificacionPlanDePagos.setDisable(false);
    });

    tcDescripcionCuota.setOnEditStart(data -> {
      btnConfirmarModificacionPlanDePagos.setDisable(true);
    });


    //Inicialmente creo la tabla de cuotas pero con la info de la DB
    entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);
    compraSeleccionada = (Compra) compraDao.find(compraSeleccionada.getIdTransaccion());
    tvListaCalculoCuotas.getItems().clear();
    tvListaCalculoCuotas.getItems().addAll(compraSeleccionada.getCuotas());
    BigDecimal totalCuotas = calcularTotalCuotas(tvListaCalculoCuotas.getItems());
    tfTotalCuotas.setText(String.valueOf(totalCuotas));

    compraDao.close();
  }

  /*
  Calculo el total con la lista de cuotas pasada por parámetro.
   */
  private BigDecimal calcularTotalCuotas(ObservableList<Cuota> listaCuotas) {
    BigDecimal sumaCuotas = new BigDecimal("0");
    //  ObservableList<Cuota> listaCuotas = tvListaCalculoCuotas.getItems();
    for (Cuota cuota : listaCuotas) {
      sumaCuotas = sumaCuotas.add(cuota.getMontoCuota());
    }
    return sumaCuotas;
  }

  /* con esta version calculo las cuotas solo mirando los elementos que ya existen en la tabla
  */

  private void calcularTotalCuotasV2() {
    BigDecimal sumaCuotas = new BigDecimal("0");
    ObservableList<Cuota> listaCuotas = tvListaCalculoCuotas.getItems();
    for (Cuota cuota : listaCuotas) {
      sumaCuotas = sumaCuotas.add(cuota.getMontoCuota());
    }
    tfTotalCuotas.setText(String.valueOf(sumaCuotas));
  }

  private boolean camposObligatoriosValidosParaModificarPlan() {
    if (tfDescripcion.getText().trim().equals("")) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
          "Debe ingresar una descripción para el Plan de Pagos");
      return false;
    }


    return true;
  }
}
