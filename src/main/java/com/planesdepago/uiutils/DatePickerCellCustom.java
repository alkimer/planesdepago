package com.planesdepago.uiutils;

import com.planesdepago.entities.Cuota;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

import java.time.LocalDate;

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