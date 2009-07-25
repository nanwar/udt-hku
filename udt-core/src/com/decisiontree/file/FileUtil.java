package com.decisiontree.file;

import java.io.File;
import java.io.IOException;

public class FileUtil {

	public static void createFileWithDirectory(String fileName) throws IOException{
		File file = new File(fileName);
		if(!file.exists()){
			if(file.getParent() != null){
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
	}
}
