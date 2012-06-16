
CREATE TABLE shaftuser (
 shaftuser varchar(50) NOT NULL,
 app varchar(50) NOT null,
 shaftusertype varchar(20) NOT null,
 digest varchar(50) NOT null,
 shaftowner varchar(50),
 PRIMARY KEY  (shaftuser,app)

) ;

CREATE TABLE shaftpasswd (
 shaftuser varchar(50) NOT NULL,
 app varchar(50) NOT null,
 shaftpassword varchar(150) NOT null,
 md5 varchar(200),
 PRIMARY KEY  (shaftuser,app)
) ;

CREATE TABLE shaftsession (
 shaftuser varchar(50) NOT NULL,
 app varchar(50) NOT null,
 shaftsessionid varchar(150) NOT null,
 sessionage TIMESTAMP default CURRENT_TIMESTAMP,
 PRIMARY KEY  (shaftuser,app)
) ;

CREATE TABLE shaftrealminvite (
 shaftuser varchar(50) NOT NULL,
 app varchar(50) NOT null,
 shaftinvitee varchar(50) NOT NULL,
 inviteage TIMESTAMP default CURRENT_TIMESTAMP,
 shaftrealmid varchar(50) NOT null,
 PRIMARY KEY  (shaftuser,shaftinvitee,app,shaftrealmid)
) ;

CREATE TABLE shaftrealmmembers (
  shaftuser varchar(50) NOT null,
   app varchar(50) NOT null,

 shaftrealmid varchar(50) NOT null,
 PRIMARY KEY  (shaftuser,app,shaftrealmid)
) ;

CREATE TABLE shaftrealm (
  shaftuser varchar(50) NOT null,
   app varchar(50) NOT null,
 shaftrealmid varchar(50) NOT null,
  shaftrealmtype varchar(20) default null,
 memberscaninvite boolean NOT null,
 secretmembership boolean NOT null,
 PRIMARY KEY  (shaftuser,app,shaftrealmid)
) ;

CREATE TABLE shaftvhost (
   app varchar(50) NOT null,

 virtualhost varchar(150) NOT null UNIQUE,
 PRIMARY KEY  (app,virtualhost)
) ;

