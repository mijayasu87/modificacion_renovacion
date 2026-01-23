/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import senadi.gob.ec.mod.daop.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.mod.daop.PpdiTituloSignoDistintivo;
import senadi.gob.ec.mod.model.Caducada;
import senadi.gob.ec.mod.model.Desistida;
import senadi.gob.ec.mod.model.Historial;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.Resolucion;
import senadi.gob.ec.mod.model.UploadNotificacion;
import senadi.gob.ec.mod.model.iepdep.HallmarkForms;
import senadi.gob.ec.mod.model.iepform.ModificacionApp;
import senadi.gob.ec.mod.model.iepform.PaymentReceipt;
import senadi.gob.ec.mod.model.iepform.Person;
import senadi.gob.ec.mod.model.iepform.RenewalForm;
import senadi.gob.ec.mod.model.iepform.Types;
import senadi.gob.ec.mod.model.transf.TituloCancelado;
import senadi.gob.ec.mod.ucc.Controlador;
import senadi.gob.ec.mod.ucc.FTPFiles;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author Michael Yanangómez
 */
@ManagedBean(name = "modificacionBean")
@ViewScoped
public class ModificacionBean implements Serializable {

    private List<Renovacion> renovaciones;
    private List<Renovacion> renovacionesFiltradas;
    private List<Renovacion> selectedRenovaciones;

    private String texto;

    private Renovacion renovacion;

    private UIData renovacionesDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;

    private String numRegistros;

    private String exportName;

    private boolean edicion;
    private String estadoTemp;

    private String historial;

    private LoginBean loginBean;

    private boolean separado;

    private boolean usuarioConsulta;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<String> archivos;

    public ModificacionBean() {
        Controlador c = new Controlador();

//        c.banderaCertificadosEmitidos();
        renovaciones = c.getRenovaciones();
        selectedRenovaciones = new ArrayList<>();
        renovacion = new Renovacion();
        dialogTitle = "NUEVA RENOVACIÓN";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Registro?";
        numRegistros = "Número Registros Mostrados: " + renovaciones.size();
        exportName = "renovacion_" + Operaciones.formatDate(new Date());
        loginBean = getLogin();
        usuarioConsulta = !loginBean.isUsuarioConsulta();
//        c.depurar();
    }

