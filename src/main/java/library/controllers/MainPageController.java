package library.controllers;

import library.domain.BookEntity;
import library.model.BookModel;
import library.service.AuthorEntityService;
import library.service.BookEntityService;
import library.service.GenreEntityService;
import library.service.PublisherEntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/** Контроллер, отвечающий за отображение главной страницы */
@Controller
public class MainPageController {
    /** Инстансы используемых сервисов */
    AuthorEntityService authorService;
    BookEntityService bookService;
    GenreEntityService genreService;
    PublisherEntityService publisherService;

    /** Состояние библиотеки */
    List<BookEntity> topBooks;      // топ книг (отображается на полке)
    Page<BookEntity> pageBooks;     // страница книг, которую нужно отобразить
    static int pageNumber;          // номер текущей страницы (начиная с 1)
    static int pageSize;            // кол-во книг на одной странице
    static int maxPageNumber;       // сколько всего страниц
    static long totalElements;      // сколько всего элементов (на всех страницах)
    // текущие критерии поиска
    SearchType searchType;          // тип используемого поиска
    long genreId;                   // id жанра (для поиска по жанру)
    String[] keywords;              // ключевые слова поиска (для поиска по keywords)

    public MainPageController(AuthorEntityService authorService, BookEntityService bookService,
                              GenreEntityService genreService, PublisherEntityService publisherService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.genreService = genreService;
        this.publisherService = publisherService;

        // настройки первого посещения (отображаем все книги, первую страницу, 10 элементов на странице)
        pageNumber = 1;
        pageSize = 10;
        searchType = SearchType.ALL;
    }

    // отображает главную страницу согласно ее текущему состоянию
    @GetMapping("/")
    public String baseUrlRedirect(Model model) {
        // формирование отображаемого контента
        topBooks = bookService.findTopBooks(5);    // выполняет поиск топовых книг
        search();   // выполняет поиск книг, которые нужно отобразить в библиотеке

        // формирование модели, отправляемой на front
        model.addAttribute("top", topBooks);                            // Содержимое топовой полки
        model.addAttribute("allGenres", genreService.getAll());         // Содержимое меню жанров
        model.addAttribute("pageBooks", pageBooks);                     // Содержимое библиотеки (текущая страница)
        model.addAttribute("maxPage", MainPageController.maxPageNumber);
        model.addAttribute("thisPage", MainPageController.pageNumber);
        model.addAttribute("pageSize", MainPageController.pageSize);
        model.addAttribute("totalElements", MainPageController.totalElements);
        model.addAttribute("SearchMessage", getSearchMessage());

        return "pages/main";
    }

    // слушает запросы на поиск книг (и меняет параметры поиска согласно полученному запросу)
    @GetMapping("/main_search")
    public String changeSearch(@RequestParam("type") String type,
                               @RequestParam(value = "keywords", required = false) String keywords,
                               @RequestParam(value = "genreId", required = false) Integer genreId) {
        pageNumber = 1;
        switch (type) {
            case ("all"):
                searchType = SearchType.ALL;
                break;
            case ("genre"):
                searchType = SearchType.SEARCH_GENRE;
                this.genreId = genreId;
                break;
            case ("keywords"):
                searchType = SearchType.SEARCH_KEYWORDS;
                this.keywords = keywords.split(" ");
                break;
        }

        return "redirect:/";
    }

    // выполняет поиск книг согласно текущим параметрам поиска, записывает результат в this.pageBooks
    public void search () {
        int pageNum = pageNumber - 1;    // контроллер страницы считает с 1, а Pageable с 0
        switch (searchType) {
            case ALL:
                pageBooks = bookService.getAll(pageNum, pageSize, "viewCount", Sort.Direction.ASC);
                break;
            case SEARCH_GENRE:
                pageBooks = bookService.findByGenre(pageNum, pageSize, "viewCount", Sort.Direction.DESC, genreId);
                break;
            case SEARCH_KEYWORDS:
                pageBooks = bookService.search(pageNum, pageSize, "viewCount", Sort.Direction.DESC, keywords);
                break;
        }
        maxPageNumber = pageBooks.getTotalPages();
        totalElements = pageBooks.getTotalElements();
    }

    // формирует сообщение о критериях, по которым был выполнен поиск, показываемое пользователю
    public String getSearchMessage() {
        String message;
        if (totalElements == 0) message = "Ничего не найдено";
        else message = "Найдено: " + getCorrectDeclension(totalElements);
        switch (searchType) {
            case SEARCH_GENRE:
                message += " (Жанр: '" + genreService.get(genreId).getName() + "')";
                break;
            case SEARCH_KEYWORDS:
                StringBuilder mess = new StringBuilder();
                for (int i=0; i<keywords.length; i++) {
                    mess.append(keywords[i] + " ");
                }
                message += " (Поиск: " + mess.toString().trim() + ")";
                break;
            default:
                ;
        }
        return message;
    }

    // возвращает число найденных книг с правильным склонением слова 'книга' (вспомогательный метод)
    public String getCorrectDeclension(long digit) {
        if (digit > 99) digit = digit - (digit/100 * 100);
        if (digit<15 && digit>9) return digit + " книг";

        int units = (int) (digit - (digit/10 * 10));
        if (units == 1) return digit + " книга";
        if (units < 5 && units > 1) return digit + " книги";
        if (units == 0 || units > 4) return digit + " книг";
        return "хз скоко книг";
    }

    // слушает кнопки toolbar'а, перелистывает страницу результатов или меняет ее размер
    @GetMapping("/Toolbar/{button}")
    public String toolbar(@PathVariable("button") String button,
                          @RequestParam(value = "title", required = false) String title,
                          @RequestParam(value = "size", required = false) Integer size) {

        if (button == null) return "";

        if (button.equals("NumberButtons")) {
            switch (title) {
                case "<<":
                    MainPageController.pageNumber = 1;
                    break;
                case "<":
                    if (MainPageController.pageNumber > 1) MainPageController.pageNumber--;
                    break;
                case ">":
                    MainPageController.pageNumber++;
                    break;
                case ">>":
                    MainPageController.pageNumber = MainPageController.maxPageNumber;
                    break;
            }
        }

        if (button.equals("PageSize")) {
            pageSize = size;
        }

        return "redirect:/";
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

    // Показывает диалог удаления книги (модальное окно)
    @GetMapping("/books/{id}/showDeleteDialog")
    public String showDeleteDialog(@PathVariable("id") long id, RedirectAttributes redirectAttr) {
        redirectAttr.addFlashAttribute("ShowDeleteModelWindow", true);
        redirectAttr.addFlashAttribute("bookId", id);
        redirectAttr.addFlashAttribute("bookName", bookService.get(id).getName());

        return "redirect:/";
    }

    // Удаляет книгу
    @DeleteMapping("/books/{id}")
    public String deleteBook(@PathVariable("id") long id) {
        bookService.delete(bookService.get(id));
        return "redirect:/";
    }

}


// типы поиска для главной страницы
enum SearchType {
    ALL,                // найти все книги
    SEARCH_GENRE,       // найти книги определенного жанра
    SEARCH_KEYWORDS;    // найти книги по ключевым словам (первое слово ищется в названии книги, остальные, если они есть, - в ФИО автора)
}