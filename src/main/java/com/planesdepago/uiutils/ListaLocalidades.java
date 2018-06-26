package com.planesdepago.uiutils;

import com.planesdepago.util.PropertiesUtils;

//public class ListaLocalidades {
/*
  public static String[] values() {
  String localidades = PropertiesUtils.readProperty("Localidades");

    return localidades.split(",");
  }

  public static String valueOf(String value) {
    return value;
  }

}
*/

public enum ListaLocalidades {
  DIECISIETEDEAGOSTO("17 de Agosto"),
  ALPACHIRI("Alpachiri"),
  AZOPARDO("Azopardo"),
  BBLANCA("Bahía Blanca"),
  BORDENAVE("Bordenave"),
  CNASANTATERESA("Col. Santa Teresa"),
  DARREGUEIRA("Darregueira"),
  ESTGASCON("Est. Agustín Gascón"),
  FELIPESOLA("Felipe Solá"),
  GRALSANMARTIN("Gral. San Martin"),
  GUATRACHE("Guatraché"),
  JACINTOARAUZ("Jacinto Arauz"),
  LOPEZLECUBE("Lopez Lecube"),
  MACACHIN("Macachín"),
  PUAN("Puán"),
  RIVERA("Rivera"),
  SANMIGUELARC("San Miguel Arcangel"),
  VILLAIRIS("Villa Iris"),
  OTRO("OTRO");


  private String displayName;

  ListaLocalidades(String displayName) {
    this.displayName = displayName;
  }
  public String displayName() { return displayName; }

  @Override
  public String toString() {
    return this.displayName;
  }


}
