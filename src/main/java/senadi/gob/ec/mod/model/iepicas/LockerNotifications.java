/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.model.iepicas;

import java.io.Serializable;

/**
 *
 * @author michael
 */
public class LockerNotifications implements Serializable {
   
    private Integer id;

    private Integer lockerId;
    
    private Integer notification_id;
   
    private String openDt;

    private String status;

    private String document;
    
//    @OneToOne(cascade = CascadeType.ALL)   
//    @JoinColumn(name = "notification_id", referencedColumnName = "id")
//    private Notifications notifications;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the lockerId
     */
    public Integer getLockerId() {
        return lockerId;
    }

    /**
     * @param lockerId the lockerId to set
     */
    public void setLockerId(Integer lockerId) {
        this.lockerId = lockerId;
    }

    /**
     * @return the openDt
     */
    public String getOpenDt() {
        return openDt;
    }

    /**
     * @param openDt the openDt to set
     */
    public void setOpenDt(String openDt) {
        this.openDt = openDt;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the document
     */
    public String getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(String document) {
        this.document = document;
    }

//    /**
//     * @return the notifications
//     */
//    public Notifications getNotifications() {
//        return notifications;
//    }
//
//    /**
//     * @param notifications the notifications to set
//     */
//    public void setNotifications(Notifications notifications) {
//        this.notifications = notifications;
//    }
    
    @Override
    public String toString(){
        return getLockerId()+" | "+getDocument();
    }

    /**
     * @return the notification_id
     */
    public Integer getNotification_id() {
        return notification_id;
    }

    /**
     * @param notification_id the notification_id to set
     */
    public void setNotification_id(Integer notification_id) {
        this.notification_id = notification_id;
    }

}
