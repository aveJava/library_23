package library.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "genre")
@EqualsAndHashCode(of = "id")
@Getter @Setter
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
public class GenreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "ru_name", nullable = false)
    private String ruName;

    @Column(name = "en_name", nullable = false)
    private String enName;

    @OneToMany(mappedBy = "genre", fetch = FetchType.LAZY)
    private List<BookEntity> books;

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
