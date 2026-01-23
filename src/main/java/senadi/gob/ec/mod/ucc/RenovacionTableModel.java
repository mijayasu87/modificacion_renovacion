/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.ucc;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import senadi.gob.ec.mod.model.Renovacion;

/**
 *
 * @author Michael Yanangómez
 */
public class RenovacionTableModel extends AbstractTableModel {

    String titulo[] = {"cert_no", "fe_certificado", "sol_senadi", "fe_presentacion", "sign",
        "no_reg", "fe_registro", "denom", "tit_actual", "fe_vence_registro", "respons", "apartado"};

    private List<Renovacion> filas;
    private Renovacion renovacion;

    public RenovacionTableModel(List<Renovacion> filas) {
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
        setRenovacion(getFilas().get(rowIndex));

        switch (columnIndex) {
            case 0:
                return getRenovacion().getCertificadoNo() + "";
            case 1:
                return Operaciones.formatDateToLarge(getRenovacion().getFechaCertificado());
            case 2:
                return getRenovacion().getSolicitudSenadi();
            case 3:
                return Operaciones.formatDateToLarge(getRenovacion().getFechaPresentacion());
            case 4:
                return getRenovacion().getSigno();
            case 5:
                return getRenovacion().getRegistroNo();
            case 6:
                return Operaciones.formatDateToLarge(getRenovacion().getFechaRegistro());
            case 7:
                return getRenovacion().getDenominacion();
            case 8:
                return getRenovacion().getTitularActual();
            case 9:
                return Operaciones.formatDateToLarge(getRenovacion().getFechaVenceRegistro());
            case 10:
                return getRenovacion().getResponsable();
            case 11:
                if (getRenovacion().getSigno().equals("NC")) {
                    return "en la Decisión 486 de la Comisión de la Comunidad Andina, establece en sus artículos 196; y, 198 inciso segundo "
                            + "<style pdfFontName='Helvetica-Oblique'>“(…) el registro se efectuará en los mismos términos del registro original.”</style>, "
                            + "en concordancia con el artículo 420";
                } else {
                    return "en los artículos 152 y 153 de la Decisión 486 de la Comisión de la Comunidad Andina, "
                            + "en concordancia con los artículos, 365, 366";
                }

        }
        return null;
    }

    /**
     * @return the filas
     */
    public List<Renovacion> getFilas() {
        return filas;
    }

    /**
     * @param filas the filas to set
     */
    public void setFilas(List<Renovacion> filas) {
        this.filas = filas;
    }

    /**
     * @return the renovacion
     */
    public Renovacion getRenovacion() {
        return renovacion;
    }

    /**
     * @param renovacion the renovacion to set
     */
    public void setRenovacion(Renovacion renovacion) {
        this.renovacion = renovacion;
    }

}
