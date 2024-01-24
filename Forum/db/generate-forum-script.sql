create schema if not exists forum;

create table tags
(
    tag_id int auto_increment
        primary key,
    name   varchar(64) not null,
    constraint tags_pk2
        unique (name)
);

create table users
(
    user_id      int auto_increment
        primary key,
    first_name   char(32)             not null,
    last_name    char(32)             not null,
    username     varchar(30)          not null,
    password     varchar(30)          not null,
    email        varchar(30)          not null,
    is_admin     tinyint(1) default 0 not null,
    phone_number int(20)              null,
    is_blocked   tinyint(1) default 0 null,
    constraint users_pk
        unique (username)
);

create table posts
(
    post_id    int auto_increment
        primary key,
    title      varchar(64)   not null,
    content    varchar(8192) not null,
    created_by int           null,
    constraint posts_users_user_id_fk
        foreign key (created_by) references users (user_id)
);

create table comments
(
    comment_id     int auto_increment
        primary key,
    content        varchar(8192) not null,
    post_date      date as (NULL) stored,
    parent_comment int           null,
    created_by     int           null,
    post_id        int           null,
    constraint comments_comments_comment_id_fk
        foreign key (parent_comment) references comments (comment_id),
    constraint comments_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint comments_users_user_id_fk
        foreign key (created_by) references users (user_id)
);

create table likes
(
    post_id int        null,
    user_id int        null,
    likes   tinyint(1) null,
    constraint likes_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint likes_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table posts_tags
(
    post_id int null,
    tag_id  int null,
    constraint posts_tags_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint posts_tags_tags_tag_id_fk
        foreign key (tag_id) references tags (tag_id)
);

create table user_posts
(
    user_id int null,
    post_id int null,
    constraint user_posts_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint user_posts_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

