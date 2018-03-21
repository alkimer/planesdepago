package com.planesdepago.services;


import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.util.ApplicationContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;

public class ClienteServices {
  static ApplicationContext context = ApplicationContext.getInstance();
  static EntityManager entityManager;
  static ClienteDao clienteDao;



  public static List<Cliente> obtenerTodosLosClientes() {
    entityManager = context.getEntityManager();
    clienteDao = new ClienteDao(entityManager);

    List<Cliente> listaClientes = clienteDao.findAll();
    clienteDao.close();
    listaClientes.sort(Comparator.comparing(Cliente::getRazonSocial));
    return listaClientes;

  }




}
