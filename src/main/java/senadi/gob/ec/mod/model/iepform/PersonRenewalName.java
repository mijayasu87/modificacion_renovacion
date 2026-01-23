/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.model.iepform;

/**
 *
 * @author michael
 */
public class PersonRenewalName {
    private Integer renewalFormId;
    private Integer personId;
    private String newName;
    private String titleName;

    /**
     * @return the renewalFormId
     */
    public Integer getRenewalFormId() {
        return renewalFormId;
    }

    /**
     * @param renewalFormId the renewalFormId to set
     */
    public void setRenewalFormId(Integer renewalFormId) {
        this.renewalFormId = renewalFormId;
    }

    /**
     * @return the personId
     */
    public Integer getPersonId() {
        return personId;
    }

    /**
     * @param personId the personId to set
     */
    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @param newName the newName to set
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * @return the titleName
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * @param titleName the titleName to set
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
}
