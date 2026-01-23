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
import org.primefaces.event.RowEditEvent;
import senadi.gob.ec.mod.daop.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.mod.daop.PpdiTituloSignoDistintivo;
import senadi.gob.ec.mod.model.Delegacion;
import senadi.gob.ec.mod.model.Delegado;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.RazonCorreccion;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.Resolucion;
import senadi.gob.ec.mod.model.Secretario;
import senadi.gob.ec.mod.model.iepdep.HallmarkForms;
import senadi.gob.ec.mod.model.iepform.PaymentReceipt;
import senadi.gob.ec.mod.model.iepform.Person;
import senadi.gob.ec.mod.model.iepform.RenewalForm;
import senadi.gob.ec.mod.model.iepform.Types;
import senadi.gob.ec.mod.ucc.Controlador;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "configuracionBean")
@ViewScoped
public class ConfiguracionBean implements Serializable {

    private List<Delegado> delegados;
    private Delegado delegado;
    private UIData delegadoDataTable;

    private List<Delegacion> delegaciones;
    private UIData delegacionesDataTable;

    private List<Resolucion> resoluciones;
    private UIData resolucionDataTable;

    private List<Resolucion> resolucionesNotificacion;
    private UIData resolucionNotDataTable;

    private List<Renovacion> renovaciones;
    private List<Renovacion> renovacionesFiltradas;
    private UIData renovacionesDataTable;

    private List<Secretario> secretarios;
    private UIData secretarioNotDataTable;

    private LoginBean loginBean;

    private RazonCorreccion razon;
    private String errorrazon;

    public ConfiguracionBean() {
        Controlador c = new Controlador();
        delegados = c.getAllDelegados();
        delegaciones = c.getAllDelegaciones();
        resoluciones = c.getResolucionesByTipo("renovacion");
        resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
        secretarios = c.getSecretarios();
        loginBean = getLogin();
    }

    public void mostrarRazon(ActionEvent ae) {
        razon = new RazonCorreccion();
    }

    public String validarRepetidos(String[] trams) {
        List<String> tramaux = new ArrayList<>();
        for (int i = 0; i < trams.length; i++) {
            String tram = trams[i].trim();
            if (!tramaux.contains(tram)) {
                tramaux.add(tram);
            } else {
                return tram;
            }
        }
        return "no";
    }

