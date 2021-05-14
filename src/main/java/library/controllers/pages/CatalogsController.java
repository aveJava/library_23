package library.controllers.pages;

import library.controllers.security.UserController;
import library.domain.AuthorEntity;
import library.domain.GenreEntity;
import library.domain.PublisherEntity;
import library.service.AuthorEntityService;
import library.service.GenreEntityService;
import library.service.PublisherEntityService;
import library.utils.TableFillCounter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/catalogs")
public class CatalogsController {
    /** Инстансы используемых сервисов */
    private final AuthorEntityService authorService;
    private final GenreEntityService genreService;
    private final PublisherEntityService publisherService;

    private final UserController userController;

    /** Состояние страницы */
    private Tab currentTab;                         // активная (текущая) вкладка
    // для вкладки Авторы
    private int authTabPageNumber;           // номер текущей страницы (начиная с 1)
    private int authTabPageSize;             // кол-во записей (строк) на одной странице
    private int authTabMaxPageNumber;        // сколько всего страниц
    private long authTabTotalElements;       // сколько всего элементов (на всех страницах)
    // для вкладки Жанры
    private int genTabPageNumber;
    private int genTabPageSize;
    private int genTabMaxPageNumber;
    private long genTabTotalElements;
    // для вкладки Издатели
    private int pubTabPageNumber;
    private int pubTabPageSize;
    private int pubTabMaxPageNumber;
    private long pubTabTotalElements;

    public CatalogsController(AuthorEntityService authorService, GenreEntityService genreService,
                              PublisherEntityService publisherService, UserController userController) {
        this.authorService = authorService;
        this.genreService = genreService;
        this.publisherService = publisherService;

        this.userController = userController;

        // настройки первого посещения (отображаем вкладку Авторы, первую страницу, 10 элементов на странице)
        this.currentTab = Tab.AUTHORS;
        authTabPageNumber = genTabPageNumber = pubTabPageNumber = 1;
        authTabPageSize = genTabPageSize = pubTabPageSize = 10;
    }

    // отображает страницу, согласно ее текущему состоянию
    @GetMapping
    public String displayCatalogsPage(Model model) {
        model.addAttribute("tab", currentTab);
        model.addAttribute("userController", userController);
        model.addAttribute("tableFillCounter", new TableFillCounter());     // используется для закраски таблиц зеброй

        addTableContentsToModel(model);     // добавление содержимого таблиц
        addToolbarOptions(model);           // настройка toolbar

        return "pages/catalogs";
    }

    // слушает кнопки тублара, меняет состояние страницы согласно пришедшему запросу
    @GetMapping("/toolbar/{tab}/{button}")
    public String toolbar(@PathVariable("tab") Tab tab, @PathVariable("button") String button,
                          @RequestParam(value = "title", required = false) String title,
                          @RequestParam(value = "size", required = false) Integer size) {

        if (tab == null || button == null) return "redirect:/catalogs";

        switch (tab) {
            case AUTHORS:
                if (button.equals("NumberButtons")) {
                    switch (title) {
                        case "<<":
                            authTabPageNumber = 1;
                            break;
                        case "<":
                            if (authTabPageNumber > 1) authTabPageNumber--;
                            break;
                        case ">":
                            authTabPageNumber++;
                            break;
                        case ">>":
                            authTabPageNumber = authTabMaxPageNumber;
                            break;
                    }
                }
                if (button.equals("PageSize")) {
                    authTabPageSize = size;
                }
                break;
            case GENRES:
                if (button.equals("NumberButtons")) {
                    switch (title) {
                        case "<<":
                            genTabPageNumber = 1;
                            break;
                        case "<":
                            if (genTabPageNumber > 1) genTabPageNumber--;
                            break;
                        case ">":
                            genTabPageNumber++;
                            break;
                        case ">>":
                            genTabPageNumber = genTabMaxPageNumber;
                            break;
                    }
                }
                if (button.equals("PageSize")) {
                    genTabPageSize = size;
                }
                break;
            case PUBLISHERS:
                if (button.equals("NumberButtons")) {
                    switch (title) {
                        case "<<":
                            pubTabPageNumber = 1;
                            break;
                        case "<":
                            if (pubTabPageNumber > 1) pubTabPageNumber--;
                            break;
                        case ">":
                            pubTabPageNumber++;
                            break;
                        case ">>":
                            pubTabPageNumber = pubTabMaxPageNumber;
                            break;
                    }
                }
                if (button.equals("PageSize")) {
                    pubTabPageSize = size;
                }
                break;
        }

        return "redirect:/catalogs";
    }

