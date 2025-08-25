package com.example.simuladorfin;

public class Sacre {
    public static double calcParcela(double saldoDevedor, int mesesRestantes, double juros) {
        return calcAmortizacao(saldoDevedor, mesesRestantes) + ((juros/100) * saldoDevedor);
    }

    public static double calcAmortizacao(double saldoDevedor, int mesesRestantes) {
        return (saldoDevedor / mesesRestantes);
    }
}
