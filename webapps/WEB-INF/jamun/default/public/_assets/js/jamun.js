var Jamun =  new function(){

this.pagesLoaded = new Array();
this.contextPath = null;
this.isLoggedIn = false;
this.loginLoaded = false;
this.pageList = {};

this.loginSession = null;
this.loginOK = "ok";
this.defaultPage = null;
this.presentPage = null;
this.defaultAction = 'read';
this.properties={};
this.labels={};
this.pageProperties={};
this.cache={};
this.resultsPages={};
this.resultsCache={};

//
this.setPresentResultsPage = function(n){
var po = this.getResultsPageObj(this.presentPage);
po["page"] = n;
}

//
this.getPresentResultsPage = function(){
var po = this.getResultsPageObj(this.presentPage);
return po["page"];
}

//
this.getPresentResultsPages = function(){
var po = this.getResultsPageObj(this.presentPage);
return po["pages"];
}

//
this.getResultsPageObj = function(p){
var rps = this.resultsPages[p];
	if (!this.hasMetaData(p))
{
rps = {};
this.resultsPages[p] = rps;
rps["pages"] = 1;
rps["page"] = 1;
return this.resultsPages[p];
}
var res = Jamun.cache[p];
var nps = res[0]["results.pages"];

if (nps != null)
{
if (rps == null){
rps = {};
rps["page"] = 1;
this.resultsPages[p] = rps;
}
rps["pages"] = nps;
//rps["page"] = 1;
}
return this.resultsPages[p];
}

//
this.batch = function(action,param,thecols,therow,f){

var bl = new Array();
var bm = {};
bm["serverobj"] = this.presentPage;
bm["action"] = action;
bm["params"] = param;
bl.push(bm);

var bom = this.pageProperties[this.presentPage][action + ".related.serverobjs"];

for (var key in bom) {
    var val = bom[key];
    var sob = this.presentPage;
	var i = key.indexOf(".");
	if (i > 0)
{
	sob = key.substring(0,i);
	key = key.substring(i + 1,key.length);
}

bm = {};
bm["serverobj"] = sob;
bm["action"] = action;
var tparam = {};
var tl = thecols.indexOf(val);
var tval = therow[tl];
tparam[key + ".eq"] = tval
bm["params"] = tparam;
bl.push(bm);
}
var js = JSON.stringify(bl);
var turl = this.getServerObjURL("batchmgr","process");
xmlhttp=new XMLHttpRequest();
xmlhttp.onreadystatechange=function()
  {
  if (xmlhttp.readyState==4 && xmlhttp.status==200)
    {
    var data = jQuery.parseJSON(xmlhttp.responseText);
f.process(data);
    }
  } ;
xmlhttp.open("POST",turl,true);
xmlhttp.send(js);
}

//
this.getPresentColumns = function(){
return this.getColumns(Jamun.presentPage);
}

//
this.hasMetaData = function(p){
var res = Jamun.cache[p];
if (typeof res == 'undefined')
return false;
if (typeof res[0] == 'undefined')
return false;
var attrs1 = res[0]["columns"];
if (typeof attrs1 == 'undefined')
return false;
return true;
}

//
this.getColumns = function(p){
if (p == "shaftusermgr")return ["shaftuser"];
var res = Jamun.cache[p];
if (typeof res[0] == 'undefined')
return null;
var attrs1 = res[0]["columns"];
if (typeof attrs1 == 'undefined')
return null;
var attrs = attrs1.slice(0,res[0]["columns"].length - 1);
return attrs;
}


//
this.listValues = function(object){
var vals = new Array();
for (var key in object) {
    vals.push(object[key]);
}
return vals;
}

//
this.removeItemInCache = function(itm){
return this.removeItemFromCache(itm,this.presentPage);

}

//
this.removeItemFromCache = function(itm,cac){
var object = this.cache[cac];
var ct = jQuery.inArray(itm,object);
if (ct > -1){
object.splice(ct,1);
return true;
}
return false;
}

//
this.addCacheItem = function(itm){
this.cache[this.presentPage].push(itm);
}

//
this.addCacheRow = function(row,id){
row.push(id);
this.cache[this.presentPage].push(row);
}

//
this.removeCacheRow = function(id){
var suc = JamunUtil.removeRowInArray(this.cache[this.presentPage],1,this.getPresentColumns().length,id);
var rc = this.resultsCache[this.presentPage];
if (rc != null){
var rsuc = false;
for (var key in rc) {
    var rvc = rc[key];
rsuc = JamunUtil.removeRowInArray(rvc,1,this.getPresentColumns().length,id);
if (rsuc)
suc = rsuc;
}
}
return suc;
}

//
this.updateCacheField = function(key,val,id){
//this.cache[this.presentPage][0].length -1
var row = JamunUtil.getRowInArray(this.cache[this.presentPage],1,this.getPresentColumns().length ,id);
row[key] = val;
}

//
this.getServerObj = function(son ) {
var data = JamunUtil.syncGetJson(Jamun.getServerObjURL(son,"read"));
if (data['status'] != Jamun.loginOK)
{

if ( data['code'] != null  )
{
	if (data['code'] == 'unable_to_authenticate')
	{

	JamunUI.renderLogin();
	}else JamunUI.renderError(data  );
}
else alert(Jamun.getLabel('error')  );
return null;
}
return data["response"];

}

//
this.getServerObjURL = function( si,a){
var url = this.contextPath + '/shaftrest/' + si + '/' + a;
if (this.loginSession)
url = url + "?shaftsessionid=" + this.loginSession["session"];
return url
}

//
this.setSession = function(l){
this.loginSession = l;
this.isLoggedIn = true;
this.checkForRequiredRoles();
//if (this.pagesLoaded.indexOf(this.defaultPage) < 0)
//JamunUI.renderPage(this.defaultPage);

}

//
this.getLabel= function (lab) {
var l = lab.toString();
var lv = this.labels[l];
if (lv != null)return lv;
if (l.lastIndexOf(".title") > 0 && (l.lastIndexOf(".title") + 6 == l.length))
l = l.substring(0,l.length - 6);
l = l.split('_').join(' ');

//l.replace("_"," ");
var c = l.charAt(0);
l = c.toUpperCase() + l.substring(1,l.length);
return l;
}

//
this.checkForRequiredRoles = function(){
var pl = Jamun.properties["pageList"];
var pl2 = {};
for (var key in pl) {
    var r = pl[key];
   //var r = p["roles"];
	if (Object.prototype.toString.call(r) == '[object Array]' && r.length > 0)
{
	if (!Jamun.isLoggedIn)
{
	JamunUI.renderLogin(key);
	return;
}
else if (JamunUtil.anyIn(Jamun.loginSession["roles"],r)){
//if ( p == true || p["loginrequired"] == true)
pl2[key] = true;
//else
//pl2[key] = false;
}
}
else{
if ( r == true )
pl2[key] = true;
else
pl2[key] = false;
}
}

Jamun.pageList = pl2;
//Jamun.properties["pageList"];
Jamun.defaultPage = JamunUtil.getHashKey(Jamun.pageList,0);
//alert(Jamun.defaultPage);
JamunUI.initPages();
}

//
this.init = function(){
var p = window.location.toString();
var i = p.indexOf("//");
p = p.substring(i+2,p.length);
i = p.indexOf("/");
p = p.substring(i + 1,p.length);
i = p.lastIndexOf("/");
if (i > 0){
p = p.substring(0,i);
}
Jamun.contextPath = "/" +  p;
if (this.contextPath == "/")
Jamun.contextPath = "";

return $.getJSON("properties/jamun.json",
  null, function(data) {
var mc = data['mobile.clients'];
for (var ct = 0; ct < mc.length;ct ++) 
{
    if (navigator.userAgent.indexOf(mc[ct]) > -1)
{
window.location = data["mobile.redirect"];
return;
}
}
Jamun.properties = data;
//checkForRequiredRoles();
var ln = navigator.language;
if (ln == null)
{
ln = window.navigator.userLanguage;
}
ln = ln.toLowerCase();
var i = ln.indexOf("-");
var ln2 = ln.substring(0,i);
$.when($.getJSON('i18n/default.json')).done(function(data){
JamunUtil.mergeHash(data,Jamun.labels);
$.when($.getJSON('i18n/' + ln2 + '.json')).done(function(data2){
JamunUtil.mergeHash(data2,Jamun.labels);

	$.when($.getJSON('i18n/' + ln + '.json')).done(function(data3){
	JamunUtil.mergeHash(data3,Jamun.labels);
	Jamun.checkForRequiredRoles();
	//JamunUI.initPages();
	}).fail(function(data3){
		Jamun.checkForRequiredRoles();
		//JamunUI.initPages();
	});


}).fail(function(data2){

	$.when($.getJSON('i18n/' + ln + '.json')).done(function(data3){
	JamunUtil.mergeHash(data3,Jamun.labels);
	Jamun.checkForRequiredRoles();
	//JamunUI.initPages();
	}).fail(function(data3){
		Jamun.checkForRequiredRoles();
		//JamunUI.initPages();
	});
});
});
});

}


};

