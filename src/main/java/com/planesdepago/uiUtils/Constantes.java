package com.planesdepago.uiUtils;

import com.planesdepago.util.PropertiesUtils;

/**
 * Created by alkim on 6/5/2018.
 */
public class Constantes {

  public static final String ANTICIPO = "Anticipo";
  public static final String SIN_CLIENTE_SELECCIONADO = "Sin cliente seleccionado.";
  public static final String IMAGEN_LOGO = "/misueniohogaricon.png";
  public static final String PATH_PDFS = "./pdfs/";
  public static final String CUOTA_PAGA = "SI";
  public static final String CUOTA_NO_PAGA = "NO";
  public static final String CUOTA_PAGO_PARCIAL = "Parcial, se abonó $";
  public static final String PATRON_MONTOS_VALIDOS = "0123456789.";
  public static final char SEPARADOR_DECIMAL = '.';
  public static final int MAX_LONGITUD_MONTOS = 8;
  public static final int MAX_LONGITUD_PORCENTAJES = 3;
  public static final String TEXTO_STANDARD_DESCRIPCION_PAGO = "Pago cliente.";

  public final static String DB_URL = "jdbc:derby:misueniohogar";
  public final static String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
  public final static String DB_BACKUP_FOLDER = PropertiesUtils.readProperty("dbBackupFolder");
  public final static int MAX_CANTIDAD_BACKUPS = 7;
  public final static String IDENTIFICADOR_BACKUP_AUTOMATICO = "$";
  public final static String PATH_PROPERTIES_FILE = "./src/main/resources/";


}
