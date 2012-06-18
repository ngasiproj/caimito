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
import java.util.Hashtable;
import com.google.gson.*;
import java.lang.reflect.*;
import com.google.gson.reflect.*;
import java.util.List;
import java.text.*;
import java.util.Locale;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

public class OpenStackResourceObj extends CloudResourceObj
    {
      protected static final int BUFFER_SIZE = 2048;
      
      
      	static         DateFormat dfm = null;

	static long maxautint = 0;

      

    	static{
    		try{
    			maxautint = CaimitoConfig.getConfig().getLong("cloud.max_auth_interval") * 60* 1000L;
    		}catch (Exception e){
    			e.printStackTrace();
    		}
    			try{

	dfm = new SimpleDateFormat(CaimitoConfig.getConfig().getString("cloud.obj.details.dateformat"));
	}catch (Throwable e){
		e.printStackTrace();
	}
    	}


    	public void retrieveCloudFile(String path,String tf)throws CaimitoException{
    		try{
    		auth();
    		Hashtable<String,String> h = new Hashtable<String,String>();
		h.put("X-Auth-Token",X_Auth_Token);
	
		
		HttpClientUtil.trustGetToFile(h,X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + CaimitoUtil.urlEncode(path),tf);
    		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

    		}

    	}


		public void doWrite(InputStream dest)throws CaimitoException{
		try{
			if (dest.available() < 0)return;


		Hashtable<String,String> h = new Hashtable<String,String>();
		h.put("X-Auth-Token",X_Auth_Token);
		if (contentType != null)
		h.put("Content-Type",contentType);
			
	//	if (contentLength > -1 )
	//	h.put("Content-Length",String.valueOf(contentLength));


		String nv = null;
	
		
		nv = HttpClientUtil.trustPut(h,X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + CaimitoUtil.urlEncode(path),dest);
		//(path + " put me " + nv);

		 		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

    		}
		}




public void doCopy(ResourceObj dest)throws CaimitoException
	{
		String fs = path;
		String t = dest.path;
//		if (!isDirectory())
//			return;
	if ( !t.endsWith("/"))
		t = t + "/";
	
//	Vector<ResourceObj> flist = doList();
			
			//-----------
			
			
					auth();

		Hashtable<String,String> h = new Hashtable<String,String>();
		h.put("X-Auth-Token",X_Auth_Token);
		if (isDirectory())
		h.put("Content-Type","application/directory");
		//h.put("Content-Length","0");

		String tp = path;
		if (tp.endsWith("/"))
			tp = tp.substring(0,tp.length() -1);
		h.put("X_COPY_FROM",CaimitoConfig.getConfig().getString("cloud.store") + tp);
		String tp2 = dest.path;
		if (tp2.endsWith("/"))
			tp2 = tp2.substring(0,tp2.length() -1);
try{

		String nv = HttpClientUtil.trustPut(h,X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + tp2);
		//(X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + tp + "-->" + h + " ******************X_COPY_FROM 1111 " + nv + ":" + tp2);		 
		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

		}
			
			if (!isDirectory())
			{
				
				return;
			}
	Vector<ResourceObj> flist = doList();
			
	if (flist == null || flist.size() < 1)
		return;
	for(int i = 0 ; i < flist.size() ; i++)
{
		OpenStackResourceObj nf = (OpenStackResourceObj)flist.elementAt(i);
		String nt = t;
		boolean isd = false;
		if (nf.isDirectory())
		{
			nt = nt + 	nf.path.substring(path.length(),nf.path.length());
			OpenStackResourceObj dr = new OpenStackResourceObj();
			dr.path = nt;
			dr.doMkdir();
			isd = true;
			nf.doCopy(dr);
		}
		else
		try
		{
			ResourceObj dr = new OpenStackResourceObj();
			dr.path = t + nf.getName();
			
			nf.xCopy(dr);
		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

		}
		
}
	
		
	}

		public void xCopy(ResourceObj dest)throws CaimitoException{
		try{


		auth();

		Hashtable<String,String> h = new Hashtable<String,String>();
		h.put("X-Auth-Token",X_Auth_Token);
		if (isDirectory())
		h.put("Content-Type","application/directory");
		//h.put("Content-Length","0");

		String tp = path;
		if (tp.endsWith("/"))
			tp = tp.substring(0,tp.length() -1);
		h.put("X_COPY_FROM",CaimitoConfig.getConfig().getString("cloud.store") + tp);
		String tp2 = dest.path;
		if (tp2.endsWith("/"))
			tp2 = tp2.substring(0,tp2.length() -1);

		String nv = HttpClientUtil.trustPut(h,X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + tp2);
		//(X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + tp + "-->" + h + " ******************X_COPY_FROM " + nv + ":" + tp2);		 



		 		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

    		}
		}


				public void doMove(ResourceObj dest)throws CaimitoException{
		try{

			doCopy(dest);
			doDelete();
		
		 		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

    		}
		}

	public boolean doExists()throws CaimitoException{
	if (path.equals("") || path.equals("/"))
		return true;

		//("OEXISTST " + path);
		init();
		return ex;
		//return new File(root + path).exists();
	}
	public void deleteCloudFile()throws CaimitoException{


try{

			auth();



			if (isDirectory())
			{
				
	Vector<ResourceObj> flist = doList();
			
	if (flist != null && flist.size() > 0)
	for(int i = 0 ; i < flist.size() ; i++)
{
		OpenStackResourceObj nf = (OpenStackResourceObj)flist.elementAt(i);
		nf.doDelete();
		/*String nt = t;
		boolean isd = false;
		if (nf.isDirectory())
		{
			nt = nt + 	nf.path.substring(path.length(),nf.path.length());
			OpenStackResourceObj dr = new OpenStackResourceObj();
			dr.path = nt;
			dr.doMkdir();
			isd = true;
			nf.doCopy(dr);
		}
		else
		try
		{
			ResourceObj dr = new OpenStackResourceObj();
			dr.path = t + nf.getName();
			
			nf.xCopy(dr);
		}catch (Exception e){
    			e.printStackTrace();
   			CaimitoException.throwException(e);

		}*/
		
}
			}




		Hashtable<String,String> h = new Hashtable<String,String>();
		h.put("X-Auth-Token",X_Auth_Token);
		//( "DEO DELOET " + X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + CaimitoUtil.urlEncode(path) + "?format=json");

		String nv = null;
			String p2 = path;
		if (p2.endsWith("/"))
			p2 = p2.substring(0,p2.length() -1);
	
		nv = HttpClientUtil.trustDelete(h,X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + CaimitoUtil.urlEncode(p2) );


	//("DELETE OF he2 " + nv);


















		}catch (Exception e){
			e.printStackTrace();
			CaimitoException.throwException(e);
		}
	}
	
	protected void init(){
		if (init)return;
try{
				init = true;
	if (path.equals("") || path.equals("/"))
		return ;

			auth();

		Hashtable<String,String> h = new Hashtable<String,String>();
		h.put("X-Auth-Token",X_Auth_Token);


		String p2 = path;
		if (p2.endsWith("/"))
			p2 = p2.substring(0,p2.length() -1);
		int nv = HttpClientUtil.trustHead(h,X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + CaimitoUtil.urlEncode(p2) + "?format=json");
		if (nv == 404)return;


	NameValuePairs nvp = new NameValuePairs(h);
	

	cl = nvp.getLong("Content-Length");
	lm =  dfm.parse(nvp.getString("Last-Modified")).getTime();
			if (nvp.getString("Content-Type").equals("application/directory"))
				isd = true;

	//init = true;
	ex = true;
}catch (Throwable e){
	e.printStackTrace();
	
	if (CaimitoConfig.cacheable){
		String tf = CaimitoConfig.cachedir + path;
		File ftf = new File(tf);
	
		if (ftf.exists())
		{
			cl = ftf.length();
			lm =  ftf.lastModified();
					if (ftf.isDirectory())
						isd = true;
		}
	}
	
	
	
}


	}

		protected boolean  ex = false;
		protected boolean init = false;
		protected boolean isd = false;
		protected long lm = 0;
		protected long cl = 0;
		//protected OSRAttribute attributes = null;

		public void doMkdir()throws CaimitoException{
		try{

		//new File(root + path).mkdirs();
		

	
		auth();

		Hashtable<String,String> h = new Hashtable<String,String>();
		h.put("X-Auth-Token",X_Auth_Token);
		h.put("Content-Type","application/directory");
		String tp = path;
		if (tp.endsWith("/"))
			//tp = tp + "/";
			tp = tp.substring(0,tp.length() -1);
		String nv = HttpClientUtil.trustPut(h,X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + tp);
		//(X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + tp + "-->" + h + " THEY AUTHOiniyt " + nv);		 
		
		
		}catch (Exception e){
			e.printStackTrace();
			CaimitoException.throwException(e);
		}
	}

	public long getLastModified()throws CaimitoException{
	//return new File(root + path).lastModified();
	init();
	return lm;
	}

	public boolean isDirectory()throws CaimitoException{
	if (path.equals("") || path.equals("/"))
		return true;
	init();
	return isd;
	}
	public long getContentLength()throws CaimitoException{
	init();
	return cl;
	}
	
	 protected static String X_Auth_Token = null; //"5bba5c21-3c8c-4f6b-8b17-9adb7c2ad4ea";
	 protected static String X_Storage_Url = null; //"http://swift.rc.nectar.org.au:8888/v1/AUTH_793";
	 protected static long authtime = 0;
	 void auth()throws CaimitoException{
		try{
			
		if (X_Auth_Token != null && X_Storage_Url != null && !((System.currentTimeMillis() - authtime) > maxautint))return;
		X_Auth_Token = null;
		X_Storage_Url = null;
		Hashtable<String,String> h = new Hashtable<String,String>();
		//String curl = null;
		String nv = null;
		String curl  = CaimitoConfig.getConfig().getString("cloud.url");
		if (curl.endsWith("/"))
			curl = curl.substring(0,curl.length() - 1);

		if (CaimitoConfig.getConfig().get("cloud.api.version") == null || CaimitoConfig.getConfig().getString("cloud.api.version").startsWith("1."))
		try{
		h.put("X-Auth-User",CaimitoConfig.getConfig().getString("cloud.username"));
		h.put("X-Auth-Key",CaimitoConfig.getConfig().getString("cloud.api.key_password"));
		//curl = CaimitoConfig.getConfig().getString("cloud.url");
		//if (curl.endsWith("/"))
		//	curl = curl.substring(0,curl.length() - 1);
		nv = HttpClientUtil.trustGet(h,curl);
			
		X_Storage_Url = h.get("X-Storage-Url");
		X_Auth_Token = h.get("X-Auth-Token");
		}catch (Exception e){
			System.out.println(" Failed V1 Auth " + e.toString());
			if (CaimitoConfig.getConfig().getString("cloud.api.version").startsWith("1."))
			{
				if (e instanceof CaimitoException)
					throw (CaimitoException)e;
				else
					CaimitoException.throwException(e);
		
			}
		}
		
		
		if (X_Storage_Url == null || X_Auth_Token == null){
			 
			if (!curl.endsWith("/tokens"))
			curl = curl + "/tokens";
		Hashtable<String,String> pc = new Hashtable<String,String>();
		pc.put("username",CaimitoConfig.getConfig().getString("cloud.username"));
		pc.put("password",CaimitoConfig.getConfig().getString("cloud.api.key_password"));
	
		Hashtable<String,Hashtable<String,String>> auh = new Hashtable<String,Hashtable<String,String>>();
		auh.put("passwordCredentials",pc);
	
		Hashtable tl = new Hashtable();
		tl.put("auth",auh);
		
		h = new Hashtable<String,String>();
		h.put("Content-type","application/json");

		Gson gson = new Gson();
		String json = gson.toJson(tl);
		ByteArrayInputStream dest = new ByteArrayInputStream(json.getBytes());

		nv = HttpClientUtil.trustPost(h,curl,dest);
		//Gson gson = new Gson();
		OSV2TokenResponse oa = 	gson.fromJson(nv, new TypeToken<OSV2TokenResponse>() {}.getType());
		Map<String,String> eps = oa.getResponse();
		if (CaimitoConfig.getConfig().getString("cloud.endpoint.access").equals("internal"))
		X_Storage_Url = eps.get("internalURL");
		else
			X_Storage_Url = eps.get("publicURL");	
		X_Auth_Token = oa.access.token.id;
		if (X_Storage_Url == null || X_Auth_Token == null)
		CaimitoException.throwException("unable_to_auth");

		
		}
		authtime = System.currentTimeMillis();
		}catch (Throwable e){
			e.printStackTrace();
			CaimitoException.throwException(e);
		}
	}

	public Vector<ResourceObj> doList()throws CaimitoException{
	//doDelete();
	Vector<ResourceObj> l = new Vector<ResourceObj>();

	try{
	
		auth();

		Hashtable<String,String> h = new Hashtable<String,String>();
		h.put("X-Auth-Token",X_Auth_Token);
		String p2 = path;
		if (p2.endsWith("/"))
			p2 = p2.substring(0,p2.length() -1);
			//p2 = p2 + "/";
	//	?prefix=photos/&delimiter=/
		boolean isroot = (p2.equals("/") || p2.equals(""));
		String pars =  "?delimiter=/";	
		if (!isroot){
		String p2b = p2;
		if (p2b.startsWith("/"))
			p2b = p2b.substring(1,p2b.length());
			pars = pars + "&prefix=" + CaimitoUtil.urlEncode(p2b) + "/";
		}
		//pars = "";
		//("***doList1 " + X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store") + CaimitoUtil.urlEncode(p2) + pars + " --> " + h + " h <-->nv ");		 
//+ CaimitoUtil.urlEncode(p2)
		String nv = HttpClientUtil.trustGet(h,X_Storage_Url + "/" + CaimitoConfig.getConfig().getString("cloud.store")  + pars);
		
		if (nv == null || nv.length() < 1)return l;
		ByteArrayInputStream bin = new ByteArrayInputStream(nv.getBytes());
		EZArrayList lblv = new EZArrayList(bin);
		bin.close();
		String oa = null;
			OpenStackResourceObj ro = null;
	String dn = "";
	String ps = "";

	if (!path.endsWith("/"))
		ps = "/";
				for (int ct = 0;ct < lblv.size();ct++)
		{
			dn = lblv.elementAt(ct).toString();
			/*//("****************************DN= " + dn);
				ro = new OpenStackResourceObj();

			ro.path =  "/" + dn;
			ro.user = user;
			ro.listing = listing;
			ro.ex = true;
			try{
			
			ro.doDelete();
			}catch (Exception e){
				e.printStackTrace();
			}*/
			if (dn.equals(".")){
			//("****************************DN= " + dn);

				continue;
			}
			if (dn.endsWith("/"))
			{
			//("****************************DN= " + dn);

				continue;
				
			}
			//(path + " OS LIST " + dn);
			ro = new OpenStackResourceObj();

			ro.path =  "/" + dn;
			ro.user = user;
			ro.listing = listing;
			ro.ex = true;
			
			
			l.add(ro);


		}
		
		
		
		/*				Gson gson = new Gson();

					List<OSRAttribute> attrs = 	gson.fromJson(nv, new TypeToken<List<OSRAttribute>>() {}.getType());

		Vector<OSRAttribute> lblv = new Vector<OSRAttribute>(attrs);
		OSRAttribute oa = null;
		OpenStackResourceObj ro = null;
	String ps = "";
	if (!path.endsWith("/"))
		ps = "/";
		String dn = null;
		for (int ct = 0;ct < lblv.size();ct++)
		{
			oa = lblv.elementAt(ct);
			//(path + " OS LIST " + oa);
			ro = new OpenStackResourceObj();
			dn = oa.name;
			if (dn.endsWith("/"))
			{
				dn = dn.substring(0,dn.length() -1);
				ro.isd = true;
				
			}
			ro.path =  path + ps + dn;
			ro.user = user;
			ro.listing = listing;
			ro.cl = oa.bytes;
			ro.init = true;
			ro.ex = true;
			ro.lm = oa.lastModified();
			l.add(ro);


		}*/
/*
	String[] dl = new File(root + path).list();
	String ps = "";
	if (!path.endsWith("/"))
		ps = "/";
	if (dl != null){
		FileResourceObj ro = null;
		for (int ct = 0; ct < dl.length;ct++){
			ro = new FileResourceObj();
			ro.path =  path + ps + dl[ct];
			ro.user = user;
			ro.listing = listing;
			l.add(ro);
		}
	}*/

		}catch (Throwable e){
			e.printStackTrace();
			CaimitoException.throwException(e);
		}
	return l;

	}


}

