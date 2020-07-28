package com.betfair.aping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class OddsUtil {

	public static Double LUCRO_PERCENTUAL = 2d;
	public static Double STAKE = 50d;
	
	
	public static void main(String[] args) {
		Double odd1 = 1.82;
		Double odd2 = 2.4;
		
		
		calculaSureBet(odd1, odd2);
	}

	public static double calculaSureBet(Double odd1, Double odd2) {
		Double stake1 = round(STAKE-((odd1*STAKE)/(odd1+odd2)), 2);
		Double stake2 = round(STAKE-((odd2*STAKE)/(odd1+odd2)), 2);
		Double percentual = (stake1*odd1)-STAKE;
		System.out.println(stake1);
		System.out.println(stake2);
//		Double percentualFinal = percentual / 100;
		System.out.println(percentual);
//		System.out.println(percentualFinal.intValue());
		 DecimalFormat decimalFormat = new DecimalFormat("0.##");
		System.out.println(decimalFormat.format(percentual));
//		if (percentualFinal > 0) {
//			System.out.println("********************** ACHOU");
//		}
		
		return percentual.doubleValue();

	}
	
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	private static void calculaSureBet2(Double odd1, Double odd2) {
		double montante = 50;
		Double odd2Calculada =  1/((((1/odd1)*montante)/(montante/odd1))-(1/odd1));
		System.out.println(odd2Calculada);
	}

}
