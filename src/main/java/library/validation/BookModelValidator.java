package library.validation;

import library.model.BookModel;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Locale;

@Component
public class BookModelValidator implements Validator {
    // используется для локализации сообщений
    final MessageSource messageSource;

    public BookModelValidator(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

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
            errors.rejectValue("name", "", getLocalizedMess("required_book_name"));

        // количество страниц
        String pageCountStr = model.getPageCount();
        if (pageCountStr == null || pageCountStr.isEmpty())
            errors.rejectValue("pageCount", "", getLocalizedMess("required_page_count"));
        else if (pageCountStr.matches(".*\\D.*") || !isDigit(pageCountStr))
            errors.rejectValue("pageCount", "", getLocalizedMess("page_count_format"));
        else if (Integer.parseInt(pageCountStr) == 0)
            errors.rejectValue("pageCount", "page_count_zero", getLocalizedMess("page_count_zero"));
        else if (Integer.parseInt(pageCountStr) > 5000)
            errors.rejectValue("pageCount", "page_count_too_much", getLocalizedMess("page_count_too_much"));

        // ISBN
        if (model.getIsbn() == null || model.getIsbn().isEmpty())
            errors.rejectValue("isbn", "required_isbn", getLocalizedMess("required_isbn"));
        else {
            String isbn = model.getIsbn();
            String isbnTrim = isbn.replaceAll("-", "");
            if (isbnTrim.replaceAll("\\d", "").length() != 0)
                errors.rejectValue("isbn", "isbn_extraneous_characters", getLocalizedMess("isbn_extraneous_characters"));
            else if ((isbnTrim.length() - isbn.length()) > 4)
                errors.rejectValue("isbn", "isbn_many_hyphens", getLocalizedMess("isbn_many_hyphens"));
            else if (isbnTrim.length() < 10 || isbnTrim.length() > 13)
                errors.rejectValue("isbn", "isbn_invalid", getLocalizedMess("isbn_invalid"));
        }

        // жанр
        if (model.getGenre() == null || model.getGenre().isEmpty())
            errors.rejectValue("genre", "required_genre", getLocalizedMess("required_genre"));

        // автор
        if (model.getAuthor() == null || model.getAuthor().isEmpty())
            errors.rejectValue("author", "required_author", getLocalizedMess("required_author"));

        // издатель
        if (model.getPublisher() == null || model.getPublisher().isEmpty())
            errors.rejectValue("publisher", "required_publisher", getLocalizedMess("required_publisher"));

        // год издания
        String pubYearStr = model.getPublishYear();
        if (pubYearStr == null || pubYearStr.isEmpty())
            errors.rejectValue("publishYear", "required_year", getLocalizedMess("required_year"));
        else if (pubYearStr.matches(".*\\D.*") || !isDigit(pubYearStr))
            errors.rejectValue("publishYear", "", getLocalizedMess("year_format"));
        else {
            int year = LocalDate.now().getYear();
            Integer pubYearInt = Integer.parseInt(model.getPublishYear());
            if (pubYearInt < 400 || pubYearInt > year)
                errors.rejectValue("isbn", "year_invalid", getLocalizedMess("year_invalid"));
        }

        // проверка наличия изображения (обложки)
        if (model.getImage() == null) {
            MultipartFile uploadedImg = model.getUploadedImage();
            if (uploadedImg == null || uploadedImg.getSize() < 199)
                errors.rejectValue("uploadedImage", "required_img", getLocalizedMess("required_img"));
        }
    }

    private String getLocalizedMess(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, locale);
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
