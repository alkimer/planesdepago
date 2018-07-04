package com.planesdepago.uicontrollers;

import static com.planesdepago.uiutils.DateUtils.myDateFormatter;
import static com.planesdepago.uiutils.PagosUtils.obtenerDescripcion;
import static com.planesdepago.util.PdfUtils.crearEncabezadoMiSuenioHogar;
import static com.planesdepago.util.PdfUtils.crearHeader;
import static com.planesdepago.util.PdfUtils.crearRandomPDFFileName;
import static com.planesdepago.util.PdfUtils.crearTitulo;
import static com.planesdepago.util.PdfUtils.mostrarPdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.planesdepago.dao.ClienteDao;
import com.planesdepago.dao.CompraDao;
import com.planesdepago.dao.PagoDao;
import com.planesdepago.entities.Cheque;
import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiutils.Constantes;
import com.planesdepago.uiutils.DateUtils;
import com.planesdepago.uiutils.DialogPopUp;
import com.planesdepago.util.ApplicationContext;
import com.planesdepago.util.PdfUtils;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIVerHistorialPagosController extends AbstractController implements Initializable {
  ApplicationContext context = ApplicationContext.getInstance();
  private Compra compraSeleccionada;

  @FXML
  private TableView<Pago> tvPagos;

  @FXML
  private TableColumn<Pago, LocalDate> tcFecha;

  @FXML
  private TableColumn<Pago, String> tcMonto;
  @FXML
  private TableColumn<Pago, String> tcDescripcion;

  @FXML
  private TableColumn<Pago, String> tcModalidad;


  @FXML
  private TextField tfNombre;

  @FXML
  private TextField tfMontoFinanciado;

  @FXML
  private TextField tfDescripcionCompra;
  @FXML
  private Button btnImprimir;
  @FXML
  private TextField tfSaldoRestante;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tcFecha.setCellValueFactory(new PropertyValueFactory<Pago, LocalDate>("fechaPago"));
    tcFecha.setCellFactory(column -> {
      TableCell<Pago, LocalDate> cell = new TableCell<Pago, LocalDate>() {

        @Override
        protected void updateItem(LocalDate item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
            setStyle("");
          } else {
            // Format date.
            setText(myDateFormatter.format(item));
          }
        }
      };
      return cell;
    });
    tcMonto.setCellValueFactory(new PropertyValueFactory<Pago, String>("montoPagado"));
    tcDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pago, String>, ObservableValue<String>>() {
      @Override
      public ObservableValue<String> call(
          TableColumn.CellDataFeatures<Pago, String> param) {
    /*    String texto = param.getValue().getDescripcionPago();
        if (param.getValue().getTipoPago() != null) {
          switch (param.getValue().getTipoPago()) {
            case CHEQUE:
              Cheque cheque = param.getValue().getCheque();
              texto +=
                  " Nro.cheque: " + cheque.getNroCheque() + " Vencimiento: " + cheque.getFechaVencimiento() + ""
                      + " Banco: " + cheque.getBanchoEmisor() + " Retenciones: " + cheque.getRetenciones();
              break;
            case TARJETA:
              texto += " Tarjeta: " + param.getValue().getTarjeta();
              break;
          }
        }*/
        return new SimpleStringProperty(obtenerDescripcion(param.getValue()));
      }
    });
    tcModalidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pago, String>, ObservableValue<String>>() {

      @Override
      public ObservableValue<String> call(
          TableColumn.CellDataFeatures<Pago, String> param) {
        String texto = "";
        texto = param.getValue().getTipoPago() == null ? texto = Constantes.TEXT_TIPO_PAGO_NO_INDICADO
            : param.getValue().getTipoPago().toString();
        return new SimpleStringProperty(texto);
      }
    });

  }

  public void init(Compra compra) {
    EntityManager entityManager;

    entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);

    compra = (Compra) compraDao.find(compra.getIdTransaccion());


    this.compraSeleccionada = compra;

    this.tfDescripcionCompra.setText(compra.getDescripcion());
    this.tfNombre.setText(compra.getIDCliente().getRazonSocial());
    this.tfMontoFinanciado.setText(String.valueOf(compra.getMontoAFinanciar()));
    this.tfSaldoRestante.setText(String.valueOf(compra.getSaldoRestante()));
    createTablePagos(compraSeleccionada.getPagos());

    compraDao.close();
  }

  private void createTablePagos(List<Pago> listaPagos) {

    ObservableList<Pago> pagos = FXCollections.observableArrayList();
    pagos.addAll(listaPagos);

    tvPagos.getItems().clear();
    tvPagos.getItems().addAll(pagos);

    tvPagos.setRowFactory(new Callback<TableView<Pago>, TableRow<Pago>>() {
      @Override
      public TableRow<Pago> call(TableView<Pago> tableView) {
        final TableRow<Pago> row = new TableRow<>();
        final ContextMenu contextMenuCompleto = new ContextMenu();
        final MenuItem eliminarPago = new MenuItem("Eliminar Pago");
        eliminarPago.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            onEliminarPago(row.getItem());
          }
        });

        contextMenuCompleto.getItems().add(eliminarPago);
        // Set context menu on row, but use a binding to make it only show for non-empty rows:
        row.contextMenuProperty()
            .bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenuCompleto));

        return row;
      }

    });
  }

  @FXML
  private void onActionBtnImprimirPDF(ActionEvent event) {
    EntityManager entityManager = context.getEntityManager();
    Document document = new Document(PageSize.A4);
    String fileName = crearRandomPDFFileName("HistoricoPagos");
    PdfWriter writer = new PdfUtils().createWriter(document, fileName);
    document.open();
    crearEncabezadoMiSuenioHogar(document);
    crearTitulo(document, "Histórico de Pagos");

    PdfPTable tableHistoricoPagos = new PdfPTable(4);
    tableHistoricoPagos.setTotalWidth(PageSize.A4.getWidth() * 0.8f);
    tableHistoricoPagos.setLockedWidth(true);

    crearHeader(tableHistoricoPagos, new String[]{"Nombre", "Compra", "Monto financiado", ""});
    tableHistoricoPagos.addCell(tfNombre.getText());
    tableHistoricoPagos.addCell(tfDescripcionCompra.getText());
    tableHistoricoPagos.addCell(tfMontoFinanciado.getText());
    tableHistoricoPagos.addCell("");
    compraSeleccionada.getPagos().sort(new Comparator<Pago>() {
      public int compare(Pago left, Pago right) {
        if (left.getFechaPago().isBefore(right.getFechaPago())) {
          return -1;
        } else {
          return 1;
        }
      }
    });

    crearHeader(tableHistoricoPagos, new String[]{"Fecha", "Descripción del pago", "Modalidad", "Monto"});

    for (Pago pago : tvPagos.getItems()) {

      tableHistoricoPagos.addCell(pago.getFechaPago().toString());
      tableHistoricoPagos.addCell(obtenerDescripcion(pago));
      tableHistoricoPagos
          .addCell(pago.getTipoPago() != null ? pago.getTipoPago().toString() : Constantes.TEXT_TIPO_PAGO_NO_INDICADO);
      tableHistoricoPagos.addCell(String.valueOf(pago.getMontoPagado()));

    }
    //Parece que al cerrar este em se cierran los demás (el de pago y cliente)
   // pagoDao.close();

    crearHeader(tableHistoricoPagos, new String[]{"", "Saldo Restante", "",""});
    tableHistoricoPagos.addCell("");
    tableHistoricoPagos.addCell(tfSaldoRestante.getText());
    tableHistoricoPagos.addCell("");
    tableHistoricoPagos.addCell("");

    try {
      document.add(tableHistoricoPagos);

    } catch (DocumentException e) {
      e.printStackTrace();
    }


    document.close();
    mostrarPdf(fileName);
  }

  void onEliminarPago(Pago pagoSeleccionado) {
    Optional<ButtonType> result = DialogPopUp
        .crearDialogo(Alert.AlertType.CONFIRMATION, "Confirmación", "Eliminación de Pago",
            "¿Está seguro que desea eliminar el Pago seleccionado de la Base de Datos?");


    if (result.get() == ButtonType.OK) {
      EntityManager entityManager;
      entityManager = context.getEntityManager();
      CompraDao compraDao = new CompraDao(entityManager);
      PagoDao pagoDao = new PagoDao(entityManager);
      pagoSeleccionado = (Pago) pagoDao.find(pagoSeleccionado.getIdPago());
      Compra compra = pagoSeleccionado.getCompraID();
      compra.setSaldoRestante(compra.getSaldoRestante().add(pagoSeleccionado.getMontoPagado()));
      compra.getPagos().remove(pagoSeleccionado);
      compraDao.edit(compra);

      pagoDao.remove(pagoSeleccionado);
      pagoDao.close();
      this.createTablePagos(compra.getPagos());
      ((UIListadoComprasClienteController) mainController).buscarTodasLasCompras();
    } else {

    }
  }
}
