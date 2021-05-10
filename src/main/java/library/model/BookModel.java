package library.model;

import library.domain.BookEntity;
import library.service.AuthorEntityService;
import library.service.BookEntityService;
import library.service.GenreEntityService;
import library.service.PublisherEntityService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class BookModel {

    private Long id;

    private String name;

    private byte[] content;

    private MultipartFile uploadedContent;

    private String pageCount;

    private String isbn;

    private String genre;

    private String author;

    private String publisher;

    @NotNull(message = "Укажите год издания")
    private String publishYear;

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

    public BookEntity toBookEntity(AuthorEntityService authorService, BookEntityService bookService, GenreEntityService genreService, PublisherEntityService publisherService) {
        BookEntity entity = new BookEntity();

        entity.setId(id);
        entity.setName(name);
        entity.setPageCount(Integer.parseInt(pageCount));
        entity.setIsbn(isbn);
        entity.setGenre(genreService.search(genre).get(0));
        entity.setAuthor(authorService.search(author).get(0));
        entity.setPublisher(publisherService.search(publisher).get(0));
        entity.setPublishYear(Integer.parseInt(publishYear));
        entity.setAvgRating(avgRating);
        entity.setTotalVoteCount(totalVoteCount);
        entity.setTotalRating(totalRating);
        entity.setViewCount(viewCount);
        entity.setDescription(description);

        if (uploadedImage != null && uploadedImage.getSize() > 199)
            try {
                entity.setImage(uploadedImage.getBytes());
            } catch (IOException e) {e.printStackTrace();}
        else entity.setImage(image);

        if (uploadedContent != null && uploadedContent.getSize() > 199)
            try {
                entity.setContent(uploadedContent.getBytes());
            } catch (IOException e) {e.printStackTrace();}
        else if (entity.getId() != null)
            entity.setContent(bookService.getContent(entity.getId()));

        return entity;
    }

    @Override
    public String toString() {
        return name;
    }

}
