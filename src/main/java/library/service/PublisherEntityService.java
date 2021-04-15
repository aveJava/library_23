package library.service;

import library.dao.PublisherEntityDao;
import library.domain.PublisherEntity;
import library.repository.PublisherEntityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherEntityService implements PublisherEntityDao {
    @Autowired
    PublisherEntityRepo publisherRepo;

    @Override
    public List<PublisherEntity> getAll() {
        return publisherRepo.findAll();
    }

    @Override
    public PublisherEntity get(long id) {
        return publisherRepo.getOne(id);
    }

    @Override
    public PublisherEntity save(PublisherEntity obj) {
        return publisherRepo.save(obj);
    }

    @Override
    public void delete(PublisherEntity object) {
        publisherRepo.delete(object);
    }

    @Override
    public List<PublisherEntity> search(String... searchString) {
        return publisherRepo.findByNameContainingIgnoreCaseOrderByName(searchString[0]);
    }

    @Override
    public List<PublisherEntity> getAll(Sort sort) {
        return publisherRepo.findAll(sort);
    }

    @Override
    public Page<PublisherEntity> getAll(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        Sort sort = Sort.by(sortDirection, sortField);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return publisherRepo.findAll(pageRequest);
    }

    @Override
    public Page<PublisherEntity> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, String... searchString) {
        Sort sort = Sort.by(sortDirection, sortField);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return publisherRepo.findByNameContainingIgnoreCaseOrderByName(searchString[0], pageRequest);
    }
}
