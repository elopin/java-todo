CREATE TABLE TASK_CATEGORY (
  id_tcy BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT ,
  title VARCHAR(100) NOT NULL DEFAULT 'Bez názvu',
  backgroundColor INT,
  textColor INT,
  deleted BIT NOT NULL DEFAULT 0,  
  id_lur BIGINT,
  CONSTRAINT fk_LoginUserTaskCategory FOREIGN KEY (id_lur)
  REFERENCES LOGIN_USER(id_lur)  
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci
