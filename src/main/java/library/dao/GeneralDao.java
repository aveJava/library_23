package library.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * DAO - абстрактный слой по работе с бизнес-процессами. Определяет какое поведение будет у каждого типа объектов и
 * что с ними можно будет делать. Данный класс является объщим предком для всех DAO-классов.
 */

public interface GeneralDao<T> {
    List<T> getAll();               // получить все объекты (например, всех авторов для класса Author)
    T get(long id);                 // плолучить объект по id (например, автора по его id для класса Author)
    T save (T obj);                 // сохранить объект в БД (и для добавления и для редактирования)
    void delete(T object);          // удалить объект

    List<T> search(String ... searchString);    // производит поиск объектов класса по ключевым словам

    List<T> getAll(Sort sort);
    Page<T> getAll(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection);
    Page<T> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, String ... searchString);
}
