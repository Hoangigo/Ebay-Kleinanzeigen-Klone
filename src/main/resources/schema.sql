/*==============================================================*/
/* Table: CATEGORY                                                */
/*==============================================================*/
create table IF NOT EXISTS CATEGORY
(
    ID        INT AUTO_INCREMENT,
    PARENT_ID INT,
    NAME      VARCHAR(30),
    constraint PK_CATEGORY primary key (ID),
    constraint FK_CATEGORY_PARENT_ID_CATEGORY foreign key (PARENT_ID)
        references CATEGORY (ID)
        on delete restrict on update restrict
);

/*==============================================================*/
/* Table: USER                                                */
/*==============================================================*/
create table IF NOT EXISTS USER
(
    ID          INT AUTO_INCREMENT,
    EMAIL       VARCHAR(35)               not null,
    PASSWORD    VARCHAR(30)              not null,
    FIRST_NAME    VARCHAR(30),
    LAST_NAME    VARCHAR(30),
    PHONE    VARCHAR(20),
    LOCATION    VARCHAR(30),
    CREATED     TIMESTAMP                 not null,
    constraint PK_AD primary key (ID)
);


/*==============================================================*/
/* Table: AD                                                */
/*==============================================================*/
create table IF NOT EXISTS AD
(
    ID          INT AUTO_INCREMENT,
    TYPE        ENUM ('OFFER', 'REQUEST') not null,
    CATEGORY_ID INT                       not null,
    TITLE       VARCHAR(30)               not null,
    DESCRIPTION VARCHAR(100)              not null,
    PRICE       INT,
    LOCATION    VARCHAR(40),
    USER_ID     INT                       not null,
    CREATED     TIMESTAMP                 not null,
    constraint PK_AD primary key (ID),
    constraint FK_AD_CATEGORY_ID_CATEGORY foreign key (CATEGORY_ID)
        references CATEGORY (ID)
        on delete restrict on update restrict,
    constraint FK_AD_USER_ID_USER foreign key (USER_ID)
        references USER (ID)
        on delete restrict on update restrict
);


