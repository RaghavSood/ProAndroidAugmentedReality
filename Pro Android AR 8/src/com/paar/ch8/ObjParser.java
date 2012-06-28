package com.paar.ch8;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ObjParser {
	private final int VERTEX_DIMENSIONS = 3;
	private final int TEXTURE_COORD_DIMENSIONS = 2;
	
	private BaseFileUtil fileUtil;
	
	public ObjParser(BaseFileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}
	
	public Model parse(String modelName, BufferedReader is) throws IOException, ParseException {
		ArrayList<float[]> vertices = new ArrayList<float[]>(1000);
		ArrayList<float[]> normals = new ArrayList<float[]>(1000);
		ArrayList<float[]> texcoords = new ArrayList<float[]>();
		
		
		Model model = new Model();
		Group curGroup = new Group();
		MtlParser mtlParser = new MtlParser(model,fileUtil);
		SimpleTokenizer spaceTokenizer = new SimpleTokenizer();
		SimpleTokenizer slashTokenizer = new SimpleTokenizer();
		slashTokenizer.setDelimiter("/");
		
		String line;
		int lineNum = 1;
		for (line = is.readLine(); 
		line != null; 
		line = is.readLine(), lineNum++)
		{
			if (line.length() > 0) {
				if (line.startsWith("#")) {
				} else if (line.startsWith("v ")) {
					String endOfLine = line.substring(2);
					spaceTokenizer.setStr(endOfLine);
					vertices.add(new float[]{
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next())});
				}
				else if (line.startsWith("vt ")) {
					String endOfLine = line.substring(3);
					spaceTokenizer.setStr(endOfLine);
					texcoords.add(new float[]{
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next())});
				}
				else if (line.startsWith("f ")) {
					String endOfLine = line.substring(2);
					spaceTokenizer.setStr(endOfLine);
					int faces = spaceTokenizer.delimOccurCount()+1;
					if(faces != 3) {
						throw new ParseException(modelName,
								lineNum, "only triangle faces are supported");
					}
					for (int i = 0; i < 3; i++) {
						String face = spaceTokenizer.next();
						slashTokenizer.setStr(face);
						int vertexCount = slashTokenizer.delimOccurCount()+1;
						int vertexID=0;
						int textureID=-1;
						int normalID=0;
						if(vertexCount == 2) {
							vertexID = Integer.parseInt(slashTokenizer.next())-1;
							normalID = Integer.parseInt(slashTokenizer.next())-1;
							throw new ParseException(modelName,
									lineNum,
									"vertex normal needed.");
						} else if(vertexCount == 3) {
							vertexID = Integer.parseInt(slashTokenizer.next())-1;
							String texCoord = slashTokenizer.next();
							if(!texCoord.equals("")) {
								textureID = Integer.parseInt(texCoord)-1;
							}
							normalID = Integer.parseInt(slashTokenizer.next())-1;
						} else {
							throw new ParseException(modelName,
									lineNum,
									"a faces needs reference a vertex, a normal vertex and optionally a texture coordinate per vertex.");
						}
						float[] vec;
						try {
							vec = vertices.get(vertexID);
						} catch (IndexOutOfBoundsException ex) {
							throw new ParseException(modelName,
									lineNum,
									"non existing vertex referenced.");
						}
						if(vec==null)
							throw new ParseException(modelName,
									lineNum,
									"non existing vertex referenced.");
						for (int j = 0; j < VERTEX_DIMENSIONS; j++)
							curGroup.groupVertices.add(vec[j]);
						if(textureID != -1) {
							try {
								vec = texcoords.get(textureID);
							} catch (IndexOutOfBoundsException ex) {
								throw new ParseException(modelName,
										lineNum,
										"non existing texture coord referenced.");
							}
							if(vec==null)
								throw new ParseException(modelName,
										lineNum,
										"non existing texture coordinate referenced.");
							for (int j = 0; j < TEXTURE_COORD_DIMENSIONS; j++)
								curGroup.groupTexcoords.add(vec[j]);
						}
						try {
							vec = normals.get(normalID);
						} catch (IndexOutOfBoundsException ex) {
							throw new ParseException(modelName,
									lineNum,
									"non existing normal vertex referenced.");
						}
						if(vec==null)
							throw new ParseException(modelName,
									lineNum,
									"non existing normal vertex referenced.");
						for (int j = 0; j < VERTEX_DIMENSIONS; j++)
							curGroup.groupNormals.add(vec[j]);
					}
				}
				else if (line.startsWith("vn ")) {
					String endOfLine = line.substring(3);
					spaceTokenizer.setStr(endOfLine);
					normals.add(new float[]{
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next())});
				} else if (line.startsWith("mtllib ")) {
					String filename = line.substring(7);
					String[] files = Util.splitBySpace(filename);
					for (int i = 0; i < files.length; i++) {
						BufferedReader mtlFile = fileUtil.getReaderFromName(files[i]);
						if(mtlFile != null)
							mtlParser.parse(model, mtlFile);
					}					
				} else if(line.startsWith("usemtl ")) {
					if(curGroup.groupVertices.size()>0) {
						model.addGroup(curGroup);
						curGroup = new Group();
					}
					curGroup.setMaterialName(line.substring(7));
				} else if(line.startsWith("g ")) {
					if(curGroup.groupVertices.size()>0) {
						model.addGroup(curGroup);
						curGroup = new Group();
					}
				}
			}
		}
		if(curGroup.groupVertices.size()>0) {
			model.addGroup(curGroup);
		}
		Iterator<Group> groupIt = model.getGroups().iterator();
		while (groupIt.hasNext()) {
			Group group = (Group) groupIt.next();
			group.setMaterial(model.getMaterial(group.getMaterialName()));
		}
		return model;
	}
}