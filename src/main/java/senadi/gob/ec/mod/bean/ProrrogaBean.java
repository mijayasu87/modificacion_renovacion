/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIData;
import senadi.gob.ec.mod.model.Documento;
import senadi.gob.ec.mod.model.Historial;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.Prorroga;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.UploadNotificacion;
import senadi.gob.ec.mod.model.iepform.RenewalForm;
import senadi.gob.ec.mod.ucc.Controlador;
import senadi.gob.ec.mod.ucc.Operaciones;
import senadi.gob.ec.mod.ucc.Reusable;

/**
 *
 * @author michael
 */
@ManagedBean(name = "prorrogaBean")
@ViewScoped
public class ProrrogaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<Prorroga> prorrogas;
    private List<Prorroga> prorrogasFiltradas;
    private List<Prorroga> selectedProrrogas;

    private UIData prorrogasDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;
    private String exportName;

    private Prorroga prorroga;

    private LoginBean loginBean;

    private String historial;

    private List<Documento> archivos;

    private boolean usuarioConsulta;

    private String estadoTemp;

    public ProrrogaBean() {
        loadProrrogas();
    }

    private void loadProrrogas() {
        Controlador c = new Controlador();
        prorrogas = c.getProrrogas();
        numRegistros = "Número Registros Mostrados: " + prorrogas.size();
        exportName = "prorroga_" + Operaciones.formatDate(new Date());
        loginBean = getLogin();
        usuarioConsulta = !loginBean.isUsuarioConsulta();
        selectedProrrogas = new ArrayList<>();
    }

    public LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return (LoginBean) session.getAttribute("loginBean");
    }

    public boolean validarFechas() {
        try {
            fechaInicio.toString();
            fechaFin.toString();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void buscarProrroga(ActionEvent ae) {
        FacesMessage msg;
        if (criterio == null || criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else if (criterio.contains("'")) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO INGRESE CARACTERES ESPECIALES");
        } else {
            Controlador c = new Controlador();
            prorrogas = c.getProrrogasByCriteria(criterio.trim());
            numRegistros = "Número Registros Mostrados: " + prorrogas.size();
            if (prorrogas.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarProrrogasPorFecha(ActionEvent ae) {
        FacesMessage msg;
        if (validarFechas()) {
            Controlador c = new Controlador();
            prorrogas = c.getProrrogasByFecha(fechaInicio, fechaFin);
            numRegistros = "Número Registros Mostrados: " + prorrogas.size();
            if (prorrogas.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {
        saveEdit = "EDITAR";
        edicion = true;
        estadoTemp = "";
        FacesMessage msg;
        prorroga = (Prorroga) prorrogasDataTable.getRowData();
        if (prorroga != null) {
            Controlador c = new Controlador();
            prorroga = c.getProrrogaBySolicitud(prorroga.getSolicitud());
            dialogTitle = "EDITAR PRÓRROGA " + prorroga.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Prórroga: " + prorroga.getSolicitud() + "?";
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(prorroga.getSolicitud());
            if (rf.getId() != null) {
                prorroga.setIdRenewalForm(rf.getId());
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR PRÓRROGA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void guardarProrroga(ActionEvent ae) {
        FacesMessage msg;
        if (prorroga != null && prorroga.getId() != null) {
            Controlador c = new Controlador();
            if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                // Transferir la prórroga a otra pestaña (NOTIFICADAS / RENOVACIONES)
                if (estadoTemp.equals("NOTIFICADAS")) {
                    pasarANotificadas(c);
                } else if (estadoTemp.equals("RENOVACIONES")) {
                    pasarARenovaciones(c);
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SELECCIONE UN ESTADO VÁLIDO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
                return;
            }
            // Edición normal
            if (c.validarExistenciaProrroga(prorroga)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
            } else {
                prorroga.setSolicitud(prorroga.getSolicitud().toUpperCase());
                if (prorroga.getNumeroProrroga() == null) {
                    prorroga.setNumeroProrroga(c.getNextNumeroProrroga(prorroga.getFechaProrroga() != null ? prorroga.getFechaProrroga() : new Date()));
                }
                if (c.updateProrroga(prorroga)) {
                    c.saveHistorial("PRORROGA", "PRORROGA", prorroga.getSolicitud(), "EDITADO", 0, loginBean.getLogin());
                    loadProrrogas();
                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA EDITADA CON ÉXITO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL EDITAR LA PRÓRROGA");
                }
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE LA PRÓRROGA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Pasa la prórroga en edición a la pestaña NOTIFICADAS (la tabla `prorroga`
     * comparte la misma forma que `notificada`, por lo que la copia es directa).
     */
    private void pasarANotificadas(Controlador c) {
        FacesMessage msg;
        if (c.validarExistenciaNotificada(prorroga.getSolicitud())) {
            PrimeFaces.current().ajax().addCallbackParam("saved", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en notificadas con el mismo número de solicitud");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        Notificada n = new Notificada();
        n.setTipoSolicitante(prorroga.getTipoSolicitante());
        n.setSolicitud(prorroga.getSolicitud().toUpperCase());
        n.setFechaPresentacion(prorroga.getFechaPresentacion());
        n.setNoComprobantePresentSolic(prorroga.getNoComprobantePresentSolic());
        n.setNoComprobanteEmisionCert(prorroga.getNoComprobanteEmisionCert());
        n.setTotalFoliosExpediente(prorroga.getTotalFoliosExpediente());
        n.setNotificacion(prorroga.getNotificacion());
        n.setFechaCertificado(prorroga.getFechaCertificado());
        n.setTituloResolucion(prorroga.getTituloResolucion());
        n.setRegistroNo(prorroga.getRegistroNo());
        n.setFechaRegistro(prorroga.getFechaRegistro());
        n.setFechaVenceRegistro(prorroga.getFechaVenceRegistro());
        n.setDenominacion(prorroga.getDenominacion());
        n.setLema(prorroga.getLema());
        n.setSigno(prorroga.getSigno());
        n.setClase(prorroga.getClase());
        n.setProtege(prorroga.getProtege());
        n.setTitularActual(prorroga.getTitularActual());
        n.setTacNJ(prorroga.getTacNJ());
        n.setNacTitularAc(prorroga.getNacTitularAc());
        n.setDomicilioTitularAc(prorroga.getDomicilioTitularAc());
        n.setAr(prorroga.getAr());
        n.setNj(prorroga.getNj());
        n.setTitApodRepre(prorroga.getTitApodRepre());
        n.setApeApodRepre(prorroga.getApeApodRepre());
        n.setNomApodRepre(prorroga.getNomApodRepre());
        n.setFechaElaboraNotificacion(prorroga.getFechaElaboraNotificacion());
        n.setFechaNotifica(prorroga.getFechaNotifica());
        n.setCasilleroSenadi(prorroga.getCasilleroSenadi());
        n.setCasilleroJudicial(prorroga.getCasilleroJudicial());
        n.setRo(prorroga.getRo());
        n.setProvidencia(prorroga.getProvidencia());
        n.setFechaProvidencia(prorroga.getFechaProvidencia());
        n.setFechaNotificaPro(prorroga.getFechaNotificaPro());
        n.setResponsable(prorroga.getResponsable());
        n.setIdentificacion(prorroga.getIdentificacion());
        n.setNotificacionEmitida(prorroga.isNotificacionEmitida());
        n.setCertificadoEmitido(prorroga.isCertificadoEmitido());
        n.setCancelado(prorroga.getCancelado());
        n.setSolicitante(prorroga.getSolicitante());

        if (c.saveNotificada(n) && c.removeProrroga(prorroga)) {
            c.saveHistorial("NOTIFICADAS", "PRORROGA", n.getSolicitud(), "PASADO A", 0, loginBean.getLogin());
            loadProrrogas();
            PrimeFaces.current().ajax().addCallbackParam("saved", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
        } else {
            PrimeFaces.current().ajax().addCallbackParam("saved", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL PASAR LA PRÓRROGA A NOTIFICADAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Pasa la prórroga en edición a la pestaña RENOVACIONES.
     */
    private void pasarARenovaciones(Controlador c) {
        FacesMessage msg;
        if (c.validarExistenciaRenovacion(prorroga.getSolicitud())) {
            PrimeFaces.current().ajax().addCallbackParam("saved", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en renovaciones con el mismo número de solicitud");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        Renovacion renova = new Renovacion();
        renova.setEstado("");
        renova.setSolicitudSenadi(prorroga.getSolicitud().toUpperCase());
        renova.setFechaPresentacion(prorroga.getFechaPresentacion());
        renova.setNoComprobantePresentSolic(prorroga.getNoComprobantePresentSolic());
        renova.setNoComprobanteEmisionCert(prorroga.getNoComprobanteEmisionCert());
        renova.setTotalFoliosExpediente(prorroga.getTotalFoliosExpediente());
        renova.setFechaCertificado(new Date());
        renova.setCertificadoNo(c.getNextNumeroCertificado(renova.getFechaCertificado()));
        renova.setTituloResolucion(prorroga.getTituloResolucion());
        renova.setRegistroNo(prorroga.getRegistroNo());
        renova.setFechaRegistro(prorroga.getFechaRegistro());
        renova.setFechaVenceRegistro(prorroga.getFechaVenceRegistro());
        renova.setDenominacion(prorroga.getDenominacion());
        renova.setLema(prorroga.getLema());
        renova.setSigno(prorroga.getSigno());
        renova.setClase(prorroga.getClase());
        renova.setProtege(prorroga.getProtege());
        renova.setTitularActual(prorroga.getTitularActual());
        renova.setTacNJ(prorroga.getTacNJ());
        renova.setNacTitularAc(prorroga.getNacTitularAc());
        renova.setAbogadoPatrocinadorApeApoRepre(prorroga.getApeApodRepre());
        renova.setCasilleroSenadi(prorroga.getCasilleroSenadi());
        renova.setCasilleroJudicial(prorroga.getCasilleroJudicial());
        renova.setResponsable(prorroga.getResponsable());
        renova.setIdentificacion(prorroga.getIdentificacion());
        renova.setCertificadoEmitido(prorroga.isCertificadoEmitido());
        renova.setNotificacionEmitida(prorroga.isNotificacionEmitida());
        renova.setCancelado(prorroga.getCancelado());
        renova.setObservacion(prorroga.getRo());

        if (c.saveRenovacion(renova) && c.removeProrroga(prorroga)) {
            c.saveHistorial("RENOVACIONES", "PRORROGA", renova.getSolicitudSenadi(), "PASADO A", 0, loginBean.getLogin());
            loadProrrogas();
            PrimeFaces.current().ajax().addCallbackParam("saved", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
        } else {
            PrimeFaces.current().ajax().addCallbackParam("saved", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL PASAR LA PRÓRROGA A RENOVACIONES");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onEstadoSelectedListener() {
        if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
            saveEdit = "ENVIAR";
        } else {
            saveEdit = "EDITAR";
        }
    }

    public void eliminarProrroga(ActionEvent ae) {
        FacesMessage msg;
        prorroga = (Prorroga) prorrogasDataTable.getRowData();
        if (prorroga != null) {
            Controlador c = new Controlador();
            if (c.removeProrroga(prorroga)) {
                c.saveHistorial("PRORROGA", "PRORROGA", prorroga.getSolicitud(), "ELIMINADO", 0, loginBean.getLogin());
                loadProrrogas();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA " + prorroga.getSolicitud() + " ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR LA PRÓRROGA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR PRÓRROGA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        prorroga = (Prorroga) prorrogasDataTable.getRowData();
        if (prorroga != null) {
            dialogTitle = "SEGUIMIENTO " + prorroga.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(prorroga.getSolicitud());
            setHistorial("");
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }
            if (historial.trim().isEmpty()) {
                historial = "Estado actual: PRÓRROGA";
            }
        }
    }

    public void buscarCasillero(ActionEvent ae) {
        if (prorroga != null && prorroga.getId() != null) {
            Controlador c = new Controlador();
            prorroga.setCasilleroSenadi(c.buscarCasilleroBySolicitud(prorroga.getSolicitud()));
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg;
        if (prorroga != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + prorroga.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(prorroga.getIdRenewalForm(), prorroga.getSolicitud());
            if (archivos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRÓ EL EXPEDIENTE");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EXPEDIENTE CARGADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL EXPEDIENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Para poder generar/ver el PDF de la prórroga son indispensables el
     * numeroProrroga, numeroAlcance y fechaAlcance.
     */
    public boolean faltanDatosAlcance(Prorroga p) {
        return p == null || p.getNumeroProrroga() == null
                || p.getNumeroAlcance() == null || p.getNumeroAlcance().trim().isEmpty()
                || p.getFechaAlcance() == null;
    }

    public void prepararDescarga(ActionEvent ae) {
        FacesMessage msg;
        prorroga = (Prorroga) prorrogasDataTable.getRowData();
        if (prorroga != null && prorroga.getId() != null) {
            if (faltanDatosAlcance(prorroga)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PARA VER EL PDF SON INDISPENSABLES EL No. PRÓRROGA, No. ALCANCE (ESCRITO) Y FECHA ALCANCE DEL TRÁMITE " + prorroga.getSolicitud());
            } else {
                loginBean.setProrroga(prorroga);
                loginBean.setVarious(false);
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA PREPARADA PARA DESCARGA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE LA PRÓRROGA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg;
        if (!selectedProrrogas.isEmpty()) {
            boolean flag = true;
            String msj = "";
            for (Prorroga p : selectedProrrogas) {
                if (faltanDatosAlcance(p)) {
                    flag = false;
                    msj = "PARA VER EL PDF SON INDISPENSABLES EL No. PRÓRROGA, No. ALCANCE (ESCRITO) Y FECHA ALCANCE DEL TRÁMITE " + p.getSolicitud();
                    break;
                }
            }
            if (flag) {
                loginBean.setProrrogas(selectedProrrogas);
                loginBean.setVarious(true);
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGAS CARGADAS PARA DESCARGA, ESPERE...");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", msj);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN SELECCIÓN", "SELECCIONE AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Abre el PDF ya notificado de la prórroga (documento subido al casillero).
     */
    public void validarProrroga(Prorroga p) {
        FacesMessage msg = null;
        if (p != null) {
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(p.getSolicitud(), true);
            String ruta = "";
            if (!uploads.isEmpty()) {
                for (int i = 0; i < uploads.size(); i++) {
                    UploadNotificacion unaux = uploads.get(i);
                    String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                    int conf = Operaciones.esCertificado(rutaux, "PRÓRROGA");
                    if (conf == 0) {
                        ruta = rutaux;
                        break;
                    }
                }
                if (ruta.trim().isEmpty()) {
                    // si no se identificó por contenido, se toma el último documento notificado
                    UploadNotificacion unaux = uploads.get(uploads.size() - 1);
                    ruta = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                }
                PrimeFaces.current().ajax().addCallbackParam("viewpro", true);
                PrimeFaces.current().ajax().addCallbackParam("view", ruta);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA CARGADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL DOCUMENTO NOTIFICADO DE LA PRÓRROGA DEL TRÁMITE " + p.getSolicitud());
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UNA PRÓRROGA SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    // ============================= getters/setters =============================
    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<Prorroga> getProrrogas() {
        return prorrogas;
    }

    public void setProrrogas(List<Prorroga> prorrogas) {
        this.prorrogas = prorrogas;
    }

    public List<Prorroga> getProrrogasFiltradas() {
        return prorrogasFiltradas;
    }

    public void setProrrogasFiltradas(List<Prorroga> prorrogasFiltradas) {
        this.prorrogasFiltradas = prorrogasFiltradas;
    }

    public List<Prorroga> getSelectedProrrogas() {
        return selectedProrrogas;
    }

    public void setSelectedProrrogas(List<Prorroga> selectedProrrogas) {
        this.selectedProrrogas = selectedProrrogas;
    }

    public UIData getProrrogasDataTable() {
        return prorrogasDataTable;
    }

    public void setProrrogasDataTable(UIData prorrogasDataTable) {
        this.prorrogasDataTable = prorrogasDataTable;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public String getSaveEdit() {
        return saveEdit;
    }

    public void setSaveEdit(String saveEdit) {
        this.saveEdit = saveEdit;
    }

    public String getMensajeConfirmacion() {
        return mensajeConfirmacion;
    }

    public void setMensajeConfirmacion(String mensajeConfirmacion) {
        this.mensajeConfirmacion = mensajeConfirmacion;
    }

    public boolean isEdicion() {
        return edicion;
    }

    public void setEdicion(boolean edicion) {
        this.edicion = edicion;
    }

    public String getNumRegistros() {
        return numRegistros;
    }

    public void setNumRegistros(String numRegistros) {
        this.numRegistros = numRegistros;
    }

    public String getExportName() {
        return exportName;
    }

    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    public Prorroga getProrroga() {
        return prorroga;
    }

    public void setProrroga(Prorroga prorroga) {
        this.prorroga = prorroga;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public String getHistorial() {
        return historial;
    }

    public void setHistorial(String historial) {
        this.historial = historial;
    }

    public List<Documento> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Documento> archivos) {
        this.archivos = archivos;
    }

    public boolean isUsuarioConsulta() {
        return usuarioConsulta;
    }

    public void setUsuarioConsulta(boolean usuarioConsulta) {
        this.usuarioConsulta = usuarioConsulta;
    }

    public String getEstadoTemp() {
        return estadoTemp;
    }

    public void setEstadoTemp(String estadoTemp) {
        this.estadoTemp = estadoTemp;
    }
}
