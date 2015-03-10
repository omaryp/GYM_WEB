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
import pe.com.gym.entidades.Servicio;
import pe.com.gym.util.Js;
import pe.com.gym.util.Message;

/**
 * @author Omar Yarleque
 * 
 */

@ManagedBean
@ViewScoped
public class ServicioController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ServicioController.class.getName());
	private int primero;
	private int ultimo;
	private int grabar;
	private int codSer;
	private boolean serActivo;
	private boolean verGuar;
	private boolean verActualizar;
	private boolean read;
	private String valBus;
	private Servicio servicio;
	private List<Servicio> servicios;
	private LazyDataModel<Servicio> modelServicio;

	public ServicioController() {
	}

	@PostConstruct
	public void init() {
		verGuar = true;
		verActualizar = false;
		read = false;
	}

	public void cargarClave() {
		servicio = new Servicio();
		codSer = Gym.INSTANCE.getCodigoServicioNvo();
		if (codSer != 0) {
			Js.execute("PF('dlg_servicio').show()");
		} else
			Message.addError(null, "Error al cargar código de Servicio");
		Js.update("mensajes");
	}
	
	public void saveServicio(){
		if (validarDatos()) {
			int res = 0;
			servicio.setCodser(codSer);
			servicio.setUsureg("");
			servicio.setFecreg(new java.sql.Date(new java.util.Date().getTime()));
			res = Gym.INSTANCE.registraServicio(servicio);
			switch (res) {
			case 0:
				Message.addInfo(null, "Servicio registrado correctamente !!!");
				break;

			default:
				Message.addError(null, "Error al guarda servicio !!!");
				break;
			}
		}
	}
	
	public void actualizarServicio(){
		int res = 0;
		try {
			if(servicio!=null){
				res = Gym.INSTANCE.actualizaServicio(servicio);
				switch (res) {
					case 0:
						Message.addInfo(null, "Se actualizó correctamenete !!!");
						break;
					default:
						Message.addError(null, "Error al actualizar servicio !!!");
						break;
				}
			}
		} catch (Exception e) {
			Message.addError(null, "Error al actualizar el cliente.");
		}
		Js.update("mensajes");
	}
	
	public void eliminarServicio(){
		int res = 0;
		try {
			if(codSer!=0){
				res = Gym.INSTANCE.cambiaEstadoModalidad(codSer, 1);
				switch (res) {
					case 0:
						Message.addInfo(null, "Se dió de baja el servicio !!!");
						break;
					default:
						Message.addError(null, "Error al dar de baja servicio !!!");
						break;
				}
			}
		} catch (Exception e) {
			Message.addError(null, "Error al dar de baja servicio !!!");
		}
		Js.update("mensajes");
	}
	
	public void salir(){
		limpiarformulario();
		reiniciarflags();
		Js.execute("PF('dlg_servicio').hide()");
	}
	
	public void reiniciarflags() {
		verActualizar = false;
		verGuar = true;
	}

	public void limpiarformulario() {
		codSer = 0;
		servicio = null;
	}
	
	public void cargarLista() {
		loadLazyModelServicios();
	}

	public boolean validarDatos() {
		if (servicio.getNomser().trim().equals(""))
			Message.addError(null, "Ingrese referencia.");
		if (servicio.getMonser() == 0)
			Message.addError(null, "Ingrese días de la modalidad.");
		if (servicio.getDesser().equals(""))
			Message.addError(null, "Ingrese dni.");
		if (codSer == 0)
			Message.addError(null, "No se generó código de cliente.");
		return !Message.hayMensajes();
	}
	
	public void loadLazyModelServicios() {
		modelServicio = new LazyDataModel<Servicio>() {
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
			public Servicio getRowData(String rowKey) {
				return null;
			}

			@Override
			public Object getRowKey(Servicio dto) {
				return null;
			}

			@Override
			public List<Servicio> load(int first, int pageSize,
					String string, SortOrder so, Map<String, Object> map) {
				primero = first;
				ultimo = pageSize + first;
				loadData();
				return servicios;
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
				servicios = (List<Servicio>) map.get("SERVICIOS");
				count = (Integer) map.get("TOTAL");
				modelServicio.setRowCount(count);
			} else
				Message.addInfo(null, "No se encontraron coincidencias!!");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error al cargar la tabla .", ex);
		}
	}
	
	public void verServicio(){
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
			servicio = Gym.INSTANCE.getServicio(codSer);
			if(servicio!=null){
				codSer = servicio.getCodser();
				Js.update("ing_modalidad");
				Js.execute("PF('dlg_modalidad').show()");
			}else
				Message.addError(null, "Error al cargar modalidad.");
		} catch (Exception e) {
			Message.addError(null, "Error al cargar modalidad.");
		}
		Js.update("mensajes");	
	}

	public int getGrabar() {
		return grabar;
	}

	public void setGrabar(int grabar) {
		this.grabar = grabar;
	}

	public int getCodSer() {
		return codSer;
	}

	public void setCodSer(int codSer) {
		this.codSer = codSer;
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

	public String getValBus() {
		return valBus;
	}

	public void setValBus(String valBus) {
		this.valBus = valBus;
	}

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

	public LazyDataModel<Servicio> getModelServicio() {
		return modelServicio;
	}

	public void setModelServicio(LazyDataModel<Servicio> modelServicio) {
		this.modelServicio = modelServicio;
	}

	public boolean isSerActivo() {
		return serActivo;
	}

	public void setSerActivo(boolean serActivo) {
		this.serActivo = serActivo;
	}
	
}
