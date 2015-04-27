/**
 * 
 */
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
import pe.com.gym.entidades.Pago;
import pe.com.gym.entidades.PagoPk;
import pe.com.gym.util.Js;
import pe.com.gym.util.Message;

/**
 * @author Omar Yarleque
 * 
 */

@ManagedBean
@ViewScoped
public class PagoController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PagoController.class.getName());
	private int primero;
	private int ultimo;
	private int grabar;
	private long corPag;
	private boolean verGuar;
	private boolean verActualizar;
	private boolean read;
	private ClienteDTO cliente;
	private Pago pago;
	private PagoPk pagoPk;
	private List<Pago> pagos;
	private LazyDataModel<Pago> modelPago;

	public PagoController() {
	}

	@PostConstruct
	public void init() {
		pagoPk = new PagoPk();
		pago = new Pago();
		verGuar = true;
		verActualizar = false;
		read = false;
	}

	public void cargarClave() {
		pago = new Pago();
		corPag = Gym.INSTANCE.getCodigoModalidadNva();
		if (corPag != 0) {
			Js.execute("PF('dlg_pago').show()");
		} else
			Message.addError(null, "Error al cargar código de modalidad.");
		Js.update("mensajes");
	}
	
	public void savePago(){
		//if (validarDatos()) {
			int res = 0;
			pagoPk.setCodcli(cliente.getCodigoCliente());
			pagoPk.setCorpag(corPag);
			pago.setId(pagoPk);
			pago.setUsureg("");
			pago.setFecpag(new Date());
			//res = Gym.INSTANCE.registraModalidad(modalidad);
			switch (res) {
			case 0:
				Message.addInfo(null, "Pago registrado correctamente !!!");
				break;

			default:
				Message.addError(null, "Error al realizar pago !!!");
				break;
			}
		//}
	}
	
	/*public void actualizarModalidad(){
		int res = 0;
		try {
			if(pago!=null){
				//res = Gym.INSTANCE.actualizaModalidad(modalidad);
				switch (res) {
					case 0:
						Message.addInfo(null, "Se actualizó correctamenete !!!");
						break;
					default:
						Message.addError(null, "Error al actualizar modalidad !!!.");
						break;
				}
			}
		} catch (Exception e) {
			Message.addError(null, "Error al actualizar el cliente.");
		}
		Js.update("mensajes");
	}*/
	
	/*public void eliminarModalidad(){
		int res = 0;
		try {
			if(codMod!=0){
				res = Gym.INSTANCE.cambiaEstadoModalidad(codMod, 1);
				switch (res) {
					case 0:
						Message.addInfo(null, "Se dió de baja esta modalidad !!!");
						break;
					default:
						Message.addError(null, "Error al dar de baja esta modalidad !!!");
						break;
				}
			}
		} catch (Exception e) {
			Message.addError(null, "Error al eliminar el cliente.");
		}
		Js.update("mensajes");
	}*/
	
	public void salir(){
		limpiarformulario();
		reiniciarflags();
		Js.execute("PF('dlg_pago').hide()");
	}
	
	public void reiniciarflags() {
		verActualizar = false;
		verGuar = true;
	}

	public void limpiarformulario() {
		corPag = 0;
		pago = null;
	}
	
	public void cargarLista() {
		loadLazyModelPagos();
	}

	/*public boolean validarDatos() {
		if (modalidad.getNommod().trim().equals(""))
			Message.addError(null, "Ingrese referencia.");
		if (modalidad.getDiamod() == 0)
			Message.addError(null, "Ingrese días de la modalidad.");
		if (modalidad.getDesmod().equals(""))
			Message.addError(null, "Ingrese dni.");
		if (codMod == 0)
			Message.addError(null, "No se generó código de cliente.");
		return !Message.hayMensajes();
	}*/
	
	public void loadLazyModelPagos() {
		modelPago = new LazyDataModel<Pago>() {
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
			public Pago getRowData(String rowKey) {
				return null;
			}

			@Override
			public Object getRowKey(Pago dto) {
				return null;
			}

			@Override
			public List<Pago> load(int first, int pageSize,
					String string, SortOrder so, Map<String, Object> map) {
				primero = first;
				ultimo = pageSize + first;
				loadData();
				return pagos;
			}
		};
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public void loadData() {
		int[] limites = new int[2];
		Map<String, Object> map = null;
		limites[0] = primero;
		limites[1] = ultimo;
		Integer count;
		try {
			//map = Gym.INSTANCE.listaModalidades(valBus, limites);
			if (map != null && !map.isEmpty()) {
				pagos = (List<Pago>) map.get("PAGOS");
				count = (Integer) map.get("TOTAL");
				modelPago.setRowCount(count);
			} else
				Message.addInfo(null, "No se encontraron coincidencias!!");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error al cargar la tabla .", ex);
		}
	}
	
	public void verPago(){
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
			//pago = Gym.INSTANCE.getModalidad(codMod);
			if(pago!=null){
				corPag = pago.getId().getCorpag();
				Js.update("ing_pago");
				Js.execute("PF('dlg_pago').show()");
			}else
				Message.addError(null, "Error al cargar pago.");
		} catch (Exception e) {
			Message.addError(null, "Error al cargar pago.");
		}
		Js.update("mensajes");	
	}

	public int getUltimo() {
		return ultimo;
	}

	public void setUltimo(int ultimo) {
		this.ultimo = ultimo;
	}

	public int getGrabar() {
		return grabar;
	}

	public void setGrabar(int grabar) {
		this.grabar = grabar;
	}

	public boolean isVerGuar() {
		return verGuar;
	}

	public void setVerGuar(boolean verGuar) {
		this.verGuar = verGuar;
	}

	public boolean isVerActualizar() {
		return verActualizar;
	}

	public void setVerActualizar(boolean verActualizar) {
		this.verActualizar = verActualizar;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Pago getPago() {
		return pago;
	}

	public void setPago(Pago pago) {
		this.pago = pago;
	}

	public PagoPk getPagoPk() {
		return pagoPk;
	}

	public void setPagoPk(PagoPk pagoPk) {
		this.pagoPk = pagoPk;
	}

	public LazyDataModel<Pago> getModelPago() {
		return modelPago;
	}

	public void setModelPago(LazyDataModel<Pago> modelPago) {
		this.modelPago = modelPago;
	}

	public ClienteDTO getCliente() {
		return cliente;
	}

	public void setCliente(ClienteDTO cliente) {
		this.cliente = cliente;
	}

	
	
}
