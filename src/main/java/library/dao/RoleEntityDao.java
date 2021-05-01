package library.dao;

import library.domain.RoleEntity;

import java.util.List;

public interface RoleEntityDao {
    List<RoleEntity> getAll();              // получить все объекты
    RoleEntity get(long id);                // плолучить объект по id
    RoleEntity save (RoleEntity role);      // сохранение/редактирование
    void delete(RoleEntity role);           // удалить объект
}
