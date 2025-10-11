-- Таблица с пользователями
-- drop table if exists posts;
create table if not exists posts(
    id bigserial primary key,
    title varchar NOT NULL,
    text varchar NOT NULL,
    image varchar NOT NULL DEFAULT '',
    likesCount bigint DEFAULT 0,
    commentsCount bigint DEFAULT 0);

-- drop table if exists comments;
create table if not exists comments(
    id bigserial primary key,
    text varchar NOT NULL,
    postid bigint REFERENCES posts(id));

insert into posts(title, text, image, likesCount, commentsCount) values ('Первое сообщение', 'Бла', 'Peschannaya.png', 1, 0);
insert into posts(title, text, image, likesCount, commentsCount) values ('Второе сообщение', 'Бла бла','Peschannaya.png', 2, 0);
insert into posts(title, text, image, likesCount, commentsCount) values ('третье сообщение', 'Бла бла бла', 'Peschannaya.png',3, 0);
insert into posts(title, text, image, likesCount, commentsCount) values ('Второе сообщение', 'Бла бла','Peschannaya.png', 2, 0);
insert into posts(title, text, image, likesCount, commentsCount) values ('третье сообщение', 'Бла бла бла', 'Peschannaya.png',3, 0);

insert into comments(text, postid) values ('1-1', 1),
                                          ('2-1', 1),
                                          ('3-1', 1),
                                          ('1-2', 2),
                                          ('2-2', 2),
                                          ('3-2', 2),
                                          ('1-3', 3),
                                          ('2-3', 3),
                                          ('3-3', 3);


