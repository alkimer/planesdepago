package com.planesdepago.uiControllers;

import com.planesdepago.main2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class UIMainIntegradoController extends AbstractController implements Initializable {

  private main2 main;

  @FXML
  private Button button;
  @FXML private BorderPane root;
  @FXML private VBox sideBox;


  @FXML
  private void onActionButton(ActionEvent event) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI_CrearCliente.fxml"));
    // TabPane pane = null;

    //
    AnchorPane pane = null;
    try {
      pane = (AnchorPane) loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    UICrearClienteController cvc = loader.getController();
    cvc.setMain(main);

    main.getBorderPane().setLeft(pane);
  }
  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void setMain(main2 main) {
    this.main = main;
  }
}
