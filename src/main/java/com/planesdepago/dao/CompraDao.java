package com.planesdepago.dao;

import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class CompraDao extends AbstractFacade {

  public CompraDao(EntityManager entityManager) {
    super(Compra.class, entityManager);
  }

  public List<Compra> getComprasConDeuda() {
    Query query = getEntityManager().createQuery("Select e FROM Compra e WHERE e.saldoRestante > 0");
    return query.getResultList();

  }

}
