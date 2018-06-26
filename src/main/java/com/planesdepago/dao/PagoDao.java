package com.planesdepago.dao;

import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiutils.Constantes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class PagoDao extends AbstractFacade {
  public PagoDao(EntityManager entityManager) {
    super(Pago.class, entityManager);
  }

  public List<Pago> getPagosDiarios(LocalDate fecha) {
    Query query = getEntityManager().createQuery("Select e FROM Pago e WHERE e.fechaPago = ?1");
    query.setParameter(1, fecha);
    return query.getResultList();

  }

  /*
  Dada una compra , obtiene el pago correspondiente a su anticipo
   */
  public Pago getAnticipo(Compra compra) {
    Pago result;
    try {
      Query query = getEntityManager().createQuery("Select e FROM Pago e WHERE e.compraID = ?1 and e.descripcionPago = ?2");
      query.setParameter(1, compra);
      query.setParameter(2, Constantes.ANTICIPO);
    result =  (Pago) query.getSingleResult();
    } catch (NoResultException nrex) {
      result = new Pago();
      result.setMontoPagado(BigDecimal.ZERO);
    }
    return result;
  }
}
