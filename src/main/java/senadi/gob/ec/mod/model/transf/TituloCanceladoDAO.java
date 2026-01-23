/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.model.transf;

import java.util.List;
import javax.persistence.Query;


/**
 *
 * @author micharesp
 */
public class TituloCanceladoDAO extends DAOAbstractTra<TituloCancelado>{
     public TituloCanceladoDAO(TituloCancelado t) {
        super(t);
    }

    @Override
    public List<TituloCancelado> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select t from TituloCancelado t order by t.fechaCancelacion desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }
    
    public boolean existsTituloCanceladoByTituloAndExpediente(String numTitulo, String expediente){
        Query query = this.getEntityManager().createQuery("Select t from TituloCancelado t where t.numeroTitulo = '"+numTitulo+"' and t.expediente = '"+expediente+"'");
         return !query.getResultList().isEmpty();
    }
    
    public boolean existsTituloCanceladoByTituloAndDenominacion(String numTitulo, String denominacion){
        Query query = this.getEntityManager().createQuery("Select t from TituloCancelado t where t.numeroTitulo = '"+numTitulo+"' and t.denominacion = '"+denominacion.replace("'", "''")+"'");
         return !query.getResultList().isEmpty();
    }
    
    public TituloCancelado getTituloCanceladoByTituloAndExpediente(String numTitulo, String expediente){
        Query query = this.getEntityManager().createQuery("Select t from TituloCancelado t where t.numeroTitulo = '"+numTitulo+"' and t.expediente = '"+expediente+"'");
        List<TituloCancelado> tits = query.getResultList();
        if(tits.isEmpty()){
            return new TituloCancelado();
        }else{
            return tits.get(0);
        }
    }
    
    public TituloCancelado getTituloCanceladoByTituloAndDenoninacion(String numTitulo, String denominacion){
        Query query = this.getEntityManager().createQuery("Select t from TituloCancelado t where t.numeroTitulo = '"+numTitulo+"' and t.denominacion = '"+denominacion.replace("'", "''")+"'");
        List<TituloCancelado> tits = query.getResultList();
        if(tits.isEmpty()){
            return new TituloCancelado();
        }else{
            return tits.get(0);
        }
    }
    
}
