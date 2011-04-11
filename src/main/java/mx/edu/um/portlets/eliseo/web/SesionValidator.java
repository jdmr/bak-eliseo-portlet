package mx.edu.um.portlets.eliseo.web;

import mx.edu.um.portlets.eliseo.dao.Sesion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 * @author jdmr
 */
@Component
public class SesionValidator implements Validator {
    
    private static final Logger log = LoggerFactory.getLogger(SesionValidator.class);

    @Override
    public boolean supports(Class<?> aClass) {
        return Sesion.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validando sesion");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dia", "sesion.dia.requerido");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "horaInicial", "sesion.horaInicial.requerido");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "horaFinal", "sesion.horaFinal.requerido");
    }

}
