package com.planesdepago.uiControllers;

import static com.planesdepago.services.ClienteServices.obtenerTodosLosClientes;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.util.ApplicationContext;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;


public class UIListadoClientesController extends AbstractController implements Initializable {
  EntityManager entityManager;
  Cliente clienteSeleccionado;
  ApplicationContext context = ApplicationContext.getInstance();
  //Botones utilidades
  @FXML
  private TextField tfBuscadorPersonaFilter;

  @FXML
  private Button btnBuscarPersona;

  @FXML
  private Button btNuevoCliente;

  @FXML
  private Button btnEditarCliente;

  @FXML
  private Button btnEliminarCliente;


  //Definiciones tabla cliente
  @FXML
  private TableView<Cliente> tvListaClientes;
  @FXML
  private TableColumn<Cliente, String> tcCuit;
  @FXML
  private TableColumn<Cliente, String> tcRazonSocial;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //Inicializar mapeos de columnas en la tabla de clientes
    tcCuit.setCellValueFactory(new PropertyValueFactory<Cliente, String>("cuit"));
    tcRazonSocial.setCellValueFactory(new PropertyValueFactory<Cliente, String>("razonSocial"));

    createTableListaClientes();

    // Agrego oyente de cambios de selección en la lista,
    // Al detectar que se seleccionó un usuario, lo guardo en el objeto
    // clienteSeleccionado.
    tvListaClientes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      int selectedIndex = tvListaClientes.getSelectionModel().getSelectedIndex();
      if (selectedIndex >= 0) {
        clienteSeleccionado = tvListaClientes.getItems().get(selectedIndex);
        ((UIMainIntegradoController) mainController).actualizarClienteSeleccionado(clienteSeleccionado);
      }

    });
  }

  private void onEliminarCliente() {
    int selectedIndex = tvListaClientes.getSelectionModel().getSelectedIndex();
    if (selectedIndex >= 0) {

      Optional<ButtonType> result = DialogPopUp
          .crearDialogo(Alert.AlertType.CONFIRMATION, "Confirmación", "Eliminación de Cliente",
              "¿Está seguro que desea eliminar el cliente seleccionado de la Base de Datos?");


      if (result.get() == ButtonType.OK) {
        entityManager = context.getEntityManager();
        ClienteDao clienteDao = new ClienteDao(entityManager);

        clienteDao.remove(clienteSeleccionado);
        clienteDao.close();
        DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Información", "Cliente eliminado",
            "Se ha eliminado el cliente seleccionado.");
        ((UIMainIntegradoController) mainController).actualizarClienteSeleccionado(null);
        this.createTableListaClientes();
      } else {
      }


    } else {
      //No seleccionó ningún cliente
      DialogPopUp.crearDialogo(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un cliente",
          "Debe seleccionar un cliente de la lista para poder Eliminarlo");
    }


  }


  private void onEditarCliente(ActionEvent event) {
    //  int selectedIndex = tvListaClientes.getSelectionModel().getSelectedIndex();
    // if (selectedIndex >= 0) {
    AbstractController cont =
        cambiarEscena("Ver/Editar cliente", "/UI_EditarCliente.fxml", Modality.WINDOW_MODAL, this, event);
    ((UIEditarClienteController) cont).mapCliente2UI(clienteSeleccionado);

    //  } else {
    //No seleccionó ningún cliente
    //    DialogPopUp.crearDialogo(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un cliente",
    //        "Debe " + "seleccionar un cliente de la lista para poder editarlo");
    //  }
  }


  //Llenar tabla de clientes en UI
  void createTableListaClientes() {

    ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    clientes.addAll(obtenerTodosLosClientes());

    //Inicio codigo filtro de búsqueda

    // 1. Wrap the ObservableList in a FilteredList (initially display all data).
    FilteredList<Cliente> filteredData = new FilteredList<>(clientes, p -> true);

    // 2. Set the filter Predicate whenever the filter changes.
    tfBuscadorPersonaFilter.textProperty().addListener((observable, oldValue, newValue) -> {
      filteredData.setPredicate(cliente -> {
        // If filter text is empty, display all persons.
        if (newValue == null || newValue.isEmpty()) {
          return true;
        }

        // Compare first name and last name of every person with filter text.
        String lowerCaseFilter = newValue.toLowerCase();

        if (cliente.getRazonSocial().toLowerCase().contains(lowerCaseFilter)) {
          return true; // Filter matches razón social.
        } else if (cliente.getCuit().toLowerCase().contains(lowerCaseFilter)) {
          return true; // Filter matches cuit.
        }
        return false; // Does not match.
      });
    });

    // 3. Wrap the FilteredList in a SortedList.
    SortedList<Cliente> sortedData = new SortedList<>(filteredData);

    // 4. Bind the SortedList comparator to the TableView comparator.
    sortedData.comparatorProperty().bind(tvListaClientes.comparatorProperty());

    // 5. Add sorted (and filtered) data to the table.
    tvListaClientes.setItems(sortedData);

    //fin codigo filtro


//    tvListaClientes.getItems().clear();
    //   tvListaClientes.getItems().addAll(clientes);

    //Agrego los menu items contextuales

    tvListaClientes.setRowFactory(new Callback<TableView<Cliente>, TableRow<Cliente>>() {

      @Override
      public TableRow<Cliente> call(TableView<Cliente> tableView) {
        final TableRow<Cliente> row = new TableRow<>();
        final ContextMenu contextMenuCompleto = new ContextMenu();

        final MenuItem eliminarClienteItem = new MenuItem("Eliminar Cliente");
        eliminarClienteItem.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            onEliminarCliente();
          }
        });

        final MenuItem editarClienteItem = new MenuItem("Ver/Editar Cliente");
        editarClienteItem.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            onEditarCliente(mapToNodeCompatibleEvent(tvListaClientes, event));
          }
        });

        contextMenuCompleto.getItems().add(eliminarClienteItem);
        contextMenuCompleto.getItems().add(editarClienteItem);
        // Set context menu on row, but use a binding to make it only show for non-empty rows:
        row.contextMenuProperty()
            .bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenuCompleto));

        return row;
      }
    });
  }

  @FXML
  private void onActionBtnBuscarPesona(ActionEvent event) {


  }
}
