package com.planesdepago.uiControllers;

import static com.planesdepago.uiUtils.DateUtils.myDateFormatter;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.dao.CompraDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.entities.Compra;
import com.planesdepago.uiUtils.Constantes;
import com.planesdepago.uiUtils.DateUtils;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.util.ApplicationContext;

import javafx.beans.binding.Bindings;
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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

public class UIListadoComprasClienteController extends AbstractController implements Initializable {
  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;

  private Cliente cliente;
  private ClienteDao clienteDao;

  private Compra compraSeleccionada;


  @FXML
  private Button btnVerCuotasYpagos;

  @FXML
  private Button btnNuevaCompra;

  @FXML
  private Button btnHistoricoPagos;

  @FXML
  private TableView<Compra> tvListaCompras;
  @FXML
  private TableColumn<Compra, String> tcRazonSocial;

  @FXML
  private TableColumn<Compra, LocalDate> tcFecha;
  @FXML
  private TableColumn<Compra, String> tcMontoCompra;

  @FXML
  private TableColumn<Compra, String> tcMontoFinanciado;
  @FXML
  private TableColumn<Compra, String> tcCuotas;
  @FXML
  private TableColumn<Compra, String> tcDescripcion;
  @FXML
  private TableColumn<Compra, String> tcSaldoRestante;
  @FXML
  private TextField tfClienteSeleccionado;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    tcCuotas.setCellValueFactory(new PropertyValueFactory<Compra, String>("cantCuotas"));
    tcDescripcion.setCellValueFactory(new PropertyValueFactory<Compra, String>("descripcion"));
    tcFecha.setCellValueFactory(new PropertyValueFactory<Compra, LocalDate>("fecha"));
    tcFecha.setCellFactory(column -> {
      TableCell<Compra, LocalDate> cell = new TableCell<Compra, LocalDate>() {

        @Override
        protected void updateItem(LocalDate item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
            setStyle("");
          } else {
            // Format date.
            setText(myDateFormatter.format(item));
/*
            // Style all dates in March with a different color.
            if (item.isBefore(LocalDate.now())  || item.equals(LocalDate.now())) {
              setTextFill(Color.BLACK);
              setStyle("-fx-background-color: red");
            } else {
              setTextFill(Color.BLACK);
              setStyle("");
            }
            */
          }
        }
      };

      return cell;
    });
    tcMontoCompra.setCellValueFactory(new PropertyValueFactory<Compra, String>("montoCompra"));
    tcMontoFinanciado.setCellValueFactory(new PropertyValueFactory<Compra, String>("montoAFinanciar"));
    tcSaldoRestante.setCellValueFactory(new PropertyValueFactory<Compra, String>("saldoRestante"));

    /*
    tcRazonSocial
        .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Compra, String>, ObservableValue<String>>() {

          @Override
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<Compra, String> param) {
            Cliente cliente = param.getValue().getIDCliente();
            return new SimpleStringProperty(cliente.getRazonSocial());
          }
        });
*/

    // Agrego oyente de cambios de selección en la lista,
    tvListaCompras.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      int selectedIndex = tvListaCompras.getSelectionModel().getSelectedIndex();
      if (selectedIndex >= 0) {
        compraSeleccionada = tvListaCompras.getItems().get(selectedIndex);
      }

    });
  }


  public void init(Cliente cliente) {
    this.cliente = cliente;

    if (cliente == null) {
      this.tfClienteSeleccionado.setText(Constantes.SIN_CLIENTE_SELECCIONADO);
      tvListaCompras.getItems().clear();
    } else {
      this.tfClienteSeleccionado.setText(cliente.getRazonSocial());
      this.buscarTodasLasCompras();

    }

  }

  public void buscarTodasLasCompras() {

    entityManager = context.getEntityManager();
    ClienteDao clienteDao = new ClienteDao(entityManager);

    cliente = clienteDao.find(cliente.getCuit());
    List<Compra> listaCompras = cliente.getCompras();
    createTableListaCompras(listaCompras);

    clienteDao.close();
  }


  private void onVerCuotasYpagos(ActionEvent event) {
    // int selectedIndex = tvListaCompras.getSelectionModel().getSelectedIndex();

    //compraSeleccionada = tvListaCompras.getItems().get(selectedIndex);

    AbstractController cont =
        cambiarEscena("Estado de Pagos", "/UI_VerEstadoPagos.fxml", Modality.WINDOW_MODAL, this, event);
    ((UIVerEstadoPagosController) cont).init(compraSeleccionada);


    cont.stage.setOnCloseRequest(e -> {
      this.buscarTodasLasCompras();
    });


  }

  @FXML
  private void onActionBtnNuevaCompra(ActionEvent event) {
    //   int selectedIndex = tvListaClientes.getSelectionModel().getSelectedIndex();
    //  if (selectedIndex >= 0) {
    if (this.cliente != null) {
      AbstractController cont =
          cambiarEscena("Crear Plan de Pagos", "/UI_CrearPlanPagos.fxml", Modality.WINDOW_MODAL, this, event);
      // controllerNC.mapCliente2UI().map
      ((UICrearPlanPagosController) cont).init(cliente);
      ((UICrearPlanPagosController) cont).setMainController(this);


    } else

    { //no seleccionó ningún cliente
      DialogPopUp.crearDialogo(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un cliente",
          "Debe seleccionar un cliente de la lista.");

    }

  }

  private void createTableListaCompras(List<Compra> listaCompras) {
    ObservableList<Compra> compras = FXCollections.observableArrayList();
    compras.addAll(listaCompras);

    tvListaCompras.getItems().clear();
    tvListaCompras.getItems().addAll(compras);
    tvListaCompras.setRowFactory(new Callback<TableView<Compra>, TableRow<Compra>>() {

      @Override
      public TableRow<Compra> call(TableView<Compra> tableView) {
        final TableRow<Compra> row = new TableRow<>();
        final ContextMenu contextMenuCompleto = new ContextMenu();
        final ContextMenu contextMenuReducido;

        final MenuItem ingresarPagoItem = new MenuItem("Nuevo Pago/Ver Plan de Pagos");
        ingresarPagoItem.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            onVerCuotasYpagos(mapToNodeCompatibleEvent(tvListaCompras, event));
          }
        });

        final MenuItem verHistoricoPagosItem = new MenuItem("Ver Histórico de Pagos");
        verHistoricoPagosItem.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            onvVerHistoricoPagos(mapToNodeCompatibleEvent(tvListaCompras, event));
          }
        });

        final MenuItem eliminarCompraItem = new MenuItem("Eliminar Compra");
        eliminarCompraItem.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            onEliminarCompra();
          }
        });

        final MenuItem modificarCompraItem = new MenuItem("Modificar Vencimientos de Compra");
        modificarCompraItem.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            onModificarCompra(mapToNodeCompatibleEvent(tvListaCompras, event));
          }
        });

        contextMenuCompleto.getItems().add(modificarCompraItem);
        contextMenuCompleto.getItems().add(verHistoricoPagosItem);
        contextMenuCompleto.getItems().add(ingresarPagoItem);
        contextMenuCompleto.getItems().add(eliminarCompraItem);
        // Set context menu on row, but use a binding to make it only show for non-empty rows:
        row.contextMenuProperty()
            .bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenuCompleto));

        return row;
      }
    });

  }

  private void onModificarCompra(ActionEvent event) {

    AbstractController cont =
        cambiarEscena("Modificar Compra", "/UI_ModificarPlanPagos.fxml", Modality.WINDOW_MODAL, this, event);
    // controllerNC.mapCliente2UI().map
    ((UIModificarPlanPagosController) cont).init(cliente, compraSeleccionada);
    ((UIModificarPlanPagosController) cont).setMainController(this);
  }

  private void onvVerHistoricoPagos(ActionEvent event) {
    int selectedIndex = tvListaCompras.getSelectionModel().getSelectedIndex();

    compraSeleccionada = tvListaCompras.getItems().get(selectedIndex);

    AbstractController cont =
        cambiarEscena("Historial de Pagos", "/UI_VerHistorialPagos.fxml", Modality.WINDOW_MODAL, this, event);
    ((UIVerHistorialPagosController) cont).init(compraSeleccionada);

  }

  /*
    ActionEvent mapToNodeCompatibleEvent(ActionEvent eventSource) {

      //Cambio event que viene del menuitem por un nuevo event2  que viene desde la tabla,
      // Triquiñuela para cambiar el elemento que generó el evento, ya que el
      // menu item no hereda de Node y no puedo obtener el Window al que pertenece.
      ActionEvent event2 = null;
      return eventSource.copyFor(tvListaCompras, eventSource.getTarget());
    }
  */
  private void onEliminarCompra() {

    CompraDao compraDao;
    Optional<ButtonType> result = DialogPopUp
        .crearDialogo(Alert.AlertType.CONFIRMATION, "Confirmación", "Eliminación de Compra",
            "¿Está seguro que desea eliminar la compra seleccionada de la Base de Datos?");


    if (result.get() == ButtonType.OK) {
      entityManager = context.getEntityManager();
      compraDao = new CompraDao(entityManager);

      compraDao.remove(compraDao.find(compraSeleccionada.getIdTransaccion()));
      compraDao.close();
      DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Información", "Compra eliminada",
          "Se ha eliminado la compra seleccionada.");
      this.buscarTodasLasCompras();
    } else {
    }
  }

}
