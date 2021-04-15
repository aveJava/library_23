package library.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class BookModel {

    private Long id;

    @NotEmpty(message = "Не должно быть безымянных книг")
    private String name;

    private byte[] content;

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

    private int avgRating;

    private long totalVoteCount;

    private long totalRating;

    private long viewCount;

    private String description;

    @Override
    public String toString() {
        return name;
    }

}
