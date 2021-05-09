package library.model;

import library.domain.BookEntity;
import library.service.AuthorEntityService;
import library.service.GenreEntityService;
import library.service.PublisherEntityService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


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
    private Integer pageCount;

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
        // присвоение стартовой обложки (no-cover.jpg)
        String path = "src/main/resources/static/images/no-cover.jpg";
        try {
            this.image = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BookEntity toBookEntity(AuthorEntityService authorService, GenreEntityService genreService, PublisherEntityService publisherService) {
        BookEntity entity = new BookEntity();

        entity.setId(id);
        entity.setName(name);
        entity.setPageCount(pageCount);
        entity.setIsbn(isbn);
        entity.setGenre(genreService.search(genre).get(0));
        entity.setAuthor(authorService.search(author).get(0));
        entity.setPublisher(publisherService.search(publisher).get(0));
        entity.setAvgRating(avgRating);
        entity.setTotalRating(totalRating);
        entity.setViewCount(viewCount);
        entity.setDescription(description);

        if (uploadedImage != null && uploadedImage.getSize() > 199)
            try {
                entity.setImage(uploadedImage.getBytes());
            } catch (IOException e) {e.printStackTrace();}

        if (uploadedContent != null && uploadedContent.getSize() > 199)
            try {
                entity.setContent(uploadedContent.getBytes());
            } catch (IOException e) {e.printStackTrace();}

        return entity;
    }

    @Override
    public String toString() {
        return name;
    }

}
