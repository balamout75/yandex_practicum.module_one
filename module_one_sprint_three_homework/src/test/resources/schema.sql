drop table if exists postsandtags;
drop table if exists comments;
drop table if exists tags;
drop table if exists posts;
drop SEQUENCE if exists image_sequence;

create table if not exists posts(
                                    id bigserial primary key,
                                    title varchar NOT NULL,
                                    text varchar NOT NULL,
                                    image varchar NOT NULL DEFAULT '',
                                    likesCount bigint DEFAULT 0);

drop table if exists comments;
create table if not exists comments(
                                       id bigserial primary key,
                                       text varchar NOT NULL,
                                       postid bigint,
                                       FOREIGN KEY (postid) REFERENCES posts(id) ON DELETE CASCADE);

drop table if exists tags;
create table if not exists tags(
                                   id bigserial primary key,
                                   tag varchar UNIQUE NOT NULL);

drop table if exists postsandtags;
create table if not exists postsandtags(
                                           post bigint NOT NULL,
                                           tag bigint NOT NULL,
                                           PRIMARY KEY (post, tag),
                                           FOREIGN KEY (post) REFERENCES posts(id) ON DELETE CASCADE,
                                           FOREIGN KEY (tag) REFERENCES tags (id) ON DELETE CASCADE);

CREATE SEQUENCE image_sequence
    START WITH 1
    INCREMENT BY 1
    CACHE 10;

insert into posts(title, text, image, likesCount) values ('Чистое синее озеро, белый песок, красочные скалы, хвойный лес — именно ' ||
                                                          'таким волшебным сочетанием природных даров отличается бухта Песчаная. Это одна ' ||
                                                          'из самых ярких достопримечательностей Байкала. Живописное место настолько красиво, ' ||
                                                          'что фотографии Песчаной бухты принимают за лазурные пляжи Франции или за берега райских островов.',
                                                          'Бла', 'Peschannaya.png', 1);
insert into posts(title, text, image, likesCount) values ('Второе сообщение', 'Бла бла','Peschannaya.png', 2);
insert into posts(title, text, image, likesCount) values ('третье сообщение', 'Бла бла бла', 'Peschannaya.png',3);
insert into posts(title, text, image, likesCount) values ('Четвертое сообщение', 'Бла бла','Peschannaya.png', 2);
insert into posts(title, text, image, likesCount) values ('Пятое сообщение', 'Бла бла бла', 'Peschannaya.png',3);
insert into posts(title, text, image, likesCount) values ('Шестое сообщение', 'Бла бла бла', 'Неправильный файл',3);

insert into tags(tag) values ('Байкал'),('Аршан'),('горы');

insert into postsandtags(post,tag) values (1,1),(1,2),(1,3);
insert into postsandtags(post,tag) values (2,1),(3,2),(3,3),(4,3);
insert into postsandtags(post,tag) values (6,1),(6,3);


insert into comments(text, postid) values ('1-1', 1),
                                          ('2-1', 1),
                                          ('3-1', 1),
                                          ('1-2', 2),
                                          ('2-2', 2),
                                          ('3-2', 2),
                                          ('1-3', 3),
                                          ('2-3', 3),
                                          ('3-3', 3);
