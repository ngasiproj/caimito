var JamunUtil =  new function(){

//
this.indexOfArray = function(object,itm){
return jQuery.inArray(itm,object);
}

//
this.anyIn = function(object,object2){

for (var ct1 = 0;ct1 < object.length;ct1++ ) {
var r1 = object[ct1];	
if (this.indexOfArray(object2,r1) > -1)
return true;

}
return false;
}

//
this.mergeHash = function(object,object2){
for (var key in object) {
    object2[key] = object[key];
}
}

//
this.getMapFromLists = function(object,object2,sindex){
var na = {};
for (var ct1 = sindex;ct1 < object.length;ct1++ ) {
	
    na[object[ct1]] = object2[ct1];
}
return na;
}

//
this.getMapFromArrays = function(object,object2,col){
var na = {};
for (var ct1 = 0;ct1 < object.length;ct1++ ) {
var r1 = object[ct1];	
if (typeof r1 == 'array' ){
	if (r1[r1.length -1] != "shaftid")	
    na[r1[col]] = object2[ct1][col];
}
else
na[object[ct1]] = object2[ct1];

}
return na;
}



//
this.getHashKey = function(object,index){
var ct = 0;
for (var key in object) {
    if (ct == index)return key;
	ct = ct + 1;
}
return null;
}

//
this.cloneArray = function(object){
var na = new Array();
for (var ct1 = 0;ct1 < object.length;ct1++ ) {
    na.push(object[ct1]);
}
return na;
}

//
this.locateRowInArray = function(object,index,rowindex,itm){
var ct = 0;
for (ct = index;ct < object.length;ct++ ) {
    if (object[ct][rowindex].toString() == itm.toString())
	return ct;
}
return -1;
}

//
this.getRowInArray = function(object,index,rowindex,itm){
var ct = this.locateRowInArray(object,index,rowindex,itm);
if (ct > -1)return object[ct];
return null;
}

//
this.removeRowInArray = function(object,index,rowindex,itm){
var retv = false;
while(true){
var ct = this.locateRowInArray(object,index,rowindex,itm);

if (ct > -1){
object.splice(ct,1);
retv = true;
}
else break;
}
return retv;
}

//
this.syncGetJson = function(url ) {

        var ret;

        $.ajax({
            type: 'GET',
            url: url,
            dataType: 'json',
            complete: function(response) {
               ret = eval('(' + response.responseText + ')');

            },
            async: false
        });


        return ret;
    }



};
