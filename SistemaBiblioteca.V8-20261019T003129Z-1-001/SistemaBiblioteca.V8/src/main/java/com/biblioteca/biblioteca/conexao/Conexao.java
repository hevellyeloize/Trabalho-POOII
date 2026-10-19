package com.biblioteca.biblioteca.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    public static Connection conectar() {
        String url = "jdbc:postgresql://localhost:5432/POO"; //POO é o nome do meu banco, colocar o do seu banco ou "postgres"
        String user = "postgres";
        String password = "123456789"; //Colocar a sua senha aqui

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver do PostgreSQL não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("FALHA: Erro ao conectar ao banco.");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Connection conn = conectar();
        if (conn != null) {
            System.out.println("SUCESSO: Conectado ao PostgreSQL com sucesso!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}