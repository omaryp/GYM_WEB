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

import pe.com.gym.entidades.Menu;
import pe.com.gym.entidades.Perfil;
import pe.com.gym.util.Js;
import pe.com.gym.util.Message;

/**
 * @author Omar Yarleque
 * 
 */

@ManagedBean
@ViewScoped
public class PerfilController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PerfilController.class.getName());
	private int primero;
	private int ultimo;
	private int grabar;
	private int codPer;
	private boolean perfilActivo;
	private boolean verGuar;
	private boolean verActualizar;
	private boolean read;
	private String valBus;
	private Perfil perfil;
	private List<Menu> menus;
	private List<Perfil> perfiles;
	private int [] menusSeleccionados;
	private LazyDataModel<Perfil> modelPerfil;

	public PerfilController() {
	}

	@PostConstruct
	public void init() {
		perfil = new Perfil();
		verGuar = true;
		verActualizar = false;
		read = false;
	}

	public void cargarClave() {
		perfil = new Perfil();
		//codPer = Gym.INSTANCE.getCodigoModalidadNva();
		if (codPer != 0) {
			Js.execute("PF('dlg_perfil').show()");
		} else
			Message.addError(null, "Error al cargar código de perfil.");
		Js.update("mensajes");
	}
	
	public void savePerfil(){
		if (validarDatos()) {
			int res = 0;
			perfil.setCodper(codPer);
			perfil.setUsureg("");
			perfil.setFecreg(new Date());
			//res = Gym.INSTANCE.registraModalidad(modalidad);
			switch (res) {
			case 0:
				Message.addInfo(null, "Perfil registrado correctamente !!!");
				break;

			default:
				Message.addError(null, "Error al guardar perfil !!!");
				break;
			}
		}
	}
	
	public void actualizarPerfil(){
		int res = 0;
		try {
			if(perfil!=null){
				//res = Gym.INSTANCE.actualizaModalidad(perfil);
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
	
	public void eliminarPerfil(){
		int res = 0;
		try {
			if(codPer!=0){
				//res = Gym.INSTANCE.cambiaEstadoModalidad(codPer,Estado.DESACTIVADO.getValue());
				switch (res) {
					case 0:
						Message.addInfo(null, "Se dió de baja al perfil !!!");
						break;
					default:
						Message.addError(null, "Error al dar de baja a este perfil !!!");
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
		codPer = 0;
		perfil = null;
	}
	
	public void cargarLista() {
		loadLazyModelModalidades();
	}

	public boolean validarDatos() {
		if (perfil.getNomper().trim().equals(""))
			Message.addError(null, "Ingrese nombre perfil.");
		if (perfil.getDesper().trim().equals(""))
			Message.addError(null, "Ingrese descripción perfil.");
		return !Message.hayMensajes();
	}
	
	public void loadLazyModelModalidades() {
		modelPerfil = new LazyDataModel<Perfil>() {
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
			public Perfil getRowData(String rowKey) {
				return null;
			}

			@Override
			public Object getRowKey(Perfil dto) {
				return null;
			}

			@Override
			public List<Perfil> load(int first, int pageSize,
					String string, SortOrder so, Map<String, Object> map) {
				primero = first;
				ultimo = pageSize + first;
				loadData();
				return perfiles;
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
			//map = Gym.INSTANCE.listaModalidades(valBus, limites);
			if (map != null && !map.isEmpty()) {
				//perfiles = (List<ModalidadPago>) map.get("PERFILES");
				count = (Integer) map.get("TOTAL");
				modelPerfil.setRowCount(count);
			} else
				Message.addInfo(null, "No se encontraron coincidencias!!");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error al cargar la tabla .", ex);
		}
	}
	
	public void verPerfil(){
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
			//perfil = Gym.INSTANCE.getModalidad(codMod);
			if(perfil!=null){
				codPer = perfil.getCodper();
				Js.update("ing_perfil");
				Js.execute("PF('dlg_perfil').show()");
			}else
				Message.addError(null, "Error al cargar perfil.");
		} catch (Exception e) {
			Message.addError(null, "Error al cargar perfil.");
		}
		Js.update("mensajes");	
	}

	public int getGrabar() {
		return grabar;
	}

	public void setGrabar(int grabar) {
		this.grabar = grabar;
	}

	public int getCodPer() {
		return codPer;
	}

	public void setCodPer(int codPer) {
		this.codPer = codPer;
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

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public LazyDataModel<Perfil> getModelPerfil() {
		return modelPerfil;
	}

	public void setModelPerfil(LazyDataModel<Perfil> modelPerfil) {
		this.modelPerfil = modelPerfil;
	}

	public boolean isPerfilActivo() {
		return perfilActivo;
	}

	public void setPerfilActivo(boolean perfilActivo) {
		this.perfilActivo = perfilActivo;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public int[] getMenusSeleccionados() {
		return menusSeleccionados;
	}

	public void setMenusSeleccionados(int[] menusSeleccionados) {
		this.menusSeleccionados = menusSeleccionados;
	}

}
