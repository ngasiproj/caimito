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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import tools.util.*;
import java.util.Vector;

public class FileResourceObj extends ResourceObj
    {
    	private static String home = "/usr/caimito/drive";
      protected static final int BUFFER_SIZE = 2048;
  	
    	static{
    		try{
    			home = CaimitoConfig.getConfig().getString("file.resource.dir");
    			new File(home).mkdirs();
    		}catch (Exception e){
    			e.printStackTrace();
    		}
    	}
    	
    	protected String getHome(){
    		return home;
    	}
    	public InputStream doGetInputStream()throws CaimitoException{
    		try{
    		
    		return new FileInputStream(getHome() + path);
    		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);
 			
    		}
     			return null;

    	}
    	

		public void doWrite(InputStream dest)throws CaimitoException{
		try{
			if (dest.available() < 0)return;
		FileOutputStream fout = new	FileOutputStream(getHome() + path);
		try{
			//(getHome() + path + " dO THE WRITE THING " + dest.available());
			StreamUtil.write(dest,fout);
			
			         byte buffer[] = new byte[BUFFER_SIZE];
            int len = -1;
                while (true) {
                    len = dest.read(buffer);
			//(getHome() + path + " dO THE WRITE THING ********* " + len);

                    if (len == -1)
                        break;
                    fout.write(buffer, 0, len);
                }
			
			
		}finally{
			fout.close();
		}
		 		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);
 			
    		}
		}


		public void doCopy(ResourceObj dest)throws CaimitoException{
		try{
		
			if (new File(getHome() + path).isDirectory())
			new File(getHome() + dest.path).mkdirs();

			FileUtil.copy(getHome() + path,getHome() + dest.path);
		 		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);
 			
    		}
		}
		
		
				public void doMove(ResourceObj dest)throws CaimitoException{
		try{
		
		//	if (new File(getHome() + path).isDirectory())
		//	new File(getHome() + dest.path).mkdirs();

			new File(getHome() + path).renameTo(new File(getHome() + dest.path));
		 		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);
 			
    		}
		}

	public boolean doExists()throws CaimitoException{
		
		return new File(getHome() + path).exists();
	}
	public void doDelete()throws CaimitoException{
		try{
		
		FileUtil.deleteAll(getHome() + path);
		new File(getHome() + path).delete();
		}catch (Exception e){
			e.printStackTrace();
			CaimitoException.throwException(e);
		}
	}
	
		public void doMkdir()throws CaimitoException{
		try{
		
		new File(getHome() + path).mkdirs();
		}catch (Exception e){
			e.printStackTrace();
			CaimitoException.throwException(e);
		}
	}

	public long getLastModified()throws CaimitoException{
	return new File(getHome() + path).lastModified();
	}

	public boolean isDirectory()throws CaimitoException{
	return new File(getHome() + path).isDirectory();
	}
	public long getContentLength()throws CaimitoException{
	//(getHome() + path + " GET THE LENGTH " + new File(getHome() + path).length() );
	return new File(getHome() + path).length();
	}	

	public Vector<ResourceObj> doList()throws CaimitoException{
	Vector<ResourceObj> l = new Vector<ResourceObj>();
	
	String[] dl = new File(getHome() + path).list();
	String ps = "";
	if (!path.endsWith("/"))
		ps = "/";
	if (dl != null){
		FileResourceObj ro = null;
		for (int ct = 0; ct < dl.length;ct++){
			ro = new FileResourceObj();
			ro.path =  path + ps + dl[ct];
			//(getHome() + path + ps + " DO LISTER " + dl[ct]);
			ro.user = user;
			ro.listing = listing;
			l.add(ro);
		}
	}
	
	return l;
	
	}

	
}


