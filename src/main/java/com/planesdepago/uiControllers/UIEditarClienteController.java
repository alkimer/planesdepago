package com.planesdepago.uiControllers;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.uiUtils.DialogPopUp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sun.applet.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class UIEditarClienteController extends AbstractClientController implements Initializable {

  @FXML
  private Button btnGuardar;
  @FXML
  private Button btnCancelar;
  private UIEditarClienteController uiEditarClienteController;

  @FXML
  private void onActionBtnGuardar(ActionEvent event) {
    entityManager = context.getEntityManager();
   ClienteDao clienteDao = new ClienteDao(entityManager);
   Cliente clienteEntity = mapUI2Cliente();
   clienteDao.edit(clienteEntity);
    DialogPopUp.crearDialogo(
        Alert.AlertType.INFORMATION, "Confirmación", "Edición exitosa",
        "Se ha editado la información del cliente seleccionado");
    clienteDao.close();

    ((MainController) mainController).buscarTodosLosClientes();
    this.cerrarVentana();
  }

  @FXML
  private void onActionBtnCancelar(ActionEvent event) {
    uiEditarClienteController.cerrarVentana();

  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCancelar.getScene().getWindow();
    stage.close();

  }

  //Seteo el cliente en la UI
  void setCliente(Cliente cliente) {

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }
}
