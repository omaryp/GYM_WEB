package pe.com.gym.delegate;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import pe.com.gym.dto.ClienteDTO;
import pe.com.gym.entidades.Cliente;
import pe.com.gym.entidades.Empleado;
import pe.com.gym.entidades.Inscripcion;
import pe.com.gym.entidades.InscripcionPk;
import pe.com.gym.entidades.Menu;
import pe.com.gym.entidades.ModalidadPago;
import pe.com.gym.entidades.Perfil;
import pe.com.gym.entidades.Servicio;
import pe.com.gym.entidades.Usuario;
import pe.com.gym.facade.FacadeGYMLocal;

/**
 * @author Omar Yarleque
 */
public enum Gym {
	INSTANCE;
	FacadeGYMLocal facadeLocal;
	Logger logger = Logger.getLogger(Gym.class.getName());

	private Gym() {
		InitialContext ic = null;
		try {
			ic = new InitialContext();
			// windows
			// facadeLocal =(FacadeCOBRALocal)ic.lookup("java:global/COBRA-EAR/COBRA-EJB-1.0-SNAPSHOT/FacadeCOBRA!pe.cajapiura.cobra.facade.FacadeCOBRALocal");
			facadeLocal = (FacadeGYMLocal) ic.lookup("java:global/GYM_EAR/GYM_EJB-1.0-SNAPSHOT/FacadeGYM!pe.com.gym.facade.FacadeGYMLocal");
			// linux
			//facadeLocal =(FacadeGYMLocal)ic.lookup("java:global/GYM-EAR-1.0-SNAPSHOT/GYM-EJB-1.0-SNAPSHOT/FacadeGYM!pe.com.gym.facade.FacadeGYMLocal");
		} catch (NamingException ex) {
			Logger.getLogger(Gym.class.getName()).log(Level.SEVERE,
					"No se pudo ubicar el recurso", ex);
		}
	}

	// Datos de clientes
	public int guardarCliente(Cliente cli) {
		return facadeLocal.guardarCliente(cli);
	}

	public long getCodigoClienteNvo() {
		return facadeLocal.getCodigoClienteNvo();
	}

	public Map<String, Object> listaClientes(String valBus, String tipper,
			int[] limites) {
		return facadeLocal.listaClientes(valBus, tipper, limites);
	}

	public List<ClienteDTO> listaClientesPJ() {
		return facadeLocal.listaClientesPJ();
	}

	public Cliente getCliente(String tipPer, long codCli) {
		return facadeLocal.getCliente(tipPer, codCli);
	}

	public int actualizaCliente(String tipper, Cliente cli) {
		return facadeLocal.actualizaCliente(tipper, cli);
	}

	public int eliminaCliente(long codCli) {
		return facadeLocal.eliminaCliente(codCli);
	}
	
	public Map<String, Object> busquedaGeneral(String valor, long codCli, int tipBus,int[] limites){
		return facadeLocal.busquedaGeneral(valor, codCli, tipBus, limites);
	}

	// para las modalidades de pago
	public long getCodigoModalidadNva() {
		return facadeLocal.getCodigoModalidadNva();
	}

	public int actualizaModalidad(ModalidadPago modal) {
		return facadeLocal.actualizaModalidad(modal);
	}

	public int registraModalidad(ModalidadPago modal) {
		return facadeLocal.registraModalidad(modal);
	}

	public Map<String, Object> listaModalidades(String valBus, int[] limites) {
		return facadeLocal.listaModalidades(valBus, limites);
	}

	public List<ModalidadPago> listaModalidades() {
		return facadeLocal.listaModalidades();
	}

	public ModalidadPago getModalidad(long codMod) {
		return facadeLocal.getModalidad(codMod);
	}

	public int cambiaEstadoModalidad(long codMod, int nvoEstado) {
		return facadeLocal.cambiaEstadoModalidad(codMod, nvoEstado);
	}

	// para los servicios
	public Servicio getServicio(int codSer) {
		return facadeLocal.getServicio(codSer);
	}

	public int registraServicio(Servicio servic) {
		return facadeLocal.registraServicio(servic);
	}

	public int actualizaServicio(Servicio servic) {
		return facadeLocal.actualizaServicio(servic);
	}

	public int getCodigoServicioNvo() {
		return facadeLocal.getCodigoServicioNvo();
	}

	public Map<String, Object> listaServicios(String valBus, int[] limites) {
		return facadeLocal.listaServicios(valBus, limites);
	}

	public List<Servicio> listaServicios() {
		return facadeLocal.listaServicios();
	}

	public int cambiaEstadoServicio(long codMod, int nvoEstado) {
		return facadeLocal.cambiaEstadoServicio(codMod, nvoEstado);
	}

	// para las inscripciones
	public Inscripcion getIncripcion(InscripcionPk id) {
		return facadeLocal.getIncripcion(id);
	}

	public int registraInscripcion(Inscripcion ins) {
		return facadeLocal.registraInscripcion(ins);
	}

	public int actualizaInscripcion(Inscripcion ins) {
		return facadeLocal.actualizaInscripcion(ins);
	}

	public int getCorrelativoIncripcion() {
		return facadeLocal.getCorrelativoIncripcion();
	}

	public Map<String, Object> listaInscripciones(String valBus, int[] limites) {
		return facadeLocal.listaInscripciones(valBus, limites);
	}

	public int cancelarInscripcion(InscripcionPk id, int nvoEstado) {
		return facadeLocal.cancelarInscripcion(id, nvoEstado);
	}

	// para los usuarios
	public int registraUsuario(Usuario usu) {
		return facadeLocal.registraUsuario(usu);
	}

	public int actualizaUsuario(Usuario usu) {
		return facadeLocal.actualizaUsuario(usu);
	}

	public boolean validaUsuario(String usu, String pass) {
		return facadeLocal.validaUsuario(usu, pass);
	}

	public Usuario getUsuario(int codemp) {
		return facadeLocal.getUsuario(codemp);
	}

	public int eliminaUsuario(int codEmp) {
		return facadeLocal.eliminaUsuario(codEmp);
	}
	
	public Usuario getUsuario(String user){
		return facadeLocal.getUsuario(user);
	}

	// para el menu de los usuarios
	public List<Menu> getMenus(int codEmp) {
		return facadeLocal.getMenus(codEmp);
	}

	// para los perfiles
	public Perfil getPerfil(int codEmp) {
		return facadeLocal.getPerfil(codEmp);
	}

	// para los empleados
	public Empleado getEmpleado(int codigoEmpleado) {
		return facadeLocal.getEmpleado(codigoEmpleado);
	}
	
	

}
