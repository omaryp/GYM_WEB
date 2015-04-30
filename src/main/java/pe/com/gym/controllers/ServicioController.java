/**
 * 
 */
package pe.com.gym.controllers;

import java.io.Serializable;
import java.util.ArrayList;
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
import pe.com.gym.entidades.Servicio;
import pe.com.gym.entidades.TarifaServicio;
import pe.com.gym.entidades.TarifaServicioPK;
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
public class ServicioController implements Serializable {

	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{InitController}")
	private InitController initController;
	private int primero;
	private int ultimo;
	private int grabar;
	private int codSer;
	private int codMod;
	private int correl;
	private boolean serActivo;
	private boolean verGuar;
	private boolean verActualizar;
	private boolean read;
	private String valBus;
	private Servicio servicio;
	private TarifaServicio tarifa;
	private TarifaServicioPK idTarifa;
	private Usuario userSesion;
	private List<Servicio> servicios;
	private List<ModalidadPago> modalidades;
	private List<TarifaServicio> tarifas;
	private LazyDataModel<Servicio> modelServicio;
	private static final Logger logger = Logger.getLogger(ServicioController.class.getName());

	public ServicioController() {
	}

	@PostConstruct
	public void init() {
		userSesion = (Usuario)initController.getSessionVars().get("USUARIO");
		verGuar = true;
		verActualizar = false;
		read = false;
		cargarLista();
	}

	public void cargarClave() {
		servicio = new Servicio();
		codSer = Gym.INSTANCE.getCodigoServicioNvo();
		if (codSer != 0) {
			tarifas = new ArrayList<TarifaServicio>();
			Js.execute("PF('dlg_servicio').show()");
		} else
			Message.addError(null, "Error al cargar código de Servicio");
		Js.update("mensajes");
	}
	
	public void saveServicio(){
		if (validarDatos()) {
			int res = 0;
			servicio.setEstser(serActivo?Estado.ACTIVO.getValue():Estado.DESACTIVADO.getValue());
			servicio.setCodser(codSer);
			servicio.setUsureg("");
			servicio.setFecreg(new java.sql.Date(new java.util.Date().getTime()));
			res = Gym.INSTANCE.registraServicio(servicio,tarifas);
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
				servicio.setEstser(serActivo?Estado.ACTIVO.getValue():Estado.DESACTIVADO.getValue());
				servicio.setUsumod(userSesion.getUsername());
				servicio.setFecmod(new Date());
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
				res = Gym.INSTANCE.cambiaEstadoModalidad(codSer, Estado.DESACTIVADO.getValue());
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
		cargarLista();
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
			Message.addError(null, "Ingrese nombre servicio.");
		if (servicio.getDesser().equals(""))
			Message.addError(null, "Ingrese descripción.");
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
			map = Gym.INSTANCE.listaServicios(valBus, limites);
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
				serActivo = (servicio.getEstser()==Estado.ACTIVO.getValue())?true:false;
				Js.update("ing_modalidad");
				Js.execute("PF('dlg_modalidad').show()");
			}else
				Message.addError(null, "Error al cargar modalidad.");
		} catch (Exception e) {
			Message.addError(null, "Error al cargar modalidad.");
		}
		Js.update("mensajes");	
	}
	
	//tarifas
	public void cargaFrmTarifa(){		
		if(!servicio.getNomser().equals("")){
			modalidades = Gym.INSTANCE.listaModalidades();
			tarifa = new TarifaServicio();
			Js.execute("PF('dlg_tarifa').show()");
			Js.update("ing_tarifa");
		}else Message.addError(null, "Ingrese el nombre del servicio");
		Js.update("mensajes");
	}
	
	public void cargaCorrelativo(){
		idTarifa = Gym.INSTANCE.getCodigoTarifaNva(codSer,codMod);
	}
	
	public void agregaTarifa(){
		if(validarDatosTarifa()){
			cargaCorrelativo();
			tarifa.setId(idTarifa);
			tarifa.setEstado(Estado.ACTIVO.getValue());
			tarifa.setFecreg(new Date());
			tarifa.setUsureg("");
			tarifas.add(tarifa);
			limpiaDatosTarifa();
			Js.execute("PF('dlg_tarifa').hide()");
		}
		Js.update("mensajes");
	}
	
	public void limpiaDatosTarifa(){
		codMod = -1;
	}
	
	public boolean validarDatosTarifa(){
		if (codMod == 0 || codMod == -1)
			Message.addError(null, "Seleccione modalidad de pago.");
		if (tarifa.getMonto() == 0)
			Message.addError(null, "Ingrese monto.");
		return !Message.hayMensajes();
	}
	
	public void salirTarifa(){
		Js.execute("PF('dlg_tarifa').hide()");
		cargarListaTarifa();
	}
	
	public void cargarListaTarifa(){
		tarifas = Gym.INSTANCE.listaTarifas(servicio);
		Js.update("tbl_tarifas");
	}	
	////////////

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

	public List<TarifaServicio> getTarifas() {
		return tarifas;
	}

	public void setTarifas(List<TarifaServicio> tarifas) {
		this.tarifas = tarifas;
	}

	public List<ModalidadPago> getModalidades() {
		return modalidades;
	}

	public void setModalidades(List<ModalidadPago> modalidades) {
		this.modalidades = modalidades;
	}

	public TarifaServicio getTarifa() {
		return tarifa;
	}

	public void setTarifa(TarifaServicio tarifa) {
		this.tarifa = tarifa;
	}

	public int getCorrel() {
		return correl;
	}

	public void setCorrel(int correl) {
		this.correl = correl;
	}

	public TarifaServicioPK getIdTarifa() {
		return idTarifa;
	}

	public void setIdTarifa(TarifaServicioPK idTarifa) {
		this.idTarifa = idTarifa;
	}

	public int getCodMod() {
		return codMod;
	}

	public void setCodMod(int codMod) {
		this.codMod = codMod;
	}
		
}
