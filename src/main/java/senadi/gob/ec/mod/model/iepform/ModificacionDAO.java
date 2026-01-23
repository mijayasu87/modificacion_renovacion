/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.model.iepform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import senadi.gob.ec.mod.ucc.IepiFormDep;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author michael
 */
public class ModificacionDAO {
    
    public ModificacionApp getModificacionAppBySolicitudAndTipo(String solicitud, String tipo){
        String sql = "select * from modificaciones_app where solicitud = '"+solicitud+"' and tipo = '"+tipo+"'";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            ModificacionApp modificacion = new ModificacionApp();
            while (rs.next()) {                
                modificacion.setId(rs.getInt("id"));
                modificacion.setSolicitud(rs.getString("solicitud"));
                modificacion.setDenominacion(rs.getString("denominacion"));
                modificacion.setFecha(rs.getTimestamp("fecha"));
                modificacion.setTipo(rs.getString("tipo"));
                modificacion.setRegistro(rs.getString("registro"));
                modificacion.setUsuario(rs.getString("usuario"));
                modificacion.setObservacion(rs.getString("observacion"));
                modificacion.setModo(rs.getString("modo"));
                modificacion.setActivo(rs.getBoolean("activo"));                                
            }
            con.close();
            return modificacion;
        } catch (Exception ex) {
            System.out.println("error en obtener datos modificaciones_app: " + ex);
            return new ModificacionApp();
        }
    }
       
    public ModificacionApp getModificacionApp(String solicitud){
        String sql = "select * from modificaciones_app where solicitud = '"+solicitud+"'";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            ModificacionApp modificacion = new ModificacionApp();
            while (rs.next()) {                
                modificacion.setId(rs.getInt("id"));
                modificacion.setSolicitud(rs.getString("solicitud"));
                modificacion.setDenominacion(rs.getString("denominacion"));
                modificacion.setFecha(rs.getTimestamp("fecha"));
                modificacion.setTipo(rs.getString("tipo"));
                modificacion.setRegistro(rs.getString("registro"));
                modificacion.setUsuario(rs.getString("usuario"));
                modificacion.setObservacion(rs.getString("observacion"));
                modificacion.setModo(rs.getString("modo"));
                modificacion.setActivo(rs.getBoolean("activo"));                                
            }
            con.close();
            return modificacion;
        } catch (Exception ex) {
            System.out.println("error en obtener datos modificaciones_app: " + ex);
            return new ModificacionApp();
        }
    }
    
    public List<ModificacionApp> getModificacionesApp(String tipo){
        String sql = "select * from modificaciones_app where tipo = '"+tipo+"' and (solicitud like 'SENADI%' or solicitud like 'IEPI%')";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            List<ModificacionApp> modificaciones = new ArrayList<>();
            while (rs.next()) {
                ModificacionApp modificacion = new ModificacionApp();
                modificacion.setId(rs.getInt("id"));
                modificacion.setSolicitud(rs.getString("solicitud"));
                modificacion.setDenominacion(rs.getString("denominacion"));
                modificacion.setFecha(rs.getTimestamp("fecha"));
                modificacion.setTipo(rs.getString("tipo"));
                modificacion.setRegistro(rs.getString("registro"));
                modificacion.setUsuario(rs.getString("usuario"));
                modificacion.setObservacion(rs.getString("observacion"));
                modificacion.setModo(rs.getString("modo"));
                modificacion.setActivo(rs.getBoolean("activo"));                
                modificaciones.add(modificacion);
            }
            con.close();
            return modificaciones;
        } catch (Exception ex) {
            System.out.println("error en obtener datos modificaciones_app: " + ex);
            return new ArrayList<>();
        }
    }
    
    public List<ModificacionApp> getModificacionesAppByTramites(String tramites){
        String sql = "select * from modificaciones_app where solicitud in ("+tramites+")";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            List<ModificacionApp> modificaciones = new ArrayList<>();
            while (rs.next()) {
                ModificacionApp modificacion = new ModificacionApp();
                modificacion.setId(rs.getInt("id"));
                modificacion.setSolicitud(rs.getString("solicitud"));
                modificacion.setDenominacion(rs.getString("denominacion"));
                modificacion.setFecha(rs.getTimestamp("fecha"));
                modificacion.setTipo(rs.getString("tipo"));
                modificacion.setRegistro(rs.getString("registro"));
                modificacion.setUsuario(rs.getString("usuario"));
                modificacion.setObservacion(rs.getString("observacion"));
                modificacion.setModo(rs.getString("modo"));
                modificacion.setActivo(rs.getBoolean("activo"));                
                modificaciones.add(modificacion);
            }
            con.close();
            return modificaciones;
        } catch (Exception ex) {
            System.out.println("error en obtener datos modificaciones_app: " + ex);
            return new ArrayList<>();
        }
    }
    

    public boolean insertModificacionApp(ModificacionApp mod) {
        String sql = "INSERT INTO modificaciones_app VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, 0);
            pst.setString(2, mod.getSolicitud());
            pst.setString(3, mod.getDenominacion());
            pst.setTimestamp(4, mod.getFecha());
            pst.setString(5, mod.getTipo());
            pst.setString(6, mod.getRegistro());
            pst.setString(7, mod.getUsuario());
            pst.setString(8, mod.getObservacion());
            pst.setString(9, mod.getModo());
            pst.setBoolean(10, mod.getActivo());

            int n = pst.executeUpdate();

            con.close();
            return n > 0;

        } catch (Exception ex) {
            System.err.println("Error al insertar modificacion app " + mod.getSolicitud() + ": " + ex);
            return false;
        }
    }

    public List<RenewalForm> getRenewalsRezagoBytType(String type, String transactionMotive) {
        String query = "SELECT * FROM renewal_forms as r "
                + "WHERE r.transaction_motive_id in (" + transactionMotive + ") "
                + "AND r.status = 'DELIVERED' "
                + "AND r.application_number not in (SELECT ma.solicitud FROM modificaciones_app AS ma WHERE ma.tipo = '" + type + "' "
                + "and (ma.solicitud LIKE 'SENADI%' OR ma.solicitud LIKE 'IEPI%'))";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<RenewalForm> renewals = new ArrayList<>();
            while (rs.next()) {
                RenewalForm renewalForm = new RenewalForm();
                renewalForm.setId(rs.getInt("id"));
                renewalForm.setFormId(rs.getInt("form_id"));
                renewalForm.setTransactionMotiveId(rs.getInt("transaction_motive_id"));
                renewalForm.setPaymentReceiptId(rs.getInt("payment_receipt_id"));
                renewalForm.setApplicationNumber(rs.getString("application_number"));
                renewalForm.setStatus(rs.getString("status"));
                renewalForm.setPowerAttorney(rs.getString("power_attorney"));
                renewalForm.setDiscountFile(rs.getString("discount_file"));
                renewalForm.setCreateDate(rs.getTimestamp("create_date"));
                renewalForm.setExpedient(rs.getString("expedient"));
                renewalForm.setExpedientDate(rs.getString("expedient_date"));
                renewalForm.setDebugId(rs.getInt("debug_id"));
                renewalForm.setApplicationDate(rs.getTimestamp("application_date"));
                renewalForm.setTransactionNumber(rs.getString("transaction_number"));
                renewalForm.setOwnerId(rs.getInt("owner_id"));
                renewalForm.setLicenseType(rs.getString("license_type"));
                renewals.add(renewalForm);
            }
            con.close();
            return renewals;
        } catch (Exception ex) {
            System.out.println("error en obtener datos renewalforms: " + ex);
            return new ArrayList<>();
        }
    }
    
    public List<RenewalForm> getRenewalsResagoBytTypeAndCriteria(String type, String transactionMotive, String criterio) {
        String query = "SELECT * FROM renewal_forms as r "
                + "WHERE r.transaction_motive_id in (" + transactionMotive + ") "
                + "AND r.status = 'DELIVERED' "
                + "and r.application_number LIKE '%"+criterio+"%' "
                + "AND r.application_number not in (SELECT ma.solicitud FROM modificaciones_app AS ma WHERE ma.tipo = '" + type + "' "                
                + "and (ma.solicitud LIKE 'SENADI%' OR ma.solicitud LIKE 'IEPI%'))";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<RenewalForm> renewals = new ArrayList<>();
            while (rs.next()) {
                RenewalForm renewalForm = new RenewalForm();
                renewalForm.setId(rs.getInt("id"));
                renewalForm.setFormId(rs.getInt("form_id"));
                renewalForm.setTransactionMotiveId(rs.getInt("transaction_motive_id"));
                renewalForm.setPaymentReceiptId(rs.getInt("payment_receipt_id"));
                renewalForm.setApplicationNumber(rs.getString("application_number"));
                renewalForm.setStatus(rs.getString("status"));
                renewalForm.setPowerAttorney(rs.getString("power_attorney"));
                renewalForm.setDiscountFile(rs.getString("discount_file"));
                renewalForm.setCreateDate(rs.getTimestamp("create_date"));
                renewalForm.setExpedient(rs.getString("expedient"));
                renewalForm.setExpedientDate(rs.getString("expedient_date"));
                renewalForm.setDebugId(rs.getInt("debug_id"));
                renewalForm.setApplicationDate(rs.getTimestamp("application_date"));
                renewalForm.setTransactionNumber(rs.getString("transaction_number"));
                renewalForm.setOwnerId(rs.getInt("owner_id"));
                renewalForm.setLicenseType(rs.getString("license_type"));
                renewals.add(renewalForm);
            }
            con.close();
            return renewals;
        } catch (Exception ex) {
            System.out.println("error en obtener datos renewalforms: " + ex);
            return new ArrayList<>();
        }
    }
    
    public List<RenewalForm> getRenewalsResagoSublicBytTypeAndCriteria(String type, String transactionMotive, String criterio) {
        String query = "SELECT * FROM renewal_forms as r "
                + "WHERE r.transaction_motive_id in (" + transactionMotive + ") "
                + "AND r.status = 'DELIVERED' "
                + "and r.application_number LIKE '%"+criterio+"%' "
                + "AND r.application_number not in (SELECT ma.solicitud FROM modificaciones_app AS ma WHERE "
                + "(ma.tipo = '" + type + "' or ma.tipo = 'LICENCIA DE USO') "
                + "and (ma.solicitud LIKE 'SENADI%' OR ma.solicitud LIKE 'IEPI%')) "
                + "and r.license_type = 'SUBLICENSE'";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<RenewalForm> renewals = new ArrayList<>();
            while (rs.next()) {
                RenewalForm renewalForm = new RenewalForm();
                renewalForm.setId(rs.getInt("id"));
                renewalForm.setFormId(rs.getInt("form_id"));
                renewalForm.setTransactionMotiveId(rs.getInt("transaction_motive_id"));
                renewalForm.setPaymentReceiptId(rs.getInt("payment_receipt_id"));
                renewalForm.setApplicationNumber(rs.getString("application_number"));
                renewalForm.setStatus(rs.getString("status"));
                renewalForm.setPowerAttorney(rs.getString("power_attorney"));
                renewalForm.setDiscountFile(rs.getString("discount_file"));
                renewalForm.setCreateDate(rs.getTimestamp("create_date"));
                renewalForm.setExpedient(rs.getString("expedient"));
                renewalForm.setExpedientDate(rs.getString("expedient_date"));
                renewalForm.setDebugId(rs.getInt("debug_id"));
                renewalForm.setApplicationDate(rs.getTimestamp("application_date"));
                renewalForm.setTransactionNumber(rs.getString("transaction_number"));
                renewalForm.setOwnerId(rs.getInt("owner_id"));
                renewalForm.setLicenseType(rs.getString("license_type"));
                renewals.add(renewalForm);
            }
            con.close();
            return renewals;
        } catch (Exception ex) {
            System.out.println("error en obtener datos renewalforms: " + ex);
            return new ArrayList<>();
        }
    }
    
    public List<RenewalForm> getRenewalsResagoBytTypeAndFecha(String type, String transactionMotive, Date ini, Date fin) {
        String start = Operaciones.formatDate(ini)+" 00:00:00";
        String end = Operaciones.formatDate(fin)+ " 23:59:59";
        String query = "SELECT * FROM renewal_forms as r "
                + "WHERE r.transaction_motive_id in (" + transactionMotive + ") "
                + "AND r.status = 'DELIVERED' "
                + "and r.application_date BETWEEN '"+start+"' and '"+end+"' "
                + "AND r.application_number not in (SELECT ma.solicitud FROM modificaciones_app AS ma WHERE ma.tipo = '" + type + "' "
                + "and (ma.solicitud LIKE 'SENADI%' OR ma.solicitud LIKE 'IEPI%'))";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<RenewalForm> renewals = new ArrayList<>();
            while (rs.next()) {
                RenewalForm renewalForm = new RenewalForm();
                renewalForm.setId(rs.getInt("id"));
                renewalForm.setFormId(rs.getInt("form_id"));
                renewalForm.setTransactionMotiveId(rs.getInt("transaction_motive_id"));
                renewalForm.setPaymentReceiptId(rs.getInt("payment_receipt_id"));
                renewalForm.setApplicationNumber(rs.getString("application_number"));
                renewalForm.setStatus(rs.getString("status"));
                renewalForm.setPowerAttorney(rs.getString("power_attorney"));
                renewalForm.setDiscountFile(rs.getString("discount_file"));
                renewalForm.setCreateDate(rs.getTimestamp("create_date"));
                renewalForm.setExpedient(rs.getString("expedient"));
                renewalForm.setExpedientDate(rs.getString("expedient_date"));
                renewalForm.setDebugId(rs.getInt("debug_id"));
                renewalForm.setApplicationDate(rs.getTimestamp("application_date"));
                renewalForm.setTransactionNumber(rs.getString("transaction_number"));
                renewalForm.setOwnerId(rs.getInt("owner_id"));
                renewalForm.setLicenseType(rs.getString("license_type"));
                renewals.add(renewalForm);
            }
            con.close();
            return renewals;
        } catch (Exception ex) {
            System.out.println("error en obtener datos renewalforms: " + ex);
            return new ArrayList<>();
        }
    }
    
    public List<RenewalForm> getRenewalsRezagoSublicBytTypeAndFecha(String type, String transactionMotive, Date ini, Date fin) {
        String start = Operaciones.formatDate(ini)+" 00:00:00";
        String end = Operaciones.formatDate(fin)+ " 23:59:59";
//        String query = "SELECT * FROM renewal_forms as r "
//                + "WHERE r.transaction_motive_id in (" + transactionMotive + ") "
//                + "AND r.status = 'DELIVERED' "
//                + "and r.application_date BETWEEN '"+start+"' and '"+end+"' "
//                + "AND r.application_number not in (SELECT ma.solicitud FROM modificaciones_app AS ma WHERE ma.tipo = '" + type + "' "
//                + "and (ma.solicitud LIKE 'SENADI%' OR ma.solicitud LIKE 'IEPI%'))";
        
        String query = "SELECT * FROM renewal_forms as r "
                + "WHERE r.transaction_motive_id in (" + transactionMotive + ") "
                + "AND r.status = 'DELIVERED' "
                + "and r.application_date BETWEEN '"+start+"' and '"+end+"' "
                + "AND r.application_number not in (SELECT ma.solicitud FROM modificaciones_app AS ma WHERE "
                + "(ma.tipo = '" + type + "' or ma.tipo = 'LICENCIA DE USO') "
                + "and (ma.solicitud LIKE 'SENADI%' OR ma.solicitud LIKE 'IEPI%')) "
                + "and r.license_type = 'SUBLICENSE'";
        
        
        
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<RenewalForm> renewals = new ArrayList<>();
            while (rs.next()) {
                RenewalForm renewalForm = new RenewalForm();
                renewalForm.setId(rs.getInt("id"));
                renewalForm.setFormId(rs.getInt("form_id"));
                renewalForm.setTransactionMotiveId(rs.getInt("transaction_motive_id"));
                renewalForm.setPaymentReceiptId(rs.getInt("payment_receipt_id"));
                renewalForm.setApplicationNumber(rs.getString("application_number"));
                renewalForm.setStatus(rs.getString("status"));
                renewalForm.setPowerAttorney(rs.getString("power_attorney"));
                renewalForm.setDiscountFile(rs.getString("discount_file"));
                renewalForm.setCreateDate(rs.getTimestamp("create_date"));
                renewalForm.setExpedient(rs.getString("expedient"));
                renewalForm.setExpedientDate(rs.getString("expedient_date"));
                renewalForm.setDebugId(rs.getInt("debug_id"));
                renewalForm.setApplicationDate(rs.getTimestamp("application_date"));
                renewalForm.setTransactionNumber(rs.getString("transaction_number"));
                renewalForm.setOwnerId(rs.getInt("owner_id"));
                renewalForm.setLicenseType(rs.getString("license_type"));
                renewals.add(renewalForm);
            }
            con.close();
            return renewals;
        } catch (Exception ex) {
            System.out.println("error en obtener datos renewalforms: " + ex);
            return new ArrayList<>();
        }
    }

}
