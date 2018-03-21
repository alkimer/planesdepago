package com.planesdepago.uiUtils;

import com.planesdepago.entities.Cuota;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

public class DatePickerCellCustom<S, T> extends TableCell<Cuota, LocalDate> {
  private DatePicker datePicker;
  private ObservableList<Cuota> listaCuotas;

  public DatePickerCellCustom(ObservableList<Cuota> listaCuotas) {

    super();

    this.listaCuotas = listaCuotas;

    if (datePicker == null) {
      createDatePicker();
    }
    setGraphic(datePicker);
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        datePicker.requestFocus();
      }
    });
  }

  @Override
  public void updateItem(LocalDate item, boolean empty) {

    super.updateItem(item, empty);

    SimpleDateFormat smp = new SimpleDateFormat(DateUtils.FORMATO_FECHA);

    if (null == this.datePicker) {
      System.out.println("datePicker is NULL");
    }

    if (empty) {
      setText(null);
      setGraphic(null);
    } else {

      if (isEditing()) {
        setContentDisplay(ContentDisplay.TEXT_ONLY);

      } else {
        datePicker.setValue(item);
        setGraphic(this.datePicker);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      }
    }
  }

  private void setDatepikerDate(String dateAsStr) {

    LocalDate ld = null;
    int jour, mois, annee;

    jour = mois = annee = 0;
    try {
      jour = Integer.parseInt(dateAsStr.substring(0, 2));
      mois = Integer.parseInt(dateAsStr.substring(3, 5));
      annee = Integer.parseInt(dateAsStr.substring(6, dateAsStr.length()));
    } catch (NumberFormatException e) {
      System.out.println("setDatepikerDate / unexpected error " + e);
    }

    ld = LocalDate.of(annee, mois, jour);
    datePicker.setValue(ld);
  }

  private void createDatePicker() {
    this.datePicker = new DatePicker();
    datePicker.setPromptText("dd/mm/aaaa");
    datePicker.setConverter(DateUtils.formateadorLocalDate());
    datePicker.setEditable(true);
    datePicker.setOnAction(new EventHandler() {
      public void handle(Event t) {
        LocalDate date = datePicker.getValue();
       int index = getIndex();
      commitEdit(date);
        if (null != getListaCuotas()) {
          getListaCuotas().get(index).setFechaVencimiento(date);
        }
      }
    });

    setAlignment(Pos.CENTER);
  }

  @Override
  public void startEdit() {
    super.startEdit();
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    setContentDisplay(ContentDisplay.TEXT_ONLY);
  }

  public ObservableList<Cuota> getListaCuotas() {
    return listaCuotas;
  }

  public void setListaCuotas(ObservableList<Cuota> listaCuotas) {
    this.listaCuotas = listaCuotas;
  }

  public DatePicker getDatePicker() {
    return datePicker;
  }

  public void setDatePicker(DatePicker datePicker) {
    this.datePicker = datePicker;
  }
}