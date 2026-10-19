package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.dao.UsuarioDAO;
import com.biblioteca.biblioteca.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UsuarioController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;

    @FXML private TableView<Usuario> tabelaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colTelefone;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        carregarTabela();

        tabelaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                txtNome.setText(selecionado.getNome());
                txtEmail.setText(selecionado.getEmail());
                txtTelefone.setText(selecionado.getTelefone());
            }
        });
    }

    private void carregarTabela() {
        listaUsuarios.clear();
        listaUsuarios.addAll(usuarioDAO.listar());
        tabelaUsuarios.setItems(listaUsuarios);
    }

    @FXML
    protected void onSalvarClick() {
        try {
            String nome = txtNome.getText();
            String email = txtEmail.getText();
            String telefone = txtTelefone.getText();

            if (nome.isEmpty()) {
                mostrarAlerta("Aviso", "O nome do usuário é obrigatório.", Alert.AlertType.WARNING);
                return;
            }

            Usuario usuarioSelecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();

            if (usuarioSelecionado == null) {
                // Inserção
                Usuario novoUsuario = new Usuario(0, nome, email, telefone);
                usuarioDAO.inserir(novoUsuario);
                mostrarAlerta("Sucesso", "Usuário cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                // Atualização
                usuarioSelecionado.setNome(nome);
                usuarioSelecionado.setEmail(email);
                usuarioSelecionado.setTelefone(telefone);
                usuarioDAO.atualizar(usuarioSelecionado);
                mostrarAlerta("Sucesso", "Usuário atualizado com sucesso!", Alert.AlertType.INFORMATION);
            }

            limparCampos();
            carregarTabela();
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao salvar usuário: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onExcluirClick() {
        Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            usuarioDAO.deletar(selecionado.getId());
            limparCampos();
            carregarTabela();
            mostrarAlerta("Sucesso", "Usuário excluído com sucesso!", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Aviso", "Selecione um usuário na tabela para excluir.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    protected void onLimparClick() {
        limparCampos();
    }

    private void limparCampos() {
        txtNome.clear();
        txtEmail.clear();
        txtTelefone.clear();
        tabelaUsuarios.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String conteudo, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}