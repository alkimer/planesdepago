package com.planesdepago.dao;

import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;

import javax.persistence.EntityManager;

public class CompraDao extends AbstractFacade {

  public CompraDao(EntityManager entityManager) {
    super(Compra.class, entityManager);
  }
}
