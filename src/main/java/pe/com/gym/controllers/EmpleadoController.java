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
import pe.com.gym.dto.EmpleadoDTO;
import pe.com.gym.entidades.Empleado;
import pe.com.gym.entidades.Perfil;
import pe.com.gym.entidades.Usuario;
import pe.com.gym.util.Js;
import pe.com.gym.util.Message;

/**
 * @author Omar Yarleque
 * 
 */

@ManagedBean
@ViewScoped
public class EmpleadoController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(EmpleadoController.class.getName());
	private int primero;
	private int ultimo;
	private int grabar;
	private int codEmp;
	private int codPer;
	private boolean modaActiva;
	private boolean verGuar;
	private boolean verActualizar;
	private boolean read;
	private String valBus;
	private String passConfirm;
	private Empleado empleado;
	private Usuario usuario;
	private List<Perfil> perfiles;
	private List<EmpleadoDTO> empleados;
	private LazyDataModel<EmpleadoDTO> modelEmpleado;

	public EmpleadoController() {
	}

	@PostConstruct
	public void init() {
		empleado = new Empleado();
		verGuar = true;
		verActualizar = false;
		read = false;
	}

	public void cargarClave() {
		empleado = new Empleado();
		//codEmp = Gym.INSTANCE.getCodigoModalidadNva();
		if (codEmp != 0) {
			Js.execute("PF('dlg_empleado').show()");
		} else
			Message.addError(null, "Error al cargar código de modalidad.");
		Js.update("mensajes");
	}
	
	public void saveEmpleado(){
		if (validarDatos()) {
			int res = 0;
			empleado.setCodemp(codEmp);
			empleado.setUsureg("");
			empleado.setFecreg(new java.sql.Date(new java.util.Date().getTime()));
			//res = Gym.INSTANCE.registraModalidad(empleado);
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
	
	public void actualizarEmpleado(){
		int res = 0;
		try {
			if(empleado!=null){
				//res = Gym.INSTANCE.actualizaModalidad(empleado);
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
	
	public void eliminarEmpleado(){
		int res = 0;
		try {
			if(codEmp!=0){
				res = Gym.INSTANCE.cambiaEstadoModalidad(codEmp, 1);
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
		codEmp = 0;
		empleado = null;
	}
	
	public void cargarLista() {
		loadLazyModelEmpleado();
	}

	public boolean validarDatos() {
		if (empleado.getNomemp().trim().equals(""))
			Message.addError(null, "Ingrese nombre.");
		if (empleado.getApeemp().trim().equals(""))
			Message.addError(null, "Ingrese apellido.");
		if (empleado.getDiremp().trim().equals(""))
			Message.addError(null, "Ingrese dirección.");
		if (empleado.getDniemp().trim().equals(""))
			Message.addError(null, "Ingrese DNI.");
		if (empleado.getUsuemp().trim().equals(""))
			Message.addError(null, "Ingrese usuario.");
		if (codPer == 0)
			Message.addError(null, "Seleccione perfil.");
		if (usuario.getUsuemp().trim().equals(""))
			Message.addError(null, "Seleccione usuario.");
		if (usuario.getPasemp().trim().equals(""))
			Message.addError(null, "Ingrese contraseña.");
		if (passConfirm.trim().equals(""))
			Message.addError(null, "Confirme contraseña.");
		if (!passConfirm.trim().equals(usuario.getPasemp()))
			Message.addError(null, "Las contraseñas no coinciden.");
		return !Message.hayMensajes();
	}
	
	public void loadLazyModelEmpleado() {
		modelEmpleado = new LazyDataModel<EmpleadoDTO>() {
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
			public EmpleadoDTO getRowData(String rowKey) {
				return null;
			}

			@Override
			public Object getRowKey(EmpleadoDTO dto) {
				return null;
			}

			@Override
			public List<EmpleadoDTO> load(int first, int pageSize,
					String string, SortOrder so, Map<String, Object> map) {
				primero = first;
				ultimo = pageSize + first;
				loadData();
				return empleados;
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
				//empleados = (List<ModalidadPago>) map.get("EMPLEADOS");
				count = (Integer) map.get("TOTAL");
				modelEmpleado.setRowCount(count);
			} else
				Message.addInfo(null, "No se encontraron coincidencias!!");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error al cargar la tabla .", ex);
		}
	}
	
	public void verEmpleado(){
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
			//empleado = Gym.INSTANCE.getModalidad(codEmp);
			if(empleado!=null){
				codEmp = empleado.getCodemp();
				Js.update("ing_empleado");
				Js.execute("PF('dlg_empleado').show()");
			}else
				Message.addError(null, "Error al cargar empleado.");
		} catch (Exception e) {
			Message.addError(null, "Error al cargar empleado.");
		}
		Js.update("mensajes");	
	}

	public int getGrabar() {
		return grabar;
	}

	public void setGrabar(int grabar) {
		this.grabar = grabar;
	}

	public int getCodEmp() {
		return codEmp;
	}

	public void setCodEmp(int codEmp) {
		this.codEmp = codEmp;
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

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public LazyDataModel<EmpleadoDTO> getModelEmpleado() {
		return modelEmpleado;
	}

	public void setModelEmpleado(LazyDataModel<EmpleadoDTO> modelEmpleado) {
		this.modelEmpleado = modelEmpleado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Perfil> getPerfiles() {
		return perfiles;
	}

	public void setPerfiles(List<Perfil> perfiles) {
		this.perfiles = perfiles;
	}

	public int getCodPer() {
		return codPer;
	}

	public void setCodPer(int codPer) {
		this.codPer = codPer;
	}

	public String getPassConfirm() {
		return passConfirm;
	}

	public void setPassConfirm(String passConfirm) {
		this.passConfirm = passConfirm;
	}	
}
