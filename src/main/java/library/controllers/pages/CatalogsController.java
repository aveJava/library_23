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

import java.util.Optional;

@Controller
@RequestMapping("/catalogs")
public class CatalogsController {
    /** Инстансы используемых сервисов */
    private final AuthorEntityService authorService;
    private final GenreEntityService genreService;
    private final PublisherEntityService publisherService;

    private final UserController userController;

    /** Состояние страницы */
    private Tab currentTab;                  // активная (текущая) вкладка
    private Optional<String> keywords;       // ключевые слова поиска
    // для вкладки Авторы
    private int authTabPageNumber;           // номер текущей страницы (начиная с 1)
    private int authTabPageSize;             // кол-во записей (строк) на одной странице
    private int authTabMaxPageNumber;        // сколько всего страниц
    // для вкладки Жанры
    private int genTabPageNumber;
    private int genTabPageSize;
    private int genTabMaxPageNumber;
    // для вкладки Издатели
    private int pubTabPageNumber;
    private int pubTabPageSize;
    private int pubTabMaxPageNumber;

    public CatalogsController(AuthorEntityService authorService, GenreEntityService genreService,
                              PublisherEntityService publisherService, UserController userController) {
        this.authorService = authorService;
        this.genreService = genreService;
        this.publisherService = publisherService;

        this.userController = userController;

        // настройки первого посещения (отображаем вкладку Авторы, первую страницу, 10 элементов на странице)
        currentTab = Tab.AUTHORS;
        authTabPageNumber = genTabPageNumber = pubTabPageNumber = 1;
        authTabPageSize = genTabPageSize = pubTabPageSize = 10;
        keywords = Optional.empty();
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

    // принимает поисковые запросы
    @GetMapping("/search")
    public String search(@RequestParam(value = "keywords", required = false) String keywords){
        boolean isKeywordsPresent = keywords != null && !keywords.trim().isEmpty();
        this.keywords = isKeywordsPresent ? Optional.of(keywords) : Optional.empty();
        return "redirect:/catalogs";
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
        // очистка параметров поиска для данной вкладки
        if (keywords.isPresent()) {
            keywords = Optional.empty();
            switch (currentTab){
                case AUTHORS:
                    authTabPageNumber = 1;
                    break;
                case GENRES:
                    genTabPageNumber = 1;
                    break;
                case PUBLISHERS:
                    pubTabPageNumber = 1;
                    break;
            }
        }

        // переключение вкладки на новую
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
                break;
            case GENRES:
                model.addAttribute("maxPage", genTabMaxPageNumber);
                model.addAttribute("thisPage", genTabPageNumber);
                model.addAttribute("pageSize", genTabPageSize);
                break;
            case PUBLISHERS:
                model.addAttribute("maxPage", pubTabMaxPageNumber);
                model.addAttribute("thisPage", pubTabPageNumber);
                model.addAttribute("pageSize", pubTabPageSize);
                break;
        }
    }

    // добавлеяет в модель данные отображаемого стравочника (даннные об авторах, жанрах или издателях)
    private void addTableContentsToModel(Model model) {
        String locale = LocaleContextHolder.getLocale().toString();
        String sortField;    // поле по которому будут сортироваться записи в Pageble

        switch (currentTab) {
            case AUTHORS:
                Page<AuthorEntity> pageAuth;
                sortField = "ru".equals(locale) ? "ruFio" : "enFio";
                if (keywords.isPresent()) {
                    pageAuth = authorService.search(authTabPageNumber - 1, authTabPageSize, sortField, Sort.Direction.ASC, keywords.get());
                    model.addAttribute("searchMess", String.format("Найдено: %d (Ключевые слова: '%s')", pageAuth.getTotalElements(), keywords.get()));
                } else {
                    pageAuth = authorService.getAll(authTabPageNumber - 1, authTabPageSize, sortField, Sort.Direction.ASC);
                    model.addAttribute("searchMess", "Найдено: " + pageAuth.getTotalElements());
                }
                model.addAttribute("authors", pageAuth);
                authTabMaxPageNumber = pageAuth.getTotalPages();
                break;
            case GENRES:
                Page<GenreEntity> pageGen;
                sortField = locale.equals("ru") ? "ruName" : "enName";
                if (keywords.isPresent()) {
                    pageGen = genreService.search(genTabPageNumber - 1, genTabPageSize, sortField, Sort.Direction.ASC, keywords.get());
                    model.addAttribute("searchMess", String.format("Найдено: %d (Ключевые слова: '%s')", pageGen.getTotalElements(), keywords.get()));
                } else {
                    pageGen = genreService.getAll(genTabPageNumber - 1, genTabPageSize, sortField, Sort.Direction.ASC);
                    model.addAttribute("searchMess", "Найдено: " + pageGen.getTotalElements());
                }
                model.addAttribute("genres", pageGen);
                genTabMaxPageNumber = pageGen.getTotalPages();
                break;
            case PUBLISHERS:
                Page<PublisherEntity> pagePub;
                sortField = locale.equals("ru") ? "enName" : "enName";
                if (keywords.isPresent()) {
                    pagePub = publisherService.search(pubTabPageNumber - 1, pubTabPageSize, sortField, Sort.Direction.ASC, keywords.get());
                    model.addAttribute("searchMess", String.format("Найдено: %d (Ключевые слова: '%s')", pagePub.getTotalElements(), keywords.get()));
                } else {
                    pagePub = publisherService.getAll(pubTabPageNumber - 1, pubTabPageSize, sortField, Sort.Direction.ASC);
                    model.addAttribute("searchMess", "Найдено: " + pagePub.getTotalElements());
                }
                model.addAttribute("publishers", pagePub);
                pubTabMaxPageNumber = pagePub.getTotalPages();
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
