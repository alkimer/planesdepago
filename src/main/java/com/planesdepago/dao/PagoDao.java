package com.planesdepago.dao;

import com.planesdepago.entities.Pago;

import javax.persistence.EntityManager;

public class PagoDao extends AbstractFacade {
  public PagoDao(EntityManager entityManager) {
    super(Pago.class, entityManager);
  }

}
