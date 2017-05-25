CREATE TABLE IF NOT EXISTS APTC
(
  sourceFacility VARCHAR (1024),
  sourceTimestamp TIMESTAMP WITH TIME ZONE,
  sensitivity VARCHAR (1024),
  eventTime TIMESTAMP WITH TIME ZONE,
  entryTime TIMESTAMP WITH TIME ZONE,
  facility VARCHAR (1024),
  airport VARCHAR (1024),
  arrRunwayConf VARCHAR (1024),
  depRunwayConf VARCHAR (1024),
  arrRate BIGINT,
  depRate BIGINT,
  updateTime TIMESTAMP WITH TIME ZONE,
  createdAt TIMESTAMP WITH TIME ZONE default now()
);
