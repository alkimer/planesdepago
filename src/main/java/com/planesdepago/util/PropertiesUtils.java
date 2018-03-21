package com.planesdepago.util;

import com.planesdepago.uiUtils.Constantes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by alkim on 6/25/2018.
 */
public class PropertiesUtils {

  public static String readProperty(String property) {
  //  File configFile = new File(Constantes.PATH_PROPERTIES_FILE +"config.properties");

   InputStream configFile = ClassLoader.getSystemResourceAsStream("config.properties");

    try {
 //     FileReader reader = new FileReader(configFile);
      Properties props = new Properties();
      props.load(configFile);


      configFile.close();
      return props.getProperty(property);
    } catch (FileNotFoundException ex) {
      return null;
      // file does not exist
    } catch (IOException ex) {
      // I/O error
      return null;

    }
  }

   /* public static void setProperty(String property, String value) {
   //   File configFile = new File(Constantes.PATH_PROPERTIES_FILE +"config.properties");
      InputStream configFile = ClassLoader.getSystemResourceAsStream("config.properties");

      try {
        Properties props = new Properties();
        props.setProperty(property,value);
    //   FileWriter writer = new FileWriter(configFile);
      //  props.store(writer, property);
      //  props.store(new FileOutputStream("xyz.properties"), null);

       // writer.close();
      } catch (FileNotFoundException ex) {
        // file does not exist
      } catch (IOException ex) {
        // I/O error
      }
  }
*/


}
