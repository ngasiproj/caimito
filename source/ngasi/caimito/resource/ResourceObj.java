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

public abstract class ResourceObj
    {
	public String contentType = null;
	public long contentLength = -1;

	public boolean listing = false;
	public String user = null;
	public String path = null;
	String mt = null;
	//public String root = null;
	public String privilege = null; //r,w,a
	
	    protected static final SimpleDateFormat format =
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    protected static final TimeZone gmtZone = TimeZone.getTimeZone("GMT");

    static {

        format.setTimeZone(gmtZone);


    }

	public boolean canRead()throws CaimitoException{
	return true;
	}
	public boolean canWrite()throws CaimitoException{
	return true;
	}


	public void checkRead()throws CaimitoException{
		if (!canRead())
			CaimitoException.throwException("forbidden");
	}
	public void checkWrite()throws CaimitoException{
		if (!canWrite())
			CaimitoException.throwException("forbidden");

	}
	public ResourceObj getReadmeFile()throws CaimitoException{
		return null;
	}	
	
	public boolean listings()throws CaimitoException{
		checkRead();
		return listing;
	}
	
		public String getName()throws CaimitoException{
			int i = path.lastIndexOf("/");
			if (i > -1 && path.length() > 1){
				return path.substring(i + 1,path.length());
			}
		return null;
	}	
	
	    public String getLastModifiedHttp() throws CaimitoException{
     synchronized (format) {
            return format.format(new Date(getLastModified()));
        }
    }
    
    	    public long getCreation() throws CaimitoException{
     return getLastModified();
    }
    

	public InputStream getInputStream()throws CaimitoException{
		checkRead();
		return doGetInputStream();
	}
	
	public Vector<ResourceObj> list()throws CaimitoException{
		checkRead();
		return doList();
	}
	public boolean exists()throws CaimitoException{
		checkRead();
		return doExists();
	}
	public void delete()throws CaimitoException{
		checkWrite();
		doDelete();
	}

	public void mkdir()throws CaimitoException{
		checkWrite();
		doMkdir();
	}

		public void write(InputStream inp)throws CaimitoException{
		checkWrite();
		doWrite(inp);
	}
	
	
		public void copy(ResourceObj dest)throws CaimitoException{
		checkRead();
		dest.checkWrite();
		doCopy(dest);
	}
	
			public void move(ResourceObj dest)throws CaimitoException{
		checkWrite();
		dest.checkWrite();
		doMove(dest);
	}
	
	public String getMimeType(){
		return mt;
	}
	public void setMimeType(String t){
		mt = t;
	}	
		public String getETag()throws CaimitoException{
			
		                    long contentLength = getContentLength();
                    long lastModified = getLastModified();
                    if ((contentLength >= 0) || (lastModified >= 0)) {
                        return  "W/\"" + contentLength + "-" +
                                   lastModified + "\"";
                    }
			
		return null;
	}
		public abstract void doMove(ResourceObj dest)throws CaimitoException;

		public abstract void doCopy(ResourceObj dest)throws CaimitoException;
		public abstract void doWrite(InputStream dest)throws CaimitoException;

	public abstract InputStream doGetInputStream()throws CaimitoException;
	public abstract long getContentLength()throws CaimitoException;

	public abstract Vector<ResourceObj> doList()throws CaimitoException;
	public abstract boolean doExists()throws CaimitoException;
	public abstract void doDelete()throws CaimitoException;
	public abstract void doMkdir()throws CaimitoException;

	public abstract boolean isDirectory()throws CaimitoException;
	
	public abstract long getLastModified()throws CaimitoException;
}


