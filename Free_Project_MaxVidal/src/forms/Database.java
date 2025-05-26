package forms;

import java.sql.*;

public class Database {

    protected static Boolean comprovarUser(String username){
        String db_url = "jdbc:mysql://localhost:3306/kronos";
        String db_user = "adminMax";
        String db_pass = "admin";
        String selectUser = "select nom from usuari where nom like '"+username+"'";

        Boolean existeix;

        try {
            Connection con = DriverManager.getConnection(db_url,db_user,db_pass);
            PreparedStatement ps = con.prepareStatement(selectUser);
            ResultSet rs = ps.executeQuery();
            existeix = rs.next();

            rs.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return existeix;
    }

    protected static void insertarPartida(String username, Integer points){
        String db_url = "jdbc:mysql://localhost:3306/kronos";
        String db_user = "adminMax";
        String db_pass = "admin";
        String selectUser = "select * from usuari where nom like '"+username+"'";
        String insertGame = "insert into joc(id_usuari,puntuacio) values(?,?)";

        try {
            Connection con = DriverManager.getConnection(db_url,db_user,db_pass);
            PreparedStatement ps = con.prepareStatement(selectUser);
            ResultSet rs = ps.executeQuery();
            int id_user = 0;
            if (rs.next()) {
                id_user = rs.getInt("id_usuari");
            }

            PreparedStatement ps2 = con.prepareStatement(insertGame);
            ps2.setInt(1, id_user);
            ps2.setInt(2, points);
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

    protected static void insertarUsuari(String username){
        String db_url = "jdbc:mysql://localhost:3306/kronos";
        String db_user = "adminMax";
        String db_pass = "admin";
        String insertUser = "insert into usuari(nom) values(?)";

        try {
            Connection con = DriverManager.getConnection(db_url,db_user,db_pass);
            PreparedStatement ps = con.prepareStatement(insertUser);
            ps.setString(1,username);
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

    protected static Object top(){
        String db_url = "jdbc:mysql://localhost:3306/kronos";
        String db_user = "adminMax";
        String db_pass = "admin";
        String globalTop = " select j.id_partida,j.id_usuari,u.nom,j.puntuacio from joc j join usuari u on u.id_usuari = j.id_usuari order by j.puntuacio desc;";
        String message = "GLOBAL TOP:\nPoints  -  Username\n";

        try {
            Connection con = DriverManager.getConnection(db_url,db_user,db_pass);
            PreparedStatement ps = con.prepareStatement(globalTop);
            ResultSet rs = ps.executeQuery();
            String top = "";
            int count = 0;
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
