-- Crear base de datos
CREATE DATABASE IF NOT EXISTS kronos;
USE kronos;

-- taula USUARI
CREATE TABLE usuari (
    id_usuari INT NOT NULL auto_increment PRIMARY KEY,
    nom CHAR(50) NOT NULL
);

-- taula REPTE
CREATE TABLE reptes (
    id_repte INT NOT NULL auto_increment PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    descripcio VARCHAR(200) NOT NULL
);

-- taula JOC
CREATE TABLE joc (
    id_partida INT NOT NULL auto_increment PRIMARY KEY,
    id_usuari INT NOT NULL,
    puntuacio INT NOT NULL,
    FOREIGN KEY (id_usuari) REFERENCES usuari(id_usuari)
);

-- taula ACONSEGUIR_REPTE
CREATE TABLE aconseguir_repte (
    id_partida INT NOT NULL,
    id_repte INT NOT NULL,
    PRIMARY KEY (id_partida, id_repte),
    FOREIGN KEY (id_partida) REFERENCES joc(id_partida),
    FOREIGN KEY (id_repte) REFERENCES reptes(id_repte)
);

-- CREATE USER adminMax@localhost IDENTIFIED BY 'admin';
-- GRANT SELECT, INSERT ON kronos.* TO adminMax@localhost;


insert into usuari(nom) values ('Paco');
insert into usuari(nom) values ('Nil');
insert into usuari(nom) values ('Iago');
insert into usuari(nom) values ('Hector');
insert into usuari(nom) values ('Gael');

insert into joc(id_usuari,puntuacio) values(5,5912);
insert into joc(id_usuari,puntuacio) values(2,2736);
insert into joc(id_usuari,puntuacio) values(4,3201);
insert into joc(id_usuari,puntuacio) values(1,3495);
insert into joc(id_usuari,puntuacio) values(3,3007);





