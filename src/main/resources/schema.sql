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
    CREATED     TIMESTAMP                 not null,
    constraint PK_AD primary key (ID),
    constraint FK_AD_CATEGORY_ID_CATEGORY foreign key (CATEGORY_ID)
        references CATEGORY (ID)
        on delete restrict on update restrict
);


