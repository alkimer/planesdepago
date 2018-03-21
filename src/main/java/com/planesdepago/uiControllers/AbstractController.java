package com.planesdepago.uiControllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractController {

  public Stage stage;
  //  MainController mainController;
  Initializable mainController;

  //Método para tener una referencia del controllador que me llama.
  public void init(Initializable mainController) {
    this.mainController = mainController;
  }

  public AbstractController cambiarEscena(
      String title, String resourceLocation, Modality modality, Initializable cont, ActionEvent event) {
  /*
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

    abstractController.
    stage.setScene(new Scene(root));
    stage.setTitle(title);
    stage.initModality(modality);



    //La comenté .. ver
     stage.initOwner(((Node) event.getSource()).getScene().getWindow());
    stage.show();
    return abstractController;
*/
    AbstractController abstractController;
    //  stage = new Stage();
    Parent root = null;
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(resourceLocation));
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    abstractController = loader.getController();
    abstractController.stage = new Stage();

    abstractController.init(cont);

    abstractController.stage.setScene(new Scene(root));
    abstractController.stage.setTitle(title);
    abstractController.stage.initModality(modality);


    //El owner lo necesito para saber qué ventana me llamó, y que funcionen loas window.modal.
    abstractController.stage.initOwner(((Node) event.getSource()).getScene().getWindow());

    abstractController.stage.show();
    return abstractController;
  }

  public Initializable getMainController() {
    return mainController;
  }

  public void setMainController(Initializable mainController) {
    this.mainController = mainController;
  }


  /*
  Utilizada para workaround cuando se usan menuitems en las tableviews cuyo eventsource no hereda de node
   */
  public ActionEvent mapToNodeCompatibleEvent(Control newControl, ActionEvent eventSource) {

    //Cambio event que viene del menuitem por un nuevo event2  que viene desde la tabla,
    // Triquiñuela para cambiar el elemento que generó el evento, ya que el
    // menu item no hereda de Node y no puedo obtener el Window al que pertenece.
    ActionEvent event2 = null;
    return eventSource.copyFor(newControl, eventSource.getTarget());
  }
}
