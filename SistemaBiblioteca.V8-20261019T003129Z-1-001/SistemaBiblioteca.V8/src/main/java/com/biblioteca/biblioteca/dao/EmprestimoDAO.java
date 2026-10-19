package com.biblioteca.biblioteca.dao;

import com.biblioteca.biblioteca.conexao.Conexao;
import com.biblioteca.biblioteca.model.Emprestimo;
import com.biblioteca.biblioteca.model.Livro;
import com.biblioteca.biblioteca.model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    public void inserir(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimo (livro_id, usuario_id, data_emprestimo, data_devolucao, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimo.getLivro().getId());
            stmt.setInt(2, emprestimo.getUsuario().getId());
            stmt.setDate(3, Date.valueOf(emprestimo.getDataEmprestimo()));
            stmt.setDate(4, emprestimo.getDataDevolucao() != null ? Date.valueOf(emprestimo.getDataDevolucao()) : null);
            stmt.setString(5, emprestimo.getStatus());

            stmt.executeUpdate();
            System.out.println("Empréstimo registrado com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir empréstimo: " + e.getMessage());
        }
    }

    public List<Emprestimo> listar() {
        String sql = "SELECT * FROM emprestimo";
        List<Emprestimo> emprestimos = new ArrayList<>();

        LivroDAO livroDAO = new LivroDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int livroId = rs.getInt("livro_id");
                int usuarioId = rs.getInt("usuario_id");

                Date dataEmpSql = rs.getDate("data_emprestimo");
                LocalDate dataEmprestimo = dataEmpSql != null ? dataEmpSql.toLocalDate() : null;

                Date dataDevSql = rs.getDate("data_devolucao");
                LocalDate dataDevolucao = dataDevSql != null ? dataDevSql.toLocalDate() : null;

                String status = rs.getString("status");

                Livro livro = livroDAO.listar().stream().filter(l -> l.getId() == livroId).findFirst().orElse(null);
                Usuario usuario = usuarioDAO.listar().stream().filter(u -> u.getId() == usuarioId).findFirst().orElse(null);

                Emprestimo emprestimo = new Emprestimo(id, livro, usuario, dataEmprestimo, dataDevolucao, status);
                emprestimos.add(emprestimo);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar empréstimos: " + e.getMessage());
        }

        return emprestimos;
    }

    public void atualizar(Emprestimo emprestimo) {
        String sql = "UPDATE emprestimo SET livro_id = ?, usuario_id = ?, data_emprestimo = ?, data_devolucao = ?, status = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimo.getLivro().getId());
            stmt.setInt(2, emprestimo.getUsuario().getId());
            stmt.setDate(3, Date.valueOf(emprestimo.getDataEmprestimo()));
            stmt.setDate(4, emprestimo.getDataDevolucao() != null ? Date.valueOf(emprestimo.getDataDevolucao()) : null);
            stmt.setString(5, emprestimo.getStatus());
            stmt.setInt(6, emprestimo.getId());

            stmt.executeUpdate();
            System.out.println("Empréstimo atualizado com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar empréstimo: " + e.getMessage());
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM emprestimo WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Empréstimo excluído com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao excluir empréstimo: " + e.getMessage());
        }
    }
}