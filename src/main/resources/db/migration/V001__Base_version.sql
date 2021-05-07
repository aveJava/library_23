# Создание базы данных library (базовая структура)


CREATE SCHEMA IF NOT EXISTS library
CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE library;


CREATE TABLE author (
        id bigint NOT NULL AUTO_INCREMENT,
        fio varchar(70) NOT NULL,
        birthday date NOT NULL,
        PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE genre (
        id bigint NOT NULL AUTO_INCREMENT,
        name varchar(50) NOT NULL,
        PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE publisher (
        id bigint NOT NULL AUTO_INCREMENT,
        name varchar(50) NOT NULL,
        PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE book (
        id bigint NOT NULL AUTO_INCREMENT,
        name varchar(150) NOT NULL,
        content longblob,
        page_count int NOT NULL,
        isbn varchar(20) NOT NULL,
        genre_id bigint NOT NULL,
        author_id bigint NOT NULL,
        publish_year int NOT NULL,
        publisher_id bigint NOT NULL,
        image longblob,
        avg_rating int DEFAULT '0',
        total_vote_count bigint DEFAULT '0',
        total_rating bigint DEFAULT '0',
        view_count bigint DEFAULT '0',
        descr varchar(500) DEFAULT NULL,
        PRIMARY KEY (id),
        UNIQUE KEY id_UNIQUE (id),
        UNIQUE KEY isbn_UNIQUE (isbn),
        KEY fk_author_idx (author_id),
        KEY fk_genre_idx (genre_id),
        KEY fk_publisher_idx (publisher_id),
        CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES author (id) ON UPDATE CASCADE,
        CONSTRAINT fk_genre FOREIGN KEY (genre_id) REFERENCES genre (id) ON UPDATE CASCADE,
        CONSTRAINT fk_publisher FOREIGN KEY (publisher_id) REFERENCES publisher (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE vote (
        id bigint NOT NULL AUTO_INCREMENT,
        value tinyint DEFAULT '0',
        book_id bigint NOT NULL,
        username varchar(35) NOT NULL,
        PRIMARY KEY (id),
        UNIQUE KEY id_UNIQUE (id),
        KEY fk_book_id_idx (book_id),
        KEY fk_user_id_idx (username),
        CONSTRAINT fk_book_id FOREIGN KEY (book_id) REFERENCES book (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE security_user (
        id bigint NOT NULL AUTO_INCREMENT,
        username varchar(35),
        password varchar(255),
        enabled bit NOT NULL,
        PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE security_role (
        role varchar(70) not null,
        primary key (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE security_user_roles (
        user_id bigint not null,
        authority varchar(70) not null,
        primary key (user_id, authority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
