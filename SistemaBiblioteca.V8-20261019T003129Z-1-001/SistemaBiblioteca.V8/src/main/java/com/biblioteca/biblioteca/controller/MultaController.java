package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.dao.EmprestimoDAO;
import com.biblioteca.biblioteca.dao.MultaDAO;
import com.biblioteca.biblioteca.model.Emprestimo;
import com.biblioteca.biblioteca.model.Multa;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MultaController {

    @FXML private ComboBox<Emprestimo> cbEmprestimo;
    @FXML private TextField txtValor;
    @FXML private ComboBox<String> cbStatusPagamento;

    @FXML private TableView<Multa> tabelaMultas;
    @FXML private TableColumn<Multa, Integer> colId;
    @FXML private TableColumn<Multa, String> colEmprestimo;
    @FXML private TableColumn<Multa, String> colValor;
    @FXML private TableColumn<Multa, String> colStatusPagamento;

    private MultaDAO multaDAO = new MultaDAO();
    private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

    private ObservableList<Multa> listaMultas = FXCollections.observableArrayList();
    private ObservableList<Emprestimo> listaEmprestimos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colValor.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("R$ %.2f", cellData.getValue().getValor()))
        );

        colStatusPagamento.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isPaga() ? "Paga" : "Pendente")
        );

        colEmprestimo.setCellValueFactory(cellData -> {
            Emprestimo e = cellData.getValue().getEmprestimo();
            if (e != null) {
                String usuario = e.getUsuario() != null ? e.getUsuario().getNome() : "Usuário Desconhecido";
                String livro = e.getLivro() != null ? e.getLivro().getTitulo() : "Livro Desconhecido";
                return new SimpleStringProperty(usuario + " - " + livro);
            }
            return new SimpleStringProperty("");
        });

        cbStatusPagamento.setItems(FXCollections.observableArrayList("Pendente", "Paga"));

        carregarCombos();
        carregarTabela();

        tabelaMultas.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                cbEmprestimo.setValue(selecionado.getEmprestimo());
                txtValor.setText(String.valueOf(selecionado.getValor()));
                cbStatusPagamento.setValue(selecionado.isPaga() ? "Paga" : "Pendente");
            }
        });
    }

    private void carregarCombos() {
        listaEmprestimos.clear();
        listaEmprestimos.addAll(emprestimoDAO.listar());
        cbEmprestimo.setItems(listaEmprestimos);
    }

    private void carregarTabela() {
        listaMultas.clear();
        listaMultas.addAll(multaDAO.listar());
        tabelaMultas.setItems(listaMultas);
    }

    @FXML
    protected void onSalvarClick() {
        try {
            Emprestimo emprestimo = cbEmprestimo.getValue();
            String statusStr = cbStatusPagamento.getValue();

            if (emprestimo == null || statusStr == null || txtValor.getText().isEmpty()) {
                mostrarAlerta("Aviso", "Preencha todos os campos obrigatórios (Empréstimo, Valor e Status).", Alert.AlertType.WARNING);
                return;
            }

            double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
            boolean paga = statusStr.equalsIgnoreCase("Paga");

            Multa multaSelecionada = tabelaMultas.getSelectionModel().getSelectedItem();

            if (multaSelecionada == null) {
                // Inserção
                Multa novaMulta = new Multa(0, emprestimo, valor, paga);
                multaDAO.inserir(novaMulta);
                mostrarAlerta("Sucesso", "Multa cadastrada com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                // Atualização
                multaSelecionada.setEmprestimo(emprestimo);
                multaSelecionada.setValor(valor);
                multaSelecionada.setPaga(paga);
                multaDAO.atualizar(multaSelecionada);
                mostrarAlerta("Sucesso", "Multa atualizada com sucesso!", Alert.AlertType.INFORMATION);
            }

            limparCampos();
            carregarTabela();
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Validação", "Por favor, insira um valor numérico válido para a multa.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao salvar multa: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onExcluirClick() {
        Multa selecionada = tabelaMultas.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            multaDAO.deletar(selecionada.getId());
            limparCampos();
            carregarTabela();
            mostrarAlerta("Sucesso", "Multa excluída com sucesso!", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Aviso", "Selecione uma multa na tabela para excluir.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    protected void onLimparClick() {
        limparCampos();
    }

    private void limparCampos() {
        cbEmprestimo.setValue(null);
        txtValor.clear();
        cbStatusPagamento.setValue(null);
        tabelaMultas.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String conteudo, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}