    // переключает вкладку
    @GetMapping("/switch")
    public String switchTab(@RequestParam("tab") Tab tab) {
        switch (tab) {
            case AUTHORS:
                currentTab = Tab.AUTHORS;
                break;
            case GENRES:
                currentTab = Tab.GENRES;
                break;
            case PUBLISHERS:
                currentTab = Tab.PUBLISHERS;
                break;
        }

        return "redirect:/catalogs";
    }

    /** Вспомогательные методы */

    // добавляет в указанную модель все атрибуты, необходимые для работы toolbar
    private void addToolbarOptions(Model model) {
        switch (currentTab) {
            case AUTHORS:
                model.addAttribute("maxPage", authTabMaxPageNumber);
                model.addAttribute("thisPage", authTabPageNumber);
                model.addAttribute("pageSize", authTabPageSize);
                model.addAttribute("totalElements", authTabTotalElements);
                break;
            case GENRES:
                model.addAttribute("maxPage", genTabMaxPageNumber);
                model.addAttribute("thisPage", genTabPageNumber);
                model.addAttribute("pageSize", genTabPageSize);
                model.addAttribute("totalElements", genTabTotalElements);
                break;
            case PUBLISHERS:
                model.addAttribute("maxPage", pubTabMaxPageNumber);
                model.addAttribute("thisPage", pubTabPageNumber);
                model.addAttribute("pageSize", pubTabPageSize);
                model.addAttribute("totalElements", pubTabTotalElements);
                break;
        }
    }

    // добавлеяет в модель данные отображаемого стравочника (даннные об авторах, жанрах или издателях)
    private void addTableContentsToModel(Model model) {
        String locale = LocaleContextHolder.getLocale().toString();
        String sortField;    // поле по которому будут сортироваться записи в Pageble

        switch (currentTab) {
            case AUTHORS:
                sortField = locale.equals("ru") ? "ruFio" : "enFio";
                Page<AuthorEntity> pageAuth = authorService.getAll(authTabPageNumber - 1, authTabPageSize, sortField, Sort.Direction.ASC);
                model.addAttribute("authors", pageAuth);
                authTabMaxPageNumber = pageAuth.getTotalPages();
                authTabTotalElements = pageAuth.getTotalElements();
                break;
            case GENRES:
                sortField = locale.equals("ru") ? "ruName" : "enName";
                Page<GenreEntity> pageGen = genreService.getAll(genTabPageNumber - 1, genTabPageSize, sortField, Sort.Direction.ASC);
                model.addAttribute("genres", pageGen);
                genTabMaxPageNumber = pageGen.getTotalPages();
                genTabTotalElements = pageGen.getTotalElements();
                break;
            case PUBLISHERS:
                sortField = locale.equals("ru") ? "enName" : "enName";
                Page<PublisherEntity> pagePab = publisherService.getAll(pubTabPageNumber - 1, pubTabPageSize, sortField, Sort.Direction.ASC);
                model.addAttribute("publishers", pagePab);
                pubTabMaxPageNumber = pagePab.getTotalPages();
                pubTabTotalElements = pagePab.getTotalElements();
                break;
        }
    }
}

// вкладки на странице catalogs
enum Tab {
    AUTHORS,
    GENRES,
    PUBLISHERS;
}
