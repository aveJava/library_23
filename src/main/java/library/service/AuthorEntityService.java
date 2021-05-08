package library.service;

import library.dao.AuthorEntityDao;
import library.domain.AuthorEntity;
import library.repository.AuthorEntityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AuthorEntityService implements AuthorEntityDao {
    @Autowired
    AuthorEntityRepo authorRepo;

    @Override
    public List<AuthorEntity> getAll() {
        return authorRepo.findAll();
    }

    @Override
    public AuthorEntity get(long id) {
        return authorRepo.getOne(id);
    }

    @Override
    public AuthorEntity save(AuthorEntity obj) {
        return authorRepo.save(obj);
    }

    @Override
    public void delete(AuthorEntity object) {
        authorRepo.delete(object);
    }

    @Override
    public List<AuthorEntity> search(String ... searchString) {
        Locale locale = LocaleContextHolder.getLocale();
        if ("en".equals(locale.toString()))
            return authorRepo.findByEnFioContainingIgnoreCaseOrderByEnFio(searchString[0]);
        else
            return authorRepo.findByRuFioContainingIgnoreCaseOrderByRuFio(searchString[0]);
    }

    @Override
    public List<AuthorEntity> getAll(Sort sort) {
        return authorRepo.findAll(sort);
    }

    @Override
    public Page<AuthorEntity> getAll(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        Sort sort = Sort.by(sortDirection, sortField);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return authorRepo.findAll(pageRequest);
    }

    @Override
    public Page<AuthorEntity> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, String... searchString) {
        Locale locale = LocaleContextHolder.getLocale();
        Sort sort = Sort.by(sortDirection, sortField);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        if ("en".equals(locale.toString()))
            return authorRepo.findByEnFioContainingIgnoreCaseOrderByEnFio(searchString[0], pageRequest);
        else
            return authorRepo.findByRuFioContainingIgnoreCaseOrderByRuFio(searchString[0], pageRequest);
    }
}
