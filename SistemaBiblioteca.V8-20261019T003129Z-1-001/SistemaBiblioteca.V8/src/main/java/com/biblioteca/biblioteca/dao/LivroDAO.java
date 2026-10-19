package com.biblioteca.biblioteca.dao;

import com.biblioteca.biblioteca.conexao.Conexao;
import com.biblioteca.biblioteca.model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    public void inserir(Livro livro) {
        String sql = "INSERT INTO livro (titulo, autor, ano, quantidade) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setInt(3, livro.getAno());
            stmt.setInt(4, livro.getQuantidade());

            stmt.executeUpdate();
            System.out.println("Livro cadastrado com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir livro: " + e.getMessage());
        }
    }

    public List<Livro> listar() {
        String sql = "SELECT * FROM livro";
        List<Livro> livros = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                int ano = rs.getInt("ano");
                int quantidade = rs.getInt("quantidade");

                Livro livro = new Livro(id, titulo, autor, ano, quantidade);
                livros.add(livro);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar livros: " + e.getMessage());
        }

        return livros;
    }

    public void atualizar(Livro livro) {
        String sql = "UPDATE livro SET titulo = ?, autor = ?, ano = ?, quantidade = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setInt(3, livro.getAno());
            stmt.setInt(4, livro.getQuantidade());
            stmt.setInt(5, livro.getId());

            stmt.executeUpdate();
            System.out.println("Livro atualizado com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar livro: " + e.getMessage());
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM livro WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Livro excluído com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao excluir livro: " + e.getMessage());
        }
    }
}