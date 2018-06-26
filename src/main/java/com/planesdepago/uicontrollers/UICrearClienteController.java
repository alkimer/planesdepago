package com.planesdepago.uicontrollers;

import com.planesdepago.GestionPrestamos;
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

public class UICrearClienteController extends AbstractClientController implements Initializable {

  //private MainController mainController;
  private ClienteDao clienteDao;

  @FXML
  private Button btnInsertar;


  @FXML
  private Button btnCancelar;

  @FXML
  private void onActionBtnCancelar(ActionEvent event) {
    this.cerrarVentana();
  }

  @FXML
  private void actionBtnInsertar(ActionEvent event) {
    if (camposObligatoriosValidosParaCliente()) {
      entityManager = context.getEntityManager();
      clienteDao = new ClienteDao(entityManager);
      Cliente clienteEntity = mapUI2Cliente();


      if (clienteDao.exists(tfCuit.getText().trim())) {
        DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Cliente existente",
            "Ya existe un cliente en la base" + " de datos con el CUIT que usted ingresó");

      } else {

        clienteDao.create(clienteEntity);
        DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Confirmación", "Nuevo cliente creado",
            "Se ha " + "insertado el nuevo cliente en la base de datos");
      }

      clienteDao.close();

      ((UIMainIntegradoController) mainController).refrescarListadoClientes();
      this.cerrarVentana();
    }
  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {

    cbLocalidad.getItems().setAll(ListaLocalidades.values());
  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCancelar.getScene().getWindow();
    stage.close();

  }

}
