package pe.com.gym.util;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Clase permite ingresar mensajes al contexto.
 * @author Nelson Abel Barranzuela I. 
 */

public final class Message {
	
	/**
	 * Agrega mensaje de información al contexto
	 * @param clientId
	 * @param detail --> Detalle del mensaje
	 */
    public static void addInfo(String clientId, String detail) {
        FacesContext fc = FacesContext.getCurrentInstance();
        FacesMessage msgInfo = new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", detail);
        fc.addMessage(clientId, msgInfo);
    }
    
    /**
	 * Agrega mensaje de Error al contexto
	 * @param clientId
	 * @param detail --> Detalle del mensaje
	 */
    public static void addError(String clientId, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msgError = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", detail);
        context.addMessage(clientId, msgError);
    }

    /**
	 * Agrega mensaje de aviso al contexto
	 * @param clientId
	 * @param detail --> Detalle del mensaje
	 */
    public static void addAviso(String clientId, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msgError = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atencion", detail);
        context.addMessage(clientId, msgError);
    }
    
    /**
     * Indica si hay mensajes para mostrar
     * @return boolean
     */
    public static boolean hayMensajes(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	Iterator<FacesMessage> it = context.getMessages();
    	return it.hasNext();
    }
}
