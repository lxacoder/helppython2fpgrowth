package com.fptree;

import spmf.algorithms.frequentpatterns.fpgrowth_with_strings.AlgoFPGrowth_Strings;
import spmf.test.MainTestFPGrowth_strings_saveToFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by lxa on 2017/2/20.
 */


public class helppython2fpgrowth {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/other";

    static final String USER = "root";
    static final String PASS = "123456";

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        Class.forName("com.mysql.jdbc.Driver");
        conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
        stmt = (Statement) conn.createStatement();

        String sql = "SELECT distinct category FROM koubei";
        rs = stmt.executeQuery(sql);

        String category = "";
        while (rs.next()) {
            category = rs.getString("category");
            System.out.println("category = " + category);

            String output = "C:/developement/python/EvaluationAnalysis/featuresetafterfpgrowth/"
                    + category + ".txt"; // the path for saving the frequent
            // itemsets found
            String input = "C:/developement/python/EvaluationAnalysis/featureset/"
                    + category + ".txt";

            double minsup = 0.005; // MINIMUM SUPPORT which the paper has used
            // i.e. 1%

            AlgoFPGrowth_Strings algo = new AlgoFPGrowth_Strings();
            algo.runAlgorithm(input, output, minsup);
            algo.printStats();

        }
        stmt.close();
        conn.close();
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestFPGrowth_strings_saveToFile.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }
}
