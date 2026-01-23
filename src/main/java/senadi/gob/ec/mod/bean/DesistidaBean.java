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
import senadi.gob.ec.mod.model.iepdep.HallmarkForms;
import senadi.gob.ec.mod.model.iepform.ModificacionApp;
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
@ManagedBean(name = "desistidaBean")
@ViewScoped
public class DesistidaBean implements Serializable {

    private List<Desistida> desistidas;
    private List<Desistida> desistidasFiltradas;

    private String texto;

    private Desistida desistida;

    private UIData desistidasDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;

    private String numRegistros;

    private String exportName;

    private boolean edicion;
    private String estadoTemp;

    private String historial;

    private boolean usuarioConsulta;

    private LoginBean loginBean;

    private Date fechaInicio;
    private Date fechaFin;

    private List<String> archivos;

    public DesistidaBean() {
        Controlador c = new Controlador();
        desistidas = c.getDesistidas();
        desistida = new Desistida();
        dialogTitle = "NUEVA DESISTIMIENTO";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Registro?";
        numRegistros = "Número Registros Mostrados: " + desistidas.size();
        exportName = "desistidas_" + Operaciones.formatDate(new Date());
        loginBean = getLogin();
        usuarioConsulta = !loginBean.isUsuarioConsulta();
    }

