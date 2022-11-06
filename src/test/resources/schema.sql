drop table if exists AD;
drop table if exists CATEGORY;


create table CATEGORY (
	id integer not null auto_increment,
    parent_id integer, 
    name varchar(50), 
    
    primary key (id), 
    foreign key (parent_id) references CATEGORY(id)
); 

create table AD (
	id integer not null auto_increment, 
    ad_type enum('OFFER', 'REQUEST') not null, 
    category_id integer not null, 
    title varchar(50) not null, 
    ad_description varchar(50) not null, 
    price integer, 
    created timestamp not null, 
    
    primary key (id),
    foreign key (category_id) references CATEGORY(id)
);