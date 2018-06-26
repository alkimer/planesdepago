package com.planesdepago.uicontrollers;

import static com.planesdepago.uiutils.Constantes.DB_BACKUP_FOLDER;
import static com.planesdepago.util.DatabaseUtils.backUpDatabase;

import com.planesdepago.uiutils.DialogPopUp;
import com.planesdepago.util.ArchivosUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UICrearBackupController extends AbstractClientController implements Initializable {
  @FXML
  private TextField tfNombreBackup;
  @FXML
  private Button btnCancelar;
  @FXML
  private Button btnCrearBackup;

  @FXML
  private void onActionBtnCancelar(ActionEvent event) {

    this.cerrarVentana();
  }

  @FXML
  private void onActionBtnCrearBackup(ActionEvent event) {

    try {
      String backupdirectory =
          DB_BACKUP_FOLDER + ArchivosUtils.todaysDate.format((java.util.Calendar.getInstance()).getTime()) + "-"
              + tfNombreBackup.getText();
      backUpDatabase(backupdirectory);
      this.cerrarVentana();

    } catch (SQLException e) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Hubo un error al crear la copia de respaldo", "");
      e.printStackTrace();
    }


  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCancelar.getScene().getWindow();
    stage.close();

  }
}
