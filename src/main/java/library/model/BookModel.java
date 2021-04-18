package library.model;

import library.domain.BookEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;


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
    private int pageCount;

    @NotEmpty(message = "Заполните ISBN")
    @Size(min = 10, max = 13, message = "ISBN должен быть не короче 10 символов и не длиннее 13")
    private String isbn;

    @NotEmpty(message = "Выберите жанр")
    private String genre;

    @NotEmpty(message = "Выберите автора")
    private String author;

    @NotEmpty(message = "Укажите издателя")
    private String publisher;

    @NotNull(message = "Введите год издания")
    private int publishYear;

    private byte[] image;

    private MultipartFile uploadedImage;

    private int avgRating;

    private long totalVoteCount;

    private long totalRating;

    private long viewCount;

    private String description;

    private boolean hasImg;

    public BookModel() {

    }

    public BookModel(BookEntity entity) {
        id = entity.getId();
        name = entity.getName();
//        content = entity.getContent();
        pageCount = entity.getPageCount();
        isbn = entity.getIsbn();
        genre = entity.getGenre().getName();
        author = entity.getAuthor().getFio();
        publisher = entity.getPublisher().getName();
        publishYear = entity.getPublishYear();
        image = entity.getImage();
        avgRating = entity.getAvgRating();
        totalVoteCount = entity.getTotalVoteCount();
        totalRating = entity.getTotalRating();
        viewCount = entity.getViewCount();
        description = entity.getDescription();

        hasImg = image != null && image.length != 0;
    }

    public boolean isHasImage() {
        long size = 0;
        if (hasImg) size += 200;    // условный минимальный размер файла
        if (uploadedImage != null) size += uploadedImage.getSize();
        return size > 199;

    }

    @Override
    public String toString() {
        return name;
    }

}
