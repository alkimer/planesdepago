package com.planesdepago.uiUtils;


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
