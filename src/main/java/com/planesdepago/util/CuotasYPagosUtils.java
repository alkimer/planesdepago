package com.planesdepago.util;


import com.planesdepago.dao.CompraDao;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Cuota;
import com.planesdepago.tableRows.CuotasYpagos;
import com.planesdepago.uiUtils.Constantes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;

public class CuotasYPagosUtils {

  /*
  Computo los pagos realizados y lo comparo con las cuotas, para ver el estado del cliente
  con respecto al plan de pagos
   */
  public static ObservableList<CuotasYpagos> obtenerTablaDeCuotas(
       Compra compra) {
    ApplicationContext context = ApplicationContext.getInstance();
    EntityManager entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);

    compra= (Compra) compraDao.find(compra.getIdTransaccion());

    ObservableList<CuotasYpagos> olCuotasYpagos = FXCollections.observableArrayList();
List<Cuota> listaCuotas = compra.getCuotas();
    BigDecimal totalPagos = compra.getMontoAFinanciar().
        subtract(compra.getSaldoRestante());
    for (Cuota cuota : listaCuotas) {

      CuotasYpagos cuotasYpagos = new CuotasYpagos();
      //Del total que se pagó, voy calculando a qué cuotas correspondería
      if (totalPagos.compareTo(cuota.getMontoCuota()) >= 0) {
        totalPagos = totalPagos.subtract(cuota.getMontoCuota());
        cuotasYpagos.setCuotaPaga(Constantes.CUOTA_PAGA);
      } else if ((totalPagos.compareTo(cuota.getMontoCuota()) == -1) && (totalPagos.compareTo(BigDecimal.ZERO) != 0)) {
        cuotasYpagos.setCuotaPaga(Constantes.CUOTA_PAGO_PARCIAL + totalPagos);
        totalPagos = BigDecimal.ZERO;
      } else {
        cuotasYpagos.setCuotaPaga(Constantes.CUOTA_NO_PAGA);
      }
//TODO: ver que los deciamles son con puntos y deberían ser comas.
      cuotasYpagos.setMontoCuota(cuota.getMontoCuota());
      cuotasYpagos.setNroCuota(cuota.getNroCuota());
      cuotasYpagos.setFechaVencimiento(cuota.getFechaVencimiento());
      cuotasYpagos.setDescripcion(cuota.getDescripcion());
      olCuotasYpagos.add(cuotasYpagos);

    }
    compraDao.close();
    return olCuotasYpagos;
  }
}
