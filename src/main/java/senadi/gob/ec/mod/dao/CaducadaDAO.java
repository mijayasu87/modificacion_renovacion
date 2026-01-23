/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Caducada;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author Michael Yanangómez
 */
public class CaducadaDAO extends DAOAbstract<Caducada> {

    public CaducadaDAO(Caducada c) {
        super(c);
    }

    @Override
    public List<Caducada> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from Caducada c ORDER BY c.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(200).getResultList();
    }

    public List<Caducada> getCaducadasCriteria(String text) {
        Query query = this.getEntityManager().createQuery("Select c from Caducada c where c.solicitudSenadi LIKE '%" + text + "%' or c.denominacion LIKE '%" + text + "%' or c.solicitante LIKE '%" + text + "%' ORDER BY c.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(100).getResultList();
    }
    
    public List<Caducada> getCaducadasBySolSenadi(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select c from Caducada c where c.solicitudSenadi = '" + solicitud + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public Caducada getCaducadaById(Integer id) {
        Query query = this.getEntityManager().createQuery("Select c from Caducada c where c.id = :id");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        query.setParameter("id", id);
        List<Caducada> caducadas = query.getResultList();
        if(caducadas.isEmpty()){
            return new Caducada();
        }else{
            return caducadas.get(0);
        }
    }
    

    public int getNextResNumber(Date fechaProvidencia) {
        Query query = this.getEntityManager().createQuery("Select c from Caducada c where c.id = (Select MAX(c1.id) from Caducada c1)");

        Caducada c = (Caducada) query.getSingleResult();

        int yearProv = fechaProvidencia.getYear() + 1900;
        int yearCad = c.getFechaProvidencia().getYear() + 1900;

        if (yearCad == yearProv) {
            return Integer.parseInt(c.getResolucion()) + 1;
        } else if (yearProv > yearCad) {
            return 1;
        } else {
            return -1;
        }
    }

    public boolean validarExistenciaCaducada(String solicitudSenadi) {
        Query query = this.getEntityManager().createQuery("Select c from Caducada c where c.solicitudSenadi = '" + solicitudSenadi + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaCad(Caducada cad) {
        Query query = this.getEntityManager().createQuery("Select c from Caducada c where c.solicitudSenadi = '" + cad.getSolicitudSenadi() + "' and c.id != " + cad.getId());
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<Caducada> getCaducadasByDate(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select c from Caducada c where (c.fechaProvidencia BETWEEN '"+start+"' and '"+end+"') or (c.fechaSolicitud BETWEEN '"+start+"' and '"+end+"')"
                + "or c.fechaDeSolicitud BETWEEN '"+start+"' and '"+end+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

}
