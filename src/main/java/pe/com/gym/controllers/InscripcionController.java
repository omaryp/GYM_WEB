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
import pe.com.gym.dto.ClienteDTO;
import pe.com.gym.dto.InscripcionDTO;
import pe.com.gym.entidades.Inscripcion;
import pe.com.gym.entidades.InscripcionPk;
import pe.com.gym.entidades.ModalidadPago;
import pe.com.gym.entidades.Servicio;
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
public class InscripcionController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(InscripcionController.class.getName());
	@ManagedProperty(value = "#{InitController}")
	private InitController initController;
	private Usuario userSesion;
	private int primero;
	private int ultimo;
	private int grabar;
	private int corIns;
	private boolean activo;
	private boolean verGuar;
	private boolean verActualizar;
	private boolean read;
	private Date horIni;
	private Date horFin;
	private String valBus;
	private ClienteDTO cliente;
	private Inscripcion inscripcion;
	private InscripcionPk inscripcionPk;
	private List<InscripcionDTO> inscripciones;
	private List<Servicio> servicios;
	private List<ModalidadPago> modalidades;
	private LazyDataModel<InscripcionDTO> modelInscripcion;

	public InscripcionController() {
	}

	@PostConstruct
	public void init() {
		verGuar = true;
		verActualizar = false;
		read = false;
	}

	public void cargarClave() {
		inscripcionPk = new InscripcionPk();
		inscripcion = new Inscripcion();
		corIns = Gym.INSTANCE.getCorrelativoIncripcion();
		inscripcionPk.setCorrel(corIns);
		if (corIns != 0) {
			Js.execute("PF('dlg_inscripcion').show()");
		} else
			Message.addError(null, "Error al cargar código del cliente.");
		Js.update("mensajes");
	}
	
	public void saveInscripcion(){
		if (validarDatos()) {
			int res = 0;
			inscripcion.getId().setCorrel(corIns);
			inscripcion.setUsureg("");
			inscripcion.setFecreg(new Date());
			res = Gym.INSTANCE.registraInscripcion(inscripcion);
			switch (res) {
			case 0:
				Message.addInfo(null, "Se inscribio cliente correctamente !!!");
				break;

			default:
				Message.addError(null, "Error al inscribir cliente !!!");
				break;
			}
		}
	}
	
	public void actualizarInscripcion(){
		int res = 0;
		try {
			if(inscripcion!=null){
				inscripcion.setUsumod("");
				inscripcion.setFecmod(new Date());
				res = Gym.INSTANCE.actualizaInscripcion(inscripcion);
				switch (res) {
					case 0:
						Message.addInfo(null, "Se actualizó correctamenete !!!");
						break;
					default:
						Message.addError(null, "Error al actualizar inscripción !!!.");
						break;
				}
			}
		} catch (Exception e) {
			Message.addError(null, "Error al actualizar el cliente.");
		}
		Js.update("mensajes");
	}
	
	public void cancelarInscripcion(){
		int res = 0;
		try {
			if(corIns!=0){
				res = Gym.INSTANCE.cancelarInscripcion(inscripcionPk,Estado.DESACTIVADO.getValue());
				switch (res) {
					case 0:
						Message.addInfo(null, "Se dió de baja la inscripción !!!");
						break;
					default:
						Message.addError(null, "Error al dar de baja la inscripción !!!");
						break;
				}
			}
		} catch (Exception e) {
			Message.addError(null, "Error al eliminar el cliente.");
		}
		Js.update("mensajes");
	}
	
	public void salir(){
		limpiarformulario();
		reiniciarflags();
		Js.execute("PF('dlg_inscripcion').hide()");
	}
	
	public void reiniciarflags() {
		verActualizar = false;
		verGuar = true;
	}

	public void limpiarformulario() {
		corIns = 0;
		inscripcion = null;
		cliente = null;
	}
	
	public void cargarLista() {
		loadLazyModelInscripcion();
	}

	public boolean validarDatos() {
		if (inscripcion.getId().getCodcli() == 0)
			Message.addError(null, "Seleccione cliente !!!");
		if (inscripcion.getId().getCodmod() == 0)
			Message.addError(null, "Seleccione modalidad de pago !!!");
		if (inscripcion.getId().getCodser() == 0)
			Message.addError(null, "Seleccione servicio !!!");
		if (horIni == null)
			Message.addError(null, "Seleccione hora inicio de rutina !!!");
		if (horFin == null)
			Message.addError(null, "Seleccione hora fin de rutina !!!");
		if (horIni.getTime() >= horFin.getTime())
			Message.addError(null, "Hora de inicio de rutina debe ser mayor que la hora de inicio");
		if (corIns == 0)
			Message.addError(null, "No se generó código de inscripción.");
		return !Message.hayMensajes();
	}
	
	public void loadLazyModelInscripcion() {
		modelInscripcion = new LazyDataModel<InscripcionDTO>() {
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
			public InscripcionDTO getRowData(String rowKey) {
				return null;
			}

			@Override
			public Object getRowKey(InscripcionDTO dto) {
				return null;
			}

			@Override
			public List<InscripcionDTO> load(int first, int pageSize,
					String string, SortOrder so, Map<String, Object> map) {
				primero = first;
				ultimo = pageSize + first;
				loadData();
				return inscripciones;
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
			map = Gym.INSTANCE.listaInscripciones(valBus, limites);
			if (map != null && !map.isEmpty()) {
				inscripciones = (List<InscripcionDTO>) map.get("INSCRIPCIONES");
				count = (Integer) map.get("TOTAL");
				modelInscripcion.setRowCount(count);
			} else
				Message.addInfo(null, "No se encontraron coincidencias!!");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error al cargar la tabla .", ex);
		}
	}
	
	public void verInscripcion(){
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
			inscripcion = Gym.INSTANCE.getIncripcion(inscripcionPk);
			if(inscripcion!=null){
				Js.update("ing_inscripcion");
				Js.execute("PF('dlg_inscripcion').show()");
			}else
				Message.addError(null, "Error al cargar inscripción.");
		} catch (Exception e) {
			Message.addError(null, "Error al cargar inscripción.");
		}
		Js.update("mensajes");	
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

	public Inscripcion getInscripcion() {
		return inscripcion;
	}

	public void setInscripcion(Inscripcion inscripcion) {
		this.inscripcion = inscripcion;
	}

	public LazyDataModel<InscripcionDTO> getModelInscripcion() {
		return modelInscripcion;
	}

	public void setModelInscripcion(LazyDataModel<InscripcionDTO> modelInscripcion) {
		this.modelInscripcion = modelInscripcion;
	}

	public InscripcionPk getInscripcionPk() {
		return inscripcionPk;
	}

	public void setInscripcionPk(InscripcionPk inscripcionPk) {
		this.inscripcionPk = inscripcionPk;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public ClienteDTO getCliente() {
		return cliente;
	}

	public void setCliente(ClienteDTO cliente) {
		this.cliente = cliente;
	}

	public List<Servicio> getServicios() {
		return servicios;
	}

	public void setServicios(List<Servicio> servicios) {
		this.servicios = servicios;
	}

	public List<ModalidadPago> getModalidades() {
		return modalidades;
	}

	public void setModalidades(List<ModalidadPago> modalidades) {
		this.modalidades = modalidades;
	}

	public Date getHorIni() {
		return horIni;
	}

	public void setHorIni(Date horIni) {
		this.horIni = horIni;
	}

	public Date getHorFin() {
		return horFin;
	}

	public void setHorFin(Date horFin) {
		this.horFin = horFin;
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
