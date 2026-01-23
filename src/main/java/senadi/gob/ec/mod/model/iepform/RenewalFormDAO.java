/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.model.iepform;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import senadi.gob.ec.mod.dao.RenovacionDAO;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.iepdep.HallmarkForms;
import senadi.gob.ec.mod.ucc.Controlador;
import senadi.gob.ec.mod.ucc.IepiFormDep;


/**
 *
 * @author michael
 */
public class RenewalFormDAO {
    
    public RenewalForm findRenewalFormsAutomatic(){
        
        RenovacionDAO rd = new RenovacionDAO(null);
        List<Renovacion> rens= rd.getMaxRenovaciones(10);
        Controlador c = new Controlador();
        String query = "Select * from renewal_forms where application_number in ("+c.getStringTramitesRenovacion(rens)+") order by id";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            RenewalForm renewalForm = new RenewalForm();
            while (rs.next()) {                
                renewalForm.setId(rs.getInt("id"));
                System.out.println("rid: "+renewalForm.getId());
            }
            con.close();           
            
            return renewalForm;
        } catch (Exception ex) {
            System.out.println("error en obtener renewalforms automatic 2: " + ex);
            return new RenewalForm();
        }
    }
    
    public List<RenewalForm> getRenewalFormsnews(){
        RenewalForm r = findRenewalFormsAutomatic();
        System.out.println("--> "+r.getId());
        String query = "Select * from renewal_forms where id > "+r.getId()+" and status = 'DELIVERED' and transaction_motive_id = 21";
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
                renewalForm.setOwnerId(rs.getInt("owner_id"));
                renewals.add(renewalForm);
            }
            con.close();
            return renewals;
        } catch (Exception ex) {
            System.out.println("error en obtener renewalforms automatic 1: " + ex);
            return new ArrayList<>();
        }        
        
    }
    
    
    public RenewalForm getRenewalFormsByApplication(String applicationNumber){
        String query = "Select * from renewal_forms where application_number = '"+applicationNumber+"'";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            RenewalForm renewalForm = new RenewalForm();
            while (rs.next()) {
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
                renewalForm.setOwnerId(rs.getInt("owner_id"));
            }
            con.close();
            return renewalForm;
        } catch (Exception ex) {
            System.out.println("error en obtener datos renewalforms: " + ex);
            return new RenewalForm();
        }
    }
    
    public FormTypes getFormTypesById(Integer id){
        String query = "Select * from form_types where id = "+id;
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            FormTypes formTypes = new FormTypes();
            while (rs.next()) {                
                formTypes.setId(rs.getInt("id"));
                formTypes.setFormId(rs.getInt("form_id"));
                formTypes.setTypeId(rs.getInt("type_id"));                
            }
            con.close();
            return formTypes;
        } catch (Exception ex) {
            System.out.println("error en obtener datos form_types: " + ex);
            return new FormTypes();
        }
    }    
    
    public Types getTypesById(Integer id){
        String query = "Select * from types where id = "+id;
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            Types types = new Types();
            while (rs.next()) {                
                types.setId(rs.getInt("id"));
                types.setParentId(rs.getInt("parent_id"));
                types.setName(rs.getString("name"));
                types.setAlias(rs.getString("alias"));
            }
            con.close();
            return types;
        } catch (Exception ex) {
            System.out.println("error en obtener datos types: " + ex);
            return new Types();
        }
    }
    
    public HallmarkForms getHallmarkForms(Integer id){
        String query = "Select * from hallmark_forms where id = "+id;
        try{
            Connection con = IepiFormDep.doConnectionToDepurar();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            HallmarkForms hallmark = new HallmarkForms();
            while (rs.next()) {                
                hallmark.setId(rs.getInt("id"));
                hallmark.setOwnerId(rs.getInt("owner_id"));
                hallmark.setDenomination(rs.getString("denomination"));
                hallmark.setExpedient(rs.getString("expedient"));
                hallmark.setStatus(rs.getString("status"));
                hallmark.setCreateDate(rs.getString("create_date"));
                hallmark.setExpYear(rs.getString("exp_year"));
            }
            con.close();
            return hallmark;
        } catch (Exception ex) {
            System.out.println("error en obtener datos types: " + ex);
            return new HallmarkForms();
        }
    }
    
    public Integer getCasilleroByOwner(Integer idOwner){
        String query = "Select * from lockers where owner_id = "+idOwner;
        try{
            Connection con = IepiFormDep.doConnectionToCasilleros();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            Integer casillero = 0;
            while (rs.next()) {                
                casillero = rs.getInt("id");
            }
            con.close();
            return casillero;
        } catch (Exception ex) {
            System.out.println("error en obtener datos casillero: " + ex);
            return 0;
        }
    }
    
    public PersonRenewalName getPersonRenewalNameByIdRenewal(Integer idRenewal){
        String query = "Select * from person_renewal_name where renewal_form_id = "+idRenewal;
        try{
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            PersonRenewalName prn = new PersonRenewalName();
            while (rs.next()) {
                prn.setRenewalFormId(rs.getInt("renewal_form_id"));
                prn.setPersonId(rs.getInt("person_id"));
                prn.setNewName(rs.getString("new_name"));
                prn.setTitleName(rs.getString("title_name"));
            }
            con.close();
            return prn;
        }catch(Exception ex){
            System.out.println("error en obtener datos person_renewal_name: " + ex);
            return new PersonRenewalName();
        }
    }
    
    public List<PersonRenewal> getPersonRenewalTypeByIdRenewal(Integer idRenewal, String type){
        String query = "Select * from person_renewal where renewal_form_id = "+idRenewal+" and type = '"+type+"'";
        try{
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<PersonRenewal> personsRenewal = new ArrayList<>();
            
            while (rs.next()) {
                PersonRenewal prn = new PersonRenewal();
                prn.setRenewalFormId(rs.getInt("renewal_form_id"));
                prn.setPersonId(rs.getInt("person_id"));
                prn.setType(rs.getString("type"));
                personsRenewal.add(prn);
            }
            con.close();
            return personsRenewal;
        }catch(Exception ex){
            System.out.println("error en obtener datos person_renewal "+type+": " + ex);
            return new ArrayList();
        }
    }
    
    
    public Person getPersonById(Integer id){
        String query = "Select * from person where id = "+id;
        try{
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            Person person = new Person();
            while (rs.next()) {
                person.setId(rs.getInt("id"));
                person.setIdentificationNumber(rs.getString("identification_number"));
                person.setName(rs.getString("name"));
                person.setAddress(rs.getString("address"));
            }
            con.close();
            return person;
        }catch(Exception ex){
            System.out.println("error en obtener datos person: " + ex);
            return new Person();
        }
    }
    
    public PaymentReceipt getPaymentReceiptById(Integer id){
        String query = "Select * from payment_receipt where id = "+id;
        try{
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement ps = con.prepareCall(query);
            ResultSet rs = ps.executeQuery();
            PaymentReceipt pr = new PaymentReceipt();
            while(rs.next()){
                pr.setId(rs.getInt("id"));
                pr.setVoucherNumber(rs.getString("voucher_number"));
                pr.setReceiptNumber(rs.getString("receipt_number"));
                pr.setAmount(rs.getDouble("amount"));
                pr.setDate(rs.getDate("date"));
            }
            con.close();
            return pr;            
            
        }catch(Exception ex){
            System.out.println("");
            return new PaymentReceipt();
        }
    }
}
