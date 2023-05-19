function AdamaTree(){function i(e){if(Array.isArray(e))for(var t=[],n=0;n<e.length;n++)t.push(i(e[n]));else{if("object"!=typeof e)return e;t={};for(key in e)"#"!=key[0]&&"__key"!=key&&"@o"!=key&&(t[key]=i(e[key]))}return t}function T(e,t){if(0==e.length)return e;for(var n=[],i=0;i<e.length;i++)t in e[i]&&n.push(e[i][t]);return n}function O(e,t,n){for(var i=0;i<e.length;i++){var r=e[i];if("@e"in r)for(var o=r["@e"],a=0;a<o.length;a++)n.push(function(){this.f(this.v)}.bind({f:o[a],v:t}))}}function N(e,t,n,i){for(var r in t){var o="#"+r,a=e[r],c=t[r];if(null==c)Array.isArray(a)&&(o in e&&P(T(n,o)),delete e[o]),P(T(n,r)),delete e[r];else if("object"!=typeof c||Array.isArray(c))e[r]=c,O(T(n,r),c,i);else if(Array.isArray(a)||"@o"in c||"@s"in c){r in e||(e[r]=[]),o in e||(e[o]={},"@o"in c&&(e[o]["@o"]=!0),e[o].__key=r);var s,u=e[o],d=T(n,r),l=null,f=null,h={};for(s in c){var p=c[s];if("@o"==s)l=p;else if("@s"==s)f=p;else if(null==p)s in u&&(h[s]=function(e){P(T(d,e)),delete u[e],R(d,e);for(var t=d,n=e,i=0;i<t.length;i++){var r=t[i];if("-"in r){r=r["-"];if("@e"in r)for(var o=r["@e"],a=0;a<o.length;a++)o[a](n)}}});else{if(!(s in u)){b=g=w=y=m=void 0;for(var _=d,v=s,m=0;m<_.length;m++){var y=_[m];if("+"in y){var w=y["+"];if("@e"in w)for(var g=w["@e"],b=0;b<g.length;b++)L(y,g[b](v),v)}}}p=c[s];"object"==typeof p?(s in u||(u[s]={},u[s].__key=s),h[s]=function(e){N(u[e],c[e],T(d,e),i),O(T(d,e),u[e],i)}):(u[s]=p,h[s]=function(e){O(T(d,e),u[e],i)})}}var x=e[r],k=[];if(null!=l){for(var S=[],A=[],C=0;C<l.length;C++){var q=l[C],D=typeof q;if("string"==D||"number"==D)S.push(u[q]),A.push(""+q);else for(var D=q[0],I=q[1],E=D;E<=I;E++)S.push(x[E]),A.push(x[E].__key)}k.push(function(){O(T(d,"~"),A,i)}),e[r]=S}else if(null!=f){for(S=[],A=[],C=0;C<f;C++)S[C]=x[C],A.push(""+C);k.push(function(){O(T(d,"~"),A,i)}),e[r]=S}for(s in h)h[s](s);for(C=0;C<k.length;C++)k[C]();O(d,e[r],i)}else{r in e&&"object"==typeof e[r]||(e[r]={});a=T(n,r);N(e[r],c,a,i),O(a,e[r],i)}}O(n,e,i)}function r(e){for(var t=0;t<e.length;t++)e[t]()}function o(e,t){if(Array.isArray(e))for(var n=0;n<e.length;n++)o(e[n],t);else if("object"==typeof e)for(var i in e)"@e"==i?o(e[i],t):(i in t||(t[i]={}),o(e[i],t[i]));else"function"==typeof e&&("@e"in t?t["@e"].push(e):t["@e"]=[e])}function u(e){var t,n={};for(t in e)if("#"!=t[0]&&"@o"!=t&&"__key"!=t){var i=e[t];if("#"+t in e){var r=e["#"+t],o={};if("@o"in r){for(var a=[],c=0;c<i.length;c++){var s=i[c];o[s.__key]=u(s),a.push(i[c].__key)}o["@o"]=a}else{for(c=0;c<i.length;c++)o[""+c]=u(r[c]);o["@s"]=i.length}n[t]=o}else n[t]="object"==typeof i?u(i):i}return n}var a=0,c={},s={},L=(this.nuke=function(){s={}},this.copy=function(){return i(c)},this.str=function(){return JSON.stringify(c)},function(e,t,n){if(Array.isArray(t))for(var i=0;i<t.length;i++)L(e,t[i],n);else if("function"==typeof t)n in e||(e[n]={}),"@e"in(o=e[n])?o["@e"].push(t):o["@e"]=[t];else if("object"==typeof t){n in e||(e[n]={});var r,o=e[n];for(r in t)"@e"==r?L(e,t[r],n):L(o,t[r],r)}}),P=function(e){if(Array.isArray(e))for(var t=e.length,n=0;n<t;n++)P(e[n]);else if("object"==typeof e)for(var i in e){var r=e[i];if("@e"==i)for(var o=0;o<r.length;o++)r[o](null);else P(r)}},R=function(e,t){if(Array.isArray(e))for(var n=e.length,i=0;i<n;i++)R(e[i],t);else"object"==typeof e&&t in e&&delete e[t]};this.update=function(e){var t,n=[];for(t in s)n.push(s[t]);var i=[];N(c,e,n,i),r(i)};this.subscribe=function(e){var t={},e=(o(e,t),u(c)),n=[],i=(N({},e,[t],n),r(n),""+a++);return s[i]=t,function(){delete s[i]}}}class WebSocketAdamaConnection{constructor(e){this.backoff=1,this.host=e,this.url="wss://"+e+"/~s",this.connected=!1,this.assets=!0,this.dead=!1,this.maximum_backoff=5e3,this.reset_backoff=1e3,this.socket=null,this.onstatuschange=function(e){},this.onping=function(e,t){},this.scheduled=!1,this.callbacks=new Map,this.nextId=0,this.onreconnect=new Map}stop(){this.dead=!0,null!==this.socket&&this.socket.close()}_retry(){var e,t;this.socket=null,this.connected&&(this.connected=!1,this.onstatuschange(!1)),this.callbacks.clear(),this.dead||this.scheduled||(e=!1,this.backoff+=Math.random()*this.backoff,this.backoff>this.maximum_backoff&&(this.backoff=this.maximum_backoff,e=!0),this.scheduled=!0,t=this,setTimeout(function(){t.start()},this.backoff),e&&(this.backoff/=2))}start(){var n=this;this.scheduled=!1,this.dead=!1,this.socket=new WebSocket(this.url),this.socket.onmessage=function(e){var t,e=JSON.parse(e.data);{if(!("ping"in e))return"status"in e?"connected"!=e.status?(n.socket.close(),n.socket=null,n.backoff=n.reset_backoff,void n._retry()):(n.backoff=1,n.connected=!0,n.assets=e.assets,n.onstatuschange(!0),n._reconnect(),void n.ConfigureMakeOrGetAssetKey({success:function(e){try{var t=new XMLHttpRequest;t.open("GET","https://"+n.host+"/~p"+e.assetKey,!0),t.withCredentials=!0,t.send()}catch(e){console.log(e)}},failure:function(){}})):void("failure"in e?n.callbacks.has(e.failure)&&(t=n.callbacks.get(e.failure))&&(n.callbacks.delete(e.failure),t(e)):"deliver"in e&&n.callbacks.has(e.deliver)&&(t=n.callbacks.get(e.deliver))&&(e.done&&n.callbacks.delete(e.deliver),t(e)));n.onping(e.ping,e.latency),e.pong=(new Date).getTime()/1e3,n.socket.send(JSON.stringify(e))}},this.socket.onclose=function(e){n._retry()},this.socket.onerror=function(e){n._retry()}}_write(e,t){this.connected?(this.callbacks.set(e.id,t),this.socket.send(JSON.stringify(e))):t({failure:600,reason:9999})}async wait_connected(){var n,i;return this.connected?new Promise(function(e){e(!0)}):(i=(n=this).onstatuschange,new Promise(function(t){n.onstatuschange=function(e){i(e),e&&(t(!0),n.onstatuschange=i)}}))}_reconnect(){this.onreconnect.forEach(function(e,t){e.__retry()})}__execute_rr(t){var n=this;return t.first=!0,n._write(t.request,function(e){t.first&&(t.first=!1,"failure"in e?"failure"in t.responder&&t.responder.failure(e.reason):"success"in t.responder&&t.responder.success(e.response)),n.onreconnect.delete(t.id)}),n.onreconnect.set(t.id,t),t.__retry=function(){n.__execute_rr(t)},t}__execute_stream(t){var n=this;return n._write(t.request,function(e){"failure"in e?("failure"in t.responder&&t.responder.failure(e.reason),n.onreconnect.delete(t.id)):(e.response&&"next"in t.responder&&t.responder.next(e.response),e.done&&("complete"in t.responder&&t.responder.complete(),n.onreconnect.delete(t.id)))}),n.onreconnect.set(t.id,t),t.__retry=function(){n.__execute_stream(t)},t}__id(){return this.nextId++,this.nextId}InitSetupAccount(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"init/setup-account",id:n,email:e}})}InitConvertGoogleUser(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"init/convert-google-user",id:n,"access-token":e}})}InitCompleteAccount(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"init/complete-account",id:r,email:e,revoke:t,code:n}})}AccountSetPassword(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"account/set-password",id:i,identity:e,password:t}})}AccountGetPaymentPlan(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"account/get-payment-plan",id:n,identity:e}})}AccountLogin(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"account/login",id:i,email:e,password:t}})}Probe(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"probe",id:n,identity:e}})}AuthorityCreate(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"authority/create",id:n,identity:e}})}AuthoritySet(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"authority/set",id:r,identity:e,authority:t,"key-store":n}})}AuthorityGet(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"authority/get",id:i,identity:e,authority:t}})}AuthorityList(e,t){var n=this.__id();return this.__execute_stream({id:n,responder:t,request:{method:"authority/list",id:n,identity:e}})}AuthorityDestroy(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"authority/destroy",id:i,identity:e,authority:t}})}SpaceCreate(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"space/create",id:r,identity:e,space:t,template:n}})}SpaceGenerateKey(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/generate-key",id:i,identity:e,space:t}})}SpaceUsage(e,t,n,i){var r=this.__id();return this.__execute_stream({id:r,responder:i,request:{method:"space/usage",id:r,identity:e,space:t,limit:n}})}SpaceGet(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/get",id:i,identity:e,space:t}})}SpaceSet(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"space/set",id:r,identity:e,space:t,plan:n}})}SpaceRedeployKick(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/redeploy-kick",id:i,identity:e,space:t}})}SpaceSetRxhtml(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"space/set-rxhtml",id:r,identity:e,space:t,rxhtml:n}})}SpaceGetRxhtml(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/get-rxhtml",id:i,identity:e,space:t}})}SpaceDelete(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/delete",id:i,identity:e,space:t}})}SpaceSetRole(e,t,n,i,r){var o=this.__id();return this.__execute_rr({id:o,responder:r,request:{method:"space/set-role",id:o,identity:e,space:t,email:n,role:i}})}SpaceReflect(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"space/reflect",id:r,identity:e,space:t,key:n}})}SpaceList(e,t,n,i){var r=this.__id();return this.__execute_stream({id:r,responder:i,request:{method:"space/list",id:r,identity:e,marker:t,limit:n}})}DomainMap(e,t,n,i,r){var o=this.__id();return this.__execute_rr({id:o,responder:r,request:{method:"domain/map",id:o,identity:e,domain:t,space:n,certificate:i}})}DomainMapDocument(e,t,n,i,r,o){var a=this.__id();return this.__execute_rr({id:a,responder:o,request:{method:"domain/map-document",id:a,identity:e,domain:t,space:n,key:i,certificate:r}})}DomainList(e,t){var n=this.__id();return this.__execute_stream({id:n,responder:t,request:{method:"domain/list",id:n,identity:e}})}DomainUnmap(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"domain/unmap",id:i,identity:e,domain:t}})}DomainGet(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"domain/get",id:i,identity:e,domain:t}})}DocumentAuthorize(e,t,n,i,r){var o=this.__id();return this.__execute_rr({id:o,responder:r,request:{method:"document/authorize",id:o,space:e,key:t,username:n,password:i}})}DocumentCreate(e,t,n,i,r,o){var a=this.__id();return this.__execute_rr({id:a,responder:o,request:{method:"document/create",id:a,identity:e,space:t,key:n,entropy:i,arg:r}})}DocumentDelete(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"document/delete",id:r,identity:e,space:t,key:n}})}DocumentList(e,t,n,i,r){var o=this.__id();return this.__execute_stream({id:o,responder:r,request:{method:"document/list",id:o,identity:e,space:t,marker:n,limit:i}})}MessageDirectSend(e,t,n,i,r,o,a){var c=this.__id();return this.__execute_rr({id:c,responder:a,request:{method:"message/direct-send",id:c,identity:e,space:t,key:n,"viewer-state":i,channel:r,message:o}})}MessageDirectSendOnce(e,t,n,i,r,o,a){var c=this.__id();return this.__execute_rr({id:c,responder:a,request:{method:"message/direct-send-once",id:c,identity:e,space:t,key:n,dedupe:i,channel:r,message:o}})}ConnectionCreate(e,t,n,i,r){var s=this,u=s.__id();return s.__execute_stream({id:u,responder:r,request:{method:"connection/create",id:u,identity:e,space:t,key:n,"viewer-state":i},send:function(e,t,n){var i=s.__id();s.__execute_rr({id:i,responder:n,request:{method:"connection/send",id:i,connection:u,channel:e,message:t}})},password:function(e,t,n,i){var r=s.__id();s.__execute_rr({id:r,responder:i,request:{method:"connection/password",id:r,connection:u,username:e,password:t,new_password:n}})},sendOnce:function(e,t,n,i){var r=s.__id();s.__execute_rr({id:r,responder:i,request:{method:"connection/send-once",id:r,connection:u,channel:e,dedupe:t,message:n}})},canAttach:function(e){var t=s.__id();s.__execute_rr({id:t,responder:e,request:{method:"connection/can-attach",id:t,connection:u}})},attach:function(e,t,n,i,r,o,a){var c=s.__id();s.__execute_rr({id:c,responder:a,request:{method:"connection/attach",id:c,connection:u,"asset-id":e,filename:t,"content-type":n,size:i,"digest-md5":r,"digest-sha384":o}})},update:function(e,t){var n=s.__id();s.__execute_rr({id:n,responder:t,request:{method:"connection/update",id:n,connection:u,"viewer-state":e}})},end:function(e){var t=s.__id();s.__execute_rr({id:t,responder:e,request:{method:"connection/end",id:t,connection:u}})}})}ConnectionCreateViaDomain(e,t,n,i){var s=this,u=s.__id();return s.__execute_stream({id:u,responder:i,request:{method:"connection/create-via-domain",id:u,identity:e,domain:t,"viewer-state":n},send:function(e,t,n){var i=s.__id();s.__execute_rr({id:i,responder:n,request:{method:"connection/send",id:i,connection:u,channel:e,message:t}})},password:function(e,t,n,i){var r=s.__id();s.__execute_rr({id:r,responder:i,request:{method:"connection/password",id:r,connection:u,username:e,password:t,new_password:n}})},sendOnce:function(e,t,n,i){var r=s.__id();s.__execute_rr({id:r,responder:i,request:{method:"connection/send-once",id:r,connection:u,channel:e,dedupe:t,message:n}})},canAttach:function(e){var t=s.__id();s.__execute_rr({id:t,responder:e,request:{method:"connection/can-attach",id:t,connection:u}})},attach:function(e,t,n,i,r,o,a){var c=s.__id();s.__execute_rr({id:c,responder:a,request:{method:"connection/attach",id:c,connection:u,"asset-id":e,filename:t,"content-type":n,size:i,"digest-md5":r,"digest-sha384":o}})},update:function(e,t){var n=s.__id();s.__execute_rr({id:n,responder:t,request:{method:"connection/update",id:n,connection:u,"viewer-state":e}})},end:function(e){var t=s.__id();s.__execute_rr({id:t,responder:e,request:{method:"connection/end",id:t,connection:u}})}})}DocumentsHashPassword(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"documents/hash-password",id:n,password:e}})}ConfigureMakeOrGetAssetKey(e){var t=this.__id();return this.__execute_rr({id:t,responder:e,request:{method:"configure/make-or-get-asset-key",id:t}})}AttachmentStart(e,t,n,i,r,o){var a=this,c=a.__id();return a.__execute_stream({id:c,responder:o,request:{method:"attachment/start",id:c,identity:e,space:t,key:n,filename:i,"content-type":r},append:function(e,t,n){var i=a.__id();a.__execute_rr({id:i,responder:n,request:{method:"attachment/append",id:i,upload:c,"chunk-md5":e,"base64-bytes":t}})},finish:function(e){var t=a.__id();a.__execute_rr({id:t,responder:e,request:{method:"attachment/finish",id:t,upload:c}})}})}SuperCheckIn(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"super/check-in",id:n,identity:e}})}SuperListAutomaticDomains(e,t,n){var i=this.__id();return this.__execute_stream({id:i,responder:n,request:{method:"super/list-automatic-domains",id:i,identity:e,timestamp:t}})}SuperSetDomainCertificate(e,t,n,i,r){var o=this.__id();return this.__execute_rr({id:o,responder:r,request:{method:"super/set-domain-certificate",id:o,identity:e,domain:t,certificate:n,timestamp:i}})}}var Adama={Production:"aws-us-east-2.adama-platform.com",Connection:WebSocketAdamaConnection},RxHTML=function(){function a(e){return e.startsWith("/")?W+e.substring(1):e}function s(e){var d;return e in D?D[e]:((d={name:e,ptr:null,tree:new AdamaTree,outstanding:{},decisions:{},choice_subs:{},resets:{},connection_events:{},id:0,connection_state:!1,choices:{}}).set_connected=function(e){if(this.connection_state!=e){this.connection_state=e;var t,n=[];for(t in d.connection_events)d.connection_events[t](e)||n.push(t);for(var i=0;i<n.length;i++)delete d.connection_events[n[i]]}}.bind(d),d.connected=function(e){var t="-|"+this.id++;return(this.connection_events[t]=e)(this.connection_state),function(){delete this.connection_events[t]}.bind(this)}.bind(d),d.subscribe_any=function(e){var t="-|"+this.id++;return this.decisions[t]=e,function(){delete this.decisions[t]}.bind(this)}.bind(d),d.subscribe=function(e,t){var n=e+"|"+this.id++;return this.decisions[n]=t,function(){delete this.decisions[n]}.bind(this)}.bind(d),d.subscribe_reset=function(e){var t="reset|"+this.id++;return this.resets[t]=e,function(){delete this.resets[t]}.bind(this)}.bind(d),d.subscribe_choice=function(e,t){var n=e+"|"+this.id++;return this.choice_subs[n]=t,function(){delete this.choice_subs[n]}.bind(this)}.bind(d),d.onchoices=function(e,t){var n,i=[];for(n in d.choice_subs)!n.startsWith(e+"|")||d.choice_subs[n](t)||i.push(n);for(var r=0;r<i.length;r++)delete d.choice_subs[i[r]]},d.ondecide=function(e){var t,n=[];for(t in d.resets)(0,d.resets[t])()||n.push(t);for(var i=0;i<n.length;i++)delete d.resets[n[i]];for(r in d.outstanding)d.outstanding[r]={options:[]};for(var r,o=e.length,i=0;i<o;i++){var a=e[i];d.outstanding[a.channel]=a}for(r in d.outstanding){var c,s=d.outstanding[r],u=[];for(c in d.decisions)!c.startsWith(r+"|")&&!c.startsWith("-|")||d.decisions[c](s,r)||u.push(c);for(i=0;i<u.length;i++)delete d.decisions[u[i]]}},D[e]=d)}function f(e,t,n){e=A.pI(e,t),"@e"in(t=e[e.current]).delta?t.delta["@e"].push(n):t.delta["@e"]=[n]}function c(e){return{tree:new AdamaTree,delta:{},parent:null,path:null,where:e}}function d(e){if(null==e)return null;var t=null,n={};return null!=(t=null!=e.parent?d(e.parent):t)&&(t.delta[e.path]=n),{tree:e.tree,parent:t,delta:n,path:e.path}}function u(e,t){var n;return null!=e.parent?((n={})[e.path]=t,u(e.parent,n)):t}function l(e){for(var t=e;null!=t.parent;)t=t.parent;return t}function h(e){for(var t=e.lastChild;t;)e.removeChild(t),t=e.lastChild}function p(e,t){var n={inflight:!1,timeout:null,inflight:!1};return function(){n.inflight||(n.inflight=!0,n.timeout=window.setTimeout(function(){n.inflight=!1,n.timeout=null,t()},e))}}function _(){return{__data:function(){},__view:function(){}}}function v(e){e.__data=function(){},e.__view=function(){}}function m(e){e.__data(),e.__view()}function y(e,t){null!=e.data?t.__data=e.data.tree.subscribe(l(e.data).delta):t.__data=function(){},null!=e.view?t.__view=e.view.tree.subscribe(l(e.view).delta):t.__view=function(){}}function w(e){var t={service:e.service,data:d(e.data),view:d(e.view),current:e.current};return null!=t.data&&(t.data.connection=e.data.connection),t}function g(e,t,n,i){if(t in e.data.connection.outstanding)for(var r=e.data.connection.outstanding[t].options,o=0;o<r.length;o++){var a=r[o];if(n in a&&a[n]==i)return a}return null}function b(e){e.dispatchEvent(new Event("success"))}function x(e,t){var n=new Event("failure");n.message=t,console.log("FAILURE:"+t),e.dispatchEvent(n)}function k(e,t){var n,e=e.data.connection.choices,i=(t in e||(e[t]={}),e[t]),r=[];for(n in i)r.push(i[n]);return r}function S(e){if("INPUT"==e.tagName.toUpperCase()&&"PASSWORD"==e.type.toUpperCase())return[e.name,e.value];if("children"in e)for(var t=e.children.length,n=0;n<t;n++){var i=e.children[n],i=S(i);if(null!==i)return i}return null}function o(e){if("INPUT"==e.tagName.toUpperCase())"email"==e.type&&"email"==e.name&&(e.value=localStorage.getItem("email_remember"));else if("children"in e)for(var t=e.children.length,n=0;n<t;n++)o(e.children[n])}var e,t,A={},R={},C={},q=new Adama.Connection(Adama.Production),D={},I=document.createElement("div"),n=document.createElement("span"),U=document.createElement("span"),W=(q.onstatuschange=function(e){U.innerHTML=e?"Yes":"No",e?q.onping=function(e,t){1<=t&&(n.innerHTML=""+t)}:(n.innerHTML="",q.onping=function(e,t){})},I.appendChild(U),I.appendChild(n),I.style="position:fixed; bottom:0px; right:0px",q.start(),"/"),i=function(e){return e},r=(window.location.hostname.endsWith(".adama-platform.com")&&!window.location.hostname.endsWith("ide.adama-platform.com")&&(e=window.location.pathname.split("/"),W=[e[0],e[1],e[2],""].join("/"),t=e[0].length+e[1].length+e[2].length+2,i=function(e){return e.substring(t)}),A.getConnectionByName=s,A.make=function(){return new AdamaTree},A.subscribe=f,A.fresh=c,A.makeDeltaCopy=d,A.pathTo=u,A.rootOf=l,A.pV=function(e){return{service:e.service,data:e.data,view:e.view,current:"view"}},A.newStateViewOf=A.pV,A.pD=function(e){return{service:e.service,data:e.data,view:e.view,current:"data"}},A.newStateDataOf=A.pD,A.pR=function(e){for(var t={service:e.service,data:e.data,view:e.view,current:e.current},n=t[e.current];null!=n.parent;){if(null==n.parent)return t[e.current],n;n=n.parent}return t[e.current]=n,t},A.newStateRootOf=A.pR,A.pU=function(e){var t={service:e.service,data:e.data,view:e.view,current:e.current},n=t[e.current];return null!=n.parent&&(t[e.current]=n),t},A.newStateParentOf=A.pU,A.pI=function(e,t){var n=e[e.current],i=(t in n.delta||(n.delta[t]={}),{service:e.service,data:e.data,view:e.view,current:e.current});return i[e.current]={tree:n.tree,delta:n.delta[t],parent:n,path:t},"data"==i.current&&(i.data.connection=n.connection),i},A.newStateDiveInto=A.pI,A.pEV=function(e,t){return t in e.view.delta||(e.view.delta[t]={}),{service:e.service,data:e.data,view:{tree:e.view.tree,delta:e.view.delta[t],parent:e.view,path:t},current:e.current}},A.newStateCreateViewChild=A.pEV,A.pIE=function(e,t,n){e=A.pI(e,t);return n?A.pEV(e,t):e},A.Y=function(e,t,n,i){f(e,n,function(e){t[n]=e,i()})},A.Y2=function(e,n,i,r,o){f(e,r,function(e){var t=n._[i];return t[r]=e,o(t),!0})},A.RX=function(e){for(var t={_:{}},n=0;n<e.length;n++)t._[e[n]]={};return t},A.YS=function(e,t,n){f(e,n,function(e){return t[n]=e,!0})},A.T=function(e){return document.createTextNode(e)},A.L=function(e,t){var n=document.createTextNode("");return f(e,t,function(e){n.nodeValue=e}),n},A.LT=function(e,t,n){var i=document.createTextNode("");return f(e,t,function(e){null!=e&&(i.nodeValue=n(e))}),i},A.E=function(e,t){return null==t?document.createElement(e):((e=document.createElementNS(t,e)).setAttribute("xmlns",t),e)},A.P=function(i,r,e,o,a){var c=_();e.__=function(){var n;"name"in e&&this.name!=e.name&&(n=s(e.name),this.name=e.name,n.connected(function(e){h(i),m(c);var t={service:r.service,data:{connection:n,tree:n.tree,delta:{},parent:null,path:null},view:d(r.view),current:"data"};return(e?o:a)(i,t),y(t,c),!0}))}.bind({name:""})},A.TP=function(e,t){R[e]=t},A.HREF=function(e,n){e.setAttribute("href",a(n)),e.onclick=function(e){var t=(n.startsWith("/")?n.substring(1):n).split("/");return!N(t,0,C,{})||(e.preventDefault(),A.run(document.body,n,!0),!1)}},A.ACLASS=function(e,t){e.setAttribute("class",t)},A.ASRC=function(e,t){e.setAttribute("src",t)},A.UT=function(e,t,n,i){(0,R[n])(e,t,i)},A.SW=function(n,i,e,r){var t={prior:null},t=(v(t),function(e){var t;e!=this.prior&&(this.prior=e,m(this),h(n),t=w(i),r(n,t,""+e),y(t,this))}.bind(t));f(i,e,t)},A.IT=function(r,e,t,o,a){var c=A.pIE(e,t,o),s={},u={};f(e,t,{"+":function(e){var t={service:(t=A.pIE(c,e,o)).service,data:t.data,view:d(t.view),current:t.current},n=_(),i=a(t);return s[e]=i,u[e]=n,r.append(i),e=n,null!=(i=t).view?e.__view=i.view.tree.subscribe(l(i.view).delta):e.__view=function(){},t[t.current].delta},"-":function(e){e in s&&(r.removeChild(s[e]),delete s[e]),e in u&&(m(u[e]),delete u[e])},"~":function(e){h(r);for(var t=0;t<e.length;t++)r.append(s[e[t]])}})},{}),F=(A.PRCUAC=function(e,t){r[e]=t},A.exCC=function(e,t,n,i){e.addEventListener(t,function(){i in r&&r[i]()})},A.aCC=function(t,n,i,e){t.onsubmit=function(e){i in r?(e.preventDefault(),e=E(t,!1),r[i](e,n,function(e){},A),b(t)):x(t,"Failed to find '"+i+"'")}},A.exFIN=function(e,t,n,i){e.addEventListener(t,function(){function t(){delete n.data.connection.choices[i],n.data.connection.onchoices(i,{})}var e=k(n,i);n.data.connection.ptr.send(i,e,{failure:function(e){t(),console.log("failed:"+e)},success:function(e){t(),console.log("Success|"+e.seq)}})})},A.FIN=function(n,i,r,o,e,a,c){var s={owner:n,shown:!1};v(s);s.update=function(){var e,t=i.data.connection.outstanding[r];t&&(e=k(i,r),e=!("min"in t&&"max"in t)||t.min<=e.length&&e.length<=t.max,s.eval!=e&&(s.eval=e,t=s.eval,m(s),h(n),e=w(i),(t===o?a:c)(n,e),y(e,s)))},i.data.connection.subscribe_choice(r,function(){return s.update(),!0})},A.exCH=function(e,t,i,n,r,o){var a={value:null};e.addEventListener(t,function(){var e,t,n=g(i,r,o,a.value);null!=n&&(e=i.data.connection.choices,r in e||(e[r]={}),e=e[r],(t=n[o])in e?delete e[t]:e[t]=n,i.data.connection.onchoices(r,e))}),f(i,n,function(e){a.value=e})},A.exD=function(e,t,n,i,r,o){var a={value:null};e.addEventListener(t,function(){var e=g(n,r,o,a.value);if(null!=e){let t=performance.now();n.data.connection.ptr.send(r,e,{failure:function(e){},success:function(e){console.log("Success|"+e.seq+";latency="+(performance.now()-t))}})}}),f(n,i,function(e){a.value=e})},A.onFORCE_AUTH=function(e,t,n,i){function r(){L[n]=i,localStorage.setItem("identity_"+n,i)}"load"==t?window.setTimeout(r,1):e.addEventListener(t,r)},A.onS=function(e,t,n,i,r){function o(){(e={})[i]="function"==typeof r?r():r;var e=u(n[n.current],e);n[n.current].tree.update(e)}"load"==t?window.setTimeout(o,1):e.addEventListener(t,o)},A.onTE=function(e,t,n,i){e.addEventListener(t,function(e){console.log("onTE:"+e.message);var t={},e=(t[i]=e.message,u(n[n.current],t));n[n.current].tree.update(e)})},A.onGO=function(e,t,n,i){function r(){var e="function"==typeof i?i():i;A.goto(n.view.tree,e)}"load"==t?window.setTimeout(r,1):e.addEventListener(t,r)},A.onT=function(e,t,n,i){var r={value:!1};e.addEventListener(t,function(){var e={},e=(e[i]=!r.value,u(n[n.current],e));n[n.current].tree.update(e)}),f(n,i,function(e){r.value=1==e})},A.onD=function(e,t,n,i,r){var o={value:0};e.addEventListener(t,function(){var e={},e=(e[i]=o.value+r,u(n[n.current],e));n[n.current].tree.update(e)}),f(n,i,function(e){"number"==typeof e?o.value=e:(e=parseFloat(e),isNaN(e)||(o.value=e))})},A.CSEN=function(n,i,e,r,t,o,a,c,s,u){var d={value:"",owner:n,shown:!1,eval:null};v(d);d.update=function(){var e,t;i.data.connection.outstanding[r]&&(e=i.data.connection.choices,r in e||(e[r]={}),e=e[r],e=d.value in e,d.eval!=e&&(d.eval=e,e=d.eval,m(d),h(n),t=w(i),(e===a?s:u)(n,t),y(t,d)))},i.data.connection.subscribe_choice(r,function(){return d.update(),!0}),f(e,o,function(e){d.value=e,d.update()})},A.DE=function(n,i,e,r,o,t,a,c,s,u){var d={value:"",owner:n,shown:!1,eval:null};v(d);d.update=function(){var e,t=null!=g(i,r,o,d.value);d.eval!=t&&(d.eval=t,t=d.eval,m(d),h(n),e=w(i),(t===a?s:u)(n,e),y(e,d))},i.data.connection.subscribe(r,function(){return d.update(),!0}),f(e,t,function(e){d.value=e,d.update()})},A.IF=function(r,o,a,c,s,u,d){var l=_(),e=function(e){var t,n,i=!!e===c;this.shown!=i&&(this.shown=i,h(r),m(l),n=t=w(o),"object"==typeof e&&(n=A.pI(n,a),s&&(n=A.pEV(n,a))),(i?u:d)(r,n),y(t,l))}.bind({shown:"no"});f(o,a,e)},A.aCP=function(n,i,r){n.onsubmit=function(e){e.preventDefault();var e=E(n,!1),t=("."!=r&&""!=r&&((t={})[r]=e,e=t),u(i.view,e));i.view.tree.update(t),b(n)}},A.SY=function(n,i,r,e){function t(e){(t={})[r]=n.value;var t=u(i.view,t);i.view.tree.update(t)}var o="type"in n?n.type.toUpperCase():"text";"CHECKBOX"==o?n.onchange=p(e,function(e){t(n.checked)}):"RADIO"==o?n.onchange=p(e,function(e){n.checked&&t(n.value)}):(n.onchange=p(e,function(e){t(n.value)}),n.onkeyup=n.onchange,window.setTimeout(function(){t(n.value)},1))},function(e,t,n){var i="TEXTAREA"==e.tagName.toUpperCase()||"SELECT"==e.tagName.toUpperCase(),r="INPUT"==e.tagName.toUpperCase(),o="name"in e,a="",c=t,s=!1;if(o&&(i||r)){for(a=e.name,kDot=a.indexOf(".");0<kDot;){var u=a.substring(0,kDot);u in c||(c[u]={}),c=c[u],a=a.substring(kDot+1),kDot=a.indexOf(".")}(s=a.endsWith("[]"))&&(a=a.substring(0,a.length-2))}if(i)c[a]=e.value;else if(r){i="type"in e?e.type.toUpperCase():"text";"SUBMIT"!=i&&"RESET"!=i&&("PASSWORD"!=i||n)&&o&&(s?(a in c&&(c[a]=[]),c=c[a],"CHECKBOX"==i?c.push(!!e.checked):"RADIO"==i&&!e.checked||c.push(e.value)):"CHECKBOX"==i?c[a]=!!e.checked:"RADIO"==i&&!e.checked||(c[a]=e.value))}else if("children"in e)for(var d=e.children.length,l=0;l<d;l++){var f=e.children[l];F(f,t,n)}}),E=function(e,t){var n={};return F(e,n,t),n},T={},O={},N=(A.PRWP=function(e,t){if(T[e]=t,e in O)for(var n=O[e],i=0;i<n.length;i++)n[i]()},A.WP=function(e,t,n,i){var r;n in T?T[n](e,t,i,A):(r=function(){T[n](e,t,i,A)},n in O?O[n].push(r):O[n]=[r])},A.PG=function(e,t){for(var n=C,i=0;i<e.length;i++){var r=e[i];r in n||(n[r]={}),n=n[r]}n["@"]=t},function(e,t,n,i){if(t<e.length){if("number"in n){var r=n.number,o=parseFloat(e[t]);if(!isNaN(o))for(var a in r){if(i[a]=o,null!==(c=N(e,t+1,r[a],i)))return c;delete i[a]}}if("text"in n){r=n.text,o=e[t];for(a in r){if(i[a]=o,null!==(c=N(e,t+1,r[a],i)))return c;delete i[a]}}var c;if("fixed"in n)for(a in r=n.fixed)if(a==e[t])if(null!==(c=N(e,t+1,r[a],i)))return c}else if("@"in n)return n["@"]}),L=(A.goto=function(e,t){window.setTimeout(function(){t.startsWith("/")?A.run(document.body,t,!0):window.location.href=a(t)},10)},A.init=function(){A.run(document.body,i(window.location.pathname+window.location.hash),!1),window.onpopstate=function(){A.run(document.body,i(window.location.pathname+window.location.hash),!1)}},A.run=function(e,t,n){for(conKey in D)D[conKey].tree.nuke();var i,r=(t.startsWith("/")?t.substring(1):t).split("/"),o={session_id:"R"+Math.random()},r=N(r,0,C,o);h(e),null!=r?((i={service:q,data:null,view:c(e),current:"view"}).view.init=o,r(e,i),i.view.tree.subscribe(i.view.delta),i.view.tree.update(o),n&&window.history.pushState({},"",a(t))):"/404"!=t&&A.run(e,"/404"),e.appendChild(I)},{}),P=(A.SIGNOUT=function(){L={},localStorage.removeItem("identity_default");var e,t=[];for(e in D){var n=D[e];null!=n.ptr&&n.ptr.end({success:function(){},failure:function(){}}),t.push(e)}for(var i=0;i<t.length;i++)delete D[t[i]];A.goto(null,"/")},A.GOOGLE_SIGN_ON=function(e){q.InitConvertGoogleUser(e,{success:function(e){L.default=e.identity,localStorage.setItem("identity_default",e.identity),A.goto(null,"/")},failure:function(e){console.log("Google failure: "+e)}})},A.aRDp=function(e,t){return function(){return t(e.view.init)}},A.aRDz=function(e){return function(){return e}},A.ID=function(e,t){!0===e&&(e="default");var n=null,i=function(){},r=localStorage.getItem("identity_"+e);if(r&&(L[e]=r),e.startsWith("direct:"))n=e.substring(7);else{if(!(e in L))return window.setTimeout(function(){A.goto(null,t())},10),{abort:!0};n=L[e],i=function(){delete L[e],localStorage.removeItem("identity_"+e),A.goto(null,t())}}return{abort:!1,cleanup:i,identity:n}},A.FIDCL=function(t,n){return{success:function(e){t.success(e)},next:function(e){t.next(e)},complete:function(){t.complete()},failure:function(e){t.failure(e),966671==e&&n.cleanup()}}},{});A.PRCUDA=function(e,t){P[e]=t},A.CUDA=function(n,i,r,o,a,c){var s=_();o.__=p(10,function(){m(s),h(n);var e=!1,t=(r in P&&("function"==typeof(t=P[r])&&(e=t(o,i,a,A))),e=e||new AdamaTree,{service:i.service,data:{connection:i.connection,tree:e,delta:{},parent:null,path:null},view:d(i.view),current:"data"});c(t),y(t,s)})},A.ST=function(e){document.title=e.value,e.__=p(1,function(){document.title=e.value})},A.CONNECT=function(r,o){var a={view:function(){}};o.__=p(5,function(){var t,e,n,i;"key"in o&&"space"in o&&"name"in o&&(t=s(o.name),n=o.space+"/"+o.key,e=function(e){a.view=r.view.tree.subscribe(function(){null!=t.ptr&&t.ptr.update(r.view.tree.copy(),{success:function(){},failure:function(){}})}),e&&t.ptr.update(r.view.tree.copy(),{success:function(){},failure:function(){}})},null!=t.ptr&&t.bound==n?e(!0):(t.space=o.space,t.key=o.key,null!=t.ptr&&(t.ptr.end({success:function(){},failure:function(){}}),t.ptr=null),t.bound=n,(n=A.ID(o.identity,function(){return o.redirect})).abort||(i=n.identity,n.cleanup,a.view(),t.ptr=q.ConnectionCreate(i,o.space,o.key,r.view.tree.copy(),{next:function(e){t.set_connected(!0),"data"in e.delta&&t.tree.update(e.delta.data),"outstanding"in e.delta&&t.ondecide(e.delta.outstanding)},complete:function(){t.set_connected(!1)},should_retry:!0,retry_task_name:"Document Connection:"+o.space+"/"+o.key,failure:function(e){console.log("CONNECT FAILURE:"+o.space+"/"+o.key+" ["+o.name+"] "+e),t.set_connected(!1),t.ptr=null}}),t.tree.update({}),e(!1))))})},A.INTERNAL=function(e){return{service:e.service,data:{connection:null,tree:new AdamaTree,delta:{},parent:null,path:null},view:d(e.view),current:"data"}};return A.aUP=function(e,t,n,i){var r,n=A.ID(n,function(){return i.rx_forward});n.abort||(e.action="https://aws-us-east-2.adama-platform.com/~upload",e.method="post",e.enctype="multipart/form-data",(r=document.createElement("input")).type="hidden",r.name="identity",r.value=n.identity,e.appendChild(r),(n=document.createElement("iframe")).name="UPLOAD_"+Math.random(),n.width="1",n.height="1",e.appendChild(n),e.target=n.name)},A.aDSO=function(t,n,i,r){r.__=function(){},t.onsubmit=function(e){e.preventDefault();e=E(t,!0);q.DocumentAuthorize(e.space,e.key,e.username,e.password,{success:function(e){L[i]=e.identity,localStorage.setItem("identity_"+i,e.identity),A.goto(n.view,r.rx_forward),b(t)},failure:function(e){x(t,"Failed signing into document:"+e)}})}},A.aDPUT=function(r,o,a,c){c.__=function(){},r.onsubmit=function(e){e.preventDefault();function t(){var e="https://"+q.host+"/"+c.space+"/"+c.key+"/"+c.path,t=new XMLHttpRequest;t.onreadystatechange=function(){var e;4==this.readyState&&(200==this.status?"text/error"==(e=this.getResponseHeader("Content-Type"))?x(r,this.responseText):"application/json"==e&&("identity"in(e=JSON.parse(this.responseText))&&(L[a]=e.identity,localStorage.setItem("identity_"+a,e.identity)),A.goto(o.view,c.rx_forward),b(r)):x(r,"Failed to communicate to server"))},t.open("PUT",e,!0),t.withCredentials=!0,t.send(JSON.stringify(n))}var n=E(r,!1),i=S(r);null!==i?q.DocumentsHashPassword(i[1],{success:function(e){n[i[0]]=e.passwordHash,t()},failed:function(){x(r,"Failed to hash password")}}):t()}},A.aSO=function(t,n,i,r){r.__=function(){},window.setTimeout(function(){o(t)},1),t.onsubmit=function(e){e.preventDefault();e=E(t,!0);e.remember?localStorage.setItem("email_remember",e.email):localStorage.setItem("email_remember",""),q.AccountLogin(e.email,e.password,{success:function(e){L[i]=e.identity,localStorage.setItem("identity_"+i,e.identity),A.goto(n.view,r.rx_forward),b(t)},failure:function(e){x(t,"AccountLogin Failed:"+e)}})}},A.aSU=function(n,i,r){n.onsubmit=function(e){e.preventDefault();var t=E(n);q.InitSetupAccount(t.email,{success:function(){localStorage.setItem("email",t.email),A.goto(i.view,r),b(n)},failure:function(e){x(n,"InitSetupAccount Failed:"+e)}})}},A.aSP=function(i,r,o){i.onsubmit=function(e){e.preventDefault();var n=E(i,!0);"email"in n||(n.email=localStorage.getItem("email")),q.InitCompleteAccount(n.email,!1,n.code,{success:function(e){var t=e.identity;q.AccountSetPassword(e.identity,n.password,{success:function(){localStorage.setItem("identity_default",t),A.goto(r.view,o),b(i)},failure:function(e){x(i,"Failed AccountSetPassword:"+e)}})},failure:function(e){x(i,"Failed InitCompleteAccount:"+e)}})}},A.aSD=function(t,n,i){t.onsubmit=function(e){e.preventDefault(),n.data.connection.ptr.send(i,E(t,!1),{success:function(){b(t)},failure:function(e){x(t,"Send failed:"+e)}})}},window.rxhtml=A}();
