package library.domain;

import library.model.PublisherModel;
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
@Table(name = "publisher")
@EqualsAndHashCode(of = "id")
@Getter @Setter
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
public class PublisherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ru_name", nullable = false)
    private String ruName;

    @Column(name = "en_name", nullable = false)
    private String enName;

    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)  // publisher - имя поля в классе Book
    private List<BookEntity> books;

    public PublisherModel toPublisherModel() {
        PublisherModel model = new PublisherModel();

        model.setId(id);
        model.setRuName(ruName);
        model.setEnName(enName);

        return model;
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
