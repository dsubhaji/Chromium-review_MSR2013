package edu.bitsgoa.logmining.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	 private static final int BUFFER_SIZE = 4096;  
     
	   public void compressFiles(String reportLocation, String destZipFile) throws FileNotFoundException, IOException {  
	          File reportFolder = new File(reportLocation);
	          List<File> reportFiles = new ArrayList<File>(1);
	          reportFiles.add(reportFolder);
	       ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));  
	          
	       for (File file : reportFiles) {  
	           if (file.isDirectory()) {  
	               addFolderToZip(file, file.getName(), zos);  
	           } else {  
	               addFileToZip(file, zos);  
	           }  
	       }  
	          
	       zos.flush();  
	       zos.close();  
	       
	   }  
	      
	    private void addFolderToZip(File folder, String parentFolder,  
	            ZipOutputStream zos) throws FileNotFoundException, IOException {  
	        for (File file : folder.listFiles()) {  
	            if (file.isDirectory()) {  
	                addFolderToZip(file, parentFolder + "/" + file.getName(), zos);  
	                continue;  
	            }  
	   
	            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));  
	   
	            BufferedInputStream bis = new BufferedInputStream(  
	                    new FileInputStream(file));  
	   
	            long bytesRead = 0;  
	            byte[] bytesIn = new byte[BUFFER_SIZE];  
	            int read = 0;  
	   
	            while ((read = bis.read(bytesIn)) != -1) {  
	                zos.write(bytesIn, 0, read);  
	                bytesRead += read;  
	            }  
	   
	            zos.closeEntry();  
	   
	        }  
	    }  
	   
	    private void addFileToZip(File file, ZipOutputStream zos)  
	            throws FileNotFoundException, IOException {  
	        zos.putNextEntry(new ZipEntry(file.getName()));  
	   
	        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(  
	                file));  
	   
	        long bytesRead = 0;  
	        byte[] bytesIn = new byte[BUFFER_SIZE];  
	        int read = 0;  
	   
	        while ((read = bis.read(bytesIn)) != -1) {  
	            zos.write(bytesIn, 0, read);  
	            bytesRead += read;  
	        }  
	   
	        zos.closeEntry();  
	    }  
}
