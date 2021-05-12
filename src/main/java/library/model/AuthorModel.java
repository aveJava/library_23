package library.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;


@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class AuthorModel {
    private Long id;

    private String ruFio;

    private String enFio;

    private String birthday_year;

    private String birthday_month;

    private String birthday_day;

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
