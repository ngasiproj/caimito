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

package ngasi.caimito;
import tools.util.*;
import java.io.*;
import java.util.*;

public class CaimitoConfig{

	    public static boolean protectedRoot = true;
	    public static long nonceValidity = 5 * 60 * 1000;

		public static String defaultDigest = "SHA";
		public static int digestLength = 12;

		public static String caimitoroot = null;
	public static String caimitoconfigd = "WEB-INF/caimito/";
public static String caimitouserreqobj = "caimitouserreqobj";
	static NameValuePairs defconfigmodnvp = null;
	public static String SecurityRealmName = "Caimito";
	public static String shaftapp = "ca";
	public static String resource = "file";
	public static String cachedir = System.getProperty("java.io.tmpdir") + File.separator;
	public static boolean cacheable = true;
	public static String serverinfo = "Caimito";
	public static String CAIMITO_CSS = null;
<<<<<<< HEAD
	public static String version = "0.3";
=======
	public static String version = "0.2";
>>>>>>> ea374546f5d7f32e981a6c3c23786426285d1110
	public static void init()throws CaimitoException{
			if (! new File(getConfigFile()).exists())
			{
				CaimitoException.throwException("*** FATAL CAIMITO ERROR. " + getConfigFile() + " does not exists. Copy " + getConfigFile() + ".sample to " + getConfigFile());
			}
		try{

			//cp /usr/caimito/webapps/WEB-INF/caimito/config.properties.sample /usr/caimito/webapps/WEB-INF/caimito/config.properties

			SecurityRealmName = getConfig().getString("SecurityRealmName");
			resource = getConfig().getString("resource");
			protectedRoot = getConfig().getBoolean("protectedRoot");
			cacheable = getConfig().getBoolean("cacheable");
			shaftapp = getConfig().getString("shaft.app");
			serverinfo = getConfig().getString("serverinfo");
			CAIMITO_CSS = getConfig().getString("CAIMITO_CSS");

			if (getConfig().get("cachedir") != null)
				cachedir = getConfig().getString("cachedir");
			//(caimitoroot + caimitoconfigd + "config.properties" + " dfd getConfig " + getConfig());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

		public static String getConfigFile(){
		return caimitoroot + caimitoconfigd + "config.properties";
	}

	public static String getTempDir(){
		return System.getProperty("java.io.tmpdir") + File.separator;
	}

		public static NameValuePairs getConfig()throws CaimitoException{
		if (defconfigmodnvp == null){
try{
			defconfigmodnvp = new NameValuePairs(getConfigFile());


			}catch (Exception e){
				e.printStackTrace();
				CaimitoException.throwException(e);
			}

					}
		return defconfigmodnvp;
	}

}