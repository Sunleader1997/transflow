create table JOB
(
    ID          BIGINT            not null,
    NAME        CHARACTER VARYING not null,
    DESCRIPTION CHARACTER VARYING not null,
    INPUTID     BIGINT,
    UPDATETIME  TIMESTAMP         not null,
    constraint JOB_PK
        primary key (ID)
);

create table NODE
(
    ID          BIGINT            not null,
    JOBID       BIGINT            not null,
    NAME        CHARACTER VARYING not null,
    DESCRIPTION CHARACTER VARYING not null,
    NODETYPE    CHARACTER VARYING not null,
    PLUGINID    CHARACTER VARYING not null,
    CONFIG      CHARACTER VARYING not null,
    constraint NODE_PK
        primary key (ID)
);

create table NODE_LINK
(
    ID       BIGINT not null,
    SOURCEID BIGINT not null,
    TARGETID BIGINT not null,
    constraint NODE_LINK_PK
        primary key (ID)
);