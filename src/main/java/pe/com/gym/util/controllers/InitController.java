package pe.com.gym.util.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import pe.com.gym.login.Usuario;


@ManagedBean(name = "InitController",eager = true)
@SessionScoped
public class InitController implements Serializable{
   
	private static final long serialVersionUID = 1L;
	private Map<String, Object> sessionVars;
	private static final Logger logger = Logger.getLogger(InitController.class.getName());
    
    public InitController() {
        sessionVars = new HashMap<String, Object>();        
    }
    
    @PostConstruct
    public void init(){
    	try {
    		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        	Usuario user = (Usuario) authentication.getPrincipal();
            sessionVars.put("USUARIO", user);
		} catch (Exception e) {
			logger.log(Level.SEVERE,e.getMessage());
		}
    }

    public Map<String, Object> getSessionVars() {
        return sessionVars;
    }

    public void setSessionVars(Map<String, Object> sessionVars) {
        this.sessionVars = sessionVars;
    }
}