package com.planesdepago.uiControllers;

import static com.planesdepago.uiUtils.DateUtils.myDateFormatter;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.dao.CompraDao;
import com.planesdepago.dao.PagoDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.entities.Pago;
import com.planesdepago.uiUtils.DateUtils;
import com.planesdepago.util.ApplicationContext;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIReportePagosDiarios extends AbstractController implements Initializable {

  ApplicationContext context = ApplicationContext.getInstance();
  @FXML
  private DatePicker dpFecha;

  @FXML
  private TextField tfTotalIngresos;

  @FXML
  private Button btnGenerarReporte;

  @FXML
  private TableView<Pago> tvPagos;
  @FXML
  private TableColumn<Pago, LocalDate> tcFecha;

  @FXML
  private TableColumn<Pago, String> tcMonto;
  @FXML
  private TableColumn<Pago, String> tcDescripcion;

  @FXML
  private TableColumn<Pago, String> tcRazonSocial;

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
    tcDescripcion.setCellValueFactory(new PropertyValueFactory<Pago, String>("descripcionPago"));

    tcRazonSocial
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pago, String>, ObservableValue<String>>() {

          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Pago, String> param) {
            EntityManager entityManager = context.getEntityManager();
            ClienteDao clienteDao = new ClienteDao(entityManager);
            PagoDao pagoDao = new PagoDao(entityManager);
            CompraDao compraDao = new CompraDao(entityManager);

            Pago pago = (Pago) pagoDao.find(param.getValue().getIdPago());
            Compra compra = (Compra) compraDao.find(pago.getCompraID().getIdTransaccion());
            Cliente cliente = (Cliente) clienteDao.find(compra.getIDCliente().getCuit());

            //Parece que al cerrar este em se cierran los demás (el de pago y cliente)
            pagoDao.close();
            return new SimpleStringProperty(cliente.getRazonSocial());
          }
        });
    String pattern = "dd-MM-yyyy";

    this.dpFecha.setValue(LocalDate.now());
    this.dpFecha.setConverter(DateUtils.formateadorLocalDate());

    this.generarReporte();
  }

  @FXML
  private void onActionBtnGenerarReporte(ActionEvent event) {
    this.generarReporte();
  }

  private void generarReporte() {
    PagoDao pagoDao;
    EntityManager entityManager;
    entityManager = context.getEntityManager();
    pagoDao = new PagoDao(entityManager);

    List<Pago> listaPagosDiarios = pagoDao.getPagosDiarios(dpFecha.getValue());
    this.createTablePagos(listaPagosDiarios);
    pagoDao.close();

    this.calcularTotalDiario(listaPagosDiarios);

  }

  private void createTablePagos(List<Pago> listaPagos) {

    ObservableList<Pago> pagos = FXCollections.observableArrayList();
    pagos.addAll(listaPagos);

    tvPagos.getItems().clear();
    tvPagos.getItems().addAll(pagos);
  }

  private void calcularTotalDiario(List<Pago> listaPagos) {
    BigDecimal sumaPagos = new BigDecimal("0");
    for (Pago pago : listaPagos) {
      sumaPagos = sumaPagos.add(pago.getMontoPagado());
    }
    tfTotalIngresos.setText(String.valueOf(sumaPagos));
  }
}