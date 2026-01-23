/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
import senadi.gob.ec.mod.model.Abandono;
import senadi.gob.ec.mod.model.Caducada;
import senadi.gob.ec.mod.model.Desistida;
import senadi.gob.ec.mod.model.Historial;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.Renovacion;
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
@ManagedBean(name = "notificadaBean")
@ViewScoped
public class NotificadaBean implements Serializable {

    private List<Notificada> notificadas;
    private List<Notificada> notificadasFiltradas;
    private List<Notificada> selectedNotificadas;

    private String texto;

    private Notificada notificada;

    private UIData notificadasDataTable;

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

    private Date fechaInicioNotificacion;
    private Date fechaFinNotificacion;

    private String roshow;

    private List<String> archivos;
    private boolean abandonosS;
    private Date fechaPuestaAbandono;

    private String tipoAbandono;

    public NotificadaBean() {
        Controlador c = new Controlador();

//        c.banderaNotificacionesEmitidas();
        notificadas = c.getNotificadas();
        selectedNotificadas = new ArrayList<>();
        notificada = new Notificada();
        dialogTitle = "NUEVA NOTIFICACIÓN";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Registro?";
        numRegistros = "Número Registros Mostrados: " + notificadas.size();
        exportName = "notificadas_" + Operaciones.formatDate(new Date());
        loginBean = getLogin();
        usuarioConsulta = !loginBean.isUsuarioConsulta();
    }

