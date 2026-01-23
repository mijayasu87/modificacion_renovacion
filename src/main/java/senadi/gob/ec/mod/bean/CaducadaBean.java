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
import senadi.gob.ec.mod.model.UploadNotificacion;
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
@ManagedBean(name = "caducadaBean")
@ViewScoped
public class CaducadaBean implements Serializable {

    private List<Caducada> caducadas;
    private List<Caducada> caducadasFiltradas;
    private List<Caducada> selectedCaducadas;

    private String texto;

    private Caducada caducada;

    private UIData caducadasDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;

    private String numRegistros;

    private String exportName;

    private String historial;

    private LoginBean loginBean;
    private boolean usuarioConsulta;

    private Date fechaInicio;
    private Date fechaFin;

    private List<String> archivos;

    private boolean edicion;
    private String estadoTemp;

    public CaducadaBean() {
        init();
    }

    private void init() {
        Controlador c = new Controlador();
        caducadas = c.getCaducadas();
        caducada = new Caducada();
        dialogTitle = "NUEVO REGISTRO CADUCADA-NEGADA";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Registro?";
        numRegistros = "Número Registros Mostrados: " + caducadas.size();
        exportName = "cad_neg_" + Operaciones.formatDate(new Date());
        loginBean = getLogin();
        usuarioConsulta = !loginBean.isUsuarioConsulta();
    }

