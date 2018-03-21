package com.planesdepago.uiUtils;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by alkim on 6/6/2018.
 */
public class DateUtils {
  public static final String FORMATO_FECHA = "dd-MM-yyyy";
  public static DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern(DateUtils.FORMATO_FECHA);

  public static StringConverter<LocalDate> formateadorLocalDate()

  { return(
    new StringConverter<LocalDate>() {
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(FORMATO_FECHA);

      @Override
      public String toString(LocalDate date) {
        if (date != null) {
          return dateFormatter.format(date);
        } else {
          return "";
        }
      }
      @Override
      public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) {
          return LocalDate.parse(string, dateFormatter);
        } else {
          return null;
        }
      }
    });
    }


  public static String formatLocalDate2StringPattern(LocalDate stringDate) {
    DateTimeFormatter formatters = DateTimeFormatter.ofPattern(FORMATO_FECHA);
    return stringDate.format(formatters);

  }
}