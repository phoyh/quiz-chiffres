package org.chiffres;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import android.content.Context;

public class Calculator {

	List<Integer> inputNumbers;
	int outputNumber;
	List<Operator> operators;
	
	public Calculator(List<Integer> inputNumbers, int outputNumber,
			boolean isComplexOpAllowed) {
		this.inputNumbers = inputNumbers;
		this.outputNumber = outputNumber;
		operators = new ArrayList<Operator>();
		operators.add(new Operator() {
			@Override
			public boolean isCommutative() {
				return true;
			}
			@Override
			public double calculate(int a, int b) {
				return a + b;
			}
			@Override
			public String getSymbol() {
				return "+";
			}
		});
		operators.add(new Operator() {
			@Override
			public boolean isCommutative() {
				return false;
			}
			@Override
			public double calculate(int a, int b) {
				return a - b;
			}
			@Override
			public String getSymbol() {
				return "-";
			}
		});
		operators.add(new Operator() {
			@Override
			public boolean isCommutative() {
				return true;
			}
			@Override
			public double calculate(int a, int b) {
				return a * b;
			}
			@Override
			public String getSymbol() {
				return "*";
			}
		});
		operators.add(new Operator() {
			@Override
			public boolean isCommutative() {
				return false;
			}
			@Override
			public double calculate(int a, int b) {
				if (a < b) return 0;
				return (double)a / b;
			}
			@Override
			public String getSymbol() {
				return "/";
			}
		});
		if (isComplexOpAllowed) {
			operators.add(new Operator() {
				@Override
				public boolean isCommutative() {
					return false;
				}
				@Override
				public double calculate(int a, int b) {
					return Math.pow(a, b);
				}
				@Override
				public String getSymbol() {
					return "^";
				}
			});
			operators.add(new Operator() {
				@Override
				public boolean isCommutative() {
					return false;
				}
				@Override
				public double calculate(int a, int b) {
					if (b <= a) return 0;
					return Math.pow(b, 1.0 / a);
				}
				@Override
				public String getSymbol() {
					return "V";
				}
			});
			operators.add(new Operator() {
				@Override
				public boolean isCommutative() {
					return false;
				}
				@Override
				public double calculate(int a, int b) {
					if (b < a || a == 1) return 0;
					return Math.log(b)/Math.log(a);
				}
				@Override
				public String getSymbol() {
					return "log";
				}
			});
		}
	}

	public List<String> getResult(Context context) {
		Queue<CalculationState> stateQueue = new LinkedList<CalculationState>();
		Set<String> foundInputSets = new HashSet<String>();
		
		CalculationState startState = new CalculationState(inputNumbers,
				new LinkedList<String>());
		if (startState.contains(outputNumber)) {
			List<String> result = new LinkedList<String>();
			result.add(context.getString(R.string.already_in_input, outputNumber));
			return result;
		}
		stateQueue.add(startState);
		foundInputSets.add(startState.getInputsString());
		int closestNumber = startState.getClosestNumber(outputNumber);
		int closestDistance = Math.abs(closestNumber - outputNumber);
		List<String> closestPriorSteps = startState.getPriorSteps();
		
		while (!stateQueue.isEmpty()) {
			CalculationState currentState = stateQueue.poll();
			for (CalculationState successorState : currentState.getSuccessors(operators)) {
				if (successorState.contains(outputNumber)) {
					return successorState.getPriorSteps();
				}
				if (foundInputSets.add(successorState.getInputsString())) {
					int successorClosestNumber = successorState
							.getClosestNumber(outputNumber);
					int successorClosestDistance = Math
							.abs(successorClosestNumber - outputNumber);
					if (successorClosestDistance < closestDistance) {
						closestDistance = successorClosestDistance;
						closestNumber = successorClosestNumber;
						closestPriorSteps = successorState.getPriorSteps();
					}
					if (successorState.getInputsNumber() > 1) {
						stateQueue.add(successorState);
					}
				}
			}
		}
		closestPriorSteps.add(context.getString(R.string.only_close_to_target,
				closestNumber));
		return closestPriorSteps;
	}
}
