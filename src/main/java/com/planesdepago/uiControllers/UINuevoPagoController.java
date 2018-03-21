package com.planesdepago.uiControllers;

import static com.planesdepago.util.PdfUtils.crearEncabezadoMiSuenioHogar;
import static com.planesdepago.util.PdfUtils.crearRandomPDFFileName;
import static com.planesdepago.util.PdfUtils.crearTitulo;
import static com.planesdepago.util.PdfUtils.mostrarPdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.planesdepago.dao.CompraDao;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiUtils.Constantes;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.uiUtils.InputCheck;
import com.planesdepago.util.ApplicationContext;
import com.planesdepago.util.PDFeventListener;
import com.planesdepago.util.PdfUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;


public class UINuevoPagoController extends AbstractController implements Initializable {
  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;
  Compra compraSeleccionada;

  @FXML
  private TextField tfDescripcion;

  @FXML
  private TextField tfMontoApagar;

  @FXML
  private Button btnCancelar;

  @FXML
  private Button btnAceptar;

  @FXML
  private CheckBox cbAnticipo;

  @FXML
  private void onActionBtnCancelar(ActionEvent event) {

    this.cerrarVentana();

  }

  private BigDecimal saldoRestante;


  @FXML
  private void onActionCbAnticipoClick(ActionEvent event) {

    if (this.cbAnticipo.isSelected()) {
      this.tfDescripcion.setText(Constantes.ANTICIPO);
      this.tfDescripcion.setEditable(false);
    } else {
      this.tfDescripcion.setText("");
      this.tfDescripcion.setEditable(true);

    }
  }


  @FXML
  private void onActionBtnAceptar(ActionEvent event) {
   if( camposObligatoriosValidosParaIngresarPago()) {
     //si quiero pagar más de lo que debo...
     if (saldoRestante.compareTo(new BigDecimal(tfMontoApagar.getText())) == -1) {
       DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Error", "Reingrese el monto a pagar",
           "Usted intenta pagar un monto mayor al total adeudado por la compra ($" + this.saldoRestante + ")");

     } else {


       Pago pago = new Pago();
       pago.setFechaPago(LocalDate.now());
       if (tfDescripcion.getText().trim().equals("")) {
         pago.setDescripcionPago(Constantes.TEXTO_STANDARD_DESCRIPCION_PAGO);
       } else {
         pago.setDescripcionPago(tfDescripcion.getText());
       }
       pago.setMontoPagado(new BigDecimal(tfMontoApagar.getText()));

       entityManager = context.getEntityManager();
       CompraDao compraDao = new CompraDao(entityManager);
       //  compraSeleccionada = (Compra)compraDao.find(compraSeleccionada.getIdTransaccion());
       compraSeleccionada.addPago(pago);
       compraSeleccionada.setSaldoRestante(compraSeleccionada.getSaldoRestante().subtract(pago.getMontoPagado()));
       compraDao.edit(compraSeleccionada);
       compraDao.close();
       DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Confirmación", "Nuevo Pago ingresado",
           "Se ha ingresado el nuevo pago en la base de datos");
       this.imprimirPago();

       ((UIVerEstadoPagosController) mainController).buscarCuotasYpagos();
       this.cerrarVentana();
     }
   }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    InputCheck.agregarControlesAEntradaDeCifras(tfMontoApagar,Constantes.MAX_LONGITUD_MONTOS);
  }

  public void init(Compra compraSeleccionada, BigDecimal saldoRestante) {

    this.saldoRestante = saldoRestante;
    this.compraSeleccionada = compraSeleccionada;

    entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);
    compraSeleccionada = (Compra) compraDao.find(compraSeleccionada.getIdTransaccion());
    if (compraSeleccionada.getPagos().size() > 0) {
      this.cbAnticipo.setDisable(true);
    }
    compraDao.close();

  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCancelar.getScene().getWindow();
    stage.close();

  }

  private void imprimirPago() {
    Document document = new Document(PageSize.A4);
    String fileName = crearRandomPDFFileName("Comprobante Pago");
    PdfWriter writer = new PdfUtils().createWriter(document, fileName);
    writer.setPageEvent(new PDFeventListener());

    document.open();
    crearEncabezadoMiSuenioHogar(document);
    crearTitulo(document, "Comprobante de Pago");

    PdfPTable tableComprobantePago = new PdfPTable(2);
    tableComprobantePago.setTotalWidth(PageSize.A4.getWidth() * 0.9f);
    tableComprobantePago.setLockedWidth(true);

    entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);
    compraSeleccionada = (Compra) compraDao.find(compraSeleccionada.getIdTransaccion());

    compraDao.close();
    // crearHeader(tablePlanPagos, new String[]{"Nombre", "Compra", "Monto pagado", "Fecha", "Saldo Restante"});
    tableComprobantePago.addCell("CUIT/DNI");
    tableComprobantePago.addCell(compraSeleccionada.getIDCliente().getCuit());
    tableComprobantePago.addCell("Nombre");
    tableComprobantePago.addCell(compraSeleccionada.getIDCliente().getRazonSocial());
    tableComprobantePago.addCell("Compra");
    tableComprobantePago.addCell(compraSeleccionada.getDescripcion());
    tableComprobantePago.addCell("Monto Pagado");
    tableComprobantePago.addCell(tfMontoApagar.getText());
    tableComprobantePago.addCell("Descripción Pago");
    if (tfDescripcion.getText().trim().equals("")) {
      tableComprobantePago.addCell(Constantes.TEXTO_STANDARD_DESCRIPCION_PAGO);
    } else {
      tableComprobantePago.addCell(tfDescripcion.getText());
    }
    tableComprobantePago.addCell("Saldo Restante");
    tableComprobantePago.addCell(String.valueOf(compraSeleccionada.getSaldoRestante()));
    try {
      document.add(tableComprobantePago);

    } catch (DocumentException e) {
      e.printStackTrace();
    }
    document.close();
    mostrarPdf(fileName);

  }

  private boolean camposObligatoriosValidosParaIngresarPago() {

    if (tfMontoApagar.getText().trim().equals("") || tfMontoApagar.getText().trim().equals("0")) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
          "Ingresó un valor inválido en el monto del pago");
      return false;
    }
  return true;
  }
}
