package org.chiffres;

import java.util.LinkedList;
import java.util.List;

public class CalculationState {
	private List<Integer> inputs;
	private List<String> priorSteps;
	
	public CalculationState(List<Integer> inputs, List<String> priorSteps) {
		this.inputs = new LinkedList<Integer>(inputs);
		this.priorSteps = new LinkedList<String>(priorSteps);
	};
	
	public boolean contains(int number) {
		return inputs.contains(number);
	}
	
	public List<String> getPriorSteps() {
		return priorSteps;
	}
	
	public String getInputsString() {
		return inputs.toString();
	}
	
	public int getInputsNumber() {
		return inputs.size();
	}
	
	public List<CalculationState> getSuccessors(List<Operator> operators) {
		List<CalculationState> result = new LinkedList<CalculationState>();
		if (inputs.size() <= 1) return result;
		for (int i = 0; i < inputs.size(); i++) {
			for (int j = 0; j < inputs.size(); j++) {
				if (i != j) {
					for (Operator op : operators) {
						if (!op.isCommutative() || i < j) {
							double calcResult = op.calculate(inputs.get(i), inputs.get(j));
							if (isNumberValid(calcResult)) {
								result.add(createNewSuccessor(i, j, op, calcResult));
							}
						}
					}
				}
			}
		}
		return result;
	}

	private boolean isNumberValid(double calcResult) {
		double roundedCalcResult = Math.round(calcResult);
		if (roundedCalcResult <= 0) return false;
		return (Math.abs(calcResult / roundedCalcResult - 1) < 0.000000000001);
	}

	private CalculationState createNewSuccessor(int i,
			int j, Operator op, double calcResult) {
		int intCalcResult = (int) Math.round(calcResult);
		List<String> newPriorSteps = new LinkedList<String>(priorSteps);
		newPriorSteps.add("" + inputs.get(i) + " " + op.getSymbol() + " "
				+ inputs.get(j) + " = " + intCalcResult);
		List<Integer> newInputs = new LinkedList<Integer>(inputs);
		newInputs.set(i, intCalcResult);
		newInputs.remove(j);
		return new CalculationState(newInputs, newPriorSteps);
	}

	public int getClosestNumber(int outputNumber) {
		int minDistance = Integer.MAX_VALUE;
		int closestNumber = 0;
		for (int input : inputs) {
			int distance = Math.abs(input - outputNumber);
			if (distance < minDistance) {
				minDistance = distance;
				closestNumber = input;
			}
		}
		return closestNumber;
	}
}
