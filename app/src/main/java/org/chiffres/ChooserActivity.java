package org.chiffres;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChooserActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chiffres);
		setTitle(R.string.title_chiffres);
		
		findViewById(R.id.calculation_start).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getCalculatingProgressBar().setVisibility(View.VISIBLE);
				new Thread() {
					@Override
					public void run() {
						calculate();
					}
				}.start();
			}
		});
	}
	
	private ProgressBar getCalculatingProgressBar() {
		return (ProgressBar) findViewById(R.id.calculating_progress);
	}
	
	private void calculate() {
		List<Integer> inputNumbers = getInputNumber();
		int outputNumber = getOutputNumber();
		boolean isComplexOpAllowed = isComplexOpAllowed();
		
		String result = getString(R.string.wrong_input);
		if (inputNumbers.size() != 0 && outputNumber != 0) {
			result = getString(R.string.no_solution_found);
			List<String> calcResult = new Calculator(inputNumbers,
					outputNumber, isComplexOpAllowed).getResult(this);
			if (calcResult != null && calcResult.size() != 0) {
				result = "";
				for (String resultLine : calcResult) {
					if (!result.equals("")) result += "\n";
					result += resultLine;
				}
			}
		}
		final String closureResult = result;
		runOnUiThread(new Runnable() {
		    public void run() {
		    	displayResult(closureResult);
		    }
		});
	}
	
	private List<Integer> getInputNumber() {
		List<Integer> numberList = new LinkedList<Integer>();
		String input = ((EditText) findViewById(R.id.option_input_numbers))
				.getText().toString();
		if (input == null) return numberList;
		for (String component : input.split(" ")) {
			try {
				numberList.add(Integer.parseInt(component));
			} catch (NumberFormatException e) {}
		}
		return numberList;
	}
	
	private int getOutputNumber() {
		String input = ((EditText) findViewById(R.id.option_output_number)).getText().toString();
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	private boolean isComplexOpAllowed() {
		return ((CheckBox) findViewById(R.id.option_complex_ops)).isChecked();
	}
	
	private void displayResult(String result) {
		getCalculatingProgressBar().setVisibility(View.GONE);
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(null)
				.setMessage(result)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = alertBuilder.create();
		alert.show();
		TextView messageTextView = (TextView) alert.findViewById(android.R.id.message);
		messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
	}

}
