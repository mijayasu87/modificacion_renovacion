/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Desistida;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author Michael Yanangómez
 */
public class DesistidaDAO extends DAOAbstract<Desistida>{

    public DesistidaDAO(Desistida d){
        super(d);
    }
    
    @Override
    public List<Desistida> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select d from Desistida d ORDER BY d.id DESC");
        return query.getResultList();
    }
    
    public List<Desistida> getDesistidasCriteria(String text){
        Query query = this.getEntityManager().createQuery("Select d from Desistida d where d.solicitudSenadi LIKE '%"+text+"%' or d.denominacion LIKE '%"+text+"%' or d.titularActual LIKE '%"+text+"%' ORDER BY d.id DESC" );
        return query.getResultList();                
    }
    
    public boolean validarExistenciaDesistida(String solicitudSenadi) {
        Query query = this.getEntityManager().createQuery("Select d from Desistida d where d.solicitudSenadi = '" + solicitudSenadi + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaDes(Desistida des) {
        Query query = this.getEntityManager().createQuery("Select d from Desistida d where d.solicitudSenadi = '" + des.getSolicitudSenadi() + "' and d.id != " + des.getId());
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<Desistida> getDesistidasBySolSenadi(String sol_senadi) {
        Query query = this.getEntityManager().createQuery("Select d from Desistida d where d.solicitudSenadi = '"+sol_senadi+"'");
        return query.getResultList();
    }

    public List<Desistida> getDesistidasByDate(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select d from Desistida d where d.fechaPresentacion BETWEEN '"+start+"' and '"+end+"'");
        return query.getResultList();
    }
    
}
