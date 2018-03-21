package com.planesdepago.uiControllers;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.util.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sun.applet.Main;

import java.net.URL;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UICrearClienteController  extends AbstractClientController implements Initializable{

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

    ((MainController) mainController).buscarTodosLosClientes();
    this.cerrarVentana();
  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCancelar.getScene().getWindow();
    stage.close();

  }

}