class OSRAttribute{

	static         DateFormat dfm = null;
	static boolean rmdend = false;
	static{
	try{
	//("DATE FORMAT " + CaimitoConfig.getConfig().getString("cloud.dateformat"));
	if (CaimitoConfig.getConfig().getBoolean("cloud.date.remove.end"))
	{
		rmdend = true;
	}
	dfm = new SimpleDateFormat(CaimitoConfig.getConfig().getString("cloud.dateformat"));
	}catch (Throwable e){
		e.printStackTrace();
	}
	}
	
	
	String name = null;
	String hash = null;
	long bytes = 0;
	String content_type = null;
	String last_modified = null;
	public long lastModified()throws Exception{
		if (rmdend)
		{
			String lm = last_modified;
			int i = lm.lastIndexOf(".");
			if (i > 0){
				lm = lm.substring(0,i);
				return dfm.parse(lm).getTime();
				
			}
		}
		return dfm.parse(last_modified).getTime();
	}

}
class OSV2TokenResponse {
	public TokenObj access = null;

	//public Hashtable<String,Hashtable<String,String>> access = null;
	public Map<String,String> getResponse(){
		Hashtable<String,String> res = new Hashtable<String,String>();
			 Iterator<EndPoints> en = 	access.serviceCatalog.iterator();
		//String n = null;
		EndPoints v = null;
		while(en.hasNext()){
		v = en.next();
		if (v.type.equals("object-store") && v.name.equals("swift")){
			 Iterator<Map<String,String>> enp = 	v.endpoints.iterator();
	
				if(enp.hasNext())
				return enp.next();	
		}
		}
	return res;
	}

}
 class TokenObj{
	 public TokenObj2 token = null;
	 public List<EndPoints> serviceCatalog = null;

 }
 class EndPoints{
	public List<Map<String,String>> endpoints = null;
	public String type = null;
	public String name = null;
	
 }
 class TokenObj2{
	public  String id = null;
 }