    private LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginBean loginBean = (LoginBean) session.getAttribute("loginBean");
        return loginBean;
    }

    public void buscarRenovaciones(ActionEvent ae) {
        System.out.println("RENOVACIÓN - SE BUSCA: " + texto);
        FacesMessage msg = null;
        if (texto.contains("'")) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO INGRESE CARACTERES ESPECIALES");
        } else {
            loadRenovaciones();
            if (!renovaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRARON RESULTADOS.");
            }

        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void loadRenovaciones() {
        Controlador c = new Controlador();
        renovaciones = new ArrayList<>();
        renovaciones = c.getRenovacionesByCriteria(texto);
        numRegistros = "Número Registros Mostrados: " + renovaciones.size();
    }

    public void loadNuevoRegistro(ActionEvent ae) {
        dialogTitle = "NUEVA RENOVACIÓN";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Registro?";
        renovacion = new Renovacion();
        edicion = false;
        System.out.println("Nueva renovación");
        if (renovacion != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    ////////////////////////////////Falta poner la opción que no permita guardar o cargar trámites con registro cancelado
    public void guardarRegistro(ActionEvent ae) {
        FacesMessage msg = null;
        if (renovacion != null) {
            Controlador c = new Controlador();
            if ((renovacion.getCasilleroSenadi() == null || renovacion.getCasilleroSenadi().trim().isEmpty()) && (renovacion.getCasilleroJudicial() == null || renovacion.getCasilleroJudicial().trim().isEmpty())) {
                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "CASILLERO", "Debe Ingresar un Casillero");
            } else {
                if (renovacion.getId() != null) {
                    if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                        if (estadoTemp.equals("NOTIFICADAS")) {
                            Notificada notificada = new Notificada();
                            notificada.setTipoSolicitante("");
                            notificada.setSolicitud(renovacion.getSolicitudSenadi().toUpperCase());
                            notificada.setFechaPresentacion(renovacion.getFechaPresentacion());
                            notificada.setNoComprobantePresentSolic(renovacion.getNoComprobantePresentSolic());
                            notificada.setNoComprobanteEmisionCert(renovacion.getNoComprobanteEmisionCert());
                            notificada.setTotalFoliosExpediente(renovacion.getTotalFoliosExpediente());
                            notificada.setFechaElaboraNotificacion(new Date());
                            notificada.setNotificacion(c.getNextNumeroNotificacion(notificada.getFechaElaboraNotificacion()));
                            notificada.setFechaCertificado(renovacion.getFechaCertificado());
                            notificada.setTituloResolucion(renovacion.getTituloResolucion());
                            notificada.setRegistroNo(renovacion.getRegistroNo());
                            notificada.setFechaRegistro(renovacion.getFechaRegistro());
                            notificada.setFechaVenceRegistro(renovacion.getFechaVenceRegistro());
                            notificada.setDenominacion(renovacion.getDenominacion());
                            notificada.setLema(renovacion.getLema());
                            notificada.setSigno(renovacion.getSigno());
                            notificada.setClase(renovacion.getClase());
                            notificada.setProtege(renovacion.getProtege());
                            notificada.setTitularActual(renovacion.getTitularActual());
                            notificada.setTacNJ(renovacion.getTacNJ());
                            notificada.setNacTitularAc(renovacion.getNacTitularAc());
                            notificada.setDomicilioTitularAc("");
                            notificada.setAr("");
                            notificada.setNj("");
                            notificada.setTitApodRepre("");
                            notificada.setApeApodRepre(renovacion.getAbogadoPatrocinadorApeApoRepre());
                            notificada.setNomApodRepre("");
                            notificada.setFechaNotifica(new Date());
                            notificada.setCasilleroSenadi(renovacion.getCasilleroSenadi());
                            notificada.setCasilleroJudicial(renovacion.getCasilleroJudicial());
                            notificada.setRo("");
                            notificada.setProvidencia("");
                            notificada.setFechaProvidencia(new Date());
                            notificada.setFechaNotificaPro(new Date());
                            notificada.setResponsable(renovacion.getResponsable());
                            notificada.setIdentificacion(renovacion.getIdentificacion());
                            notificada.setCertificadoEmitido(renovacion.isCertificadoEmitido());
                            notificada.setNotificacionEmitida(renovacion.isNotificacionEmitida());
                            notificada.setCancelado(renovacion.getCancelado());

                            if (c.validarExistenciaNotificada(notificada.getSolicitud())) {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en notificadas con el mismo número de solicitud");
                            } else {
                                if (c.saveNotificada(notificada)) {

                                    c = new Controlador();
                                    List<Renovacion> renovas = c.getRenovacionesBySolSenadi(notificada.getSolicitud());
                                    int cont = 0;
                                    for (int i = 0; i < renovas.size(); i++) {
                                        if (c.removeRenovacion(renovas.get(i))) {
                                            cont++;
                                        }
                                    }
                                    if (renovas.size() == cont) {
                                        c.saveHistorial("NOTIFICADAS", "RENOVACIONES", notificada.getSolicitud(), "PASADO A", 0, loginBean.getLogin());

                                        loadRenovaciones();
                                        //context.addCallbackParam("saved", true);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + notificada.getSolicitud() + " de renovación a notificada");
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                    } else {
//                                        context.addCallbackParam("saved", false);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA RENOVACIÓN");
                                    }
                                } else {
//                                    context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                                }
                            }

                        } else if (estadoTemp.equals("DESISTIDAS")) {
                            Desistida desist = new Desistida();
                            desist.setEstado("DESISTE");
                            desist.setSolicitudSenadi(renovacion.getSolicitudSenadi().toUpperCase());
                            desist.setIepi(renovacion.getSolicitudSenadi());
                            desist.setSolicitudNo("");
                            desist.setFechaPresentacion(renovacion.getFechaPresentacion());
                            desist.setTotalFoliosExpediente(renovacion.getTotalFoliosExpediente());
                            desist.setCertificadoNo(renovacion.getCertificadoNo());
                            desist.setFechaCertificado(renovacion.getFechaCertificado());
                            desist.setRegistroNo(renovacion.getRegistroNo());
                            desist.setFechaRegistro(renovacion.getFechaRegistro());
                            desist.setFechaVenceRegistro(renovacion.getFechaVenceRegistro());
                            desist.setDenominacion(renovacion.getDenominacion());
                            desist.setSigno(renovacion.getSigno());
                            desist.setProtege(renovacion.getProtege());
                            desist.setTitularActual(renovacion.getTitularActual());
                            desist.setAr("");
                            desist.setNj("");
                            desist.setTitApodRepre("");
                            desist.setApeApodRepre(renovacion.getAbogadoPatrocinadorApeApoRepre());
                            desist.setNomApodRepre("");
                            desist.setFechaElaboraNotificacion(new Date());
                            desist.setFechaNotifica(new Date());
                            desist.setCasilleroSenadi(renovacion.getCasilleroSenadi());
                            desist.setCasilleroJudicial(renovacion.getCasilleroJudicial());
                            desist.setRo("");
                            desist.setResponsable(renovacion.getResponsable());
                            desist.setFechaDesistida(new Date());
                            desist.setIdentificacion(renovacion.getIdentificacion());
                            desist.setCancelado(renovacion.getCancelado());

                            if (c.validarExistenciaDesistida(desist.getSolicitudSenadi())) {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en desistidas con el mismo número de solicitud");
                            } else {
                                if (c.saveDesistida(desist)) {

                                    c = new Controlador();
                                    List<Renovacion> renovas = c.getRenovacionesBySolSenadi(desist.getSolicitudSenadi());
                                    int cont = 0;
                                    for (int i = 0; i < renovas.size(); i++) {
                                        if (c.removeRenovacion(renovas.get(i))) {
                                            cont++;
                                        }
                                    }
                                    if (renovas.size() == cont) {
                                        c.saveHistorial("DESISTIDAS", "RENOVACIONES", desist.getSolicitudSenadi(), "PASADO A", 0, loginBean.getLogin());
                                        loadRenovaciones();
                                        //context.addCallbackParam("saved", true);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + desist.getSolicitudSenadi() + " de renovación a desistida");
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                    } else {
//                                        context.addCallbackParam("saved", false);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA RENOVACIÓN");
                                    }
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                                }
                            }
                        } else {
                            Caducada caducada = new Caducada();
                            caducada.setSolicitudSenadi(renovacion.getSolicitudSenadi().toUpperCase());
                            caducada.setFechaSolicitud(renovacion.getFechaPresentacion()); //<-----------
                            caducada.setDenominacion(renovacion.getDenominacion());
                            caducada.setRegistroNo(renovacion.getRegistroNo());
                            caducada.setFechaRegistro(renovacion.getFechaRegistro());
                            caducada.setSolicitante("");
                            caducada.setFechaProvidencia(new Date());
                            caducada.setResolucion(c.getNextResCaducadaNumber(caducada.getFechaProvidencia()) + "");
                            caducada.setFechaNotificacion(new Date());
                            caducada.setAbogadoPatrocinador(renovacion.getAbogadoPatrocinadorApeApoRepre());
                            caducada.setCasilleroNo(!renovacion.getCasilleroSenadi().isEmpty() ? renovacion.getCasilleroSenadi() : renovacion.getCasilleroJudicial());
                            caducada.setEmail("");
                            caducada.setRequisitoNotificado("");
                            caducada.setCertificaNo(renovacion.getCertificadoNo());
                            caducada.setFechaOtorgada(new Date());
                            caducada.setSolicitud("");
                            caducada.setFechaSolicitud(new Date());
                            caducada.setResponsable(renovacion.getResponsable());
                            caducada.setSigno(renovacion.getSigno());
                            caducada.setFechaVencimiento(renovacion.getFechaVenceRegistro());
                            caducada.setIdentificacion(renovacion.getIdentificacion());
                            caducada.setCancelado(renovacion.getCancelado());

                            if (c.validarExistenciaCaducada(caducada.getSolicitudSenadi())) {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en caducadas con el mismo número de solicitud");
                            } else {
                                if (c.saveCaducada(caducada)) {

                                    c = new Controlador();
                                    List<Renovacion> renovas = c.getRenovacionesBySolSenadi(caducada.getSolicitudSenadi());
                                    int cont = 0;
                                    for (int i = 0; i < renovas.size(); i++) {
                                        if (c.removeRenovacion(renovas.get(i))) {
                                            cont++;
                                        }
                                    }
                                    if (renovas.size() == cont) {
                                        c.saveHistorial("CADUCADAS-NEGADAS", "RENOVACIONES", caducada.getSolicitudSenadi(), "PASADO A", 0, loginBean.getLogin());
                                        loadRenovaciones();
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + caducada.getSolicitudSenadi() + " de renovación a caducada-negada");
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                    } else {
                                        //context.addCallbackParam("saved", false);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA RENOVACIÓN");
                                    }
                                } else {
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                                }
                            }
                        }
                    } else {
                        if (c.validarExistsRen(renovacion)) {
//                            context.addCallbackParam("saved", false);
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD.");
                        } else {

                            renovacion.setSolicitudSenadi(renovacion.getSolicitudSenadi().toUpperCase());
                            renovacion.setCargado(false);
                            if (c.updateRenovacion(renovacion)) {
                                c.saveHistorial("RENOVACIÓN", "RENOVACIÓN", renovacion.getSolicitudSenadi(), "EDITADO", 0, loginBean.getLogin());
                                System.out.println("Renovación " + (renovacion.getSolicitudSenadi() != null ? renovacion.getSolicitudSenadi() : "") + " editada");
                                //context.addCallbackParam("saved", true);
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "RENOVACIÓN EDITADA CORRECTAMENTE.");
                            } else {
                                System.out.println("Hola a todos 4");
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ERROR AL EDITAR RENOVACIÓN.");
                            }
                        }
                    }
                } else {
                    if (c.validarExistenciaRenovacion(renovacion.getSolicitudSenadi())) {
                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "YA EXISTE UN TRÁMITE CON EL MISMO NÚMERO DE SOLICITUD");
                    } else {
                        ModificacionApp mapp = c.getModificacionApp(renovacion.getSolicitudSenadi());
                        if (mapp.getId() != null) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                        } else {
                            if (c.validarExistenciaNotificada(renovacion.getSolicitudSenadi())) {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "YA EXISTE UN TRÁMITE CON EL MISMO NÚMERO DE SOLICITUD");
                            } else {
                                boolean habilitado = true;
                                if (renovacion.getDenominacion() != null && !renovacion.getDenominacion().trim().isEmpty()
                                        && renovacion.getRegistroNo() != null && !renovacion.getRegistroNo().trim().isEmpty()) {
                                    if (c.existsTituloCanceladoByTituloAndDenominacion(renovacion.getRegistroNo(), renovacion.getDenominacion())) {
                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(renovacion.getRegistroNo(), renovacion.getDenominacion());
                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN '"
                                                    + renovacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                            renovacion = new Renovacion();
                                            habilitado = false;
                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                            renovacion.setCancelado(titca.getTipoCancelacion());
                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN '"
                                                    + renovacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN '"
                                                    + renovacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                            renovacion = new Renovacion();
                                            habilitado = false;
                                        }
                                    } else {
                                        habilitado = true;
                                    }
                                }

                                if (habilitado) {
                                    renovacion.setSolicitudSenadi(renovacion.getSolicitudSenadi().toUpperCase());
                                    renovacion.setCertificadoNo(c.getNextNumeroCertificado(renovacion.getFechaCertificado()));

                                    if (c.saveRenovacion(renovacion)) {
                                        c.saveModificacionApp(renovacion.getDenominacion(), renovacion.getRegistroNo(), renovacion.getSolicitudSenadi(), "RENOVACION", loginBean.getNombre());
                                        c.saveHistorial("RENOVACIÓN", "RENOVACIÓN", renovacion.getSolicitudSenadi(), "CREADO", 0, loginBean.getLogin());
                                        System.out.println("Nueva Renovación " + (renovacion.getSolicitudSenadi() != null ? renovacion.getSolicitudSenadi() : "") + " guardada");
                                        renovaciones = c.getRenovaciones();
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "GUARDADO", "RENOVACIÓN GUARDADA CORRECTAMENTE.");
                                    } else {
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ERROR AL GUARDAR.");
                                    }
                                }
                            }

                        }
                    }
                }
            }

        } else {
            //context.addCallbackParam("saved", false);
            PrimeFaces.current().ajax().addCallbackParam("saved", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "CAMPOS NULOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {
        dialogTitle = "EDITAR RENOVACIÓN";
        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        renovacion = (Renovacion) renovacionesDataTable.getRowData();
        if (renovacion != null) {
            mensajeConfirmacion = "¿Seguro de editar la Renovación: " + renovacion.getSolicitudSenadi() + "?";

            Controlador c = new Controlador();
            RenewalForm rf = c.findRenewalFormsByApplicationNumber(renovacion.getSolicitudSenadi());
            if (rf.getId() != null) {
                renovacion.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RENOVACIÓN CARGADA.");
            System.out.println(renovacion.toString());
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR RENOVACIÓN");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarRenovacion(ActionEvent ae) {
        FacesMessage msg = null;
        renovacion = (Renovacion) renovacionesDataTable.getRowData();
        if (renovacion != null) {
            Controlador c = new Controlador();
            if (c.removeRenovacion(renovacion)) {
                c.saveHistorial("RENOVACIÓN", "RENOVACIÓN", renovacion.getSolicitudSenadi(), "ELIMINADO", 0, loginBean.getLogin());
                loadRenovaciones();
                System.out.println("Renovación " + renovacion.getSolicitudSenadi() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RENOVACIÓN " + renovacion.getSolicitudSenadi() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR RENOVACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR RENOVACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        renovacion = (Renovacion) renovacionesDataTable.getRowData();
        if (renovacion != null) {
            dialogTitle = "SEGUIMIENTO " + renovacion.getSolicitudSenadi();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(renovacion.getSolicitudSenadi());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: RENOVACIONES";
            }
        }
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewRenovacion(ActionEvent e) {
        FacesMessage msg = null;
        renovacion = (Renovacion) renovacionesDataTable.getRowData();
//        RequestContext context = RequestContext.getCurrentInstance();
        boolean correcto = false;
        if (renovacion != null) {
            if (validarFechaVencimientoRegistro()) {
                System.out.println("Descargando Renovación " + renovacion.getCertificadoNo() + " A");
                correcto = true;
                loginBean.setNotificadaFlotante(null);
                loginBean.setRenovacionFlotante(renovacion);
                loginBean.setVarious(false);
                loginBean.setNewreport(false);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "RENOVACIÓN", "DESCARGANDO RENOVACIÓN");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "FECHA", "EL TRÁMITE NO POSEE FECHA DE VENCIMIENTO");
            }

        } else {
            correcto = false;
        }

        if (correcto) {
            boolean doit = true;
            PrimeFaces.current().ajax().addCallbackParam("doit", doit);
            PrimeFaces.current().ajax().addCallbackParam("view", "reportes");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewNewRenovacion(ActionEvent e) {
        FacesMessage msg = null;
        renovacion = (Renovacion) renovacionesDataTable.getRowData();
//        RequestContext context = RequestContext.getCurrentInstance();
        boolean correcto = false;
        if (renovacion != null) {
            if (validarFechaVencimientoRegistro()) {
                System.out.println("Descargando Renovación " + renovacion.getCertificadoNo() + " N");
                correcto = true;
                loginBean.setNotificadaFlotante(null);
                loginBean.setRenovacionFlotante(renovacion);
                loginBean.setVarious(false);
                loginBean.setNewreport(true);
                loginBean.setSubrogante(false);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "RENOVACIÓN", "DESCARGANDO RENOVACIÓN");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "FECHA", "EL TRÁMITE NO POSEE FECHA DE VENCIMIENTO");
            }

        } else {
            correcto = false;
        }
//        System.out.println("new renovaciooooooon");
        if (correcto) {
            boolean doit = true;
//            context.addCallbackParam("doit", doit);
//            context.addCallbackParam("view", "reportes");
            PrimeFaces.current().ajax().addCallbackParam("doit", doit);
            PrimeFaces.current().ajax().addCallbackParam("view", "reportes");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean validarFechaVencimientoRegistro() {
        try {
            renovacion.getFechaVenceRegistro().toString();
            return true;
        } catch (Exception ex) {
            System.out.println("Error en fecha de vencimiento " + renovacion.getSolicitudSenadi() + ": " + ex);
            return false;
        }
    }

    /* Da la orden de visualizar el reporte, clase NewInforme (Webservlet)*/
    public void viewNewInfoRenovacion(ActionEvent e) {
        FacesMessage msg = null;
        renovacion = (Renovacion) renovacionesDataTable.getRowData();
        if (renovacion != null) {
            if (validarFechaVencimientoRegistro()) {
                if (renovacion.getSigno().trim().toUpperCase().equals("LC")) {
                    if (renovacion.getLema() == null || renovacion.getLema().trim().isEmpty()) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "RENOVACIÓN", "LOS LEMAS COMERCIALES DEBEN TENER 'SIGNO AL QUE ACOMPAÑA EL LEMA'");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    }
                }
                System.out.println("Descargando Renovación " + renovacion.getCertificadoNo() + " NEWINFO");
                loginBean.setNotificadaFlotante(null);
                loginBean.setRenovacionFlotante(renovacion);
                loginBean.setVarious(false);
                loginBean.setNewreport(true);
                loginBean.setSubrogante(false);

                boolean doit = true;
                PrimeFaces.current().ajax().addCallbackParam("doit", doit);
                PrimeFaces.current().ajax().addCallbackParam("view", "newinforme");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "RENOVACIÓN", "DESCARGANDO RENOVACIÓN");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "FECHA", "EL TRÁMITE NO POSEE FECHA DE VENCIMIENTO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EXISTE UN PROBLEMA AL CARGAR EL TRÁMITE");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedRenovaciones.isEmpty()) {
            System.out.println("Descargando Múltiples Renovacion A...");
            String validar = "";
            for (int i = 0; i < selectedRenovaciones.size(); i++) {
                Renovacion aux = selectedRenovaciones.get(i);
                if (!validarFecha(aux.getFechaVenceRegistro())) {
                    validar = aux.getSolicitudSenadi() + " NO POSEE FECHA DE VENCIMIENTO DE REGISTRO";
                    break;
                }
                if (aux.isCertificadoEmitido()) {
                    validar = aux.getSolicitudSenadi() + " YA HA SIDO EMITIDO SU CERTIFICADO";;
                    break;
                }
            }
            if (validar.isEmpty()) {
                loginBean.setRenovacionesFlotantes(selectedRenovaciones);
                loginBean.setNotificadasFlotantes(new ArrayList<Notificada>());
                loginBean.setVarious(true);
                loginBean.setNewreport(false);
                if (separado) {
                    loginBean.setAllInOne(false);
                } else {
                    loginBean.setAllInOne(true);
                }

//            context.addCallbackParam("doit", true);
//            context.addCallbackParam("view", "reportes");
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                PrimeFaces.current().ajax().addCallbackParam("view", "reportes");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "ACCIÓN", "DESCARGANDO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "EL TRÁMITE " + validar);
            }
        } else {
//            context.addCallbackParam("doit", false);
            PrimeFaces.current().ajax().addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "DEBE SELECCIONAR AL MENOS UNA RENOVACIÓN");

            System.out.println("No hay seleccionadas");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean validarFecha(Date fecha) {
        try {
            fecha.toString();
            return true;
        } catch (Exception ex) {
            System.err.println("Fecha incorrecta: " + ex);
            return false;
        }
    }

    public void downloadSelectedNew(ActionEvent ae) {
        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedRenovaciones.isEmpty()) {
            System.out.println("Descargando Múltiples Renovacion New...");
            String validar = "";
            for (int i = 0; i < selectedRenovaciones.size(); i++) {
                Renovacion aux = selectedRenovaciones.get(i);
                if (!validarFecha(aux.getFechaVenceRegistro())) {
                    validar = aux.getSolicitudSenadi() + " NO POSEE FECHA DE VENCIMIENTO DE REGISTRO";
                    break;
                }
                if (aux.isCertificadoEmitido()) {
                    validar = aux.getSolicitudSenadi() + " YA HA SIDO EMITIDO SU CERTIFICADO";;
                    break;
                }
            }
            if (validar.isEmpty()) {
                loginBean.setRenovacionesFlotantes(selectedRenovaciones);
                loginBean.setNotificadasFlotantes(new ArrayList<Notificada>());
                loginBean.setVarious(true);
                loginBean.setNewreport(true);
                loginBean.setSubrogante(false);
                if (separado) {
                    loginBean.setAllInOne(false);
                } else {
                    loginBean.setAllInOne(true);
                }

                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                PrimeFaces.current().ajax().addCallbackParam("view", "reportes");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "ACCIÓN", "DESCARGANDO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "EL TRÁMITE " + validar);
            }
        } else {
//            context.addCallbackParam("doit", false);
            PrimeFaces.current().ajax().addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "DEBE SELECCIONAR AL MENOS UNA RENOVACIÓN");
            System.out.println("No hay seleccionadas");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelectedNewN(ActionEvent ae) {
        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedRenovaciones.isEmpty()) {
            System.out.println("Descargando Múltiples Renovacion New (S)...");
            String validar = "";
            for (int i = 0; i < selectedRenovaciones.size(); i++) {
                Renovacion aux = selectedRenovaciones.get(i);
                if (!validarFecha(aux.getFechaVenceRegistro())) {
                    validar = aux.getSolicitudSenadi() + " NO POSEE FECHA DE VENCIMIENTO DE REGISTRO";
                    break;
                }
                if (aux.isCertificadoEmitido()) {
                    validar = aux.getSolicitudSenadi() + " YA HA SIDO EMITIDO SU CERTIFICADO";;
                    break;
                }
            }
            if (validar.isEmpty()) {

                loginBean.setRenovacionesFlotantes(selectedRenovaciones);
                loginBean.setNotificadasFlotantes(new ArrayList<Notificada>());
                loginBean.setVarious(true);
                loginBean.setNewreport(true);
                loginBean.setSubrogante(true);
                if (separado) {
                    loginBean.setAllInOne(false);
                } else {
                    loginBean.setAllInOne(true);
                }

//            context.addCallbackParam("doit", true);
//            context.addCallbackParam("view", "reportes");
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                PrimeFaces.current().ajax().addCallbackParam("view", "reportes");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "ACCIÓN", "DESCARGANDO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "EL TRÁMITE " + validar);
            }
        } else {
            //context.addCallbackParam("doit", false);
            PrimeFaces.current().ajax().addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "DEBE SELECCIONAR AL MENOS UNA RENOVACIÓN");

            System.out.println("No hay seleccionadas");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelectedNewInfo(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedRenovaciones.isEmpty()) {
            Controlador c = new Controlador();
            Resolucion resolucionN = c.getResolucionActiva("notificacion");
            if (!validarFecha(resolucionN.getFecha())) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY FECHA EN LA RESOLUCIÓN - NOTIFICACIÓN, VAYA A CONFIGURACIÓN");
            } else {
                String validar = "";
                for (int i = 0; i < selectedRenovaciones.size(); i++) {
                    Renovacion aux = selectedRenovaciones.get(i);
                    if (aux.getSigno().trim().toUpperCase().equals("LC") && (aux.getLema() == null || aux.getLema().trim().isEmpty())) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "EL TRÁMITE " + aux.getSolicitudSenadi() + " NO TIENE 'SIGNO AL QUE ACOMPAÑA EL LEMA'");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    }
                    if (!validarFecha(aux.getFechaVenceRegistro())) {
                        validar = aux.getSolicitudSenadi() + " NO POSEE FECHA DE VENCIMIENTO DE REGISTRO";
                        break;
                    }
                    if (aux.isCertificadoEmitido()) {
                        validar = aux.getSolicitudSenadi() + " YA HA SIDO EMITIDO SU CERTIFICADO";;
                        break;
                    }
                }
                if (validar.isEmpty()) {
                    System.out.println("Descargando Múltiples Renovacion NewInfo...");
                    loginBean.setRenovacionesFlotantes(selectedRenovaciones);
                    loginBean.setNotificadasFlotantes(new ArrayList<Notificada>());
                    loginBean.setVarious(true);
                    loginBean.setNewreport(true);
                    loginBean.setSubrogante(false);
                    if (separado) {
                        loginBean.setAllInOne(false);
                    } else {
                        loginBean.setAllInOne(true);
                    }

//            context.addCallbackParam("doit", true);
//            context.addCallbackParam("view", "newinforme");
                    PrimeFaces.current().ajax().addCallbackParam("doit", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", "newinforme");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "ACCIÓN", "DESCARGANDO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "EL TRÁMITE " + validar);
                }
            }

        } else {
//            context.addCallbackParam("doit", false);
            PrimeFaces.current().ajax().addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "DEBE SELECCIONAR AL MENOS UNA RENOVACIÓN");

            System.out.println("No hay seleccionadas");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelectedNewInfoS(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedRenovaciones.isEmpty()) {
            System.out.println("Descargando Múltiples Renovacion New Info (S)..");
            String validar = "";
            for (int i = 0; i < selectedRenovaciones.size(); i++) {
                Renovacion aux = selectedRenovaciones.get(i);
                if (!validarFecha(aux.getFechaVenceRegistro())) {
                    validar = aux.getSolicitudSenadi() + " NO POSEE FECHA DE VENCIMIENTO DE REGISTRO";
                    break;
                }
                if (aux.isCertificadoEmitido()) {
                    validar = aux.getSolicitudSenadi() + " YA HA SIDO EMITIDO SU CERTIFICADO";;
                    break;
                }
            }
            if (validar.isEmpty()) {
                loginBean.setRenovacionesFlotantes(selectedRenovaciones);
                loginBean.setNotificadasFlotantes(new ArrayList<Notificada>());
                loginBean.setVarious(true);
                loginBean.setNewreport(true);
                loginBean.setSubrogante(true);
                if (separado) {
                    loginBean.setAllInOne(false);
                } else {
                    loginBean.setAllInOne(true);
                }

//            context.addCallbackParam("doit", true);
//            context.addCallbackParam("view", "newinforme");
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                PrimeFaces.current().ajax().addCallbackParam("view", "newinforme");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "ACCIÓN", "DESCARGANDO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "EL TRÁMITE " + validar);
            }
        } else {
            //context.addCallbackParam("doit", false);
            PrimeFaces.current().ajax().addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "DEBE SELECCIONAR AL MENOS UNA RENOVACIÓN");

            System.out.println("No hay seleccionadas");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarSeleccionados(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedRenovaciones.isEmpty()) {
            System.out.println("Eliminado Múltiples Renovaciones...");
//            System.out.println(selectedRenovaciones.toString());
            int n = 0;
            for (int i = 0; i < selectedRenovaciones.size(); i++) {
                Controlador c = new Controlador();
                if (c.removeRenovacion(selectedRenovaciones.get(i))) {
                    n++;
                } else {
                    System.out.println("No se eliminaron todas las renovaciones seleccionadas");
                }
            }
            if (n == selectedRenovaciones.size()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HAN ELIMINADO CORRECTAMENTE LOS REGISTROS SELECCIONADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ELIMINARON TODOS LOS REGISTROS SELECCIONADOS");
            }
            Controlador cont = new Controlador();
            renovaciones = cont.getRenovaciones();
            selectedRenovaciones = new ArrayList<>();

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void goToChangePass(ActionEvent ae) {
//        RequestContext context = RequestContext.getCurrentInstance();
//        context.addCallbackParam("doit", true);
//        context.addCallbackParam("view", "changepass.xhtml");
        PrimeFaces.current().ajax().addCallbackParam("doit", true);
        PrimeFaces.current().ajax().addCallbackParam("view", "changepass.xhtml");
    }

    public boolean validarFechas(Date ini, Date fin) {
        try {
            ini.toString();
            fin.toString();
            return true;
        } catch (Exception ex) {
            System.out.println("Error en fechas " + ex);
            return false;
        }
    }

    public void buscarRenovacionesPorFechaCertificado(ActionEvent ae) {
        FacesMessage msg = null;
//        System.out.println(getFechaInicio() + " " + getFechaFin());
        if (validarFechas(fechaInicioCertificado, fechaFinCertificado)) {
            //System.out.println(getFechaInicio() + " " + getFechaFin());
            Controlador c = new Controlador();
            renovaciones = new ArrayList<>();
            renovaciones = c.getRenovacionesByFechaCertificado(fechaInicioCertificado, fechaFinCertificado);
            numRegistros = "Número Registros Mostrados: " + renovaciones.size();

            if (renovaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "FECHAS INCORRECTAS.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarRenovacionesPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
//        System.out.println(getFechaInicio() + " " + getFechaFin());
        if (validarFechas(fechaInicio, fechaFin)) {
            //System.out.println(getFechaInicio() + " " + getFechaFin());
            Controlador c = new Controlador();
            renovaciones = new ArrayList<>();
            renovaciones = c.getRenovacionesByDate(fechaInicio, fechaFin);
            numRegistros = "Número Registros Mostrados: " + renovaciones.size();

            if (renovaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "FECHAS INCORRECTAS.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;
        if (renovacion != null && renovacion.getSolicitudSenadi() != null && !renovacion.getSolicitudSenadi().trim().isEmpty()) {
            String tramite = renovacion.getSolicitudSenadi();
            Controlador c = new Controlador();
            String msj = c.validarExistenciaTramite(tramite);
            if (!msj.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", msj);
            } else {

                RenewalForm rf = c.findRenewalFormsByApplicationNumber(tramite);

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
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
                                                            + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    renovacion = new Renovacion();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    renovacion.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
                                                            + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
                                                            + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    renovacion = new Renovacion();
                                                }
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                            }
                                        } else {
                                            if (c.existsTituloCanceladoByTituloAndDenominacion(renovacion.getRegistroNo(), renovacion.getDenominacion())) {
                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(renovacion.getRegistroNo(), renovacion.getDenominacion());
                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
                                                            + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    renovacion = new Renovacion();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    renovacion.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
                                                            + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + renovacion.getRegistroNo() + " CON DENOMINACIÓN "
                                                            + renovacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    renovacion = new Renovacion();
                                                }
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                            }
                                        }

                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                    }

                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                }

                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA RENOVACIÓN, SINO " + t.getName());
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD");
                        }

                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE NO ENCONTRADO");
                }
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN TRÁMITE VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void validarRenovacion(Renovacion renov) {
        FacesMessage msg = null;
        if (renov != null) {
            String rutaNotificacionCasillero = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(renov.getSolicitudSenadi(), true);
            if (!uploads.isEmpty()) {
                if (uploads.size() > 1) {
                    for (int i = 0; i < uploads.size(); i++) {
                        UploadNotificacion unaux = uploads.get(i);
                        String rutaux = "http://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
//                        System.out.println("documento: " + rutaux);

                        if (uploads.get(i).getDocumento().contains("_raz-")) {
                            rutaNotificacionCasillero = rutaux;
                            break;
                        } else if (uploads.get(i).getDocumento().contains("certificado") || uploads.get(i).getDocumento().contains("renovacion")) {
                            rutaNotificacionCasillero = rutaux;
                            break;
                        }
//                        System.out.println("rutaaux: " + rutaux);
//                        int conf = Operaciones.esCertificado(rutaux, "CERTIFICADO DE RENOVACIÓN");
//                        if (conf == 1) {
//                            rutaNotificacionCasillero = rutaux;
//                            break;
//                        }
                    }
                    if (!rutaNotificacionCasillero.trim().isEmpty()) {
                        PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                        PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGAD0");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCUENTRA EL CERTIFICADO");
                    }
                } else {
                    UploadNotificacion unaux = uploads.get(0);
                    System.out.println("documento: " + unaux.getDocumento());
                    rutaNotificacionCasillero = "http://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
//                    System.out.println("rutacertcas: " + rutaNotificacionCasillero);
                    PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGADO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGÚN CERTIFICADO DEL TRÁMITE " + renov.getSolicitudSenadi());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void verNotificacion(ActionEvent ae) {
        FacesMessage msg = null;
        renovacion = (Renovacion) renovacionesDataTable.getRowData();
        if (renovacion != null) {

            Controlador c = new Controlador();

            List<UploadNotificacion> uploads = c.getUploadNotificacionByTramite(renovacion.getSolicitudSenadi(), true);
            if (uploads.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
            } else {

                UploadNotificacion un = new UploadNotificacion();
                for (int i = 0; i < uploads.size(); i++) {
                    un = uploads.get(i);
                    String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + un.getCasillero() + "/" + un.getDocumento();
                    int conf = Operaciones.esCertificado(rutaux, "CERTIFICADO DE RENOVACIÓN No");
                    if (conf == 1) {
                        break;
                    }
                }
                if (un.getId() != null) {
                    PrimeFaces.current().ajax().addCallbackParam("notit", true);
                    PrimeFaces.current().ajax().addCallbackParam("rutanot", "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + un.getCasillero() + "/" + un.getDocumento());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "RENOVACIÓN", "VISUALIZANDO CERTIFICADO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
                }

            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "FECHA", "EL TRÁMITE NO POSEE FECHA DE VENCIMIENTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (renovacion != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + renovacion.getSolicitudSenadi();
            FTPFiles files = new FTPFiles(130);
            archivos = new ArrayList<>();
            archivos = files.listarDirectorio("/var/www/html/solicitudes/media/files/renewal_forms/" + renovacion.getIdRenewalForm());

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EXPEDIENTE CARGADO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL EXPEDIENTE");
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
     * @return the texto
     */
    public String getTexto() {
        return texto;
    }

    /**
     * @param texto the texto to set
     */
    public void setTexto(String texto) {
        this.texto = texto;
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
     * @return the renovacionesDataTable
     */
    public UIData getRenovacionesDataTable() {
        return renovacionesDataTable;
    }

    /**
     * @param renovacionesDataTable the renovacionesDataTable to set
     */
    public void setRenovacionesDataTable(UIData renovacionesDataTable) {
        this.renovacionesDataTable = renovacionesDataTable;
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
     * @return the saveEdit
     */
    public String getSaveEdit() {
        return saveEdit;
    }

    /**
     * @param saveEdit the saveEdit to set
     */
    public void setSaveEdit(String saveEdit) {
        this.saveEdit = saveEdit;
    }

    /**
     * @return the mensajeConfirmacion
     */
    public String getMensajeConfirmacion() {
        return mensajeConfirmacion;
    }

    /**
     * @param mensajeConfirmacion the mensajeConfirmacion to set
     */
    public void setMensajeConfirmacion(String mensajeConfirmacion) {
        this.mensajeConfirmacion = mensajeConfirmacion;
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
     * @return the exportName
     */
    public String getExportName() {
        return exportName;
    }

    /**
     * @param exportName the exportName to set
     */
    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    /**
     * @return the historial
     */
    public String getHistorial() {
        return historial;
    }

    /**
     * @param historial the historial to set
     */
    public void setHistorial(String historial) {
        this.historial = historial;
    }

    /**
     * @return the edicion
     */
    public boolean isEdicion() {
        return edicion;
    }

    /**
     * @param edicion the edicion to set
     */
    public void setEdicion(boolean edicion) {
        this.edicion = edicion;
    }

    /**
     * @return the estadoTemp
     */
    public String getEstadoTemp() {
        return estadoTemp;
    }

    /**
     * @param estadoTemp the estadoTemp to set
     */
    public void setEstadoTemp(String estadoTemp) {
        this.estadoTemp = estadoTemp;
    }

    /**
     * @return the loginBean
     */
    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * @param loginBean the loginBean to set
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
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
     * @return the separado
     */
    public boolean isSeparado() {
        return separado;
    }

    /**
     * @param separado the separado to set
     */
    public void setSeparado(boolean separado) {
        this.separado = separado;
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
     * @return the archivos
     */
    public List<String> getArchivos() {
        return archivos;
    }

    /**
     * @param archivos the archivos to set
     */
    public void setArchivos(List<String> archivos) {
        this.archivos = archivos;
    }

    /**
     * @return the fechaInicioCertificado
     */
    public Date getFechaInicioCertificado() {
        return fechaInicioCertificado;
    }

    /**
     * @param fechaInicioCertificado the fechaInicioCertificado to set
     */
    public void setFechaInicioCertificado(Date fechaInicioCertificado) {
        this.fechaInicioCertificado = fechaInicioCertificado;
    }

    /**
     * @return the fechaFinCertificado
     */
    public Date getFechaFinCertificado() {
        return fechaFinCertificado;
    }

    /**
     * @param fechaFinCertificado the fechaFinCertificado to set
     */
    public void setFechaFinCertificado(Date fechaFinCertificado) {
        this.fechaFinCertificado = fechaFinCertificado;
    }

}
