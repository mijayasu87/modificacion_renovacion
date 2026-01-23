/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.model.iepform;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author Michael Yanangómez
 */

public class RenewalForm implements Serializable{

    private Integer id;
    
    private Integer formId;
    
    private Integer transactionMotiveId;
    
    private Integer paymentReceiptId;
    
    private String applicationNumber;
    
    private String status;
    
    private String powerAttorney;
    
    private String discountFile;
    
    private Timestamp createDate;
    
    private String expedient;
    
    private String expedientDate;
    
    private Integer debugId;
    
    private Timestamp applicationDate;
    
    private String transactionNumber;
    
    private Integer ownerId;
    private String licenseType;

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
     * @return the paymentReceiptId
     */
    public Integer getPaymentReceiptId() {
        return paymentReceiptId;
    }

    /**
     * @param paymentReceiptId the paymentReceiptId to set
     */
    public void setPaymentReceiptId(Integer paymentReceiptId) {
        this.paymentReceiptId = paymentReceiptId;
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
     * @return the powerAttorney
     */
    public String getPowerAttorney() {
        return powerAttorney;
    }

    /**
     * @param powerAttorney the powerAttorney to set
     */
    public void setPowerAttorney(String powerAttorney) {
        this.powerAttorney = powerAttorney;
    }

    /**
     * @return the discountFile
     */
    public String getDiscountFile() {
        return discountFile;
    }

    /**
     * @param discountFile the discountFile to set
     */
    public void setDiscountFile(String discountFile) {
        this.discountFile = discountFile;
    }

    /**
     * @return the createDate
     */
    public Timestamp getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the expedientDate
     */
    public String getExpedientDate() {
        return expedientDate;
    }

    /**
     * @param expedientDate the expedientDate to set
     */
    public void setExpedientDate(String expedientDate) {
        this.expedientDate = expedientDate;
    }

    /**
     * @return the transactionMotiveId
     */
    public Integer getTransactionMotiveId() {
        return transactionMotiveId;
    }

    /**
     * @param transactionMotiveId the transactionMotiveId to set
     */
    public void setTransactionMotiveId(Integer transactionMotiveId) {
        this.transactionMotiveId = transactionMotiveId;
    }

    /**
     * @return the formId
     */
    public Integer getFormId() {
        return formId;
    }

    /**
     * @param formId the formId to set
     */
    public void setFormId(Integer formId) {
        this.formId = formId;
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
     * @return the debugId
     */
    public Integer getDebugId() {
        return debugId;
    }

    /**
     * @param debugId the debugId to set
     */
    public void setDebugId(Integer debugId) {
        this.debugId = debugId;
    }

    /**
     * @return the applicationDate
     */
    public Timestamp getApplicationDate() {
        return applicationDate;
    }

    /**
     * @param applicationDate the applicationDate to set
     */
    public void setApplicationDate(Timestamp applicationDate) {
        this.applicationDate = applicationDate;
    }

    /**
     * @return the transactionNumber
     */
    public String getTransactionNumber() {
        return transactionNumber;
    }

    /**
     * @param transactionNumber the transactionNumber to set
     */
    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
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
     * @return the licenseType
     */
    public String getLicenseType() {
        return licenseType;
    }

    /**
     * @param licenseType the licenseType to set
     */
    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }
}
