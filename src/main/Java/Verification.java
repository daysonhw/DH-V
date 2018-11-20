import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
/**
 * Copyright under Apache License version 2.0
 * java verification component
 * @author Dayson Hwang
 * November 2018
 * */

public class Verification extends HttpServlet {
    private Connection conn = null;
    private boolean success = false;
    private String username = "username";
// toDO  longCookie, shortCookie, session;

    @Override
    public void init() {
        String url = "jdbc:mysql://db.ct5.xyz:3306/user";
        String usrname = "jdbc";
        String pass = "jdbcjdbc";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, usrname, pass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("/sql connection init success!");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String temppass = encryptSHA(req.getParameter("password"));
        if (usrVerify(req.getParameter(username), temppass)) {
            System.out.println("Verify Successful!");
        }else {
            System.out.println("fail with unknown reasons");
        }
        System.out.println("recapcha verification: " + isCaptchaValid(req.getParameter("recapcha")));
    }


    public static String encryptSHA(String text) {
        String temptext = addSalt(text);
        String encodeStr;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(temptext.getBytes("UTF-8"));
            encodeStr = byte2Hex(md.digest());
            System.out.println(encodeStr);
            return encodeStr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String salt = "K6DeSsOf3";
    public static String addSalt(String text) {
        return salt + text + salt;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

    public boolean usrVerify(String user, String password) {
        try {
            PreparedStatement preparedStatement =
                    conn.prepareStatement
                            ("select id from user where username = ? and pass = ?");
            preparedStatement.setString(1,user);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("uv suc");
                return true;
            }else {
                System.out.println("user Verification unsuccessful");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String secretKey = "6Lf1lHgUAAAAAB6RoLuefXw8eNct9gQ8VOasmhJi";
    public static boolean isCaptchaValid(String response){
        try {
            String url = "https://www.recaptcha.org/recaptcha/api/siteverify?"
                    + "secret=" + secretKey
                    + "&response=" + response;
            InputStream res = new URL(url).openStream();
            JSONObject json = new JSONObject(res);
            if (json.getBoolean("success") == true) {
                System.out.println("/recatcha running ok!");
            }
            if (json.getBoolean("success") == true
                    && json.getString("hostname") == "ct5.xyz") {
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return  false;
    }

    @Override
    public void destroy() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}