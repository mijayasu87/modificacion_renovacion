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
public class FormTypes {
    private Integer id;
    private Integer formId;
    private Integer typeId;

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
     * @return the typeId
     */
    public Integer getTypeId() {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}
