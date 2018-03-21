package com.planesdepago.uiControllers;


import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractController {
Stage stage;
  //  MainController mainController;
Initializable mainController;
  //Método para tener una referencia del controllador que me llama.
  public void init(Initializable mainController) {
    this.mainController = mainController;
  }

  public AbstractController cambiarEscena(String title, String resourceLocation, Modality modality, Initializable
      cont) {
    AbstractController abstractController;
    stage = new Stage();
    Parent root = null;
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(resourceLocation));
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    abstractController = loader.getController();
    abstractController.init(cont);

    stage.setScene(new Scene(root));
    stage.setTitle(title);
    stage.initModality(modality);

    //La comenté .. ver
    // stage.initOwner(((Node) event.getSource()).getScene().getWindow());
    stage.show();
    return abstractController;

  }
}
