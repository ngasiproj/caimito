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
import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;
import org.shaft.server.utils.*;
import tools.util.*;
import java.sql.Connection;
import java.sql.ResultSet;


public class ResourceStore
    {
    	public static int READ = 1;
      	public static int WRITE = 2;
  	    protected static MessageDigest md5Helper = null;

  		static{
				
       try {
                md5Helper = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
  		}

	    public static  ResourceObj lookup(String path, HttpServletRequest request)
        throws CaimitoException {
        	//("GET Z PAth " + path);
	    	String	u = (String)request.getAttribute(CaimitoConfig.caimitouserreqobj);
        	if (u == null)
        	u = login(request);
        	return lookup(path,u);
        }

	    //public static  ResourceObj lookup(String path, HttpServletRequest request,
        //                         HttpServletResponse response)
        //throws CaimitoException {
        //	
        //}
		protected static Connection getConnection()throws Exception{
		return DBConMgr.getConnection(ShaftRestConfig.datasourceName);
	}
	    public static  boolean isPublicPath(String path,CaimitoBooleanHolder l,String u)
        throws CaimitoException {
        	try{
        //("PUBLIC PATHS " + path);
        		if (path.startsWith("/" + CaimitoConfig.shaftapp + "/") || path.equals("/favicon.ico"))
      			return true;
        	if (path.startsWith("/"))
        		path = path.substring(1,path.length());
         	if (path.endsWith("/"))
        		path = path.substring(0,path.length() -1);

        	return pathMatch(path,"publicpaths",l,u);
        	}catch (Exception e){
        		//e.printStackTrace();
        		//(path + " isPublicPath.error " + e.toString());
        	}
        	return false;
        }


	public static boolean pathMatch(String path,String table,CaimitoBooleanHolder l,String u)throws Exception
		{
		
			if (doPathMatch(path,table,l,u))
				return true;
		
		int i = path.lastIndexOf("/");
		while (i > 0)
		{
			path = path.substring(0,i);
			if (doPathMatch(path,table,l,u))
				return true;
			i = path.lastIndexOf("/");
		}
		if (i < 0)
			return doPathMatch(path,table,l,u);
		
			return false;
		}

	protected static boolean doPathMatch(String path,String table,CaimitoBooleanHolder l,String u)throws Exception
		{
		SPQueryAndUpdate spq = new SPQueryAndUpdate(getConnection());
			try{
		String sql = "SELECT path,shaftowner,listing from " + CaimitoConfig.shaftapp + "_" + table + " where path = ?";
			spq.setTemplate(sql);
			spq.addVariable(path);
			ResultSet res = spq.query();
				//(path + " doPathMatch 1 " + table);

			if (res.next()){
				String su = res.getString(2);
				NameValuePairs nvp =  getUserPriv(su);
				//(path + ":" + res.getString(2) + " doPathMatch 2 " + nvp);
				if (nvp == null || nvp.size() < 1)
					return false;
	           	if (!path.startsWith(nvp.getString("path") + "/") && !(path + "/").startsWith(nvp.getString("path") + "/"))
	           		return false;
	           	if (u == null || (!su.equals(u) && !UserMgr.getUserMgr(CaimitoConfig.shaftapp).isAdminOrOwnerOf(CaimitoConfig.shaftapp,u,su)))
				l.value = (res.getBoolean(3));
				else
				l.value = CaimitoConfig.getConfig().getBoolean("resource.dir.listing");
				return true;
			}
			}

			finally{
				if (spq != null)spq.close();
			}
			return false;
		}



	protected static NameValuePairs getUserPrivFromDB(String u)throws Exception
		{
			try{
			
		SPQueryAndUpdate spq = new SPQueryAndUpdate(getConnection());
			try{
		String sql = "SELECT * from " + CaimitoConfig.shaftapp + "_pathprivileges where resourceuser = ? AND shaftowner = ?";

		//	username = UserMgr. (CaimitoConfig.shaftapp).getOwner(CaimitoConfig.shaftapp,username); 	
		//	if (username == null)
		//		break;
		boolean isadmin = false;
		if (RealmMgr.getRealmMgr(CaimitoConfig.shaftapp).isAdmin(CaimitoConfig.shaftapp,u)){
sql = "SELECT * from " + CaimitoConfig.shaftapp + "_pathprivileges where resourceuser = ?";
		
		 isadmin = true;
		}

			spq.setTemplate(sql);
			spq.addVariable(u);
			if (!isadmin)
			spq.addVariable(UserMgr.getUserMgr(CaimitoConfig.shaftapp).getOwner(CaimitoConfig.shaftapp,u));
				
			return spq.getHash();
			}

			finally{
				if (spq != null)spq.close();
			}
			}catch (Exception e){
	        		//(u + " getUserPrivFromDB " + e.toString() + ":" );
	        		e.printStackTrace();
			
			}
			return null;
		}

	protected static NameValuePairs getUserPriv(String u)throws Exception
		{
			try{
			
	

			NameValuePairs nvp = getUserPrivFromDB(u);
			if (nvp == null || nvp.size() < 1)
				return nvp;
			String path2 = null;
			String username = u;
			while (true){
			username = UserMgr.getUserMgr(CaimitoConfig.shaftapp).getOwner(CaimitoConfig.shaftapp,username); 	
			if (username == null)
				break;
			if (RealmMgr.getRealmMgr(CaimitoConfig.shaftapp).isAdmin(CaimitoConfig.shaftapp,username))
				break;
			if (path2 == null)path2 = "";
			path2 =  getUserPrivFromDB(username).getString("path") + "/" + path2;
			}
			if (path2 != null){
				path2 =   path2 + "/" + nvp.getString("path");
				//("ZEPATH@ A " + path2);

				path2 = CaimitoUtil.normalize(path2);
				//("ZEPATH@ " + path2);

				nvp.put("path",path2);
				
			
			}
			return nvp;
	


			}catch (Exception e){
	        		//(u + " getUserPriv.error " + e.toString() + ":" );
	        		e.printStackTrace();
			
			}
			return null;
		}


	    public static  ResourceObj lookup(String path, HttpServletRequest request,
                                 HttpServletResponse response,int action)
        throws CaimitoException {
        	String u = null;
        	//("GET Z PAth  2 " + path);      	
        	if (path.equals("/") || path.equals(""))
        	{
        	if (CaimitoConfig.protectedRoot)
        	{
        	
        	u = login(request,response);
        	if (u == null)return null;	
        	}
        	}
        	
        	if (u == null)
        	{
        		u = (String)request.getAttribute(CaimitoConfig.caimitouserreqobj);
        	if (u == null)
        	u = login(request);
        	}
try{
        	//(action + ":" + path + " THE USER IS " + u);
        	if (RealmMgr.getRealmMgr(CaimitoConfig.shaftapp).isAdmin(CaimitoConfig.shaftapp,u))
        	return lookup(path,u);

        	
        	if (action == READ){
        	
        	CaimitoBooleanHolder l = new CaimitoBooleanHolder();
        	if (isPublicPath(path,l,u)){
        	
         	ResourceObj ro = lookup(path,u);
         	if (ro != null)
         		ro.listing = l.value;
         	return ro;
        	}
        	}
        	if (u == null){
        	
        	u = login(request,response);
        	if (u == null)return null;	
        	}
        	
        	
        	
        	NameValuePairs nvp = getUserPriv(u);
        	//(u + " PRIVILEGE GO " + nvp);
        	        	if (nvp == null || nvp.size() < 1)
        	{
             response.sendError(HttpServletResponse.SC_FORBIDDEN);
       		return null;
        	}
       

        	if (action == WRITE && (nvp.getInt("privilege") != WRITE ||(path.startsWith("/" + CaimitoConfig.shaftapp + "/") || path.equals("/favicon.ico"))))
        	{
            	//(u + " PRIVILEGE GO 2 " + nvp);

             response.sendError(HttpServletResponse.SC_FORBIDDEN);
       		return null;
        	}
           	if (!path.startsWith("/" + nvp.getString("path") + "/") && !(path + "/").startsWith("/" + nvp.getString("path") + "/"))
        	{
        	//(path + " PRIVILEGE GO 3 " + nvp);

             response.sendError(HttpServletResponse.SC_FORBIDDEN);
       		return null;
        	}else lookup("/" + nvp.getString("path") + "/",u) .mkdir();    	
          	return lookup(path,u);
      	
        	}catch (Exception e){
        		
        		e.printStackTrace();
        		return null;
        	}
        	/*//(u +  " WOWWW SD 1 " + CaimitoConfig.resource + ":" + path);
        	if (CaimitoConfig.resource.equals("file")){
        		ro = new FileResourceObj();
        	}
          	//(u +  " WOWWW SD 2 " + ro);

        	if (ro != null){
        		ro.user = u;
        		ro.path = path;
        	}
        	return ro;*/
//        	return null;
        }
        
        
        	    public static  ResourceObj lookup(String path,String u)
        throws CaimitoException {
        	ResourceObj ro = null;
			//if (u == null)
			//	Thread.dumpStack();
        	//(u +  " ***************WOWWW SD 1 " + CaimitoConfig.resource + ":" + path);
        	if (path.startsWith("/" + CaimitoConfig.shaftapp + "/"))// || path.equals("/favicon.ico"))
        	{
        		//if (path.equals("/favicon.ico"))
        			//path = "/" + CaimitoConfig.shaftapp + "/public/favicon.ico" ;
               		if (!path.startsWith("/" + CaimitoConfig.shaftapp + "/public/"))
               			path = "/" + CaimitoConfig.shaftapp + "/public/" + path.substring(CaimitoConfig.shaftapp.length() + 1,path.length());
               		//(" FAV NEW PATH " + path);
               		ro = new ShaftResourceObj();
 		
        	}
        	else if (CaimitoConfig.resource.equals("file")){
        		ro = new FileResourceObj();
        	}
        	 else if (CaimitoConfig.resource.equals("openstack")){
         	//(u +  " WOWWW SD 2 " + CaimitoConfig.resource + ":" + path);

        		ro = new OpenStackResourceObj();
        	}
          	//(u +  " WOWWW SD 2 " + ro);

        	if (ro != null){
        		ro.user = u;
        		ro.path = path;
        		ro.listing = CaimitoConfig.getConfig().getBoolean("resource.dir.listing");
        	}
        	
    		if (path.equals("/favicon.ico") && !ro.exists()){
    			path = "/" + CaimitoConfig.shaftapp + "/public/favicon.ico" ;
           		ro = new ShaftResourceObj();
        		ro.user = u;
        		ro.path = path;
    		}
        	
        	
        	return ro;
        }
     
     
             	    public static  String login( HttpServletRequest request )
        throws CaimitoException {
        	return login(request,null);
        }
        
        
        
        
            protected static String getDigest(String username, String realmName)throws CaimitoException {

try{

          return UserMgr.getUserMgr(CaimitoConfig.shaftapp).getMD5Digest(CaimitoConfig.shaftapp,username);  
        /*String digestValue = username + ":" + realmName + ":"
            + UserMgr. (CaimitoConfig.shaftapp).getPasswd(CaimitoConfig.shaftapp,username);

        byte[] valueBytes = null;
            valueBytes = digestValue.getBytes(Charset.defaultCharset());

        byte[] digest = null;
        synchronized(md5Helper) {
            digest = md5Helper.digest(valueBytes);
        }

        return md5Encode(digest);*/
}catch (Exception e){
	e.printStackTrace();
	CaimitoException.throwException(e);
}
return null;
    }
        
        
        
            protected static String authenticate(NameValuePairs nvp) throws CaimitoException{
							// username="caimitoadmin", realm="Caimito", nonce="", uri="/", response="2fb52dbbcb7b25fd33840eac3027e9c5"

String username = (String)nvp.get("username");
 String clientDigest = (String)nvp.get("response");
  String nonce = (String)nvp.get("nonce");
  String nc = (String)nvp.get("nc");
String cnonce = (String)nvp.get("cnonce");
String qop = (String)nvp.get("qop");
String realm = (String)nvp.get("realm");
            String a2 = nvp.get("method") + ":" + nvp.get("uri");

            byte[] buffer;
            synchronized (md5Helper) {
                buffer = md5Helper.digest(a2.getBytes(Charset.defaultCharset()));
            }
            String md5a2 = md5Encode(buffer);


        String md5a1 = getDigest(username, realm);
        if (md5a1 == null)
            return null;
        String serverDigestValue;
        if (qop == null) {
            serverDigestValue = md5a1 + ":" + nonce + ":" + md5a2;
        } else {
            serverDigestValue = md5a1 + ":" + nonce + ":" + nc + ":" +
                    cnonce + ":" + qop + ":" + md5a2;
        }

        byte[] valueBytes = null;
       // try {
            valueBytes = serverDigestValue.getBytes(Charset.defaultCharset());
       // } catch (UnsupportedEncodingException uee) {
       //     log.error("Illegal digestEncoding: " + getDigestEncoding(), uee);
        //    throw new IllegalArgumentException(uee.getMessage());
        //}

        String serverDigest = null;
        // Bugzilla 32137
        synchronized(md5Helper) {
            serverDigest = md5Encode(md5Helper.digest(valueBytes));
        }

        /*if (log.isDebugEnabled()) {
            log.debug("Digest : " + clientDigest + " Username:" + username 
                    + " ClientSigest:" + clientDigest + " nonce:" + nonce 
                    + " nc:" + nc + " cnonce:" + cnonce + " qop:" + qop 
                    + " realm:" + realm + "md5a2:" + md5a2 
                    + " Server digest:" + serverDigest);
        }*/
        
        if (serverDigest.equals(clientDigest)) {
            return (username);
        }

        return null;
    }
        
        
   
           	    public static  String login( HttpServletRequest request,
                                 HttpServletResponse response
                                )
        throws CaimitoException {
        	String ua = request.getHeader("user-agent");
        	
        	/*java.util.Enumeration hs = request.getHeaderNames();
        	while (hs.hasMoreElements())
        	{
        		String hn = (String)hs.nextElement();
        		String hv = request.getHeader(hn);   
        		System.out.println(hn + " USER  HEADER " + hv);
           	  
        	}
        	
        	
        	java.util.Enumeration hds = request.getParameterNames();
        	while (hds.hasMoreElements())
        	{
        		String hd = (String)hds.nextElement();
        		String hdv = request.getParameter(hd);
        		//(hd + " PAR " + hdv);
        		System.out.println(hd + " USER  params " + hdv);
        	}
        	
        	String ct = request.getContentType();
        	if (ct != null && ct.equals("application/xml"))
        	{
        	    int cl = request.getContentLength();   
        	    if (cl > 0){
        		byte[] transferBuffer = new byte[cl];
        		try{
        	       BufferedInputStream  requestBufInStream =     new BufferedInputStream(request.getInputStream(), cl);
        	       requestBufInStream.read(transferBuffer);  		
        		System.out.println(" XML VAL " + new String(transferBuffer));
        		}catch (Exception e){
        			e.printStackTrace();
        		}
        		}
        	}
        	
        	//if (ua.equals("Microsoft Data Access Internet Publishing Provider DAV") || ua.indexOf("davfs2") > -1 || CaimitoConfig.getConfig().getString(ua + ".auth").equals("basic"))
        	//	return basicLogin(request,response);
        		System.out.println("USER AGAENT " + ua);*/
         		if (CaimitoConfig.getConfig().getString(ua + ".auth").equals("digest"))
         		return digestLogin(request,response);
       			return basicLogin(request,response);
        }
   
        
        	    public static  String digestLogin( HttpServletRequest request,
                                 HttpServletResponse response
                                )
        throws CaimitoException {
        		//("DIGEST LOGIN 1");
        	    	NameValuePairs nvp = new NameValuePairs();
        					String hr = request.getHeader("Authorization");
					if (hr != null)
					{
						if (hr.startsWith("Digest "))
						{
try{
							//hr = SharedMethods.replaceSubstring(hr,"Digest ","");
							hr = hr.trim();
							hr = hr.substring(7,hr.length());
							// username="caimitoadmin", realm="Caimito", nonce="", uri="/", response="2fb52dbbcb7b25fd33840eac3027e9c5"
							NameValuePairs nvp1 = NameValuePairs.toNameValuePair(hr,"=",",");
							Enumeration nvp1e = nvp1.keys();
							String k = null;
							String v = null;
							while(nvp1e.hasMoreElements())
							{
								k = (String)nvp1e.nextElement();
								v = nvp1.getString(k);
								nvp.put(k.trim(),StringUtil.getTrimmedValue(v.trim()));
							}
							nvp.put("method",request.getMethod());
							//(nvp + " DIGEST 1 " + hr);
							//byte[] b = Base64Decoder.fromBase64(hr.getBytes());
							//String auth = new String(b);
							//String name = auth.substring(0,auth.indexOf(":"));
							//String pass = auth.substring(auth.indexOf(":") + 1,auth.length());
							
							
							String name = authenticate(nvp);
							//if (UserMgr. (CaimitoConfig.shaftapp).login(CaimitoConfig.shaftapp,name,pass) != null)
							if (name != null)
							{
								//request.setPrincipal(RealmEngine.get().getPrincipal(name));
								//request.setAuthType("BASIC");
							//	request.getSession()
								request.setAttribute(CaimitoConfig.caimitouserreqobj,name);
								return name;
							}
							}catch (Exception e){
								e.printStackTrace();
								//return null;
							}
						}
					}
					if (response != null)
					sendError(request,response,nvp.getString("nonce").trim());
					return null;
					
        }
        
        
      
      protected static boolean isNonceStale(String nonce){
      
        
                    int i = nonce.indexOf(":");
            if (i < 0 || (i + 1) == nonce.length()) {
                return true;
            }
            long nonceTime;
            try {
                nonceTime = Long.parseLong(nonce.substring(0, i));
            } catch (NumberFormatException nfe) {
                return true;
            }
            //String md5clientIpTimeKey = nonce.substring(i + 1);
            long currentTime = System.currentTimeMillis();
            //(currentTime + ":" + nonceTime + ":" + CaimitoConfig.nonceValidity + ":IS NONE STALE ##########################");
            if ((currentTime - nonceTime) > CaimitoConfig.nonceValidity) {
                return true;
            }
            else return false;
//            return true;
        
      }   
        
        
              		public  static void sendError( HttpServletRequest request,HttpServletResponse response,String nonce1)
	{
	    try
	    {
        String nonce = generateNonce(request);
		boolean isn = isNonceStale(nonce1);
		//(nonce1 + " IS NOT STALE " + isn);
        setAuthenticateHeader(request, response,  nonce,
                isn);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	    }
        catch (Exception e)
        {
						e.printStackTrace();

        }
    }
       
       
           protected static String generateNonce(HttpServletRequest request) {

        long currentTime = System.currentTimeMillis();

        
        String ipTimeKey =
            request.getRemoteAddr() + ":" + currentTime + ":" + randomGenerate();

        byte[] buffer;
        synchronized (md5Helper) {
            buffer = md5Helper.digest(
                    ipTimeKey.getBytes(Charset.defaultCharset()));
        }

        return currentTime + ":" + md5Encode(buffer);
    }
    
        private static final char[] hexadecimal =
    {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
     'a', 'b', 'c', 'd', 'e', 'f'};


    // --------------------------------------------------------- Public Methods


    /**
     * Encodes the 128 bit (16 bytes) MD5 into a 32 character String.
     *
     * @param binaryData Array containing the digest
     * @return Encoded MD5, or null if encoding failed
     */
    protected static  String md5Encode( byte[] binaryData ) {

        if (binaryData.length != 16)
            return null;

        char[] buffer = new char[32];

        for (int i=0; i<16; i++) {
            int low = binaryData[i] & 0x0f;
            int high = (binaryData[i] & 0xf0) >> 4;
            buffer[i*2] = hexadecimal[high];
            buffer[i*2 + 1] = hexadecimal[low];
        }

        return new String(buffer);

    }
    
    
        
        protected static String randomGenerate(){
        	
        	String tid = AlphaNumeric.generateRandomAlphNumeric(CaimitoConfig.digestLength);
String dig = CaimitoConfig.defaultDigest;
if (dig != null)
tid = Crypto.digest(tid,dig);
        return tid;	
        }
        
       protected static final String AUTH_HEADER_NAME = "WWW-Authenticate";
     protected static final String QOP = "auth";
    
            protected static void setAuthenticateHeader(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String nonce,
                                         boolean isNonceStale) {

        // Get the realm name
        String realmName = CaimitoConfig.SecurityRealmName;

        String authenticateHeader;
        if (isNonceStale) {
            authenticateHeader = "Digest realm=\"" + realmName + "\", " +
            "qop=\"" + QOP + "\", nonce=\"" + nonce + "\", " + "opaque=\"" +
            randomGenerate() + "\", stale=true";
        } else {
            authenticateHeader = "Digest realm=\"" + realmName + "\", " +
            "qop=\"" + QOP + "\", nonce=\"" + nonce + "\", " + "opaque=\"" +
            randomGenerate() + "\"";
        }


//("authenticateHeader 1 " + authenticateHeader);
        response.setHeader(AUTH_HEADER_NAME, authenticateHeader);

    }
        
        
        
            	    public static  String basicLogin( HttpServletRequest request,
                                 HttpServletResponse response
                                )
        throws CaimitoException {
        
        					String hr = request.getHeader("Authorization");
					if (hr != null)
					{
						if (hr.startsWith("Basic "))
						{

							//hr = SharedMethods.replaceSubstring(hr,"Digest ","");
							hr = hr.trim();
							hr = hr.substring(6,hr.length());
							// username="caimitoadmin", realm="Caimito", nonce="", uri="/", response="2fb52dbbcb7b25fd33840eac3027e9c5"
							NameValuePairs nvp = NameValuePairs.toNameValuePair(hr,"=",",");
							//(nvp + " BASIC 1 " + hr);
							byte[] b = Base64Decoder.fromBase64(hr.getBytes());
							String auth = new String(b);
							String name = auth.substring(0,auth.indexOf(":"));
							String pass = auth.substring(auth.indexOf(":") + 1,auth.length());
							try{
							
							if (UserMgr.getUserMgr(CaimitoConfig.shaftapp).login(CaimitoConfig.shaftapp,name,pass,false) != null)
							{
								//request.setPrincipal(RealmEngine.get().getPrincipal(name));
								//request.setAuthType("BASIC");
							//	request.getSession()
								request.setAttribute(CaimitoConfig.caimitouserreqobj,name);
								return name;
							}
							}catch (Exception e){
								e.printStackTrace();
								//return null;
							}
						}
					}
					if (response != null)
					sendBasicError(request,response);
					return null;
					
        }
        
        		public  static void sendBasicError( HttpServletRequest request,HttpServletResponse response)
	{
	    try
	    {
		/*		
							if (DeploymentDescriptor.isSecureLoginRequired() && !request.getScheme().equalsIgnoreCase("https"))
			{
							String fau = request.getTrimmedRequestURI();
			if (request.getQueryString() != null)
					fau = fau  + "?" + request.getQueryString();
			if (!fau.startsWith("/"))
				fau = "/" + fau;
			response.sendRedirect("https://" + request.getServerName()  + fau);
				return;
			}*/
			String rn = CaimitoConfig.SecurityRealmName;

	    	//("BASIC ERROR SENT " + rn);
				response.setHeader("Content-Type","text/html");
		    	response.setHeader("WWW-Authenticate","Basic realm=\"" + rn + "\"");
				response.sendError(401);
	    }
        catch (Exception e)
        {
						e.printStackTrace();

        }
    }
}

class CaimitoBooleanHolder{
	public boolean value = false;
}
