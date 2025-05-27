package forms;

import java.sql.*;

public class Database {

    //FUNCIÓ PER COMPROVAR SI EL NOM D'USUARI EXISTEIX
    protected static Boolean comprovarUser(String username){
        String db_url = "jdbc:mysql://localhost:3306/kronos";
        String db_user = "adminMax";
        String db_pass = "admin";
        String selectUser = "select nom from usuari where nom like '"+username+"'";

        //variable per comprovar que existeix
        Boolean existeix;

        try {
            Connection con = DriverManager.getConnection(db_url,db_user,db_pass);

            //Select per comprovar si el nom d'usuari existeix
            PreparedStatement ps = con.prepareStatement(selectUser);
            ResultSet rs = ps.executeQuery();

            //Si existeix es guarda a la variable com true
            existeix = rs.next();

            rs.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return existeix;
    }

    //FUNCIÓ PER INSERTAR PARTIDA A LA BASE DE DADES
    protected static void insertarPartida(String username, Integer points){
        String db_url = "jdbc:mysql://localhost:3306/kronos";
        String db_user = "adminMax";
        String db_pass = "admin";
        String selectUser = "select * from usuari where nom like '"+username+"'";
        String insertGame = "insert into joc(id_usuari,puntuacio) values(?,?)";

        try {
            Connection con = DriverManager.getConnection(db_url,db_user,db_pass);

            //Guarda el id de l'usuari que ha jugat la partida
            PreparedStatement ps = con.prepareStatement(selectUser);
            ResultSet rs = ps.executeQuery();

            //Guarda el id de l'usuari
            int id_user = 0;
            if (rs.next()) {
                id_user = rs.getInt("id_usuari");
            }

            //Fa l'insert
            PreparedStatement ps2 = con.prepareStatement(insertGame);
            ps2.setInt(1, id_user);
            ps2.setInt(2, points);

            //Comprova que l'insert s'ha fet correctament
            int addRows = ps2.executeUpdate();
            if(addRows > 0){
                System.out.println("Partida agregada exitosamente");
            }

            rs.close();
            ps2.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    //FUNCIÓ INSERTAR USUARI A LA BASE DE DADES
    protected static void insertarUsuari(String username){
        String db_url = "jdbc:mysql://localhost:3306/kronos";
        String db_user = "adminMax";
        String db_pass = "admin";
        String insertUser = "insert into usuari(nom) values(?)";

        try {
            Connection con = DriverManager.getConnection(db_url,db_user,db_pass);

            //Fer l'insert
            PreparedStatement ps = con.prepareStatement(insertUser);
            ps.setString(1,username);

            //Comprovar si s'ha realitzat
            int addRows = ps.executeUpdate();
            if(addRows > 0){
                System.out.println("Usuario agregado exitosamente");
            }

            ps.close();
            con.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    //FUNCIÓ DE MOSTRA EL TOP DE JUGADORS
    protected static Object top(){
        //Conectarse a la base de dades
        String db_url = "jdbc:mysql://localhost:3306/kronos";
        String db_user = "adminMax";
        String db_pass = "admin";
        String globalTop = " select j.id_partida,j.id_usuari,u.nom,j.puntuacio from joc j join usuari u on u.id_usuari = j.id_usuari order by j.puntuacio desc;";
        String message = "GLOBAL TOP:\nPoints  -  Username\n";

        try {
            Connection con = DriverManager.getConnection(db_url,db_user,db_pass);

            //Executar el select
            PreparedStatement ps = con.prepareStatement(globalTop);
            ResultSet rs = ps.executeQuery();
            String top = "";
            int count = 0;

            //Bucle que afageix al top les 5 partides amb més punts
            while (rs.next() && count < 5) {
                top = top + (rs.getInt(4)+"     -   "+ rs.getString(3)+"\n");
                count++;
            }

            message = message+top;

            rs.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return message;
    }
}
