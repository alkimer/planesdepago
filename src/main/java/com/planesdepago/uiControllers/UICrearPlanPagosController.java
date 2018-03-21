package com.planesdepago.uiControllers;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.dao.CompraDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Cuota;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.util.ApplicationContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.BigDecimalStringConverter;

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

  //Definiciones de la tabla de cuotas
  @FXML
  private TableView<Cuota> tvListaCalculoCuotas;
  @FXML
  private TableColumn<Cuota, String> tcNroCuota;
  @FXML
  private TableColumn<Cuota, BigDecimal> tcMontoCuota;
  @FXML
  private TableColumn<Cuota, String> tcVencimientoCuota;
  @FXML
  private TableColumn<Cuota, String> tcDescripcionCuota;
  @FXML
  private Button btnCrearPlanDePagos;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.dpFecha.setValue(LocalDate.now());
    this.dpFecha.setPromptText("dd-mm-yyyy");
    this.tfMonto.setText("0");
    this.tfInteres.setText("0");
    this.tfAnticipo.setText("0");
    for (int i = 1; i <= 24; i++) {
      this.cbCantCuotas.getItems().add(String.valueOf(i));
    }

  }

  /*
    @FXML
    private void onKeyTypedTfAnticipo(KeyEvent keyEvent){

      System.out.print("dd");
    }
  */
  @FXML
  private void onActionBtnCalcularCuotas(ActionEvent event) {
    int numeroCuotas = Integer.valueOf((String) this.cbCantCuotas.getValue());
    LocalDate fechaVencimientoCuota = dpFecha.getValue();
    BigDecimal montoCompra = new BigDecimal(tfMonto.getText());
    BigDecimal anticipo = new BigDecimal(tfAnticipo.getText());

    tfImporteAFinanciar.setText(String.valueOf(montoCompra.subtract(anticipo)));

    ObservableList<Cuota> listaCuotas = FXCollections.observableArrayList();
    Cuota cuota;
    BigDecimal montoCuota =
        new BigDecimal(tfImporteAFinanciar.getText()).divide(new BigDecimal(numeroCuotas), 2, BigDecimal.ROUND_DOWN);

    for (int i = 0; i < numeroCuotas; i++) {


      cuota = new Cuota();
      cuota.setDescripcion("descricpción " + i);
      cuota.setFechaVencimiento(fechaVencimientoCuota);
      fechaVencimientoCuota = fechaVencimientoCuota.plusMonths(1);
      cuota.setMontoCuota(montoCuota);
      cuota.setNroCuota(i + 1);
      listaCuotas.add(i, cuota);
    }

    tvListaCalculoCuotas.getItems().clear();
    tvListaCalculoCuotas.getItems().addAll(listaCuotas);
    calcularTotalFinanciado();
  }

  @FXML
  private void onActionBtnCrearPlanDePagos(ActionEvent event) {
    entityManager = context.getEntityManager();
    int numeroCuotas = Integer.valueOf((String) this.cbCantCuotas.getValue());
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

    cliente = clienteDao.find(cliente.getCuit());
    cliente.addCompra(comp);

    clienteDao.edit(cliente);
    clienteDao.close();


    DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Confirmación", "Se creó el plan de pagos",
        "Se creó el plan de pagos para el cliente seleccionado");

    this.cerrarVentana();
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
    tcMontoCuota.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
    tcMontoCuota.setOnEditCommit(data -> {
      Cuota cuota = data.getRowValue();
      cuota.setMontoCuota(data.getNewValue());
      calcularTotalFinanciado();
    });

    tcMontoCuota.setOnEditStart(data -> {
      tfTotalCuotas.setText("");

    });
    tcVencimientoCuota.setCellValueFactory(new PropertyValueFactory<Cuota, String>("fechaVencimiento"));
    tcDescripcionCuota.setCellValueFactory(new PropertyValueFactory<Cuota, String>("descripcion"));
    tcDescripcionCuota.setCellFactory(TextFieldTableCell.forTableColumn());
  }

  private void calcularTotalFinanciado() {
    BigDecimal sumaCuotas = new BigDecimal("0");
    ObservableList<Cuota> listaCuotas = tvListaCalculoCuotas.getItems();
    for (Cuota cuota : listaCuotas) {
      sumaCuotas = sumaCuotas.add(cuota.getMontoCuota());
    }

    tfTotalCuotas.setText(String.valueOf(sumaCuotas));
  }
}
