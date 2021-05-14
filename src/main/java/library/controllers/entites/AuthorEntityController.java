package library.controllers.entites;

import library.domain.AuthorEntity;
import library.model.AuthorModel;
import library.service.AuthorEntityService;
import library.utils.MessLocalizer;
import library.validation.AuthorModelValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/authors")
public class AuthorEntityController {
    private final AuthorEntityService authorService;
    private final AuthorModelValidator validator;

    public AuthorEntityController(AuthorEntityService authorService, AuthorModelValidator validator) {
        this.authorService = authorService;
        this.validator = validator;
    }

    // Отправляет форму создания нового автора
    @GetMapping("/new")
    public String getCreateForm(RedirectAttributes redirectAttr) {
        redirectAttr.addFlashAttribute("ShowCreateNewEntryWindow", true);
        redirectAttr.addFlashAttribute("EditObj", new AuthorModel());
        redirectAttr.addFlashAttribute("domain", "authors");

        return "redirect:/catalogs";
    }

    // Получает форму и создает нового автора
    @PostMapping()
    public String create(@ModelAttribute("EditObj") AuthorModel model,
                         RedirectAttributes redirectAttr) {

        boolean isValid = validateAndPrepareRedirectAttributesIfInvalid(model, redirectAttr);
        if (isValid) {
            AuthorEntity entity = model.toAuthorEntity();
            authorService.save(entity);
        } else {
            redirectAttr.addFlashAttribute("ShowErrorsModalWindow", true);
        }

        return "redirect:/catalogs";
    }

    // Отправляет форму редактирования автора
    @GetMapping("/{id}/edit")
    public String getEditForm(@PathVariable("id") long id, RedirectAttributes redirectAttr) {
        redirectAttr.addFlashAttribute("EditingEntry", authorService.get(id).toAuthorModel());
        redirectAttr.addFlashAttribute("editingTab", "AUTHORS");
        redirectAttr.addFlashAttribute("editingId", id);

        return "redirect:/catalogs";
    }

    // Принимает форму редактирования и обновляет автора
    @PatchMapping("/{id}")
    public String edit(@PathVariable("id") long id,
                       @ModelAttribute("EditingEntry") AuthorModel model,
                       RedirectAttributes redirectAttr) {

        boolean isValid = validateAndPrepareRedirectAttributesIfInvalid(model, redirectAttr);
        if (isValid) {
            AuthorEntity entity = model.toAuthorEntity();
            authorService.save(entity);
        } else {
            redirectAttr.addFlashAttribute("ShowErrorsModalWindow", true);
        }

        return "redirect:/catalogs";
    }

    // Удаляет автора
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id, RedirectAttributes redirectAttr) {
        int count = authorService.get(id).getBooks().size();
        if (count != 0) {
            String mess = MessLocalizer.get("constraint_delete_author");
            redirectAttr.addFlashAttribute("errors", List.of(mess));
            redirectAttr.addFlashAttribute("ShowErrorsModalWindow", true);
        } else authorService.delete(authorService.get(id));

        return "redirect:/catalogs";
    }


    // валидирует заполненную форму создания или редактирования автора
    // если данные не валидны, подготавливает RedirectAttributes для перенаправления на повторное заполнение формы
    // возвращает true, если форма была заполнена правильно, false - если неправильно.
    public boolean validateAndPrepareRedirectAttributesIfInvalid(AuthorModel model, RedirectAttributes redirectAttr) {
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