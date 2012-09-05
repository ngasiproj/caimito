/*
 Copyright (c) 2011-2012, the Caimito project (http://caimito.ngasi.com/). All rights reserved.
Apache Software License 2.0
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL WebAppShowCase
OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/




package ngasi.caimito.resource;
import ngasi.caimito.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import tools.util.FileUtil;
public abstract class CloudResourceObj extends ResourceObj
    {
	static String rpath = null;
	static String arpath = null;
	public String store = null;
	static{
		if (CaimitoConfig.cloud_setup==CaimitoConfig.cloud_accounts_for_administration_only){
			rpath = "";
			arpath = "";
		}
		else
			try {
				rpath = "/" + CaimitoConfig.getConfig().getString("cloud.store");
				arpath = "/" + CaimitoConfig.getConfig().getString("cloud.store");
			} catch (CaimitoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
    	public InputStream doGetInputStream()throws CaimitoException{
    		try{
		String tf = CaimitoConfig.cachedir ;
		if (CaimitoConfig.cloud_setup==CaimitoConfig.cloud_accounts_for_administration_only)
			tf = tf + "/" + CaimitoUtil.normalize(user) + "/";	
		tf = tf + path;

		
			if (CaimitoConfig.cacheable){
				File ftf = new File(tf);
				if (ftf.exists() && ftf.lastModified() == getLastModified())
				{
					//("CACHE obje " + tf);
					return new FileInputStream(tf);
				}
				//else
						//(ftf.exists() + ":" + tf + ":" + new Date(ftf.lastModified()) + " NO CACHE obje " + new Date(getLastModified()) );
				

			}
			//StringUtil.replaceSubstring(path,"/" , "_");
			CacheMgr cmr = CacheMgr.getCacheMgr();
			boolean rl = false;
			if (cmr.getBoolean(tf))
				cmr.waitFor(tf);
			else{
			
				cmr.put(tf);
				rl = true;
			}
			
			try{
			
    		new File(tf).delete();
    		int i = tf.lastIndexOf("/");
    		 new File (tf.substring(0,i)).mkdirs();

			retrieveCloudFile(path,tf);
			if (CaimitoConfig.cacheable)
				new File(tf).setLastModified(getLastModified());
			}finally{
				if (rl)
					cmr.remove(tf);
			}
    		
    		return new FileInputStream(tf);
    		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

    		}
     			return null;

    	}
    	
    		public void doDelete()throws CaimitoException{
    		try{
    		
    			try{
    				deleteCloudFile();
    			}finally{
    			String tf = CaimitoConfig.cachedir;
    			
    			if (CaimitoConfig.cloud_setup==CaimitoConfig.cloud_accounts_for_administration_only)
    				tf = tf + "/" + CaimitoUtil.normalize(user) + "/";	
    			tf = tf   + path;
    			
				FileUtil.deleteAll(tf);
				new File(tf).delete();
    			}
    	  		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

    		}
    		}
    		
    		public boolean isRoot(){
    			return (path.equals("/") || path.equals(""));
    		}
    		
    	public  String getRootPath(){
    		if (store != null)return store;
    		return rpath;
    	}
    	
    	public String getStorage(){
    		//if (store != null)return store;
    		if (!isRoot()){
    			String p2b = path;
    			if (p2b.endsWith("/"))
    				p2b = p2b.substring(0,p2b.length() -1);
    	
    			if (p2b.startsWith("/"))
    				p2b = p2b.substring(1,p2b.length());
    			   //if (!ist)
    			
    			if  ( CaimitoConfig.cloud_setup==CaimitoConfig.cloud_accounts_for_administration_only)
    			{
    				int i = p2b.indexOf("/");
    				if (i > 0){
    					return "/" + p2b.substring(0,i);
    					//p2b = p2b.substring(i + 1,p2b.length());			
    				}
    				else{
    					return  "/" + p2b;
    					//p2b = "";
    				}
    			}
    		}
    		//if (pathEquals(store))store = null;
    		//return store;
    		return null;
    	}
    	
 
    	public static String getAbsoluteRootPath(){
    		return arpath;
    	}
    	
    	public boolean pathEquals(String p){
    		if (path == null || p == null)return false;
    		String p1 = path;
    		if (p1.startsWith("/"))
    			p1 = p1.substring(1,p1.length());
       		if (p.startsWith("/"))
    			p = p.substring(1,p.length());
     		if (p1.endsWith("/"))
    			p1 = p1.substring(0,p1.length() -1);
    		if (p.endsWith("/"))
    			p = p.substring(0,p.length() -1);
 
     		return (p1.equals(p));
   
    	}
    	
    	public boolean isStorage(String p2){
    		return  (p2.length() > 1 && (p2.lastIndexOf("/") < 1 || p2.substring(0,p2.length() -1).lastIndexOf("/") < 1)&& CaimitoConfig.cloud_setup==CaimitoConfig.cloud_accounts_for_administration_only);
    	}
    	
    	public abstract void retrieveCloudFile(String path,String tf)throws CaimitoException;
    	public abstract void deleteCloudFile()throws CaimitoException;

}