    private LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginBean loginB = (LoginBean) session.getAttribute("loginBean");
        return loginB;
    }

    public void validarNotificacion(Notificada notif) {
        FacesMessage msg = null;
        if (notif != null) {
            String rutaNotificacionCasillero = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(notif.getSolicitud(), true);
            if (!uploads.isEmpty()) {
                if (uploads.size() > 1) {
                    for (int i = 0; i < uploads.size(); i++) {
                        UploadNotificacion unaux = uploads.get(i);
                        String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                        int conf = Operaciones.esCertificado(rutaux, "CERTIFICADO DE RENOVACIÓN No.");
                        if (conf == 0) {
                            rutaNotificacionCasillero = rutaux;
                            break;
                        }
                    }
                    if (!rutaNotificacionCasillero.trim().isEmpty()) {
                        PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                        PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGAD0");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADA");
                    }
                } else {
                    UploadNotificacion unaux = uploads.get(0);
                    System.out.println("documento: " + unaux.getDocumento());
                    rutaNotificacionCasillero = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
//                    System.out.println("rutacertcas: " + rutaNotificacionCasillero);
                    PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGADO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGÚN CERTIFICADO DEL TRÁMITE " + notif.getSolicitud());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarNotificadas(ActionEvent ae) {
        System.out.println("NOTIFICADA - SE BUSCA: " + texto);
        FacesMessage msg = null;
        if (texto.contains("'")) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO INGRESE CARACTERES ESPECIALES");
        } else if (texto.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            notificadas = c.getNotificadasByCriteria(texto);
//            loadNotificadas();
            if (notificadas.size() > 0) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRADO RESULTADOS.");
            }

        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void loadNotificadas() {
        Controlador c = new Controlador();
        notificadas = new ArrayList<>();
        texto = "";
        notificadas = c.getNotificadas();
        numRegistros = "Número Registros Mostrados: " + notificadas.size();
    }

    public void loadNuevoRegistro(ActionEvent ae) {
        dialogTitle = "NUEVA NOTIFICACIÓN";
        saveEdit = "GUARDAR";
        edicion = false;
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Registro?";
        notificada = new Notificada();
        System.out.println("Nueva notificación");
        if (notificada != null) {
//            RequestContext context = RequestContext.getCurrentInstance();
//            context.addCallbackParam("doit", true);
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void prepararViewRo(ActionEvent ae) {
        notificada = (Notificada) notificadasDataTable.getRowData();
        if (notificada != null) {
            Controlador c = new Controlador();
            roshow = notificada.getRo();
            PrimeFaces.current().ajax().addCallbackParam("viewro", true);
        } else {
            PrimeFaces.current().ajax().addCallbackParam("viewro", false);
        }
    }

    public void guardarRegistro(ActionEvent ae) {
        FacesMessage msg = null;
        if (notificada != null) {
            Controlador c = new Controlador();
            if ((notificada.getCasilleroSenadi() == null || notificada.getCasilleroSenadi().trim().isEmpty()) && (notificada.getCasilleroJudicial() == null || notificada.getCasilleroJudicial().trim().isEmpty())) {
                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "CASILLERO", "Debe Ingresar un Casillero");
            } else {
                if (notificada.getId() != null) {
                    if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                        if (estadoTemp.equals("RENOVACIONES")) {
                            Renovacion renova = new Renovacion();
                            renova.setEstado("");
                            renova.setSolicitudSenadi(notificada.getSolicitud().toUpperCase());
                            renova.setFechaPresentacion(notificada.getFechaPresentacion());
                            renova.setNoComprobantePresentSolic(notificada.getNoComprobantePresentSolic());
                            renova.setNoComprobanteEmisionCert(notificada.getNoComprobanteEmisionCert());
                            renova.setTotalFoliosExpediente(notificada.getTotalFoliosExpediente());
                            renova.setFechaCertificado(new Date());
                            renova.setCertificadoNo(c.getNextNumeroCertificado(renova.getFechaCertificado()));
                            renova.setTituloResolucion(notificada.getTituloResolucion());
                            renova.setRegistroNo(notificada.getRegistroNo());
                            renova.setFechaRegistro(notificada.getFechaRegistro());
                            renova.setFechaVenceRegistro(notificada.getFechaVenceRegistro());
                            renova.setDenominacion(notificada.getDenominacion());
                            renova.setLema(notificada.getLema());
                            renova.setSigno(notificada.getSigno());
                            renova.setClase(notificada.getClase());
                            renova.setProtege(notificada.getProtege());
                            renova.setTitularActual(notificada.getTitularActual());
                            renova.setTacNJ(notificada.getTacNJ());
                            renova.setNacTitularAc(notificada.getNacTitularAc());
                            renova.setAbogadoPatrocinadorApeApoRepre(notificada.getApeApodRepre());
                            renova.setCasilleroSenadi(notificada.getCasilleroSenadi());
                            renova.setCasilleroJudicial(notificada.getCasilleroJudicial());
                            renova.setResponsable(notificada.getResponsable());
                            renova.setIdentificacion(notificada.getIdentificacion());
                            renova.setCertificadoEmitido(notificada.isCertificadoEmitido());
                            renova.setNotificacionEmitida(notificada.isNotificacionEmitida());
                            renova.setCancelado(notificada.getCancelado());

                            if (c.validarExistenciaRenovacion(renova.getSolicitudSenadi())) {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en renovaciones con el mismo número de solicitud");
                            } else {
                                if (c.saveRenovacion(renova)) {

                                    c = new Controlador();
                                    List<Notificada> notis = c.getNotificadasBySolSenadi(renova.getSolicitudSenadi());
                                    int cont = 0;
                                    for (int i = 0; i < notis.size(); i++) {
                                        if (c.removeNotificada(notis.get(i))) {
                                            cont++;
                                        }
                                    }
                                    if (notis.size() == cont) {
                                        c.saveHistorial("RENOVACIONES", "NOTIFICADAS", renova.getSolicitudSenadi(), "PASADO A", 0, loginBean.getLogin());

                                        loadNotificadas();
                                        //context.addCallbackParam("saved", true);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + renova.getSolicitudSenadi() + " de notificada a renovación");
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                    } else {
                                        //context.addCallbackParam("saved", false);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA NOTIFICACIÓN");
                                    }
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                                }
                            }

                        } else if (estadoTemp.equals("DESISTIDAS")) {
                            Desistida desist = new Desistida();
                            desist.setEstado("DESISTE");
                            desist.setSolicitudSenadi(notificada.getSolicitud().toUpperCase());
                            desist.setIepi(notificada.getSolicitud());
                            desist.setSolicitudNo("");
                            desist.setFechaPresentacion(notificada.getFechaPresentacion());
                            desist.setTotalFoliosExpediente(notificada.getTotalFoliosExpediente());
                            desist.setCertificadoNo(notificada.getNotificacion());
                            desist.setFechaCertificado(notificada.getFechaCertificado());
                            desist.setRegistroNo(notificada.getRegistroNo());
                            desist.setFechaRegistro(notificada.getFechaRegistro());
                            desist.setFechaVenceRegistro(notificada.getFechaVenceRegistro());
                            desist.setDenominacion(notificada.getDenominacion());
                            desist.setSigno(notificada.getSigno());
                            desist.setProtege(notificada.getProtege());
                            desist.setTitularActual(notificada.getTitularActual());
                            desist.setAr(notificada.getAr());
                            desist.setNj(notificada.getNj());
                            desist.setTitApodRepre(notificada.getTitApodRepre());
                            desist.setApeApodRepre(notificada.getApeApodRepre());
                            desist.setNomApodRepre(notificada.getNomApodRepre());
                            desist.setFechaElaboraNotificacion(notificada.getFechaElaboraNotificacion());
                            desist.setFechaNotifica(notificada.getFechaNotifica());
                            desist.setCasilleroSenadi(notificada.getCasilleroSenadi());
                            desist.setCasilleroJudicial(notificada.getCasilleroJudicial());
                            desist.setRo(notificada.getRo());
                            desist.setResponsable(notificada.getResponsable());
                            desist.setFechaDesistida(new Date());
                            desist.setIdentificacion(notificada.getIdentificacion());
                            desist.setCancelado(notificada.getCancelado());

                            if (c.validarExistenciaDesistida(desist.getSolicitudSenadi())) {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en desistidas con el mismo número de solicitud");
                            } else {
                                if (c.saveDesistida(desist)) {

                                    c = new Controlador();
                                    List<Notificada> notis = c.getNotificadasBySolSenadi(desist.getSolicitudSenadi());
                                    int cont = 0;
                                    for (int i = 0; i < notis.size(); i++) {
                                        if (c.removeNotificada(notis.get(i))) {
                                            cont++;
                                        }
                                    }
                                    if (notis.size() == cont) {
                                        c.saveHistorial("DESISTIDAS", "NOTIFICADAS", desist.getSolicitudSenadi(), "PASADO A", 0, loginBean.getLogin());
                                        loadNotificadas();
                                        //context.addCallbackParam("saved", true);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + desist.getSolicitudSenadi() + " de notificada a desistida");
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                    } else {
                                        //context.addCallbackParam("saved", false);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA NOTIFICACIÓN");
                                    }
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                                }
                            }
                        } else {
                            Caducada caducada = new Caducada();
                            caducada.setSolicitudSenadi(notificada.getSolicitud().toUpperCase());
                            caducada.setFechaSolicitud(notificada.getFechaPresentacion()); //<-----------
                            caducada.setDenominacion(notificada.getDenominacion());
                            caducada.setRegistroNo(notificada.getRegistroNo());
                            caducada.setFechaRegistro(notificada.getFechaRegistro());
                            caducada.setSolicitante("");
                            caducada.setFechaProvidencia(new Date());
                            caducada.setResolucion(c.getNextResCaducadaNumber(caducada.getFechaProvidencia()) + "");
                            caducada.setFechaNotificacion(notificada.getFechaNotifica());
                            caducada.setAbogadoPatrocinador(notificada.getApeApodRepre());
                            caducada.setCasilleroNo(!notificada.getCasilleroSenadi().isEmpty() ? notificada.getCasilleroSenadi() : notificada.getCasilleroJudicial());
                            caducada.setEmail("");
                            caducada.setRequisitoNotificado("");
                            caducada.setCertificaNo(notificada.getNotificacion());
                            caducada.setFechaOtorgada(new Date());
                            caducada.setSolicitud("");
                            caducada.setFechaSolicitud(new Date());
                            caducada.setResponsable(notificada.getResponsable());
                            caducada.setSigno(notificada.getSigno());
                            caducada.setFechaVencimiento(notificada.getFechaVenceRegistro());
                            caducada.setIdentificacion(notificada.getIdentificacion());
                            caducada.setCancelado(notificada.getCancelado());

                            if (c.validarExistenciaCaducada(caducada.getSolicitudSenadi())) {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en caducadas con el mismo número de solicitud");
                            } else {
                                if (c.saveCaducada(caducada)) {

                                    c = new Controlador();
                                    List<Notificada> notis = c.getNotificadasBySolSenadi(caducada.getSolicitudSenadi());
                                    int cont = 0;
                                    for (int i = 0; i < notis.size(); i++) {
                                        if (c.removeNotificada(notis.get(i))) {
                                            cont++;
                                        }
                                    }
                                    if (notis.size() == cont) {
                                        c.saveHistorial("CADUCADAS-NEGADAS", "NOTIFICADAS", caducada.getSolicitudSenadi(), "PASADO A", 0, loginBean.getLogin());
                                        loadNotificadas();
                                        //context.addCallbackParam("saved", true);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + caducada.getSolicitudSenadi() + " de notificada a caducada-negada");
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                    } else {
                                        //context.addCallbackParam("saved", false);
                                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA NOTIFICACIÓN");
                                    }
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                                }
                            }
                        }
                    } else {
                        if (c.validarExistsNot(notificada)) {
                            //context.addCallbackParam("saved", false);
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD.");
                        } else {
                            notificada.setSolicitud(notificada.getSolicitud().toUpperCase());
                            if (c.updateNotificada(notificada)) {
                                c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", notificada.getSolicitud(), "EDITADO", 0, loginBean.getLogin());
                                System.out.println("Notificación " + (notificada.getSolicitud() != null ? notificada.getSolicitud() : "") + " editada");
                                //context.addCallbackParam("saved", true);
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "NOTICACIÓN EDITADA CORRECTAMENTE.");
                            } else {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ERROR AL EDITAR NOTIFICACIÓN.");
                            }
                        }
                    }
                } else {
                    if (c.validarExistenciaNotificada(notificada.getSolicitud())) {
                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "YA EXISTE UN TRÁMITE CON EL MISMO NÚMERO DE SOLICITUD");
                    } else {
                        ModificacionApp mapp = c.getModificacionApp(notificada.getSolicitud());
                        if (mapp.getId() != null) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                        } else {
                            boolean habilitado = true;
                            if (notificada.getDenominacion() != null && !notificada.getDenominacion().trim().isEmpty()
                                    && notificada.getRegistroNo() != null && !notificada.getRegistroNo().trim().isEmpty()) {
                                if (c.existsTituloCanceladoByTituloAndDenominacion(notificada.getRegistroNo(), notificada.getDenominacion())) {
                                    TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(notificada.getRegistroNo(), notificada.getDenominacion());
                                    if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                + notificada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                        notificada = new Notificada();
                                        habilitado = false;
                                    } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                        notificada.setCancelado(titca.getTipoCancelacion());
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                + notificada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                + notificada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                        notificada = new Notificada();
                                        habilitado = false;
                                    }
                                } else {
                                    habilitado = true;
                                }
                            }

                            if (habilitado) {
                                notificada.setSolicitud(notificada.getSolicitud().toUpperCase());
                                notificada.setNotificacion(c.getNextNumeroNotificacion(notificada.getFechaElaboraNotificacion()));
                                if (c.saveNotificada(notificada)) {
                                    c.saveModificacionApp(notificada.getDenominacion(), notificada.getRegistroNo(), notificada.getSolicitud(), "RENOVACION", loginBean.getNombre());
                                    c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", notificada.getSolicitud(), "CREADO", 0, loginBean.getLogin());
                                    System.out.println("Nueva notificación " + (notificada.getSolicitud() != null ? notificada.getSolicitud() : "") + " guardado");
                                    //context.addCallbackParam("saved", true);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "GUARDADO", "NOTIFICACIÓN GUARDADA CORRECTAMENTE.");
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
        dialogTitle = "EDITAR NOTIFICACIÓN";
        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        notificada = (Notificada) notificadasDataTable.getRowData();
        if (notificada != null) {
            mensajeConfirmacion = "¿Seguro de editar la Notificación: " + notificada.getSolicitud() + "?";

            Controlador c = new Controlador();
            RenewalForm rf = c.findRenewalFormsByApplicationNumber(notificada.getSolicitud());
            if (rf.getId() != null) {
                notificada.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN CARGADA.");
//            System.out.println(notificada.toString());
//            context.addCallbackParam("peditar", true);
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR NOTIFICACIÓN");
            //context.addCallbackParam("peditar", false);
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarNotificada(ActionEvent ae) {
        FacesMessage msg = null;
        notificada = (Notificada) notificadasDataTable.getRowData();
        if (notificada != null) {
            Controlador c = new Controlador();
            if (c.removeNotificada(notificada)) {
                c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", notificada.getSolicitud(), "ELIMINADO", 0, loginBean.getLogin());
                System.out.println("Notificación " + notificada.getSolicitud() + " Eliminada");
                loadNotificadas();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN " + notificada.getSolicitud() + " ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR NOTIFICACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR RENOVACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        notificada = (Notificada) notificadasDataTable.getRowData();
        if (notificada != null) {
            dialogTitle = "SEGUIMIENTO " + notificada.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(notificada.getSolicitud());
            setHistorial("");
            for (int i = 0; i < hists.size(); i++) {
                setHistorial(getHistorial() + hists.get(i).toString() + "\n");
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: NOTIFICADA";
            }
        }
    }

    public void viewNotificada(ActionEvent ae) {
        System.out.println("Visualizando Notificada....");
        FacesMessage msg = null;
        notificada = (Notificada) notificadasDataTable.getRowData();
//        RequestContext context = RequestContext.getCurrentInstance();
        if (notificada != null) {
            Controlador c = new Controlador();

            if (c.validarSecretarioActivo()) {
                RenewalForm aux = c.findRenewalFormsByApplicationNumber(notificada.getSolicitud());
                if (aux.getId() != null) {
                    notificada.setCasilleroSenadi(c.getCasilleroSenadi(aux.getOwnerId()) + "");
                }
                if (notificada.getCasilleroSenadi() != null && !notificada.getCasilleroSenadi().trim().isEmpty()) {
                    if (!notificada.getRo().trim().isEmpty()) {
                        loginBean.setRenovacionFlotante(null);
                        loginBean.setNotificadaFlotante(notificada);
                        loginBean.setVarious(false);

                        boolean doit = true;

                        PrimeFaces.current().ajax().addCallbackParam("doit", doit);
                        PrimeFaces.current().ajax().addCallbackParam("view", "reportes");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "VISUALIZANDO REPORTE");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "ERROR", "NO EXISTE LA RAZÓN DE LA NOTIFICACIÓN");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE DEBE POSEER UNA CASILLA CORRECTA");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL U");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedNotificadas.isEmpty()) {
            Controlador c = new Controlador();
            if (c.validarSecretarioActivo()) {

                boolean aviso = true;
                String tramaux = "";
                for (int i = 0; i < selectedNotificadas.size(); i++) {
                    Notificada aux = selectedNotificadas.get(i);
                    if (aux.getCasilleroSenadi() == null || aux.getCasilleroSenadi().trim().isEmpty()) {
                        tramaux = "EL TRÁMITE " + aux.getSolicitud() + " NO POSEE CASILLERO";
                        aviso = false;
                        break;
                    }
                    if (aux.getRo().trim().isEmpty()) {
                        tramaux = "EL TRÁMITE " + aux.getSolicitud() + " NO POSEE RAZÓN DE NOTIFICACIÓN";
                        aviso = false;
                        break;
                    }
                }

                if (aviso) {
                    System.out.println("Descargando Múltiples Notitificadas...");
                    loginBean.setNotificadasFlotantes(selectedNotificadas);
                    loginBean.setRenovacionesFlotantes(new ArrayList<Renovacion>());
                    loginBean.setVarious(true);

//                    if (separado) {
//                        System.out.println("separado: "+separado);
                    loginBean.setAllInOne(false);
//                    } else {
//                        System.out.println("no separado: "+separado);
//                        loginBean.setAllInOne(true);
//                    }

                    PrimeFaces.current().ajax().addCallbackParam("doit", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", "reportes");

                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "VISUALIZANDO REPORTE");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", tramaux);
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
            }

        } else {
            PrimeFaces.current().ajax().addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "DEBE SELECCIONAR AL MENOS UNA RENOVACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public void buscarNotificacionesPorFechaNotificacion(ActionEvent ae) {
        FacesMessage msg = null;
//        System.out.println(getFechaInicio() + " " + getFechaFin());
        if (validarFechas(fechaInicioNotificacion, fechaFinNotificacion)) {
//            System.out.println(getFechaInicio() + " " + getFechaFin());
            Controlador c = new Controlador();
            notificadas = new ArrayList<>();
            notificadas = c.getNotificacionesByFechaNotificacion(fechaInicioNotificacion, fechaFinNotificacion);
            numRegistros = "Número Registros Mostrados: " + notificadas.size();

            if (!notificadas.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRARON RESULTADOS.");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "FECHAS INCORRECTAS.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarNotificacionesPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
//        System.out.println(getFechaInicio() + " " + getFechaFin());
        if (validarFechas(fechaInicio, fechaFin)) {
//            System.out.println(getFechaInicio() + " " + getFechaFin());
            Controlador c = new Controlador();
            notificadas = new ArrayList<>();
            notificadas = c.getNotificacionesByDate(fechaInicio, fechaFin);
            numRegistros = "Número Registros Mostrados: " + notificadas.size();

            if (!notificadas.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRARON RESULTADOS.");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "FECHAS INCORRECTAS.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCasillero(ActionEvent ae) {
        if (notificada != null && notificada.getId() != null) {
            Controlador c = new Controlador();
            RenewalForm aux = c.findRenewalFormsByApplicationNumber(notificada.getSolicitud());
            if (aux.getId() != null) {
                notificada.setCasilleroSenadi(c.getCasilleroSenadi(aux.getOwnerId()) + "");
            }
        }
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

//        System.out.println("LLegando por aquí");
        if (notificada != null && notificada.getSolicitud() != null && !notificada.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = notificada.getSolicitud();
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

                                notificada.setFechaPresentacion(rf.getApplicationDate());
                                PaymentReceipt pr = c.getPaymentReceiptById(rf.getPaymentReceiptId());
                                if (pr.getId() != null) {
                                    notificada.setNoComprobantePresentSolic(pr.getVoucherNumber());
                                }
//                                renovacion.setCertificadoNo(c.getNextNumeroCertificado());
//
                                notificada.setSigno(ttp.getAlias());
                                notificada.setCasilleroSenadi(c.getCasilleroSenadi(rf.getOwnerId()) + "");
                                notificada.setIdRenewalForm(rf.getId());
//
                                if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//
                                    HallmarkForms hf = c.getHallmarkForm(rf.getDebugId());
//
                                    if (hf.getId() != null) {
                                        notificada.setDenominacion(hf.getDenomination());
                                        notificada.setRegistroNo(hf.getExpedient());
//
                                        if (notificada.getRegistroNo() != null && !notificada.getRegistroNo().trim().isEmpty()) {
                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByNumeroTitulo(notificada.getRegistroNo());
                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                notificada.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                            }
                                        }
                                    }
                                } else {
                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
//
                                        PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                        if (ps.getCodigoSolicitudSigno() != null) {
                                            notificada.setDenominacion(ps.getDenominacionSigno());
                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                notificada.setRegistroNo(titulo.getNumeroTitulo());
                                                notificada.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                            }
                                        }
                                    }
                                }

                                Person tit = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                if (tit.getId() != null) {
                                    notificada.setTitularActual(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "APPLICANT"));
                                    notificada.setIdentificacion(tit.getIdentificationNumber());
                                }

                                Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                if (apoder.getId() != null) {
                                    notificada.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "LAWYER"));
                                } else {
                                    apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                    if (apoder.getId() != null) {
                                        notificada.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "ATTORNEY"));
                                    }
                                }

                                notificada.setFechaElaboraNotificacion(new Date());

                                if (notificada.getRegistroNo() != null && !notificada.getRegistroNo().trim().isEmpty()) {
                                    if (notificada.getDenominacion() != null && !notificada.getDenominacion().trim().isEmpty()) {
                                        if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                            if (c.existsTituloCanceladoByTituloAndExpediente(notificada.getRegistroNo(), rf.getExpedient())) {
                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(notificada.getRegistroNo(), rf.getExpedient());
                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + notificada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    notificada = new Notificada();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    notificada.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + notificada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + notificada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    notificada = new Notificada();
                                                }
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                            }
                                        } else {
                                            if (c.existsTituloCanceladoByTituloAndDenominacion(notificada.getRegistroNo(), notificada.getDenominacion())) {
                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(notificada.getRegistroNo(), notificada.getDenominacion());
                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + notificada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    notificada = new Notificada();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    notificada.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + notificada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + notificada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    notificada = new Notificada();
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
        if (notificada != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + notificada.getSolicitud();
            FTPFiles files = new FTPFiles(130);
            archivos = new ArrayList<>();
            archivos = files.listarDirectorio("/var/www/html/solicitudes/media/files/renewal_forms/" + notificada.getIdRenewalForm());

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EXPEDIENTE CARGADO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL EXPEDIENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void viewErjafe(ActionEvent ae) {
        FacesMessage msg = null;
        notificada = (Notificada) notificadasDataTable.getRowData();
        if (notificada != null) {
            if (notificada.getRegistroNo() != null && !notificada.getRegistroNo().trim().isEmpty()) {
                if (notificada.getSolicitante() != null && !notificada.getSolicitante().trim().isEmpty()) {
                    if (notificada.getNomApodRepre() != null && !notificada.getNomApodRepre().trim().isEmpty()) {
                        if (validarFechas(notificada.getFechaRegistro(), notificada.getFechaRegistro())) {
                            if (validarFechas(notificada.getFechaElaboraNotificacion(), notificada.getFechaElaboraNotificacion())) {
                                if (notificada.getNotificacion() != null) {
                                    loginBean.setNotificadaFlotante(notificada);
                                    loginBean.setVarious(false);
                                    PrimeFaces.current().ajax().addCallbackParam("doit", true);

                                    System.out.println("envía notificada licencia descargar");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + notificada.getSolicitud());
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO EXISTE EL NÚMERO DE NOTIFICACION");
                                }
                            }

                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "LA FECHA DE REGISTRO NO ES CORRECTA");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO EXISTE UN APODERADO EN EL TRÁMITE");
                    }

                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "INGRESE UN SOLICITANTE VÁLIDO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO EXISTE EL NúMERO DE REGISTRO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelectedErjafe(ActionEvent ae) {
        FacesMessage msg = null;
        if (selectedNotificadas != null && !selectedNotificadas.isEmpty()) {
            boolean flag = true;
            String msj = "";
            for (int i = 0; i < selectedNotificadas.size(); i++) {
                Notificada notificadaaux = selectedNotificadas.get(i);
                System.out.println("notificada: " + notificadaaux.getSolicitud());
                if (notificadaaux.getRegistroNo() == null || notificadaaux.getRegistroNo().trim().isEmpty()) {
                    msj = "NO EXISTE EL NÚMERO DE REGISTRO PARA EL TRÁMITE " + notificadaaux.getSolicitud();
                    flag = false;
                    break;
                }
                if (notificadaaux.getSolicitante() == null || notificadaaux.getSolicitante().trim().isEmpty()) {
                    msj = "INGRESE UN SOLICITANTE VÁLIDO PARA EL TRÁMITE " + notificadaaux.getSolicitud();
                    flag = false;
                    break;
                }

                if (notificadaaux.getNomApodRepre() == null || notificadaaux.getNomApodRepre().trim().isEmpty()) {
                    msj = "NO EXISTE UN APODERADO EN EL TRÁMITE " + notificadaaux.getSolicitud();
                    flag = false;
                    break;
                }
                if (!validarFechas(notificadaaux.getFechaRegistro(), notificadaaux.getFechaRegistro())) {
                    msj = "LA FECHA DE REGISTRO NO ES CORRECTA EN EL TRÁMITE " + notificadaaux.getSolicitud();;
                    flag = false;
                    break;
                }
                if (!validarFechas(notificadaaux.getFechaElaboraNotificacion(), notificadaaux.getFechaElaboraNotificacion())) {
                    msj = "LA FECHA DE NOTIFICACIÓN NO ES CORRECTA EN EL TRÁMITE " + notificadaaux.getSolicitud();
                    flag = false;
                    break;
                }
                if (notificadaaux.getNotificacion() == null) {
                    msj = "NO EXISTE EL NÚMERO DE NOTIFICACION EN EL TRÁMITE " + notificadaaux.getSolicitud();;
                    flag = false;
                    break;
                }
            }
            if (flag) {
                loginBean.setNotificadasFlotantes(selectedNotificadas);
                loginBean.setVarious(true);
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                System.out.println("envía notificada licencia descargar");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE ENVIARON LAS NOTIFICACIONES A DESCARGA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", msj);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararPasarAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (selectedNotificadas.isEmpty()) {
            abandonosS = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        } else {
            abandonosS = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITES CARGADOS");
            PrimeFaces.current().ajax().addCallbackParam("abait", true);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararParaAbandonos() {
        FacesMessage msg = null;
        if (selectedNotificadas.isEmpty()) {
            abandonosS = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        } else {
            abandonosS = false;
            fechaPuestaAbandono = new Date();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITES CARGADOS");
            PrimeFaces.current().ajax().addCallbackParam("abait", true);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void pasarAAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedNotificadas.isEmpty()) {
            if (tipoAbandono != null && !tipoAbandono.trim().isEmpty()) {
                Controlador c = new Controlador();
                int n = 0;
                for (int i = 0; i < selectedNotificadas.size(); i++) {
                    System.out.println(selectedNotificadas.get(i).getSolicitud());
                    Notificada notaux = selectedNotificadas.get(i);
                    Abandono abandono = new Abandono();
                    abandono.setSolicitud(notaux.getSolicitud().toUpperCase());
                    abandono.setFechaPresentacion(notaux.getFechaPresentacion());
                    abandono.setFechaAbandono(new Date());
                    abandono.setNumeroAbandono(c.getNextNumeroAbandono(abandono.getFechaAbandono()));
                    abandono.setFechaElaboraNotificacion(notaux.getFechaElaboraNotificacion());
                    abandono.setNotificacion(notaux.getNotificacion());
                    abandono.setFechaNotificacion(notaux.getFechaNotifica());
                    abandono.setRegistro(notaux.getRegistroNo());
                    abandono.setFechaRegistro(notaux.getFechaRegistro());
                    abandono.setDenominacion(notaux.getDenominacion());
                    abandono.setSigno(notaux.getSigno());
                    //abandono.setTitularAnterior(notaux.getTitularAnterior());
                    abandono.setTitularActual(notaux.getTitularActual());
                    abandono.setApeApodRepre(notaux.getApeApodRepre());
                    abandono.setRo(notaux.getRo());
                    abandono.setCasilleroSenadi(notaux.getCasilleroSenadi());
                    abandono.setCasilleroJudicial(notaux.getCasilleroJudicial());
                    abandono.setResponsable(notaux.getResponsable());
                    abandono.setIdentificacion(notaux.getIdentificacion());
                    //abandono.setCertificado(notaux.getCertificado() + "");
                    abandono.setFechaCertificado(notaux.getFechaCertificado());
                    //abandono.setDomicilioTitularActual(notaux.getDomicilioTitularActual());
                    //abandono.setComprobante(notaux.getComprobante());
                    abandono.setCertificadoEmitido(notaux.isCertificadoEmitido());
                    abandono.setNotificacionEmitida(notaux.isNotificacionEmitida());
                    abandono.setCancelado(notaux.getCancelado());
                    abandono.setSolicitante(notaux.getSolicitante());

                    abandono.setTipoAbandono(tipoAbandono);
                    if (c.saveAbandono(abandono)) {
                        if (c.removeNotificada(notaux)) {
                            c.saveHistorial("ABANDONO", "NOTIFICADAS", abandono.getSolicitud(), "PASADO A", 0, loginBean.getLogin());
                            n++;
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL ABANDONO DEL TRÁMITE " + abandono.getSolicitud());
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    }
                }
                if (n > 0) {
                    loadNotificadas();
                    PrimeFaces.current().ajax().addCallbackParam("abit", true);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA PASADO SATISFACTORIAMENTE LOS NOTIFICADOS SELECCIONADOS A ABANDONOS");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR LOS ABANDONOS, CONSULTE AL ADMINISTRADOR DEL SISTEMA");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE UN TIPO DE ABANDONO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void paraAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedNotificadas.isEmpty()) {
            if (Operaciones.validarFecha(fechaPuestaAbandono)) {
                if (tipoAbandono != null && !tipoAbandono.trim().isEmpty()) {
                    Controlador c = new Controlador();
                    int n = 0;
                    for (int i = 0; i < selectedNotificadas.size(); i++) {
                        Notificada notaux = selectedNotificadas.get(i);
                        notaux.setTipoAbandono(tipoAbandono);
                        notaux.setFechaPuestaAbandono(fechaPuestaAbandono);
                        if (!c.updateNotificada(notaux)) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO ESTABLECER PARA ABANDONO A " + notaux.getSolicitud());
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                            return;
                        } else {
                            c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", notaux.getSolicitud(), "PARA ABANDONO", 0, loginBean.getLogin());
                            n++;
                        }
                    }
                    if (n > 0) {
                        loadNotificadas();
                        PrimeFaces.current().ajax().addCallbackParam("abit", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA ESTABLECIDO SATISFACTORIAMENTE LOS NOTIFICADOS SELECCIONADOS PARA ABANDONOS");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR LOS ABANDONOS, CONSULTE AL ADMINISTRADOR DEL SISTEMA");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE UN TIPO DE ABANDONO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UNA FECHA DE ABANDONO VÁLIDA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getTooltipAbandono(Notificada noti) {
        if (noti.getFechaPuestaAbandono() == null || noti.getTipoAbandono() == null) {
            return "";
        }

        LocalDate fechaLimite = null;
        switch (noti.getTipoAbandono()) {
            case "ERJAFE":
                fechaLimite = noti.getFechaPuestaAbandono().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(60);
                break;
            case "REGLAMENTO":
                fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(noti.getFechaPuestaAbandono(), 10);
                break;
            case "COA":
                fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(noti.getFechaPuestaAbandono(), 10);
                break;
            default:
                break;
        }

        long faltan = ChronoUnit.DAYS.between(LocalDate.now(), fechaLimite);

        if (faltan >= 0) {
            return "Faltan " + faltan + " días para pasar el trámite " + noti.getSolicitud() + " a abandono";
        } else {
            return "Ya venció hace " + Math.abs(faltan) + " días";
        }
    }

    /**
     * @return the notificadas
     */
    public List<Notificada> getNotificadas() {
        return notificadas;
    }

    /**
     * @param notificadas the notificadas to set
     */
    public void setNotificadas(List<Notificada> notificadas) {
        this.notificadas = notificadas;
    }

    /**
     * @return the notificadasFiltradas
     */
    public List<Notificada> getNotificadasFiltradas() {
        return notificadasFiltradas;
    }

    /**
     * @param notificadasFiltradas the notificadasFiltradas to set
     */
    public void setNotificadasFiltradas(List<Notificada> notificadasFiltradas) {
        this.notificadasFiltradas = notificadasFiltradas;
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
     * @return the notificada
     */
    public Notificada getNotificada() {
        return notificada;
    }

    /**
     * @param notificada the notificada to set
     */
    public void setNotificada(Notificada notificada) {
        this.notificada = notificada;
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
     * @return the notificadasDataTable
     */
    public UIData getNotificadasDataTable() {
        return notificadasDataTable;
    }

    /**
     * @param notificadasDataTable the notificadasDataTable to set
     */
    public void setNotificadasDataTable(UIData notificadasDataTable) {
        this.notificadasDataTable = notificadasDataTable;
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
     * @return the selectedNotificadas
     */
    public List<Notificada> getSelectedNotificadas() {
        return selectedNotificadas;
    }

    /**
     * @param selectedNotificadas the selectedNotificadas to set
     */
    public void setSelectedNotificadas(List<Notificada> selectedNotificadas) {
        this.selectedNotificadas = selectedNotificadas;
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
     * @return the roshow
     */
    public String getRoshow() {
        return roshow;
    }

    /**
     * @param roshow the roshow to set
     */
    public void setRoshow(String roshow) {
        this.roshow = roshow;
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
     * @return the fechaInicioNotificacion
     */
    public Date getFechaInicioNotificacion() {
        return fechaInicioNotificacion;
    }

    /**
     * @param fechaInicioNotificacion the fechaInicioNotificacion to set
     */
    public void setFechaInicioNotificacion(Date fechaInicioNotificacion) {
        this.fechaInicioNotificacion = fechaInicioNotificacion;
    }

    /**
     * @return the fechaFinNotificacion
     */
    public Date getFechaFinNotificacion() {
        return fechaFinNotificacion;
    }

    /**
     * @param fechaFinNotificacion the fechaFinNotificacion to set
     */
    public void setFechaFinNotificacion(Date fechaFinNotificacion) {
        this.fechaFinNotificacion = fechaFinNotificacion;
    }

    /**
     * @return the abandonosS
     */
    public boolean isAbandonosS() {
        return abandonosS;
    }

    /**
     * @param abandonosS the abandonosS to set
     */
    public void setAbandonosS(boolean abandonosS) {
        this.abandonosS = abandonosS;
    }

    /**
     * @return the fechaPuestaAbandono
     */
    public Date getFechaPuestaAbandono() {
        return fechaPuestaAbandono;
    }

    /**
     * @param fechaPuestaAbandono the fechaPuestaAbandono to set
     */
    public void setFechaPuestaAbandono(Date fechaPuestaAbandono) {
        this.fechaPuestaAbandono = fechaPuestaAbandono;
    }

    /**
     * @return the tipoAbandono
     */
    public String getTipoAbandono() {
        return tipoAbandono;
    }

    /**
     * @param tipoAbandono the tipoAbandono to set
     */
    public void setTipoAbandono(String tipoAbandono) {
        this.tipoAbandono = tipoAbandono;
    }
}
