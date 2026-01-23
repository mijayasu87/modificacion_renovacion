/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.ucc;

import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import senadi.gob.ec.mod.dao.AbandonoDAO;
import senadi.gob.ec.mod.dao.CaducadaDAO;
import senadi.gob.ec.mod.dao.DelegacionDAO;
import senadi.gob.ec.mod.dao.DelegadoDAO;
import senadi.gob.ec.mod.dao.DesistidaDAO;
import senadi.gob.ec.mod.dao.HistorialDAO;
import senadi.gob.ec.mod.dao.NotificadaDAO;
import senadi.gob.ec.mod.dao.RenovacionDAO;
import senadi.gob.ec.mod.dao.ResolucionDAO;
import senadi.gob.ec.mod.dao.SecretarioDAO;
import senadi.gob.ec.mod.dao.UploadNotificacionDAO;
import senadi.gob.ec.mod.dao.UsuarioDAO;
import senadi.gob.ec.mod.daop.PpdiSignoDAO;
import senadi.gob.ec.mod.daop.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.mod.daop.PpdiTituloDAO;
import senadi.gob.ec.mod.daop.PpdiTituloSignoDistintivo;
import senadi.gob.ec.mod.model.Abandono;
import senadi.gob.ec.mod.model.Caducada;
import senadi.gob.ec.mod.model.Delegacion;
import senadi.gob.ec.mod.model.Delegado;
import senadi.gob.ec.mod.model.Desistida;
import senadi.gob.ec.mod.model.Historial;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.Resolucion;
import senadi.gob.ec.mod.model.Secretario;
import senadi.gob.ec.mod.model.UploadNotificacion;
import senadi.gob.ec.mod.model.Usuario;
import senadi.gob.ec.mod.model.iepdep.HallmarkForms;
import senadi.gob.ec.mod.model.iepform.FormTypes;
import senadi.gob.ec.mod.model.iepform.ModificacionApp;
import senadi.gob.ec.mod.model.iepform.ModificacionDAO;
import senadi.gob.ec.mod.model.iepform.PaymentReceipt;
import senadi.gob.ec.mod.model.iepform.Person;
import senadi.gob.ec.mod.model.iepform.PersonRenewal;
import senadi.gob.ec.mod.model.iepform.PersonRenewalName;
import senadi.gob.ec.mod.model.iepform.RenewalForm;
import senadi.gob.ec.mod.model.iepform.RenewalFormDAO;
import senadi.gob.ec.mod.model.iepform.Types;
import senadi.gob.ec.mod.model.iepicas.NotificationsDAO;
import senadi.gob.ec.mod.model.transf.TituloCancelado;
import senadi.gob.ec.mod.model.transf.TituloCanceladoDAO;

/**
 *
 * @author Michael Yanangómez
 */
public class Controlador {

