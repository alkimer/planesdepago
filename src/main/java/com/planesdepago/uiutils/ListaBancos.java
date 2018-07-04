package com.planesdepago.uiutils;

import com.planesdepago.util.PropertiesUtils;

/**
 * Created by alkim on 7/5/2018.
 */
public class ListaBancos {

  public static String[] values() {
    String localidades = PropertiesUtils.readProperty("Bancos");

    return localidades.split(",");
  }

  public static String valueOf(String value) {
    return value;
  }

}

