# Изменение таблицы library.genre. Добавлены английские названия жанров.


ALTER TABLE `library`.`genre`
    ADD COLUMN en_name VARCHAR(50) NOT NULL AFTER ru_name,
    CHANGE COLUMN name ru_name VARCHAR(50) NOT NULL;
