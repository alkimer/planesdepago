package com.planesdepago.uiControllers;

import static com.planesdepago.services.ClienteServices.obtenerTodosLosClientes;

import com.planesdepago.dao.ClienteDao;
import com.planesdepago.entities.Cliente;
import com.planesdepago.uiUtils.DialogPopUp;
import com.planesdepago.util.ApplicationContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


import javax.persistence.EntityManager;

public class MainController extends AbstractController implements Initializable {

  UICrearClienteController controllerNC;

  ApplicationContext context = ApplicationContext.getInstance();
  EntityManager entityManager;
  ClienteDao clienteDao;
  Cliente clienteSeleccionado;

  @FXML
  private Button btNuevoCliente;

  @FXML
  private Button btnEditarCliente;

  @FXML
  private Button btnEliminarCliente;

  @FXML
  private Button btnCrearPlanPagos;

  @FXML
  private Button btnVerComprasCliente;

  //Definiciones tabla cliente
  @FXML
  private TableView<Cliente> tvListaClientes;
  @FXML
  private TableColumn<Cliente, String> tcCuit;
  @FXML
  private TableColumn<Cliente, String> tcRazonSocial;
  @FXML
  private TableColumn<Cliente, String> tcLocalidad;
  @FXML
  private TableColumn<Cliente, String> tcDireccion;
  @FXML
  private TableColumn<Cliente, String> tcProvincia;
  @FXML
  private TableColumn<Cliente, String> tcCelular;
  @FXML
  private TableColumn<Cliente, String> tcTelFijo;
  //


  public void buscarTodosLosClientes() {
 //   entityManager = context.getEntityManager();
  //  clienteDao = new ClienteDao(entityManager);

  //  List<Cliente> listaClientes = clienteDao.findAll();

    createTableListaClientes(obtenerTodosLosClientes());
   // clienteDao.close();

  }

  @FXML
  private void onActionBtnEliminarCliente(ActionEvent event) {
    int selectedIndex = tvListaClientes.getSelectionModel().getSelectedIndex();
    if (selectedIndex >= 0) {

      Optional<ButtonType> result = DialogPopUp
          .crearDialogo(Alert.AlertType.CONFIRMATION, "Confirmación", "Eliminación de Cliente",
              "¿Está seguro que desea eliminar el cliente seleccionado de la Base de Datos?");


      if (result.get() == ButtonType.OK) {
        entityManager = context.getEntityManager();
        clienteDao = new ClienteDao(entityManager);

        clienteDao.remove(clienteSeleccionado);
        DialogPopUp.crearDialogo(Alert.AlertType.INFORMATION, "Información", "Cliente eliminado",
            "Se ha eliminado el cliente seleccionado.");
        this.buscarTodosLosClientes();
      } else {
      }


    } else {
      //No seleccionó ningún cliente
      DialogPopUp.crearDialogo(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un cliente",
          "Debe seleccionar un cliente de la lista para poder Eliminarlo");
    }


  }

  @FXML
  private void onActionBtnCrearPlanPagos(ActionEvent event) {

    int selectedIndex = tvListaClientes.getSelectionModel().getSelectedIndex();
    if (selectedIndex >= 0) {
      AbstractController cont = cambiarEscena("Crear Plan de Pagos", "/UI_CrearPlanPagos.fxml",
     Modality.WINDOW_MODAL, this, event);
    // controllerNC.mapCliente2UI().map
    ((UICrearPlanPagosController) cont).init(clienteSeleccionado);

  } else

  { //no seleccionó ningún cliente
    DialogPopUp.crearDialogo(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un cliente",
        "Debe seleccionar un cliente de la lista.");

  }

}


