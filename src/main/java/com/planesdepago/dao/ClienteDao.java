package com.planesdepago.dao;

import com.planesdepago.entities.Cliente;

import javax.persistence.EntityManager;

public class ClienteDao extends AbstractFacade<Cliente>{

  public ClienteDao(EntityManager entityManager) {
    super(Cliente.class,entityManager);
  }
}
