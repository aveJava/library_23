package library.validation;

import library.model.PublisherModel;
import library.utils.MessLocalizer;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PublisherModelValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return PublisherModel.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PublisherModel model = (PublisherModel) o;

        Long id = model.getId();
        String ruName = model.getRuName();
        String enName = model.getEnName();

        if (ruName == null || ruName.isEmpty())
            errors.rejectValue("ruName", "", MessLocalizer.get("required_ruName"));
        else if (ruName.length() < 3 || ruName.length() > 70)
            errors.rejectValue("ruName", "", MessLocalizer.get("entry_name_size"));
        if (enName == null || enName.isEmpty())
            errors.rejectValue("enName", "", MessLocalizer.get("required_enName"));
        else if (enName.length() < 3 || enName.length() > 70)
            errors.rejectValue("enName", "", MessLocalizer.get("entry_name_size"));
    }

}
