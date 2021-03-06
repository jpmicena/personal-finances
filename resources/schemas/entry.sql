create table entry (
  id      integer primary key autoincrement,
  increasing_account_id integer not null,
  decreasing_account_id integer not null,
  description text not null,
  value numeric not null,
  entry_date text not null,
  post_date text not null,

  foreign key(increasing_account_id) references account(id),
  foreign key(decreasing_account_id) references account(id)
  on delete cascade
)
