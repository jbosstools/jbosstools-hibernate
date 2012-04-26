
    create table PUBLIC.CUSTOMERS (
        CUSTOMERNUMBER int4 not null unique,
        ADDRESSLINE1 varchar(50) not null,
        ADDRESSLINE2 varchar(50),
        CITY varchar(50) not null,
        CONTACTFIRSTNAME varchar(50) not null,
        CONTACTLASTNAME varchar(50) not null,
        COUNTRY varchar(50) not null,
        CREDITLIMIT float8,
        CUSTOMERNAME varchar(50) not null,
        PHONE varchar(50) not null,
        POSTALCODE varchar(15),
        STATE varchar(50),
        SALESREPEMPLOYEENUMBER int4,
        primary key (CUSTOMERNUMBER)
    );

    create table PUBLIC.EMPLOYEES (
        EMPLOYEENUMBER int4 not null unique,
        EMAIL varchar(100) not null,
        EXTENSION varchar(10) not null,
        FIRSTNAME varchar(50) not null,
        JOBTITLE varchar(50) not null,
        LASTNAME varchar(50) not null,
        OFFICECODE varchar(10) not null,
        REPORTSTO int4,
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
        ORDERDETAILNUMBER  serial not null unique,
        ORDERLINENUMBER int2 not null,
        PRICEEACH float8 not null,
        QUANTITYORDERED int4 not null,
        ORDERNUMBER int4 not null,
        PRODUCTCODE varchar(15) not null,
        primary key (ORDERDETAILNUMBER)
    );

    create table PUBLIC.ORDERS (
        ORDERNUMBER int4 not null unique,
        COMMENTS varchar(255),
        ORDERDATE date not null,
        REQUIREDDATE date not null,
        SHIPPEDDATE date,
        STATUS varchar(16) not null,
        CUSTOMERNUMBER int4 not null,
        primary key (ORDERNUMBER)
    );

    create table PUBLIC.PAYMENTS (
        PAYMENTNUMBER  serial not null unique,
        AMOUNT float8 not null,
        CHECKNUMBER varchar(50) not null,
        PAYMENTDATE date not null,
        CUSTOMERNUMBER int4 not null,
        primary key (PAYMENTNUMBER)
    );

    create table PUBLIC.PRODUCTLINES (
        PRODUCTLINE varchar(50) not null unique,
        HTMLDESCRIPTION varchar(4000),
        TEXTDESCRIPTION varchar(4000),
        primary key (PRODUCTLINE)
    );

    create table PUBLIC.PRODUCTS (
        PRODUCTCODE varchar(15) not null unique,
        BUYPRICE float8 not null,
        MSRP float8 not null,
        PRODUCTDESCRIPTION varchar(4000) not null,
        PRODUCTNAME varchar(70) not null,
        PRODUCTSCALE varchar(10) not null,
        PRODUCTVENDOR varchar(50) not null,
        QUANTITYINSTOCK int2 not null,
        PRODUCTLINE varchar(50) not null,
        primary key (PRODUCTCODE)
    );

    alter table PUBLIC.CUSTOMERS 
        add constraint FK6268C352BA1F285 
        foreign key (SALESREPEMPLOYEENUMBER) 
        references PUBLIC.EMPLOYEES;

    alter table PUBLIC.ORDERDETAILS 
        add constraint FKB8CC1F346F6F0C88 
        foreign key (PRODUCTCODE) 
        references PUBLIC.PRODUCTS;

    alter table PUBLIC.ORDERDETAILS 
        add constraint FKB8CC1F34854B4C4 
        foreign key (ORDERNUMBER) 
        references PUBLIC.ORDERS;

    alter table PUBLIC.ORDERS 
        add constraint FK8B7256E5814A2234 
        foreign key (CUSTOMERNUMBER) 
        references PUBLIC.CUSTOMERS;

    alter table PUBLIC.PAYMENTS 
        add constraint FK810FFF2D814A2234 
        foreign key (CUSTOMERNUMBER) 
        references PUBLIC.CUSTOMERS;

    alter table PUBLIC.PRODUCTS 
        add constraint FKF2D1C164A675589B 
        foreign key (PRODUCTLINE) 
        references PUBLIC.PRODUCTLINES;
