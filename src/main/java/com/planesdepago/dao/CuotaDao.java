package com.planesdepago.dao;

import com.planesdepago.entities.Pago;

import javax.persistence.EntityManager;

public class CuotaDao extends AbstractFacade {
  public CuotaDao(EntityManager entityManager) {
    super(Pago.class, entityManager);
  }

}
