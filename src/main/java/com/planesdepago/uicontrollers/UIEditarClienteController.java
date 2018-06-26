package com.planesdepago.uicontrollers;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.uiutils.DialogPopUp;
import com.planesdepago.uiutils.ListaLocalidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UIEditarClienteController extends AbstractClientController implements Initializable {

  @FXML
  private Button btnGuardar;
  @FXML
  private Button btnCancelar;

  @FXML
  private void onActionBtnGuardar(ActionEvent event) {
    if (camposObligatoriosValidosParaCliente()) {
      entityManager = context.getEntityManager();
      ClienteDao clienteDao = new ClienteDao(entityManager);
      Cliente clienteEntity = mapUI2Cliente();
      clienteDao.edit(clienteEntity);
      DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Confirmación", "Edición exitosa",
          "Se ha editado la información del cliente seleccionado");
      clienteDao.close();

      ((UIListadoClientesController) mainController).createTableListaClientes();
      this.cerrarVentana();
    }
  }

  @FXML
  private void onActionBtnCancelar(ActionEvent event) {
    cerrarVentana();

  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCancelar.getScene().getWindow();
    stage.close();

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    cbLocalidad.getItems().setAll(ListaLocalidades.values());

  }
}