    public void banderaCertificadosEmitidos() {
        RenovacionDAO rd = new RenovacionDAO(null);
        List<String> renovaciones = rd.getRenovacionesCertificadoEmitido();

        for (int i = 0; i < renovaciones.size(); i++) {
            Renovacion renovacion = getRenovacionesBySolSenadi(renovaciones.get(i)).get(0);
            renovacion.setCertificadoEmitido(true);
            rd = new RenovacionDAO(renovacion);
            try {
                rd.update();
                System.out.println(i + ": " + renovacion.getSolicitudSenadi() + " editado");
            } catch (Exception ex) {
                Logger.getLogger(RenovacionDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("--------------------Evento terminado-----------------");
    }

    public void banderaNotificacionesEmitidas() {
        NotificadaDAO nd = new NotificadaDAO(null);
        List<String> notificadas = nd.getNotificadasNotificacionEmitido();

        for (int i = 0; i < notificadas.size(); i++) {
            Notificada notificada = getNotificadasBySolSenadi(notificadas.get(i)).get(0);
            notificada.setNotificacionEmitida(true);
            nd = new NotificadaDAO(notificada);
            try {
                nd.update();
                System.out.println(i + ": " + notificada.getSolicitud() + " editado");
            } catch (Exception ex) {
                Logger.getLogger(RenovacionDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("--------------------Evento terminado-----------------");
    }

    public List<Renovacion> getRenovaciones() {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.buscarTodos();
    }

    public List<Notificada> getNotificadas() {
        NotificadaDAO nd = new NotificadaDAO(null);
        return nd.buscarTodos();
    }

    public List<Desistida> getDesistidas() {
        DesistidaDAO dd = new DesistidaDAO(null);
        return dd.buscarTodos();
    }

    public List<Caducada> getCaducadas() {
        CaducadaDAO cd = new CaducadaDAO(null);
        return cd.buscarTodos();
    }

    public List<Caducada> getCaducadasByCriteria(String texto) {
        CaducadaDAO cd = new CaducadaDAO(null);
        return cd.getCaducadasCriteria(texto);
    }

    public List<Caducada> getCaducadasBySolSenadi(String solicitud) {
        CaducadaDAO cd = new CaducadaDAO(null);
        return cd.getCaducadasBySolSenadi(solicitud);
    }

    public Caducada getCaducadaById(Integer id) {
        CaducadaDAO cd = new CaducadaDAO(null);
        return cd.getCaducadaById(id);
    }

    public List<Notificada> getNotificadasByCriteria(String texto) {
        NotificadaDAO nd = new NotificadaDAO(null);
        return nd.getNotificadasCriteria(texto);
    }

    public List<Desistida> getDesistidasByCriteria(String texto) {
        DesistidaDAO dd = new DesistidaDAO(null);
        return dd.getDesistidasCriteria(texto);
    }

    public List<Renovacion> getRenovacionesByCriteria(String texto) {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.getRenovacionesByCriteria(texto);
    }

    public boolean saveRenovacion(Renovacion r) {
        RenovacionDAO rd = new RenovacionDAO(r);
        try {
            rd.persist();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al guardar renovación: " + ex);
            return false;
        }
    }

    public boolean saveModificacionApp(ModificacionApp modificacion) {
        ModificacionDAO md = new ModificacionDAO();
        return md.insertModificacionApp(modificacion);
    }

    public boolean saveNotificada(Notificada n) {
        NotificadaDAO nd = new NotificadaDAO(n);
        try {
            nd.persist();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al guardar notificada: " + ex);
            return false;
        }
    }

    public boolean saveAbandono(Abandono abandono) {
        AbandonoDAO ad = new AbandonoDAO(abandono);
        try {
            ad.persist();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al guardar abandono: " + ex);
            return false;
        }
    }

    public Integer getNextNumeroAbandono(Date fechaAbandono) {
        AbandonoDAO ad = new AbandonoDAO(null);
        return ad.getNextNumeroAbandono(fechaAbandono);
    }

    public boolean saveDesistida(Desistida d) {
        DesistidaDAO dd = new DesistidaDAO(d);
        try {
            dd.persist();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al guardar desistida: " + ex);
            return false;
        }
    }

    public boolean saveCaducada(Caducada c) {
        CaducadaDAO cd = new CaducadaDAO(c);
        try {
            cd.persist();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al guardar caducada: " + ex);
            return false;
        }
    }

    public int getNextNumeroCertificado(Date fechaCertificado) {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.getNextNumeroCertificado(fechaCertificado);
    }

    public int getNextNumeroNotificacion(Date fechaElaboraNotificacion) {
        NotificadaDAO nd = new NotificadaDAO(null);
        return nd.getNextNumeroNotificacion(fechaElaboraNotificacion);
    }

    public boolean updateRenovacion(Renovacion r) {
        RenovacionDAO rd = new RenovacionDAO(r);
        try {
            rd.update();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al editar renovación: " + ex);
            return false;
        }
    }

    public boolean updateNotificada(Notificada n) {
        NotificadaDAO nd = new NotificadaDAO(n);
        try {
            nd.update();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al editar notificada: " + ex);
            return false;
        }
    }

    public boolean updateDesistida(Desistida d) {
        DesistidaDAO nd = new DesistidaDAO(d);
        try {
            nd.update();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al editar desistida: " + ex);
            return false;
        }
    }

    public boolean updateCaducada(Caducada c) {
        CaducadaDAO cd = new CaducadaDAO(c);
        try {
            cd.update();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al editar caducada: " + ex);
            return false;
        }
    }

    public boolean existsUser(String login, String clave) {
        UsuarioDAO ud = new UsuarioDAO(null);
        return ud.existsUser(login, clave);
    }

    public Usuario getUsuario(String login, String clave) {
        UsuarioDAO ud = new UsuarioDAO(null);
        return ud.getUsuario(login, clave);
    }

    public boolean getUsuarioRol(int idRol, int idUser) {
        UsuarioDAO ud = new UsuarioDAO(null);
        return ud.getUsuarioRol(idRol, idUser);
    }

    public boolean removeRenovacion(Renovacion renovacion) {
        RenovacionDAO rd = new RenovacionDAO(renovacion);
        try {
            if (!rd.getEntityManager().contains(renovacion)) {
                System.out.println("merge renovación");
                renovacion = rd.getEntityManager().merge(renovacion);
                rd = new RenovacionDAO(renovacion);
            }
            rd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover renovación: " + ex);
            return false;
        }
    }

    public boolean removeNotificada(Notificada notificada) {
        NotificadaDAO nd = new NotificadaDAO(notificada);
        try {
            if (!nd.getEntityManager().contains(notificada)) {
                System.out.println("merge notificada");
                notificada = nd.getEntityManager().merge(notificada);
                nd = new NotificadaDAO(notificada);
            }

            nd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover notificada: " + ex);
            return false;
        }
    }

    public boolean removeDesistida(Desistida desistida) {
        DesistidaDAO dd = new DesistidaDAO(desistida);
        try {

            dd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover desistida: " + ex);
            return false;
        }
    }

    public boolean removeCaducada(Caducada caducada) {
        CaducadaDAO cd = new CaducadaDAO(caducada);
        try {
            cd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover caducada: " + ex);
            return false;
        }
    }
    
    public boolean removeAbandono(Abandono abandono) {
        AbandonoDAO ad = new AbandonoDAO(abandono);
        try {
            ad.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover caducada: " + ex);
            return false;
        }
    }

    public int getNextResCaducadaNumber(Date fechaProvidencia) {
        CaducadaDAO cd = new CaducadaDAO(null);
        return cd.getNextResNumber(fechaProvidencia);
    }

    public List<Desistida> getDesistidasBySolSenadi(String sol_senadi) {
        DesistidaDAO nd = new DesistidaDAO(null);
        return nd.getDesistidasBySolSenadi(sol_senadi);
    }

    public List<Notificada> getNotificadasBySolSenadi(String sol_senadi) {
        NotificadaDAO nd = new NotificadaDAO(null);
        return nd.getNotificadasBySolSenadi(sol_senadi);
    }

    public Notificada getNotificadaBySolSenadi(String solicitud) {
        NotificadaDAO nd = new NotificadaDAO(null);
        return nd.getNotificadaBySolSenadi(solicitud);
    }

    public List<Renovacion> getRenovacionesBySolSenadi(String sol_senadi) {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.getRenovacionesBySolSenadi(sol_senadi);
    }

    public boolean validarExistenciaRenovacion(String solicitudSenadi) {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.validarExistenciaRenovacion(solicitudSenadi);
    }

    public boolean validarExistsRen(Renovacion ren) {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.validarExistenciaRen(ren);
    }

    public boolean validarExistenciaNotificada(String solicitudSenadi) {
        NotificadaDAO rd = new NotificadaDAO(null);
        return rd.validarExistenciaNotificada(solicitudSenadi);
    }

    public boolean validarExistsNot(Notificada not) {
        NotificadaDAO rd = new NotificadaDAO(null);
        return rd.validarExistenciaNot(not);
    }

    public boolean validarExistenciaDesistida(String solicitudSenadi) {
        DesistidaDAO rd = new DesistidaDAO(null);
        return rd.validarExistenciaDesistida(solicitudSenadi);
    }

    public boolean validarExistsDesistida(Desistida des) {
        DesistidaDAO dd = new DesistidaDAO(null);
        return dd.validarExistenciaDes(des);
    }

    public boolean validarExistenciaCaducada(String solicitudSenadi) {
        CaducadaDAO rd = new CaducadaDAO(null);
        return rd.validarExistenciaCaducada(solicitudSenadi);
    }

    public String validarExistenciaTramite(String tramite) {
        String msg = "";
        if (validarExistenciaRenovacion(tramite)) {
            msg = "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN RENOVACIONES";
        } else if (validarExistenciaNotificada(tramite)) {
            msg = "EL TRÁMITE ESTÁ EN LA PESTAÑA DE NOTIFICADOS";
        } else if (validarExistenciaDesistida(tramite)) {
            msg = "EL TRÁMITE ESTÁ EN LA PESTAÑA DE DESISTIDAS";
        } else if (validarExistenciaCaducada(tramite)) {
            msg = "EL TRÁMITE ESTÁ EN LA PESTAÑA DE CADUCADAS-NEGADAS";
        } else if (validarExistenciaAbandono(tramite)) {
            msg = "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN ABANDONOS";
        }
        return msg;
    }

    public boolean validarExistenciaAbandono(Abandono a) {
        AbandonoDAO td = new AbandonoDAO(null);
        return td.validarExistenciaAbandono(a);
    }

    public boolean validarExistenciaAbandono(String solicitudSenadi) {
        AbandonoDAO rd = new AbandonoDAO(null);
        return rd.validarExistenciaAbandono(solicitudSenadi);
    }
    
    public boolean updateAbandono(Abandono a) {
        AbandonoDAO ad = new AbandonoDAO(a);
        try {
            ad.update();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al editar abandono: " + ex);
            return false;
        }
    }

    public boolean validarExistsCaducada(Caducada cad) {
        CaducadaDAO cc = new CaducadaDAO(null);
        return cc.validarExistenciaCad(cad);
    }

    public List<Historial> getHistorialBySolicitudSenadi(String solicitudSenadi) {
        HistorialDAO hd = new HistorialDAO(null);
        return hd.getHistorialBySolicitudSenadi(solicitudSenadi);
    }

    public boolean saveHistorial(Historial historial) {
        HistorialDAO hd = new HistorialDAO(historial);
        try {
            hd.persist();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al guardar Historial: " + ex);
            return false;
        }
    }

    public boolean saveHistorial(String estadoActual, String estadoAnterior, String solicitudSenadi, String accion, int user_id, String user) {
        Historial historial = new Historial();
        historial.setEstadoActual(estadoActual);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setSolicitudSenadi(solicitudSenadi);
        historial.setAccion(accion);
        historial.setFechaModificacion(Operaciones.getCurrentTimeStamp());
        historial.setUserId(user_id);
        historial.setUsuario(user);
        if (saveHistorial(historial)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updateUsuario(Usuario usuario) {
        UsuarioDAO ud = new UsuarioDAO(usuario);
        try {
            ud.update();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al actualizar usuario: " + ex);
            return false;
        }
    }

    public List<Renovacion> getRenovacionesByDate(Date ini, Date fin) {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.getRenovacionesByDate(ini, fin);
    }

    public List<Renovacion> getRenovacionesByFechaCertificado(Date ini, Date fin) {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.getRenovacionesByCertificateDate(ini, fin);
    }

    public List<Notificada> getNotificacionesByDate(Date ini, Date fin) {
        NotificadaDAO nd = new NotificadaDAO(null);
        return nd.getNotificadasByDate(ini, fin);
    }

    public List<Notificada> getNotificacionesByFechaNotificacion(Date ini, Date fin) {
        NotificadaDAO nd = new NotificadaDAO(null);
        return nd.getNotificadasByFechaNotificacion(ini, fin);
    }

    public List<Desistida> getDesistidasByDate(Date ini, Date fin) {
        DesistidaDAO dd = new DesistidaDAO(null);
        return dd.getDesistidasByDate(ini, fin);
    }

    public List<Caducada> getCaducadasByDate(Date ini, Date fin) {
        CaducadaDAO cd = new CaducadaDAO(null);
        return cd.getCaducadasByDate(ini, fin);
    }

    public RenewalForm findRenewalFormsByApplicationNumber(String applicationNumber) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getRenewalFormsByApplication(applicationNumber);
    }

    public Types getTypes(Integer id) {
        RenewalFormDAO rd = new RenewalFormDAO();
        FormTypes ft = rd.getFormTypesById(id);
        if (ft.getId() != null) {
            Types t = rd.getTypesById(ft.getTypeId());
            return t;
        } else {
            return new Types();
        }
    }

    public PaymentReceipt getPaymentReceiptById(Integer id) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getPaymentReceiptById(id);
    }

    public HallmarkForms getHallmarkForm(Integer debug_id) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getHallmarkForms(debug_id);
    }

    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoByNumeroTitulo(String numeroTitulo) {
        PpdiTituloDAO pd = new PpdiTituloDAO(null);
        return pd.getPpdiTituloSignoDistintivoByNumeroTitulo(numeroTitulo);
    }

    public PpdiSolicitudSignoDistintivo getPpdiSolicitudSignoDistintivoByExpedient(String expedient) {
        PpdiSignoDAO ps = new PpdiSignoDAO(null);
        return ps.getPpdiSolicitudSignoDistintivoByExpedient(expedient);
    }

    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(Integer codigoSolicitudSigno) {
        PpdiTituloDAO pd = new PpdiTituloDAO(null);
        return pd.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(codigoSolicitudSigno);
    }

    public Integer getCasilleroSenadi(Integer ownerId) {
        RenewalFormDAO td = new RenewalFormDAO();
        return td.getCasilleroByOwner(ownerId);
    }

    public Person getTitularActual(Integer idRenewal) {
        RenewalFormDAO rd = new RenewalFormDAO();
        PersonRenewalName prn = rd.getPersonRenewalNameByIdRenewal(idRenewal);
        if (prn.getRenewalFormId() != null) {
            return rd.getPersonById(prn.getPersonId());
        } else {
            return new Person();
        }
    }

    public Person getFirstPersonRenewalTypeByIdRenewal(Integer idRenewal, String type) {
        RenewalFormDAO rd = new RenewalFormDAO();
        List<PersonRenewal> pr = rd.getPersonRenewalTypeByIdRenewal(idRenewal, type);
        if (!pr.isEmpty()) {
            PersonRenewal pra = pr.get(0);
            return rd.getPersonById(pra.getPersonId());
        } else {
            return new Person();
        }
    }

    public String getNamesPersonRenewalTextTypeByIdRenewal(Integer idRenewal, String type) {
        RenewalFormDAO rd = new RenewalFormDAO();
        List<PersonRenewal> pr = rd.getPersonRenewalTypeByIdRenewal(idRenewal, type);
        String nombres = "";
        if (!pr.isEmpty()) {
            for (int i = 0; i < pr.size(); i++) {
                nombres += rd.getPersonById(pr.get(i).getPersonId()).getName();
            }
            return nombres;
        } else {
            return nombres;
        }
    }

    public boolean saveDelegado(Delegado dele) {
        DelegadoDAO dd = new DelegadoDAO(dele);
        try {
            dd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar delegado " + ex);
            return false;
        }
    }

    public boolean saveDelegacion(Delegacion dele) {
        DelegacionDAO dd = new DelegacionDAO(dele);
        try {
            dd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar delegación " + ex);
            return false;
        }
    }

    public boolean saveResolucion(Resolucion resol) {
        ResolucionDAO rd = new ResolucionDAO(resol);
        try {
            rd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar resolución " + ex);
            return false;
        }
    }

    public boolean saveSecretario(Secretario secre) {
        SecretarioDAO sd = new SecretarioDAO(secre);
        try {
            sd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar secretario " + ex);
            return false;
        }
    }

    public boolean updateDelegado(Delegado dele) {
        DelegadoDAO dd = new DelegadoDAO(dele);
        try {
            dd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar delegado " + ex);
            return false;
        }
    }

    public boolean updateDelegacion(Delegacion dele) {
        DelegacionDAO dd = new DelegacionDAO(dele);
        try {
            dd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar delegación " + ex);
            return false;
        }
    }

    public boolean updateResolucion(Resolucion resol) {
        ResolucionDAO rd = new ResolucionDAO(resol);
        try {
            rd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar resolución " + ex);
            return false;
        }
    }

    public boolean updateSecretario(Secretario resol) {
        SecretarioDAO sd = new SecretarioDAO(resol);
        try {
            sd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar secretario " + ex);
            return false;
        }
    }

    public boolean activeADelegado(Delegado delegado, List<Delegado> delegados) {
        for (int i = 0; i < delegados.size(); i++) {
            if (!delegados.get(i).getId().equals(delegado.getId())) {
                delegados.get(i).setEstado(false);
                updateDelegado(delegados.get(i));
            }
        }
        if (updateDelegado(delegado)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean removeDelegado(Delegado dele) {
        DelegadoDAO dd = new DelegadoDAO(dele);
        try {
            dd.remove();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al remover delegado " + ex);
            return false;
        }
    }

    public boolean activeADelegacion(Delegacion delegacion, List<Delegacion> delegaciones) {
        for (int i = 0; i < delegaciones.size(); i++) {
            if (!delegaciones.get(i).getId().equals(delegacion.getId())) {
                delegaciones.get(i).setActivo(false);
                updateDelegacion(delegaciones.get(i));
            }
        }
        if (updateDelegacion(delegacion)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean removeDelegacion(Delegacion dele) {
        DelegacionDAO dd = new DelegacionDAO(dele);
        try {
            dd.remove();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al remover delegación " + ex);
            return false;
        }
    }

    public boolean activeAResolucion(Resolucion resolucion, List<Resolucion> resoluciones) {
        for (int i = 0; i < resoluciones.size(); i++) {
            if (!resoluciones.get(i).getId().equals(resolucion.getId())) {
                resoluciones.get(i).setActivo(false);
                updateResolucion(resoluciones.get(i));
            }
        }
        if (updateResolucion(resolucion)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean activeASecretario(Secretario secre, List<Secretario> secretarios) {
        for (int i = 0; i < secretarios.size(); i++) {
            if (!secretarios.get(i).getId().equals(secre.getId())) {
                secretarios.get(i).setEstado(false);
                updateSecretario(secretarios.get(i));
            }
        }
        if (updateSecretario(secre)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean removeResolucion(Resolucion resol) {
        ResolucionDAO rd = new ResolucionDAO(resol);
        try {
            rd.remove();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al remover resolución: " + ex);
            return false;
        }
    }

    public boolean removeSecretario(Secretario secre) {
        SecretarioDAO sd = new SecretarioDAO(secre);
        try {
            if (!sd.getEntityManager().contains(secre)) {
                System.out.println("merge secretario");
                secre = sd.getEntityManager().merge(secre);
                sd = new SecretarioDAO(secre);
            }
            sd.remove();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al remover secretario: " + ex);
            return false;
        }
    }

    public boolean validarDelegadoActivo() {
        DelegadoDAO dd = new DelegadoDAO(null);
        return dd.validarDelegadoActivo();
    }

    public boolean validarDelegacionActivo() {
        DelegacionDAO dd = new DelegacionDAO(null);
        return dd.validarDelegacionActiva();
    }

    public boolean validarSecretarioActivo() {
        SecretarioDAO sd = new SecretarioDAO(null);
        return sd.validarSecretarioActiva();
    }

    public boolean validarResolucionActiva(String tipo) {
        ResolucionDAO rd = new ResolucionDAO(null);
        return rd.validarResolucionActiva(tipo);
    }

    public Delegado getDelegadoActivo() {
        DelegadoDAO dd = new DelegadoDAO(null);
        return dd.getDelegadoActivo();
    }

    public Delegacion getDelegacionActiva() {
        DelegacionDAO dd = new DelegacionDAO(null);
        return dd.getDelegacionActiva();
    }

    public Resolucion getResolucionActiva(String tipo) {
        ResolucionDAO rd = new ResolucionDAO(null);
        return rd.getResolucionActiva(tipo);
    }

    public Secretario getSecretarioActivo() {
        SecretarioDAO sd = new SecretarioDAO(null);
        return sd.getSecretarioActiva();
    }

    public List<Delegado> getAllDelegados() {
        DelegadoDAO dd = new DelegadoDAO(null);
        return dd.buscarTodos();
    }

    public List<Delegacion> getAllDelegaciones() {
        DelegacionDAO dd = new DelegacionDAO(null);
        return dd.buscarTodos();
    }

    public List<Resolucion> getResolucionesByTipo(String tipo) {
        ResolucionDAO rd = new ResolucionDAO(null);
        return rd.getResolucionesByTipo(tipo);
    }

    public List<Secretario> getSecretarios() {
        SecretarioDAO sd = new SecretarioDAO(null);
        return sd.getSecretarios();
    }

    public List<Renovacion> loadRenovacionesFromRenewals(List<RenewalForm> renewals) {
        List<Renovacion> renovaciones = new ArrayList<>();
        for (int i = 0; i < renewals.size(); i++) {
            RenewalForm rf = renewals.get(i);
            Renovacion renovacion = new Renovacion();
            renovacion.setFechaPresentacion(rf.getApplicationDate());
            renovacion.setIdRenewalForm(rf.getId());
            renovacion.setSolicitudSenadi(rf.getApplicationNumber());
            renovacion.setRegistroNo(rf.getTransactionNumber());

            renovacion.setTacNJ("MARCA");

            renovaciones.add(renovacion);
        }
        return renovaciones;
    }

    public List<RenewalForm> getRenewalsRezagoBytType(String type, String transactionalMotive) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getRenewalsRezagoBytType(type, transactionalMotive);
    }

    public List<RenewalForm> getRenewalsRezagoBytTypeAndCriterio(String type, String transactionalMotive, String criterio) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getRenewalsResagoBytTypeAndCriteria(type, transactionalMotive, criterio);
    }

    public List<RenewalForm> getRenewalsRezagoBytTypeAndFecha(String type, String transactionalMotive, Date ini, Date fin) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getRenewalsResagoBytTypeAndFecha(type, transactionalMotive, ini, fin);
    }

    public boolean validarExistenciaUploadNotificacion(String solicitud) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.validarExistenciaUploadNotificacion(solicitud);
    }

    public boolean validarExistenciaUploadNotificacion(String solicitud, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.validarExistenciaUploadNotificacion(solicitud, estado);
    }

    public boolean validarExistenciaUploadNotificacionByDocument(String documento) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.validarExistenciaUploadNotificacionByDocument(documento);
    }

    public boolean validarExistenciaUploadNotificacionByDocument(String documento, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.validarExistenciaUploadNotificacionByDocument(documento, estado);
    }

    public boolean saveUploadNotificacion(UploadNotificacion un) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(un);
        try {
            ud.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Hubo un error al guardar UploadNotificacion: " + ex);
            return false;
        }
    }

    public boolean updateUploadNotificacion(UploadNotificacion un) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(un);
        try {
            ud.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Hubo un error al editar UploadNotificacion: " + ex);
            return false;
        }
    }

    public List<UploadNotificacion> getNotificacionesByEstado(boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.getNotificacionesByEstado(estado);
    }

    public List<UploadNotificacion> getUploadNotificacionByTramite(String criterio, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.getUploadNotificacionByTramite(criterio, estado);
    }

    public List<UploadNotificacion> getUploadNotificacionByCriterio(String criterio, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.getUploadNotificacionByCriterio(criterio, estado);
    }

    public List<UploadNotificacion> getUploadNotificacionByDate(Date start, Date end, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.getUploadNotificacionByDate(start, end, estado);
    }

    public boolean downLockerNotifications(UploadNotificacion un) {
        NotificationsDAO nd = new NotificationsDAO();
        return nd.downLockerNotifications(un);
    }

    public String getStringTramitesRenovacion(List<Renovacion> rens) {
        String tramites = "";
        for (int i = 0; i < rens.size(); i++) {
            tramites += "'" + rens.get(i).getSolicitudSenadi() + "',";
        }
        tramites = tramites.substring(0, tramites.length() - 1);
        return tramites;
    }

    public List<RenewalForm> getRenewalFormsNews() {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getRenewalFormsnews();
    }

    public boolean existsTituloCanceladoByTituloAndExpediente(String numTitulo, String expediente) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.existsTituloCanceladoByTituloAndExpediente(numTitulo, expediente);
    }

    public boolean existsTituloCanceladoByTituloAndDenominacion(String numTitulo, String denominacion) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.existsTituloCanceladoByTituloAndDenominacion(numTitulo, denominacion);
    }

    public TituloCancelado getTituloCanceladoByTituloAndExpediente(String numTitulo, String expediente) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.getTituloCanceladoByTituloAndExpediente(numTitulo, expediente);
    }

    public TituloCancelado getTituloCanceladoByTituloAndDenoninacion(String numTitulo, String denominacion) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.getTituloCanceladoByTituloAndDenoninacion(numTitulo, denominacion);
    }

    public List<UploadNotificacion> getUploadNotificacionBySolicitud(String solicitud, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.getUploadNotificacionBySolicitud(solicitud, estado);
    }

    public boolean anularDocumentoRenovacion(UploadNotificacion un, String tramite, String user) {
//        RenewalForm rf = getRenewalFormsByApplicationNumber(tramite);

        String rutadoccas = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + un.getCasillero() + "/" + un.getDocumento();

        Renovacion re = getRenovacionBySolSenadi(tramite);
        if (re.getId() != null) {
            int conf = Operaciones.esCertificado(rutadoccas, "CERTIFICADO DE RENOVACIÓN");
            if (conf == 1) {
                re.setCertificadoEmitido(false);
                saveHistorial("CERTIFICADO", "CERTIFICADO", tramite, "DOC CERTIFICADO ANULADO " + un.getDocumento(), 0, user);
                System.out.println("certificado para " + tramite + " anulado");
                if (updateRenovacion(re)) {
                    return true;
                }
            } else if (conf == 0) {
                Notificada not = getNotificadaBySolSenadi(tramite);
                if (not.getId() != null) {

                    not.setNotificacionEmitida(false);
                    saveHistorial("NOTIFICACION", "NOTIFICACION", tramite, "DOC NOTIFICACION ANULADA " + un.getDocumento(), 0, user);
                    System.out.println("notificación para " + tramite + " anulado");

                    if (updateNotificada(not)) {
                        return true;
                    }

                }

            }
        } else {
            Notificada not = getNotificadaBySolSenadi(tramite);
            if (not.getId() != null) {
                not.setNotificacionEmitida(false);
                saveHistorial("NOTIFICACION", "NOTIFICACION", tramite, "DOC NOTIFICACION ANULADA " + un.getDocumento(), 0, user);
                System.out.println("notificación para " + tramite + " anulado");

                if (updateNotificada(not)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Renovacion getRenovacionBySolSenadi(String solicitud) {
        RenovacionDAO r = new RenovacionDAO(null);
        return r.getRenovacionBySolSenadi(solicitud);
    }

    public File concatenarPdfDoFile(List<Object[]> pdfBytes, String nombre) throws Exception {
// Path del fichero temporal
        final String pathFile = nombre;

// Creamos el fichero pdf necesario.
        FileOutputStream file = new FileOutputStream(pathFile);

// Objeto reader para añadir los pdf.
        PdfReader reader = null;

// Objeto para concatenar los pdf.
        PdfCopyFields copy = new PdfCopyFields(file);

// tamaño total de bytes
        int tam = 0;

// Recorremos los bytes y los vamos concatenando.
        int cont = 1;
        for (Object[] pdfByte : pdfBytes) {
            tam += pdfByte.length;
            reader = new PdfReader((byte[]) pdfByte[0]);
            copy.addDocument(reader);
            System.out.println(cont++ + ": " + pdfByte[1]);
        }

        copy.close();
        file.close();

        File f = new File(pathFile);

        return f;
    }

    public ModificacionApp getModificacionApp(String solicitud) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getModificacionApp(solicitud);
    }

    public void updateRezago() {

        List<RenewalForm> renewals = getRenewalsRezagoBytType("RENOVACION", "21");
        int n = renewals.size();
        System.out.println("Iniciando el rezago " + n);
        for (int i = 0; i < renewals.size(); i++) {
            RenewalForm ren = renewals.get(i);
            if (ren.getApplicationNumber() != null && !ren.getApplicationNumber().trim().isEmpty()) {
                boolean aviso = false;
                if (validarExistenciaRenovacion(ren.getApplicationNumber())) {
                    System.out.print((i + 1) + "/" + n + ": Renovación: " + ren.getApplicationNumber());
                    aviso = true;
                } else if (validarExistenciaNotificada(ren.getApplicationNumber())) {
                    System.out.print((i + 1) + " Notificada: " + ren.getApplicationNumber());
                    aviso = true;
                } else if (validarExistenciaDesistida(ren.getApplicationNumber())) {
                    System.out.print((i + 1) + " Desistida: " + ren.getApplicationNumber());
                    aviso = true;
                } else if (validarExistenciaCaducada(ren.getApplicationNumber())) {
                    System.out.print((i + 1) + " Caducada: " + ren.getApplicationNumber());
                    aviso = true;
                } else {
                    aviso = false;
                    System.out.print((i + 1) + ": " + ren.getApplicationNumber() + " si está bien");
                }

                if (aviso) {
                    ModificacionDAO md = new ModificacionDAO();
                    ModificacionApp moaux = md.getModificacionApp(ren.getApplicationNumber());

                    if (moaux.getId() == null) {
                        ModificacionApp map = new ModificacionApp();
                        map.setDenominacion("");
                        map.setFecha(new Timestamp(new Date().getTime()));
                        map.setObservacion("");
                        map.setRegistro(ren.getExpedient());
                        map.setSolicitud(ren.getApplicationNumber());
                        map.setTipo("RENOVACION");
                        map.setUsuario("migracion");
                        map.setModo("MARCA");
                        map.setActivo(false);
                        if (saveModificacionApp(map)) {
                            System.out.print(" guardado en modapp");
                        } else {
                            System.out.print(" no se guardó error");
                        }
                    } else {
                        System.out.print(" no entra");
                    }
                }
                System.out.println("");
            }
        }

        System.out.println("Terminado el análisis");
    }

    public List<Renovacion> getAllRenovaciones() {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.getAllRenovaciones();
    }

    public ModificacionApp getModificacionAppBySolicitudAndTipo(String solicitud, String tipo) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getModificacionAppBySolicitudAndTipo(solicitud, tipo);
    }

    public List<Renovacion> getAllRenovacionesSistema() {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.getAllRenovacionesSistema();
    }

    /**
     * Se depuran las renovaciones que están en el sistema, pero no están en
     * modificaciones app (rezago)*
     */
    public void depurar() {
        List<Renovacion> renovaciones = getAllRenovacionesSistema();
        int size = renovaciones.size();
        System.out.println("size: " + size);
        for (int i = 0; i < renovaciones.size(); i++) {
            Renovacion r = renovaciones.get(i);
            ModificacionApp mapp = getModificacionAppBySolicitudAndTipo(r.getSolicitudSenadi().trim().toUpperCase(), "RENOVACION");
            System.out.print("(" + (i + 1) + "/" + size + ") " + r.getSolicitudSenadi() + " -- ");
            if (mapp.getId() == null) {
                ModificacionApp map = new ModificacionApp();
                map.setDenominacion("");
                map.setFecha(new Timestamp(new Date().getTime()));
                map.setObservacion("");
                map.setRegistro(r.getRegistroNo());
                map.setSolicitud(r.getSolicitudSenadi().toUpperCase().trim());
                map.setTipo("RENOVACION");
                map.setUsuario("migracion");
                map.setModo("MARCA");
                map.setActivo(false);
                if (saveModificacionApp(map)) {
                    System.out.println("guardado en modapp *******");
                } else {
                    System.out.println("no se guardó error");
                }
            } else {
                System.out.println("ya existe en modificaciones app");
            }
        }
    }

    public boolean saveModificacionApp(String denominacion, String registro, String solicitud, String tipo, String usuario) {
        ModificacionApp mapp = new ModificacionApp();
        mapp.setDenominacion(denominacion != null ? denominacion : "");
        mapp.setFecha(new Timestamp(new Date().getTime()));
        mapp.setObservacion("rezago pasado");
        mapp.setRegistro(registro);
        mapp.setSolicitud(solicitud);
        mapp.setTipo(tipo);
        mapp.setUsuario(usuario);
        mapp.setModo("MARCA");
        mapp.setActivo(true);
        return saveModificacionApp(mapp);
    }

    public List<Abandono> getAbandonos() {
        AbandonoDAO nd = new AbandonoDAO(null);
        return nd.buscarTodos();
    }

    public List<Abandono> getAbandonoByCriteria(String text) {
        AbandonoDAO nd = new AbandonoDAO(null);
        return nd.getAbandonoByCriteria(text);
    }

    public List<Abandono> getAbandonosByFecha(Date ini, Date fin) {
        AbandonoDAO td = new AbandonoDAO(null);
        return td.getAbandonoByFecha(ini, fin);
    }

    public Abandono getAbandonoBySolSenadi(String solicitud) {
        AbandonoDAO t = new AbandonoDAO(null);
        return t.getAbandonoBySolicitud(solicitud);
    }
    
    public List<Abandono> getAbandonosBySolSenadi(String solicitud){
        AbandonoDAO ad = new AbandonoDAO(null);
        return ad.getAbandonosBySolSenadi(solicitud);
    }

    public void refreshAbandono(Abandono abandono) {
        AbandonoDAO ad = new AbandonoDAO(null);
        ad.getEntityManager().refresh(abandono);
    }

    public RenewalForm getRenewalFormsByApplicationNumber(String applicationNumber) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getRenewalFormsByApplication(applicationNumber);
    }
    
    public Integer getCasilleroSenadiByOwnerId(Integer ownerId) {
        RenewalFormDAO td = new RenewalFormDAO();
        return td.getCasilleroByOwner(ownerId);
    }
    
    public String buscarCasilleroBySolicitud(String solicitud) {
        RenewalForm aux = getRenewalFormsByApplicationNumber(solicitud);
        String casillero = "";
        if (aux.getId() != null) {
            casillero = getCasilleroSenadiByOwnerId(aux.getOwnerId()) + "";
        }
        return casillero;

    }
}
