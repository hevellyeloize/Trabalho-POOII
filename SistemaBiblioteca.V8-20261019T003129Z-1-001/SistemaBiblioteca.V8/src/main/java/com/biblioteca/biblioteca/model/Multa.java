package com.biblioteca.biblioteca.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Multa {
    private int id;
    private Emprestimo emprestimo;
    private double valor;
    private boolean paga; // true eh quitada, false eh pendente

    public Multa() {
    }

    public Multa(int id, Emprestimo emprestimo, double valor, boolean paga) {
        this.id = id;
        this.emprestimo = emprestimo;
        this.valor = valor;
        this.paga = paga;
    }

    public void calcularMultaPorAtraso(double valorPorDia, LocalDate dataEntrega) {
        if (this.emprestimo != null && this.emprestimo.getDataDevolucao() != null) {
            if (dataEntrega.isAfter(this.emprestimo.getDataDevolucao())) {
                long diasDeAtraso = ChronoUnit.DAYS.between(this.emprestimo.getDataDevolucao(), dataEntrega);
                this.valor = diasDeAtraso * valorPorDia;
            } else {
                this.valor = 0.0;
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public boolean isPaga() {
        return paga;
    }

    public void setPaga(boolean paga) {
        this.paga = paga;
    }
}