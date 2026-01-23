/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.bean;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import senadi.gob.ec.mod.model.Usuario;
import senadi.gob.ec.mod.ucc.Controlador;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author michael
 */
@ManagedBean(name = "changePassBean")
@ViewScoped
public class ChangePassBean implements Serializable {

    private Usuario usuario;
    private LoginBean loginbean;

    private String passActual;
    private String confirmarPass;
    private String newPass;
    private String userName;

    public ChangePassBean() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        loginbean = (LoginBean) session.getAttribute("loginBean");
        usuario = loginbean.getUsuario();
        passActual = "";
        confirmarPass = "";
        newPass = "";
        userName = usuario.getLogin();
    }

    public void savePass(ActionEvent ae) {
        FacesMessage msg = null;
        if (userName.trim().equals("") || passActual.trim().equals("") || confirmarPass.trim().equals("") || newPass.trim().equals("")) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "CAMPOS VACÍOS");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {            
            if (Operaciones.md5(passActual).equals(loginbean.getUsuario().getPassword())) {
                if (newPass.equals(confirmarPass)) {
                    usuario.setLogin(userName);
                    usuario.setPassword(Operaciones.md5(newPass));;
                    Controlador c = new Controlador();
                    if (c.updateUsuario(usuario)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "GUARDADO", "CREDENCIALES ACTUALIZADAS");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        goToPagosLinea();
                    }else{
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Error al actualizar contraseña");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "No coincide la confirmación de la nueva contraseña");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La contraseña actual no es correcta");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void goToPagosLinea() {
//        RequestContext context = RequestContext.getCurrentInstance();        
//        context.addCallbackParam("doit", true);
//        context.addCallbackParam("view", "renovaciones.xhtml");
        
        PrimeFaces.current().ajax().addCallbackParam("doit", true);
        PrimeFaces.current().ajax().addCallbackParam("view", "renovaciones.xhtml");
    }

    /**
     * @return the passActual
     */
    public String getPassActual() {
        return passActual;
    }

    /**
     * @param passActual the passActual to set
     */
    public void setPassActual(String passActual) {
        this.passActual = passActual;
    }

    /**
     * @return the confirmarPass
     */
    public String getConfirmarPass() {
        return confirmarPass;
    }

    /**
     * @param confirmarPass the confirmarPass to set
     */
    public void setConfirmarPass(String confirmarPass) {
        this.confirmarPass = confirmarPass;
    }

    /**
     * @return the newPass
     */
    public String getNewPass() {
        return newPass;
    }

    /**
     * @param newPass the newPass to set
     */
    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the loginbean
     */
    public LoginBean getLoginbean() {
        return loginbean;
    }

    /**
     * @param loginbean the loginbean to set
     */
    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
