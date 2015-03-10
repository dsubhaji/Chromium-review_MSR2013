package edu.bitsgoa.logmining.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

public class FileUtility {

	private final FileFilter _dirFilter;
	private SpecificFileFilter _fileFilter;
/**
 * 
 * @author denis_jose
 *Utility Class for Filtering filenames based on required File Extensions
 */
	class SpecificFileFilter implements FilenameFilter {
		private String[] _extns;

		public SpecificFileFilter() {
			_extns = null;
		}

		public void setFileExtns(String[] ext) {
			_extns = ext;
		}

		public boolean accept(File dir, String name) {
			if(_extns == null) {
				return true;
			}
			else {
				for (String ext : _extns) {
					if (name.endsWith("." + ext) == true)
						return true;
				}
				return false;
			}
		}
	}

	/**
	 * 
	 * @author denis_jose
	 *Checks if input File is a Directory
	 */
	class DirectoryFilter implements FileFilter {
		public boolean accept(File f) {
			return f.isDirectory() == true ? true : false;
		}
	}

	public FileUtility() {
		_dirFilter = new DirectoryFilter();
		_fileFilter = new SpecificFileFilter();
	}

	/**
	 * This method recurses through a directory to get a list of files
	 * 
	 * @param dirName
	 * @param shouldRecurse
	 * @param fileNames
	 */
	public void setFileExtns(String[] ext) {
		_fileFilter.setFileExtns(ext);
	}
/**
 * Gets all files in the directory location provided
 * @param dirName = Directory where files are searched
 * @param shouldRecurse = should inner directories be searched recursively
 * @param fileNames = filenames extracted from directory
 */
	public void getFilesWithExtn(String dirName, boolean shouldRecurse,
			HashSet<String> fileNames) {
		File dir = new File(dirName);
		File[] srcFiles = dir.listFiles(_fileFilter);
		File[] subDirs = dir.listFiles(_dirFilter);
		if (srcFiles != null) {
			for (File aFile : srcFiles) {
				String fName = aFile.getAbsolutePath();
				// String truncatedNm = fName.substring(_workspace.length());
				fileNames.add(fName);
			}
		}
		if (subDirs != null && shouldRecurse == true) {
			for (File aDir : subDirs) {
				getFilesWithExtn(new String(aDir.getAbsolutePath()), true,
						fileNames);
			}
		}
	}

/*	*//**
	 * @param dirName 
	 * @param shouldRecurse 
	 * @param dirs = 
	 * @param withFiles 
	 *//*
	public void getSubdirectories(String dirName, HashSet<String> dirs,
			boolean withFiles, boolean shouldRecurse) {
		File dir = new File(dirName);
		File[] subDirs = dir.listFiles(_dirFilter);
		File[] srcFiles = dir.listFiles(_fileFilter);
		if (srcFiles != null) {
			if (srcFiles.length > 0 || withFiles == false) {
				String name = new String(dir.getAbsolutePath());
				dirs.add(name);
			}
		}
		if (subDirs != null && shouldRecurse == true) {
			for (File aDir : subDirs) {
				getSubdirectories(aDir.getAbsolutePath(), dirs, withFiles,
						shouldRecurse);
			}
		}
	}

*/	
	/**
	 * 
	 * @param dirName= Directory where files are searched
	 * @param dirs=directory names extracted from given directory
	 * @param shortNameOnly= boolean value to specify if you need a short name of the immediate subdirectory. Is overridden when subdirs are recursively retrieved.
	 * @param shouldRecurse= should inner directories be searched recursively
	 */
	public void getSubdirectories(String dirName, HashSet<String> dirs,
			boolean shortNameOnly, boolean shouldRecurse) {
		File dir = new File(dirName);
		File[] subDirs = dir.listFiles(
				new FileFilter() {
					public boolean accept(File f) {
						return f.isDirectory() == true ? true : false;
					}
				});
		boolean getFullPath = (shouldRecurse==true)?true:!shortNameOnly;
		if (subDirs != null) {
			for (File aDir : subDirs) {
				if (shouldRecurse)
					getSubdirectories(aDir.getAbsolutePath(), dirs, shortNameOnly,
						shouldRecurse);
				String name= (getFullPath==true)?aDir.getAbsolutePath():aDir.getName();
				dirs.add(name);
			}
		}
	}
	
	/**
	 * deletes files from given directory which match the file filter 
	  * @param dirName = Directory where files are searched
	  * @param shouldRecurse = should inner directories be searched recursively
	 */
	public void deleteFilesWithExtn(String dirName, boolean shouldRecurse) {
		File dir = new File(dirName);
		File[] srcFiles = dir.listFiles(_fileFilter);
		File[] subDirs = dir.listFiles(_dirFilter);
		if (srcFiles != null) {
			for (File aFile : srcFiles) {
				aFile.delete();
			}
		}
		if (subDirs != null && shouldRecurse == true) {
			for (File aDir : subDirs) {
				deleteFilesWithExtn(new String(aDir.getAbsolutePath()), true);
			}
		}
	}
/**
 * creates a  temporary file name ICIRRUSTMP
 * @return
 * @throws IOException
 */
	public static File generateTempFile() throws IOException {
		File f = null;
		String prefix = "ICIRRUSTMP";
		f = File.createTempFile(prefix, null);
		f.deleteOnExit();
		return f;
	}
/**
 * returns the directory name from canonical path
 * @param fname =input filename
 * @return
 */
	public static String[] getDirNamesFromFile(String fname) {
		String name = fname.replace(File.separatorChar, '#');
		String[] names = name.split("#");
		names[names.length - 1] = names[names.length - 1].split("\\.")[0];
		return names;
	}
	
	/**
	 * Following two methods are added by Gaurav Rawat on 26th March 2013 for file archiving operations
	 * 
	 */
	public void copyDirectory(String inputFileLocation,
			String outputFileLocation) throws IOException {
		File sourceLocation = new File(inputFileLocation);
		File targetLocation = new File(outputFileLocation);
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(inputFileLocation + "/" + children[i],
						outputFileLocation + "/" + children[i]);
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static void delete(File file) throws IOException {
		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);
					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			// if file, then delete it
			file.delete();
		}
	}

}
