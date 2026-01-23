/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Resolucion;

/**
 *
 * @author micharesp
 */
public class ResolucionDAO extends DAOAbstract<Resolucion>{
    
    public ResolucionDAO(Resolucion r){
        super(r);
    }

    @Override
    public List<Resolucion> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select r from Resolucion r");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public List<Resolucion> getResolucionesByTipo(String tipo){
        Query query = this.getEntityManager().createQuery("Select r from Resolucion r where r.tipo = '"+tipo+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public boolean validarResolucionActiva(String tipo) {
        Query query = this.getEntityManager().createQuery("Select d from Resolucion d where d.activo = true and d.tipo = '"+tipo+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public Resolucion getResolucionActiva(String tipo){
        Query query = this.getEntityManager().createQuery("Select d from Resolucion d where d.activo = true and d.tipo = '"+tipo+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return (Resolucion) query.getSingleResult();
    }
    
}
