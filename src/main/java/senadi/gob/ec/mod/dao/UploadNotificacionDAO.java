/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.UploadNotificacion;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author micharesp
 */
public class UploadNotificacionDAO extends DAOAbstract<UploadNotificacion> {

    public UploadNotificacionDAO(UploadNotificacion t) {
        super(t);
    }

    @Override
    public List<UploadNotificacion> buscarTodos() {
        Query query = this.getEntityManager().createQuery("SELECT t FROM UploadNotificacion T ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<UploadNotificacion> getNotificacionesByEstado(boolean estado) {
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.estado = " + estado + " and u.activo = true");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public boolean validarExistenciaUploadNotificacionByDocument(String document) {
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.documento = '" + document + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaUploadNotificacion(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.solicitud = '" + solicitud + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaUploadNotificacionByDocument(String document, boolean estado) {
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.documento = '" + document + "' and u.estado = "
                + estado + " and u.activo = true");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaUploadNotificacion(String solicitud, boolean estado) {
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.solicitud = '" + solicitud + "' and u.estado = "
                + estado + " and u.activo = true");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<UploadNotificacion> getUploadNotificacionByTramite(String criterio, boolean estado) {
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.solicitud = '"
                + criterio + "' and u.estado = " + estado + " and u.activo = true order by u.id desc");
        return query.getResultList();
    }

    public List<UploadNotificacion> getUploadNotificacionByCriterio(String criterio, boolean estado) {
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.solicitud like '%"
                + criterio + "%' and u.estado = " + estado + " and u.activo = true order by u.id desc");
        return query.getResultList();
    }

    public List<UploadNotificacion> getUploadNotificacionByDate(Date start, Date end, boolean estado) {
        String ini = Operaciones.formatDate(start);
        String fin = Operaciones.formatDate(end);
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.activo = true and u.estado = " + estado + " and "
                + "u.fechaNotificacion BETWEEN '" + ini + "' and '" + fin + "' order by u.id desc");
//        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.estado = "+estado+" and u.fechaNotificacion BETWEEN '"+ini+"' and '"+fin+"'");
        return query.getResultList();
    }

    public List<UploadNotificacion> getUploadNotificacionBySolicitud(String solicitud, boolean estado) {
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.solicitud = '" + solicitud + "' "
                + "and u.estado = " + estado + " and u.activo = true order by u.id desc");
        return query.getResultList();
    }
}
