package com.planesdepago.util;

import static com.planesdepago.uiutils.Constantes.DB_DRIVER;
import static com.planesdepago.uiutils.Constantes.DB_URL;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {

  private DatabaseUtils() {
    throw new IllegalStateException("Utility class shouldn't be instantiated.");

  }

  public static void backUpDatabase(String backupdirectory) throws SQLException {

   try{
        Class.forName(DB_DRIVER); // throws ClassNotFoundException
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      Connection conn = DriverManager.getConnection(DB_URL);

      try (CallableStatement cs = conn.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)")) {
        cs.setString(1, backupdirectory);
        cs.execute();
      }

      System.out.println("Base de Datos se ha resguardado en:  " + backupdirectory);
    }


  public static void restoreDatabase(String backupdirectory ) throws SQLException {
    try {
      Class.forName(DB_DRIVER); // throws ClassNotFoundException
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    try {
      DriverManager.getConnection(DB_URL + ";shutdown=true");
    } catch (SQLException se) {
      if (se.getSQLState().equals("08006")) {
        System.out.println("Database shut down normally");

      } else {
        System.out.println("Database did not shut down normally");
      }
    }

    // luego que cargó los datos viejos , así que hay q refrescar la UI

    String restoreUrl = DB_URL + ";restoreFrom=" + backupdirectory + "/misueniohogar";

    Connection conn = DriverManager.getConnection(restoreUrl);
    conn.commit();
    System.out.println("The database has been successfully restored from: " + restoreUrl);
  }



}
