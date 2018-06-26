package com.planesdepago.util;

import static com.planesdepago.uiutils.Constantes.DB_BACKUP_FOLDER;

import com.planesdepago.uiutils.Constantes;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alkim on 6/25/2018.
 */
public class ArchivosUtils {
  public static final SimpleDateFormat todaysDate = new java.text.SimpleDateFormat("yyyy-MM-dd");



  /*
 recorro la carpeta de backups, y si la cantidad de backups es mayor a MAX_CANTIDAD_BACKUPS, borro los mas viejos
 */
  public void sanitizarBackupFolder() {
    int maxCantidadBackups;
    maxCantidadBackups = Integer.valueOf(PropertiesUtils.readProperty("maxCantidadBackups"));
    List<Path> result = obtenerListadoArchivos(DB_BACKUP_FOLDER);
    List<Path> listaBackupsAutomaticos = new ArrayList<>();

    //Obtuve el listado de archivos, pero están incluidos los backups que hizo el usuario por pedido, estos los debo
    // quitar. Sólo lo dejo los automáticos, que son los que comienzan con el IDENTIFICADOR_BACKUP_AUTOMATICO.

for (Path backup: result) {
  if (backup.getFileName().toString().startsWith(Constantes.IDENTIFICADOR_BACKUP_AUTOMATICO)) {
    listaBackupsAutomaticos.add(backup);
  }
}


    int cantABorrar = listaBackupsAutomaticos.size() - maxCantidadBackups;
    if (cantABorrar > 0) {
      for (int i = 0; i < cantABorrar; i++) {
        deleteDirectory(listaBackupsAutomaticos.get(i).toFile());
      }
    }

  }

  boolean deleteDirectory(File directoryToBeDeleted) {
    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        deleteDirectory(file);
      }
    }
    return directoryToBeDeleted.delete();
  }

  public static List<Path> obtenerListadoArchivos(String carpeta) {

    List<Path> result = new ArrayList<>();
    //Si cambie la carpeta, la primera vez no va a existir, entonces la creo
    if (!(Paths.get(carpeta).toFile().exists())) {
      try {
        Files.createFile(Files.createDirectories(Paths.get(carpeta)));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(carpeta))) {
        for (Path entry : stream) {
          result.add(entry);
        }
        return result;


      }
    } catch (IOException e1) {
      e1.printStackTrace();
      return result;
    }
  }
}
