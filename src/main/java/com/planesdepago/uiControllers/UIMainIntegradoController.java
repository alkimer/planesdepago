package com.planesdepago.uiControllers;

import com.planesdepago.GestionPrestamos;
import com.planesdepago.entities.Cliente;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class UIMainIntegradoController extends AbstractController implements Initializable {
  public static HostServices hostServices;
  private GestionPrestamos main;

  AbstractController controllerIzq;
  AbstractController controllerDer;

  @FXML
  private MenuItem miNuevoCliente;
  @FXML
  private MenuItem miDeudoresVencidos;

  @FXML
  private MenuItem miDeudoresTodos;

  @FXML
  private MenuItem miTotalesDiarios;

  @FXML
  private MenuItem miRestaurar;

  @FXML
  private MenuItem miCrearBackup;

  @FXML
  private MenuBar mbMenuSuperior;

  @FXML
  private BorderPane root;
  @FXML
  private VBox sideBox;


  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void cargaInicial() {
    FXMLLoader loaderIzquierda = new FXMLLoader(getClass().getResource("/UI_ListadoClientes.fxml"));
    FXMLLoader loaderDerecha = new FXMLLoader(getClass().getResource("/UI_ListadoComprasCliente.fxml"));

    AnchorPane paneIzquierda = null;
    AnchorPane paneDerecha = null;
    try {
      paneIzquierda = (AnchorPane) loaderIzquierda.load();
      paneDerecha = (AnchorPane) loaderDerecha.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    controllerIzq = loaderIzquierda.getController();
    controllerIzq.setMainController(this);
    main.getBorderPane().setLeft(paneIzquierda);
    controllerDer = loaderDerecha.getController();
    controllerDer.setMainController(this);
    main.getBorderPane().setRight(paneDerecha);
  }


  //En un evento de cambio de cliente, mi hijo me avisa que debo actualizar las compras que se muestran.
  public void actualizarClienteSeleccionado(Cliente clienteSeleccionado) {
    ((UIListadoComprasClienteController) controllerDer).init(clienteSeleccionado);
  }

  public void setMain(GestionPrestamos main) {
    this.main = main;
  }

  @FXML
  private void onActionMiNuevoCliente(ActionEvent event) {
    AbstractController abs = cambiarEscena("Crear nuevo cliente", "/UI_CrearCliente.fxml", Modality.WINDOW_MODAL, this,
        mapToNodeCompatibleEvent(mbMenuSuperior, event));
    //((UICrearClienteController) abs).createTableListaClientes();


  }

  @FXML
  private void onActionMiDeudoresVencidos(ActionEvent event) {
    AbstractController abs =
        cambiarEscena("Reporte de Deudores Vencidos", "/UI_ReporteDeudoresVencidos.fxml", Modality.WINDOW_MODAL, this,
            mapToNodeCompatibleEvent(mbMenuSuperior, event));

  }

  @FXML
  private void onActionMiDeudoresTodos(ActionEvent event) {
    AbstractController abs =
        cambiarEscena("Reporte de Deudores: Todos", "/UI_ReporteDeudoresTodos.fxml", Modality.WINDOW_MODAL, this,
            mapToNodeCompatibleEvent(mbMenuSuperior, event));

  }

  @FXML
  private void onActionMiTotalesDiarios(ActionEvent event) {
    AbstractController abs =
        cambiarEscena("Reporte Pagos Diarios", "/UI_ReportePagosDiarios.fxml", Modality.WINDOW_MODAL, this,
            mapToNodeCompatibleEvent(mbMenuSuperior, event));

  }

  @FXML
  private void onActionMiRestaurar(ActionEvent event) {
    AbstractController abs = cambiarEscena("Restaurar Backup", "/UI_GestorBackups.fxml", Modality.WINDOW_MODAL, this,
        mapToNodeCompatibleEvent(mbMenuSuperior, event));

  }

  @FXML
  private void onActionMiCrearBackup(ActionEvent event) {
    AbstractController abs = cambiarEscena("Crear Backup", "/UI_CrearBackup.fxml", Modality.WINDOW_MODAL, this,
        mapToNodeCompatibleEvent(mbMenuSuperior, event));

  }

  //LLamo al controlador que muestra la lista de clientes para actualizarlo luego de un nuevo cliente.
  public void refrescarListadoClientes() {
    ((UIListadoClientesController) controllerIzq).createTableListaClientes();
  }
}
