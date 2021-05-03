package library.service;

import library.dao.RoleEntityDao;
import library.domain.RoleEntity;
import library.repository.RoleEntityRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleEntityService implements RoleEntityDao {

    RoleEntityRepo roleRepo;

    public RoleEntityService(RoleEntityRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public List<RoleEntity> getAll() {
        return roleRepo.findAll();
    }

    @Override
    public RoleEntity get(long id) {
        return roleRepo.getOne(id);
    }

    @Override
    public RoleEntity save(RoleEntity role) {
        return roleRepo.save(role);
    }

    @Override
    public void delete(RoleEntity role) {
        roleRepo.delete(role);
    }
}
