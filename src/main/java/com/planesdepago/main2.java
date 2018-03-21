/**
 *
 */
package com.planesdepago;

import com.planesdepago.uiControllers.UIMainIntegradoController;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class main2 extends Application {
  @FXML
  private BorderPane root;

  Stage window;

  @Override
  public void start(Stage primaryStage) throws IOException {
    window = primaryStage;
    FXMLLoader loader = new FXMLLoader(main2.class.getResource("/UI_MainIntegrado.fxml"));

    this.root = loader.load();

    UIMainIntegradoController mwc = loader.getController();
    mwc.setMain(this);

    Scene scene = new Scene(root);
    window.setTitle("JavaFX");
    window.setScene(scene);
    window.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  public BorderPane getBorderPane() {
    return root;
  }
}
