package com.planesdepago.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Cheque {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer idCheque;
  private LocalDate fechaVencimiento;

  private int nroCheque;
  private String banchoEmisor;

  @ManyToOne(fetch = FetchType.EAGER)
  private Pago idPago;

  public LocalDate getFechaVencimiento() {
    return fechaVencimiento;
  }

  public void setFechaVencimiento(LocalDate fechaVencimiento) {
    this.fechaVencimiento = fechaVencimiento;
  }

  public int getNroCheque() {
    return nroCheque;
  }

  public void setNroCheque(int nroCheque) {
    this.nroCheque = nroCheque;
  }

  public String getBanchoEmisor() {
    return banchoEmisor;
  }

  public void setBanchoEmisor(String banchoEmisor) {
    this.banchoEmisor = banchoEmisor;
  }

  public Pago getIdPago() {
    return idPago;
  }

  public void setIdPago(Pago idPago) {
    this.idPago = idPago;
  }

  public Integer getIdCheque() {
    return idCheque;
  }

  public void setIdCheque(Integer idCheque) {
    this.idCheque = idCheque;
  }
}
