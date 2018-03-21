package com.planesdepago.uiControllers;

import com.planesdepago.dao.CompraDao;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.util.ApplicationContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;


public class UINuevoPagoController extends AbstractController implements Initializable {
  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;
  Compra compraSeleccionada;

  @FXML
  private TextField tfDescripcion;

  @FXML
  private TextField tfMontoApagar;

  @FXML
  private Button btnCancelar;

  @FXML
  private Button btnAceptar;

  @FXML
  private void onActionBtnCancelar(ActionEvent event) {

    this.cerrarVentana();

  }

  private BigDecimal saldoRestante;


  @FXML
  private void onActionBtnAceptar(ActionEvent event) {

    //si quiero pagar más de lo que debo...
    if (saldoRestante.compareTo(new BigDecimal(tfMontoApagar.getText())) == -1) {
      DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Error", "Reingrese el monto a pagar",
          "Usted intenta pagar un monto mayor al total adeudado por la compra ($" + this.saldoRestante + ")");

    } else {

      entityManager = context.getEntityManager();

      Pago pago = new Pago();
      pago.setFechaPago(LocalDate.now());
      pago.setDescripcionPago(tfDescripcion.getText());
      pago.setMontoPagado(new BigDecimal(tfMontoApagar.getText()));

      CompraDao compraDao = new CompraDao(entityManager);
      compraSeleccionada.addPago(pago);
      compraSeleccionada.setSaldoRestante(compraSeleccionada.getSaldoRestante().subtract(pago.getMontoPagado()));
      compraDao.edit(compraSeleccionada);
      compraDao.close();
      DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Confirmación", "Nuevo Pago ingresado",
          "Se ha ingresado el nuevo pago en la base de datos");


      ((UIVerPagosYcuotasController)mainController).buscarCuotasYpagos();
      this.cerrarVentana();
    }
  }
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void init(Compra compraSeleccionada, BigDecimal saldoRestante) {

    this.saldoRestante = saldoRestante;
    this.compraSeleccionada = compraSeleccionada;
  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCancelar.getScene().getWindow();
    stage.close();

  }
}
