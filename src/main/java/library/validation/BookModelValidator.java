package library.validation;

import library.model.BookModel;
import library.utils.MessLocalizer;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Component
public class BookModelValidator implements Validator {

    // вызывается валидатором перед проверкой, чтобы определить предназначен ли этот валидатор для данного объекта
    // валидатор предназначен только для валидации объектов класса BookModel (для остальных классов - false)
    @Override
    public boolean supports(Class<?> aClass) {
        return BookModel.class.equals(aClass);
    }

    // o - объект, который нужно провалидировать
    // errors - объект ошибок, в который нужно записать ошибка, если таковые есть
    @Override
    public void validate(Object o, Errors errors) {
        BookModel model = (BookModel) o;

        // проверка правильности заполнения полей и сбор ошибок в Errors (если имеются)
        // параметры метода rejectValue:
        //      s  - имя поля, с которым связана ошибка,
        //      s1 - код ошибки,
        //      s2 - сообщение для этой ошибки.

        // имя
        if (model.getName() == null || model.getName().isEmpty())
            errors.rejectValue("name", "", MessLocalizer.get("required_book_name"));

        // количество страниц
        String pageCountStr = model.getPageCount();
        if (pageCountStr == null || pageCountStr.isEmpty())
            errors.rejectValue("pageCount", "", MessLocalizer.get("required_page_count"));
        else if (pageCountStr.matches(".*\\D.*") || !isDigit(pageCountStr))
            errors.rejectValue("pageCount", "", MessLocalizer.get("page_count_format"));
        else if (Integer.parseInt(pageCountStr) == 0)
            errors.rejectValue("pageCount", "page_count_zero", MessLocalizer.get("page_count_zero"));
        else if (Integer.parseInt(pageCountStr) > 5000)
            errors.rejectValue("pageCount", "page_count_too_much", MessLocalizer.get("page_count_too_much"));

        // ISBN
        if (model.getIsbn() == null || model.getIsbn().isEmpty())
            errors.rejectValue("isbn", "required_isbn", MessLocalizer.get("required_isbn"));
        else {
            String isbn = model.getIsbn();
            String isbnTrim = isbn.replaceAll("-", "");
            if (isbnTrim.replaceAll("\\d", "").length() != 0)
                errors.rejectValue("isbn", "isbn_extraneous_characters", MessLocalizer.get("isbn_extraneous_characters"));
            else if ((isbnTrim.length() - isbn.length()) > 4)
                errors.rejectValue("isbn", "isbn_many_hyphens", MessLocalizer.get("isbn_many_hyphens"));
            else if (isbnTrim.length() < 10 || isbnTrim.length() > 13)
                errors.rejectValue("isbn", "isbn_invalid", MessLocalizer.get("isbn_invalid"));
        }

        // жанр
        if (model.getGenre() == null || model.getGenre().isEmpty())
            errors.rejectValue("genre", "required_genre", MessLocalizer.get("required_genre"));

        // автор
        if (model.getAuthor() == null || model.getAuthor().isEmpty())
            errors.rejectValue("author", "required_author", MessLocalizer.get("required_author"));

        // издатель
        if (model.getPublisher() == null || model.getPublisher().isEmpty())
            errors.rejectValue("publisher", "required_publisher", MessLocalizer.get("required_publisher"));

        // год издания
        String pubYearStr = model.getPublishYear();
        if (pubYearStr == null || pubYearStr.isEmpty())
            errors.rejectValue("publishYear", "required_year", MessLocalizer.get("required_year"));
        else if (pubYearStr.matches(".*\\D.*") || !isDigit(pubYearStr))
            errors.rejectValue("publishYear", "", MessLocalizer.get("year_format"));
        else {
            int year = LocalDate.now().getYear();
            Integer pubYearInt = Integer.parseInt(model.getPublishYear());
            if (pubYearInt < 400 || pubYearInt > year)
                errors.rejectValue("isbn", "year_invalid", MessLocalizer.get("year_invalid"));
        }

        // описание
        String description = model.getDescription();
        if (description.length() > 500) {
            String mess = MessLocalizer.get("description_size").replace("*%*", String.valueOf(description.length()));
            errors.rejectValue("description", "description_size", mess);
        }

        // проверка наличия изображения (обложки)
        if (model.getImage() == null) {
            MultipartFile uploadedImg = model.getUploadedImage();
            if (uploadedImg == null || uploadedImg.getSize() < 199)
                errors.rejectValue("uploadedImage", "required_img", MessLocalizer.get("required_img"));
        }
    }

    private boolean isDigit(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
