package com.sentiment;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sentiment.LJSentimentAnalysisLibrary.CLibrarySentimentAnalysis;
/**
 * Created by lxa on 2017/2/21.
 */
public class GenerateAdjList {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/other?useUnicode=true&characterEncoding=UTF-8";

    static final String USER = "root";
    static final String PASS = "123456";

    private static String pattern = "[\\d]";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection con = null;
        Statement stmt = null;
        ResultSet res = null;

        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(DB_URL, USER, PASS);
        stmt = con.createStatement();

        String sql = "SELECT opinionWord FROM potentialfeature GROUP BY opinionWord";
        String sql2 = "INSERT INTO polarity values (?,?)";
        res = stmt.executeQuery(sql);
        CLibrarySentimentAnalysis instance = init();
        PreparedStatement preparedStatement = con.prepareStatement(sql2);
        while (res.next()){
            int polarity = 0;
            String opinionword= res.getString(1);
            System.out.println(opinionword);
            byte[] afteranalyze = new byte[10000];
            instance.LJST_GetParagraphSent(opinionword, afteranalyze);
            int result = getResult(new String(afteranalyze));
            if (result == 0 || result ==1){
                polarity = 1;
            }else if(result != -99){
                polarity = -1;
            }else {
                polarity = 0;
            }
            preparedStatement.setString(1,opinionword);
            preparedStatement.setInt(2,polarity);
            preparedStatement.executeUpdate();
        }
        con.close();
        instance.LJST_Exits();
    }

    /**
     * 获得情感分析结果。
     *     EMOTION_HAPPY：表示文章中属于“乐”的情感词的个数；
     EMOTION_GOOD：表示文章中属于“好”的情感词的个数；
     EMOTION_ANGER：表示文章中属于“怒”的情感词的个数；
     EMOTION_SORROW：表示文章中属于“哀”的情感词的个数；
     EMOTION_FEAR：表示文章中属于“惧”的情感词的个数；
     EMOTION_EVIL：表示文章中属于“恶”的情感词的个数；
     EMOTION_SURPRISE：表示文章中属于“惊”的情感词的个数；
     */
    private static int getResult(String input){
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(input);
        int[] results = new int[7];
        int count = 0;
        while (matcher.find()){
            results[count] = Integer.parseInt(matcher.group(0));
            count++;
        }
        for (int i=0;i<7;i++){
            if (results[i]>0){
                return i;
            }
        }
        return -99;
    }

    public static CLibrarySentimentAnalysis init(){
        int flag =CLibrarySentimentAnalysis.Instance.LJST_Inits("sentimentAnalysisData", 1, "");
        if (flag == 0) {
            System.out.println("SentimentAnalysis初始化失败");
            System.exit(0);
        } else {
            System.out.println("SentimentAnalysis初始化成功");
        }
        return CLibrarySentimentAnalysis.Instance;
    }
}
