package library.model;

import library.domain.AuthorEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;


@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class AuthorModel {
    private Long id;

    private String ruFio;

    private String enFio;

    private String birthdayYear;

    private String birthdayMonth;

    private String birthdayDay;

    public AuthorEntity toAuthorEntity() {
        AuthorEntity entity = new AuthorEntity();

        entity.setId(id);
        entity.setRuFio(ruFio);
        entity.setEnFio(enFio);
        LocalDate birthday = LocalDate.of(Integer.parseInt(birthdayYear),
                Integer.parseInt(birthdayMonth), Integer.parseInt(birthdayDay));
        Date birth = Date.from(birthday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        entity.setBirthday(birth);

        return entity;
    }

    @Override
    public String toString() {
        return getLocalizedFio();
    }

    public String getLocalizedFio() {
        Locale locale = LocaleContextHolder.getLocale();
        if ("ru".equals(locale.toString())) return ruFio;
        else return enFio;
    }
}
