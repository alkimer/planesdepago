package com.planesdepago.uiControllers;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.util.ApplicationContext;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIListadoComprasClienteController extends AbstractController implements Initializable {
  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;

  private Cliente cliente;

  private Compra compraSeleccionada;


  @FXML
  private Button btnVerCuotasYpagos;

  @FXML
  private Button btnPagosRealizados;

  @FXML
  private TableView<Compra> tvListaCompras;
  @FXML
  private TableColumn<Compra, String> tcRazonSocial;

  @FXML
  private TableColumn<Compra, String> tcFecha;
  @FXML
  private TableColumn<Compra, String> tcMontoCompra;

  @FXML
  private TableColumn<Compra, String> tcMontoFinanciado;
  @FXML
  private TableColumn<Compra, String> tcCuotas;
  @FXML
  private TableColumn<Compra, String> tcDescripcion;
  @FXML
  private TableColumn<Compra, String> tcSaldoRestante;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tcCuotas.setCellValueFactory(new PropertyValueFactory<Compra, String>("cantCuotas"));
    tcDescripcion.setCellValueFactory(new PropertyValueFactory<Compra, String>("descripcion"));
    tcFecha.setCellValueFactory(new PropertyValueFactory<Compra, String>("fecha"));
    tcMontoCompra.setCellValueFactory(new PropertyValueFactory<Compra, String>("montoCompra"));
    tcMontoFinanciado.setCellValueFactory(new PropertyValueFactory<Compra, String>("montoAFinanciar"));
    tcSaldoRestante.setCellValueFactory(new PropertyValueFactory<Compra,String>("saldoRestante"));

    tcRazonSocial
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Compra, String>, ObservableValue<String>>() {

          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Compra, String> param) {
            Cliente cliente = param.getValue().getIDCliente();
            return new SimpleStringProperty(cliente.getRazonSocial());
          }
        });
  }


  public void init(Cliente cliente) {
    this.cliente = cliente;
    this.buscarTodasLasCompras();

  }

  private void buscarTodasLasCompras() {
    entityManager = context.getEntityManager();
    ClienteDao clienteDao = new ClienteDao(entityManager);
    cliente = clienteDao.find(cliente.getCuit());
    List<Compra> listaCompras = cliente.getCompras();
    createTableListaCompras(listaCompras);


  }

  @FXML
  private void onActionBtnVerCuotasYpagos(ActionEvent event) {
    int selectedIndex = tvListaCompras.getSelectionModel().getSelectedIndex();

    compraSeleccionada = tvListaCompras.getItems().get(selectedIndex);

    AbstractController cont =  cambiarEscena("Estado de Cuotas y Pagos",
        "/UI_VerPagosYcuotas.fxml", Modality.WINDOW_MODAL, this);
    ((UIVerPagosYcuotasController) cont).init(compraSeleccionada);

  }

  private void createTableListaCompras(List<Compra> listaCompras) {
    ObservableList<Compra> compras = FXCollections.observableArrayList();
    compras.addAll(listaCompras);

    tvListaCompras.getItems().clear();
    tvListaCompras.getItems().addAll(compras);


  }

  @FXML
  private void onActionBtnPagosRealizados(ActionEvent event) {
    int selectedIndex = tvListaCompras.getSelectionModel().getSelectedIndex();

    compraSeleccionada = tvListaCompras.getItems().get(selectedIndex);

    AbstractController cont =  cambiarEscena("Historial de Pagos",
        "/UI_VerPagos.fxml", Modality.WINDOW_MODAL, this);
    ((UIVerPagosController) cont).init(compraSeleccionada);

  }


}
