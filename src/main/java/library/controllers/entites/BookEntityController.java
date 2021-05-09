package library.controllers.entites;

import library.domain.BookEntity;
import library.model.BookModel;
import library.service.AuthorEntityService;
import library.service.BookEntityService;
import library.service.GenreEntityService;
import library.service.PublisherEntityService;
import library.validation.BookModelValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookEntityController {
    /** Инстансы используемых сервисов */
    final AuthorEntityService authorService;
    final BookEntityService bookService;
    final GenreEntityService genreService;
    final PublisherEntityService publisherService;

    final BookModelValidator validator;

    public BookEntityController(AuthorEntityService authorService, BookEntityService bookService,
                                GenreEntityService genreService, PublisherEntityService publisherService,
                                BookModelValidator validator) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.genreService = genreService;
        this.publisherService = publisherService;
        this.validator = validator;
    }

    // Отправляет форму на создание книги
    @GetMapping("/books/new")
    public String getBookCreateForm(RedirectAttributes redirectAttr) {
        BookModel book = new BookModel();
        redirectAttr.addFlashAttribute("EditableBook", book);
        redirectAttr.addFlashAttribute("actionURL", "/books");
        redirectAttr.addFlashAttribute("actionMethod", "POST");
        redirectAttr.addFlashAttribute("ShowEditModelWindow", true);
        redirectAttr.addFlashAttribute("allAuthors", authorService.getAll());
        redirectAttr.addFlashAttribute("allPublishers", publisherService.getAll());
        return "redirect:/main_page";
    }

    // Создает новую книгу
    @PostMapping(value = "/books", consumes = { "multipart/form-data" })
    public String createBook(@ModelAttribute("EditableBook") BookModel model,
                             RedirectAttributes redirectAttr) {

        boolean isValid = validateAndPrepareRedirectAttributesIfInvalid(model, redirectAttr);
        if (isValid) {
            BookEntity entity = model.toBookEntity(authorService, genreService, publisherService);
            bookService.save(entity);
        } else {
            redirectAttr.addFlashAttribute("actionMethod", "POST");     // метод, которым отправлять форму после редактирования
            redirectAttr.addFlashAttribute("actionURL", "/books");      // адрес, на который отправлять форму
        }

        return "redirect:/main_page";
    }

    // Отправляет форму на редактирование книги
    @GetMapping("/books/{id}/edit")
    public String getBookEditForm(@PathVariable("id") long id, RedirectAttributes redirectAttr) {
        BookModel book = bookService.get(id).toBookModel();
        redirectAttr.addFlashAttribute("EditableBook", book);
        redirectAttr.addFlashAttribute("ShowEditModelWindow", true);
        redirectAttr.addFlashAttribute("actionURL", "/books/" + book.getId());
        redirectAttr.addFlashAttribute("actionMethod", "PATCH");
        redirectAttr.addFlashAttribute("allAuthors", authorService.getAll());
        redirectAttr.addFlashAttribute("allPublishers", publisherService.getAll());
        return "redirect:/main_page";
    }

    // Обновляет книгу (принимает заполненную форму на редактирование)
    @PatchMapping(value = "/books/{id}", consumes = { "multipart/form-data" })
    public String bookEdit(@PathVariable("id") long id,
                           @ModelAttribute("EditableBook") BookModel model,
                           RedirectAttributes redirectAttr) {

        boolean isValid = validateAndPrepareRedirectAttributesIfInvalid(model, redirectAttr);
        if (isValid) {
            BookEntity entity = model.toBookEntity(authorService, genreService, publisherService);
            bookService.save(entity);
        } else {
            redirectAttr.addFlashAttribute("actionMethod", "PATCH");                     // метод, которым отправлять форму после редактирования
            redirectAttr.addFlashAttribute("actionURL", "/books/" + model.getId());      // адрес, на который отправлять форму
        }

        return "redirect:/main_page";
    }

    // Удаляет книгу
    @DeleteMapping("/books/{id}")
    public String deleteBook(@PathVariable("id") long id) {
        bookService.delete(bookService.get(id));
        return "redirect:/main_page";
    }


    /** Методы для получения отдельных полей книги */

    // Предоставляет обложку книги по ее id
    @GetMapping("/books/img")
    public void getImage(HttpServletResponse response, @RequestParam("id") int id) throws IOException {
        BookEntity book = bookService.get(id);

        byte[] imageBytes = book.getImage();
        if (imageBytes == null) return;
        response.setContentType("image/jpg");
        response.setContentLength(imageBytes.length);
        OutputStream os = response.getOutputStream();
        os.write(imageBytes);
        os.close();
    }

    // Предоставляет содержание книги (pdf) по ее id
    @GetMapping("/books/content")
    public void getContent(HttpServletResponse response, @RequestParam("id") int id) throws IOException {
        BookEntity book = bookService.get(id);

        byte[] contentBytes = book.getContent();
        if (contentBytes == null) return;
        response.setContentType("application/pdf");
        response.setContentLength(contentBytes.length);
        OutputStream os = response.getOutputStream();
        os.write(contentBytes);
        os.close();

        bookService.updateViewCount(id, book.getViewCount() + 1);
    }

    /** Вспомогательные методы контроллера */

    // валидирует заполненную форму создания или редактирования книги
    // если данные не валидны, подготавливает RedirectAttributes для перенаправления на повторное заполнение формы
    // возвращает true, если форма была заполнена правильно, false - если неправильно.
    public boolean validateAndPrepareRedirectAttributesIfInvalid(BookModel model, RedirectAttributes redirectAttr) {
        final DataBinder dataBinder = new DataBinder(model);
        dataBinder.addValidators(validator);
        dataBinder.validate();
        BindingResult result = dataBinder.getBindingResult();

        // создаем список сообщений об ошибках, отправляемый пользователю
        List<String> errorMessages = new ArrayList<>();
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        if (result.hasErrors()) {
            // передаем контроллеру, вызываемому по redirect, список ошибок и прочие данные, необходимые для повторного редактирования объекта
            redirectAttr.addFlashAttribute("errors", errorMessages);                        // список сообщений об ошибках
            redirectAttr.addFlashAttribute("EditableBook", model);                          // редактуруемый объект
            redirectAttr.addFlashAttribute("ShowEditModelWindow", true);                 // показывать модальное окно редактирования
            redirectAttr.addFlashAttribute("allAuthors", authorService.getAll());           // список авторов
            redirectAttr.addFlashAttribute("allPublishers", publisherService.getAll());     // список издательств
        }

        return !result.hasErrors();
    }
}