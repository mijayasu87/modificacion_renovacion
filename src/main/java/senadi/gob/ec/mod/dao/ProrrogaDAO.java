/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Prorroga;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author michael
 */
public class ProrrogaDAO extends DAOAbstract<Prorroga> {

    public ProrrogaDAO(Prorroga p) {
        super(p);
    }

    @Override
    public List<Prorroga> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select n from Prorroga n ORDER BY n.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<Prorroga> getProrrogaByCriteria(String text) {
        Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.solicitud LIKE '%" + text + "%' or n.denominacion LIKE '%" + text + "%' "
                + "or n.titularActual LIKE '%" + text + "%' ORDER BY n.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Prorroga> getProrrogaByFecha(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.fechaPresentacion between '" + start + "' and '" + end + "' "
                + "or n.fechaProrroga between '" + start + "' and '" + end + "' or n.fechaRegistro between '" + start + "' and '" + end + "' ORDER BY n.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Prorroga> getProrrogasByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.denominacion LIKE '%" + denominacion + "%' ORDER BY n.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Prorroga> getProrrogaByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.titularActual LIKE '%" + titular + "%' ORDER BY n.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public Prorroga getProrrogaBySolicitud(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Prorroga r where r.solicitud = :solicitud");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        query.setParameter("solicitud", solicitud);
        if (!query.getResultList().isEmpty()) {
            return (Prorroga) query.getResultList().get(0);
        } else {
            return new Prorroga();
        }
    }

    public boolean validarExistenciaProrroga(Prorroga n) {
        Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.solicitud = :solicitud and n.id != :id");
        query.setParameter("solicitud", n.getSolicitud());
        query.setParameter("id", n.getId());
        return !query.getResultList().isEmpty();
    }

    public int getNextNumeroProrroga(Date fechaProrroga) {
        Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.numeroProrroga = (Select MAX(n1.numeroProrroga) from Prorroga n1)");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<Prorroga> prorrogas = query.getResultList();
        if (prorrogas.isEmpty()) {
            return 1;
        } else {
            Prorroga prorroga = prorrogas.get(0);
            if (prorroga.getNumeroProrroga() == null || prorroga.getFechaProrroga() == null) {
                return 1;
            }

            int yearActual = fechaProrroga.getYear() + 1900;
            int yearUltima = prorroga.getFechaProrroga().getYear() + 1900;

            if (yearActual == yearUltima) {
                int next = prorroga.getNumeroProrroga() + 1;
                System.out.println("asignación de número de prórroga " + next);
                return next;
            } else if (yearActual > yearUltima) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
