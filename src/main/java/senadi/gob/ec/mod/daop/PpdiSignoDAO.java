/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.daop;

import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author michael
 */
public class PpdiSignoDAO extends DAOAbstractP<PpdiSolicitudSignoDistintivo> {

    public PpdiSignoDAO(PpdiSolicitudSignoDistintivo pd){
        super(pd);
    }
    
    @Override
    public List<PpdiSolicitudSignoDistintivo> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudSignoDistintivo p");
        return query.getResultList();
    }
    
    public PpdiSolicitudSignoDistintivo getPpdiSolicitudSignoDistintivoByExpedient(String expedient){
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudSignoDistintivo p where p.numeroExpediente = '"+expedient+"'");
        if(query.getResultList().isEmpty()){
            return new PpdiSolicitudSignoDistintivo();
        }else{
            return (PpdiSolicitudSignoDistintivo) query.getResultList().get(0);
        }
    }
    
}
