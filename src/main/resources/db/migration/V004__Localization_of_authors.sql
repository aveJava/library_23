# Изменение таблицы library.author. Добавлены английские fio авторов.
# Поле birthday теперь может быть null (т.к. может быть известно не для всех авторов).


ALTER TABLE `library`.`author`
    ADD COLUMN en_fio VARCHAR(70) NOT NULL AFTER ru_fio,
    CHANGE COLUMN fio ru_fio VARCHAR(70) NOT NULL,
    CHANGE COLUMN birthday birthday DATE NULL;