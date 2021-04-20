package library.dao;

import library.domain.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BookEntityDao extends GeneralDao<BookEntity> {
    List<BookEntity> findTopBooks(int limit);
    byte[] getContent(long id);
    Page<BookEntity> findByGenre(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, long genreId);

    // обновить количество посмотров книги
    void updateViewCount(long id, long viewCount);

    // обновить данные рейтинга
    void updateRating(long id, long totalRating, long totalViewCount, int avgRating);

    // получить все ISBN
    List<String> getAllISBN();
}
