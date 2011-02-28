package mx.edu.um.portlets.eliseo.web;

import mx.edu.um.portlets.eliseo.dao.Salon;
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
public class SalonValidator implements Validator {
    
    private static final Logger log = LoggerFactory.getLogger(SalonValidator.class);

    @Override
    public boolean supports(Class<?> aClass) {
        return Salon.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validando salon");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nombre", "salon.nombre.requerido");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "curso", "salon.curso.requerido");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "maestroNombre", "salon.maestro.requerido");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "inicia", "salon.inicia.requerido");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "termina", "salon.termina.requerido");
    }

}
