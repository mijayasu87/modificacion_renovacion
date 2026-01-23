/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.bean;

import com.jcraft.jsch.JSchException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIData;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FilesUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFileWrapper;
import org.primefaces.model.file.UploadedFiles;
import org.primefaces.model.file.UploadedFilesWrapper;
import senadi.gob.ec.mod.dao.AbandonoDAO;
import senadi.gob.ec.mod.dao.CaducadaDAO;
import senadi.gob.ec.mod.dao.NotificadaDAO;
import senadi.gob.ec.mod.dao.RenovacionDAO;
import senadi.gob.ec.mod.model.Abandono;
import senadi.gob.ec.mod.model.Caducada;
import senadi.gob.ec.mod.model.Documento;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.UploadNotificacion;
import senadi.gob.ec.mod.model.iepform.RenewalForm;
import senadi.gob.ec.mod.model.iepicas.LockerNotifications;
import senadi.gob.ec.mod.model.iepicas.Notifications;
import senadi.gob.ec.mod.model.iepicas.NotificationsDAO;
import senadi.gob.ec.mod.ucc.Controlador;
import senadi.gob.ec.mod.ucc.FTPFiles;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "uploadBean")
@ViewScoped
public class UploadCertBean implements Serializable {

    private UploadedFilesWrapper filesw;
    private UploadedFileWrapper filew;

    private UploadedFiles files;
    private UploadedFile file;

    private boolean notexistentes;
    private boolean activo;
    private String log;

    private List<UploadNotificacion> notificaciones;
    private List<UploadNotificacion> notificacionesFiltradas;
    private UIData notificacionesDataTable;

    private List<UploadNotificacion> notificados;
    private List<UploadNotificacion> notificadosFiltradas;

    private UIData notificadosDataTable;

    private LoginBean login;

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private String localpath;
    private List<Documento> documentos;
    private List<Documento> documentosFiltrados;
    private UploadNotificacion notificado;
    private boolean obsaviso;

    private double progressValue;

    private boolean cargaHecha;

    public UploadCertBean() {
        loadData();
    }

