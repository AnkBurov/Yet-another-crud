CREATE TABLE DBUPDATE (ID INT NOT NULL AUTO_INCREMENT,
                    SCRIPTID INT NOT NULL,
                    AUTHOR VARCHAR(45) NOT NULL,
                    SCRIPTNAME VARCHAR(255) NOT NULL,
                    PRIMARY KEY (ID),
                    UNIQUE INDEX SCRIPTID_UNIQUE (SCRIPTID ASC));

INSERT INTO DBUPDATE (SCRIPTID, AUTHOR, SCRIPTNAME)
VALUES ('1', 'Author', 'create_db_update');