    public LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginBean loginBea = (LoginBean) session.getAttribute("loginBean");
        return loginBea;
    }

    public void loadNuevoRegistro(ActionEvent ae) {
        dialogTitle = "NUEVO DESISTIMIENTO";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Registro?";
        desistida = new Desistida();
        edicion = false;
        System.out.println("Nuevo desistimiento");
        if (desistida != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void buscarDesistidas(ActionEvent ae) {
        System.out.println("DESISTIDA - SE BUSCA: " + texto);
        FacesMessage msg = null;
        if (texto.contains("'")) {

            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO INGRESE CARACTERES ESPECIALES");

        } else {
            loadDesistidas();
            if (desistidas.size() > 0) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRADO RESULTADOS.");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void loadDesistidas() {
        Controlador c = new Controlador();
        desistidas = new ArrayList<>();
        desistidas = c.getDesistidasByCriteria(texto);
        numRegistros = "Número Registros Mostrados: " + desistidas.size();
    }

    public void guardarRegistro(ActionEvent ae) {
        FacesMessage msg = null;
        if (desistida != null) {
            Controlador c = new Controlador();
            if ((desistida.getCasilleroSenadi() == null || desistida.getCasilleroSenadi().trim().isEmpty()) && (desistida.getCasilleroJudicial() == null || desistida.getCasilleroJudicial().trim().isEmpty())) {
//                context.addCallbackParam("saved", false);
                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "CASILLERO", "Debe Ingresar un Casillero");
            } else if (!desistida.getCasilleroSenadi().trim().isEmpty() && !desistida.getCasilleroJudicial().trim().isEmpty()) {
//                context.addCallbackParam("saved", false);
                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "CASILLERO", "Debe Ingresar un solo Casillero de SENADI o Judicial");
            } else {
                if (desistida.getId() != null) {
                    if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                        if (estadoTemp.equals("RENOVACIONES")) {
                            Renovacion renova = new Renovacion();
                            renova.setEstado("");
                            renova.setSolicitudSenadi(desistida.getSolicitudSenadi().toUpperCase());
                            renova.setFechaPresentacion(desistida.getFechaPresentacion());
                            renova.setNoComprobantePresentSolic("");
                            renova.setNoComprobanteEmisionCert("");
                            renova.setTotalFoliosExpediente(desistida.getTotalFoliosExpediente());
                            renova.setFechaCertificado(desistida.getFechaCertificado());
                            renova.setCertificadoNo(c.getNextNumeroCertificado(renova.getFechaCertificado()));
                            renova.setTituloResolucion("");
                            renova.setRegistroNo(desistida.getRegistroNo());
                            renova.setFechaRegistro(desistida.getFechaRegistro());
                            renova.setFechaVenceRegistro(desistida.getFechaVenceRegistro());
                            renova.setDenominacion(desistida.getDenominacion());
                            renova.setLema("");
                            renova.setSigno(desistida.getSigno());
                            renova.setClase("");
                            renova.setProtege(desistida.getProtege());
                            renova.setTitularActual(desistida.getTitularActual());
                            renova.setTacNJ("");
                            renova.setNacTitularAc("");
                            renova.setAbogadoPatrocinadorApeApoRepre(desistida.getApeApodRepre());
                            renova.setCasilleroSenadi(desistida.getCasilleroSenadi());
                            renova.setCasilleroJudicial(desistida.getCasilleroJudicial());
                            renova.setResponsable(desistida.getResponsable());
                            renova.setIdentificacion(desistida.getIdentificacion());
                            renova.setCancelado(desistida.getCancelado());

                            if (c.validarExistenciaRenovacion(renova.getSolicitudSenadi())) {
//                                context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en renovaciones con el mismo número de solicitud");
                            } else {
                                if (c.saveRenovacion(renova)) {

                                    c = new Controlador();
                                    List<Desistida> desists = c.getDesistidasBySolSenadi(renova.getSolicitudSenadi());
                                    int cont = 0;
                                    for (int i = 0; i < desists.size(); i++) {
                                        if (c.removeDesistida(desists.get(i))) {
                                            cont++;
                                        }
                                    }
                                    if (desists.size() == cont) {
                                        c.saveHistorial("RENOVACIONES", "DESISTIDAS", renova.getSolicitudSenadi(), "PASADO A", 0, getLoginBean().getLogin());
                                        loadDesistidas();
                                        //context.addCallbackParam("saved", true);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + renova.getSolicitudSenadi() + " de desistida a renovación");
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                    } else {
                                        //context.addCallbackParam("saved", false);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA DESISTIDA");
                                    }
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                                }
                            }

                        } else if (estadoTemp.equals("NOTIFICADAS")) {
                            Notificada notificada = new Notificada();
                            notificada.setTipoSolicitante("");
                            notificada.setSolicitud(desistida.getSolicitudSenadi().toUpperCase());
                            notificada.setFechaPresentacion(desistida.getFechaPresentacion());
                            notificada.setNoComprobantePresentSolic("");
                            notificada.setNoComprobanteEmisionCert("");
                            notificada.setTotalFoliosExpediente(desistida.getTotalFoliosExpediente());
                            notificada.setFechaElaboraNotificacion(new Date());
                            notificada.setNotificacion(c.getNextNumeroNotificacion(notificada.getFechaElaboraNotificacion()));
                            notificada.setFechaCertificado(desistida.getFechaCertificado());
                            notificada.setTituloResolucion("");
                            notificada.setRegistroNo(desistida.getRegistroNo());
                            notificada.setFechaRegistro(desistida.getFechaRegistro());
                            notificada.setFechaVenceRegistro(desistida.getFechaVenceRegistro());
                            notificada.setDenominacion(desistida.getDenominacion());
                            notificada.setLema("");
                            notificada.setSigno(desistida.getSigno());
                            notificada.setClase("");
                            notificada.setProtege(desistida.getProtege());
                            notificada.setTitularActual(desistida.getTitularActual());
                            notificada.setTacNJ("");
                            notificada.setNacTitularAc("");
                            notificada.setDomicilioTitularAc("");
                            notificada.setAr(desistida.getAr());
                            notificada.setNj(desistida.getNj());
                            notificada.setTitApodRepre(desistida.getTitApodRepre());
                            notificada.setApeApodRepre(desistida.getApeApodRepre());
                            notificada.setNomApodRepre(desistida.getNomApodRepre());
                            notificada.setFechaNotifica(desistida.getFechaNotifica());
                            notificada.setCasilleroSenadi(desistida.getCasilleroSenadi());
                            notificada.setCasilleroJudicial(desistida.getCasilleroJudicial());
                            notificada.setRo(desistida.getRo());
                            notificada.setProvidencia("");
                            notificada.setFechaProvidencia(new Date());
                            notificada.setFechaNotificaPro(new Date());
                            notificada.setResponsable(desistida.getResponsable());
                            notificada.setIdentificacion(desistida.getIdentificacion());
                            notificada.setCancelado(desistida.getCancelado());

                            if (c.validarExistenciaNotificada(notificada.getSolicitud())) {
//                                context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en notificadas con el mismo número de solicitud");
                            } else {
                                if (c.saveNotificada(notificada)) {

                                    c = new Controlador();
                                    List<Desistida> desists = c.getDesistidasBySolSenadi(notificada.getSolicitud());
                                    int cont = 0;
                                    for (int i = 0; i < desists.size(); i++) {
                                        if (c.removeDesistida(desists.get(i))) {
                                            cont++;
                                        }
                                    }
                                    if (desists.size() == cont) {
                                        c.saveHistorial("NOTIFICADAS", "DESISTIDAS", notificada.getSolicitud(), "PASADO A", 0, getLoginBean().getLogin());

                                        loadDesistidas();
                                        //context.addCallbackParam("saved", true);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + notificada.getSolicitud() + " de desistida a notificada");
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                    } else {
                                        //context.addCallbackParam("saved", false);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA DESISTIDAS");
                                    }
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                                }
                            }
                        } else {
                            Caducada caducada = new Caducada();
                            caducada.setSolicitudSenadi(desistida.getSolicitudSenadi().toUpperCase());
                            caducada.setFechaSolicitud(desistida.getFechaPresentacion()); //<-----------
                            caducada.setDenominacion(desistida.getDenominacion());
                            caducada.setRegistroNo(desistida.getRegistroNo());
                            caducada.setFechaRegistro(desistida.getFechaRegistro());
                            caducada.setSolicitante("");
                            caducada.setFechaProvidencia(new Date());
                            caducada.setResolucion(c.getNextResCaducadaNumber(caducada.getFechaProvidencia()) + "");
                            caducada.setFechaNotificacion(desistida.getFechaNotifica());
                            caducada.setAbogadoPatrocinador(desistida.getApeApodRepre());
                            caducada.setCasilleroNo(!desistida.getCasilleroSenadi().isEmpty() ? desistida.getCasilleroSenadi() : desistida.getCasilleroJudicial());
                            caducada.setEmail("");
                            caducada.setRequisitoNotificado("");
                            caducada.setCertificaNo(desistida.getCertificadoNo());
                            caducada.setFechaOtorgada(new Date());
                            caducada.setSolicitud(desistida.getSolicitudNo());
                            caducada.setFechaSolicitud(new Date());
                            caducada.setResponsable(desistida.getResponsable());
                            caducada.setSigno(desistida.getSigno());
                            caducada.setFechaVencimiento(desistida.getFechaVenceRegistro());
                            caducada.setIdentificacion(desistida.getIdentificacion());
                            caducada.setCancelado(desistida.getCancelado());

                            if (c.validarExistenciaCaducada(caducada.getSolicitudSenadi())) {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en caducadas con el mismo número de solicitud");
                            } else {
                                if (c.saveCaducada(caducada)) {

                                    c = new Controlador();
                                    List<Desistida> desists = c.getDesistidasBySolSenadi(caducada.getSolicitudSenadi());
                                    int cont = 0;
                                    for (int i = 0; i < desists.size(); i++) {
                                        if (c.removeDesistida(desists.get(i))) {
                                            cont++;
                                        }
                                    }
                                    if (desists.size() == cont) {
                                        c.saveHistorial("CADUCADAS-NEGADAS", "DESISTIDAS", caducada.getSolicitudSenadi(), "PASADO A", 0, getLoginBean().getLogin());
                                        loadDesistidas();
                                        //context.addCallbackParam("saved", true);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + caducada.getSolicitudSenadi() + " de desistida a caducada-negada");
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                    } else {
                                        //context.addCallbackParam("saved", false);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA RENOVACIÓN");
                                    }
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                                }
                            }
                        }
                    } else {
                        if (c.validarExistsDesistida(desistida)) {
                            //context.addCallbackParam("saved", false);
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD.");
                        } else {
                            desistida.setSolicitudSenadi(desistida.getSolicitudSenadi().toUpperCase());
                            if (c.updateDesistida(desistida)) {
                                c.saveHistorial("DESISTIDA", "DESISTIDA", desistida.getSolicitudSenadi(), "EDITADO", 0, getLoginBean().getLogin());
                                loadDesistidas();
                                System.out.println("Desistimiento " + (desistida.getSolicitudSenadi() != null ? desistida.getSolicitudSenadi() : "") + " editado");
                                //context.addCallbackParam("saved", true);
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "DESISTIMIENTO EDITADO CORRECTAMENTE.");
                            } else {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ERROR AL EDITAR DESISTIMIENTO.");
                            }
                        }
                    }
                } else {
                    if (c.validarExistenciaDesistida(desistida.getSolicitudSenadi())) {
                        //context.addCallbackParam("saved", false);
                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "YA EXISTE UN TRÁMITE CON EL MISMO NÚMERO DE SOLICITUD");
                    } else {
                        ModificacionApp mapp = c.getModificacionApp(desistida.getSolicitudSenadi());
                        if (mapp.getId() != null) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                        } else {
                            boolean habilitado = true;
                            if (desistida.getDenominacion() != null && !desistida.getDenominacion().trim().isEmpty()
                                    && desistida.getRegistroNo() != null && !desistida.getRegistroNo().trim().isEmpty()) {
                                if (c.existsTituloCanceladoByTituloAndDenominacion(desistida.getRegistroNo(), desistida.getDenominacion())) {
                                    TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(desistida.getRegistroNo(), desistida.getDenominacion());
                                    if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistida.getRegistroNo() + " CON DENOMINACIÓN '"
                                                + desistida.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                        desistida = new Desistida();
                                        habilitado = false;
                                    } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                        desistida.setCancelado(titca.getTipoCancelacion());
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistida.getRegistroNo() + " CON DENOMINACIÓN '"
                                                + desistida.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistida.getRegistroNo() + " CON DENOMINACIÓN '"
                                                + desistida.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                        desistida = new Desistida();
                                        habilitado = false;
                                    }
                                } else {
                                    habilitado = true;
                                }
                            }

                            if (habilitado) {
                                desistida.setSolicitudSenadi(desistida.getSolicitudSenadi().toUpperCase());
                                if (c.saveDesistida(desistida)) {
                                    c.saveModificacionApp(desistida.getDenominacion(), desistida.getRegistroNo(), desistida.getSolicitudSenadi(), "RENOVACION", loginBean.getNombre());
                                    c.saveHistorial("DESISTIDA", "DESISTIDA", desistida.getSolicitudSenadi(), "CREADO", 0, getLoginBean().getLogin());
                                    loadDesistidas();
                                    System.out.println("Nuevo desistimiento " + (desistida.getSolicitudSenadi() != null ? desistida.getSolicitudSenadi() : "") + " guardado");
                                    //context.addCallbackParam("saved", true);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "GUARDADO", "DESISTIMIENTO GUARDADO CORRECTAMENTE.");
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ERROR AL GUARDAR.");
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
        dialogTitle = "EDITAR DESISTIDA";
        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        desistida = (Desistida) desistidasDataTable.getRowData();
        if (desistida != null) {
            mensajeConfirmacion = "¿Seguro de editar el registro: " + desistida.getSolicitudSenadi() + "?";

            Controlador c = new Controlador();
            RenewalForm rf = c.findRenewalFormsByApplicationNumber(desistida.getSolicitudSenadi());
            if (rf.getId() != null) {
                desistida.setIdRenewalForm(rf.getId());
            } else {
                desistida.setIdRenewalForm(null);
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESISTIMIENTO CARGADO.");
            //context.addCallbackParam("peditar", true);
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR DESISTIMIENTO");
            //context.addCallbackParam("peditar", false);
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarDesistida(ActionEvent ae) {
        FacesMessage msg = null;
        desistida = (Desistida) desistidasDataTable.getRowData();
        if (desistida != null) {
            Controlador c = new Controlador();
            if (c.removeDesistida(desistida)) {
                loadDesistidas();
                System.out.println("Desistimiento " + desistida.getSolicitudSenadi() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESISTIMIENTO " + desistida.getSolicitudSenadi() + " ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR DESISTIMIENTO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR DESISTIMIENTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        desistida = (Desistida) desistidasDataTable.getRowData();
        if (desistida != null) {
            dialogTitle = "SEGUIMIENTO " + desistida.getSolicitudSenadi();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(desistida.getSolicitudSenadi());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: DESISTIDAS";
            }
        }
    }

    public boolean validarFechas() {
        try {
            getFechaInicio().toString();
            getFechaFin().toString();
            return true;
        } catch (Exception ex) {
            System.out.println("Error en fechas " + ex);
            return false;
        }

    }

    public void buscarDesistidasPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            desistidas = new ArrayList<>();
            desistidas = c.getDesistidasByDate(fechaInicio, fechaFin);
            numRegistros = "Número Registros Mostrados: " + desistidas.size();

            if (!desistidas.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRARON RESULTADOS.");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "FECHAS INCORRECTAS.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

        if (desistida != null && desistida.getSolicitudSenadi() != null && !desistida.getSolicitudSenadi().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = desistida.getSolicitudSenadi();
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

                                desistida.setFechaPresentacion(rf.getApplicationDate());

                                desistida.setSigno(ttp.getAlias());
                                desistida.setCasilleroSenadi(c.getCasilleroSenadi(rf.getOwnerId()) + "");
                                desistida.setIdRenewalForm(rf.getId());

                                if (rf.getDebugId() != null && rf.getDebugId() != 0) {

                                    HallmarkForms hf = c.getHallmarkForm(rf.getDebugId());

                                    if (hf.getId() != null) {
                                        desistida.setDenominacion(hf.getDenomination());
                                        desistida.setRegistroNo(hf.getExpedient());

                                        if (desistida.getRegistroNo() != null && !desistida.getRegistroNo().trim().isEmpty()) {
                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByNumeroTitulo(desistida.getRegistroNo());
                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                desistida.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                            }
                                        }
                                    }
                                } else {
                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                        PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                        if (ps.getCodigoSolicitudSigno() != null) {
                                            desistida.setDenominacion(ps.getDenominacionSigno());
                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                desistida.setRegistroNo(titulo.getNumeroTitulo());
                                                desistida.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                            }
                                        }
                                    }
                                }

                                Person tit = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                if (tit.getId() != null) {
                                    desistida.setTitularActual(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "APPLICANT"));
                                    desistida.setIdentificacion(tit.getIdentificationNumber());
                                }

                                Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                if (apoder.getId() != null) {
                                    desistida.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "LAWYER"));
                                } else {
                                    apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                    if (apoder.getId() != null) {
                                        desistida.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "ATTORNEY"));
                                    }
                                }

                                if (desistida.getRegistroNo() != null && !desistida.getRegistroNo().trim().isEmpty()) {
                                    if (desistida.getDenominacion() != null && !desistida.getDenominacion().trim().isEmpty()) {
                                        if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                            if (c.existsTituloCanceladoByTituloAndExpediente(desistida.getRegistroNo(), rf.getExpedient())) {
                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(desistida.getRegistroNo(), rf.getExpedient());
                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistida.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + desistida.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    desistida = new Desistida();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    desistida.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistida.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + desistida.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistida.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + desistida.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    desistida = new Desistida();
                                                }
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                            }
                                        } else {
                                            if (c.existsTituloCanceladoByTituloAndDenominacion(desistida.getRegistroNo(), desistida.getDenominacion())) {
                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(desistida.getRegistroNo(), desistida.getDenominacion());
                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistida.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + desistida.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    desistida = new Desistida();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    desistida.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistida.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + desistida.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistida.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + desistida.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    desistida = new Desistida();
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

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (desistida != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + desistida.getSolicitudSenadi();
            FTPFiles files = new FTPFiles(130);
            archivos = new ArrayList<>();
            archivos = files.listarDirectorio("/var/www/html/solicitudes/media/files/renewal_forms/" + desistida.getIdRenewalForm());

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EXPEDIENTE CARGADO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL EXPEDIENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the desistidas
     */
    public List<Desistida> getDesistidas() {
        return desistidas;
    }

    /**
     * @param desistidas the desistidas to set
     */
    public void setDesistidas(List<Desistida> desistidas) {
        this.desistidas = desistidas;
    }

    /**
     * @return the desistidasFiltradas
     */
    public List<Desistida> getDesistidasFiltradas() {
        return desistidasFiltradas;
    }

    /**
     * @param desistidasFiltradas the desistidasFiltradas to set
     */
    public void setDesistidasFiltradas(List<Desistida> desistidasFiltradas) {
        this.desistidasFiltradas = desistidasFiltradas;
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
     * @return the desistida
     */
    public Desistida getDesistida() {
        return desistida;
    }

    /**
     * @param desistida the desistida to set
     */
    public void setDesistida(Desistida desistida) {
        this.desistida = desistida;
    }

    /**
     * @return the desistidasDataTable
     */
    public UIData getDesistidasDataTable() {
        return desistidasDataTable;
    }

    /**
     * @param desistidasDataTable the desistidasDataTable to set
     */
    public void setDesistidasDataTable(UIData desistidasDataTable) {
        this.desistidasDataTable = desistidasDataTable;
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
}
