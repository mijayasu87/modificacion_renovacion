/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.model.iepdep;

/**
 *
 * @author michael
 */
public class HallmarkForms {
    private Integer id;
    private Integer hallmarkNatureId;
    private Integer ownerId;
    private Integer hallmarkTypeId;
    private String applicationNumber;
    private String denomination;
    private String expedient;
    private String status;
    private String createDate;
    private String expYear;

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
     * @return the hallmarkNatureId
     */
    public Integer getHallmarkNatureId() {
        return hallmarkNatureId;
    }

    /**
     * @param hallmarkNatureId the hallmarkNatureId to set
     */
    public void setHallmarkNatureId(Integer hallmarkNatureId) {
        this.hallmarkNatureId = hallmarkNatureId;
    }

    /**
     * @return the ownerId
     */
    public Integer getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId the ownerId to set
     */
    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the hallmarkTypeId
     */
    public Integer getHallmarkTypeId() {
        return hallmarkTypeId;
    }

    /**
     * @param hallmarkTypeId the hallmarkTypeId to set
     */
    public void setHallmarkTypeId(Integer hallmarkTypeId) {
        this.hallmarkTypeId = hallmarkTypeId;
    }

    /**
     * @return the applicationNumber
     */
    public String getApplicationNumber() {
        return applicationNumber;
    }

    /**
     * @param applicationNumber the applicationNumber to set
     */
    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    /**
     * @return the denomination
     */
    public String getDenomination() {
        return denomination;
    }

    /**
     * @param denomination the denomination to set
     */
    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    /**
     * @return the expedient
     */
    public String getExpedient() {
        return expedient;
    }

    /**
     * @param expedient the expedient to set
     */
    public void setExpedient(String expedient) {
        this.expedient = expedient;
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
     * @return the createDate
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the expYear
     */
    public String getExpYear() {
        return expYear;
    }

    /**
     * @param expYear the expYear to set
     */
    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }
}
