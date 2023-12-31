package com.mobigen.snet.NeAgent.utils;


import com.sk.snet.manipulates.PatternMaker;

public class PatternBuilder {

	public PatternMaker patternMaker;

	public PatternBuilder(){
		patternMaker = new PatternMaker();
	}


	public static String getTypeVal(){
		String retStr = "";

		String[] VARIDX = {"A","B","D","Q","S","R","C","E","T","F"};


		String mon = DateUtil.getMonth(DateUtil.getCurrDateByHour());

		int monInt = Integer.parseInt(mon);

		int monIntMod = monInt%10;

		System.out.println(monInt+" "+ monIntMod +" "+VARIDX[monIntMod]);

		retStr = VARIDX[monIntMod];

		return retStr;
	}

	public static String getKSPatterns(String type){
		String retStr = "";

		if(type.equals("A"))
			retStr = PatternMaker.KS_PMANNERA;

		else if(type.equals("B"))
			retStr = PatternMaker.KS_PMANNERB;

		else if(type.equals("D"))
			retStr = PatternMaker.KS_PMANNERDG;

		else if(type.equals("E"))
			retStr = PatternMaker.KS_PMANNERET;

		else if(type.equals("S"))
			retStr = PatternMaker.KS_PMANNERBS;

		else
			retStr = PatternMaker.KS_PMANNER;


		return retStr;
	}


	public static String getEncPatterns(String type){
		String retStr = "";

		if(type.equals("R"))
			retStr = PatternMaker.ENCRPTION_PMANNERR;

		else if(type.equals("A"))
			retStr = PatternMaker.ENCRPTION_PMANNERA;

		else if(type.equals("B"))
			retStr = PatternMaker.ENCRPTION_PMANNERB;

		else if(type.equals("D"))
			retStr = PatternMaker.ENCRPTION_PMANNERD;

		else if(type.equals("C"))
			retStr = PatternMaker.ENCRPTION_PMANNERC;

		else if(type.equals("Q"))
			retStr = PatternMaker.ENCRPTION_PMANNERQ;

		else
			retStr = PatternMaker.ENCRPTION_PMANNER;


		return retStr;
	}

	public static String getDecPatterns(String type){
		String retStr = "";

		if(type.equals("T"))
			retStr = PatternMaker.DEC_PMANNERTU;

		else if(type.equals("A"))
			retStr = PatternMaker.DEC_PMANNERAS;

		else if(type.equals("F"))
			retStr = PatternMaker.DEC_PMANNERDF;

		else if(type.equals("D"))
			retStr = PatternMaker.DEC_PMANNERDC;

		else if(type.equals("C"))
			retStr = PatternMaker.DEC_PMANNERDC;

		else
			retStr = PatternMaker.DEC_PMANNER;

		return retStr;
	}


	public static void main(String[] args){
		String argVar0 = "";
		String argVar1 = "";



		if(args.length == 1){
		    argVar0 = args[0];
		    argVar1 = args[1];
		 System.out.println("argVar0 :"+argVar0+", argVar1:"+argVar1);
		}

		PatternBuilder pb = new PatternBuilder();

		getTypeVal();

		System.out.println("KS Pattern "+argVar1+": "+ getKSPatterns(argVar1));

		System.out.println("KS Pattern A: "+ getKSPatterns("A"));
		System.out.println("KS Pattern B: "+ getKSPatterns("B"));
		System.out.println("KS Pattern E: "+ getKSPatterns("E"));
		System.out.println("KS Pattern D: "+ getKSPatterns("S"));
		System.out.println("KS Pattern !: "+ getKSPatterns("!"));


		System.out.println("Enc Pattern "+argVar1+": "+ getEncPatterns(argVar1));
		System.out.println("Enc Pattern A: "+ getEncPatterns("A"));
		System.out.println("Enc Pattern B: "+ getEncPatterns("B"));
		System.out.println("Enc Pattern C: "+ getEncPatterns("C"));
		System.out.println("Enc Pattern D: "+ getEncPatterns("D"));
		System.out.println("Enc Pattern Q: "+ getEncPatterns("Q"));


		System.out.println("Dec Pattern "+argVar1+": "+ getDecPatterns(argVar1));
		System.out.println("Dec Pattern T: "+ getDecPatterns("T"));
		System.out.println("Dec Pattern A: "+ getDecPatterns("A"));
		System.out.println("Dec Pattern C: "+ getDecPatterns("C"));
		System.out.println("Dec Pattern D: "+ getDecPatterns("D"));
		System.out.println("Dec Pattern F: "+ getDecPatterns("F"));



	}



}