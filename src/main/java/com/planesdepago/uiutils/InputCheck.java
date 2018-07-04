package com.planesdepago.uiutils;

import com.planesdepago.entities.Cuota;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.math.BigDecimal;

public class InputCheck {

  private InputCheck() {
    throw new IllegalStateException("Utility class shouldn't be instantiated.");

  }

  /*
  Chequeo que no tenga dos o mas puntos decimales
   */
  public static boolean formatoNumericoCorrecto(String value) {
    int cont = 0;
    for (int i = 0; i < value.length(); i++) {

      if (value.charAt(i) == Constantes.SEPARADOR_DECIMAL) cont++;
    }

    return cont < 2;
  }

  /*
  Controlo que el patron sea valido , y no sea mas largo que maxCantChars.
   */
  public static void agregarControlesAEntradaDeCifras(TextField tf, int maxCantChars) {
    tf.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      procesarIngresoMonto(e);
    });
    tf.addEventFilter(KeyEvent.KEY_TYPED, e -> {
      procesarIngresoMonto(e);
    });
    tf.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
      procesarIngresoMonto(e);
    });

    tf.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.length() > maxCantChars) ((StringProperty) observable).setValue(oldValue);
    });
  }

  /*
 Como procesar los eventos de ingreso de montos en la UI
  */
  public static void procesarIngresoMonto(KeyEvent ev) {

    if (Constantes.PATRON_MONTOS_VALIDOS.contains(ev.getCharacter()) || ev.getCode().equals(KeyCode.BACK_SPACE)) {
    } else {
      ev.consume();

    }
  }

  public static void procesarIngresoMonto(TableColumn.CellEditEvent<Cuota, BigDecimal> data) {
    if (Constantes.PATRON_MONTOS_VALIDOS
        .contains(data.getNewValue().toString().substring(data.getNewValue().toString().length()))) {
    } else {

    }
  }
}
