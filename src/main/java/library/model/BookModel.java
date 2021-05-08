package library.model;

import library.domain.BookEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class BookModel {

    private Long id;

    @NotEmpty(message = "Не должно быть безымянных книг")
    private String name;

    private byte[] content;

    private MultipartFile uploadedContent;

    @NotNull(message = "Введите количество страниц")
    @Min(value = 1, message = "В книге должна быть хотя бы одна страница!")
    @Max(value = 5000, message = "Слишком много страниц для одной книги!")
    private int pageCount;

    private String isbn;

    @NotEmpty(message = "Выберите жанр")
    private String genre;

    @NotEmpty(message = "Выберите автора")
    private String author;

    @NotEmpty(message = "Укажите издателя")
    private String publisher;

    @NotNull(message = "Укажите год издания")
    private Integer publishYear;

    private byte[] image;

    private MultipartFile uploadedImage;

    private int avgRating;

    private long totalVoteCount;

    private long totalRating;

    private long viewCount;

    private String description;

    public BookModel() {

    }

    public BookModel(BookEntity entity) {
        id = entity.getId();
        name = entity.getName();
        content = entity.getContent();
        pageCount = entity.getPageCount();
        isbn = entity.getIsbn();
        genre = entity.getGenre().getLocalizedName();
        author = entity.getAuthor().getLocalizedFio();
        publisher = entity.getPublisher().getLocalizedName();
        publishYear = entity.getPublishYear();
        image = entity.getImage();
        avgRating = entity.getAvgRating();
        totalVoteCount = entity.getTotalVoteCount();
        totalRating = entity.getTotalRating();
        viewCount = entity.getViewCount();
        description = entity.getDescription();
    }

    @Override
    public String toString() {
        return name;
    }

}
