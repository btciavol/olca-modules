package org.openlca.core.matrix;

import org.openlca.expressions.FormulaInterpreter;
import org.openlca.util.Strings;
import org.slf4j.LoggerFactory;

public class CalcAllocationFactor {

	private double amount;
	private boolean evaluated;
	private String formula;

	private CalcAllocationFactor() {
	}

	public static CalcAllocationFactor of(double amount) {
		return of(null, amount);
	}

	public static CalcAllocationFactor of(String formula, double amount) {
		var factor = new CalcAllocationFactor();
		factor.amount = amount;
		if (Strings.nullOrEmpty(formula)) {
			factor.evaluated = true;
		} else {
			factor.formula = formula;
			factor.evaluated = false;
		}
		return factor;
	}

	/**
	 * Get the value of the allocation factor. If no formula is bound to the factor,
	 * simply the value of the factor is returned. Otherwise, the value of the
	 * evaluated formula is returned. The formula is only evaluated once and the
	 * value of that evaluation is cached when calling this method.
	 */
	public double get(FormulaInterpreter interpreter) {
		if (evaluated)
			return amount;
		if (interpreter == null)
			return amount;
		try {
			amount = interpreter.eval(formula);
		} catch (Exception e) {
			var log = LoggerFactory.getLogger(getClass());
			log.error("failed to evaluate formula of allocation factor: "
					+ formula);
		}
		evaluated = true;
		return amount;
	}

	/**
	 * If the allocation factor is bound to a formula, this formula is always
	 * evaluated. No cached value is returned in contrast to the `get` method.
	 */
	public double force(FormulaInterpreter interpreter) {
		if (formula == null)
			return amount;
		evaluated = false;
		return get(interpreter);
	}
}
