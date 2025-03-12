create table job
(
    id          LONG    not null,
    name        VARCHAR not null,
    description VARCHAR not null,
    constraint job_pk
        primary key (id)
);

