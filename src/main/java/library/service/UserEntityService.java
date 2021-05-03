package library.service;

import library.dao.UserEntityDao;
import library.domain.UserEntity;
import library.repository.RoleEntityRepo;
import library.repository.UserEntityRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserEntityService implements UserEntityDao, UserDetailsService {

    UserEntityRepo userRepo;
    RoleEntityRepo roleRepo;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserEntityService(UserEntityRepo userRepo, RoleEntityRepo roleRepo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserEntity get(long id) {
        return userRepo.getOne(id);
    }

    @Override
    public List<UserEntity> getAll() {
        return userRepo.findAll();
    }

    @Override
    public List<UserEntity> findAllByUsername(String username) {
        return userRepo.findAllByUsername(username);
    }

    @Override
    public boolean isPresentEnableUsersWithUsername(String username) {
        return userRepo.findByUsernameAndEnabledIsTrue(username).isPresent();
    }

    @Override
    public boolean save(UserEntity user) {
        // если в БД уже есть активный пользователь с таким именем, то ничего не делать
        if (isPresentEnableUsersWithUsername(user.getUsername())) {
            return false;
        }

        // хеширование пароля, установка новому пользователю роли User и сохраниние в БД
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleRepo.findByName("ROLE_USER")));
        user.setEnabled(true);
        userRepo.save(user);

        return true;
    }

    @Override
    public boolean update(UserEntity user) {
        // если такого пользователя нет в БД - ничего не делать
        Optional<UserEntity> optional = userRepo.findById(user.getId());
        if (!optional.isPresent()) return false;

        UserEntity userFromDB = optional.get();
        String hashPass = bCryptPasswordEncoder.encode(user.getPassword());

        // если пароль из формы не совпадает с паролем из БД - ничего не делать
        if (!hashPass.equals(userFromDB.getPassword())) {
            return false;
        }

        // обновить имя или пароль, если в форме на обновление были указаны новые имя или пароль
        if (user.getNewUserName() != null) user.setUsername(user.getNewUserName());
        if (user.getNewPassword() != null) user.setPassword(bCryptPasswordEncoder.encode(user.getNewUserName()));

        userRepo.save(user);

        return true;
    }

    @Override
    public void delete(UserEntity user) {
        userRepo.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> usr = userRepo.findByUsernameAndEnabledIsTrue(username);
        if (!usr.isPresent()) throw new UsernameNotFoundException("User not found");

        return usr.get();    // UserEntity реализует interface UserDetails
    }
}
