package library.controllers.entites;

import library.domain.BookEntity;
import library.model.BookModel;
import library.service.AuthorEntityService;
import library.service.BookEntityService;
import library.service.GenreEntityService;
import library.service.PublisherEntityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookEntityController {
    /** Инстансы используемых сервисов */
    AuthorEntityService authorService;
    BookEntityService bookService;
    GenreEntityService genreService;
    PublisherEntityService publisherService;

    public BookEntityController(AuthorEntityService authorService, BookEntityService bookService,
                                GenreEntityService genreService, PublisherEntityService publisherService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.genreService = genreService;
        this.publisherService = publisherService;
    }

    // Отправляет форму на редактирование книги
    @GetMapping("/books/{id}/edit")
    public String getBookEditForm(@PathVariable("id") long id, RedirectAttributes redirectAttr) {
        BookModel book = new BookModel(bookService.get(id));
        redirectAttr.addFlashAttribute("EditableBook", book);
        redirectAttr.addFlashAttribute("ShowEditModelWindow", true);
        redirectAttr.addFlashAttribute("allAuthors", authorService.getAll());
        redirectAttr.addFlashAttribute("allPublishers", publisherService.getAll());
        return "redirect:/";
    }

    // Обновляет книгу (принимает заполненную форму на редактирование)
    @PatchMapping(value = "/books/{id}", consumes = { "multipart/form-data" })
    public String bookEdit(@PathVariable("id") long id,
                           @ModelAttribute("EditableBook") @Valid BookModel model,
                           BindingResult binding, RedirectAttributes redirectAttr) {

        // создаем список сообщений об ошибках, отправляемый пользователю
        List<String> errorMessages = new ArrayList<>();

        if (!model.isHasImage()) errorMessages.add("Загрузите обложку книги (jpg, png или gif не менее 200 байт)");
        if (binding.hasErrors()) {
            for (ObjectError error : binding.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        // если форма заполнена правильно - сохраняем объект, иначе перенаправляем пользователя снова на страницу редактированя
        if (errorMessages.isEmpty()) {
            // если форма была заполнена правильно, сохраняем данные в БД
            BookEntity book = new BookEntity(model, bookService, genreService, authorService, publisherService);
            bookService.save(book);
        } else {
            // передаем контроллеру, вызываемому по redirect, список ошибок и прочие данные, необходимые для повторного редактирования объекта
            redirectAttr.addFlashAttribute("errors", errorMessages);                        // список сообщений об ошибках
            redirectAttr.addFlashAttribute("EditableBook", model);                          // редактуруемый объект
            redirectAttr.addFlashAttribute("ShowEditModelWindow", true);                 // показывать модальное окно редактирования
            redirectAttr.addFlashAttribute("allAuthors", authorService.getAll());           // список авторов
            redirectAttr.addFlashAttribute("allPublishers", publisherService.getAll());     // список издательств
        }

        return "redirect:/";
    }

    // Удаляет книгу
    @DeleteMapping("/books/{id}")
    public String deleteBook(@PathVariable("id") long id) {
        bookService.delete(bookService.get(id));
        return "redirect:/";
    }


    /** Методы для получения отдельных полей книги */

    // Предоставляет обложку книги по ее id
    @GetMapping("/books/img")
    public void getImage(HttpServletResponse response, @RequestParam("id") int id, Model model) throws IOException {
        BookEntity book = bookService.get(id);

        byte[] imageBytes = book.getImage();
        response.setContentType("image/jpg");
        response.setContentLength(imageBytes.length);
        OutputStream os = response.getOutputStream();
        os.write(imageBytes);
        os.close();
    }

    // Предоставляет содержание книги (pdf) по ее id
    @GetMapping("/books/content")
    public void getContent(HttpServletResponse response, @RequestParam("id") int id, Model model) throws IOException {
        BookEntity book = bookService.get(id);

        byte[] contentBytes = book.getContent();
        response.setContentType("application/pdf");
        response.setContentLength(contentBytes.length);
        OutputStream os = response.getOutputStream();
        os.write(contentBytes);
        os.close();
    }

}
