function ProcessDeleteResult(ansel,tsData){
this.ansel = ansel;
this.tsData = tsData;
this.tgrid = JamunUI.grids[Jamun.presentPage];

this.process  = function(data){

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
return;
}


var res = data['response'];
if (res != true && res[0] != true){
alert(Jamun.getLabel('Error') + ":" + res );
return;
}
this.tgrid.fnDeleteRow( this.ansel );
if (!Jamun.hasMetaData(Jamun.presentPage))
Jamun.removeItemInCache(this.tsData[this.tsData.length - 1]);
else
Jamun.removeCacheRow(this.tsData[this.tsData.length - 1]);
JamunUI.updateRelatedViewsAfterDelete(this.tsData);
}
}

var JamunUI = new function(){

this.grids = {} ;
this.pagesInit = false;
this.viewsCache = {} ;

//
this.selectPage = function(){
var pps = parseInt($("#" + Jamun.presentPage + "rpageselect").val());
$("#" + Jamun.presentPage).html("");
Jamun.setPresentResultsPage(pps);
var tpg = jQuery.inArray(Jamun.presentPage,Jamun.pagesLoaded);
if (tpg > -1){
Jamun.pagesLoaded.splice(tpg,1);
}
this.renderPage(Jamun.presentPage);
}

//
this.setUpPageSelect = function(){
var pps = Jamun.getPresentResultsPages();
if (pps > 1){
var ppn = Jamun.getPresentResultsPage();
$("#" + Jamun.presentPage + "rpagesdiv").show();
var rpgsel = "";
for (var ppsct = 1; ppsct <= pps;ppsct++) 
{
rpgsel += '<option value="' + ppsct + '"';

if (ppsct == ppn){
rpgsel +=  ' selected ' ;
}

  rpgsel +=  '>' + ppsct + '</option>';

}
$("#" + Jamun.presentPage + "rpageselect").append(rpgsel);

}
}

//
this.updateRelatedViewsAfterDelete = function(therow){
var bom = Jamun.pageProperties[Jamun.presentPage]["delete.related.serverobjs"];
if (bom == null)return;
var thecols = Jamun.getPresentColumns();
for (var key in bom) {
    var val = bom[key];
    var sob = this.presentPage;
	var i = key.indexOf(".");
	if (i > 0)
{
	sob = key.substring(0,i);
	key = key.substring(i + 1,key.length);
}
var tc = Jamun.cache[sob];
if (tc != null && tc.length > 0){
var tl = JamunUtil.indexOfArray(thecols,val);
var tval = therow[tl];
var delcols = Jamun.getColumns(sob);
var deltl = JamunUtil.indexOfArray(delcols,key);

if (sob != "shaftusermgr"){

var tgrws = JamunUI.grids[sob].$('tr')
for (var rc = 0; rc < tgrws.length;rc++){
var anSelected = tgrws[rc];
var sdData = JamunUI.grids[sob].fnGetData( anSelected );

var pdata = JamunUtil.getRowInArray(Jamun.cache[sob],1,delcols.length,sdData[delcols.length]);
if (pdata[deltl] == tval)
JamunUI.grids[sob].fnDeleteRow( anSelected);
}
JamunUtil.removeRowInArray(Jamun.cache[sob],1,deltl,tval);
}
else{
var tgrws = JamunUI.grids[sob].$('tr')
for (var rc = 0; rc < tgrws.length;rc++){
var anSelected = tgrws[rc];
var sdData = JamunUI.grids[sob].fnGetData( anSelected );

var pdata = JamunUtil.getRowInArray(Jamun.cache[sob],0,delcols.length,sdData[delcols.length]);
if (pdata[deltl] == tval)
JamunUI.grids[sob].fnDeleteRow( anSelected);
}

Jamun.removeItemFromCache(tval,"shaftusermgr");
}
}
}
}

//
this.renderError = function(data){
if (Jamun.properties["deployment.mode"] == "development" && data["stacktrace"])
{
alert(Jamun.getLabel(data['code']) + ":" + data["stacktrace"]);	
}
else
alert(Jamun.getLabel(data['code']));
}

//
this.fnGetSelected = function( gridLocal )
{
    return gridLocal.$('tr.row_selected');
}

//
this.getInputValues = function(form , attrs){
var vals = {};
 for (i=0; i<attrs.length; i++)
  {
    vals[attrs[i]] = $('#' + attrs[i], $('#' + form)).val();
  }
return vals;
}

//
this.appendHeaderToGrid = function(grid , heading){
var tr = "<tr>";
 for (i=0; i<heading.length; i++)
  {
    tr = tr + "<th>" + Jamun.getLabel(heading[i]) + "</th>";
  }
 tr = tr + "</tr>";
$('#' + grid + " thead").append(tr);

}

//
this.formatField = function( cn,val){
var pprop = Jamun.pageProperties[Jamun.presentPage];
if (pprop != null)
{

		var cp = pprop[cn + ".input"];

		if (cp != null)
		{
			var opts = cp["options"];
			if (opts != null)
			{
				//var prv = rdata[ct1];
				var pal = opts[val];
				if (pal != null){
				//rdata = JamunUtil.cloneArray(rdata);
				 return Jamun.getLabel(pal);

				}
			}
		}
}

return val;
}

//
this.formatRow = function(pp , rdata){
var pprop = Jamun.pageProperties[pp];
if (pprop != null)
{
	var cns = Jamun.getPresentColumns();
	if (typeof cns != 'undefined' && cns != null)
{
	for ( var ct1 = 0;ct1 < cns.length;ct1++)
	{
		var cn = cns[ct1];
		var cp = pprop[cn + ".input"];

		if (cp != null)
		{
			var opts = cp["options"];
			if (opts != null)
			{
				var prv = rdata[ct1];
				var pal = opts[prv];
				if (pal != null){
				rdata = JamunUtil.cloneArray(rdata);
				 rdata[ct1] = Jamun.getLabel(pal);

				}
			}
		}
	}
}
}
return rdata;
}

//
this.appendRowToGrid = function(pp , rdata,id){
var grid = pp + "grid";
rdata = this.formatRow(pp,rdata);
var addId = $('#' + grid).dataTable().fnAddData(rdata);
var theNode = $('#' + grid).dataTable().fnSettings().aoData[addId[0]].nTr;
theNode.setAttribute('id',id);
}

//
this.renderLogin = function(){
Jamun.pagesLoaded = new Array();
Jamun.cache = {};
Jamun.resultsCache={};
Jamun.resultsPages={};
this.viewsCache={};
Jamun.loginSession = null;
Jamun.isLoggedIn = false;
$('#nav_submenu').slideUp(100);
$("#login_nav_login").show();
$("#login_nav_options").hide();
		var name = $( "#name" ),
			password = $( "#password" ),
			allFields = $( [] ).add( name ).add( password ),
			tips = $( ".validateTips" );

		function updateTips( t ) {
			tips
				.text( t )
				.addClass( "ui-state-highlight" );
			setTimeout(function() {
				tips.removeClass( "ui-state-highlight", 1500 );
			}, 500 );
		}

		function checkLength( o, n, min, max ) {
			if ( o.val().length > max || o.val().length < min ) {
				o.addClass( "ui-state-error" );
				updateTips( "Length of " + n + " must be between " +
					min + " and " + max + "." );
				return false;
			} else {
				return true;
			}
		}

		function checkRegexp( o, regexp, n ) {
			if ( !( regexp.test( o.val() ) ) ) {
				o.addClass( "ui-state-error" );
				updateTips( n );
				return false;
			} else {
				return true;
			}
		}


var NewLoginDialog = $('<div id="login-dialog-form">' +
'<p class="validateTips">&nbsp;&nbsp;<image src="_assets/images/lock.png"/>&nbsp;&nbsp;&nbsp;' + Jamun.getLabel("fields_required") + '</p>' + 
	'<form>' +
	'<fieldset>' +
		'<label for="name">' + Jamun.getLabel("User") + '</label>' +
		'<input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" /\>' +
		'<label for="password">' + Jamun.getLabel("Password") + '</label>' +
		'<input type="password" name="password" id="password"  class="text ui-widget-content ui-corner-all" /\>' +
	'</fieldset>' +
	'</form>' +
'</div>'
);
        NewLoginDialog.dialog({
	open: function(event, ui) { $(this).parent().children().children('.ui-dialog-titlebar-close').hide();},
	    autoOpen: true,
            modal: true,
	    width: 350,
            title: Jamun.getLabel("Login"),
            show: 'clip',
            hide: 'clip',
            buttons: [
                {text: Jamun.getLabel("Login"), click: function() {
//begin login
name = $( "#name" ),
			password = $( "#password" ),
			allFields = $( [] ).add( name ).add( password ),
			tips = $( ".validateTips" );
					var bValid = true;
					allFields.removeClass( "ui-state-error" );

					bValid = bValid && checkLength( name, "username", 3, 16 );
					bValid = bValid && checkLength( password, "password", 3, 16 );
					bValid = bValid && checkRegexp( name, /^[a-z]([0-9a-z_])+$/i, "Username may consist of a-z, 0-9, underscores, begin with a letter." );
					bValid = bValid && checkRegexp( password, /^([0-9a-zA-Z])+$/, "Password field only allow : a-z 0-9" );

					if ( bValid ) {
						//login 

var jqxhr = $.getJSON(Jamun.contextPath + "/shaftrest/shaftusermgr/login",
  {
    shaftuser: name.val(),
    shaftpassword: password.val()
  }, function(data) {

if (data['status'] != Jamun.loginOK)
{
updateTips( Jamun.getLabel('login_error') );
return;
}

$('#login-dialog-form').dialog("close");
$('#login-dialog-form').remove();
Jamun.setSession(data['response']);
$("#nav_user").html(name.val());

$("#login_nav_login").hide();
$("#login_nav_options").show();


})
.success(function() { 

 })
.error(function() { alert("error"); })
.complete(function() { 

});

jqxhr.complete(function(){ 

});

}

//end login


}},
                {text: Jamun.getLabel("Cancel"), click: function() {
$('#login-dialog-form').dialog("close");
$('#login-dialog-form').remove();


}}
            ]
        });


}

//
this.renderPage = function(pg){
if (!Jamun.isLoggedIn){
if (Jamun.pageList[pg])
{
this.renderLogin();
return;
}
}
var oldpage = Jamun.presentPage;
$('#' + Jamun.presentPage).hide();
$('#' + pg).show(
);
Jamun.presentPage = pg;
if ($( "#login-dialog-form" ))
$( "#login-dialog-form" ).dialog( "close" );
if (jQuery.inArray(pg,Jamun.pagesLoaded) > -1){
$("#page_title").html(Jamun.getLabel(Jamun.presentPage +  ".title"));
$( 'title' ).html ( Jamun.getLabel(Jamun.presentPage +   ".title") );
return;
}

//load view from cache
var vfc = JamunUI.viewsCache[Jamun.presentPage];
if (vfc != null)
{
$('#' + pg).html(vfc);
Jamun.pagesLoaded.push(pg);
$("#page_title").html(Jamun.getLabel(Jamun.presentPage +  ".title"));
$( 'title' ).html ( Jamun.getLabel(Jamun.presentPage +   ".title") );
return;
}
//end load view from cache

var url = Jamun.contextPath + '/views/' + pg + ".html";
	$.when($.getJSON('properties/' + pg + '.json')).done(function(data3){
	Jamun.pageProperties[pg] = data3;
//load view
//$('#' + pg).load(url,function(response,status,xhr){
$.get(url, function(data) {
JamunUI.viewsCache[Jamun.presentPage] = data;
$('#' + pg).html(data);


/**if (status == "error")
{

if ( oldpage != null){
$('#' + Jamun.presentPage).hide();
$('#' + oldpage).show();
Jamun.presentPage = oldpage;
}
alert(Jamun.getLabel("not_found") );
}
else{*/
	Jamun.pagesLoaded.push(pg);
	$("#page_title").html(Jamun.getLabel(Jamun.presentPage +  ".title"));
	$( 'title' ).html ( Jamun.getLabel(Jamun.presentPage +   ".title") );
	//}
});

//end load view

}).fail(function(data3){
	Jamun.pageProperties[pg] = {};
//$('#' + pg).html("");
$.get(url, function(data) {
JamunUI.viewsCache[Jamun.presentPage] = data;
$('#' + pg).html(data);
/*$('#' + pg).load(url,function(response,status,xhr){
if (status == "error")
{
if ( oldpage != null){
$('#' + Jamun.presentPage).hide();
$('#' + oldpage).show();
Jamun.presentPage = oldpage;
}
alert(Jamun.getLabel("not_found") );
}
else{*/
	Jamun.pagesLoaded.push(pg);
	$("#page_title").html(Jamun.getLabel(Jamun.presentPage +  ".title"));
	$( 'title' ).html ( Jamun.getLabel(Jamun.presentPage +   ".title") );
	//}
});
});


}

//
this.createSelect = function(id,options,selected,disabled){
var dist = "";
if (disabled)
	dist = ' disabled="disabled" ';


var newfields = '<select' + dist + ' id="' + id + '">';
for (var key in options) 
{
  newfields += '<option value="' + key + '"';

if (selected != null && selected.toString() == key.toString()){
newfields +=  ' selected ' ;
}

  newfields +=  '>' + Jamun.getLabel(options[key]) + '</option>';

}
newfields += '</select>';

return newfields;
}

//passwdReset
this.passwdReset = function() {


   var NewUpdateDialog =  $('<div id="jamunPasswdResetDialog"><p>' + Jamun.getLabel("confirm_update") + '</p>' +
	'<form>' +
	'<fieldset>' +

function(){
var editfields = "";

		editfields += '<label for="passwdreset1">' + Jamun.getLabel("New_Password") + '</label>' +
		'<input type="password" value="" name="passwdreset1" id="passwdreset1" class="text ui-widget-content ui-corner-all" /\>' ;

		editfields += '<label for="passwdreset2">' + Jamun.getLabel("New_Password") + '</label>' +
		'<input type="password" value="" name="passwdreset2" id="passwdreset2" class="text ui-widget-content ui-corner-all" /\>' ;

return editfields;
}() + 

	'</fieldset>' +
	'</form>' +
'</div>'
);
   NewUpdateDialog.dialog({
	open: function(event, ui) { $(this).parent().children().children('.ui-dialog-titlebar-close').hide();},
	
    autoOpen: true,
            modal: true,
	    width: 450,
            title: Jamun.getLabel("Password_Reset"),
            show: 'clip',
            hide: 'clip',
            buttons: [
                {text: Jamun.getLabel("Update"), click: function() {
if ($('#passwdreset1', $('#jamunPasswdResetDialog')).val() != $('#passwdreset2', $('#jamunPasswdResetDialog')).val()){
JamunUI.showAlert("does_not_match");
return;
}
var url = Jamun.getServerObjURL("shaftusermgr",'update');
$.getJSON(url,{
"shaftpassword.new":$('#passwdreset1', $('#jamunPasswdResetDialog')).val()
}
, function(data) {

if (data['status'] != Jamun.loginOK)
{

$('#jamunPasswdResetDialog').dialog("close");
$('#jamunPasswdResetDialog').remove();
JamunUI.renderLogin();
return;
}

var res = data['response'];

if (res != true){
if ( data['code'] ){
if ( data['code'] == 'unable_to_authenticate'){
$('#jamunPasswdResetDialog').dialog("close");
$('#jamunPasswdResetDialog').remove();
JamunUI.renderLogin();
}
else
alert(Jamun.getLabel(data['code']));
}
else
alert(Jamun.getLabel('error')  );

return;
}




$('#jamunPasswdResetDialog').dialog("close");
$('#jamunPasswdResetDialog').remove();
});

}},
                {text: Jamun.getLabel("Cancel"), click: function() {
$(this).dialog("close");
$('#jamunPasswdResetDialog').remove();
}}
            ]
        });

    
    }
//end passwd reset

//
this.showAlert = function(l){
alert(Jamun.getLabel(l));
}

//
this.logout = function(){
var p = window.location.toString();
window.location = p;
}

//
this.initPages = function()
{
if (this.pagesInit){
JamunUI.renderPage(Jamun.defaultPage);
return;
}
this.pagesInit = true;
$("#login_nav_options").hide();
$("#login_nav_login").html(Jamun.getLabel("Login"));
$("#nav_logout").html(Jamun.getLabel("Logout"));
$("#nav_passwdreset").html(Jamun.getLabel("Password"));
$("#login_nav_login").click(function(){
JamunUI.renderLogin();
}
);

$("#nav_logout").click(function(){
//JamunUI.renderLogin();
JamunUI.logout();
}
);

$("#nav_passwdreset").click(function(){
JamunUI.passwdReset();
}
);

for (var key in Jamun.pageList) 
{

$("#pagebuttons").append('<button id="Button' + key + '" onclick="JamunUI.renderPage(\'' + key + '\')">' + Jamun.getLabel(key) + '</button>');
$("#pages").append('<div id="' + key + '"> </div>');

}
this.renderPage(Jamun.defaultPage);
return;

}

};
