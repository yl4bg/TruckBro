use truck;

create table users(
	userid CHAR(50) not null,
	username CHAR(50) character set gbk not null,
	salt     CHAR(50) not null,
	password CHAR(50) not null,
	points INT,
	privilege CHAR(50),
	referperson CHAR(50),
	showuseridtodriver TINYINT(1),
	showuseridtoowner TINYINT(1),
	showuseridtoher TINYINT(1),
	usertype CHAR(50),
	drivertype CHAR(50),
	primary key(userid)
);

ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table pics(
	eventid CHAR(50) not null,
	picid CHAR(50) not null,
	primary key(eventid,picid)
);

create table userlocations(
	userid CHAR(50) not null,
	longitude DOUBLE(7,4) not null,
	latitude DOUBLE(6,4) not null,
	reporttime TIMESTAMP not null,
	primary key(userid)
);

create table userdevices(
	userid CHAR(50) not null,
	deviceid CHAR(50) not null,
	cookie CHAR(50) not null,
	primary key(userid)
);

create table events(
	eventid CHAR(50) not null,
	eventtype CHAR(50) not null,
	eventtime TIMESTAMP not null,
	eventinfo CHAR(191) character set gbk,
	longitude DOUBLE(7,4) not null,
	latitude DOUBLE(6,4) not null,
	radius DOUBLE(10,3) not null,
	senderid CHAR(50) not null,
	
	roadnum CHAR(50) character set gbk,
	province CHAR(50) character set gbk,
	city CHAR(50) character set gbk,
	district CHAR(50) character set gbk,
	primary key(eventid)
);

ALTER TABLE events CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table eventinteractions(
	eventid CHAR(50) not null,
	interaction CHAR(50) not null,
	userid CHAR(50) not null,
	primary key(eventid, interaction, userid)
);

create table userinformation(
	userid CHAR(50) not null,
	nickname CHAR(50),
	hometown CHAR(50),
	portrait CHAR(50),
	signature CHAR(191),
	mytruck CHAR(50),
	mytruckpicid CHAR(50),
	boughttime CHAR(50),
	driverlicensepic CHAR(100),
	registrationpic CHAR(100),
	licenseplate CHAR(100),
	showfptodriver TINYINT(1),
	showfptoowner TINYINT(1),
	showfptoher TINYINT(1),
	primary key(userid)
);

ALTER TABLE userinformation CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table usergoodtypes(
	userid CHAR(50) not null,
	goodtype CHAR(50) not null,
	primary key(userid, goodtype)
);

create table userfrequentplaces(	
	userid CHAR(50) not null,
	frequentplace CHAR(100) not null,
	primary key(userid, frequentplace)
);

create table detailedhometown(
	userid CHAR(50) not null,
	province CHAR(20) character set gbk,
	city CHAR(20) character set gbk,
	district CHAR(20) character set gbk,
	primary key(userid)
);

create table groupchatid(
	address CHAR(150) character set gbk not null,
	chatid CHAR(50) not null,
	primary key(address)
);

create table chatsettings(
	userid CHAR(50) not null,
	chatid CHAR(50) not null,
	muted TINYINT(1),
	locked TINYINT(1),
	top TINYINT(1),
	primary key(userid, chatid)
);

create table pointshistory(
	userid CHAR(50) not null,
	pdate TIMESTAMP,
	pdesc CHAR(191),
	pdiff CHAR(50) not null,
	primary key(userid)
);

--alter table <some_table> convert to character set utf8 collate utf8_unicode_ci;

--# For each database:
--ALTER DATABASE database_name CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
--# For each table:
--ALTER TABLE table_name CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--# For each column:
--ALTER TABLE table_name CHANGE column_name column_name VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

--[mysqld]
--wait_timeout = 31536000

--[client]
--default-character-set = utf8mb4

--[mysql]
--default-character-set = utf8mb4

--[mysqld]
--character-set-client-handshake = FALSE
--character-set-server = utf8mb4
--collation-server = utf8mb4_unicode_ci