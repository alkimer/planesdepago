package com.planesdepago.uiControllers;


import com.planesdepago.entities.Cliente;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.uiUtils.ListaLocalidades;
import com.planesdepago.util.ApplicationContext;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.persistence.EntityManager;

public abstract class AbstractClientController extends AbstractController {
  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;


  @FXML
  TextField tfRazonSocial;
  @FXML
  TextField tfCuit;
  @FXML
  TextField tfDireccion;

  @FXML
  TextField tfProvincia;
  @FXML
  TextField tfCelular;
  @FXML
  TextField tfTelFijo;

  @FXML
  ComboBox<ListaLocalidades> cbLocalidad;

  //Mapeo un objeto cliente en la UI
  void mapCliente2UI(Cliente cliente) {

    tfRazonSocial.setText(cliente.getRazonSocial());
    tfCuit.setText(cliente.getCuit());
    tfCelular.setText(cliente.getCelular());
    tfDireccion.setText(cliente.getDireccion());
    cbLocalidad.setValue(ListaLocalidades.valueOf(cliente.getLocalidad()));
    tfProvincia.setText(cliente.getProvincia());
    tfTelFijo.setText(cliente.getTelefonoFijo());

  }

  //Creo un objeto cliente a partir de la UI
  Cliente mapUI2Cliente() {
    Cliente cliente = new Cliente();
    cliente.setTelefonoFijo(this.tfTelFijo.getText());
    cliente.setRazonSocial(this.tfRazonSocial.getText());
    cliente.setProvincia(this.tfProvincia.getText());
    cliente.setLocalidad(this.cbLocalidad.getValue().name());
    cliente.setCelular(this.tfCelular.getText());
    cliente.setCuit(this.tfCuit.getText());
    cliente.setDireccion(this.tfDireccion.getText());

    return cliente;

  }

  /*
  contro del obligatoreidad de campos
   */
  boolean camposObligatoriosValidosParaCliente() {

    if (tfCuit.getText().trim().equals("") ) {
    DialogPopUp.crearDialogo(
        Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
        "El campo Cuit/DNI es obligatorio");
    return false;
  }

    if (tfRazonSocial.getText().trim().equals("") ) {
    DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
        "El campo Razón Social es obligatorio");
    return false;
  }

    if (this.cbLocalidad.getValue() == null ) {
    DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
        "El campo Localidad es obligatorio");
    return false;
  }
    return true;
}
}
