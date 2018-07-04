package com.planesdepago.uicontrollers;

import static com.planesdepago.uiutils.DateUtils.myDateFormatter;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Cuota;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiutils.Constantes;
import com.planesdepago.uiutils.DatePickerCellCustom;
import com.planesdepago.uiutils.DateUtils;
import com.planesdepago.uiutils.DialogPopUp;
import com.planesdepago.uiutils.InputCheck;
import com.planesdepago.util.ApplicationContext;
import com.planesdepago.util.BigDecimalStringConverterCustom;
import com.planesdepago.util.CuotasYPagosUtils;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UICrearPlanPagosController extends AbstractController implements Initializable {
  ApplicationContext context = ApplicationContext.getInstance();

  EntityManager entityManager;

  private Cliente cliente;
@FXML
Button btnAnticipo;

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
  Button btnCalcularCuotas;

  private Pago anticipo = new Pago();

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
  private Button btnCrearPlanDePagos;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.dpFecha.setValue(LocalDate.now());
    this.dpFecha.setConverter(DateUtils.formateadorLocalDate());
    this.dpFecha.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        btnCrearPlanDePagos.setDisable(true);
      }
    });
    this.tfMonto.setText("0");
    this.tfMonto.setOnKeyTyped(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        btnCrearPlanDePagos.setDisable(true);
      }
    });


    this.tfInteres.setText("0");
    this.tfInteres.setOnKeyTyped(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        btnCrearPlanDePagos.setDisable(true);
      }
    });
    this.tfAnticipo.setText("0");
    this.tfAnticipo.setOnKeyTyped(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        btnCrearPlanDePagos.setDisable(true);
      }
    });
    for (int i = 1; i <= 24; i++) {
      this.cbCantCuotas.getItems().add(String.valueOf(i));
    }
    this.cbCantCuotas.setValue("1");
    this.cbCantCuotas.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        btnCrearPlanDePagos.setDisable(true);
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
 /*
            // Style all dates in March with a different color.
            if (item.getMonth() == Month.MARCH) {
              setTextFill(Color.CHOCOLATE);
              setStyle("-fx-background-color: yellow");
            } else {
              setTextFill(Color.BLACK);
              setStyle("");
            } */
          }
        }
      };

      return cell;
    });


    this.btnCrearPlanDePagos.setDisable(true);

  }

  @FXML
  private void onActionBtnAnticipo(ActionEvent event) {
    AbstractController cont = cambiarEscena("Anticipo", "/UI_NuevoPago.fxml", Modality.WINDOW_MODAL, this, event);
   //llamo con persistir en false porque todavía la compra no existe

    ((UINuevoPagoController) cont).init(null, null,true);

  }

  @FXML
  private void onActionBtnCalcularCuotas(ActionEvent event) {
    if (camposObligatoriosValidosParaCalcularCuotas()) {
      this.btnCrearPlanDePagos.setDisable(false);
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
      importeAFinanciar = importeAFinanciar.add((interes.multiply(importeAFinanciar).divide(new BigDecimal(100))));
      importeAFinanciar = importeAFinanciar.setScale(2, BigDecimal.ROUND_DOWN);
      tfImporteAFinanciar.setText(String.valueOf(importeAFinanciar));

      ObservableList<Cuota> listaCuotas = FXCollections.observableArrayList();
      Cuota cuota = null;
      BigDecimal montoCuota =
          new BigDecimal(tfImporteAFinanciar.getText()).divide(new BigDecimal(numeroCuotas), 2, BigDecimal.ROUND_DOWN);

      for (int i = 0; i < numeroCuotas; i++) {


        cuota = new Cuota();
        cuota.setFechaVencimiento(fechaVencimientoCuota);
        cuota.setDescripcion("");
        fechaVencimientoCuota = fechaVencimientoCuota.plusMonths(1);


        cuota.setMontoCuota(montoCuota);
        cuota.setNroCuota(i + 1);
        listaCuotas.add(i, cuota);
      }
      totalCuotas = calcularTotalCuotas(listaCuotas);

      //Si encuentro que por tema de redondeo, el total de las cuotas difiere del total financiado, entonces agrego esa
      // diferencia en la última cuota.
      if (importeAFinanciar.compareTo(totalCuotas) > 0) {

        if (cuota != null) {
          cuota.setMontoCuota(cuota.getMontoCuota().add(importeAFinanciar.subtract(totalCuotas)));
          totalCuotas = calcularTotalCuotas(listaCuotas);
        }

      }

      tfTotalCuotas.setText(String.valueOf(totalCuotas));
      tvListaCalculoCuotas.getItems().clear();
      tvListaCalculoCuotas.getItems().addAll(listaCuotas);

      tcVencimientoCuota.setCellFactory(new Callback<TableColumn, TableCell>() {
        @Override
        public TableCell call(TableColumn p) {
          return new DatePickerCellCustom(tvListaCalculoCuotas.getItems());
        }
      });
    }
  }

  @FXML
  private void onActionBtnCrearPlanDePagos(ActionEvent event) {

    if (camposObligatoriosValidosParaCrearPlan()) {
      if (new BigDecimal(tfTotalCuotas.getText()).compareTo(new BigDecimal(tfImporteAFinanciar.getText())) != 0) {
        DialogPopUp.crearDialogo(Alert.AlertType.ERROR, Constantes.TEXT_ERROR, "Revise las cuotas",
            "Ud. modificó manualmente el monto de las cuotas y éstas no coinciden con el monto a financiar.");

      } else {

        entityManager = context.getEntityManager();
        int numeroCuotas = Integer.valueOf((String) this.cbCantCuotas.getValue());
     //   Pago pago = new Pago();
        ClienteDao clienteDao = new ClienteDao(entityManager);

        Compra comp = new Compra();
        comp.setCantCuotas(numeroCuotas);
        comp.setFecha(dpFecha.getValue());
        comp.setDescripcion(tfDescripcion.getText());
        comp.setInteres(new BigDecimal(tfInteres.getText()));
        comp.setMontoCompra(new BigDecimal(tfMonto.getText()));
        comp.setMontoAFinanciar(new BigDecimal(tfImporteAFinanciar.getText()));
        comp.setSaldoRestante(new BigDecimal(tfImporteAFinanciar.getText()));

        ObservableList<Cuota> listaCuotas = tvListaCalculoCuotas.getItems();
        comp.setCuotas(listaCuotas);


        //Si hubo anticipo lo cargo en forma de Pago para que aparezca en el histórico
        if (new BigDecimal(tfMonto.getText()).compareTo(new BigDecimal(tfAnticipo.getText())) < 0) {
          DialogPopUp.crearDialogo(Alert.AlertType.ERROR, Constantes.TEXT_ERROR, "Anticipo erróneo",
              "El anticipo no puede ser mayor que el monto de la compra");
        } else {
          if (new BigDecimal(tfAnticipo.getText()).compareTo(BigDecimal.ZERO) > 0) {
            //Si el anticipo es mayor a 0 , lo guardo como pago
         //   pago.setMontoPagado(new BigDecimal(tfAnticipo.getText()));
         //   pago.setDescripcionPago(Constantes.ANTICIPO);
         //   pago.setFechaPago(LocalDate.now());
            comp.addPago(anticipo);
          }


          cliente = clienteDao.find(cliente.getCuit());
          cliente.addCompra(comp);

          clienteDao.edit(cliente);
          clienteDao.close();


          DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Confirmación", "Se creó el plan de pagos",
              "Se creó el plan de pagos para el cliente seleccionado");
          ((UIListadoComprasClienteController) mainController).buscarTodasLasCompras();

          CuotasYPagosUtils.imprimirPlanPagos(tfRazonSocial.getText(), tfDescripcion.getText(), tfMonto.getText(),
              tfAnticipo.getText(), tfImporteAFinanciar.getText(), tvListaCalculoCuotas);
          this.cerrarVentana();
        }
      }
    }

  }


  private boolean camposObligatoriosValidosParaCrearPlan() {
    if (tfDescripcion.getText().trim().equals("")) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, Constantes.TEXT_ERROR, Constantes.TEXT_REVISE_LA_INFORMACION,
          "Debe ingresar una descripción para el Plan de Pagos");
      return false;
    }
    if (tfMonto.getText().trim().equals("0")) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, Constantes.TEXT_ERROR, Constantes.TEXT_REVISE_LA_INFORMACION,
          "Ingresó un valor inválido para el monto de la compra");
      return false;
    }
    if (tfAnticipo.getText().trim().equals("")) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, Constantes.TEXT_ERROR, Constantes.TEXT_REVISE_LA_INFORMACION,
          "Ingresó un valor inválido el anticipo");
      return false;
    }
    if (tfInteres.getText().trim().equals("")) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, Constantes.TEXT_ERROR, Constantes.TEXT_REVISE_LA_INFORMACION,
          "Ingresó un valor inválido para el interés");
      return false;
    }

    return true;
  }

  private boolean camposObligatoriosValidosParaCalcularCuotas() {

    if (tfMonto.getText().trim().equals("0")) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, Constantes.TEXT_ERROR, Constantes.TEXT_REVISE_LA_INFORMACION,
          "Ingresó un valor inválido para el Monto de la compra");
      return false;
    }
    if (tfAnticipo.getText().trim().equals("")) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, Constantes.TEXT_ERROR, Constantes.TEXT_REVISE_LA_INFORMACION,
          "Ingresó un valor inválido en el campo Anticipo");
      return false;
    }
    if (tfInteres.getText().trim().equals("")) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, Constantes.TEXT_ERROR, Constantes.TEXT_REVISE_LA_INFORMACION,
          "Ingresó un valor inválido para el campo Interés");
      return false;
    }

    return true;
  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCrearPlanDePagos.getScene().getWindow();
    stage.close();
  }

  //inicializo el cliente
  void init(Cliente cliente) {
    this.cliente = cliente;
    tfCuit.setText(cliente.getCuit());
    tfRazonSocial.setText(cliente.getRazonSocial());

    //Inicializo propiedades de la tabla de cuotas
    tcNroCuota.setCellValueFactory(new PropertyValueFactory<Cuota, String>("nroCuota"));
    tcMontoCuota.setCellValueFactory(new PropertyValueFactory<Cuota, BigDecimal>("montoCuota"));
    tcMontoCuota.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverterCustom()));

    tcMontoCuota.setOnEditCommit(data -> {
      Cuota cuota = data.getRowValue();
      cuota.setMontoCuota(data.getNewValue());
      calcularTotalCuotasV2();
      btnCrearPlanDePagos.setDisable(false);

    });

    tcMontoCuota.setOnEditStart(data -> {
      btnCrearPlanDePagos.setDisable(true);
      tfTotalCuotas.setText("");

    });
    tcVencimientoCuota.setCellValueFactory(new PropertyValueFactory<Cuota, LocalDate>("fechaVencimiento"));
    tcVencimientoCuota.setOnEditCommit(data ->

        btnCrearPlanDePagos.setDisable(false));
    tcVencimientoCuota.setOnEditStart(data -> btnCrearPlanDePagos.setDisable(true));
    tcDescripcionCuota.setCellValueFactory(new PropertyValueFactory<Cuota, String>("descripcion"));
    tcDescripcionCuota.setOnEditCommit(data -> {
      Cuota cuota = data.getRowValue();
      cuota.setDescripcion(data.getNewValue());
      btnCrearPlanDePagos.setDisable(false);

    });
    tcDescripcionCuota.setOnEditStart(data -> btnCrearPlanDePagos.setDisable(true));
    tcDescripcionCuota.setCellFactory(TextFieldTableCell.forTableColumn());


    InputCheck.agregarControlesAEntradaDeCifras(tfMonto, Constantes.MAX_LONGITUD_MONTOS);
    InputCheck.agregarControlesAEntradaDeCifras(tfAnticipo, Constantes.MAX_LONGITUD_MONTOS);
    InputCheck.agregarControlesAEntradaDeCifras(tfInteres, Constantes.MAX_LONGITUD_PORCENTAJES);
  }


  /*
  Calculo el total con la lista de cuotas pasada por parámetro.
   */
  private BigDecimal calcularTotalCuotas(ObservableList<Cuota> listaCuotas) {
    BigDecimal sumaCuotas = new BigDecimal("0");
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

 public void setearAnticipo (Pago anticipo) {
    this.anticipo = anticipo;
    this.tfAnticipo.setText(String.valueOf(anticipo.getMontoPagado()));
    System.out.println("recibi el anticipo de " + anticipo.getMontoPagado());
 }
}
