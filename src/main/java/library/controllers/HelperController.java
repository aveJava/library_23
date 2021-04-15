package library.controllers;

import library.domain.BookEntity;
import library.service.BookEntityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class HelperController {
    BookEntityService bookService;

    public HelperController(BookEntityService bookService) {
        this.bookService = bookService;
    }

    // Image-servlet - высылает изображение книги по ее id (id указывается в параметре id запроса)
    @GetMapping("/img")
    public String getImage(HttpServletResponse response, @RequestParam("id") int id, Model model) throws IOException {
        BookEntity book = bookService.get(id);

        byte[] imageBytes = book.getImage();
        response.setContentType("image/jpg");
        response.setContentLength(imageBytes.length);
        OutputStream os = response.getOutputStream();
        os.write(imageBytes);
        os.close();

        return "";
    }

}
