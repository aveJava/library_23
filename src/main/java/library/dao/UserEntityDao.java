package library.dao;

import library.domain.UserEntity;

import java.util.List;

public interface UserEntityDao {
    UserEntity get(long id);                // получить пользователя по id
    List<UserEntity> getAll();              // получить всех пользователей
    boolean save (UserEntity user);         // сохраненить пользователя
    boolean update(UserEntity user);        // обновить пользователя
    void delete(UserEntity user);           // удалить пользователя

    List<UserEntity> findAllByUsername(String username);            // получить всех пользователей с таким именем
    boolean isPresentEnableUsersWithUsername(String username);      // определить, есть ли активные пользователи с таким именем
}
