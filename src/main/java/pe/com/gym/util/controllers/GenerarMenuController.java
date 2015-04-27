package pe.com.gym.util.controllers;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import pe.com.gym.entidades.Menu;
import pe.com.gym.login.Usuario;


/**
 * @author Omar Yarleque
 *
 */
@ManagedBean
@SessionScoped
public class GenerarMenuController implements Serializable {

    @ManagedProperty(value = "#{InitController}")
    private InitController initController;	
	private static final long serialVersionUID = 1L;
    private List<Menu> menuPrincipalBean;
    private Menu menuSeleccionado;
    private Usuario userIniSesion;
    private MenuModel menuModelBean;
    private String fecha;
    
    private String tituloPanel;
    private final static Logger logger = Logger.getLogger(GenerarMenuController.class.getName());
    
    public GenerarMenuController() {
    }
    
    @PostConstruct
    public void init() {
    	logger.log(Level.INFO,"Entrando al postConstrut menu ....");    
    	userIniSesion=(Usuario)getInitController().getSessionVars().get("USUARIO");
    	formateaFecha();
        StringBuilder url=new StringBuilder();
        try {
            menuPrincipalBean = userIniSesion.getOpciones();
            if (menuPrincipalBean == null) {
            	url.append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath());
            	url.append("/login/login.do");
                FacesContext.getCurrentInstance().getExternalContext().redirect(url.toString());
            } 
        } catch (NullPointerException e) {
			try {
    			url.append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath());
    			url.append("/login/login.do");
    			FacesContext.getCurrentInstance().getExternalContext().redirect(url.toString());
    		} catch (Exception ex) {
    			logger.log(Level.SEVERE, "No se pudo redireccionar: {0}",ex);
    		}
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en el Objeto", e.getStackTrace());
        } 
    }
    
    public void formateaFecha(){
    	Date now = new Date();
    	DateFormat df4 = DateFormat.getDateInstance(DateFormat.FULL);
    	fecha= df4.format(now);
    }

    public List<Menu> getMenuPrincipalBean() {
        if (this.menuPrincipalBean == null) {
            this.menuPrincipalBean = new ArrayList<Menu>();
        }
        return menuPrincipalBean;
    }
    
    public void setMenuPrincipalBean(List<Menu> menuPrincipalBean) {
        this.menuPrincipalBean = menuPrincipalBean;
    }

    public MenuModel getMenuModelBean() {
    	List<Menu> listaMenus = null;
    	DefaultMenuItem menuItem = null;
	    Menu opcion = null;
	    int indice = 0;
    	if (menuModelBean==null){
    		menuModelBean = new DefaultMenuModel();
		    try {
		        listaMenus = this.getMenuPrincipalBean();
			} catch (Exception ex) {
			    logger.log(Level.SEVERE, "error{0}", ex);
			}
			if (listaMenus != null) {
				while (indice<listaMenus.size()){
					opcion = listaMenus.get(indice);
	                menuItem = new DefaultMenuItem(opcion.getNommen());
	                menuItem.setUrl(opcion.getRutmen());
	                String id = "menu" + opcion.getCodmen();
	                menuItem.setId(id);
	                menuModelBean.addElement(menuItem);
		            indice++;
				}
			}
    	}
        return menuModelBean;
    }

	public InitController getInitController() {
		return initController;
	}

	public void setInitController(InitController initController) {
		this.initController = initController;
	}
	
	public void setMenuModelBean(MenuModel menuModelBean) {
		this.menuModelBean = menuModelBean;
	}

	public Menu getMenuSeleccionado() {
		return menuSeleccionado;
	}

	public void setMenuSeleccionado(Menu menuSeleccionado) {
		this.menuSeleccionado = menuSeleccionado;
	}

	public Usuario getUserIniSesion() {
		return userIniSesion;
	}

	public void setUserIniSesion(Usuario userIniSesion) {
		this.userIniSesion = userIniSesion;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}
	
	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}	
}