/*class OSRObjDetails{

	static         DateFormat dfm = null;
	static boolean rmdend = false;
	static{
	try{
	//("DATE FORMAT " + CaimitoConfig.getConfig().getString("cloud.dateformat"));
	if (CaimitoConfig.getConfig().getBoolean("cloud.date.remove.end"))
	{
		rmdend = true;
	}
	dfm = new SimpleDateFormat(CaimitoConfig.getConfig().getString("cloud.dateformat"));
	}catch (Throwable e){
		e.printStackTrace();
	}
	}
	
	

Last-Modified=Tue, 24 Apr 2012 00:05:15 GMT, Accept-Ranges=bytes, Content-Length=0, X-Trans-Id=txd7c65f16aa884af9a09266da2636daf5, Date=Tue, 24 Apr 2012 03:15:30 GMT, X-Auth-Token=AUTH_tke0c45f67d4fb47189ad41fc771b4b53f, Content-Type=application/x-www-form-urlencoded; charset=ISO-8859-1, Connection=keep-alive, Etag=d41d8cd98f00b204e9800998ecf8427e



	public long lastModified()throws Exception{
		if (rmdend)
		{
			String lm = last_modified;
			int i = lm.lastIndexOf(".");
			if (i > 0){
				lm = lm.substring(0,i);
				return dfm.parse(lm).getTime();
				
			}
		}
		return dfm.parse(last_modified).getTime();
	}

}*/

