CREATE TABLE TASK (
  id_tak BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT ,
  title VARCHAR(100) NOT NULL DEFAULT 'Bez n�zvu',
  description TEXT,
  repetition_period SMALLINT,
  warning_period SMALLINT,
  completed BIT NOT NULL DEFAULT 0,
  creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completion_date TIMESTAMP NULL DEFAULT NULL,
  deadline TIMESTAMP NULL DEFAULT NULL,  
  deleted BIT NOT NULL DEFAULT 0,
  id_tcy BIGINT,
  id_lur BIGINT,
  CONSTRAINT fk_LoginUserTask FOREIGN KEY (id_lur)
  REFERENCES LOGIN_USER(id_lur), 
  CONSTRAINT fk_TaskCategoryTask FOREIGN KEY (id_tcy)
  REFERENCES TASK_CATEGORY (id_tcy) 
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci
