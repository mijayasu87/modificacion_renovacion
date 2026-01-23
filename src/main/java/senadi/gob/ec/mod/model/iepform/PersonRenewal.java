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
public class PersonRenewal {
    private Integer renewalFormId;
    private Integer personId;
    private String type;

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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
