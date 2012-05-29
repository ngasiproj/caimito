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
    	public InputStream doGetInputStream()throws CaimitoException{
    		try{
		String tf = CaimitoConfig.cachedir + path;
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
    			String tf = CaimitoConfig.cachedir + path;
				FileUtil.deleteAll(tf);
				new File(tf).delete();
    			}
    	  		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

    		}
    		}
    	
    	public abstract void retrieveCloudFile(String path,String tf)throws CaimitoException;
    	public abstract void deleteCloudFile()throws CaimitoException;

}


