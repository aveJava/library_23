package library.service;

import library.dao.BookEntityDao;
import library.domain.BookEntity;
import library.repository.BookEntityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookEntityService implements BookEntityDao {
    @Autowired
    BookEntityRepo bookRepo;

    @Override
    public List<BookEntity> getAll() {
        return bookRepo.findAll();
    }

    @Override
    public BookEntity get(long id) {
        return bookRepo.getOne(id);
    }

    @Override
    public BookEntity save(BookEntity obj) {
        return bookRepo.save(obj);
    }

    @Override
    public void delete(BookEntity object) {
        bookRepo.delete(object);
    }

    @Override
    public List<BookEntity> search(String ... searchString) {
        if (searchString.length == 1) searchString = new String[]{searchString[0], ""};
        return bookRepo.findByNameContainingIgnoreCaseOrAuthorFioContainingIgnoreCaseOrderByName(searchString[0], searchString[1]);
    }

    @Override
    public List<BookEntity> getAll(Sort sort) {
        return bookRepo.findAll(sort);
    }

    @Override
    public Page<BookEntity> getAll(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        Sort sort = Sort.by(sortDirection, sortField);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return bookRepo.findAll(pageRequest);
    }

    @Override
    public Page<BookEntity> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, String... searchString) {
        if (searchString.length == 1) searchString = new String[]{searchString[0], searchString[0]};

        Sort sort = Sort.by(sortDirection, sortField);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return bookRepo.findByNameContainingIgnoreCaseOrAuthorFioContainingIgnoreCaseOrderByName(searchString[0], searchString[1], pageRequest);
    }

    @Override
    public List<BookEntity> findTopBooks(int limit) {
        Sort sort = Sort.by(Sort.Direction.DESC, "viewCount");
        PageRequest pageRequest = PageRequest.of(0, limit, sort);
        return bookRepo.findTopBooks(pageRequest);
    }

    @Override
    public byte[] getContent(long id) {
        return bookRepo.getContent(id);
    }

    @Override
    public Page<BookEntity> findByGenre(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, long genreId) {
        Sort sort = Sort.by(sortDirection, sortField);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return bookRepo.findByGenre(genreId, pageRequest);
    }

    @Override
    public void updateViewCount(long id, long viewCount) {
        bookRepo.updateViewCount(id, viewCount);
    }

    @Override
    public void updateRating(long id, long totalRating, long totalViewCount, int avgRating) {
        bookRepo.updateRating(id, totalRating, totalViewCount, avgRating);
    }
}
