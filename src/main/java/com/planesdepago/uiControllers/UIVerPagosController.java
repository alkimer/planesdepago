package com.planesdepago.uiControllers;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;
import com.planesdepago.util.ApplicationContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIVerPagosController extends AbstractController implements Initializable {
  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;
  private Compra compraSeleccionada;

  @FXML
  private TableView<Pago> tvPagos;

  @FXML
  private TableColumn<Pago, String> tcFecha;

  @FXML
  private TableColumn<Pago, String> tcMonto;
  @FXML
  private TableColumn<Pago, String> tcDescripcion;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tcFecha.setCellValueFactory(new PropertyValueFactory<Pago, String>("fechaPago"));
    tcMonto.setCellValueFactory(new PropertyValueFactory<Pago, String>("montoPagado"));
    tcDescripcion.setCellValueFactory(new PropertyValueFactory<Pago, String>("descripcionPago"));

  }

  public void init(Compra compra) {

    this.compraSeleccionada = compra;

    createTablePagos(compraSeleccionada.getPagos());
  }

  private void createTablePagos(List<Pago> listaPagos) {

    ObservableList<Pago> pagos = FXCollections.observableArrayList();
    pagos.addAll(listaPagos);

    tvPagos.getItems().clear();
    tvPagos.getItems().addAll(pagos);
  }
}
