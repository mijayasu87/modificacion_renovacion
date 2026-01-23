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
public class Notifications implements Serializable {

    private Integer id;

    private Integer mat_id;

    private Integer not_id;

    private String matter;

    private String document;

    private String createDt;

    private String source;

    private Integer created_id;

//    @OneToOne(mappedBy = "notifications")
//    private LockerNotifications lockerNotifications;
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
     * @return the mat_id
     */
    public Integer getMat_id() {
        return mat_id;
    }

    /**
     * @param mat_id the mat_id to set
     */
    public void setMat_id(Integer mat_id) {
        this.mat_id = mat_id;
    }

    /**
     * @return the not_id
     */
    public Integer getNot_id() {
        return not_id;
    }

    /**
     * @param not_id the not_id to set
     */
    public void setNot_id(Integer not_id) {
        this.not_id = not_id;
    }

    /**
     * @return the matter
     */
    public String getMatter() {
        return matter;
    }

    /**
     * @param matter the matter to set
     */
    public void setMatter(String matter) {
        this.matter = matter;
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

    /**
     * @return the createDt
     */
    public String getCreateDt() {
        return createDt;
    }

    /**
     * @param createDt the createDt to set
     */
    public void setCreateDt(String createDt) {
        this.createDt = createDt;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the created_id
     */
    public Integer getCreated_id() {
        return created_id;
    }

    /**
     * @param created_id the created_id to set
     */
    public void setCreated_id(Integer created_id) {
        this.created_id = created_id;
    }

//    /**
//     * @return the lockerNotifications
//     */
//    public LockerNotifications getLockerNotifications() {
//        return lockerNotifications;
//    }
//
//    /**
//     * @param lockerNotifications the lockerNotifications to set
//     */
//    public void setLockerNotifications(LockerNotifications lockerNotifications) {
//        this.lockerNotifications = lockerNotifications;
//    }
}
