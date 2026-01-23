/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.bean;

import java.io.Serializable;
import java.sql.Timestamp;
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
import senadi.gob.ec.mod.daop.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.mod.daop.PpdiTituloSignoDistintivo;
import senadi.gob.ec.mod.model.Documento;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.iepdep.HallmarkForms;
import senadi.gob.ec.mod.model.iepform.ModificacionApp;
import senadi.gob.ec.mod.model.iepform.PaymentReceipt;
import senadi.gob.ec.mod.model.iepform.Person;
import senadi.gob.ec.mod.model.iepform.RenewalForm;
import senadi.gob.ec.mod.model.iepform.Types;
import senadi.gob.ec.mod.model.transf.TituloCancelado;
import senadi.gob.ec.mod.ucc.Controlador;
import senadi.gob.ec.mod.ucc.Reusable;

/**
 *
 * @author michael
 */
@ManagedBean(name = "renrezBean")
@ViewScoped
public class RenovacionRezagoBean implements Serializable {

    private List<Renovacion> renovaciones;
    private List<Renovacion> renovacionesFiltradas;
    private List<Renovacion> selectedRenovaciones;
    private UIData renovacionDataTable;

    private Renovacion renovacion;
    private String dialogTitle;

    private String numRegistros;
    private String criterio;
    private Date fechaInicio;
    private Date fechaFin;

    private Integer valorProgessBar;

    private List<Documento> archivos;

    private String tramitesLog;

    private LoginBean loginBean;
    private boolean botonpasar;

    private boolean usuarioConsulta;

    public RenovacionRezagoBean() {
        loadRenovacionesRezagadas();
    }

    private void loadRenovacionesRezagadas() {
        renovaciones = new ArrayList<>();
        Controlador c = new Controlador();
        tramitesLog = "";
        loginBean = getLogin();
        usuarioConsulta = !loginBean.isUsuarioConsulta();
    }

