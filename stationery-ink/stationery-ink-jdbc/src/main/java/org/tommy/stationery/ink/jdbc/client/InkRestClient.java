package org.tommy.stationery.ink.jdbc.client;

import org.tommy.stationery.ink.domain.BaseStatement;
import org.tommy.stationery.ink.jdbc.exception.InkSQLException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by kun7788 on 15. 1. 30..
 */
public class InkRestClient {
    static final boolean DEBUG = false;

    List params;
    int pos;

    private InkHttpClient client;
    private String url;
    private String sql;
    private String sessionId;
    private List<BaseStatement> statements;
    private String user;
    private String password;

    public void close() {

    }

    public InkRestClient() {

    }

    public static String initializeSession() {
        InetAddress localIp = null;
        try {
            localIp = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        }

        String sessionId = localIp.toString() + "_" + System.currentTimeMillis();
        System.out.println("sessionId : " + sessionId);
        return sessionId;
    }

    public String send(String sql) {
        return client.send(sessionId, user, password, sql);
    }

    public String send() {
        return client.send(sessionId,user, password,  sql);
    }

    public InkRestClient(String url) {
        this.url = url;
        client = new InkHttpClient(url);
    }

    public InkRestClient(String url, String sql)
            throws InkSQLException, Exception {
        this.url = url;
        this.sql = sql;
        //statements = parse( sql );

        client = new InkHttpClient(url);

        if ( DEBUG ) System.out.println( sql );
    }

    void setParams( List params ){
        pos = 1;
        this.params = params;
    }

    public String query() throws InkSQLException {
        String ret = client.send(sessionId, user, password, sql);
        return ret;
    }

    /*public List<BaseStatement> parse( String sql ) throws InkSQLException, Exception {
        sql = sql.trim();
        InkSqlParser inkSqlParser = new InkSqlParser();
        List<BaseStatement> statements = inkSqlParser.prepare(sql);
        return statements;
    }*/

    public InkHttpClient getClient() {
        return client;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
