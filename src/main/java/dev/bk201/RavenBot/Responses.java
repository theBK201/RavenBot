package dev.bk201.RavenBot;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Responses {
    private Connection connect(){
        String url = "jdbc:sqlite:src/main/resources/responses";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void insertResponseSQl(String response, String value, String user){
        String sql = "INSERT INTO responses(response,value,user,datetime) VALUES (?,?,?,?)";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        Date date = new Date();
        Date currentTimestamp = new Timestamp(date.getTime());
        String datetime = sdf1.format(currentTimestamp);

        try (Connection conn = this.connect()){
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, response);
            preparedStatement.setString(2, value);
            preparedStatement.setString(3, user);
            preparedStatement.setString(4, datetime);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public String searchResponseSQL(String Key) {
        String sql = "SELECT response,value FROM responses WHERE response = ?";
        String response = "";

        try(Connection conn = this.connect()){
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, Key);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                response = rs.getString(2);
            }

        } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        return response;
    }


//    public List<String> giveAllResponses(boolean redis){
//        Set<String> redisKeys;
//        List<String> keyList = new ArrayList<>();
//        if (redis){
//            try (Jedis jedis = pool.getResource()){
//                redisKeys = jedis.keys("*");
//                Iterator<String> it = redisKeys.iterator();
//
//                while (it.hasNext()){
//                    String data = it.next();
//                    keyList.add(data);
//                }
//            }
//        }
//        return keyList;
//    }

    public boolean checkForDuplicateSQL(String key){
        boolean duplicate = false;
        String sql = "SELECT response FROM responses WHERE response = ?";
        String tmp= "";

        try (Connection conn = this.connect()){
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setString(1, key);
            ResultSet rs = psmt.executeQuery();

            while (rs.next()){
                tmp = rs.getString(1);
            }

            if (tmp.equals(key)){
                duplicate = true;
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return duplicate;
    }

    public void editResponse(String key, String newValue) throws SQLException {
        String sql = "UPDATE responses SET value = ?," +
                     "WHERE response = ?";

        try (Connection conn = this.connect()){
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setString(1, newValue);
            psmt.setString(2, key);

            psmt.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    public void deleteResponse(String key) throws SQLException {
        String sql = "DELETE FROM responses WHERE response = ?";

        try (Connection conn = this.connect()){
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setString(1,key);
            psmt.executeUpdate();
        }
    }
}

