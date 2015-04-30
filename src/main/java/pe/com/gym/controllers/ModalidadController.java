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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import pe.com.gym.delegate.Gym;
import pe.com.gym.entidades.ModalidadPago;
import pe.com.gym.login.Usuario;
import pe.com.gym.util.Estado;
import pe.com.gym.util.Js;
import pe.com.gym.util.Message;
import pe.com.gym.util.controllers.InitController;

/**
 * @author Omar Yarleque
 * 
 */

@ManagedBean
@ViewScoped
public class ModalidadController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ModalidadController.class.getName());
	@ManagedProperty(value = "#{InitController}")
	private InitController initController;
	private Usuario userSesion;
	private int primero;
	private int ultimo;
	private int grabar;
	private long codMod;
	private boolean modaActiva;
	private boolean verGuar;
	private boolean verActualizar;
	private boolean read;
	private String valBus;
	private ModalidadPago modalidad;
	private List<ModalidadPago> modalidades;
	private List<ModalidadPago> modalidades_activas;
	private LazyDataModel<ModalidadPago> modelModalidad;

	public ModalidadController() {
	}

	@PostConstruct
	public void init() {
		verGuar = true;
		verActualizar = false;
		read = false;
		userSesion = (Usuario)initController.getSessionVars().get("USUARIO");
		cargarLista();
	}

	public void cargarClave() {
		modalidad = new ModalidadPago();
		codMod = Gym.INSTANCE.getCodigoModalidadNva();
		if (codMod != 0) {
			Js.execute("PF('dlg_modalidad').show()");
		} else
			Message.addError(null, "Error al cargar código de modalidad.");
		Js.update("mensajes");
	}
	
	public void saveModalidad(){
		if (validarDatos()) {
			int res = 0;
			modalidad.setCodmod(codMod);
			modalidad.setUsureg(userSesion.getUsername());
			modalidad.setFecreg(new Date());
			modalidad.setEstmod((modaActiva)?Estado.ACTIVO.getValue():Estado.DESACTIVADO.getValue());
			res = Gym.INSTANCE.registraModalidad(modalidad);
			switch (res) {
			case 0:
				Message.addInfo(null, "Modalidad registrada correctamente !!!");
				break;

			default:
				Message.addError(null, "Error al guardar modalidad !!!");
				break;
			}
		}
	}
	
	public void actualizarModalidad(){
		int res = 0;
		try {
			if(modalidad!=null){
				modalidad.setFecmod(new Date());
				modalidad.setEstmod((modaActiva)?Estado.ACTIVO.getValue():Estado.DESACTIVADO.getValue());
				res = Gym.INSTANCE.actualizaModalidad(modalidad);
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
	}
	
	public void eliminarModalidad(){
		int res = 0;
		try {
			if(codMod!=0){
				res = Gym.INSTANCE.cambiaEstadoModalidad(codMod, Estado.DESACTIVADO.getValue());
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
		cargarLista();
	}
	
	public void salir(){
		limpiarformulario();
		reiniciarflags();
		Js.execute("PF('dlg_modalidad').hide()");
		cargarLista();
	}
	
	public void reiniciarflags() {
		verActualizar = false;
		verGuar = true;
	}

	public void limpiarformulario() {
		codMod = 0;
		modalidad = null;
	}
	
	public void cargarLista() {
		loadLazyModelModalidades();
	}

	public boolean validarDatos() {
		if (modalidad.getNommod().trim().equals(""))
			Message.addError(null, "Ingrese referencia.");
		if (modalidad.getDiamod() == 0)
			Message.addError(null, "Ingrese días de la modalidad.");
		if (modalidad.getDesmod().equals(""))
			Message.addError(null, "Ingrese dni.");
		if (codMod == 0)
			Message.addError(null, "No se generó código de cliente.");
		return !Message.hayMensajes();
	}
	
	public void loadLazyModelModalidades() {
		modelModalidad = new LazyDataModel<ModalidadPago>() {
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
			public ModalidadPago getRowData(String rowKey) {
				return null;
			}

			@Override
			public Object getRowKey(ModalidadPago dto) {
				return null;
			}

			@Override
			public List<ModalidadPago> load(int first, int pageSize,
					String string, SortOrder so, Map<String, Object> map) {
				primero = first;
				ultimo = pageSize + first;
				loadData();
				return modalidades;
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
			map = Gym.INSTANCE.listaModalidades(valBus, limites);
			if (map != null && !map.isEmpty()) {
				modalidades = (List<ModalidadPago>) map.get("MODALIDADES");
				count = (Integer) map.get("TOTAL");
				modelModalidad.setRowCount(count);
			} else
				Message.addInfo(null, "No se encontraron coincidencias!!");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error al cargar la tabla .", ex);
		}
	}
	
	public void verModalidad(){
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
			modalidad = Gym.INSTANCE.getModalidad(codMod);
			if(modalidad!=null){
				codMod = modalidad.getCodmod();
				modaActiva = (modalidad.getEstmod() == Estado.ACTIVO.getValue());
				Js.update("ing_modalidad");
				Js.execute("PF('dlg_modalidad').show()");
			}else
				Message.addError(null, "Error al cargar modalidad.");
		} catch (Exception e) {
			Message.addError(null, "Error al cargar modalidad.");
		}
		Js.update("mensajes");	
	}

	public ModalidadPago getModalidad() {
		return modalidad;
	}

	public void setModalidad(ModalidadPago modalidad) {
		this.modalidad = modalidad;
	}

	public long getCodMod() {
		return codMod;
	}

	public void setCodMod(long codMod) {
		this.codMod = codMod;
	}

	public boolean isModaActiva() {
		return modaActiva;
	}

	public void setModaActiva(boolean modaActiva) {
		this.modaActiva = modaActiva;
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

	public List<ModalidadPago> getModalidades_activas() {
		return modalidades_activas;
	}

	public void setModalidades_activas(List<ModalidadPago> modalidades_activas) {
		this.modalidades_activas = modalidades_activas;
	}

	public LazyDataModel<ModalidadPago> getModelModalidad() {
		return modelModalidad;
	}

	public void setModelModalidad(LazyDataModel<ModalidadPago> modelModalidad) {
		this.modelModalidad = modelModalidad;
	}

	public String getValBus() {
		return valBus;
	}

	public void setValBus(String valBus) {
		this.valBus = valBus;
	}

	public int getGrabar() {
		return grabar;
	}

	public void setGrabar(int grabar) {
		this.grabar = grabar;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public InitController getInitController() {
		return initController;
	}

	public void setInitController(InitController initController) {
		this.initController = initController;
	}

	public Usuario getUserSesion() {
		return userSesion;
	}

	public void setUserSesion(Usuario userSesion) {
		this.userSesion = userSesion;
	}
	
}
