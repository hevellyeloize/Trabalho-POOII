package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.dao.EmprestimoDAO;
import com.biblioteca.biblioteca.dao.LivroDAO;
import com.biblioteca.biblioteca.dao.UsuarioDAO;
import com.biblioteca.biblioteca.model.Emprestimo;
import com.biblioteca.biblioteca.model.Livro;
import com.biblioteca.biblioteca.model.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.biblioteca.biblioteca.dao.MultaDAO;
import com.biblioteca.biblioteca.model.Multa;
import java.time.temporal.ChronoUnit;

import java.time.LocalDate;

public class EmprestimoController {

    @FXML private ComboBox<Usuario> cbUsuario;
    @FXML private ComboBox<Livro> cbLivro;
    @FXML private DatePicker dpDataEmprestimo;
    @FXML private DatePicker dpDataDevolucao;
    @FXML private ComboBox<String> cbStatus;

    @FXML private TableView<Emprestimo> tabelaEmprestimos;
    @FXML private TableColumn<Emprestimo, Integer> colId;
    @FXML private TableColumn<Emprestimo, String> colUsuario;
    @FXML private TableColumn<Emprestimo, String> colLivro;
    @FXML private TableColumn<Emprestimo, LocalDate> colDataEmprestimo;
    @FXML private TableColumn<Emprestimo, LocalDate> colDataDevolucao;
    @FXML private TableColumn<Emprestimo, String> colStatus;

    private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private LivroDAO livroDAO = new LivroDAO();
    private MultaDAO multaDAO = new MultaDAO();

    private ObservableList<Emprestimo> listaEmprestimos = FXCollections.observableArrayList();
    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private ObservableList<Livro> listaLivros = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDataEmprestimo.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
        colDataDevolucao.setCellValueFactory(new PropertyValueFactory<>("dataDevolucao"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colUsuario.setCellValueFactory(cellData -> {
            Usuario u = cellData.getValue().getUsuario();
            return new SimpleStringProperty(u != null ? u.getNome() : "");
        });

        colLivro.setCellValueFactory(cellData -> {
            Livro l = cellData.getValue().getLivro();
            return new SimpleStringProperty(l != null ? l.getTitulo() : "");
        });

        cbStatus.setItems(FXCollections.observableArrayList("Ativo", "Devolvido", "Atrasado"));

        carregarCombos();
        carregarTabela();

        tabelaEmprestimos.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                cbUsuario.setValue(selecionado.getUsuario());
                cbLivro.setValue(selecionado.getLivro());
                dpDataEmprestimo.setValue(selecionado.getDataEmprestimo());
                dpDataDevolucao.setValue(selecionado.getDataDevolucao());
                cbStatus.setValue(selecionado.getStatus());
            }
        });
    }

    private void carregarCombos() {
        listaUsuarios.clear();
        listaUsuarios.addAll(usuarioDAO.listar());
        cbUsuario.setItems(listaUsuarios);

        listaLivros.clear();
        listaLivros.addAll(livroDAO.listar());
        cbLivro.setItems(listaLivros);
    }

    private void carregarTabela() {
        listaEmprestimos.clear();
        listaEmprestimos.addAll(emprestimoDAO.listar());
        tabelaEmprestimos.setItems(listaEmprestimos);
    }

    @FXML
    protected void onSalvarClick() {
        try {
            Usuario usuario = cbUsuario.getValue();
            Livro livro = cbLivro.getValue();
            LocalDate dataEmprestimo = dpDataEmprestimo.getValue();
            LocalDate dataDevolucao = dpDataDevolucao.getValue();
            String status = cbStatus.getValue();

            if (usuario == null || livro == null || dataEmprestimo == null || status == null) {
                mostrarAlerta("Aviso", "Preencha todos os campos obrigatórios (Usuário, Livro, Data Empréstimo e Status).", Alert.AlertType.WARNING);
                return;
            }

            Emprestimo emprestimoSelecionado = tabelaEmprestimos.getSelectionModel().getSelectedItem();

            if (emprestimoSelecionado == null) {
                // Inserção
                Emprestimo novoEmprestimo = new Emprestimo(0, livro, usuario, dataEmprestimo, dataDevolucao, status);
                emprestimoDAO.inserir(novoEmprestimo);

                // Multa Automática
                if ("Atrasado".equalsIgnoreCase(status)) {
                    gerarMultaAutomatica(novoEmprestimo);
                }

                mostrarAlerta("Sucesso", "Empréstimo registrado com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                // Atualização
                emprestimoSelecionado.setUsuario(usuario);
                emprestimoSelecionado.setLivro(livro);
                emprestimoSelecionado.setDataEmprestimo(dataEmprestimo);
                emprestimoSelecionado.setDataDevolucao(dataDevolucao);
                emprestimoSelecionado.setStatus(status);
                emprestimoDAO.atualizar(emprestimoSelecionado);

                // Multa Automática
                if ("Atrasado".equalsIgnoreCase(status)) {
                    gerarMultaAutomatica(emprestimoSelecionado);
                }

                mostrarAlerta("Sucesso", "Empréstimo atualizado com sucesso!", Alert.AlertType.INFORMATION);
            }

            limparCampos();
            carregarTabela();
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao salvar empréstimo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void gerarMultaAutomatica(Emprestimo emprestimo) {
        if (emprestimo == null) return;

        LocalDate hoje = LocalDate.now();
        LocalDate dataPrevista = emprestimo.getDataDevolucao() != null ? emprestimo.getDataDevolucao() : hoje;

        Multa multa = new Multa();
        multa.setEmprestimo(emprestimo);
        multa.setPaga(false);

        multa.calcularMultaPorAtraso(2.50, hoje);

        if (multa.getValor() <= 0.0) {
            long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(dataPrevista, hoje);
            if (diasAtraso <= 0) diasAtraso = 1;
            multa.setValor(diasAtraso * 2.50);
        }

        multaDAO.inserir(multa);
    }

    @FXML
    protected void onExcluirClick() {
        Emprestimo selecionado = tabelaEmprestimos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            emprestimoDAO.deletar(selecionado.getId());
            limparCampos();
            carregarTabela();
            mostrarAlerta("Sucesso", "Empréstimo excluído com sucesso!", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Aviso", "Selecione um empréstimo na tabela para excluir.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    protected void onLimparClick() {
        limparCampos();
    }

    private void limparCampos() {
        cbUsuario.setValue(null);
        cbLivro.setValue(null);
        dpDataEmprestimo.setValue(null);
        dpDataDevolucao.setValue(null);
        cbStatus.setValue(null);
        tabelaEmprestimos.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String conteudo, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}