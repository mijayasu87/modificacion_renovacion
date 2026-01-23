/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.model.iepicas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import senadi.gob.ec.mod.model.UploadNotificacion;
import senadi.gob.ec.mod.ucc.IepiFormDep;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author micharesp
 */
public class NotificationsDAO {

    public boolean saveNotifications(Notifications n) {
        String query = "INSERT INTO notifications VALUES(0," + n.getMat_id() + ", " + n.getNot_id() + ",'" + n.getMatter() + "','" + n.getDocument() + "',"
                + "'" + n.getCreateDt() + "',null,null,'" + n.getSource() + "'," + n.getCreated_id() + ")";
        try {
            Connection con = IepiFormDep.doConnectionToCasilleros();
            PreparedStatement pst = con.prepareStatement(query);
            int num = pst.executeUpdate();
            con.close();
            if (num > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            System.err.println("Hubo un problema al guardar notifications: " + ex);
            return false;
        }
    }

    public Notifications getNotificationsByMatterAndCreateDt(String matter, String createDt) {
        String query = "SELECT * FROM notifications WHERE matter = '" + matter + "' and create_dt = '" + createDt + "'";
        try {
            Connection con = IepiFormDep.doConnectionToCasilleros();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            Notifications n = new Notifications();
            if (rs.next()) {
                n.setId(rs.getInt("id"));
                n.setMat_id(rs.getInt("mat_id"));
                n.setNot_id(rs.getInt("not_id"));
                n.setMatter(rs.getString("matter"));
                n.setDocument(rs.getString("document"));
                n.setCreateDt(rs.getString("create_dt"));
                n.setSource(rs.getString("source"));
                n.setCreated_id(rs.getInt("created_id"));
            }
            con.close();
            return n;

        } catch (Exception ex) {
            System.err.println("Hubo un problema al obtener notifications: " + ex);
            return new Notifications();
        }
    }

    public boolean saveLockerNotifications(LockerNotifications ln) {
        String query = "INSERT INTO locker_notifications VALUES(0," + ln.getLockerId() + ", " + ln.getNotification_id() + ", null, '" + ln.getStatus() + "','" + ln.getDocument() + "')";
        try {
            Connection con = IepiFormDep.doConnectionToCasilleros();
            PreparedStatement pst = con.prepareStatement(query);
            int num = pst.executeUpdate();
            con.close();
            if (num > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            System.err.println("Hubo un problema al guardar locker_notifications: " + ex);
            return false;
        }
    }

    public boolean downLockerNotifications(UploadNotificacion un) {
        String fecha = Operaciones.formatDate(un.getFechaNotificacion());
//        System.out.println("fecha: "+fecha+", documento: "+un.getDocumento());
        String query = "";
        if (un.getNotificationsId() != null) {
            query = "UPDATE locker_notifications set status = 'DRAFT' WHERE notification_id = " + un.getNotificationsId() + " and document = '" + un.getDocumento() + "' and locker_id = '"+un.getCasillero()+"'";
        } else {
            Integer idnotifications = getIdNotificationFromLockerNotification(un);
            un.setNotificationsId(idnotifications);
            query = "UPDATE locker_notifications set status = 'DRAFT' WHERE notification_id = " + idnotifications+" and locker_id = '"+un.getCasillero()+"'";
        }

        try {

            Connection con = IepiFormDep.doConnectionToCasilleros();
//            System.out.println("aaaaaaaaa2");            
            PreparedStatement pst = con.prepareStatement(query);
            int num = pst.executeUpdate();
//            System.out.println("aaaaaaaaa4: "+num);
            con.close();
            if (num > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            System.err.println("Hubo un problema al editar locker_notifications " + un.getDocumento() + ": " + ex);
            return false;
        }

    }

    public int getIdNotificationFromLockerNotification(UploadNotificacion un) {
        String fecha = Operaciones.formatDate(un.getFechaNotificacion());
//        System.out.println("1: fecha: "+fecha+", documento: "+un.getDocumento());
        String query = "Select * from notifications WHERE document = '" + un.getDocumento() + "' order by id desc" ;
        try {
            Connection con = IepiFormDep.doConnectionToCasilleros();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            int idnot = 0;
            if (rs.next()) {
                idnot = rs.getInt("id");

            }
            con.close();
            if(idnot != 0){
//                System.out.println("id encontrado: "+not.getId());
                return idnot;
            }else{
//                System.out.println("no se encontró");
                return -1;
            }

        } catch (Exception ex) {
            System.err.println("Hubo un problema al obtener id de notifications: " + ex);
            return -1;
        }
    }
}
