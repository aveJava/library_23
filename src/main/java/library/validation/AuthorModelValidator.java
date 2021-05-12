package library.validation;

import library.model.AuthorModel;
import library.utils.MessLocalizer;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.DateTimeException;
import java.time.LocalDate;

@Component
public class AuthorModelValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return AuthorModel.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        AuthorModel model = (AuthorModel) o;

        Long id = model.getId();
        String ruFio = model.getRuFio();
        String enFio = model.getEnFio();
        String birthday_year = model.getBirthday_year();
        String birthday_month = model.getBirthday_month();
        String birthday_day = model.getBirthday_day();

        // ruFio
        if (ruFio == null || ruFio.isEmpty())
            errors.rejectValue("ruFio", "", MessLocalizer.get("required_author_ruFio"));
        else if (ruFio.length() < 3 || ruFio.length() > 70)
            errors.rejectValue("ruFio", "", MessLocalizer.get("author_fio_size"));
        // enFio
        if (enFio == null || enFio.isEmpty())
            errors.rejectValue("enFio", "", MessLocalizer.get("required_author_enFio"));
        else if (enFio.length() < 3 || enFio.length() > 70)
            errors.rejectValue("ruFio", "", MessLocalizer.get("author_fio_size"));
        // birthday
        LocalDate birthday = null;
        try {
            Integer year = Integer.parseInt(birthday_year);
            Integer month = Integer.parseInt(birthday_month);
            Integer day = Integer.parseInt(birthday_day);
            birthday = LocalDate.of(year, month, day);
        } catch (NumberFormatException e) {
            errors.rejectValue("birthday", "", "author_birthday_number_ex");
        } catch (DateTimeException e) {
            errors.rejectValue("birthday", "", "author_birthday_date_ex");
        }
        if (birthday.isAfter(LocalDate.now()))
            errors.rejectValue("birthday", "", "author_birthday_before");
    }

}
