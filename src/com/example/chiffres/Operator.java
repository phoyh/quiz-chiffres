package com.example.chiffres;

public interface Operator {
	boolean isCommutative();
	double calculate(int a, int b);
	String getSymbol();
}
