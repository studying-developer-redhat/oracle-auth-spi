package com.redhat.rhsso.spi;

import com.redhat.rhsso.spi.model.User;
import oracle.jdbc.xa.client.OracleXADataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleXAConnectionTest {

    public static void main(String[] args) {

        // https://www.oracle.com/database/technologies/jdbcdriver-ucp-downloads.html
        // https://mkyong.com/maven/how-to-add-oracle-jdbc-driver-in-your-maven-local-repository/
        // mvn install:install-file -Dfile=_configuration/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar
        // jdbc:oracle:thin:@localhost:1521:XE

        OracleXADataSource oracleXADataSource = null;
        Connection connection = null;
        try {
            oracleXADataSource = new OracleXADataSource();
            oracleXADataSource.setURL("jdbc:oracle:thin:@127.0.0.1:1521");
            oracleXADataSource.setDatabaseName("ABCPDB1");
            oracleXADataSource.setUser("ot");
            oracleXADataSource.setPassword("Orcl1234");

            connection = oracleXADataSource.getConnection();
            Statement stmt = connection.createStatement();
            stmt.execute("SELECT 1 FROM DUAL");
            ResultSet rs = stmt.getResultSet();
            rs.next();
            System.out.println(stmt.getResultSet().getInt(1));

            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
