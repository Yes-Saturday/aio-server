--drop table `system_user`;
CREATE TABLE system_user (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	username TEXT,
	password TEXT,
	salt TEXT,
	level INTEGER,
	create_time TEXT
);

--drop table `blog_info`;
CREATE TABLE blog_info (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	pid INTEGER,
	gid INTEGER,
	title TEXT,
	markdown TEXT,
	html TEXT,
	level INTEGER,
	del INTEGER,
	create_time TEXT,
	update_uid INTEGER,
	update_time TEXT
);

--drop table `blog_history`;
CREATE TABLE `blog_history` (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	bid INTEGER,
	title TEXT,
	markdown TEXT,
	create_uid INTEGER,
	create_time datetime
);