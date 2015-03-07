/**
 * 
 */
package pe.com.gym.controllers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import pe.com.gym.dto.ClienteDTO;
import pe.com.gym.entidades.ModalidadPago;
import pe.com.gym.util.Message;

/**
 * @author Omar Yarleque
 * 
 */

@ManagedBean
@ViewScoped
public class ModalidadController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ModalidadController.class.getName());
	//private int primero;
	//private int ultimo;
	private ModalidadPago modalidad;
	private List<ClienteDTO> clientes;
	private List<ClienteDTO> empresas;
	private LazyDataModel<ClienteDTO> modelCliente;

	public ModalidadController() {
	}

	@PostConstruct
	public void init() {
		
	}

	public void cargarClave() {
		modalidad = new ModalidadPago();
		/*codCli = Gym.INSTANCE.getCodigoClienteNvo();
		if (codCli != 0) {
			Js.execute("PF('dlg_cliente').show()");
		} else
			Message.addError(null, "Error al cargar código del cliente.");
		Js.update("mensajes");*/
	}

	
	public boolean validarDatos() {
		
		return !Message.hayMensajes();
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
				//primero = first;
				//ultimo = pageSize + first;
				//loadData();
				return clientes;
			}
		};
	}

	/*@SuppressWarnings("unchecked")
	public void loadData() {
		int[] limites = new int[2];
		Map<String, Object> map = null;
		limites[0] = primero;
		limites[1] = ultimo;
		Integer count;
		try {
			/*map = Gym.INSTANCE.listaClientes(valBus.trim(), sTipPer, limites);
			if (map != null && !map.isEmpty()) {
				clientes = (List<ClienteDTO>) map.get("CLIENTES");
				count = (Integer) map.get("TOTAL");
				modelCliente.setRowCount(count);
			} else
				Message.addInfo(null, "No se encontraron coincidencias!!");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error al cargar la tabla .", ex);
		}
	}*/

	public ModalidadPago getModalidad() {
		return modalidad;
	}

	public void setModalidad(ModalidadPago modalidad) {
		this.modalidad = modalidad;
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
	
}