    private LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginBean loginBean = (LoginBean) session.getAttribute("loginBean");
        return loginBean;
    }

    public void longRunning() throws InterruptedException {
        valorProgessBar = 0;
//        Integer k = valorProgessBar;
        if (!selectedRenovaciones.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedRenovaciones.size(); i++) {

                Renovacion renoaux = selectedRenovaciones.get(i);

                if (c.validarExistenciaRenovacion(renoaux.getSolicitudSenadi())
                        || c.validarExistenciaNotificada(renoaux.getSolicitudSenadi())
                        || c.validarExistenciaDesistida(renoaux.getSolicitudSenadi())
                        || c.validarExistenciaCaducada(renoaux.getSolicitudSenadi())) {
                    tramitesLog += renoaux.getSolicitudSenadi() + "\n";
                } else {

                    RenewalForm rf = c.findRenewalFormsByApplicationNumber(renoaux.getSolicitudSenadi());

                    if (rf.getId() != null) {
                        if (rf.getStatus().equals("DELIVERED")) {

                            Types t = c.getTypes(rf.getTransactionMotiveId());

                            Types ttp = c.getTypes(rf.getFormId());

                            if (t.getId() != null && ttp.getId() != null) {
                                if (t.getName().trim().toLowerCase().contains("renovación")) {

                                    renovacion.setFechaPresentacion(rf.getApplicationDate());
                                    PaymentReceipt pr = c.getPaymentReceiptById(rf.getPaymentReceiptId());
                                    if (pr.getId() != null) {
                                        renovacion.setNoComprobantePresentSolic(pr.getVoucherNumber());
                                    }

                                    renovacion.setSigno(ttp.getAlias());
                                    renovacion.setCasilleroSenadi(c.getCasilleroSenadi(rf.getOwnerId()) + "");
                                    renovacion.setIdRenewalForm(rf.getId());

                                    if (rf.getDebugId() != null && rf.getDebugId() != 0) {

                                        HallmarkForms hf = c.getHallmarkForm(rf.getDebugId());

                                        if (hf.getId() != null) {
                                            renovacion.setDenominacion(hf.getDenomination());
                                            renovacion.setRegistroNo(hf.getExpedient());
                                            if (renovacion.getRegistroNo() != null && !renovacion.getRegistroNo().trim().isEmpty()) {
                                                PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByNumeroTitulo(renovacion.getRegistroNo());
                                                if (titulo.getCodigoSolicitudSigno() != null) {
                                                    renovacion.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                }
                                            }
                                        }
                                    } else {
                                        if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
//
                                            PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                            if (ps.getCodigoSolicitudSigno() != null) {
                                                renovacion.setDenominacion(ps.getDenominacionSigno());
                                                PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                if (titulo.getCodigoSolicitudSigno() != null) {
                                                    renovacion.setRegistroNo(titulo.getNumeroTitulo());
                                                    renovacion.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                }
                                            }
                                        }
                                    }

                                    Person tit = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                    if (tit.getId() != null) {
                                        renovacion.setTitularActual(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "APPLICANT"));
                                        renovacion.setIdentificacion(tit.getIdentificationNumber());
                                    }

                                    Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                    if (apoder.getId() != null) {
                                        renovacion.setAbogadoPatrocinadorApeApoRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "LAWYER"));
                                    } else {
                                        apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                        if (apoder.getId() != null) {
                                            renovacion.setAbogadoPatrocinadorApeApoRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "ATTORNEY"));
                                        }
                                    }

                                    if (renovacion.getRegistroNo() != null && !renovacion.getRegistroNo().trim().isEmpty()) {
                                        if (renovacion.getDenominacion() != null && !renovacion.getDenominacion().trim().isEmpty()) {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                if (c.existsTituloCanceladoByTituloAndExpediente(renovacion.getRegistroNo(), rf.getExpedient())) {
                                                    TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(renovacion.getRegistroNo(), rf.getExpedient());
                                                    if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
//                                                                + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        tramitesLog += "TÍTULO DE " + renoaux.getSolicitudSenadi() + " CANCELADO\n";
                                                        renovacion = new Renovacion();
                                                    } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                        renovacion.setCancelado(titca.getTipoCancelacion());
                                                        tramitesLog += "TÍTULO DE " + renoaux.getSolicitudSenadi() + " CANCELADO\n";
//                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
//                                                                + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    } else {
//                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
//                                                                + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        tramitesLog += "TÍTULO DE " + renoaux.getSolicitudSenadi() + " CANCELADO\n";
                                                        renovacion = new Renovacion();
                                                    }
                                                } else {
                                                    System.out.println("renovación " + renoaux.getSolicitudSenadi()+ " CARGADA CORRECTAMENTE");
                                                    //msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                }
                                            } else {
                                                if (c.existsTituloCanceladoByTituloAndDenominacion(renovacion.getRegistroNo(), renovacion.getDenominacion())) {
                                                    TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(renovacion.getRegistroNo(), renovacion.getDenominacion());
                                                    if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
//                                                                + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        tramitesLog += "TÍTULO DE " + renoaux.getSolicitudSenadi() + " CANCELADO\n";
                                                        renovacion = new Renovacion();
                                                    } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                        tramitesLog += "TÍTULO DE " + renoaux.getSolicitudSenadi() + " CANCELADO\n";
                                                        renovacion.setCancelado(titca.getTipoCancelacion());
//                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
//                                                                + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    } else {
                                                        tramitesLog += "TÍTULO DE " + renoaux.getSolicitudSenadi() + " CANCELADO\n";
//                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
//                                                                + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        renovacion = new Renovacion();
                                                    }
                                                } else {
                                                    System.out.println("renovación " + renoaux.getSolicitudSenadi()+ " CARGADA CORRECTAMENTE");
                                                    //msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                }
                                            }
                                            
                                            } else {
                                                System.out.println(renoaux.getSolicitudSenadi()+ ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }
                                        } else {
                                            System.out.println(renoaux.getSolicitudSenadi()+ ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }
                                    } else {
                                        System.out.println(renoaux.getSolicitudSenadi()+ ": TRÁMITE ENCONTRADO PERO NO ES UNA RENOVACIÓN, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                            } else {
                                tramitesLog += renoaux.getSolicitudSenadi()+ ": EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD\n";
                                //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD");
                            }

                        } else {
                            tramitesLog += renoaux.getSolicitudSenadi()+ ":TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO\n";
                            //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO");
                        }
                    }
                    
                    if (renoaux.getSolicitudSenadi()!= null) {
                        renoaux.setFechaCertificado(new Date());
                        renoaux.setCertificadoNo(c.getNextNumeroCertificado(new Date()));
                        renoaux.setSolicitudSenadi(renoaux.getSolicitudSenadi().trim().toUpperCase());
                        renoaux.setResponsable(loginBean.getLogin());
                        if (c.saveRenovacion(renoaux)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(renoaux.getDenominacion() != null ? renoaux.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(renoaux.getRegistroNo());
                            mapp.setSolicitud(renoaux.getSolicitudSenadi());
                            mapp.setTipo("RENOVACION");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("MARCA");
                            mapp.setActivo(true);

                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorial("RENOVACION", "RENOVACION", renoaux.getSolicitudSenadi(), "NUEVA", 0, loginBean.getNombre());
                            }
                        }
                    }
                }
                int n = i + 1;
                valorProgessBar = (n * 100) / selectedRenovaciones.size();