    public LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginBean loginBea = (LoginBean) session.getAttribute("loginBean");
        return loginBea;
    }

    public void loadNuevoRegistro(ActionEvent ae) {
        dialogTitle = "NUEVO REGISTRO CADUCADA-NEGADA";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Registro?";
        edicion = false;
        caducada = new Caducada();
        Controlador c = new Controlador();
        caducada.setResolucion(c.getNextResCaducadaNumber(new Date()) + "");
        if (caducada != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void buscarCaducadas(ActionEvent ae) {
        System.out.println("CADUCADA-NEGADA - SE BUSCA: " + getTexto());
        FacesMessage msg = null;
        if (getTexto().contains("'")) {

            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO INGRESE CARACTERES ESPECIALES");

        } else {
            loadCaducadas();
            if (caducadas.size() > 0) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRADO RESULTADOS.");
            }

        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void loadCaducadas() {
        Controlador c = new Controlador();
        caducadas = new ArrayList<>();
        caducadas = c.getCaducadasByCriteria(texto);
        numRegistros = "Número Registros Mostrados: " + caducadas.size();
    }

    public void guardarRegistro(ActionEvent ae) {
        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        if (caducada != null) {
            Controlador c = new Controlador();
            if (caducada.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    if (estadoTemp.equals("RENOVACIONES")) {
                        Renovacion renova = new Renovacion();
                        renova.setEstado("");
                        renova.setSolicitudSenadi(caducada.getSolicitudSenadi().toUpperCase());
                        renova.setFechaPresentacion(caducada.getFechaSolicitud());
                        renova.setFechaCertificado(new Date());
                        renova.setCertificadoNo(c.getNextNumeroCertificado(renova.getFechaCertificado()));
                        renova.setTituloResolucion(caducada.getResolucion());
                        renova.setRegistroNo(caducada.getRegistroNo());
                        renova.setFechaRegistro(caducada.getFechaRegistro());
                        renova.setFechaVenceRegistro(caducada.getFechaVencimiento());
                        renova.setDenominacion(caducada.getDenominacion());
                        renova.setLema("");
                        renova.setSigno(caducada.getSigno());
                        renova.setClase("");
                        renova.setTitularActual(caducada.getSolicitante());
                        renova.setTacNJ("");
                        renova.setNacTitularAc("");
                        renova.setAbogadoPatrocinadorApeApoRepre(caducada.getAbogadoPatrocinador());
                        renova.setCasilleroSenadi(caducada.getCasilleroNo());
                        renova.setResponsable(caducada.getResponsable());
                        renova.setIdentificacion(caducada.getIdentificacion());
                        renova.setCancelado(caducada.getCancelado());

                        if (c.validarExistenciaRenovacion(renova.getSolicitudSenadi())) {
//                                context.addCallbackParam("saved", false);
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en renovaciones con el mismo número de solicitud");
                        } else {
                            if (c.saveRenovacion(renova)) {

                                c = new Controlador();
                                List<Caducada> cadus = c.getCaducadasBySolSenadi(renova.getSolicitudSenadi());
                                int cont = 0;
                                for (int i = 0; i < cadus.size(); i++) {
                                    if (c.removeCaducada(cadus.get(i))) {
                                        cont++;
                                    }
                                }
                                if (cadus.size() == cont) {
                                    c.saveHistorial("RENOVACIONES", "CADUCADAS", renova.getSolicitudSenadi(), "PASADO A", 0, getLoginBean().getLogin());
                                    init();
                                    //context.addCallbackParam("saved", true);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + renova.getSolicitudSenadi() + " de caducada a renovación");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA CADUCADA");
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
                        notificada.setSolicitud(caducada.getSolicitudSenadi().toUpperCase());
                        notificada.setFechaPresentacion(caducada.getFechaSolicitud());
                        notificada.setNoComprobantePresentSolic("");
                        notificada.setNoComprobanteEmisionCert("");
//                        notificada.setTotalFoliosExpediente(desistida.getTotalFoliosExpediente());
                        notificada.setFechaElaboraNotificacion(new Date());
                        notificada.setNotificacion(c.getNextNumeroNotificacion(notificada.getFechaElaboraNotificacion()));
                        notificada.setFechaCertificado(caducada.getFechaSolicitud());
                        notificada.setTituloResolucion(caducada.getResolucion());
                        notificada.setRegistroNo(caducada.getRegistroNo());
                        notificada.setFechaRegistro(caducada.getFechaRegistro());
                        notificada.setFechaVenceRegistro(caducada.getFechaVencimiento());
                        notificada.setDenominacion(caducada.getDenominacion());
                        notificada.setLema("");
                        notificada.setSigno(caducada.getSigno());
                        notificada.setClase("");
                        notificada.setTitularActual(caducada.getSolicitante());
                        notificada.setTacNJ("");
                        notificada.setNacTitularAc("");
                        notificada.setDomicilioTitularAc("");
                        notificada.setApeApodRepre(caducada.getAbogadoPatrocinador());
                        notificada.setFechaNotifica(caducada.getFechaNotificacion());
                        notificada.setCasilleroSenadi(caducada.getCasilleroNo());
                        notificada.setFechaProvidencia(caducada.getFechaProvidencia());
                        notificada.setFechaNotificaPro(new Date());
                        notificada.setResponsable(caducada.getResponsable());
                        notificada.setIdentificacion(caducada.getIdentificacion());
                        notificada.setCancelado(caducada.getCancelado());

                        if (c.validarExistenciaNotificada(notificada.getSolicitud())) {
//                                context.addCallbackParam("saved", false);
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en notificadas con el mismo número de solicitud");
                        } else {
                            if (c.saveNotificada(notificada)) {

                                c = new Controlador();
                                List<Caducada> cadus = c.getCaducadasBySolSenadi(notificada.getSolicitud());
                                int cont = 0;
                                for (int i = 0; i < cadus.size(); i++) {
                                    if (c.removeCaducada(cadus.get(i))) {
                                        cont++;
                                    }
                                }
                                if (cadus.size() == cont) {
                                    c.saveHistorial("NOTIFICADAS", "CADUCADAS", notificada.getSolicitud(), "PASADO A", 0, getLoginBean().getLogin());

                                    init();
                                    //context.addCallbackParam("saved", true);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + notificada.getSolicitud() + " de caducada a notificada");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA CADUCADA");
                                }
                            } else {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                            }
                        }
                    } else {
                        Desistida desist = new Desistida();
                        desist.setEstado("DESISTE");
                        desist.setSolicitudSenadi(caducada.getSolicitudSenadi().toUpperCase());
                        desist.setIepi(caducada.getSolicitudSenadi());
                        desist.setSolicitudNo("");
                        desist.setFechaPresentacion(caducada.getFechaSolicitud());
                        desist.setCertificadoNo(caducada.getCertificaNo());
                        desist.setFechaCertificado(caducada.getFechaProvidencia());
                        desist.setRegistroNo(caducada.getRegistroNo());
                        desist.setFechaRegistro(caducada.getFechaRegistro());
                        desist.setFechaVenceRegistro(caducada.getFechaVencimiento());
                        desist.setDenominacion(caducada.getDenominacion());
                        desist.setSigno(caducada.getSigno());
                        desist.setTitularActual(caducada.getSolicitante());
                        desist.setAr("");
                        desist.setNj("");
                        desist.setTitApodRepre("");
                        desist.setApeApodRepre(caducada.getAbogadoPatrocinador());
                        desist.setNomApodRepre("");
                        desist.setFechaElaboraNotificacion(new Date());
                        desist.setFechaNotifica(caducada.getFechaNotificacion());
                        desist.setCasilleroSenadi(caducada.getCasilleroNo());
                        desist.setRo("");
                        desist.setResponsable(caducada.getResponsable());
                        desist.setFechaDesistida(new Date());
                        desist.setIdentificacion(caducada.getIdentificacion());
                        desist.setCancelado(caducada.getCancelado());

                        if (c.validarExistenciaDesistida(desist.getSolicitudSenadi())) {
                            //context.addCallbackParam("saved", false);
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en desistidas con el mismo número de solicitud");
                        } else {
                            if (c.saveDesistida(desist)) {

                                c = new Controlador();
                                List<Caducada> cadus = c.getCaducadasBySolSenadi(desist.getSolicitudSenadi());
                                int cont = 0;
                                for (int i = 0; i < cadus.size(); i++) {
                                    if (c.removeCaducada(cadus.get(i))) {
                                        cont++;
                                    }
                                }
                                if (cadus.size() == cont) {
                                    c.saveHistorial("DESISTIDAS", "CADUCADAS", desist.getSolicitudSenadi(), "PASADO A", 0, loginBean.getLogin());
                                    init();
                                    //context.addCallbackParam("saved", true);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + desist.getSolicitudSenadi() + " de caducada a desistida");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                } else {
//                                        context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA CADUCADA");
                                }
                            } else {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                            }
                        }
                    }
                } else {
                    if (c.validarExistsCaducada(caducada)) {
                        PrimeFaces.current().ajax().addCallbackParam("saved", false);
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD.");
                    } else {
                        caducada.setSolicitudSenadi(caducada.getSolicitudSenadi().toUpperCase());
                        if (c.updateCaducada(caducada)) {
                            //loadCaducadas();
                            c.saveHistorial("CADUCADA", "CADUCADA", caducada.getSolicitudSenadi(), "EDITADO", 0, loginBean.getLogin());
                            init();
                            System.out.println("Caducada-Negada " + (caducada.getSolicitudSenadi() != null ? caducada.getSolicitudSenadi() : "") + " editado");
                            //context.addCallbackParam("saved", true);
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "CADUCADA-NEGADA EDITADO CORRECTAMENTE.");
                        } else {
//                        context.addCallbackParam("saved", false);
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ERROR AL EDITAR CADUCADA-NEGADA.");
                        }
                    }
                }
            } else {
                if (c.validarExistenciaCaducada(caducada.getSolicitudSenadi())) {
//                    context.addCallbackParam("saved", false);
                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD.");
                } else {
                    ModificacionApp mapp = c.getModificacionApp(caducada.getSolicitudSenadi());
                    if (mapp.getId() != null) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                    } else {
                        boolean habilitado = true;
                        if (caducada.getDenominacion() != null && !caducada.getDenominacion().trim().isEmpty()
                                && caducada.getRegistroNo() != null && !caducada.getRegistroNo().trim().isEmpty()) {
                            if (c.existsTituloCanceladoByTituloAndDenominacion(caducada.getRegistroNo(), caducada.getDenominacion())) {
                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(caducada.getRegistroNo(), caducada.getDenominacion());
                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistroNo() + " CON DENOMINACIÓN '"
                                            + caducada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    caducada = new Caducada();
                                    habilitado = false;
                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                    caducada.setCancelado(titca.getTipoCancelacion());
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistroNo() + " CON DENOMINACIÓN '"
                                            + caducada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistroNo() + " CON DENOMINACIÓN '"
                                            + caducada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    caducada = new Caducada();
                                    habilitado = false;
                                }
                            } else {
                                habilitado = true;
                            }
                        }

                        if (habilitado) {
                            caducada.setSolicitudSenadi(caducada.getSolicitudSenadi().toUpperCase());
                            caducada.setResolucion(c.getNextResCaducadaNumber(caducada.getFechaProvidencia()) + "");
                            if (c.saveCaducada(caducada)) {
                                //loadCaducadas();
                                c.saveModificacionApp(caducada.getDenominacion(), caducada.getRegistroNo(), caducada.getSolicitudSenadi(), "RENOVACION", loginBean.getNombre());
                                c.saveHistorial("CADUCADA", "CADUCADA", caducada.getSolicitudSenadi(), "NUEVO", 0, loginBean.getLogin());
                                init();

                                System.out.println("Nuevo Caducada-Negada " + (caducada.getSolicitudSenadi() != null ? caducada.getSolicitudSenadi() : "") + " guardado");
                                //context.addCallbackParam("saved", true);
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "GUARDADO", "CADUCADA-NEGADA GUARDADO CORRECTAMENTE.");
                            } else {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ERROR AL GUARDAR.");
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

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

        if (caducada != null && caducada.getSolicitudSenadi() != null && !caducada.getSolicitudSenadi().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = caducada.getSolicitudSenadi();
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

                                caducada.setFechaSolicitud(rf.getApplicationDate());

                                caducada.setSigno(ttp.getAlias());
                                caducada.setCasilleroNo(c.getCasilleroSenadi(rf.getOwnerId()) + "");
                                caducada.setIdRenewalForm(rf.getId());

                                if (rf.getDebugId() != null && rf.getDebugId() != 0) {

                                    HallmarkForms hf = c.getHallmarkForm(rf.getDebugId());

                                    if (hf.getId() != null) {
                                        caducada.setDenominacion(hf.getDenomination());
                                        caducada.setRegistroNo(hf.getExpedient());

                                        if (caducada.getRegistroNo() != null && !caducada.getRegistroNo().trim().isEmpty()) {
                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByNumeroTitulo(caducada.getRegistroNo());
                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                caducada.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                            }
                                        }
                                    }
                                } else {
                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                        PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                        if (ps.getCodigoSolicitudSigno() != null) {
                                            caducada.setDenominacion(ps.getDenominacionSigno());
                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                caducada.setRegistroNo(titulo.getNumeroTitulo());
                                                caducada.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                            }
                                        }
                                    }
                                }

                                Person tit = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                if (tit.getId() != null) {
                                    caducada.setSolicitante(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "APPLICANT"));
                                    caducada.setIdentificacion(tit.getIdentificationNumber());
                                }

                                Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                if (apoder.getId() != null) {
                                    caducada.setAbogadoPatrocinador(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "LAWYER"));
                                } else {
                                    apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                    if (apoder.getId() != null) {
                                        caducada.setAbogadoPatrocinador(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "ATTORNEY"));
                                    }
                                }

                                if (caducada.getRegistroNo() != null && !caducada.getRegistroNo().trim().isEmpty()) {
                                    if (caducada.getDenominacion() != null && !caducada.getDenominacion().trim().isEmpty()) {
                                        if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                            if (c.existsTituloCanceladoByTituloAndExpediente(caducada.getRegistroNo(), rf.getExpedient())) {
                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(caducada.getRegistroNo(), rf.getExpedient());
                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + caducada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    caducada = new Caducada();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    caducada.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + caducada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + caducada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    caducada = new Caducada();
                                                }
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                            }
                                        } else {
                                            if (c.existsTituloCanceladoByTituloAndDenominacion(caducada.getRegistroNo(), caducada.getDenominacion())) {
                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(caducada.getRegistroNo(), caducada.getDenominacion());
                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + caducada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    caducada = new Caducada();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    caducada.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + caducada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistroNo() + " CON DENOMINACIÓN '"
                                                            + caducada.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    caducada = new Caducada();
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

    public void prepararEditar(ActionEvent ae) {
        dialogTitle = "EDITAR CADUCADA-NEGADA";
        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        caducada = (Caducada) caducadasDataTable.getRowData();
        if (caducada != null) {
            mensajeConfirmacion = "¿Seguro de editar Caducada-Negada: " + caducada.getSolicitudSenadi() + "?";

            Controlador c = new Controlador();
            RenewalForm rf = c.findRenewalFormsByApplicationNumber(caducada.getSolicitudSenadi());
            if (rf.getId() != null) {
                caducada.setIdRenewalForm(rf.getId());
            } else {
                caducada.setIdRenewalForm(null);
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CADUCADA-NEGADA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CADUCADA-NEGADA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarCaducada(ActionEvent ae) {
        FacesMessage msg = null;
        caducada = (Caducada) caducadasDataTable.getRowData();
        if (caducada != null) {
            Controlador c = new Controlador();
            if (c.removeCaducada(caducada)) {
                c.saveHistorial("CADUCADA", "CADUCADA", caducada.getSolicitudSenadi(), "ELIMINADO", 0, loginBean.getLogin());
                init();
                System.out.println("Caducada " + caducada.getSolicitudSenadi() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CADUCADA " + caducada.getSolicitudSenadi() + " ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR CADUCADA-NEGADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CADUCADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        caducada = (Caducada) caducadasDataTable.getRowData();
        if (caducada != null) {
            dialogTitle = "SEGUIMIENTO " + caducada.getSolicitudSenadi();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(caducada.getSolicitudSenadi());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: CADUCADA - NEGADA";
            }
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (caducada != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + caducada.getSolicitudSenadi();
            FTPFiles files = new FTPFiles(130);
            archivos = new ArrayList<>();
            archivos = files.listarDirectorio("/var/www/html/solicitudes/media/files/renewal_forms/" + caducada.getIdRenewalForm());

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EXPEDIENTE CARGADO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL EXPEDIENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public void validarCaducadaDocumento(Caducada caduc) {
        FacesMessage msg = null;
        if (caduc != null) {
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(caduc.getSolicitudSenadi(), true);
            if (!uploads.isEmpty()) {
                UploadNotificacion unaux = uploads.get(0);
                String rutaNotificacionCasillero = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                System.out.println("rutacertcad: " + rutaNotificacionCasillero);
                PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGADO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGÚN CERTIFICADO DEL TRÁMITE " + caduc.getSolicitud());
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCaducadasPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
//        System.out.println(getFechaInicio() + " " + getFechaFin());
        if (validarFechas()) {
            //System.out.println(getFechaInicio() + " " + getFechaFin());
            Controlador c = new Controlador();
            caducadas = new ArrayList<>();
            caducadas = c.getCaducadasByDate(fechaInicio, fechaFin);
            numRegistros = "Número Registros Mostrados: " + caducadas.size();

            if (caducadas.size() > 0) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE HAN ENCONTRARON RESULTADOS.");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "FECHAS INCORRECTAS.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void viewCaducada(ActionEvent ae) {
        FacesMessage msg = null;
        caducada = (Caducada) caducadasDataTable.getRowData();
        if (caducada != null) {
            Controlador c = new Controlador();
            if (c.validarSecretarioActivo()) {
                if (caducada.getAntDes() != null && !caducada.getAntDes().trim().isEmpty()) {
                    System.out.println("Descargando Caducada " + caducada.getResolucion());
                    loginBean.setVarious(false);
                    loginBean.setCaducada(caducada);
                    loginBean.setOther(false);
                    loginBean.setCaducadas(new ArrayList<Caducada>());

                    PrimeFaces.current().ajax().addCallbackParam("doit", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", "cadinforme");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CADUCADA", "DESCARGANDO CADUCADA");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DE SELECCIONAR UNA OPCIÓN ENTRE ANTES Y DESPUÉS DEL TRÁMITE: " + caducada.getSolicitudSenadi());
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void viewCaducadaOther(ActionEvent ae) {
        FacesMessage msg = null;
        caducada = (Caducada) caducadasDataTable.getRowData();
        if (caducada != null) {

            Controlador c = new Controlador();
            if (c.validarSecretarioActivo()) {
                if (caducada.getAntDes() != null && !caducada.getAntDes().trim().isEmpty()) {

                    System.out.println("Descargando Caducada Other " + caducada.getResolucion());
                    loginBean.setVarious(false);
                    loginBean.setCaducada(caducada);
                    loginBean.setOther(true);
                    loginBean.setCaducadas(new ArrayList<Caducada>());
                    PrimeFaces.current().ajax().addCallbackParam("doit", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", "cadinforme");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CADUCADA", "DESCARGANDO CADUCADA");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DE SELECCIONAR UNA OPCIÓN ENTRE ANTES Y DESPUÉS DEL TRÁMITE: " + caducada.getSolicitudSenadi());
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedCaducadas.isEmpty()) {
            Controlador c = new Controlador();
            if (c.validarSecretarioActivo()) {
                if (c.validarSecretarioActivo()) {
                    boolean aviso = true;
                    String tramaux = "";
                    for (int i = 0; i < selectedCaducadas.size(); i++) {
                        Caducada aux = selectedCaducadas.get(i);
                        if (aux.getAntDes() == null || aux.getAntDes().trim().isEmpty()) {
                            tramaux = aux.getSolicitudSenadi() + " NO POSEE ANTES-DESPUÉS";
                            aviso = false;
                            break;
                        }
                    }
                    if (aviso) {
                        System.out.println("Descargando Múltiples Caducadas...");
                        loginBean.setCaducadas(selectedCaducadas);
                        loginBean.setVarious(true);
                        loginBean.setOther(false);
                        PrimeFaces.current().ajax().addCallbackParam("doit", true);                        
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "VISUALIZANDO REPORTE");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE " + tramaux);
                    }

                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
            }

        } else {
            PrimeFaces.current().ajax().addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "DEBE SELECCIONAR AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelectedOther(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedCaducadas.isEmpty()) {
            Controlador c = new Controlador();
            if (c.validarSecretarioActivo()) {
                if (c.validarSecretarioActivo()) {
                    boolean aviso = true;
                    String tramaux = "";
                    for (int i = 0; i < selectedCaducadas.size(); i++) {
                        Caducada aux = selectedCaducadas.get(i);
                        if (aux.getAntDes() == null || aux.getAntDes().trim().isEmpty()) {
                            tramaux = aux.getSolicitudSenadi();
                            aviso = false;
                            break;
                        }
                    }
                    if (aviso) {
                        System.out.println("Descargando Múltiples Caducadas Other...");
                        loginBean.setCaducadas(selectedCaducadas);
                        loginBean.setVarious(true);
                        loginBean.setOther(true);
                        PrimeFaces.current().ajax().addCallbackParam("doit", true);                        
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "VISUALIZANDO REPORTE");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE " + tramaux + " NO POSEE ANTES - DESPUÉS");
                    }

                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
            }

        } else {
            PrimeFaces.current().ajax().addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "DEBE SELECCIONAR AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void downloadSelectedNegada(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedCaducadas.isEmpty()) {
            Controlador c = new Controlador();
            if (c.validarSecretarioActivo()) {
                if (c.validarSecretarioActivo()) {
                    boolean aviso = true;
                    String tramaux = "";
                    for (int i = 0; i < selectedCaducadas.size(); i++) {
                        Caducada aux = selectedCaducadas.get(i);
                        if (aux.getAntDes() == null || aux.getAntDes().trim().isEmpty()) {
                            tramaux = aux.getSolicitudSenadi();
                            aviso = false;
                            break;
                        }
                    }
                    if (aviso) {
                        System.out.println("Descargando Múltiples Caducadas Negada...");
                        loginBean.setCaducadas(selectedCaducadas);
                        loginBean.setVarious(true);
                        loginBean.setOther(true);
                        PrimeFaces.current().ajax().addCallbackParam("doit", true);
//                        PrimeFaces.current().ajax().addCallbackParam("view", "reportes");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "VISUALIZANDO REPORTE");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE " + tramaux + " NO POSEE ANTES - DESPUÉS");
                    }

                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTE UN SECRETARIO ACTIVO");
            }

        } else {            
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "VACÍO", "DEBE SELECCIONAR AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCasillero(ActionEvent ae) {
        if (caducada != null && caducada.getId() != null) {
            Controlador c = new Controlador();
            RenewalForm aux = c.findRenewalFormsByApplicationNumber(caducada.getSolicitudSenadi());
            if (aux.getId() != null) {
                caducada.setCasilleroNo(c.getCasilleroSenadi(aux.getOwnerId()) + "");
            }
        }
    }
    
    public void viewCaducadaN(ActionEvent ae){
        System.out.println("llego por aquí");
        FacesMessage msg = null;
        caducada = (Caducada) caducadasDataTable.getRowData();      
        if (caducada != null && caducada.getSolicitudSenadi() != null) {
            Controlador c = new Controlador();
//            caducada = c.getCaducadaById(caducada.getId());
            if (caducada.getRegistroNo() != null && !caducada.getRegistroNo().trim().isEmpty()) {
                System.out.println("Descargando Caducada: " + caducada.getSolicitudSenadi());

                
                if (caducada.getSolicitante() == null || caducada.getSolicitante().trim().isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO HAY UN SOLICITANTE VÁLIDO");
                } else if (!c.validarDelegadoActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (caducada.getObservacion()== null || caducada.getObservacion().trim().isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO ESTÁ INGRESADO EL MOTIVO ESPECÍFICO (Observación)");
                } else {
                    //System.out.println("id: "+caducada.getId()+", tramite: "+caducada.getSolicitudSenadi()+", fecha_solicitud: "+caducada.getFechaSolicitud());
                    loginBean.setCaducada(caducada);
                    loginBean.setVarious(false);
                    PrimeFaces.current().ajax().addCallbackParam("doit", true);

                    System.out.println("envía caducada transferencia descargar");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + caducada.getSolicitud());
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL REGISTRO SELECCIONADO NO TIENE NÚMERO DE REGISTRO ASIGNADO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR LOS DATOS DEL REGISTRO SELECCIONADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
     * @return the caducadas
     */
    public List<Caducada> getCaducadas() {
        return caducadas;
    }

    /**
     * @param caducadas the caducadas to set
     */
    public void setCaducadas(List<Caducada> caducadas) {
        this.caducadas = caducadas;
    }

    /**
     * @return the caducadasFiltradas
     */
    public List<Caducada> getCaducadasFiltradas() {
        return caducadasFiltradas;
    }

    /**
     * @param caducadasFiltradas the caducadasFiltradas to set
     */
    public void setCaducadasFiltradas(List<Caducada> caducadasFiltradas) {
        this.caducadasFiltradas = caducadasFiltradas;
    }

    /**
     * @return the caducada
     */
    public Caducada getCaducada() {
        return caducada;
    }

    /**
     * @param caducada the caducada to set
     */
    public void setCaducada(Caducada caducada) {
        this.caducada = caducada;
    }

    /**
     * @return the caducadasDataTable
     */
    public UIData getCaducadasDataTable() {
        return caducadasDataTable;
    }

    /**
     * @param caducadasDataTable the caducadasDataTable to set
     */
    public void setCaducadasDataTable(UIData caducadasDataTable) {
        this.caducadasDataTable = caducadasDataTable;
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
     * @return the selectedCaducadas
     */
    public List<Caducada> getSelectedCaducadas() {
        return selectedCaducadas;
    }

    /**
     * @param selectedCaducadas the selectedCaducadas to set
     */
    public void setSelectedCaducadas(List<Caducada> selectedCaducadas) {
        this.selectedCaducadas = selectedCaducadas;
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
}
