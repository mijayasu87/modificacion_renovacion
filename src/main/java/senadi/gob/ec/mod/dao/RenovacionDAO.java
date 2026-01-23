/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author Michael Yanangómez
 */
public class RenovacionDAO extends DAOAbstract<Renovacion> {

    public RenovacionDAO(Renovacion r) {
        super(r);
    }

    @Override
    public List<Renovacion> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r ORDER BY r.fechaCertificado DESC, r.certificadoNo DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }
    
    public List<Renovacion> getAllRenovaciones(){
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r ORDER BY r.fechaCertificado DESC, r.certificadoNo DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public List<Renovacion> getAllRenovacionesSistema(){
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where r.solicitudSenadi like 'SENADI%' OR r.solicitudSenadi like 'IEPI%'  ORDER BY r.fechaCertificado DESC, r.certificadoNo DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<String> getRenovacionesCertificadoEmitido() {
        System.out.println("---------------------Empezando el reconocimiento------------------");
        String sql = "Select DISTINCT(r.solicitud_senadi) from "
                + "renovacion as r "
                + "left join notificacion_casillero as n on r.solicitud_senadi = n.solicitud "
                + "where n.estado_notificacion = 1 and documento like '%cert%' ";

        Query query = this.getEntityManager().createNativeQuery(sql);
        List<String> renovaciones = query.getResultList();

        System.out.println("--------------------Terminado el reconocimiento-----------------");
        System.out.println("Trámites encontrados: " + renovaciones.size());
        return renovaciones;
    }

    /*
    select r from 
renovacion as r
left join notificacion_casillero as n on r.solicitud_senadi = n.solicitud
where n.estado_notificacion = 1 and documento like '%cert%'
order by fecha_certificado desc, certificado_no desc */
    public List<Renovacion> getRenovacionesByCriteria(String text) {
        String certtext = "";
        try {
            int n = Integer.parseInt(text);
            certtext = "or r.certificadoNo = " + n;
        } catch (NumberFormatException ne) {
            certtext = "";
        }

        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where r.solicitudSenadi LIKE '%" + text + "%' " + certtext + " or r.denominacion LIKE '%" + text + "%' or r.titularActual LIKE '%" + text + "%' ORDER BY r.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public int getNextNumeroCertificado(Date fechaCertificado) {
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where r.id = (Select MAX(r1.id) from Renovacion r1)");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        Renovacion r = (Renovacion) query.getSingleResult();

        int yearCert = fechaCertificado.getYear() + 1900;
        int yearRen = r.getFechaCertificado().getYear() + 1900;

        if (yearRen == yearCert) {
            return r.getCertificadoNo() + 1;
        } else if (yearCert > yearRen) {
            return 1;
        } else {
            return -1;
        }
    }

    public List<Renovacion> getMaxRenovaciones(int limite) {
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r order by r.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<Renovacion> rens = query.setMaxResults(limite).getResultList();
        System.out.println("------------------------------------");
        System.out.println(rens.toString());
        System.out.println("------------------------------------");
        return rens;
    }

    public boolean validarExistenciaRenovacion(String solicitudSenadi) {
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where r.solicitudSenadi = '" + solicitudSenadi + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaRen(Renovacion ren) {
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where r.solicitudSenadi = '" + ren.getSolicitudSenadi() + "' and r.id != " + ren.getId());
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<Renovacion> getRenovacionesBySolSenadi(String sol_senadi) {
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where r.solicitudSenadi = '" + sol_senadi + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public Renovacion getRenovacionBySolSenadi(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where r.solicitudSenadi = '" + solicitud + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<Renovacion> renovaciones = query.getResultList();
        if (renovaciones.isEmpty()) {
            return new Renovacion();
        } else {
            return renovaciones.get(0);
        }

    }

    public List<Renovacion> getRenovacionesByDate(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where r.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + "or r.fechaCertificado BETWEEN '" + start + "' and '" + end + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Renovacion> getRenovacionesByCertificateDate(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where r.fechaCertificado BETWEEN '" + start + "' and '" + end + "' order by r.fechaCertificado desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

}
