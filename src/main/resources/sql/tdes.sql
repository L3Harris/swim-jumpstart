CREATE TABLE IF NOT EXISTS TDES
(
  airport VARCHAR (1024),
  srcAddr VARCHAR (1024),
  datisTime VARCHAR (1024),
  header VARCHAR (1024),
  body VARCHAR (18000),
  createdAt TIMESTAMP WITH TIME ZONE default now()
);
