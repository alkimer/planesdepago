package com.planesdepago.uiutils;

import com.planesdepago.util.PropertiesUtils;

public class ListaTarjetas{
  public static String[] values() {
    String localidades = PropertiesUtils.readProperty("Tarjetas");

    return localidades.split(",");
  }

  public static String valueOf(String value) {
    return value;
  }


}
/*
public enum ListaTarjetas {ARGENTA,CABAL,MAESTRO,MASTERCARD,NATIVA,VISA
}
*/