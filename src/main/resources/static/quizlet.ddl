alter table if exists card
drop
constraint if exists FK5ceceujik7f8q3balq9uj0fv;
drop table if exists card cascade;
drop table if exists card_pack cascade;
drop sequence if exists card_pack_seq;
drop sequence if exists card_seq;


create sequence card_pack_seq start with 1 increment by 50;
create sequence card_seq start with 1 increment by 50;


create table card
(
    id           bigint not null,
    term         varchar(255),
    definition   varchar(255),
    card_pack_id bigint,
    primary key (id)
);

create table card_pack
(
    id bigint not null,
    primary key (id)
);

alter table if exists card
    add constraint FK5ceceujik7f8q3balq9uj0fv
    foreign key (card_pack_id)
    references card_pack