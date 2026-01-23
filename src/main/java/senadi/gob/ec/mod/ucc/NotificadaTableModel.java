/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.ucc;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import senadi.gob.ec.mod.model.Notificada;

/**
 *
 * @author Michael Yanangómez
 */
public class NotificadaTableModel extends AbstractTableModel {

    String titulo[] = {"idNotificada","notifica", "tipo_sol", "tit_actual", "sol_senadi",
        "fecha_pres", "denom", "reg_no", "fecha_reg", "fecha_el_not", "ro", "cas_senadi",
        "cas_judicial", "tit_apod_repre", "nom_apod_repre", "respons", "sign",
        "r1", "r2", "r3", "r4", "r5", "r6"};

    private List<Notificada> filas;
    private Notificada notificada;

    public NotificadaTableModel(List<Notificada> filas) {
        this.filas = filas;
    }

    @Override
    public int getRowCount() {
        return getFilas() != null ? getFilas().size() : 0;//retorna el numero de filas
    }

    @Override
    public int getColumnCount() {
        return titulo.length;
    }

    @Override
    public String getColumnName(int column) {
        return titulo[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        setNotificada(getFilas().get(rowIndex));

        switch (columnIndex) {
            case 0:
                return getNotificada().getId();
            case 1:
                return getNotificada().getNotificacion() + "";
            case 2:
                return getNotificada().getTipoSolicitante();
            case 3:
                return getNotificada().getTitularActual();
            case 4:
                return getNotificada().getSolicitud();
            case 5:
                return Operaciones.formatDate(getNotificada().getFechaPresentacion());
            case 6:
                return getNotificada().getDenominacion();
            case 7:
                return getNotificada().getRegistroNo();
            case 8:
                return Operaciones.formatDate(getNotificada().getFechaRegistro());
            case 9:
                return Operaciones.formatDateToLarge(getNotificada().getFechaElaboraNotificacion());
            case 10:
                return getNotificada().getRo();
            case 11:
                return getNotificada().getCasilleroSenadi();
            case 12:
                return getNotificada().getCasilleroJudicial();
            case 13:
                return getNotificada().getTitApodRepre();
            case 14:
                return getNotificada().getNomApodRepre();
            case 15:
                return getNotificada().getResponsable();
            case 16:
                return getNotificada().getSigno();
            case 17:
                return getNotificada().getR1() != null ? getNotificada().getR1() : "";
            case 18:
                return getNotificada().getR2() != null ? getNotificada().getR2() : "";
            case 19:
                return getNotificada().getR3() != null ? getNotificada().getR3() : "";
            case 20:
                return getNotificada().getR4() != null ? getNotificada().getR4() : "";
            case 21:
                return getNotificada().getR5() != null ? getNotificada().getR5() : "";
            case 22:
                return getNotificada().getR6() != null ? getNotificada().getR6() : "";
        }
        return null;
    }

    /**
     * @return the filas
     */
    public List<Notificada> getFilas() {
        return filas;
    }

    /**
     * @param filas the filas to set
     */
    public void setFilas(List<Notificada> filas) {
        this.filas = filas;
    }

    /**
     * @return the notificada
     */
    public Notificada getNotificada() {
        return notificada;
    }

    /**
     * @param notificada the notificada to set
     */
    public void setNotificada(Notificada notificada) {
        this.notificada = notificada;
    }

}
