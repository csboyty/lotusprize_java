package com.zhongyi.lotusprize.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Strings;


public class LotusprizeLocalFiles {
	
	private final static LotusprizeLocalFiles _instance = new LotusprizeLocalFiles();
	
	
	
	private final String _localStoragePath;
	
	private final String localStorageHttpRootPath;
	
	private final String _tempStoragePath;
	
	private final String tempStorageHttpRootPath;
	
	private final String _pdfLocalStoragePath;
	
	private final String pdfLocalStorageHttpRootPath;
	
	private final File localStorageDir;
	
	private final File tempStorageDir;
	
	private final File pdfLocalStorageDir;
	
	private LotusprizeLocalFiles(){
		_tempStoragePath = ApplicationProperty.instance().getAsString("storage.temp.path");
		tempStorageDir = new File(_tempStoragePath);
		if(!tempStorageDir.exists())tempStorageDir.mkdirs();
		tempStorageHttpRootPath = ApplicationProperty.instance().getAsString("storage.temp.http_root_path");
		
		_localStoragePath = ApplicationProperty.instance().getAsString("storage.local.path");
		localStorageDir = new File(_localStoragePath);
		if(!localStorageDir.exists())localStorageDir.mkdirs();
		localStorageHttpRootPath = ApplicationProperty.instance().getAsString("storage.local.http_root_path");
		
		_pdfLocalStoragePath =  ApplicationProperty.instance().getAsString("storage.local.path.pdf");
		pdfLocalStorageDir = new File(_pdfLocalStoragePath);
		if(!pdfLocalStorageDir.exists())pdfLocalStorageDir.mkdirs();
		pdfLocalStorageHttpRootPath = ApplicationProperty.instance().getAsString("storage.local.http_root_path.pdf");
	}
	
	public File localStorageDir(){
		return localStorageDir;
	}
	
	public File tempStorageDir(){
		return tempStorageDir;
	}
	
	public File pdfLocalStorageDir(){
	    return pdfLocalStorageDir;
	}
	
	public File userStorageDir(Integer userId){
		if(userId == null){
			throw new IllegalStateException("userId 不能为 NULL");
		}
		File userStorageDir =  new File(localStorageDir,String.valueOf(userId));
		if(!userStorageDir.exists()){
			userStorageDir.mkdirs();
		}
		return userStorageDir;
	}
	
	public File tempFile(String filename){
		return new File(tempStorageDir,filename);
	}
	
	public String tempHttpPath(File tempFile){
		String path = tempStorageHttpRootPath+"/"+
				FilenameUtils.normalize(tempFile.getAbsolutePath().substring(_tempStoragePath.length()+1),true);
		return path;
	}
	
	public String localFilePath(String userPartPath){
	    return _localStoragePath+"/"+userPartPath;
	}
	
	public String localHttpPath(String userPartPath){
		return localStorageHttpRootPath+"/"+userPartPath;
	}
	
	public String localHttpPath(Integer userId,String filename){
		return localStorageHttpRootPath+"/"+userId+"/"+filename;
	}
	
	public String pdfHttpPath(String pdfFilename){
	    return pdfLocalStorageHttpRootPath+"/"+pdfFilename;
	}
	
	public String pdfFilePath(String pdfFilename){
	    return _pdfLocalStoragePath+"/"+pdfFilename;
	}
	
	public String localHttpPath(File localFile){
		String path=localStorageHttpRootPath+"/"+
				FilenameUtils.normalize(localFile.getAbsolutePath().substring(_localStoragePath.length()+1),true);
		return path;
	}
	
	public String userPartPath(String localHttpPath){
		return localHttpPath.substring(localStorageHttpRootPath.length());
	}
	
	public File toLocalFile(String httpFilePath){
		return new File(_localStoragePath+"/"+httpFilePath.substring(localStorageHttpRootPath.length()+1));
	}
	
	public boolean isLocalFile(String httpFilePath){
		if(Strings.isNullOrEmpty(httpFilePath))return false;
		if(httpFilePath.startsWith(localStorageHttpRootPath))return true;
		return false;
	}
	
	public File toTempFile(String httpFilePath){
		return new File(_tempStoragePath +"/"+httpFilePath.substring(tempStorageHttpRootPath.length()+1));
	}
	
	public boolean isTempFile(String httpFilePath){
		if(Strings.isNullOrEmpty(httpFilePath))return false;
		if(httpFilePath.startsWith(tempStorageHttpRootPath))return true;
		return false;
	}
	
	public String tempHttpPathToLocalHttpPath(String temphttpPath,Integer userId) throws IOException{
		File tempFile = toTempFile(temphttpPath);
		File userDir = userStorageDir(userId);
		File localFile = new File(userDir,tempFile.getName());
		FileUtils.copyFile(tempFile, localFile);
		return localHttpPath(localFile);
	}
	
	public void deletePdfByArtifactId(Integer artifactId){
	    File artifactPdfDir = new File(pdfLocalStorageDir,artifactId.toString());
	    if(artifactPdfDir.exists()){
	        FileUtils.deleteQuietly(artifactPdfDir);
	    }
	}
	
	public static LotusprizeLocalFiles instance(){
		return _instance;
	}
	
}
