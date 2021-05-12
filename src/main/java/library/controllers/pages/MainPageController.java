package library.controllers.pages;

import library.controllers.security.UserController;
import library.domain.BookEntity;
import library.service.AuthorEntityService;
import library.service.BookEntityService;
import library.service.GenreEntityService;
import library.service.PublisherEntityService;
import library.utils.MessLocalizer;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;


/** Контроллер, отвечающий за отображение главной страницы */
@Controller
@RequestMapping("/main_page")
public class MainPageController {
    /** Инстансы используемых сервисов */
    AuthorEntityService authorService;
    BookEntityService bookService;
    GenreEntityService genreService;
    PublisherEntityService publisherService;
    UserController userController;

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
                              GenreEntityService genreService, PublisherEntityService publisherService,
                              UserController userController) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.genreService = genreService;
        this.publisherService = publisherService;
        this.userController = userController;

        // настройки первого посещения (отображаем все книги, первую страницу, 10 элементов на странице)
        pageNumber = 1;
        pageSize = 10;
        searchType = SearchType.ALL;
    }

    // отображает главную страницу согласно ее текущему состоянию
    @GetMapping()
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
        model.addAttribute("genreId", searchType == SearchType.SEARCH_GENRE ? genreId : -1);
        model.addAttribute("userController", userController);
        model.addAttribute("toolbarControllerAddress", "/main_page/toolbar");

        return "pages/main";
    }

    // слушает запросы на поиск книг (и меняет параметры поиска согласно полученному запросу)
    @GetMapping("/search")
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

        return "redirect:/main_page";
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
        // получение локализованных сообщений
        String empty = MessLocalizer.get("empty");
        String found = MessLocalizer.get("found");
        String genre = MessLocalizer.get("genre");
        String search = MessLocalizer.get("search");

        // составление локализованного сообщения о критериях поиска
        String message;
        if (totalElements == 0) message = empty;
        else message = found + getCorrectDeclension(totalElements);
        switch (searchType) {
            case SEARCH_GENRE:
                message += String.format(" (%s: '%s')", genre, genreService.get(genreId).getLocalizedName());
                break;
            case SEARCH_KEYWORDS:
                // сборка массива ключевых слов в одну строку
                StringBuilder mess = new StringBuilder();
                for (int i=0; i<keywords.length; i++) {
                    mess.append(keywords[i] + " ");
                }
                message += String.format(" (%s: %s)", search, mess.toString().trim());
                break;
            default:
                ;
        }
        return message;
    }

    // возвращает число найденных книг с правильным склонением слова 'книга' (вспомогательный метод)
    public String getCorrectDeclension(long digit) {
        Locale locale = LocaleContextHolder.getLocale();
        if ("en".equals(locale.toString())) {
            return digit == 1 ? "1 book" : digit + " books";
        }
        if (digit > 99) digit = digit - (digit/100 * 100);
        if (digit<15 && digit>9) return digit + " книг";

        int units = (int) (digit - (digit/10 * 10));
        if (units == 1) return digit + " книга";
        if (units < 5 && units > 1) return digit + " книги";
        if (units == 0 || units > 4) return digit + " книг";
        return "хз скоко книг";
    }

    // слушает кнопки toolbar'а, перелистывает страницу результатов или меняет ее размер
    @GetMapping("/toolbar/{button}")
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

        return "redirect:/main_page";
    }


    // Рейтинг                  GET:  /main_page/rating?bookId=15&rating=4
    @GetMapping("/rating")
    public String registerVoice(@RequestParam("bookId") long bookId, @RequestParam("rating") int rating, RedirectAttributes redirectAttr, Locale loc) {
        BookEntity book = bookService.get(bookId);
        long totalRating = book.getTotalRating() + rating;
        long totalVoteCount = book.getTotalVoteCount() + 1;
        int avgRating = (int) Math.round(totalRating * 1.0 / totalVoteCount);
        bookService.updateRating(bookId, totalRating, totalVoteCount, avgRating);

        redirectAttr.addFlashAttribute("ShowRatingMessWindow", true);
        String label = null;
        if ("ru".equals(loc.toString())) label = rating == 1 ? "1 звезда" : rating > 1 && rating < 5 ? rating + " звезды" : "5 звезд";
        if ("en".equals(loc.toString())) label = rating == 1 ? "1 star" : rating + " stars";
        redirectAttr.addFlashAttribute("RatingLabel", label);

        return "redirect:/main_page";
    }


    // Показывает диалог удаления книги (модальное окно)
    @GetMapping("/deleteDialog")
    public String showDeleteDialog(@RequestParam("id") long id, RedirectAttributes redirectAttr) {
        redirectAttr.addFlashAttribute("ShowDeleteModelWindow", true);
        redirectAttr.addFlashAttribute("bookId", id);
        redirectAttr.addFlashAttribute("bookName", bookService.get(id).getName());

        return "redirect:/main_page";
    }

    // Принимает запрос на показ содержания книги
    @GetMapping("/viewing/{id}")
    public String getContent(@PathVariable("id") int id) {
        byte[] content = bookService.get(id).getContent();
        if (content != null && content.length > 0) {
            return "redirect:/books/content?id=" + id;
        } else {
            return "redirect:/errors?name=pdf_not_found";
        }
    }

}


// типы поиска для главной страницы
enum SearchType {
    ALL,                // найти все книги
    SEARCH_GENRE,       // найти книги определенного жанра
    SEARCH_KEYWORDS;    // найти книги по ключевым словам (первое слово ищется в названии книги, остальные, если они есть, - в ФИО автора)
}