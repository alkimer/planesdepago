/**
 *
 */
package com.planesdepago;

import static com.planesdepago.util.PdfUtils.deletePdfFiles;

import com.planesdepago.uiControllers.UIMainIntegradoController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionPrestamos extends Application {
  @FXML
  private BorderPane root;

  Stage window;

  @Override
  public void start(Stage primaryStage) throws IOException {
    //Borra archivos pdfs que hayan quedado de sesiones anteriores
    deletePdfFiles();

    window = primaryStage;
    FXMLLoader loader = new FXMLLoader(GestionPrestamos.class.getResource("/UI_MainIntegrado.fxml"));

    this.root = loader.load();

    UIMainIntegradoController mwc = loader.getController();
    mwc.setMain(this);

    mwc.cargaInicial();

    Scene scene = new Scene(root);
    window.setTitle("Gestión de Préstamos");
    window.setScene(scene);
    UIMainIntegradoController.hostServices = getHostServices();
    //Se cierra la sesión completa (BD) al cerra la ventana

    window.setOnCloseRequest(e -> {
      Platform.exit();
      System.exit(0);
    });

    window.show();
  }


  public BorderPane getBorderPane() {
    return root;
  }
}
