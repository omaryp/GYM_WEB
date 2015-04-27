package pe.com.gym.controllers;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import pe.com.gym.delegate.Gym;
import pe.com.gym.dto.ClienteDTO;
import pe.com.gym.entidades.Cliente;
import pe.com.gym.util.Js;
import pe.com.gym.util.Message;

/**
 * @author Omar Yarleque
 * 
 */

@ManagedBean
@ViewScoped
public class ClienteController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ClienteController.class.getName());
	private int grabar;
	private int carga;
	private int ultimo;
	private int tipoPer;
	private int primero;
	private int opcion;
	private boolean read;
	private boolean verPn;
	private boolean verPj;
	private boolean verGuar;
	private boolean verCampo;
	private boolean verActualizar;
	private long codCli;
	private Cliente cliente;
	private ClienteDTO cli;
	private String sTipPer;
	private String valBus;
	private String texto;
	private List<ClienteDTO> clientes;
	private List<ClienteDTO> empresas;
	private LazyDataModel<ClienteDTO> modelCliente;
	
	public ClienteController() {
	}

	@PostConstruct
	public void init() {
		// se activa persona natural por defecto
		texto="Nombre : ";
		opcion = 1;
		verPn = true;
		tipoPer = 1;
		sTipPer = "N";
		verGuar = true;
		valBus = "";
		verCampo = true;
		read = false;
		grabar = 0;
		codCli = 0;
		cargarLista();
	}

	public void campoBusqueda() {
		switch (tipoPer) {
			case 1:
				verCampo = true;
				sTipPer = "N";
				verPn = true;
				verPj = false;
				break;
			case 2:
				verCampo = false;
				sTipPer = "J";
				verPn = false;
				verPj = true;
				break;
		}
		Js.update("tbl_clientes");
	}

	public void mostrarCampos() {
		switch (tipoPer) {
			case 1:
				verPn = true;
				verPj = false;
				verCampo = true;
				sTipPer = "N";
				break;
			case 2:
				verPn = false;
				verPj = true;
				verCampo = false;
				sTipPer = "J";
				break;
		}
		limpiarformulario();
		codCli = Gym.INSTANCE.getCodigoClienteNvo();
	}

	public void cargarClave() {
		cargaEmpresas();
		cliente = new Cliente();
		cliente.setCodcli(0);
		codCli = Gym.INSTANCE.getCodigoClienteNvo();
		if (codCli != 0) {
			Js.execute("PF('dlg_cliente').show()");
		} else
			Message.addError(null, "Error al cargar código del cliente.");
		Js.update("mensajes");
	}

	public void cargarLista() {
		carga = 1;
		loadLazyModelClientes();
	}
	
	public void buscarCliente() {
		carga = 2;
		loadLazyModelClientes();
	}
	
	public void cargaFrmBusqueda(){
		clientes = null;
		buscarCliente();
		Js.execute("PF('dlg_busqueda').show()");
	}
	
	public void saveCliente() {
		if (validarDatos()) {
			int res = 0;
			cliente.setCodcli(codCli);
			cliente.setUsureg("");
			cliente.setFecreg(new Date());
			switch (tipoPer) {
			case 1:
				cliente.setTipper("N");
				cliente.setRutfot("");
				break;
			case 2:
				cliente.setTipper("J");
				cliente.setDnirepl("");
				break;
			}
			res = Gym.INSTANCE.guardarCliente(cliente);
			switch (res) {
			case 0:
				Message.addInfo(null, "Cliente guardado correctamente");
				break;

			default:
				Message.addError(null, "Error al guardar cliente");
				break;
			}
		}
	}

	public boolean validarDatos() {
		switch (tipoPer) {
		case 1:
			if (cliente.getNomcli().trim().equals(""))
				Message.addError(null, "Ingrese nombres.");
			if (cliente.getApecli().trim().equals(""))
				Message.addError(null, "Ingrese apellidos.");
			if (cliente.getDnicli().trim().equals(""))
				Message.addError(null, "Ingrese dni.");
			else if (cliente.getDnicli().trim().length() != 8)
				Message.addError(null, "Ingrese dni correcto.");
			if (cliente.getFecnac() == null)
				Message.addError(null, "Ingrese fecha de nacimiento.");
			break;
		case 2:
			if (cliente.getRazsoc().trim().equals(""))
				Message.addError(null, "Ingrese Razón Social.");
			if (cliente.getRepleg().trim().equals(""))
				Message.addError(null, "Ingrese Representante Legal.");
			if (cliente.getRuccli() .trim().equals(""))
				Message.addError(null, "Ingrese ruc.");
			break;
		}
		if (codCli == 0)
			Message.addError(null, "No se generó código de cliente.");
		return !Message.hayMensajes();
	}

	public void salir() {
		limpiarformulario();
		reiniciarflags();
		cargarLista();
		Js.execute("PF('dlg_cliente').hide()");
	}

	public void salirBusqueda() {
		clientes = null;
		Js.execute("PF('dlg_busqueda').hide()");
	}
	
	public void reiniciarflags() {
		texto="Nombre : ";
		opcion = 1;
		tipoPer = 1;
		sTipPer = "N";
		verPn = true;
		verPj = false;
		verGuar = true;
		verActualizar = false;
		verCampo = true;
		read = false;
	}

	public void limpiarformulario() {
		codCli = 0;
	}

	public void loadLazyModelClientes() {
		modelCliente = new LazyDataModel<ClienteDTO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setRowIndex(int rowIndex) {
				try {
					super.setRowIndex(rowIndex);
				} catch (ArithmeticException e) {
					logger.log(Level.SEVERE, "Error al Dividir {0}", e);
				}
			}

			@Override
			public ClienteDTO getRowData(String rowKey) {
				return null;
			}

			@Override
			public Object getRowKey(ClienteDTO dto) {
				return null;
			}

			@Override
			public List<ClienteDTO> load(int first, int pageSize,
					String string, SortOrder so, Map<String, Object> map) {
				primero = first;
				ultimo = pageSize + first;
				loadData();
				return clientes;
			}
		};
	}

	@SuppressWarnings("unchecked")
	public void loadData() {
		int[] limites = new int[2];
		Map<String, Object> map = null;
		limites[0] = primero;
		limites[1] = ultimo;
		Integer count;
		try {
			switch (carga) {
				case 1:
					map = Gym.INSTANCE.listaClientes(valBus.trim(), sTipPer, limites);
					break;
				case 2:
					try {
						if(!valBus.equals("")){
							codCli = Long.parseLong(valBus);
							map = Gym.INSTANCE.busquedaGeneral(valBus, codCli, opcion, limites);
						}else Message.addError(null, "Debe ingresar números !!!");
					} catch (Exception e) {
						Message.addError(null, "Solo números !!!");
					}
					Js.update("mensajes");
					break;
			}
			if (map != null && !map.isEmpty()) {
				clientes = (List<ClienteDTO>) map.get("CLIENTES");
				count = (Integer) map.get("TOTAL");
				modelCliente.setRowCount(count);
			} else
				Message.addInfo(null, "No se encontraron coincidencias!!");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error al cargar la tabla .", ex);
		}
	}
	
	public void seleccionaCliente(){
		Js.update(":ing_inscripcion");
		Js.execute("PF('dlg_busqueda').hide()");
	}
		
	public void verCliente(){
		if(grabar==1){
			verActualizar = true;
			read = false;
		}
		else{
			verActualizar = false;
			read = true;
		}
		verGuar = false;
		try {
			cliente = Gym.INSTANCE.getCliente(sTipPer, codCli);
			if(cliente!=null){
				switch (cliente.getTipper().charAt(0)) {
					case 'N':
						cargaEmpresas();
						break;
					case 'J':
						verPj = true;
						verPn = false;
						break;
				}
				codCli = cliente.getCodcli();
				Js.update("ing_cliente");
				Js.execute("PF('dlg_cliente').show()");
			}else
				Message.addError(null, "Error al cargar el cliente.");
		} catch (Exception e) {
			Message.addError(null, "Error al cargar el cliente.");
		}
		Js.update("mensajes");	
	}
	
	public void actualizarCliente(){
		int res = 0;
		try {
			if(cliente!=null){
				cliente.setFecmod(new Date());
				res = Gym.INSTANCE.actualizaCliente(sTipPer, cliente);
				switch (res) {
					case 0:
						Message.addInfo(null, "Se actualizo correctamenete el cliente.");
						break;
					default:
						Message.addError(null, "Error al actualizar el cliente.");
						break;
				}
			}
		} catch (Exception e) {
			Message.addError(null, "Error al actualizar el cliente.");
		}
		Js.update("mensajes");
	}
		
	public void eliminarCliente(){
		int res = 0;
		try {
			if(codCli!=0){
				res = Gym.INSTANCE.eliminaCliente(codCli);
				switch (res) {
					case 0:
						Message.addInfo(null, "Se dió de baja al cliente.");
						break;
					default:
						Message.addError(null, "Error al dar de baja al cliente.");
						break;
				}
			}
		} catch (Exception e) {
			Message.addError(null, "Error al eliminar el cliente.");
		}
		Js.update("mensajes");
	}
	
	public void cargarComponentes(){
		switch (opcion) {
			case 1:
				texto="Nombre : ";
				break;
			case 2:
				texto="Código : ";
				break;
			case 3:
				texto="DNI : ";
				break;
		}
		logger.log(Level.INFO, texto);
		valBus = "";
		Js.update("lbl_valor");
	}
	
	public void cargaEmpresas(){
		empresas = Gym.INSTANCE.listaClientesPJ();
	}

	public int getGrabar() {
		return grabar;
	}

	public void setGrabar(int grabar) {
		this.grabar = grabar;
	}

	public int getTipoPer() {
		return tipoPer;
	}

	public void setTipoPer(int tipoPer) {
		this.tipoPer = tipoPer;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isVerPn() {
		return verPn;
	}

	public void setVerPn(boolean verPn) {
		this.verPn = verPn;
	}

	public boolean isVerPj() {
		return verPj;
	}

	public void setVerPj(boolean verPj) {
		this.verPj = verPj;
	}

	public boolean isVerGuar() {
		return verGuar;
	}

	public void setVerGuar(boolean verGuar) {
		this.verGuar = verGuar;
	}

	public boolean isVerCampo() {
		return verCampo;
	}

	public void setVerCampo(boolean verCampo) {
		this.verCampo = verCampo;
	}

	public boolean isVerActualizar() {
		return verActualizar;
	}

	public void setVerActualizar(boolean verActualizar) {
		this.verActualizar = verActualizar;
	}

	public long getCodCli() {
		return codCli;
	}

	public void setCodCli(long codCli) {
		this.codCli = codCli;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getValBus() {
		return valBus;
	}

	public void setValBus(String valBus) {
		this.valBus = valBus;
	}

	public List<ClienteDTO> getEmpresas() {
		return empresas;
	}

	public void setEmpresas(List<ClienteDTO> empresas) {
		this.empresas = empresas;
	}

	public LazyDataModel<ClienteDTO> getModelCliente() {
		return modelCliente;
	}

	public void setModelCliente(LazyDataModel<ClienteDTO> modelCliente) {
		this.modelCliente = modelCliente;
	}

	public int getOpcion() {
		return opcion;
	}

	public void setOpcion(int opcion) {
		this.opcion = opcion;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public ClienteDTO getCli() {
		return cli;
	}

	public void setCli(ClienteDTO cli) {
		this.cli = cli;
	}

}
