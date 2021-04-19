package library.controllers;

import library.domain.BookEntity;
import library.service.AuthorEntityService;
import library.service.BookEntityService;
import library.service.GenreEntityService;
import library.service.PublisherEntityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class HelperController {
    BookEntityService bookService;
    /** Инстансы используемых сервисов */
    AuthorEntityService authorService;
    GenreEntityService genreService;
    PublisherEntityService publisherService;

    public HelperController(BookEntityService bookService) {
        this.bookService = bookService;
    }

    // Image-servlet - высылает изображение книги по ее id (id указывается в параметре id запроса)
    @GetMapping("/img")
    public void getImage(HttpServletResponse response, @RequestParam("id") int id, Model model) throws IOException {
        BookEntity book = bookService.get(id);

        byte[] imageBytes = book.getImage();
        response.setContentType("image/jpg");
        response.setContentLength(imageBytes.length);
        OutputStream os = response.getOutputStream();
        os.write(imageBytes);
        os.close();

//        return "";
    }

    @GetMapping("/content")
    public void getContent(HttpServletResponse response, @RequestParam("id") int id, Model model) throws IOException {
        BookEntity book = bookService.get(id);

        byte[] contentBytes = book.getContent();
        response.setContentType("application/pdf");
        response.setContentLength(contentBytes.length);
        OutputStream os = response.getOutputStream();
        os.write(contentBytes);
        os.close();
    }

    // PDF-servlet - высылает содержание книги по ее id
    @GetMapping("/books/{id}/PDF_Content")
    public String getContent(@PathVariable("id") int id) {
        byte[] content = bookService.get(id).getContent();
        if (content != null && content.length > 0) {
            return "forward:/content?id=" + id;
        } else {
            return "redirect:/errors?name=pdf_not_found";
        }
    }


}
