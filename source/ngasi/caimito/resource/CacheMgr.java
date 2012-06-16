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
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ngasi.caimito.*;
import org.shaft.server.auth.*;
import tools.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.io.UnsupportedEncodingException;
import org.shaft.server.utils.*;
import tools.util.*;
import java.util.Hashtable;


public class CacheMgr
    {
    		int defwaitsleep = 1000;
    		long defwait = 1800;

 	static CacheMgr defc = new CacheMgr();
	public static CacheMgr getCacheMgr(){
		return defc;
	}

	public boolean waitFor(String n)throws Exception{
		boolean b = false;
		for (int ct = 0; ct < defwait;ct++)
		{
			b = getBoolean(n);
		//	if (b == null)
		//		if (!tf)return tf;
			if (!b)
				return true;
			Thread.sleep(defwaitsleep);
		}
		return false;
	}



	public  void put(String n)throws Exception{
		for (int ct = 0;ct < defwait;ct++){
		
			if (get(n) == null)
				break;
				Thread.sleep(defwaitsleep);
		
		}
			if (get(n) != null)
	CaimitoException.throwException("unable_to_lock_cache");
		cache.put(n,true);
	}


NameValuePairs cache = new NameValuePairs();



	public Object get(String n)throws Exception{
		return cache.get(n);
	}
		public Object remove(String n)throws Exception{
		return cache.remove(n);
	}
	
		public boolean getBoolean(String n)throws Exception{
		return  cache.getBoolean(n);
	}

}