package library.repository;

import library.domain.PublisherEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherEntityRepo extends JpaRepository<PublisherEntity, Long> {

    // поиск издателей по имени
    List<PublisherEntity> findByRuNameContainingIgnoreCaseOrderByRuName(String name);
    List<PublisherEntity> findByEnNameContainingIgnoreCaseOrderByEnName(String name);

    // поиск издателей по имени с постраничностью
    Page<PublisherEntity> findByRuNameContainingIgnoreCaseOrderByRuName(String name, Pageable pageable);
    Page<PublisherEntity> findByEnNameContainingIgnoreCaseOrderByEnName(String name, Pageable pageable);

}
