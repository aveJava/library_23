package library.controllers.entites;

import library.domain.GenreEntity;
import library.model.GenreModel;
import library.service.GenreEntityService;
import library.utils.MessLocalizer;
import library.validation.GenreModelValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/genres")
public class GenreEntityController {
    private final GenreEntityService genreService;
    private final GenreModelValidator validator;

    public GenreEntityController(GenreEntityService genreService, GenreModelValidator validator) {
        this.genreService = genreService;
        this.validator = validator;
    }

    // Отправляет форму создания нового жанра
    @GetMapping("/new")
    public String getCreateForm(RedirectAttributes redirectAttr) {
        redirectAttr.addFlashAttribute("ShowCreateNewEntryWindow", true);
        redirectAttr.addFlashAttribute("EditObj", new GenreModel());
        redirectAttr.addFlashAttribute("domain", "genres");

        return "redirect:/catalogs";
    }

    // Получает форму и создает новый жанр
    @PostMapping()
    public String create(@ModelAttribute("EditObj") GenreModel model,
                         RedirectAttributes redirectAttr) {

        boolean isValid = validateAndPrepareRedirectAttributesIfInvalid(model, redirectAttr);
        if (isValid) {
            GenreEntity entity = model.toGenreEntity();
            genreService.save(entity);
        } else {
            redirectAttr.addFlashAttribute("ShowErrorsModalWindow", true);
        }

        return "redirect:/catalogs";
    }

    // Отправляет форму редактирования жанра
    @GetMapping("/{id}/edit")
    public String getEditForm(@PathVariable("id") long id, RedirectAttributes redirectAttr) {
        redirectAttr.addFlashAttribute("EditingEntry", genreService.get(id).toGenreModel());
        redirectAttr.addFlashAttribute("editingTab", "GENRES");
        redirectAttr.addFlashAttribute("editingId", id);

        return "redirect:/catalogs";
    }

    // Принимает форму редактирования и обновляет жанр
    @PatchMapping("/{id}")
    public String edit(@PathVariable("id") long id,
                       @ModelAttribute("EditingEntry") GenreModel model,
                       RedirectAttributes redirectAttr) {

        boolean isValid = validateAndPrepareRedirectAttributesIfInvalid(model, redirectAttr);
        if (isValid) {
            GenreEntity entity = model.toGenreEntity();
            genreService.save(entity);
        } else {
            redirectAttr.addFlashAttribute("ShowErrorsModalWindow", true);
        }

        return "redirect:/catalogs";
    }

    // Удаляет жанр
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id, RedirectAttributes redirectAttr) {
        int count = genreService.get(id).getBooks().size();
        if (count != 0) {
            String mess = MessLocalizer.get("constraint_delete_genre");
            redirectAttr.addFlashAttribute("errors", List.of(mess));
            redirectAttr.addFlashAttribute("ShowErrorsModalWindow", true);
        } else genreService.delete(genreService.get(id));

        return "redirect:/catalogs";
    }


    // валидирует заполненную форму создания или редактирования жанра
    // если данные не валидны, подготавливает RedirectAttributes для перенаправления на повторное заполнение формы
    // возвращает true, если форма была заполнена правильно, false - если неправильно.
    public boolean validateAndPrepareRedirectAttributesIfInvalid(GenreModel model, RedirectAttributes redirectAttr) {
        DataBinder dataBinder = new DataBinder(model);
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
            redirectAttr.addFlashAttribute("errors", errorMessages);
            redirectAttr.addFlashAttribute("EditObj", model);
        }

        return !result.hasErrors();
    }
}