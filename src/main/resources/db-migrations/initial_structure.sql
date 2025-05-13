create table if not exists m_cashier
(
  id         varchar(10)                           not null
    primary key,
  name       varchar(25)                           null,
  gender     char                                  null,
  phone      varchar(20)                           null,
  religion   varchar(10)                           null,
  address    varchar(500)                          null,
  password   varchar(500)                          null,
  created_at timestamp default current_timestamp() null,
  updated_at timestamp default current_timestamp() null
);

create table if not exists m_customer
(
  id         varchar(32)                           not null
    primary key,
  name       varchar(30)                           null,
  gender     char                                  null,
  phone      varchar(20)                           null,
  address    varchar(500)                          null,
  created_at timestamp default current_timestamp() null,
  updated_at timestamp default current_timestamp() null
);

create table if not exists m_item
(
  id         varchar(20)                           not null
    primary key,
  name       varchar(50)                           null,
  type       varchar(15)                           null,
  buy_price  double                                null,
  sell_price double                                null,
  created_at timestamp default current_timestamp() null,
  updated_at timestamp default current_timestamp() null
);

