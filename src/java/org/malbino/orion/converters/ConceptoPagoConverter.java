/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.malbino.orion.entities.ConceptoPago;
import org.malbino.orion.facades.ConceptoPagoFacade;

/**
 *
 * @author malbino
 */
@Named(value = "ConceptoPagoConverter")
@RequestScoped
public class ConceptoPagoConverter implements Converter {

    @EJB
    ConceptoPagoFacade conceptoPagoFacade;

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        Object o;

        if (submittedValue == null || submittedValue.isEmpty()) {
            o = null;
        } else {
            o = conceptoPagoFacade.find(Integer.valueOf(submittedValue));
        }

        return o;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        String s;

        if (value == null || value.equals("")) {
            s = "";
        } else {
            s = String.valueOf(((ConceptoPago) value).getId_conceptopago());
        }

        return s;
    }

}
