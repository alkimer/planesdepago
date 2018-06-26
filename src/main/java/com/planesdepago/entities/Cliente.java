package com.planesdepago.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Cliente")
public class Cliente implements Serializable{
  private static final long serialVersionUID = 1L;

  private String razonSocial;

  @Id
  private String cuit;
  private String direccion;
  private String localidad;
  private String provincia;
  private String celular;
  private String telefonoFijo;

  @OneToMany(mappedBy = "IDCliente", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<Compra> compras = new ArrayList<>();


  public String getRazonSocial() {
    return razonSocial;
  }

  @Override
  public String toString() {
    return "CUIT" + cuit;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Cliente other = (Cliente) obj;
    if (cuit == null) {
      if (other.cuit != null)
        return false;
    } else if (!cuit.equals(other.cuit))
      return false;

    return true;
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cuit == null) ? 0 : cuit.hashCode());
    result = prime * result + ((razonSocial == null) ? 0 : razonSocial.hashCode());
    return result;
  }

  public void setRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
  }

  public String getCuit() {
    return cuit;
  }

  public void setCuit(String cuit) {
    this.cuit = cuit;
  }

  public String getDireccion() {
    return direccion;
  }

  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  public String getLocalidad() {
    return localidad;
  }

  public void setLocalidad(String localidad) {
    this.localidad = localidad;
  }

  public String getProvincia() {
    return provincia;
  }

  public void setProvincia(String provincia) {
    this.provincia = provincia;
  }

  public String getCelular() {
    return celular;
  }

  public void setCelular(String celular) {
    this.celular = celular;
  }

  public String getTelefonoFijo() {
    return telefonoFijo;
  }

  public void setTelefonoFijo(String telefonoFijo) {
    this.telefonoFijo = telefonoFijo;
  }


  public List<Compra> getCompras() {
    return compras;
  }

  public void addCompra(Compra compra) {
    compras.add(compra);
    compra.setIDCliente(this);

  }


  public void setCompras(List<Compra> compras) {
    for (Compra compra : compras) {

      this.addCompra(compra);

    }
  }


}
