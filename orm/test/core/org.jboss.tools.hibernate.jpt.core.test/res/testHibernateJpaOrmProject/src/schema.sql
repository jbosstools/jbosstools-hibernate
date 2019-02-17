
    create table col_entity.JustData_entity.ManyToMany1_ManyToMany1_entity.ManyToMany1_mtm1 (
        fk_null_entity.JustData_JustData_NULL_ID3 int4 not null,
        fk_mtm1_entity.ManyToMany1_ManyToMany1_NULL_ID1 int4 not null,
        primary key (fk_null_entity.JustData_JustData_NULL_ID3, fk_mtm1_entity.ManyToMany1_ManyToMany1_NULL_ID1),
        unique (fk_mtm1_entity.ManyToMany1_ManyToMany1_NULL_ID1)
    );

    create table col_entity.ManyToMany2_entity.ManyToMany1_ManyToMany1_entity.ManyToMany1_mtm1 (
        fk_mtm2_entity.ManyToMany2_ManyToMany22_ID_ID2 int4 not null,
        fk_mtm1_entity.ManyToMany1_ManyToMany1_NULL_ID1 int4 not null,
        primary key (fk_mtm2_entity.ManyToMany2_ManyToMany22_ID_ID2, fk_mtm1_entity.ManyToMany1_ManyToMany1_NULL_ID1)
    );

    create table ctn_JustData (
        pc_id3 int4 not null,
        primary key (pc_id3)
    );

    create table ctn_ManyToMany1 (
        pc_id1 int4 not null,
        fk_justData1_entity.JustData_JustData_NULL_ID3 int4,
        primary key (pc_id1)
    );

    create table tn_ManyToMany22 (
        cn_id int4 not null,
        cn_simpleData varchar(255),
        primary key (cn_id)
    );

    alter table col_entity.JustData_entity.ManyToMany1_ManyToMany1_entity.ManyToMany1_mtm1 
        add constraint FK9F2D79611090434E 
        foreign key (fk_null_entity.JustData_JustData_NULL_ID3) 
        references ctn_JustData;

    alter table col_entity.JustData_entity.ManyToMany1_ManyToMany1_entity.ManyToMany1_mtm1 
        add constraint FK9F2D79616057A2A 
        foreign key (fk_mtm1_entity.ManyToMany1_ManyToMany1_NULL_ID1) 
        references ctn_ManyToMany1;

    alter table col_entity.ManyToMany2_entity.ManyToMany1_ManyToMany1_entity.ManyToMany1_mtm1 
        add constraint FK46F84CB66057A2A 
        foreign key (fk_mtm1_entity.ManyToMany1_ManyToMany1_NULL_ID1) 
        references ctn_ManyToMany1;

    alter table col_entity.ManyToMany2_entity.ManyToMany1_ManyToMany1_entity.ManyToMany1_mtm1 
        add constraint FK46F84CB65013FC2F 
        foreign key (fk_mtm2_entity.ManyToMany2_ManyToMany22_ID_ID2) 
        references tn_ManyToMany22;

    alter table ctn_ManyToMany1 
        add constraint FKB4665956A5274FEE 
        foreign key (fk_justData1_entity.JustData_JustData_NULL_ID3) 
        references ctn_JustData;
