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
import senadi.gob.ec.mod.daop.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.mod.daop.PpdiTituloSignoDistintivo;
import senadi.gob.ec.mod.model.Abandono;
import senadi.gob.ec.mod.model.Documento;
import senadi.gob.ec.mod.model.Historial;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.iepdep.HallmarkForms;
import senadi.gob.ec.mod.model.iepform.ModificacionApp;
import senadi.gob.ec.mod.model.iepform.Person;
import senadi.gob.ec.mod.model.iepform.RenewalForm;
import senadi.gob.ec.mod.model.iepform.Types;
import senadi.gob.ec.mod.model.transf.TituloCancelado;
import senadi.gob.ec.mod.ucc.Controlador;
import senadi.gob.ec.mod.ucc.Operaciones;
import senadi.gob.ec.mod.ucc.Reusable;

/**
 *
 * @author michael
 */
@ManagedBean(name = "abandonoBean")
@ViewScoped
public class AbandonoBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<Abandono> abandonos;
    private List<Abandono> abandonosFiltradas;

    private UIData abandonosDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private Abandono abandono;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<String> roRazones;
    private String razon;

    private boolean roselectable;

    private List<Abandono> selectedAbandonos;

//    private List<Rooptions> roos;
    private UIData roDataTable;

    private String roChoose;

    private boolean separado;

    private String roshow;

    private List<Documento> archivos;

    private String rutaNotificacionCasillero;

    private boolean paraEnviar;

    public AbandonoBean() {
        loadAbandonos();
    }

    private void loadAbandonos() {
        Controlador c = new Controlador();
        abandonos = c.getAbandonos();
        numRegistros = "Número Registros Mostrados: " + abandonos.size();
        exportName = "abandono_" + Operaciones.formatDate(new Date());
        loginBean = getLogin();
        //roRazones = Operaciones.getRazonesNotificar();
        razon = "";
        selectedAbandonos = new ArrayList<>();
    }

    public LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginBean loginBea = (LoginBean) session.getAttribute("loginBean");
        return loginBea;
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

    public void buscarAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            abandonos = c.getAbandonoByCriteria(criterio.trim());
            numRegistros = "Número Registros Mostrados: " + abandonos.size();
            if (abandonos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarAbandonosPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            abandonos = c.getAbandonosByFecha(fechaInicio, fechaFin);
            numRegistros = "Número Registros Mostrados: " + abandonos.size();
            if (abandonos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO ABANDONO";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Abandono?";
        abandono = new Abandono();
//        Controlador c = new Controlador();
        //abandono.setNotificacion(c.getNextNumeroAbandono(new Date())); //<--- revisar el next 
//        abandono.setResponsable(loginBean.getUsuario().getAlias());d
        abandono.setFechaAbandono(new Date());
//        roos = new ArrayList<>();

        edicion = false;
        roselectable = false;
        razon = "";
        if (abandono != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }
//

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

        if (abandono != null && abandono.getSolicitud() != null && !abandono.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = abandono.getSolicitud();
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

                                abandono.setFechaPresentacion(rf.getApplicationDate());

                                abandono.setSigno(ttp.getAlias());
                                abandono.setCasilleroSenadi(c.getCasilleroSenadi(rf.getOwnerId()) + "");
                                abandono.setIdRenewalForm(rf.getId());

                                if (rf.getDebugId() != null && rf.getDebugId() != 0) {

                                    HallmarkForms hf = c.getHallmarkForm(rf.getDebugId());

                                    if (hf.getId() != null) {
                                        abandono.setDenominacion(hf.getDenomination());
                                        abandono.setRegistro(hf.getExpedient());

                                        if (abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByNumeroTitulo(abandono.getRegistro());
                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                abandono.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                            }
                                        }
                                    }
                                } else {
                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                        PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                        if (ps.getCodigoSolicitudSigno() != null) {
                                            abandono.setDenominacion(ps.getDenominacionSigno());
                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                abandono.setRegistro(titulo.getNumeroTitulo());
                                                abandono.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                            }
                                        }
                                    }
                                }

                                Person tit = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                if (tit.getId() != null) {
                                    abandono.setSolicitante(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "APPLICANT"));
                                    abandono.setIdentificacion(tit.getIdentificationNumber());
                                }

                                Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                if (apoder.getId() != null) {
                                    abandono.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "LAWYER"));
                                } else {
                                    apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                    if (apoder.getId() != null) {
                                        abandono.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "ATTORNEY"));
                                    }
                                }

                                if (abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                                    if (abandono.getDenominacion() != null && !abandono.getDenominacion().trim().isEmpty()) {
                                        if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                            if (c.existsTituloCanceladoByTituloAndExpediente(abandono.getRegistro(), rf.getExpedient())) {
                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(abandono.getRegistro(), rf.getExpedient());
                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    abandono = new Abandono();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    abandono.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    abandono = new Abandono();
                                                }
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                            }
                                        } else {
                                            if (c.existsTituloCanceladoByTituloAndDenominacion(abandono.getRegistro(), abandono.getDenominacion())) {
                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(abandono.getRegistro(), abandono.getDenominacion());
                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    abandono = new Abandono();
                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                    abandono.setCancelado(titca.getTipoCancelacion());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    abandono = new Abandono();
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
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA RENOVACIÓN, SINO " + t.getName().toUpperCase());
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

    public void guardarAbandono(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {
            Controlador c = new Controlador();
            if (abandono.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    if (estadoTemp.equals("NOTIFICADAS")) {
                        Notificada notificada = new Notificada();
                        notificada.setTipoSolicitante("");
                        notificada.setSolicitud(abandono.getSolicitud().toUpperCase());
                        notificada.setFechaPresentacion(abandono.getFechaPresentacion());
                        notificada.setNoComprobantePresentSolic("");
                        notificada.setNoComprobanteEmisionCert("");
//                        notificada.setTotalFoliosExpediente(desistida.getTotalFoliosExpediente());
                        notificada.setFechaElaboraNotificacion(new Date());
                        notificada.setNotificacion(c.getNextNumeroNotificacion(notificada.getFechaElaboraNotificacion()));
                        notificada.setFechaCertificado(abandono.getFechaCertificado());
                        //notificada.setTituloResolucion(abandono.getResolucion());
                        notificada.setRegistroNo(abandono.getRegistro());
                        notificada.setFechaRegistro(abandono.getFechaRegistro());
                        //notificada.setFechaVenceRegistro(abandono.getFechaVencimiento());
                        notificada.setDenominacion(abandono.getDenominacion());
                        notificada.setLema("");
                        notificada.setSigno(abandono.getSigno());
                        notificada.setClase("");
                        notificada.setTitularActual(abandono.getTitularActual());
                        notificada.setTacNJ("");
                        notificada.setNacTitularAc("");
                        notificada.setDomicilioTitularAc("");
                        notificada.setApeApodRepre(abandono.getApeApodRepre());
                        notificada.setFechaNotifica(abandono.getFechaNotificacion());
                        notificada.setCasilleroSenadi(abandono.getCasilleroSenadi());
                        //notificada.setFechaProvidencia(abandono.getFechaProvidencia());
                        notificada.setFechaNotificaPro(new Date());
                        notificada.setResponsable(abandono.getResponsable());
                        notificada.setIdentificacion(abandono.getIdentificacion());
                        notificada.setCancelado(abandono.getCancelado());
                        notificada.setSolicitante(abandono.getSolicitante());
                        notificada.setRo(abandono.getRo());
//
                        if (c.validarExistenciaNotificada(notificada.getSolicitud())) {
//                                context.addCallbackParam("saved", false);
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en notificadas con el mismo número de solicitud");
                        } else {
                            if (c.saveNotificada(notificada)) {

                                c = new Controlador();
                                List<Abandono> abandos = c.getAbandonosBySolSenadi(notificada.getSolicitud());
                                int cont = 0;
                                for (int i = 0; i < abandos.size(); i++) {
                                    if (c.removeAbandono(abandos.get(i))) {
                                        cont++;
                                    }
                                }
                                if (abandos.size() == cont) {
                                    c.saveHistorial("NOTIFICADAS", "ABANDONOS", notificada.getSolicitud(), "PASADO A", 0, getLoginBean().getLogin());

                                    loadAbandonos();
                                    //context.addCallbackParam("saved", true);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + notificada.getSolicitud() + " de abandono a notificada");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                } else {
                                    //context.addCallbackParam("saved", false);
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER EL ABANDONO");
                                }
                            } else {
                                //context.addCallbackParam("saved", false);
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                            }
                        }
                    }
                } else {
//                    //Editar Abandono
                    if (c.validarExistenciaAbandono(abandono)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        abandono.setSolicitud(abandono.getSolicitud().toUpperCase());
                        if (c.updateAbandono(abandono)) {
                            System.out.println("llego aquí 4");
                            c.saveHistorial("ABANDONO", "ABANDONO", abandono.getSolicitud(), "EDITADO", 0, loginBean.getLogin());
                            loadAbandonos();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA ABANDONO");
                        }
                    }
                }
            } else {
                //Guardar Abandono
                if (c.validarExistenciaNotificada(abandono.getSolicitud())) {
                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "YA EXISTE UN TRÁMITE CON EL MISMO NÚMERO DE SOLICITUD");
                } else {
                    ModificacionApp mapp = c.getModificacionApp(abandono.getSolicitud());
                    if (mapp.getId() != null) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                    } else {
                        boolean habilitado = true;
                        if (abandono.getDenominacion() != null && !abandono.getDenominacion().trim().isEmpty()
                                && abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                            if (c.existsTituloCanceladoByTituloAndDenominacion(abandono.getRegistro(), abandono.getDenominacion())) {
                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(abandono.getRegistro(), abandono.getDenominacion());
                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    abandono = new Abandono();
                                    habilitado = false;
                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                    abandono.setCancelado(titca.getTipoCancelacion());
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    abandono = new Abandono();
                                    habilitado = false;
                                }
                            } else {
                                habilitado = true;
                            }
                        }

                        if (habilitado) {
                            abandono.setSolicitud(abandono.getSolicitud().toUpperCase());
                            abandono.setNumeroAbandono(c.getNextNumeroAbandono(new Date()));
                            if (c.saveAbandono(abandono)) {
                                c.saveModificacionApp(abandono.getDenominacion(), abandono.getRegistro(), abandono.getSolicitud(), "RENOVACION", loginBean.getLogin());
                                c.saveHistorial("ABANDONO", "ABANDONO", abandono.getSolicitud(), "CREADO", 0, loginBean.getLogin());
                                System.out.println("Nuevo abandono " + (abandono.getSolicitud() != null ? abandono.getSolicitud() : "") + " guardado");
                                loadAbandonos();
                                //context.addCallbackParam("saved", true);
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "GUARDADO", "ABANDONO GUARDADO CORRECTAMENTE.");
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
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararDescarga(ActionEvent ae) {
        FacesMessage msg = null;
        abandono = (Abandono) abandonosDataTable.getRowData();
        if (abandono != null && abandono.getId() != null) {
            if (abandono.getSolicitante() != null && !abandono.getSolicitante().trim().isEmpty()) {
                if (abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                    if (Operaciones.validarFecha(abandono.getFechaRegistro())) {
                        Controlador c = new Controlador();
                        if (abandono.getRo() != null && !abandono.getRo().trim().isEmpty()) {
                            loginBean.setAbandono(abandono);
                            loginBean.setVarious(false);
                            System.out.println("envía abandono descargar");
                            PrimeFaces.current().ajax().addCallbackParam("doit", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO PREPARADO PARA DESCARGA");

                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE INGRESAR UN MOTIVO DE NOTIFICACIÓN");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL ABANDONO " + abandono.getSolicitud() + " NO POSEE FECHA DE REGISTRO");
                    }

                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL ABANDONO " + abandono.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE INGRESAR UN SOLICITANTE VÁLIDO PARA EL TRÁMITE " + abandono.getSolicitud());
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedAbandonos.isEmpty()) {

            System.out.println("Descargando Múltiples Abandonos... " + selectedAbandonos.size());

            boolean flag = true;
            Controlador c = new Controlador();
            String msj = "";
            for (int i = 0; i < selectedAbandonos.size(); i++) {
                Abandono abandonoaux = selectedAbandonos.get(i);
                if (abandonoaux != null && abandonoaux.getId() != null) {
                    if (abandonoaux.getSolicitante() != null && !abandonoaux.getSolicitante().trim().isEmpty()) {
                        if (abandonoaux.getRegistro() != null && !abandonoaux.getRegistro().trim().isEmpty()) {
                            if (Operaciones.validarFecha(abandonoaux.getFechaRegistro())) {
                                if (abandonoaux.getRo() == null || abandonoaux.getRo().trim().isEmpty()) {
                                    flag = false;
                                    msj = "DEBE INGRESAR UN MOTIVO DE NOTIFICACIÓN PARA EL TRÁMITE " + abandonoaux.getSolicitud();
                                    break;
                                }
                            } else {
                                flag = false;
                                msj = "EL ABANDONO " + abandonoaux.getSolicitud() + " NO POSEE FECHA DE REGISTRO";
                                break;
                            }
                        } else {
                            flag = false;
                            msj = "EL ABANDONO " + abandonoaux.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO";
                            break;
                        }
                    } else {
                        flag = false;
                        msj = "DEBE INGRESAR UN SOLICITANTE VÁLIDO PARA EL TRÁMITE " + abandonoaux.getSolicitud();
                        break;
                    }
                } else {
                    flag = false;
                    msj = "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN";
                    break;
                }
            }
            if (flag) {
                loginBean.setAbandonos(selectedAbandonos);
                loginBean.setVarious(true);
                System.out.println("envía abandonos seleccionados a descargar");
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONOS CARGADOS PARA DESCARGA, ESPERE...");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", msj);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN SELECCIÓN", "SELECCIONE AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {
        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        abandono = (Abandono) abandonosDataTable.getRowData();
        if (abandono != null) {
            Controlador c = new Controlador();
            abandono = c.getAbandonoBySolSenadi(abandono.getSolicitud());
            c.refreshAbandono(abandono);

//            System.out.println("fechaaaaaaaaaA: " + notificacion.getFechaPresentacion());
            roselectable = false;
            dialogTitle = "EDITAR ABANDONO " + abandono.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar el Abandono: " + abandono.getSolicitud() + "?";

            RenewalForm rf = c.getRenewalFormsByApplicationNumber(abandono.getSolicitud());
            if (rf.getId() != null) {
                abandono.setIdRenewalForm(rf.getId());
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANBDONO CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR ABANDONO");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCasillero(ActionEvent ae) {
        if (abandono != null && abandono.getId() != null) {
            Controlador c = new Controlador();
            abandono.setCasilleroSenadi(c.buscarCasilleroBySolicitud(abandono.getSolicitud()));
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + abandono.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(abandono.getIdRenewalForm(), abandono.getSolicitud());
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

    public void eliminarAbandono(ActionEvent ae) {
        FacesMessage msg = null;
        abandono = (Abandono) abandonosDataTable.getRowData();
        if (abandono != null) {
            Controlador c = new Controlador();
            if (c.removeAbandono(abandono)) {
                c.saveHistorial("ABANDONO", "ABANDONO", abandono.getSolicitud(), "ELIMINADO", 0, loginBean.getLogin());
                loadAbandonos();
                System.out.println("Abandono " + abandono.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO " + abandono.getSolicitud() + "ELIMINADO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR ABANDONO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR ABANDONO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        abandono = (Abandono) abandonosDataTable.getRowData();
        if (abandono != null) {
            dialogTitle = "SEGUIMIENTO " + abandono.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(abandono.getSolicitud());
            setHistorial("");
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: ABANDONO";
            }
        }
    }

    public void onTipoAbandonoSelectedListener() {
        System.out.println(estadoTemp);
        if (estadoTemp != null && estadoTemp.equals("NOTIFICADAS")) {
            saveEdit = "ENVIAR";
        } else {
            saveEdit = "EDITAR";
        }
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

    /**
     * @return the abandonos
     */
    public List<Abandono> getAbandonos() {
        return abandonos;
    }

    /**
     * @param abandonos the abandonos to set
     */
    public void setAbandonos(List<Abandono> abandonos) {
        this.abandonos = abandonos;
    }

    /**
     * @return the abandonosFiltradas
     */
    public List<Abandono> getAbandonosFiltradas() {
        return abandonosFiltradas;
    }

    /**
     * @param abandonosFiltradas the abandonosFiltradas to set
     */
    public void setAbandonosFiltradas(List<Abandono> abandonosFiltradas) {
        this.abandonosFiltradas = abandonosFiltradas;
    }

    /**
     * @return the abandonosDataTable
     */
    public UIData getAbandonosDataTable() {
        return abandonosDataTable;
    }

    /**
     * @param abandonosDataTable the abandonosDataTable to set
     */
    public void setAbandonosDataTable(UIData abandonosDataTable) {
        this.abandonosDataTable = abandonosDataTable;
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
     * @return the abandono
     */
    public Abandono getAbandono() {
        return abandono;
    }

    /**
     * @param abandono the abandono to set
     */
    public void setAbandono(Abandono abandono) {
        this.abandono = abandono;
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
     * @return the roRazones
     */
    public List<String> getRoRazones() {
        return roRazones;
    }

    /**
     * @param roRazones the roRazones to set
     */
    public void setRoRazones(List<String> roRazones) {
        this.roRazones = roRazones;
    }

    /**
     * @return the razon
     */
    public String getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(String razon) {
        this.razon = razon;
    }

    /**
     * @return the roselectable
     */
    public boolean isRoselectable() {
        return roselectable;
    }

    /**
     * @param roselectable the roselectable to set
     */
    public void setRoselectable(boolean roselectable) {
        this.roselectable = roselectable;
    }

    /**
     * @return the selectedAbandonos
     */
    public List<Abandono> getSelectedAbandonos() {
        return selectedAbandonos;
    }

    /**
     * @param selectedAbandonos the selectedAbandonos to set
     */
    public void setSelectedAbandonos(List<Abandono> selectedAbandonos) {
        this.selectedAbandonos = selectedAbandonos;
    }

    /**
     * @return the roDataTable
     */
    public UIData getRoDataTable() {
        return roDataTable;
    }

    /**
     * @param roDataTable the roDataTable to set
     */
    public void setRoDataTable(UIData roDataTable) {
        this.roDataTable = roDataTable;
    }

    /**
     * @return the roChoose
     */
    public String getRoChoose() {
        return roChoose;
    }

    /**
     * @param roChoose the roChoose to set
     */
    public void setRoChoose(String roChoose) {
        this.roChoose = roChoose;
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
     * @return the rutaNotificacionCasillero
     */
    public String getRutaNotificacionCasillero() {
        return rutaNotificacionCasillero;
    }

    /**
     * @param rutaNotificacionCasillero the rutaNotificacionCasillero to set
     */
    public void setRutaNotificacionCasillero(String rutaNotificacionCasillero) {
        this.rutaNotificacionCasillero = rutaNotificacionCasillero;
    }

    /**
     * @return the paraEnviar
     */
    public boolean isParaEnviar() {
        return paraEnviar;
    }

    /**
     * @param paraEnviar the paraEnviar to set
     */
    public void setParaEnviar(boolean paraEnviar) {
        this.paraEnviar = paraEnviar;
    }
}
