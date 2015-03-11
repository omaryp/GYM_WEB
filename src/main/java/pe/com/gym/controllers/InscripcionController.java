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

import pe.com.gym.delegate.Gym;
import pe.com.gym.entidades.Inscripcion;
import pe.com.gym.util.Js;
import pe.com.gym.util.Message;

/**
 * @author Omar Yarleque
 * 
 */

@ManagedBean
@ViewScoped
public class InscripcionController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(InscripcionController.class.getName());
	private int primero;
	private int ultimo;
	private int grabar;
	private int corIns;
	private boolean verGuar;
	private boolean verActualizar;
	private boolean read;
	private String valBus;
	private Inscripcion inscripcion;
	private List<Inscripcion> inscripciones;
	private LazyDataModel<Inscripcion> modelInscripcion;

	public InscripcionController() {
	}

	@PostConstruct
	public void init() {
		verGuar = true;
		verActualizar = false;
		read = false;
	}

	public void cargarClave() {
		inscripcion = new Inscripcion();
		//codMod = Gym.INSTANCE.getCodigoModalidadNva();
		if (corIns != 0) {
			Js.execute("PF('dlg_inscripcion').show()");
		} else
			Message.addError(null, "Error al cargar código del cliente.");
		Js.update("mensajes");
	}
	
	public void saveInscripcion(){
		if (validarDatos()) {
			int res = 0;
			inscripcion.setCorrel(corIns);
			inscripcion.setUsureg("");
			inscripcion.setFecreg(new java.sql.Date(new java.util.Date().getTime()));
			//res = Gym.INSTANCE.registraModalidad(modalidad);
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
				//res = Gym.INSTANCE.actualizaModalidad(modalidad);
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
	
	public void eliminarInscripcion(){
		int res = 0;
		try {
			if(corIns!=0){
				//res = Gym.INSTANCE.cambiaEstadoModalidad(codMod, 1);
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
	}
	
	public void salir(){
		limpiarformulario();
		reiniciarflags();
		Js.execute("PF('dlg_modalidad').hide()");
	}
	
	public void reiniciarflags() {
		verActualizar = false;
		verGuar = true;
	}

	public void limpiarformulario() {
		corIns = 0;
		inscripcion = null;
	}
	
	public void cargarLista() {
		loadLazyModelInscripcion();
	}

	public boolean validarDatos() {
		if (inscripcion.getCodcli() == 0)
			Message.addError(null, "Seleccione cliente !!!");
		if (inscripcion.getCodmod() == 0)
			Message.addError(null, "Seleccione modalidad de pago !!!");
		if (inscripcion.getCodser() == 0)
			Message.addError(null, "Seleccione servicio !!!");
		if (corIns == 0)
			Message.addError(null, "No se generó código de inscripción.");
		return !Message.hayMensajes();
	}
	
	public void loadLazyModelInscripcion() {
		modelInscripcion = new LazyDataModel<Inscripcion>() {
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
			public Inscripcion getRowData(String rowKey) {
				return null;
			}

			@Override
			public Object getRowKey(Inscripcion dto) {
				return null;
			}

			@Override
			public List<Inscripcion> load(int first, int pageSize,
					String string, SortOrder so, Map<String, Object> map) {
				primero = first;
				ultimo = pageSize + first;
				loadData();
				return inscripciones;
			}
		};
	}

	public void loadData() {
		int[] limites = new int[2];
		Map<String, Object> map = null;
		limites[0] = primero;
		limites[1] = ultimo;
		Integer count;
		try {
			map = Gym.INSTANCE.listaModalidades(valBus, limites);
			if (map != null && !map.isEmpty()) {
				//inscripciones = (List<Inscripciones>) map.get("INSCRIPCIONES");
				count = (Integer) map.get("TOTAL");
				modelInscripcion.setRowCount(count);
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
			//inscripcion = Gym.INSTANCE.getModalidad(codMod);
			if(inscripcion!=null){
				corIns = inscripcion.getCorrel();
				Js.update("ing_modalidad");
				Js.execute("PF('dlg_modalidad').show()");
			}else
				Message.addError(null, "Error al cargar modalidad.");
		} catch (Exception e) {
			Message.addError(null, "Error al cargar modalidad.");
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
	
}
