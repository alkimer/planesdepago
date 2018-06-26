package com.planesdepago.services;


import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.util.ApplicationContext;

import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;

public class ClienteServices {
  static ApplicationContext context = ApplicationContext.getInstance();
  static EntityManager entityManager;
  static ClienteDao clienteDao;

private ClienteServices() {
  throw new IllegalStateException("Utility class shouldn't be instantiated.");
}

  public static List<Cliente> obtenerTodosLosClientes() {
    entityManager = context.getEntityManager();
    clienteDao = new ClienteDao(entityManager);

    List<Cliente> listaClientes = clienteDao.findAll();
    clienteDao.close();
    listaClientes.sort(Comparator.comparing(Cliente::getRazonSocial));
    return listaClientes;

  }




}