    public void ejecutarRazon(ActionEvent ae) {
        FacesMessage msg = null;
        if (razon != null) {
            System.out.println(razon.getTramites());
            String[] tramites = razon.getTramites().replace("¸", ",").split(",");
            String res = validarRepetidos(tramites);
            if (res.equals("no")) {
                Object[] datos = null;
                datos = renovaciones(tramites);
                if (!datos[0].toString().isEmpty()) {
                    errorrazon = datos[0].toString();
                    System.out.println("error---------------\n" + getErrorrazon());
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL LEER LOS TRÁMITES INGRESADOS: " + getErrorrazon());
                } else {
                    List<Renovacion> renovacs = (List<Renovacion>) datos[1];
                    loginBean.setRenovacionesFlotantes(renovacs);
                    loginBean.setNotificadasFlotantes(new ArrayList<Notificada>());
                    loginBean.setVarious(true);
                    loginBean.setAllInOne(false);
                    loginBean.setNewreport(true);
                    loginBean.setRazon(razon);

                    PrimeFaces.current().ajax().addCallbackParam("doit", true);
                    PrimeFaces.current().ajax().addCallbackParam("tipo", "razo");
                    PrimeFaces.current().ajax().addCallbackParam("view", "newinforme");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO RENOVACIONES SELECCIONADAS");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + res + " ESTÁ REPETIDO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GENERAR LOS DOCUMENTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Object[] renovaciones(String[] tramites) {

        List<Renovacion> novaciones = new ArrayList<>();
        String error = "";
        Controlador c = new Controlador();
        for (int i = 0; i < tramites.length; i++) {
            String tramren = tramites[i].trim();
            if (tramren.contains("SENADI") || tramren.contains("IEPI")) {
//                System.out.print("tramite: " + tramren);
                Renovacion renova = c.getRenovacionBySolSenadi(tramren);
//                System.out.println(", renova " + (i + 1) + ": " + renova.getId());
                if (renova.getId() != null) {
                    if (renova.getRegistroNo() == null || renova.getRegistroNo().trim().isEmpty()) {
                        error += "El trámite " + renova.getSolicitudSenadi() + " no tiene número de título\n";
                    } else {
                        novaciones.add(renova);
                    }
                } else {
                    error += "No se encontró la renovación: " + tramites[i] + "\n";
                }
            } else {
                error += "El trámite: " + tramites[i] + " está mal escrito\n";
            }

        }
        return new Object[]{error, novaciones};
    }

    private LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginBean loginB = (LoginBean) session.getAttribute("loginBean");
        return loginB;
    }

    public void buscarRenovacionesAutomatico(ActionEvent ae) {
        FacesMessage msg = null;
        Controlador c = new Controlador();
        renovaciones = new ArrayList<>();
        List<RenewalForm> renewals = c.getRenewalFormsNews();
        int cont = 0;
        int certificado_no = c.getNextNumeroCertificado(new Date());
        for (int i = 0; i < renewals.size(); i++) {
            RenewalForm rf = renewals.get(i);
//            Types t = c.getTypes(rf.getTransactionMotiveId());

            if (c.validarExistenciaRenovacion(rf.getApplicationNumber())) {
                System.out.println("EL TRÁMITE " + rf.getApplicationNumber() + " YA SE ENCUENTRA REGISTRADO EN RENOVACIONES");
            } else if (c.validarExistenciaNotificada(rf.getApplicationNumber())) {
                System.out.println("EL TRÁMITE " + rf.getApplicationNumber() + " ESTÁ EN LA PESTAÑA DE NOTIFICADOS");
            } else if (c.validarExistenciaDesistida(rf.getApplicationNumber())) {
                System.out.println("EL TRÁMITE " + rf.getApplicationNumber() + " ESTÁ EN LA PESTAÑA DE DESISTIDAS");
            } else if (c.validarExistenciaCaducada(rf.getApplicationNumber())) {
                System.out.println("EL TRÁMITE " + rf.getApplicationNumber() + " ESTÁ EN LA PESTAÑA DE CADUCADAS-NEGADAS");
            } else {

                Types ttp = c.getTypes(rf.getFormId());

                if (ttp.getId() != null) {

                    System.out.println((cont++) + ": " + rf.getApplicationNumber());
                    Renovacion renovacion = new Renovacion();

                    renovacion.setCertificadoNo(certificado_no);
                    renovacion.setFechaCertificado(new Date());

                    renovacion.setResponsable(loginBean.getLogin().substring(0, 2).toUpperCase());

                    renovacion.setSolicitudSenadi(rf.getApplicationNumber());
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

                    renovacion.setCargado(true);

                    renovaciones.add(renovacion);
                    certificado_no++;
                } else {
                    System.out.println("EL TRÁMITE " + rf.getApplicationNumber() + " PRESENTA UN PROBLEMA DE IDENTIDAD");
                }
            }
        }
        if (renovaciones.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "RENOVACIONES", "NO SE ENCONTRARON REGISTROS NUEVOS");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "RENOVACIONES", "SE HAN CARGADO LOS REGISTROS CORRECTAMENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void saveRenovaciones(ActionEvent ae) {
        FacesMessage msg = null;
        if (renovaciones != null && !renovaciones.isEmpty()) {
            boolean todoposi = true;
            for (int i = 0; i < renovaciones.size(); i++) {
                Renovacion renova = renovaciones.get(i);
                Controlador c = new Controlador();
                if (c.saveRenovacion(renova)) {
                    System.out.println("Automático: Trámite " + renova.getSolicitudSenadi() + " Guardado");
                } else {
                    todoposi = false;
                    System.err.println("Automático: Trámite " + renova.getSolicitudSenadi() + " no se guardó");
                }
            }

            if (todoposi) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "RENOVACIONES", "SE HAN GUARDADO LOS REGISTROS CORRECTAMENTE");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "UNO O MÁS REGISTROS TUVIERON UN PROBLEMA AL GUARDARSE");
            }
            PrimeFaces.current().ajax().addCallbackParam("loaddone", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "LA TABLA DEBE CONTENER DATOS PARA CONTINUAR");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNewDelegado() {
        FacesMessage msg = null;
        Delegado dele = new Delegado();
        Controlador c = new Controlador();
        if (c.saveDelegado(dele)) {
            delegados = c.getAllDelegados();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVO DELEGADO", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UN NUEVO DELEGADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNewDelegacion() {
        FacesMessage msg = null;
        Delegacion dele = new Delegacion();
        Controlador c = new Controlador();
        if (c.saveDelegacion(dele)) {
            delegaciones = c.getAllDelegaciones();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVA DELEGACIÓN", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UNA NUEVA DELEGACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNewResolucion() {
        FacesMessage msg = null;
        Resolucion resol = new Resolucion();
        Controlador c = new Controlador();
        resol.setTipo("renovacion");
        if (c.saveResolucion(resol)) {
            resoluciones = c.getResolucionesByTipo("renovacion");
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVA RESOLUCIÓN", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UNA NUEVA RESOLUCIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNewResolucionNotificacion() {
        FacesMessage msg = null;
        Resolucion resol = new Resolucion();
        Controlador c = new Controlador();
        resol.setTipo("notificacion");
        if (c.saveResolucion(resol)) {
            resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVA RESOLUCIÓN", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UNA NUEVA RESOLUCIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNewSecretario() {
        FacesMessage msg = null;
        Secretario secre = new Secretario();
        Controlador c = new Controlador();
        if (c.saveSecretario(secre)) {
            secretarios = c.getSecretarios();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVO SECRETARIO", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UN NUEVO SECRETARIO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowEditDelegado(RowEditEvent<Delegado> event) {
        FacesMessage msg = null;
        Delegado dele = event.getObject();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.updateDelegado(dele)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "DELEGADO EDITADO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL INTENTAR EDITAR EL DELEGADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR EL DELEGADO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelDelegado(RowEditEvent<Delegado> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CANCELADO", "PROCESO CANCELADO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowEditDelegacion(RowEditEvent<Delegacion> event) {
        FacesMessage msg = null;
        Delegacion dele = event.getObject();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.updateDelegacion(dele)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "DELEGACIÓN EDITADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL INTENTAR EDITAR LA DELEGACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR LA DELEGACIÓN");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelDelegacion(RowEditEvent<Delegacion> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CANCELADO", "PROCESO CANCELADO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowEditResolucion(RowEditEvent<Resolucion> event) {
        FacesMessage msg = null;
        Resolucion resol = event.getObject();
        if (resol != null) {
            Controlador c = new Controlador();
            if (c.updateResolucion(resol)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "RESOLUCIÓN EDITADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL INTENTAR EDITAR LA RESOLUCIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR LA RESOLUCIÓN");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelResolucion(RowEditEvent<Resolucion> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CANCELADO", "PROCESO CANCELADO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowEditSecretaria(RowEditEvent<Secretario> event) {
        FacesMessage msg = null;
        Secretario resol = event.getObject();
        if (resol != null) {
            Controlador c = new Controlador();
            if (c.updateSecretario(resol)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "SECRETARIO EDITADO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL INTENTAR EDITAR AL SECRETARIO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR AL SECRETARIO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelSecretario(RowEditEvent<Secretario> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CANCELADO", "PROCESO CANCELADO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableDelegado(Delegado dele) {
        Controlador c = new Controlador();
        if (dele != null) {
            if (dele.isEstado()) {
                dele.setEstado(false);
                c.updateDelegado(dele);
                delegados = c.getAllDelegados();
            } else {
                dele.setEstado(true);
                c.activeADelegado(dele, delegados);
                delegados = c.getAllDelegados();
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR EL DELEGADO");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void eliminarDelegado(ActionEvent ae) {
        FacesMessage msg = null;
        Delegado dele = (Delegado) delegadoDataTable.getRowData();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.removeDelegado(dele)) {
                delegados = c.getAllDelegados();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "DELEGADO REMOVIDO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER EL DELEGADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER EL DELEGADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableDelegacion(Delegacion dele) {
        Controlador c = new Controlador();
        if (dele != null) {
            if (dele.isActivo()) {
                dele.setActivo(false);
                c.updateDelegacion(dele);
                delegaciones = c.getAllDelegaciones();
            } else {
                dele.setActivo(true);
                c.activeADelegacion(dele, delegaciones);
                delegaciones = c.getAllDelegaciones();
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR LA DELEGACIÓN");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void eliminarDelegacion(ActionEvent ae) {
        FacesMessage msg = null;
        Delegacion dele = (Delegacion) delegacionesDataTable.getRowData();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.removeDelegacion(dele)) {
                delegaciones = c.getAllDelegaciones();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "DELEGACIÓN REMOVIDA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA DELEGACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA DELEGACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableResolucion(Resolucion resol) {
        Controlador c = new Controlador();
        if (resol != null) {
            if (resol.isActivo()) {
                resol.setActivo(false);
                c.updateResolucion(resol);
                resoluciones = c.getResolucionesByTipo("renovacion");
            } else {
                resol.setActivo(true);
                c.activeAResolucion(resol, resoluciones);
                resoluciones = c.getResolucionesByTipo("renovacion");
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR LA DELEGACIÓN");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void eliminarResolucion(ActionEvent ae) {
        FacesMessage msg = null;
        Resolucion resol = (Resolucion) resolucionDataTable.getRowData();
        if (resol != null) {
            Controlador c = new Controlador();
            if (c.removeResolucion(resol)) {
                resoluciones = c.getResolucionesByTipo("renovacion");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "RESOLUCIÓN REMOVIDA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA RESOLUCIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA RESOLUCIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableResolucionNotificacion(Resolucion resol) {
        Controlador c = new Controlador();
        if (resol != null) {
            if (resol.isActivo()) {
                resol.setActivo(false);
                c.updateResolucion(resol);
                resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
            } else {
                resol.setActivo(true);
                c.activeAResolucion(resol, resolucionesNotificacion);
                resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR LA DELEGACIÓN");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void enableSecretario(Secretario secre) {
        Controlador c = new Controlador();
        if (secre != null) {
            if (secre.isEstado()) {
                secre.setEstado(false);
                c.updateSecretario(secre);
                resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
            } else {
                secre.setEstado(true);
                c.activeASecretario(secre, secretarios);
                resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR AL SECRETARIO");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void eliminarResolucionNotificacion(ActionEvent ae) {
        FacesMessage msg = null;
        Resolucion resol = (Resolucion) resolucionNotDataTable.getRowData();
        if (resol != null) {
            Controlador c = new Controlador();
            if (c.removeResolucion(resol)) {
                resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "RESOLUCIÓN-NOTIFICACIÓN REMOVIDA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA RESOLUCIÓN-NOTIFICACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA RESOLUCIÓN-NOTIFICACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarSecretario(ActionEvent ae) {
        FacesMessage msg = null;
        Secretario secre = (Secretario) secretarioNotDataTable.getRowData();
        if (secre != null) {
            Controlador c = new Controlador();
            if (c.removeSecretario(secre)) {
                secretarios = c.getSecretarios();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "SECRETARIO REMOVIDO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER AL SECRETARIO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER AL SECRETARIO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the delegados
     */
    public List<Delegado> getDelegados() {
        return delegados;
    }

    /**
     * @param delegados the delegados to set
     */
    public void setDelegados(List<Delegado> delegados) {
        this.delegados = delegados;
    }

    /**
     * @return the delegado
     */
    public Delegado getDelegado() {
        return delegado;
    }

    /**
     * @param delegado the delegado to set
     */
    public void setDelegado(Delegado delegado) {
        this.delegado = delegado;
    }

    /**
     * @return the delegadoDataTable
     */
    public UIData getDelegadoDataTable() {
        return delegadoDataTable;
    }

    /**
     * @param delegadoDataTable the delegadoDataTable to set
     */
    public void setDelegadoDataTable(UIData delegadoDataTable) {
        this.delegadoDataTable = delegadoDataTable;
    }

    /**
     * @return the delegaciones
     */
    public List<Delegacion> getDelegaciones() {
        return delegaciones;
    }

    /**
     * @param delegaciones the delegaciones to set
     */
    public void setDelegaciones(List<Delegacion> delegaciones) {
        this.delegaciones = delegaciones;
    }

    /**
     * @return the delegacionesDataTable
     */
    public UIData getDelegacionesDataTable() {
        return delegacionesDataTable;
    }

    /**
     * @param delegacionesDataTable the delegacionesDataTable to set
     */
    public void setDelegacionesDataTable(UIData delegacionesDataTable) {
        this.delegacionesDataTable = delegacionesDataTable;
    }

    /**
     * @return the resoluciones
     */
    public List<Resolucion> getResoluciones() {
        return resoluciones;
    }

    /**
     * @param resoluciones the resoluciones to set
     */
    public void setResoluciones(List<Resolucion> resoluciones) {
        this.resoluciones = resoluciones;
    }

    /**
     * @return the resolucionDataTable
     */
    public UIData getResolucionDataTable() {
        return resolucionDataTable;
    }

    /**
     * @param resolucionDataTable the resolucionDataTable to set
     */
    public void setResolucionDataTable(UIData resolucionDataTable) {
        this.resolucionDataTable = resolucionDataTable;
    }

    /**
     * @return the resolucionesNotificacion
     */
    public List<Resolucion> getResolucionesNotificacion() {
        return resolucionesNotificacion;
    }

    /**
     * @param resolucionesNotificacion the resolucionesNotificacion to set
     */
    public void setResolucionesNotificacion(List<Resolucion> resolucionesNotificacion) {
        this.resolucionesNotificacion = resolucionesNotificacion;
    }

    /**
     * @return the resolucionNotDataTable
     */
    public UIData getResolucionNotDataTable() {
        return resolucionNotDataTable;
    }

    /**
     * @param resolucionNotDataTable the resolucionNotDataTable to set
     */
    public void setResolucionNotDataTable(UIData resolucionNotDataTable) {
        this.resolucionNotDataTable = resolucionNotDataTable;
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
     * @return the secretarios
     */
    public List<Secretario> getSecretarios() {
        return secretarios;
    }

    /**
     * @param secretarios the secretarios to set
     */
    public void setSecretarios(List<Secretario> secretarios) {
        this.secretarios = secretarios;
    }

    /**
     * @return the secretarioNotDataTable
     */
    public UIData getSecretarioNotDataTable() {
        return secretarioNotDataTable;
    }

    /**
     * @param secretarioNotDataTable the secretarioNotDataTable to set
     */
    public void setSecretarioNotDataTable(UIData secretarioNotDataTable) {
        this.secretarioNotDataTable = secretarioNotDataTable;
    }

    /**
     * @return the razon
     */
    public RazonCorreccion getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(RazonCorreccion razon) {
        this.razon = razon;
    }

    /**
     * @return the errorrazon
     */
    public String getErrorrazon() {
        return errorrazon;
    }

    /**
     * @param errorrazon the errorrazon to set
     */
    public void setErrorrazon(String errorrazon) {
        this.errorrazon = errorrazon;
    }
}
