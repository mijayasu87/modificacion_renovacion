/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Historial;

/**
 *
 * @author Michael Yanangómez
 */
public class HistorialDAO extends DAOAbstract<Historial> {
    public HistorialDAO(Historial h){
        super(h);
    }

    @Override
    public List<Historial> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select h from Historial h");
        return query.getResultList();
    }
    
    public List<Historial> getHistorialBySolicitudSenadi(String solicitudSenadi){
        Query query = this.getEntityManager().createQuery("Select h from Historial h where h.solicitudSenadi = '"+solicitudSenadi+"'");
        return query.getResultList();
    }
}
