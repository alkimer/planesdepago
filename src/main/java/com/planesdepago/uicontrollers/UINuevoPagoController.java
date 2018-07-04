package com.planesdepago.uicontrollers;

import static com.planesdepago.uiutils.DateUtils.formatLocalDate2StringPattern;
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
import com.planesdepago.entities.Cheque;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiutils.Constantes;
import com.planesdepago.uiutils.DateUtils;
import com.planesdepago.uiutils.DialogPopUp;
import com.planesdepago.uiutils.InputCheck;
import com.planesdepago.uiutils.ListaBancos;
import com.planesdepago.uiutils.ListaTarjetas;
import com.planesdepago.uiutils.ListaTiposDePago;
import com.planesdepago.util.ApplicationContext;
import com.planesdepago.util.PDFeventListener;
import com.planesdepago.util.PdfUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
  private Label lblMontoApagar;
  @FXML
  private AnchorPane apDatosCheques;

  @FXML
  private AnchorPane apRetenciones;

  @FXML
  private AnchorPane apDatosTarjeta;

  @FXML
  private TextField tfDescripcion;

  @FXML
  private TextField tfMontoApagar;

  @FXML
  private Button btnCancelar;

  @FXML
  private Button btnAceptar;

  @FXML
  private ComboBox<ListaTiposDePago> cbTipoPago;

  @FXML
  private void onActionBtnCancelar(ActionEvent event) {

    this.cerrarVentana();

  }

  @FXML
  private TextField tfNumeroCheque;

  @FXML
  private ComboBox<String> cbBanco;

  @FXML
  private ComboBox<String> cbTarjetas;

  @FXML
  private DatePicker dpFechaVencimientoCheque;

  @FXML
  private TextField tfRetenciones;

  private BigDecimal saldoRestante;

  //utilizada para saber si estoy llamando al pago desde una compra que ya existe (esAnticipo = true)
  //o si lo estoy llamando en el momento que todavía no la creé y quiero cargar el anticipo
  private boolean esAnticipo;

  @FXML
  private void onActionBtnAceptar(ActionEvent event) {


    if (!this.esAnticipo) { //debo guardar los datos el pago
      if (camposObligatoriosValidosParaIngresarPago()) {
        //si quiero pagar más de lo que debo...
        if (saldoRestante.compareTo(new BigDecimal(tfMontoApagar.getText())) < 0) {
          DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Error", "Reingrese el monto a pagar",
              "Usted intenta pagar un monto mayor al total adeudado por la compra ($" + this.saldoRestante + ")");

        } else {
          Pago pago = new Pago();
          if (tfDescripcion.getText().trim().equals("")) {
            pago.setDescripcionPago(Constantes.TEXTO_STANDARD_DESCRIPCION_PAGO);
          } else {
            pago.setDescripcionPago(tfDescripcion.getText());
          }

          cargarDatosDePago(pago);

          entityManager = context.getEntityManager();
          CompraDao compraDao = new CompraDao(entityManager);
          compraSeleccionada = (Compra) compraDao.find(compraSeleccionada.getIdTransaccion());
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
    } else //Si es anticipo solo devuelvo el objeto al proceso de creación de la compra

    {
      if (camposObligatoriosValidosParaIngresarPago()) {
        Pago pago = new Pago();
        pago.setDescripcionPago(Constantes.ANTICIPO);
        cargarDatosDePago(pago);
        ((UICrearPlanPagosController) mainController).setearAnticipo(pago);
        this.cerrarVentana();
      }


    }
  }

  private void cargarDatosDePago(Pago pago) {
    pago.setFechaPago(LocalDate.now());
    pago.setMontoPagado(new BigDecimal(tfMontoApagar.getText()));
    pago.setTipoPago(cbTipoPago.getValue());
    switch (cbTipoPago.getValue()) {
      case CHEQUE:
        Cheque cheque = new Cheque();
        pago.setTipoPago(ListaTiposDePago.CHEQUE);
        cheque.setBanchoEmisor(this.cbBanco.getValue().toString());
        cheque.setNroCheque(Integer.valueOf(this.tfNumeroCheque.getText()));
        cheque.setFechaVencimiento(this.dpFechaVencimientoCheque.getValue());
        pago.addCheque(cheque);
        break;
      case EFECTIVO:
        pago.setTipoPago(ListaTiposDePago.EFECTIVO);
        break;
      case TARJETA:
        pago.setTipoPago(ListaTiposDePago.TARJETA);
        pago.setTarjeta(cbTarjetas.getValue().toString());
        break;
      case RETENCIONES:
        pago.setTipoPago(ListaTiposDePago.RETENCIONES);
        pago.setNumeroRetencion(Integer.valueOf(tfRetenciones.getText()));

        break;

    }

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    InputCheck.agregarControlesAEntradaDeCifras(tfMontoApagar, Constantes.MAX_LONGITUD_MONTOS);
    InputCheck.agregarControlesAEntradaDeCifras(tfRetenciones, Constantes.MAX_LONGITUD_MONTOS);
    InputCheck.agregarControlesAEntradaDeCifras(tfNumeroCheque, Constantes.MAX_LONGITUD_MONTOS);

    cbTipoPago.getItems().setAll(ListaTiposDePago.values());
    cbBanco.getItems().setAll(ListaBancos.values());
    cbTarjetas.getItems().setAll(ListaTarjetas.values());
    dpFechaVencimientoCheque.setConverter(DateUtils.formateadorLocalDate());
    apDatosCheques.setVisible(false);
    apDatosTarjeta.setVisible(false);
    apRetenciones.setVisible(false);
    this.cbTipoPago.setValue(ListaTiposDePago.EFECTIVO);
    this.cbTipoPago.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        switch (cbTipoPago.getValue()) {
          case EFECTIVO:
            apDatosTarjeta.setVisible(false);
            apDatosCheques.setVisible(false);
            apRetenciones.setVisible(false);

            break;

          case TARJETA:
            apDatosCheques.setVisible(false);
            apDatosTarjeta.setVisible(true);
            apRetenciones.setVisible(false);

            break;

          case CHEQUE:
            apDatosCheques.setVisible(true);
            apDatosTarjeta.setVisible(false);
            apRetenciones.setVisible(false);

            break;

          case RETENCIONES:
            apDatosCheques.setVisible(false);
            apDatosTarjeta.setVisible(false);
            apRetenciones.setVisible(true);
            break;
        }
      }
    });
  }

  public void init(Compra compraSeleccionada, BigDecimal saldoRestante, boolean esAnticipo) {
    this.esAnticipo = esAnticipo;
    if (!esAnticipo) {
      this.saldoRestante = saldoRestante;
      this.compraSeleccionada = compraSeleccionada;

      entityManager = context.getEntityManager();
      CompraDao compraDao = new CompraDao(entityManager);
      this.compraSeleccionada = (Compra) compraDao.find(compraSeleccionada.getIdTransaccion());

      compraDao.close();
    }

  }

  public void configurarSoloLectura(Pago pago) {
    apDatosTarjeta.setVisible(false);
    apDatosCheques.setVisible(false);
    this.btnAceptar.setVisible(false);
    this.btnCancelar.setVisible(false);
    this.tfMontoApagar.setText(java.lang.String.valueOf(pago.getMontoPagado()));
    this.tfMontoApagar.setDisable(true);
    this.tfDescripcion.setText(pago.getDescripcionPago());
    this.tfDescripcion.setDisable(true);
    this.cbTipoPago.setValue(pago.getTipoPago());
    this.cbTipoPago.setDisable(true);
    switch (pago.getTipoPago()) {
      case CHEQUE:
        this.apDatosCheques.setVisible(true);
        this.tfNumeroCheque.setText(java.lang.String.valueOf(pago.getCheque().getNroCheque()));
        this.tfNumeroCheque.setDisable(true);
        this.dpFechaVencimientoCheque.setValue(pago.getCheque().getFechaVencimiento());
        this.dpFechaVencimientoCheque.setDisable(true);
        this.cbBanco.setValue(pago.getCheque().getBanchoEmisor());
        this.cbBanco.setDisable(true);

        break;

      case EFECTIVO:

        break;

      case TARJETA:
        this.apDatosTarjeta.setVisible(true);
        this.cbTarjetas.setValue(pago.getTarjeta());
        this.cbTarjetas.setDisable(true);
        break;
      case RETENCIONES:
        this.tfRetenciones.setDisable(true);
        break;
    }


  }

  private void cerrarVentana() {
    Stage stage = (Stage) btnCancelar.getScene().getWindow();
    stage.close();

  }

  private void imprimirPago() {
    Document document = new Document(PageSize.A4);
    java.lang.String fileName = crearRandomPDFFileName("Comprobante Pago");
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
    tableComprobantePago.addCell("Modalidad");
    tableComprobantePago.addCell(cbTipoPago.getValue().toString());
    switch (cbTipoPago.getValue()) {
      case CHEQUE:

        tableComprobantePago.addCell("Número cheque");
        tableComprobantePago.addCell(tfNumeroCheque.getText());
        tableComprobantePago.addCell("Banco");
        tableComprobantePago.addCell(cbBanco.getValue().toString());
        tableComprobantePago.addCell("Fecha Vencimiento");
        tableComprobantePago.addCell(formatLocalDate2StringPattern(dpFechaVencimientoCheque.getValue()));

        break;
      case TARJETA:
        tableComprobantePago.addCell("Nombre Tarjeta");
        tableComprobantePago.addCell(cbTarjetas.getValue().toString());
        break;
      case RETENCIONES:
        tableComprobantePago.addCell("Número Retención");
        tableComprobantePago.addCell(tfRetenciones.getText());
    }


    tableComprobantePago.addCell("Saldo Restante");
    tableComprobantePago.addCell(java.lang.String.valueOf(compraSeleccionada.getSaldoRestante()));
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
    if ((tfDescripcion.getText() != null) && (tfDescripcion.getText().trim().equalsIgnoreCase(Constantes.ANTICIPO))) {
      DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
          "No está permitido agregar un anticipo manualmente luego de realizada la compra");
      return false;

    }

    switch (cbTipoPago.getValue()) {
      case CHEQUE:
        if (tfNumeroCheque.getText().trim().equals("")) {

          DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
              "El campo Número de Cheque es requerido");
          return false;

        }

        if (cbBanco.getValue() == null) {
          DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
              "El campo Banco es requerido");
          return false;

        }
        if (dpFechaVencimientoCheque.getValue() == null) {
          DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
              "El campo Fecha de Vencimiento es requerido");
          return false;

        }

        break;
      case TARJETA:

        if (cbTarjetas.getValue() == null) {
          DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
              "El campo Tarjeta es requerido");
          return false;

        }
        break;

      case RETENCIONES:
        if (tfRetenciones.getText().trim().equals("")) {
          DialogPopUp.crearDialogo(Alert.AlertType.ERROR, "Error", "Verifique los datos ingresados",
              "El campo Número de Retención es requerido");
          return false;

        }
        break;

    }

    return true;
  }
}
