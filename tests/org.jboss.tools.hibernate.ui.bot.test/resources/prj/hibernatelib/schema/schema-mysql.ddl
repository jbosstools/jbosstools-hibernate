
    create table PUBLIC.CUSTOMERS (
        CUSTOMERNUMBER integer not null unique,
        ADDRESSLINE1 varchar(50) not null,
        ADDRESSLINE2 varchar(50),
        CITY varchar(50) not null,
        CONTACTFIRSTNAME varchar(50) not null,
        CONTACTLASTNAME varchar(50) not null,
        COUNTRY varchar(50) not null,
        CREDITLIMIT double precision,
        CUSTOMERNAME varchar(50) not null,
        PHONE varchar(50) not null,
        POSTALCODE varchar(15),
        STATE varchar(50),
        SALESREPEMPLOYEENUMBER integer,
        primary key (CUSTOMERNUMBER)
    );

    create table PUBLIC.EMPLOYEES (
        EMPLOYEENUMBER integer not null unique,
        EMAIL varchar(100) not null,
        EXTENSION varchar(10) not null,
        FIRSTNAME varchar(50) not null,
        JOBTITLE varchar(50) not null,
        LASTNAME varchar(50) not null,
        OFFICECODE varchar(10) not null,
        REPORTSTO integer,
        primary key (EMPLOYEENUMBER)
    );

    create table PUBLIC.OFFICES (
        OFFICECODE varchar(10) not null unique,
        ADDRESSLINE1 varchar(50) not null,
        ADDRESSLINE2 varchar(50),
        CITY varchar(50) not null,
        COUNTRY varchar(50) not null,
        PHONE varchar(50) not null,
        POSTALCODE varchar(15) not null,
        STATE varchar(50),
        TERRITORY varchar(10) not null,
        primary key (OFFICECODE)
    );

    create table PUBLIC.ORDERDETAILS (
        ORDERDETAILNUMBER integer not null auto_increment unique,
        ORDERLINENUMBER smallint not null,
        PRICEEACH double precision not null,
        QUANTITYORDERED integer not null,
        ORDERNUMBER integer not null,
        PRODUCTCODE varchar(15) not null,
        primary key (ORDERDETAILNUMBER)
    );

    create table PUBLIC.ORDERS (
        ORDERNUMBER integer not null unique,
        COMMENTS varchar(255),
        ORDERDATE date not null,
        REQUIREDDATE date not null,
        SHIPPEDDATE date,
        STATUS varchar(16) not null,
        CUSTOMERNUMBER integer not null,
        primary key (ORDERNUMBER)
    );

    create table PUBLIC.PAYMENTS (
        PAYMENTNUMBER integer not null auto_increment unique,
        AMOUNT double precision not null,
        CHECKNUMBER varchar(50) not null,
        PAYMENTDATE date not null,
        CUSTOMERNUMBER integer not null,
        primary key (PAYMENTNUMBER)
    );

    create table PUBLIC.PRODUCTLINES (
        PRODUCTLINE varchar(50) not null unique,
        HTMLDESCRIPTION longtext,
        TEXTDESCRIPTION longtext,
        primary key (PRODUCTLINE)
    );

    create table PUBLIC.PRODUCTS (
        PRODUCTCODE varchar(15) not null unique,
        BUYPRICE double precision not null,
        MSRP double precision not null,
        PRODUCTDESCRIPTION longtext not null,
        PRODUCTNAME varchar(70) not null,
        PRODUCTSCALE varchar(10) not null,
        PRODUCTVENDOR varchar(50) not null,
        QUANTITYINSTOCK smallint not null,
        PRODUCTLINE varchar(50) not null,
        primary key (PRODUCTCODE)
    );

    alter table PUBLIC.CUSTOMERS 
        add index FK6268C352BA1F285 (SALESREPEMPLOYEENUMBER), 
        add constraint FK6268C352BA1F285 
        foreign key (SALESREPEMPLOYEENUMBER) 
        references PUBLIC.EMPLOYEES (EMPLOYEENUMBER);

    alter table PUBLIC.ORDERDETAILS 
        add index FKB8CC1F346F6F0C88 (PRODUCTCODE), 
        add constraint FKB8CC1F346F6F0C88 
        foreign key (PRODUCTCODE) 
        references PUBLIC.PRODUCTS (PRODUCTCODE);

    alter table PUBLIC.ORDERDETAILS 
        add index FKB8CC1F34854B4C4 (ORDERNUMBER), 
        add constraint FKB8CC1F34854B4C4 
        foreign key (ORDERNUMBER) 
        references PUBLIC.ORDERS (ORDERNUMBER);

    alter table PUBLIC.ORDERS 
        add index FK8B7256E5814A2234 (CUSTOMERNUMBER), 
        add constraint FK8B7256E5814A2234 
        foreign key (CUSTOMERNUMBER) 
        references PUBLIC.CUSTOMERS (CUSTOMERNUMBER);

    alter table PUBLIC.PAYMENTS 
        add index FK810FFF2D814A2234 (CUSTOMERNUMBER), 
        add constraint FK810FFF2D814A2234 
        foreign key (CUSTOMERNUMBER) 
        references PUBLIC.CUSTOMERS (CUSTOMERNUMBER);

    alter table PUBLIC.PRODUCTS 
        add index FKF2D1C164A675589B (PRODUCTLINE), 
        add constraint FKF2D1C164A675589B 
        foreign key (PRODUCTLINE) 
        references PUBLIC.PRODUCTLINES (PRODUCTLINE);