//                System.out.println("valorprog: " + valorProgessBar);
                //Thread.sleep(500);
            }
            valorProgessBar = 100;
        }
        valorProgessBar = 100;
    }

    public void onComplete() {
        if (tramitesLog.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SE HAN IMPORTADO CORRECTAMENTE LOS CAMBIOS DE DOMICILIO"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", tramitesLog));
        }
        loadRenovacionesRezagadas();
    }

    public void cargarRezago(ActionEvent ae) {
        Controlador c = new Controlador();
        List<RenewalForm> renewals = c.getRenewalsRezagoBytType("RENOVACION", "21");
//        c.updateRezago();
        renovaciones = c.loadRenovacionesFromRenewals(renewals);
        botonpasar = !renovaciones.isEmpty();
        numRegistros = "Número Registros Mostrados: " + renovaciones.size();
    }

    public void buscarRezagoRenovacionCriterio(ActionEvent ae) {
        FacesMessage msg = null;
        if (!criterio.trim().isEmpty() && criterio.trim().length() > 3) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndCriterio("RENOVACION", "21", criterio.trim().toUpperCase());
            renovaciones = c.loadRenovacionesFromRenewals(renewals);
            botonpasar = !renovaciones.isEmpty();
            numRegistros = "Número Registros Mostrados: " + renovaciones.size();
            if (renovaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO VÁLIDO (MÁS DE 3 CARACTERES)");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarRenovacionesPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndFecha("RENOVACION", "21", fechaInicio, fechaFin);
            renovaciones = c.loadRenovacionesFromRenewals(renewals);
            botonpasar = !renovaciones.isEmpty();
            numRegistros = "Número Registros Mostrados: " + renovaciones.size();
            if (renovaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN RANGO DE FECHAS VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg;
        renovacion = (Renovacion) renovacionDataTable.getRowData();
//        System.out.println("hereeeeeeee");
        if (renovacion != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + renovacion.getSolicitudSenadi();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(renovacion.getIdRenewalForm(), renovacion.getSolicitudSenadi());
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

    public boolean validarFechas() {
        try {
            fechaInicio.toString();
            fechaFin.toString();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void quitarTramites(ActionEvent ae) {
        FacesMessage msg;
        if (selectedRenovaciones != null && !selectedRenovaciones.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedRenovaciones.size(); i++) {

                Renovacion ren = selectedRenovaciones.get(i);

                ModificacionApp map = new ModificacionApp();
                map.setDenominacion("");
                map.setFecha(new Timestamp(new Date().getTime()));
                map.setObservacion(ren.getNacTitularAc());
                map.setRegistro(ren.getRegistroNo());
                map.setSolicitud(ren.getSolicitudSenadi());
                map.setTipo("RENOVACION");
                map.setUsuario(loginBean.getNombre());
                map.setModo(ren.getTacNJ());
                map.setActivo(false);
                c.saveModificacionApp(map);
            }

            loadRenovacionesRezagadas();
            PrimeFaces.current().ajax().addCallbackParam("quitado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON REMOVIDOS DE REZAGO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararQuitar(ActionEvent ae) {
        FacesMessage msg;
        if (selectedRenovaciones != null && !selectedRenovaciones.isEmpty()) {
            for (int i = 0; i < selectedRenovaciones.size(); i++) {
                selectedRenovaciones.get(i).setNacTitularAc("El trámite no pertenece a la Dirección de Modificaciones");
            }
            dialogTitle = "QUITAR DE REZAGADOS " + selectedRenovaciones.size() + " TRÁMITES?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON  CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void pasarSeleccionados(ActionEvent ae) {
        FacesMessage msg;
        if (selectedRenovaciones != null && !selectedRenovaciones.isEmpty()) {
            dialogTitle = "PASAR " + selectedRenovaciones.size() + " TRÁMITES A RENOVACIONES?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS HAN SIDO CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the renovaciones
     */
    public List<Renovacion> getRenovaciones() {
        return renovaciones;
    }

    /**
     * @param renovaciones the renovaciones to set
     */
    public void setRenovaciones(List<Renovacion> renovaciones) {
        this.renovaciones = renovaciones;
    }

    /**
     * @return the renovacionesFiltradas
     */
    public List<Renovacion> getRenovacionesFiltradas() {
        return renovacionesFiltradas;
    }

    /**
     * @param renovacionesFiltradas the renovacionesFiltradas to set
     */
    public void setRenovacionesFiltradas(List<Renovacion> renovacionesFiltradas) {
        this.renovacionesFiltradas = renovacionesFiltradas;
    }

    /**
     * @return the selectedRenovaciones
     */
    public List<Renovacion> getSelectedRenovaciones() {
        return selectedRenovaciones;
    }

    /**
     * @param selectedRenovaciones the selectedRenovaciones to set
     */
    public void setSelectedRenovaciones(List<Renovacion> selectedRenovaciones) {
        this.selectedRenovaciones = selectedRenovaciones;
    }

    /**
     * @return the renovacionDataTable
     */
    public UIData getRenovacionDataTable() {
        return renovacionDataTable;
    }

    /**
     * @param renovacionDataTable the renovacionDataTable to set
     */
    public void setRenovacionDataTable(UIData renovacionDataTable) {
        this.renovacionDataTable = renovacionDataTable;
    }

    /**
     * @return the numRegistros
     */
    public String getNumRegistros() {
        return numRegistros;
    }

    /**
     * @param numRegistros the numRegistros to set
     */
    public void setNumRegistros(String numRegistros) {
        this.numRegistros = numRegistros;
    }

    /**
     * @return the criterio
     */
    public String getCriterio() {
        return criterio;
    }

    /**
     * @param criterio the criterio to set
     */
    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * @return the renovacion
     */
    public Renovacion getRenovacion() {
        return renovacion;
    }

    /**
     * @param renovacion the renovacion to set
     */
    public void setRenovacion(Renovacion renovacion) {
        this.renovacion = renovacion;
    }

    /**
     * @return the dialogTitle
     */
    public String getDialogTitle() {
        return dialogTitle;
    }

    /**
     * @param dialogTitle the dialogTitle to set
     */
    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    /**
     * @return the archivos
     */
    public List<Documento> getArchivos() {
        return archivos;
    }

    /**
     * @param archivos the archivos to set
     */
    public void setArchivos(List<Documento> archivos) {
        this.archivos = archivos;
    }

    /**
     * @return the valorProgessBar
     */
    public Integer getValorProgessBar() {
        return valorProgessBar;
    }

    /**
     * @param valorProgessBar the valorProgessBar to set
     */
    public void setValorProgessBar(Integer valorProgessBar) {
        this.valorProgessBar = valorProgessBar;
    }

    /**
     * @return the tramitesLog
     */
    public String getTramitesLog() {
        return tramitesLog;
    }

    /**
     * @param tramitesLog the tramitesLog to set
     */
    public void setTramitesLog(String tramitesLog) {
        this.tramitesLog = tramitesLog;
    }

    /**
     * @return the botonpasar
     */
    public boolean isBotonpasar() {
        return botonpasar;
    }

    /**
     * @param botonpasar the botonpasar to set
     */
    public void setBotonpasar(boolean botonpasar) {
        this.botonpasar = botonpasar;
    }

    /**
     * @return the usuarioConsulta
     */
    public boolean isUsuarioConsulta() {
        return usuarioConsulta;
    }

    /**
     * @param usuarioConsulta the usuarioConsulta to set
     */
    public void setUsuarioConsulta(boolean usuarioConsulta) {
        this.usuarioConsulta = usuarioConsulta;
    }

}
