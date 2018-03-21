package com.planesdepago.uiControllers;

import static com.planesdepago.uiUtils.Constantes.DB_BACKUP_FOLDER;
import static com.planesdepago.util.DatabaseUtils.restoreDatabase;

import com.planesdepago.tableRows.BackupsFolders;
import com.planesdepago.uiUtils.Constantes;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.util.ArchivosUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UIGestorBackupsController extends AbstractController implements Initializable {

  @FXML
  TableView<BackupsFolders> tvListaBackups;
  @FXML
  private Button btnRestaurarBackup;
  @FXML
  private Button btnCancelar;
  @FXML
  private TableColumn<BackupsFolders, String> tcPath;
  @FXML
  private TextField tfCarpetaBackups;

  private String backupSeleccionado;

  @FXML
  private void onActionBtnRestaurarBackup(ActionEvent event) {

    String backupdirectory = DB_BACKUP_FOLDER + backupSeleccionado;
    try {
      restoreDatabase(backupdirectory);
      DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Información", "Restauración exitosa",
          "Se cerrará la aplicación, vuelva a ejecutarla para ver los cambios. Se restauró:  " + backupdirectory);
      Platform.exit();
      System.exit(0);

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  @FXML
  private void onActionBtnCancelar(ActionEvent event) {
    this.cerrarVentana();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tcPath.setCellValueFactory(new PropertyValueFactory<BackupsFolders, String>("folder"));
    this.tfCarpetaBackups.setText(Constantes.DB_BACKUP_FOLDER);
    // Agrego oyente de cambios de selección en la lista,
    tvListaBackups.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      int selectedIndex = tvListaBackups.getSelectionModel().getSelectedIndex();
      if (selectedIndex >= 0) {
        backupSeleccionado = tvListaBackups.getItems().get(selectedIndex).getFolder();
      }

    });

    this.crearTablaDeBackups();


  }

  private List<BackupsFolders> obtenerListadoBackups() {
    List<Path> listaCarpetas = ArchivosUtils.obtenerListadoArchivos(Constantes.DB_BACKUP_FOLDER);
    BackupsFolders backupsFolders;
    List<BackupsFolders> listaBackupFolders = new ArrayList<BackupsFolders>();
    for (Path carpeta : listaCarpetas) {
      backupsFolders = new BackupsFolders();
      backupsFolders.setFolder(carpeta.getFileName().toString());
      listaBackupFolders.add(backupsFolders);

    }
    return listaBackupFolders;
  }

  private void crearTablaDeBackups() {
    ObservableList<BackupsFolders> backupsFolders = FXCollections.observableArrayList();
    backupsFolders.addAll(obtenerListadoBackups());
    tvListaBackups.setItems(backupsFolders);
  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCancelar.getScene().getWindow();
    stage.close();

  }
}