  @FXML
  private void actionBtnNuevoCliente(ActionEvent event) {
/*
    entityManager = context.getEntityManager();
    CompraDao compraDao = new CompraDao(entityManager);
    Compra comp = new Compra();
    comp.setCuit("1234");
    comp.setCantCuotas(12);
    comp.setFecha(LocalDate.now());
    comp.setDescripcion("prueba2");
    comp.setInteres(35);
    comp.setMontoCompra(new BigDecimal("12000"));
    comp.setMontoAFinanciar(new BigDecimal("10000"));


    Pago pag = new Pago();
    pag.setDescripcionPago("anticipo");
    pag.setFechaPago(LocalDate.now());
    pag.setMontoPagado(new BigDecimal("1000"));
    comp.addPago(pag);
    pag = new Pago();
    pag.setDescripcionPago("cuota 1");
    pag.setFechaPago(LocalDate.now());
    pag.setMontoPagado(new BigDecimal("1200.50"));
    comp.addPago(pag);
    compraDao.create(comp);

    compraDao.close();
    */


     cambiarEscena("Crear nuevo cliente", "/UI_CrearCliente.fxml", Modality.WINDOW_MODAL, this, event);
  }

  @FXML
  private void onActionBtnEditarCliente(ActionEvent event) {

    int selectedIndex = tvListaClientes.getSelectionModel().getSelectedIndex();
    if (selectedIndex >= 0) {
      AbstractController cont = cambiarEscena("Editar cliente", "/UI_EditarCliente.fxml", Modality.WINDOW_MODAL, this,
          event);
      // controllerNC.mapCliente2UI().map
      ((UIEditarClienteController) cont).mapCliente2UI(clienteSeleccionado);

    } else {
      //No seleccionó ningún cliente
      DialogPopUp.crearDialogo(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un cliente",
          "Debe " + "seleccionar un cliente de la lista para poder editarlo");
    }


  }

  @FXML
      private void onActionBtnVerComprasCliente(ActionEvent event) {

    AbstractController cont =  cambiarEscena("Compras realizadas por cliente",
        "/UI_ListadoComprasCliente.fxml", Modality.WINDOW_MODAL, this, event);
    ((UIListadoComprasClienteController) cont).init(clienteSeleccionado);




  }


  //Llenar tabla de clientes en UI
  void createTableListaClientes(List<Cliente> listaClientes) {
    ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    clientes.addAll(listaClientes);

    tvListaClientes.getItems().clear();
    tvListaClientes.getItems().addAll(clientes);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    //Inicializar mapeos de columnas en la tabla de clientes
    tcCuit.setCellValueFactory(new PropertyValueFactory<Cliente, String>("cuit"));
    tcRazonSocial.setCellValueFactory(new PropertyValueFactory<Cliente, String>("razonSocial"));
    tcCelular.setCellValueFactory(new PropertyValueFactory<Cliente, String>("celular"));
    tcDireccion.setCellValueFactory(new PropertyValueFactory<Cliente, String>("direccion"));
    tcProvincia.setCellValueFactory(new PropertyValueFactory<Cliente, String>("provincia"));
    tcTelFijo.setCellValueFactory(new PropertyValueFactory<Cliente, String>("telefonoFijo"));
    tcLocalidad.setCellValueFactory(new PropertyValueFactory<Cliente, String>("localidad"));

    //busco inicialmente todos los clientes
    buscarTodosLosClientes();


    // Agrego oyente de cambios de selección en la lista,
    // Al detectar que se seleccionó un usuario, lo guardo en el objeto
    // clienteSeleccionado.
    tvListaClientes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      int selectedIndex = tvListaClientes.getSelectionModel().getSelectedIndex();
      if (selectedIndex >= 0) {
        clienteSeleccionado = tvListaClientes.getItems().get(selectedIndex);
      }

    });

  }
/*
  private AbstractController cambiarEscena(String title, String resourceLocation, Modality modality) {
    AbstractController abstractController;
    Stage stage = new Stage();
    Parent root = null;
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(resourceLocation));
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    abstractController = loader.getController();
    abstractController.init(this);

    stage.setScene(new Scene(root));
    stage.setTitle(title);
    stage.initModality(modality);

    //La comenté .. ver
    // stage.initOwner(((Node) event.getSource()).getScene().getWindow());
    stage.show();
    return abstractController;

  } */
}
