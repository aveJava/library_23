# Изменение таблицы library.publisher. Добавлены английские названия издателей.


ALTER TABLE `library`.`publisher`
    ADD COLUMN en_name VARCHAR(50) NOT NULL AFTER ru_name,
    CHANGE COLUMN name ru_name VARCHAR(50) NOT NULL;