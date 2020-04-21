package org.chiffres;

public interface Operator {
	boolean isCommutative();
	double calculate(int a, int b);
	String getSymbol();
}
