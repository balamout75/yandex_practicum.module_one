-- Таблица с пользователями
create table if not exists posts(
  id bigserial primary key,
  title varchar,
  text varchar,
  likesCount bigint,
  commentsCount bigint);

insert into posts(title, text, likesCount, commentsCount) values ('Первое сообщение', 'Бла', 1, 0);
insert into posts(title, text, likesCount, commentsCount) values ('Второе сообщение', 'Бла бла', 2, 0);
insert into posts(title, text, likesCount, commentsCount) values ('третье сообщение', 'Бла бла бла', 3, 0);

