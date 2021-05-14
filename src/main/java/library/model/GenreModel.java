package library.model;

import library.domain.GenreEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;


@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class GenreModel {

    private Long id;

    private String ruName;

    private String enName;

    public GenreEntity toGenreEntity() {
        GenreEntity entity = new GenreEntity();

        entity.setId(id);
        entity.setRuName(ruName);
        entity.setEnName(enName);

        return entity;
    }

    @Override
    public String toString() {
        return getLocalizedName();
    }

    public String getLocalizedName() {
        Locale locale = LocaleContextHolder.getLocale();
        if ("ru".equals(locale.toString())) return ruName;
        else return enName;
    }

}
