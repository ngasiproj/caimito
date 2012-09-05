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

package ngasi.caimito.usermgr;
import tools.util.*;
import java.io.*;
import java.util.*;

import ngasi.caimito.CaimitoConfig;

import org.shaft.server.auth.RealmMgr;
import org.shaft.server.auth.UserMgr;

public abstract class CaimitoCAFAOUserMgr extends CaimitoUserMgr{

	public boolean isAdminOrOwnerOf(String u,String o)throws Exception{
		return (u != null && o != null);
	}
	
	public String  getOwner(String u)throws Exception{
		return u;
	}
	
	public String  getMD5Digest(String u)throws Exception{
		return UserMgr.getUserMgr(CaimitoConfig.shaftapp).getMD5Digest(CaimitoConfig.shaftapp,u);  
	}
	public boolean isAdmin(String u)throws Exception{
		return (u != null);
		}
	

}
