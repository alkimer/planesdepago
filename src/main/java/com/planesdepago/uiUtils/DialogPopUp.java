package com.planesdepago.uiUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class DialogPopUp {

  public static Optional<ButtonType> crearDialogo(Alert.AlertType alertType, String title, String header, String text) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(text);

    return alert.showAndWait();
  }


}
