package com.biblioteca.biblioteca.dao;

import com.biblioteca.biblioteca.conexao.Conexao;
import com.biblioteca.biblioteca.model.Emprestimo;
import com.biblioteca.biblioteca.model.Multa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MultaDAO {

    public void inserir(Multa multa) {
        String sql = "INSERT INTO multa (emprestimo_id, valor, paga) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, multa.getEmprestimo().getId());
            stmt.setDouble(2, multa.getValor());
            stmt.setBoolean(3, multa.isPaga());

            stmt.executeUpdate();
            System.out.println("Multa cadastrada com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir multa: " + e.getMessage());
        }
    }

    public List<Multa> listar() {
        String sql = "SELECT * FROM multa";
        List<Multa> multas = new ArrayList<>();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int emprestimoId = rs.getInt("emprestimo_id");
                double valor = rs.getDouble("valor");
                boolean paga = rs.getBoolean("paga");

                Emprestimo emprestimo = emprestimoDAO.listar().stream().filter(e -> e.getId() == emprestimoId).findFirst().orElse(null);

                Multa multa = new Multa(id, emprestimo, valor, paga);
                multas.add(multa);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar multas: " + e.getMessage());
        }

        return multas;
    }

    public void atualizar(Multa multa) {
        String sql = "UPDATE multa SET emprestimo_id = ?, valor = ?, paga = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, multa.getEmprestimo().getId());
            stmt.setDouble(2, multa.getValor());
            stmt.setBoolean(3, multa.isPaga());
            stmt.setInt(4, multa.getId());

            stmt.executeUpdate();
            System.out.println("Multa atualizada com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar multa: " + e.getMessage());
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM multa WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Multa excluída com sucesso");

        } catch (SQLException e) {
            System.err.println("Erro ao excluir multa: " + e.getMessage());
        }
    }
}