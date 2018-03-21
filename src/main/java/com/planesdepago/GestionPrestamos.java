/**
 *
 */
package com.planesdepago;

import static com.planesdepago.uiUtils.Constantes.DB_BACKUP_FOLDER;
import static com.planesdepago.util.PdfUtils.deletePdfFiles;
import static com.planesdepago.util.DatabaseUtils.backUpDatabase;

import com.planesdepago.uiControllers.UIMainIntegradoController;
import com.planesdepago.uiUtils.Constantes;
import com.planesdepago.util.ArchivosUtils;
import com.planesdepago.util.DatabaseUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GestionPrestamos extends Application {
  @FXML
  private BorderPane root;

  Stage window;

  @Override
  public void start(Stage primaryStage) throws IOException, Exception {
    //Los backups que empiezaon on IDENTIFICADOR_BACKUP_AUTOMATICO son los generados automaticamente, los otros son
    // los que creó el usuario.
    String backupdirectory = DB_BACKUP_FOLDER + Constantes.IDENTIFICADOR_BACKUP_AUTOMATICO + ArchivosUtils.todaysDate
        .format((java.util.Calendar
        .getInstance())
        .getTime());


    //Borra archivos pdfs que hayan quedado de sesiones anteriores
    deletePdfFiles();
    //Sanitizo la carpeta de backups, para borrar los que sean viejos
    ArchivosUtils archUtils = new ArchivosUtils();
    archUtils.sanitizarBackupFolder();
    //Crear backup
    //si no existe el backup lo creo
    //la idea es que la primera ejecución de cada dia se cree el backup.
   if (Files.notExists(Paths.get(backupdirectory))) {

      backUpDatabase(backupdirectory);
    }

      window = primaryStage;
      FXMLLoader loader = new FXMLLoader(GestionPrestamos.class.getResource("/UI_MainIntegrado.fxml"));

      window.getIcons().add(new Image(Constantes.IMAGEN_LOGO));

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