    public LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginBean logb = (LoginBean) session.getAttribute("loginBean");
        return logb;
    }

    private void loadData() {
        activo = false;
        log = "";
        file = null;
        notificaciones = new ArrayList<>();
        notificados = new ArrayList<>();
        login = getLogin();
        localpath = "/opt/renovaciones_doc/";
    }

    public void uploadMultiple(ActionEvent ae) {
        if (files != null) {
            for (UploadedFile f : files.getFiles()) {
                FacesMessage message = new FacesMessage("Successful", f.getFileName() + " is uploaded.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        System.out.println("hereeeeeeeeeeee");
        FacesMessage message = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void uploadFiles(FileUploadEvent event) throws JSchException, IllegalAccessException, IOException {
//        loadData();
        file = event.getFile();

        if (file != null && file.getFileName() != null && !file.getFileName().trim().isEmpty()) {
            System.out.println("---> " + file.getFileName());
            updloadPdfToCorrespondingFolder();
            activo = true;
        }
        FacesMessage message = new FacesMessage("Successful", "Acción Completada.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void uploadFilesP(FilesUploadEvent event) throws JSchException, IllegalAccessException, IOException {
        loadData();
        System.out.println("files size: " + files.getSize());
        FacesMessage message = new FacesMessage("Successful", "Acción Completada.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void updloadPdfToCorrespondingFolder() throws JSchException, IllegalAccessException, IOException {

        String pdfName = file.getFileName();

        if (pdfName.contains("signed")) {
            String tramite = Operaciones.getTramiteFromPdfName(pdfName);
            if (tramite.trim().isEmpty()) {
                log += "No se encuentra el casillero para archivo: " + pdfName + "\n";
            } else {
                Controlador c = new Controlador();
                if (!c.validarExistenciaUploadNotificacion(tramite, true)) {
                    RenewalForm rd = c.findRenewalFormsByApplicationNumber(tramite);
                    if (rd.getId() != null) {
                        String ruta = "/var/www/html/solicitudes/media/files/renewal_forms/" + rd.getId() + "/";

                        FTPFiles ftpf = new FTPFiles(130);

                        if (ftpf.validateFolderExists(ruta)) {
                            String rutalocal = "/opt/doctr_temp/";
//                ruta = "/home/mjyanangomez/certs_pru/";

                            if (Operaciones.copyFile(pdfName, file.getInputStream(), rutalocal)) {

                                if (ftpf.doCopyFromLocalToRemote(rutalocal + pdfName, ruta)) {
                                    System.out.println("Si se copió " + tramite + " en: " + ruta);

                                    UploadNotificacion not = new UploadNotificacion();
                                    not.setRenewalFormId(rd.getId());
                                    not.setDocumento(pdfName);
                                    not.setSolicitud(tramite);
                                    not.setCasillero(c.getCasilleroSenadi(rd.getOwnerId()));
                                    not.setEstado(false);
                                    not.setUsuario(login.getLogin());
                                    not.setActivo(true);

                                    if (!c.validarExistenciaUploadNotificacion(not.getSolicitud())) {
                                        if (c.saveUploadNotificacion(not)) {
                                            log += "Se subió correctamente el archivo: " + pdfName + "\n";
                                            notificaciones.add(not);
                                        } else {
                                            log += "Se subió correctamente el archivo: " + pdfName + ", pero no se guardo en el historial, 'INFORMAR'\n";
                                        }
                                    } else {
                                        log += "Se subió correctamente el archivo: " + pdfName + "\n";
                                    }
                                } else {
                                    System.out.println("No se copió " + tramite + " en: " + ruta);
                                    log += "No se subió el archivo: " + pdfName + "\n";
                                }
                            } else {
                                log += "No se subió el archivo " + pdfName + " al repositorio local\n";
                            }

                        } else {
                            System.out.println("No existe ruta para archivo " + pdfName);
                            log += "No existe ruta para archivo " + pdfName + "\n";
                        }
                    } else {
                        log += "No se encontró la ruta del expediente del documento " + pdfName + "\n";
                    }
                } else {
                    log += "El trámite " + tramite + ", ya ha sido notificado anteriormente\n";
                }
            }
        } else {
            log += "El documento " + pdfName + " debe estar firmado para notificarse\n";
        }

    }

    public void prepararNuevo(ActionEvent ae) {
        loadData();
    }

    public void notificarCertificados(ActionEvent ae) throws JSchException, IllegalAccessException, IOException {
        FacesMessage message = null;
        if (notificaciones != null && !notificaciones.isEmpty()) {
            String rutaarchivo = "/var/www/html/solicitudes/media/files/renewal_forms/";
            String rutacasillero = "/var/www/html/casilleros/media/files/";

            FTPFiles ftpf = new FTPFiles(130);
            Controlador c = new Controlador();

            double val = 100.0 / notificaciones.size();
            progressValue = 0;

            for (int i = 0; i < notificaciones.size(); i++) {
                System.out.println(i + ":------------------------------");
                UploadNotificacion un = notificaciones.get(i);

                if (ftpf.validateFolderExists(rutaarchivo + un.getRenewalFormId())) {
                    if (ftpf.validateFolderExists(rutacasillero + un.getCasillero())) {
                        String renewalcert = rutaarchivo + un.getRenewalFormId() + "/" + un.getDocumento();
                        String casillerofolder = rutacasillero + un.getCasillero() + "/";
                        System.out.println(renewalcert + "\n" + casillerofolder);
//                        if (ftpf.exeComando(renewalcert, casillerofolder)) {
                        if (ftpf.exeComando("cp " + renewalcert + " " + casillerofolder + " && echo 'movido'")) {
                            NotificationsDAO nd = new NotificationsDAO();

                            Notifications n = new Notifications();
                            n.setNot_id(3);
                            n.setMat_id(13);
                            n.setMatter(un.getSolicitud());
                            n.setDocument(un.getDocumento());
                            n.setCreateDt(Operaciones.getCurrentTimeStamp());
                            n.setSource("LOCAL");
                            n.setCreated_id(1);

                            if (nd.saveNotifications(n)) {
                                LockerNotifications ln = new LockerNotifications();
                                ln.setLockerId(un.getCasillero());
                                ln.setStatus("SENT");
                                ln.setDocument(un.getDocumento());
                                Notifications nbd = nd.getNotificationsByMatterAndCreateDt(n.getMatter(), n.getCreateDt());
                                if (nbd.getId() != null) {
                                    ln.setNotification_id(nbd.getId());
                                    un.setNotificationsId(nbd.getId());
                                    if (nd.saveLockerNotifications(ln)) {
                                        //para indicar que se ha entregado certificado digital firmado
                                        List<Renovacion> renovaciones = c.getRenovacionesBySolSenadi(un.getSolicitud());
                                        if (!renovaciones.isEmpty()) {
                                            for (int j = 0; j < renovaciones.size(); j++) {
                                                Renovacion ren = renovaciones.get(j);
                                                ren.setCertificadoEmitido(true);
                                                RenovacionDAO rd = new RenovacionDAO(ren);
                                                try {
                                                    rd.update();
                                                    c.saveHistorial("RENOVACIONES", "RENOVACIONES", ren.getSolicitudSenadi(), "CERTIFICADO EMITIDO " + un.getDocumento(), 0, login.getLogin());
                                                } catch (Exception ex) {
                                                    Logger.getLogger(UploadCertBean.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            }
                                        } else {
                                            //para indicar que se ha realizado una notificación digital firmado
                                            List<Notificada> notificadas = c.getNotificadasBySolSenadi(un.getSolicitud());
                                            if (!notificadas.isEmpty()) {
                                                for (int j = 0; j < notificadas.size(); j++) {
                                                    Notificada noti = notificadas.get(j);
                                                    noti.setNotificacionEmitida(true);
                                                    NotificadaDAO nod = new NotificadaDAO(noti);
                                                    try {
                                                        nod.update();
                                                        c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", noti.getSolicitud(), "NOTIFICACIÓN EMITIDA " + un.getDocumento(), 0, login.getLogin());
                                                    } catch (Exception ex) {
                                                        Logger.getLogger(UploadCertBean.class.getName()).log(Level.SEVERE, null, ex);
                                                    }
                                                }
                                            } else {
                                                List<Caducada> caducadas = c.getCaducadasBySolSenadi(un.getSolicitud());
                                                if (!caducadas.isEmpty()) {
                                                    for (int j = 0; j < caducadas.size(); j++) {
                                                        Caducada caduc = caducadas.get(j);
                                                        caduc.setCaducadaEmitida(true);
                                                        CaducadaDAO cd = new CaducadaDAO(caduc);
                                                        try {
                                                            cd.update();
                                                            c.saveHistorial("CADUCADAS", "CADUCADAS", caduc.getSolicitudSenadi(), "CADUCADA EMITIDA " + un.getDocumento(), 0, login.getLogin());
                                                        } catch (Exception ex) {
                                                            Logger.getLogger(UploadCertBean.class.getName()).log(Level.SEVERE, null, ex);
                                                        }
                                                    }
                                                } else {
                                                    List<Abandono> abandonos = c.getAbandonosBySolSenadi(un.getSolicitud());
                                                    if (!abandonos.isEmpty()) {
                                                        for (int j = 0; j < abandonos.size(); j++) {
                                                            Abandono aban = abandonos.get(j);
                                                            aban.setAbadonoNotificado(true);
                                                            AbandonoDAO ad = new AbandonoDAO(aban);
                                                            try {
                                                                ad.update();
                                                                c.saveHistorial("ABANDONO", "ABANDONO", aban.getSolicitud(), "ABANDONO EMITIDO " + un.getDocumento(), 0, login.getLogin());
                                                            } catch (Exception ex) {
                                                                Logger.getLogger(UploadCertBean.class.getName()).log(Level.SEVERE, null, ex);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        un.setObservacion("Notificado correctamente");
                                        un.setEstado(true);
                                        un.setFechaNotificacion(new Date());
                                    } else {
                                        System.out.println("Si se guardó notifications pero locker no");
                                        un.setObservacion("No se completó la notificación " + un.getDocumento() + ", al casillero " + un.getCasillero() + ", no se guardó ln");
                                    }
                                } else {
                                    System.err.println("No se encontró la notificación " + n.getMatter() + ", " + n.getCreateDt());
                                    un.setObservacion("No se completó la notificación " + un.getDocumento() + ", al casillero " + un.getCasillero());
                                }
                            } else {
                                System.err.println("No se guardó la notificación " + n.getMatter());
                                un.setObservacion("No se completó la notificación " + un.getDocumento() + ", al casillero " + un.getCasillero() + "no se guardó n");
                            }
                        } else {
                            System.out.println("No se copió/movió el documento " + un.getDocumento() + ", al casillero " + un.getCasillero());
                            un.setObservacion("No se pudo notificar el documento " + un.getDocumento() + ", al casillero " + un.getCasillero());
                        }
                    } else {
                        un.setObservacion("No se encuentra la ruta al casillero " + un.getCasillero() + ", del trámite " + un.getDocumento());
                    }
                } else {
                    un.setObservacion("No se encuentra ruta al archivo subido " + un.getDocumento());
                }

                c.updateUploadNotificacion(un);
                progressValue += val;
            }
            progressValue = 100;

            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "NOTIFICADOS", "ACCIÓN COMPLETADA");

        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY DOCUMENTOS CARGADOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void prepararNotificar(ActionEvent ae) {
        FacesMessage message = null;
        Controlador c = new Controlador();
        notificaciones = c.getNotificacionesByEstado(false);
        progressValue = 0;

        PrimeFaces.current().ajax().addCallbackParam("uploadn", true);
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "NOTIFICADOS", "MOSTRANDO DOCUMENTOS SUBIDOS");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void prepararNotificados(ActionEvent ae) {
        FacesMessage message = null;
        Controlador c = new Controlador();
        notificados = new ArrayList<>();
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "NOTIFICADOS", "MOSTRANDO DOCUMENTOS NOTIFICADOS");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void buscarNotificados(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            notificados = c.getUploadNotificacionByCriterio(criterio, true);
            if (notificados.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarNotificadosPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            notificados = c.getUploadNotificacionByDate(fechaInicio, fechaFin, true);
            if (notificados.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
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

    public void cargarDocumentosFtp(ActionEvent ae) throws IOException, JSchException, IllegalAccessException {
        FacesMessage message = null;
        File filelocal = new File(localpath);
        documentos = new ArrayList<>();
        if (filelocal.exists()) {
            File[] docs = filelocal.listFiles();
            if (docs.length == 0) {
                activo = false;
            } else {
                for (int i = 0; i < docs.length; i++) {
                    Documento documento = new Documento();
                    documento.setDocumento(docs[i].getName());
                    documento.setPath(docs[i].getAbsolutePath());
                    documentos.add(documento);
                }
                activo = true;
                cargaHecha = true;
            }

        } else {
            activo = false;
        }

        Controlador c = new Controlador();
        if (c.getNotificacionesByEstado(false).isEmpty()) {
            notexistentes = false;
        } else {
            notexistentes = true;
        }

        if (activo && notexistentes) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE CARGARON TODOS LOS DOCUMENTOS CORRECTAMENTE");
        } else if (!activo && notexistentes) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO EXISTEN DOCUMENTOS PARA CARGAR, PERO EXISTEN NOTIFICACIONES PENDIENTES, HAGA CLIC EN NOTIFICAR");
        } else if (!activo && !notexistentes) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON DOCUMENTOS PARA CARGAR");
        } else if (notexistentes) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EXISTEN TRÁMITES PENDIENTES PARA NOTIFICAR, DE CLIC EN 'NOTIFICAR'");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "Se cargaron todos los documentos correctamente");
        }

        obsaviso = false;
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void subirDocumentos(ActionEvent ae) throws JSchException, IllegalAccessException, IOException {
        FacesMessage message = null;
        if (documentos != null && !documentos.isEmpty()) {

            double val = 100.0 / documentos.size();
            progressValue = 0;

            for (int i = 0; i < documentos.size(); i++) {
                Documento doc = documentos.get(i);
                if (uploadDocumentToCorrespondingFolder(doc)) {
                    documentos.get(i).setEstado(true);
                    notexistentes = true;
                }
                obsaviso = true;
                progressValue += val;
//                System.out.println(progressValue + "%");
            }
            progressValue = 100;
//            System.out.println(progressValue + "%");
            cargaHecha = false;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "Acción Completada.");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTEN DOCUMENTOS EN LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onComplete() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Se ha terminado de analizar/subir todos los documentos"));
    }

    public void onCompleteN() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Se ha terminado de notificar los documentos"));
    }

    public boolean uploadDocumentToCorrespondingFolder(Documento doc) throws JSchException, IllegalAccessException, IOException {

        String pdfName = doc.getDocumento();

        if (pdfName.contains("signed")) {
            String tramite = Operaciones.getTramiteFromPdfName(pdfName);
            if (tramite.trim().isEmpty()) {
                log += "No se encuentra el casillero para archivo: " + pdfName + "\n";
                doc.setObservacion("No se encuentra el casillero para el archivo");
                removerDocumentoDeFolder(doc);
            } else {
                Controlador c = new Controlador();
                //if (!c.validarExistenciaUploadNotificacion(tramite, true)) {
                if (!c.validarExistenciaUploadNotificacionByDocument(pdfName, true)) {
                    RenewalForm rd = c.findRenewalFormsByApplicationNumber(tramite);
                    if (rd.getId() != null) {
                        String ruta = "/var/www/html/solicitudes/media/files/renewal_forms/" + rd.getId() + "/";

                        FTPFiles ftpf = new FTPFiles(130);

                        if (ftpf.validateFolderExists(ruta)) {

                            if (ftpf.doCopyFromLocalToRemote(localpath + pdfName, ruta)) {
                                System.out.println("Si se copió " + tramite + " en: " + ruta);

                                UploadNotificacion not = new UploadNotificacion();
                                not.setRenewalFormId(rd.getId());
                                not.setDocumento(pdfName);
                                not.setSolicitud(tramite);
                                not.setCasillero(c.getCasilleroSenadi(rd.getOwnerId()));
                                not.setEstado(false);
                                not.setUsuario(login.getLogin());
                                not.setActivo(true);

//                                if (!c.validarExistenciaUploadNotificacion(not.getSolicitud())) {
                                if (c.saveUploadNotificacion(not)) {
                                    log += "Se subió correctamente el archivo: " + pdfName + "\n";
                                    notificaciones.add(not);
                                    removerDocumentoDeFolder(doc);
                                    return true;
                                } else {
                                    log += "Se subió correctamente el archivo: " + pdfName + ", pero no se guardo en el historial, 'INFORMAR'\n";
                                    System.out.println("Se subió correctamente el archivo: " + pdfName + ", pero no se guardo en el historial, 'INFORMAR'");
                                    doc.setObservacion("Se subió correctamente el archivo, pero no se guardo en el historial, 'INFORMAR'");
                                    removerDocumentoDeFolder(doc);
                                }
//                                } else {
//                                    log += "Ya existe el uploadnotificacion del archivo: " + pdfName + "\n";
//                                    doc.setObservacion("Ya existe el uploadnotificacion del archivo:");
//                                    removerDocumentoDeFolder(doc);
//                                }
                            } else {
                                log += "No se subió el archivo: " + pdfName + "\n";
                                doc.setObservacion("No se subió el archivo.");
                                removerDocumentoDeFolder(doc);
                            }

                        } else {
//                            System.out.println("No existe ruta para archivo " + pdfName);
                            doc.setObservacion("No existe ruta para archivo.");
                            log += "No existe ruta para archivo " + pdfName + "\n";
                            removerDocumentoDeFolder(doc);
                        }
                    } else {
                        log += "No se encontró la ruta del expediente del documento " + pdfName + "\n";
                        doc.setObservacion("No se encontró la ruta del expediente del documento.");
                        removerDocumentoDeFolder(doc);
                    }
                } else {
                    log += "El documento " + pdfName + ", ya ha sido notificado anteriormente\n";
                    doc.setObservacion("El trámite " + pdfName + ", ya ha sido notificado anteriormente");
                    removerDocumentoDeFolder(doc);
                }
            }
        } else {
            log += "El documento " + pdfName + " debe estar firmado para notificarse\n";
            doc.setObservacion("El documento " + pdfName + " debe estar firmado para notificarse\n");
            removerDocumentoDeFolder(doc);
            //System.out.println("El documento " + pdfName + " debe estar firmado para notificarse\n");
        }
        return false;
    }

    public boolean removerDocumentoDeFolder(Documento doc) {
        System.out.println("doc: " + doc.getPath());
        File file = new File(doc.getPath());
        if (file.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public void darDeBaja(ActionEvent ae) {
        FacesMessage msg = null;
        notificado = (UploadNotificacion) notificadosDataTable.getRowData();
        if (notificado != null) {
            System.out.println("llega por aquí");
            Controlador c = new Controlador();
            if (c.downLockerNotifications(notificado)) {
//                System.out.println("algooo a");
                notificado.setActivo(false);
                notificado.setEstado(false);
                notificado.setObservacion("");
                String tramite = notificado.getSolicitud();
                notificado.setSolicitud(notificado.getSolicitud() + "_bad");
                if (c.updateUploadNotificacion(notificado)) {
                    //c.saveHistorial("RENOVACIÓN", "RENOVACIÓN", tramite, "DOC RENOVACIÓN ANULADO " + un.getDocumento(), 0, user);
                    c.anularDocumentoRenovacion(notificado, tramite, login.getNombre());
                    if (validarFechas()) {
                        notificados = c.getUploadNotificacionByDate(fechaInicio, fechaFin, true);
                    } else {
                        notificados = c.getUploadNotificacionByCriterio(criterio, true);
                    }

                    System.out.println(notificado.getSolicitud() + " dado de baja de notificadas");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA DADO DE BAJA A LA NOTIFICACIÓN DEL TRÁMITE " + notificado.getSolicitud());
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA DADO DE BAJA A LA NOTIFICACIÓN DEL TRÁMITE " + notificado.getSolicitud() + ", PERO NO SE HA DESHABILITADO DE LA APP DE TRANSFERENCIAS");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO DAR DE BAJA A LA NOTIFICACIÓN DEL TRÁMITE " + notificado.getSolicitud());
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR LA NOTIFICACIÓN");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the files
     */
    public UploadedFiles getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(UploadedFiles files) {
        this.files = files;
    }

    /**
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     * @return the activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * @return the log
     */
    public String getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(String log) {
        this.log = log;
    }

    /**
     * @return the notificaciones
     */
    public List<UploadNotificacion> getNotificaciones() {
        return notificaciones;
    }

    /**
     * @param notificaciones the notificaciones to set
     */
    public void setNotificaciones(List<UploadNotificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    /**
     * @return the notificacionesFiltradas
     */
    public List<UploadNotificacion> getNotificacionesFiltradas() {
        return notificacionesFiltradas;
    }

    /**
     * @param notificacionesFiltradas the notificacionesFiltradas to set
     */
    public void setNotificacionesFiltradas(List<UploadNotificacion> notificacionesFiltradas) {
        this.notificacionesFiltradas = notificacionesFiltradas;
    }

    /**
     * @return the notificacionesDataTable
     */
    public UIData getNotificacionesDataTable() {
        return notificacionesDataTable;
    }

    /**
     * @param notificacionesDataTable the notificacionesDataTable to set
     */
    public void setNotificacionesDataTable(UIData notificacionesDataTable) {
        this.notificacionesDataTable = notificacionesDataTable;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(LoginBean login) {
        this.login = login;
    }

    /**
     * @return the notificados
     */
    public List<UploadNotificacion> getNotificados() {
        return notificados;
    }

    /**
     * @param notificados the notificados to set
     */
    public void setNotificados(List<UploadNotificacion> notificados) {
        this.notificados = notificados;
    }

    /**
     * @return the notificadosFiltradas
     */
    public List<UploadNotificacion> getNotificadosFiltradas() {
        return notificadosFiltradas;
    }

    /**
     * @param notificadosFiltradas the notificadosFiltradas to set
     */
    public void setNotificadosFiltradas(List<UploadNotificacion> notificadosFiltradas) {
        this.notificadosFiltradas = notificadosFiltradas;
    }

    /**
     * @return the notificadosDataTable
     */
    public UIData getNotificadosDataTable() {
        return notificadosDataTable;
    }

    /**
     * @param notificadosDataTable the notificadosDataTable to set
     */
    public void setNotificadosDataTable(UIData notificadosDataTable) {
        this.notificadosDataTable = notificadosDataTable;
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
     * @return the filesw
     */
    public UploadedFilesWrapper getFilesw() {
        return filesw;
    }

    /**
     * @param filesw the filesw to set
     */
    public void setFilesw(UploadedFilesWrapper filesw) {
        this.filesw = filesw;
    }

    /**
     * @return the filew
     */
    public UploadedFileWrapper getFilew() {
        return filew;
    }

    /**
     * @param filew the filew to set
     */
    public void setFilew(UploadedFileWrapper filew) {
        this.filew = filew;
    }

    /**
     * @return the documentos
     */
    public List<Documento> getDocumentos() {
        return documentos;
    }

    /**
     * @param documentos the documentos to set
     */
    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    /**
     * @return the documentosFiltrados
     */
    public List<Documento> getDocumentosFiltrados() {
        return documentosFiltrados;
    }

    /**
     * @param documentosFiltrados the documentosFiltrados to set
     */
    public void setDocumentosFiltrados(List<Documento> documentosFiltrados) {
        this.documentosFiltrados = documentosFiltrados;
    }

    /**
     * @return the obsaviso
     */
    public boolean isObsaviso() {
        return obsaviso;
    }

    /**
     * @param obsaviso the obsaviso to set
     */
    public void setObsaviso(boolean obsaviso) {
        this.obsaviso = obsaviso;
    }

    /**
     * @return the notificado
     */
    public UploadNotificacion getNotificado() {
        return notificado;
    }

    /**
     * @param notificado the notificado to set
     */
    public void setNotificado(UploadNotificacion notificado) {
        this.notificado = notificado;
    }

    /**
     * @return the notexistentes
     */
    public boolean isNotexistentes() {
        return notexistentes;
    }

    /**
     * @param notexistentes the notexistentes to set
     */
    public void setNotexistentes(boolean notexistentes) {
        this.notexistentes = notexistentes;
    }

    /**
     * @return the progressValue
     */
    public double getProgressValue() {
        return progressValue;
    }

    /**
     * @param progressValue the progressValue to set
     */
    public void setProgressValue(double progressValue) {
        this.progressValue = progressValue;
    }

    /**
     * @return the cargaHecha
     */
    public boolean isCargaHecha() {
        return cargaHecha;
    }

    /**
     * @param cargaHecha the cargaHecha to set
     */
    public void setCargaHecha(boolean cargaHecha) {
        this.cargaHecha = cargaHecha;
    }
}
