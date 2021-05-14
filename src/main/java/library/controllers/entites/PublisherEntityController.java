package library.controllers.entites;

import library.domain.PublisherEntity;
import library.model.PublisherModel;
import library.service.PublisherEntityService;
import library.utils.MessLocalizer;
import library.validation.PublisherModelValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/publishers")
public class PublisherEntityController {
    private final PublisherEntityService publisherService;
    private final PublisherModelValidator validator;

    public PublisherEntityController(PublisherEntityService publisherService, PublisherModelValidator validator) {
        this.publisherService = publisherService;
        this.validator = validator;
    }

    // Отправляет форму создания нового издателя
    @GetMapping("/new")
    public String getCreateForm(RedirectAttributes redirectAttr) {
        redirectAttr.addFlashAttribute("ShowCreateNewEntryWindow", true);
        redirectAttr.addFlashAttribute("EditObj", new PublisherModel());
        redirectAttr.addFlashAttribute("domain", "publishers");

        return "redirect:/catalogs";
    }

    // Получает форму и создает нового издателя
    @PostMapping()
    public String create(@ModelAttribute("EditObj") PublisherModel model,
                         RedirectAttributes redirectAttr) {

        boolean isValid = validateAndPrepareRedirectAttributesIfInvalid(model, redirectAttr);
        if (isValid) {
            PublisherEntity entity = model.toPublisherEntity();
            publisherService.save(entity);
        } else {
            redirectAttr.addFlashAttribute("ShowErrorsModalWindow", true);
        }

        return "redirect:/catalogs";
    }

    // Отправляет форму редактирования издателя
    @GetMapping("/{id}/edit")
    public String getPublisherEditForm(@PathVariable("id") long id, RedirectAttributes redirectAttr) {
        redirectAttr.addFlashAttribute("EditingEntry", publisherService.get(id).toPublisherModel());
        redirectAttr.addFlashAttribute("editingTab", "PUBLISHERS");
        redirectAttr.addFlashAttribute("editingId", id);

        return "redirect:/catalogs";
    }

    // Принимает форму редактирования и обновляет издателя
    @PatchMapping("/{id}")
    public String edit(@PathVariable("id") long id,
                           @ModelAttribute("EditingEntry") PublisherModel model,
                           RedirectAttributes redirectAttr) {

        boolean isValid = validateAndPrepareRedirectAttributesIfInvalid(model, redirectAttr);
        if (isValid) {
            PublisherEntity entity = model.toPublisherEntity();
            publisherService.save(entity);
        } else {
            redirectAttr.addFlashAttribute("ShowErrorsModalWindow", true);
        }

        return "redirect:/catalogs";
    }

    // Удаляет издателя
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id, RedirectAttributes redirectAttr) {
        int count = publisherService.get(id).getBooks().size();
        if (count != 0) {
            String mess = MessLocalizer.get("constraint_delete_publisher");
            redirectAttr.addFlashAttribute("errors", List.of(mess));
            redirectAttr.addFlashAttribute("ShowErrorsModalWindow", true);
        } else publisherService.delete(publisherService.get(id));

        return "redirect:/catalogs";
    }


    // валидирует заполненную форму создания или редактирования издателя
    // если данные не валидны, подготавливает RedirectAttributes для перенаправления на повторное заполнение формы
    // возвращает true, если форма была заполнена правильно, false - если неправильно.
    public boolean validateAndPrepareRedirectAttributesIfInvalid(PublisherModel model, RedirectAttributes redirectAttr) {
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