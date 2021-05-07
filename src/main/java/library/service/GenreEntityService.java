package library.service;

import library.dao.GenreEntityDao;
import library.domain.GenreEntity;
import library.repository.GenreEntityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class GenreEntityService implements GenreEntityDao {
    @Autowired
    GenreEntityRepo genreRepo;

    @Override
    public List<GenreEntity> getAll() {
        return genreRepo.findAll();
    }

    @Override
    public GenreEntity get(long id) {
        return genreRepo.getOne(id);
    }

    @Override
    public GenreEntity save(GenreEntity obj) {
        return genreRepo.save(obj);
    }

    @Override
    public void delete(GenreEntity object) {
        genreRepo.delete(object);
    }

    @Override
    public List<GenreEntity> search(String... searchString) {
        Locale locale = LocaleContextHolder.getLocale();
        if ("en".equals(locale.toString()))
            return genreRepo.findByEnNameContainingIgnoreCaseOrderByEnName(searchString[0]);
        else
            return genreRepo.findByRuNameContainingIgnoreCaseOrderByRuName(searchString[0]);
    }

    @Override
    public List<GenreEntity> getAll(Sort sort) {
        return genreRepo.findAll(sort);
    }

    @Override
    public Page<GenreEntity> getAll(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        Sort sort = Sort.by(sortDirection, sortField);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return genreRepo.findAll(pageRequest);
    }

    @Override
    public Page<GenreEntity> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, String... searchString) {
        Locale locale = LocaleContextHolder.getLocale();
        Sort sort = Sort.by(sortDirection, sortField);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        if ("en".equals(locale.toString()))
            return genreRepo.findByEnNameContainingIgnoreCaseOrderByEnName(searchString[0], pageRequest);
        else
            return genreRepo.findByRuNameContainingIgnoreCaseOrderByRuName(searchString[0], pageRequest);
    }
}
