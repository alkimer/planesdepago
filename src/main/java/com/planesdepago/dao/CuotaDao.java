package com.planesdepago.dao;

import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class CuotaDao extends AbstractFacade {
  public CuotaDao(EntityManager entityManager) {
    super(Pago.class, entityManager);
  }

}
