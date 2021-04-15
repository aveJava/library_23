package library.repository;

import library.domain.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorEntityRepo extends JpaRepository<AuthorEntity, Long> {

    // поиск авторов по фио без постраничности
    // найти авторов, значение поля fio которых содержит (в любом месте (начале, конце, середине))
    // переданную строку игнорируя раскладку, результаты отсортировать по значению поля fio
    List<AuthorEntity> findByFioContainingIgnoreCaseOrderByFio(String fio);

    // поиск авторов по фио с постраничностью
    // Page содержит некоторое количество результатов запроса
    // Pageable - параметры постраничности (сколько результатов выводит на одной странице и т.д.)
    Page<AuthorEntity> findByFioContainingIgnoreCaseOrderByFio(String fio, Pageable pageable);

}
