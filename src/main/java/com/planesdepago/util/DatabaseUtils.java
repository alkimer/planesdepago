package com.planesdepago.util;

import static java.awt.SystemColor.text;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class DatabaseUtils {

  final static String DB_URL = "jdbc:derby:misueniohogar";
  final static String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
  final static String DB_BACKUP_FOLDER = "C:/BACKUP_PlanesDePago/";
  final static SimpleDateFormat todaysDate =
      new java.text.SimpleDateFormat("yyyy-MM-dd");



  public static void backUpDatabase()throws SQLException
  {

    String backupPath = "C:\\mybacksup\\2015-05-02\\OOS_db";
   // String dbUrl = "jdbc:derby:misueniohogar;restoreFrom=" + backupPath;

    try {
      Class.forName(DB_DRIVER); // throws ClassNotFoundException
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    Connection conn = DriverManager.getConnection(DB_URL);
    //System.out.println("The database has been successfully restored")

    // Get today's date as a string:

    String backupdirectory = DB_BACKUP_FOLDER +
        todaysDate.format((java.util.Calendar.getInstance()).getTime());
//TODO : qué hago con los backups ? uno por día cuando apenas se inicia ??

    try (CallableStatement cs = conn.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)")) {
      cs.setString(1, backupdirectory);
      cs.execute();
      cs.close();
    }

    System.out.println("Base de Datos se ha resguardado en:  "+backupdirectory);
  }

  public static void restoreDatabase()throws SQLException{
    try {
      Class.forName(DB_DRIVER); // throws ClassNotFoundException
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    try {
     DriverManager.getConnection(DB_URL+ ";shutdown=true");
    } catch (SQLException se) {
      if ( se.getSQLState().equals("08006") )
        System.out.println("Database shut down normally");
      else
        System.out.println("Database did not shut down normally");
    }

    //TODO : El restore y backup funcionan, mejorar el tema de cuantos días, y tambiéna que hace el restore pero
    // luego que cargó los datos viejos , así que hay q refrescar la UI
    String backupdirectory = DB_BACKUP_FOLDER +
        todaysDate.format((java.util.Calendar.getInstance()).getTime());

    String restoreUrl = DB_URL +";restoreFrom=" +backupdirectory+ "/misueniohogar";

    Connection conn = DriverManager.getConnection(restoreUrl);

    conn.commit();
    System.out.println("The database has been successfully restored from: " + restoreUrl);

}

}
