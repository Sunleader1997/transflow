create table JOB
(
    ID          CHARACTER VARYING not null,
    NAME        CHARACTER VARYING not null,
    DESCRIPTION CHARACTER VARYING not null,
    INPUTID     CHARACTER VARYING,
    UPDATETIME  TIMESTAMP,
    constraint JOB_PK
        primary key (ID)
);

create table NODE
(
    ID       CHARACTER VARYING not null,
    JOBID    CHARACTER VARYING not null,
    NAME     CHARACTER VARYING not null,
    NODETYPE CHARACTER VARYING not null,
    PLUGINID CHARACTER VARYING not null,
    CONFIG   CHARACTER VARYING not null,
    X        INTEGER default 0 not null,
    Y        INTEGER default 0 not null,
    constraint NODE_PK
        primary key (ID)
);

create table NODE_LINK
(
    ID       CHARACTER VARYING not null,
    SOURCEID CHARACTER VARYING not null,
    SOURCEHANDLE CHARACTER VARYING ,
    TARGETID CHARACTER VARYING not null,
    TARGETHADNLE CHARACTER VARYING ,
    constraint NODE_LINK_PK
        primary key (ID)
);

