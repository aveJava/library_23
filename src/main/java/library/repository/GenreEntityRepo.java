package library.repository;

import library.domain.GenreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreEntityRepo extends JpaRepository<GenreEntity, Long> {

    // поиск жанров по имени
    List<GenreEntity> findByRuNameContainingIgnoreCaseOrderByRuName(String ruName);
    List<GenreEntity> findByEnNameContainingIgnoreCaseOrderByEnName(String enName);

    // поиск жанров по имени с постраничностью
    Page<GenreEntity> findByRuNameContainingIgnoreCaseOrderByRuName(String name, Pageable pageable);
    Page<GenreEntity> findByEnNameContainingIgnoreCaseOrderByEnName(String name, Pageable pageable);

}
