package com.planesdepago.uiControllers;


import com.planesdepago.entities.Cliente;
import com.planesdepago.util.ApplicationContext;

import javafx.fxml.FXML;
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
  TextField tfLocalidad;
  @FXML
  TextField tfProvincia;
  @FXML
  TextField tfCelular;
  @FXML
  TextField tfTelFijo;


  //Mapeo un objeto cliente en la UI
  void mapCliente2UI(Cliente cliente) {

    tfRazonSocial.setText(cliente.getRazonSocial());
    tfCuit.setText(cliente.getCuit());
    tfCelular.setText(cliente.getCelular());
    tfDireccion.setText(cliente.getDireccion());
    tfLocalidad.setText(cliente.getLocalidad());
    tfProvincia.setText(cliente.getProvincia());
    tfTelFijo.setText(cliente.getTelefonoFijo());

  }

  //Creo un objeto cliente a partir de la UI
  Cliente mapUI2Cliente() {
    Cliente cliente = new Cliente();
    cliente.setTelefonoFijo(this.tfTelFijo.getText());
    cliente.setRazonSocial(this.tfRazonSocial.getText());
    cliente.setProvincia(this.tfProvincia.getText());
    cliente.setLocalidad(this.tfLocalidad.getText());
    cliente.setCelular(this.tfCelular.getText());
    cliente.setCuit(this.tfCuit.getText());
    cliente.setDireccion(this.tfDireccion.getText());

    return cliente;

  }
}
