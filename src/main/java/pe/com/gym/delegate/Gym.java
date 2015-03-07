
package pe.com.gym.delegate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import pe.com.gym.dto.ClienteDTO;
import pe.com.gym.entidades.Cliente;
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
            //windows
            //facadeLocal =(FacadeCOBRALocal) ic.lookup("java:global/COBRA-EAR/COBRA-EJB-1.0-SNAPSHOT/FacadeCOBRA!pe.cajapiura.cobra.facade.FacadeCOBRALocal");
            facadeLocal =(FacadeGYMLocal) ic.lookup("java:global/GYM_EAR/GYM_EJB-1.0-SNAPSHOT/FacadeGYM!pe.com.gym.facade.FacadeGYMLocal");
            //linux
            //facadeLocal =(FacadeGYMLocal) ic.lookup("java:global/COBRA-EAR-1.0-SNAPSHOT/COBRA-EJB-1.0-SNAPSHOT/FacadeCOBRA!pe.cajapiura.cobra.facade.FacadeCOBRALocal");
        } catch (NamingException ex) {
        	Logger.getLogger(Gym.class.getName()).log(Level.SEVERE, "No se pudo ubicar el recurso", ex);
        }
    }
    
    //Datos de clientes
	public int guardarCliente(Cliente cli) {
		return facadeLocal.guardarCliente(cli);
	}
	
	public long getCodigoClienteNvo() {
		return facadeLocal.getCodigoClienteNvo();
	}
	
	public Map<String, Object> listaClientes(String valBus,String tipper,int[] limites){
		return facadeLocal.listaClientes(valBus,tipper,limites);
	}
	
	public List<ClienteDTO> listaClientesPJ(){
		return facadeLocal.listaClientesPJ();
	}
	
	public Cliente getCliente(String tipPer,long codCli){
		return facadeLocal.getCliente(tipPer,codCli);
	}
	
	public int actualizaCliente(String tipper,Cliente cli){
		return facadeLocal.actualizaCliente(tipper, cli);
	}
	
	public int eliminaCliente(long codCli){
		return facadeLocal.eliminaCliente(codCli);
	}
}
