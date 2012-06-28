package com.paar.ch8;

import java.io.BufferedReader;
import java.io.IOException;


public class MtlParser { 

	private BaseFileUtil fileUtil;
	
	public MtlParser(Model model, BaseFileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}

	public void parse(Model model, BufferedReader is) {
		Material curMat = null;
		int lineNum = 1;
		String line;
		try {
			for (line = is.readLine(); 
			line != null; 
			line = is.readLine(), lineNum++)
			{
				line = Util.getCanonicalLine(line).trim();
				if (line.length() > 0) {
					if (line.startsWith("newmtl ")) {
						String mtlName = line.substring(7);
						curMat = new Material(mtlName);
						model.addMaterial(curMat);
					} else if(curMat == null) {
					} else if (line.startsWith("# ")) {
					} else if (line.startsWith("Ka ")) {
						String endOfLine = line.substring(3);
						curMat.setAmbient(parseTriple(endOfLine));
					} else if (line.startsWith("Kd ")) {
						String endOfLine = line.substring(3);
						curMat.setDiffuse(parseTriple(endOfLine));
					} else if (line.startsWith("Ks ")) {
						String endOfLine = line.substring(3);
						curMat.setSpecular(parseTriple(endOfLine));
					} else if (line.startsWith("Ns ")) {
						String endOfLine = line.substring(3);
						curMat.setShininess(Float.parseFloat(endOfLine));
					} else if (line.startsWith("Tr ")) {
						String endOfLine = line.substring(3);
						curMat.setAlpha(Float.parseFloat(endOfLine));
					} else if (line.startsWith("d ")) {
						String endOfLine = line.substring(2);
						curMat.setAlpha(Float.parseFloat(endOfLine));
					} else if(line.startsWith("map_Kd ")) {
						String imageFileName = line.substring(7);
						curMat.setFileUtil(fileUtil);
						curMat.setBitmapFileName(imageFileName);
					} else if(line.startsWith("mapKd ")) {
						String imageFileName = line.substring(6);
						curMat.setFileUtil(fileUtil);
						curMat.setBitmapFileName(imageFileName);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static float[] parseTriple(String str) {
		String[] colorVals = str.split(" ");
		float[] colorArr = new float[]{
				Float.parseFloat(colorVals[0]),
				Float.parseFloat(colorVals[1]),
				Float.parseFloat(colorVals[2])};
		return colorArr;
		
	}
	
}