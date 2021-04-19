package library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorPageController {
    private String errName;
    private String errMessage;

    // отображает страницу ошибок согласно ее текущему состоянию
    @GetMapping("/errors")
    public String base1(@RequestParam(value = "name", required = false) String name, Model model) {
        name = name == null ? "" : name;

        switch (name) {
            case "pdf_not_found" :
                errName = "PDF-файл не найден!";
                errMessage = "Очевидно, для этой книги не загружен контент. Вы можете сообщить об этом администратору через форму обратной связи.";
                break;
            default:
                errName = "Произошла непредвиденная ошибка!";
                errMessage = "Попробуйте сделать то же самое через несколько минут, возможно, это поможет.";
                break;
        }

        model.addAttribute("errName", errName);
        model.addAttribute("errMessage", errMessage);

        return "pages/error";
    }

}
