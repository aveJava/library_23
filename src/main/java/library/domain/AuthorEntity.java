package library.domain;

import library.model.AuthorModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@Entity
@Table(name = "author")
@EqualsAndHashCode(of = "id")
@Getter @Setter
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ru_fio", nullable = false)
    private String ruFio;

    @Column(name = "en_fio", nullable = false)
    private String enFio;

    private Date birthday;

    @OneToMany(mappedBy = "author", fetch=FetchType.LAZY) // author - имя поля в классе Book
    private List<BookEntity> books;

    public AuthorModel toAuthorModel() {
        AuthorModel model = new AuthorModel();

        model.setId(id);
        model.setRuFio(ruFio);
        model.setEnFio(enFio);
        LocalDate birth = birthday.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        model.setBirthdayYear(String.valueOf(birth.getYear()));
        model.setBirthdayMonth(String.valueOf(birth.getMonthValue()));
        model.setBirthdayDay(String.valueOf(birth.getDayOfMonth()));

        return model;
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
