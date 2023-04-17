package br.com.erudio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.exception.UnsupportedMathOperationException;
import br.com.erudio.operations.MathOperations;
import br.com.erudio.util.Util;

@RestController
public class MathController {
	
	@GetMapping("/sum/{numberOne}/{numberTwo}")
	public Double sum(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) {
		if (!Util.isNumeric(numberOne) || !Util.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		Double numberOneDouble = Util.convertToDouble(numberOne);
		Double numberTwoDouble = Util.convertToDouble(numberTwo);
		
		return MathOperations.sum(numberOneDouble, numberTwoDouble);
	}
	
	@GetMapping("/sub/{numberOne}/{numberTwo}")
	public Double sub(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) {
		if (!Util.isNumeric(numberOne) || !Util.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		Double numberOneDouble = Util.convertToDouble(numberOne);
		Double numberTwoDouble = Util.convertToDouble(numberTwo);
		
		return MathOperations.subtraction(numberOneDouble, numberTwoDouble);
	}
	
	@GetMapping("/mul/{numberOne}/{numberTwo}")
	public Double mul(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) {
		if (!Util.isNumeric(numberOne) || !Util.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		Double numberOneDouble = Util.convertToDouble(numberOne);
		Double numberTwoDouble = Util.convertToDouble(numberTwo);
		
		return MathOperations.multiplication(numberOneDouble, numberTwoDouble);
	}
	
	@GetMapping("/div/{numberOne}/{numberTwo}")
	public Double div(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) {
		if (!Util.isNumeric(numberOne) || !Util.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		Double numberOneDouble = Util.convertToDouble(numberOne);
		Double numberTwoDouble = Util.convertToDouble(numberTwo);
		
		return MathOperations.division(numberOneDouble, numberTwoDouble);
	}
	
	@GetMapping("/ave/{numberOne}/{numberTwo}")
	public Double ave(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) {
		if (!Util.isNumeric(numberOne) || !Util.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		Double numberOneDouble = Util.convertToDouble(numberOne);
		Double numberTwoDouble = Util.convertToDouble(numberTwo);
		
		return MathOperations.average(numberOneDouble, numberTwoDouble);
	}
	
	@GetMapping("/sqrt/{number}")
	public Double sqrt(@PathVariable(value = "number") String number) {
		if (!Util.isNumeric(number)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		Double numberDouble = Util.convertToDouble(number);
		
		return MathOperations.sqrt(numberDouble);
	}
}
