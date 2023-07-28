function AdamaTree(){function i(e){if(Array.isArray(e))for(var t=[],n=0;n<e.length;n++)t.push(i(e[n]));else{if("object"!=typeof e)return e;t={};for(key in e)"#"!=key[0]&&"__key"!=key&&"@o"!=key&&(t[key]=i(e[key]))}return t}function E(e,t){if(0==e.length)return e;for(var n=[],i=0;i<e.length;i++)t in e[i]&&n.push(e[i][t]);return n}function N(e,t,n){for(var i=0;i<e.length;i++){var r=e[i];if("@e"in r)for(var o=r["@e"],a=0;a<o.length;a++)n.push(function(){this.f(this.v)}.bind({f:o[a],v:t}))}}function O(e,t,n,i){for(var r in t){var o="#"+r,a=e[r],c=t[r];if(null==c)Array.isArray(a)&&(o in e&&P(E(n,o)),delete e[o]),P(E(n,r)),delete e[r];else if("object"!=typeof c||Array.isArray(c))e[r]=c,N(E(n,r),c,i);else if(Array.isArray(a)||"@o"in c||"@s"in c){r in e||(e[r]=[]),o in e||(e[o]={},"@o"in c&&(e[o]["@o"]=!0),e[o].__key=r);var s,u=e[o],d=E(n,r),l=null,f=null,h={};for(s in c){var _=c[s];if("@o"==s)l=_;else if("@s"==s)f=_;else if(null==_)s in u&&(h[s]=function(e){P(E(d,e)),delete u[e],R(d,e);for(var t=d,n=e,i=0;i<t.length;i++){var r=t[i];if("-"in r){r=r["-"];if("@e"in r)for(var o=r["@e"],a=0;a<o.length;a++)o[a](n)}}});else{if(!(s in u)){b=w=y=g=m=void 0;for(var p=d,v=s,m=0;m<p.length;m++){var g=p[m];if("+"in g){var y=g["+"];if("@e"in y)for(var w=y["@e"],b=0;b<w.length;b++)U(g,w[b](v),v)}}}_=c[s];"object"==typeof _?(s in u||(u[s]={},u[s].__key=s),h[s]=function(e){O(u[e],c[e],E(d,e),i),N(E(d,e),u[e],i)}):(u[s]=_,h[s]=function(e){N(E(d,e),u[e],i)})}}var x=e[r],k=[];if(null!=l){for(var S=[],A=[],q=0;q<l.length;q++){var C=l[q],D=typeof C;if("string"==D||"number"==D)S.push(u[C]),A.push(""+C);else for(var D=C[0],I=C[1],T=D;T<=I;T++)S.push(x[T]),A.push(x[T].__key)}k.push(function(){N(E(d,"~"),A,i)}),e[r]=S}else if(null!=f){for(S=[],A=[],q=0;q<f;q++)S[q]=x[q],A.push(""+q);k.push(function(){N(E(d,"~"),A,i)}),e[r]=S}for(s in h)h[s](s);for(q=0;q<k.length;q++)k[q]();N(d,e[r],i)}else{r in e&&"object"==typeof e[r]||(e[r]={});a=E(n,r);O(e[r],c,a,i),N(a,e[r],i)}}N(n,e,i)}function r(e){for(var t=0;t<e.length;t++)e[t]()}function o(e,t){if(Array.isArray(e))for(var n=0;n<e.length;n++)o(e[n],t);else if("object"==typeof e)for(var i in e)"@e"==i?o(e[i],t):(i in t||(t[i]={}),o(e[i],t[i]));else"function"==typeof e&&("@e"in t?t["@e"].push(e):t["@e"]=[e])}function u(e){var t,n={};for(t in e)if("#"!=t[0]&&"@o"!=t&&"__key"!=t){var i=e[t];if("#"+t in e){var r=e["#"+t],o={};if("@o"in r){for(var a=[],c=0;c<i.length;c++){var s=i[c];o[s.__key]=u(s),a.push(i[c].__key)}o["@o"]=a}else{for(c=0;c<i.length;c++)o[""+c]=u(r[c]);o["@s"]=i.length}n[t]=o}else n[t]="object"==typeof i?u(i):i}return n}var a=0,c={},s={},U=(this.nuke=function(){s={}},this.copy=function(){return i(c)},this.str=function(){return JSON.stringify(c)},this.raw=function(){return c},function(e,t,n){if(Array.isArray(t))for(var i=0;i<t.length;i++)U(e,t[i],n);else if("function"==typeof t)n in e||(e[n]={}),"@e"in(o=e[n])?o["@e"].push(t):o["@e"]=[t];else if("object"==typeof t){n in e||(e[n]={});var r,o=e[n];for(r in t)"@e"==r?U(e,t[r],n):U(o,t[r],r)}}),P=function(e){if(Array.isArray(e))for(var t=e.length,n=0;n<t;n++)P(e[n]);else if("object"==typeof e)for(var i in e)if("+"!=i&&"-"!=i){var r=e[i];if("@e"==i)for(var o=0;o<r.length;o++)r[o](null);else P(r)}},R=function(e,t){if(Array.isArray(e))for(var n=e.length,i=0;i<n;i++)R(e[i],t);else"object"==typeof e&&t in e&&delete e[t]};this.update=function(e){var t,n=[];for(t in s)n.push(s[t]);var i=[];O(c,e,n,i),r(i)};this.subscribe=function(e){var t={},e=(o(e,t),u(c)),n=[],i=(O({},e,[t],n),r(n),""+a++);return s[i]=t,function(){delete s[i]}}}class WebSocketAdamaConnection{constructor(e){this.backoff=1,this.host=e,this.url="wss://"+e+"/~s",this.connected=!1,this.assets=!0,this.dead=!1,this.maximum_backoff=5e3,this.reset_backoff=1e3,this.socket=null,this.onstatuschange=function(e){},this.onping=function(e,t){},this.scheduled=!1,this.callbacks=new Map,this.nextId=0,this.onreconnect=new Map}stop(){this.dead=!0,null!==this.socket&&this.socket.close()}_retry(){var e,t;this.socket=null,this.connected&&(this.connected=!1,this.onstatuschange(!1)),this.callbacks.clear(),this.dead||this.scheduled||(e=!1,this.backoff+=Math.random()*this.backoff,this.backoff>this.maximum_backoff&&(this.backoff=this.maximum_backoff,e=!0),this.scheduled=!0,t=this,setTimeout(function(){t.start()},this.backoff),e&&(this.backoff/=2))}start(){var n=this;this.scheduled=!1,this.dead=!1,this.socket=new WebSocket(this.url),this.socket.onmessage=function(e){var t,e=JSON.parse(e.data);{if(!("ping"in e))return"status"in e?"connected"!=e.status?(n.socket.close(),n.socket=null,n.backoff=n.reset_backoff,void n._retry()):(n.backoff=1,n.connected=!0,n.assets=e.assets,n.onstatuschange(!0),n._reconnect(),void n.ConfigureMakeOrGetAssetKey({success:function(e){try{var t=new XMLHttpRequest;t.open("GET","https://"+n.host+"/~p"+e.assetKey,!0),t.withCredentials=!0,t.send()}catch(e){console.log(e)}},failure:function(){}})):void("failure"in e?n.callbacks.has(e.failure)&&(t=n.callbacks.get(e.failure))&&(n.callbacks.delete(e.failure),t(e)):"deliver"in e&&n.callbacks.has(e.deliver)&&(t=n.callbacks.get(e.deliver))&&(e.done&&n.callbacks.delete(e.deliver),t(e)));n.onping(e.ping,e.latency),e.pong=(new Date).getTime()/1e3,n.socket.send(JSON.stringify(e))}},this.socket.onclose=function(e){n._retry()},this.socket.onerror=function(e){n._retry()}}_write(e,t){this.connected?(this.callbacks.set(e.id,t),this.socket.send(JSON.stringify(e))):t({failure:600,reason:999})}async wait_connected(){var n,i;return this.connected?new Promise(function(e){e(!0)}):(i=(n=this).onstatuschange,new Promise(function(t){n.onstatuschange=function(e){i(e),e&&(t(!0),n.onstatuschange=i)}}))}_reconnect(){this.onreconnect.forEach(function(e,t){e.__retry()})}__execute_rr(t){var n=this;return t.first=!0,n._write(t.request,function(e){t.first&&(t.first=!1,"failure"in e?"failure"in t.responder&&t.responder.failure(e.reason):"success"in t.responder&&t.responder.success(e.response)),n.onreconnect.delete(t.id)}),n.onreconnect.set(t.id,t),t.__retry=function(){n.__execute_rr(t)},t}__execute_stream(t){var n=this;return n._write(t.request,function(e){"failure"in e?("failure"in t.responder&&t.responder.failure(e.reason),n.onreconnect.delete(t.id)):(e.response&&"next"in t.responder&&t.responder.next(e.response),e.done&&("complete"in t.responder&&t.responder.complete(),n.onreconnect.delete(t.id)))}),n.onreconnect.set(t.id,t),t.__retry=function(){n.__execute_stream(t)},t}__id(){return this.nextId++,this.nextId}InitSetupAccount(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"init/setup-account",id:n,email:e}})}InitConvertGoogleUser(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"init/convert-google-user",id:n,"access-token":e}})}InitCompleteAccount(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"init/complete-account",id:r,email:e,revoke:t,code:n}})}AccountSetPassword(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"account/set-password",id:i,identity:e,password:t}})}AccountGetPaymentPlan(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"account/get-payment-plan",id:n,identity:e}})}AccountLogin(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"account/login",id:i,email:e,password:t}})}Probe(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"probe",id:n,identity:e}})}AuthorityCreate(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"authority/create",id:n,identity:e}})}AuthoritySet(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"authority/set",id:r,identity:e,authority:t,"key-store":n}})}AuthorityGet(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"authority/get",id:i,identity:e,authority:t}})}AuthorityList(e,t){var n=this.__id();return this.__execute_stream({id:n,responder:t,request:{method:"authority/list",id:n,identity:e}})}AuthorityDestroy(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"authority/destroy",id:i,identity:e,authority:t}})}SpaceCreate(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"space/create",id:r,identity:e,space:t,template:n}})}SpaceGenerateKey(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/generate-key",id:i,identity:e,space:t}})}SpaceUsage(e,t,n,i){var r=this.__id();return this.__execute_stream({id:r,responder:i,request:{method:"space/usage",id:r,identity:e,space:t,limit:n}})}SpaceGet(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/get",id:i,identity:e,space:t}})}SpaceSet(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"space/set",id:r,identity:e,space:t,plan:n}})}SpaceRedeployKick(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/redeploy-kick",id:i,identity:e,space:t}})}SpaceSetRxhtml(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"space/set-rxhtml",id:r,identity:e,space:t,rxhtml:n}})}SpaceGetRxhtml(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/get-rxhtml",id:i,identity:e,space:t}})}SpaceDelete(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"space/delete",id:i,identity:e,space:t}})}SpaceSetRole(e,t,n,i,r){var o=this.__id();return this.__execute_rr({id:o,responder:r,request:{method:"space/set-role",id:o,identity:e,space:t,email:n,role:i}})}SpaceListDevelopers(e,t,n){var i=this.__id();return this.__execute_stream({id:i,responder:n,request:{method:"space/list-developers",id:i,identity:e,space:t}})}SpaceReflect(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"space/reflect",id:r,identity:e,space:t,key:n}})}SpaceList(e,t,n,i){var r=this.__id();return this.__execute_stream({id:r,responder:i,request:{method:"space/list",id:r,identity:e,marker:t,limit:n}})}DomainMap(e,t,n,i,r){var o=this.__id();return this.__execute_rr({id:o,responder:r,request:{method:"domain/map",id:o,identity:e,domain:t,space:n,certificate:i}})}DomainMapDocument(e,t,n,i,r,o,a){var c=this.__id();return this.__execute_rr({id:c,responder:a,request:{method:"domain/map-document",id:c,identity:e,domain:t,space:n,key:i,route:r,certificate:o}})}DomainList(e,t){var n=this.__id();return this.__execute_stream({id:n,responder:t,request:{method:"domain/list",id:n,identity:e}})}DomainUnmap(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"domain/unmap",id:i,identity:e,domain:t}})}DomainGet(e,t,n){var i=this.__id();return this.__execute_rr({id:i,responder:n,request:{method:"domain/get",id:i,identity:e,domain:t}})}DocumentAuthorize(e,t,n,i,r){var o=this.__id();return this.__execute_rr({id:o,responder:r,request:{method:"document/authorize",id:o,space:e,key:t,username:n,password:i}})}DocumentAuthorizeDomain(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"document/authorize-domain",id:r,domain:e,username:t,password:n}})}DocumentCreate(e,t,n,i,r,o){var a=this.__id();return this.__execute_rr({id:a,responder:o,request:{method:"document/create",id:a,identity:e,space:t,key:n,entropy:i,arg:r}})}DocumentDelete(e,t,n,i){var r=this.__id();return this.__execute_rr({id:r,responder:i,request:{method:"document/delete",id:r,identity:e,space:t,key:n}})}DocumentList(e,t,n,i,r){var o=this.__id();return this.__execute_stream({id:o,responder:r,request:{method:"document/list",id:o,identity:e,space:t,marker:n,limit:i}})}MessageDirectSend(e,t,n,i,r,o,a){var c=this.__id();return this.__execute_rr({id:c,responder:a,request:{method:"message/direct-send",id:c,identity:e,space:t,key:n,"viewer-state":i,channel:r,message:o}})}MessageDirectSendOnce(e,t,n,i,r,o,a){var c=this.__id();return this.__execute_rr({id:c,responder:a,request:{method:"message/direct-send-once",id:c,identity:e,space:t,key:n,dedupe:i,channel:r,message:o}})}ConnectionCreate(e,t,n,i,r){var s=this,u=s.__id();return s.__execute_stream({id:u,responder:r,request:{method:"connection/create",id:u,identity:e,space:t,key:n,"viewer-state":i},send:function(e,t,n){var i=s.__id();s.__execute_rr({id:i,responder:n,request:{method:"connection/send",id:i,connection:u,channel:e,message:t}})},password:function(e,t,n,i){var r=s.__id();s.__execute_rr({id:r,responder:i,request:{method:"connection/password",id:r,connection:u,username:e,password:t,new_password:n}})},sendOnce:function(e,t,n,i){var r=s.__id();s.__execute_rr({id:r,responder:i,request:{method:"connection/send-once",id:r,connection:u,channel:e,dedupe:t,message:n}})},canAttach:function(e){var t=s.__id();s.__execute_rr({id:t,responder:e,request:{method:"connection/can-attach",id:t,connection:u}})},attach:function(e,t,n,i,r,o,a){var c=s.__id();s.__execute_rr({id:c,responder:a,request:{method:"connection/attach",id:c,connection:u,"asset-id":e,filename:t,"content-type":n,size:i,"digest-md5":r,"digest-sha384":o}})},update:function(e,t){var n=s.__id();s.__execute_rr({id:n,responder:t,request:{method:"connection/update",id:n,connection:u,"viewer-state":e}})},end:function(e){var t=s.__id();s.__execute_rr({id:t,responder:e,request:{method:"connection/end",id:t,connection:u}})}})}ConnectionCreateViaDomain(e,t,n,i){var s=this,u=s.__id();return s.__execute_stream({id:u,responder:i,request:{method:"connection/create-via-domain",id:u,identity:e,domain:t,"viewer-state":n},send:function(e,t,n){var i=s.__id();s.__execute_rr({id:i,responder:n,request:{method:"connection/send",id:i,connection:u,channel:e,message:t}})},password:function(e,t,n,i){var r=s.__id();s.__execute_rr({id:r,responder:i,request:{method:"connection/password",id:r,connection:u,username:e,password:t,new_password:n}})},sendOnce:function(e,t,n,i){var r=s.__id();s.__execute_rr({id:r,responder:i,request:{method:"connection/send-once",id:r,connection:u,channel:e,dedupe:t,message:n}})},canAttach:function(e){var t=s.__id();s.__execute_rr({id:t,responder:e,request:{method:"connection/can-attach",id:t,connection:u}})},attach:function(e,t,n,i,r,o,a){var c=s.__id();s.__execute_rr({id:c,responder:a,request:{method:"connection/attach",id:c,connection:u,"asset-id":e,filename:t,"content-type":n,size:i,"digest-md5":r,"digest-sha384":o}})},update:function(e,t){var n=s.__id();s.__execute_rr({id:n,responder:t,request:{method:"connection/update",id:n,connection:u,"viewer-state":e}})},end:function(e){var t=s.__id();s.__execute_rr({id:t,responder:e,request:{method:"connection/end",id:t,connection:u}})}})}DocumentsHashPassword(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"documents/hash-password",id:n,password:e}})}ConfigureMakeOrGetAssetKey(e){var t=this.__id();return this.__execute_rr({id:t,responder:e,request:{method:"configure/make-or-get-asset-key",id:t}})}AttachmentStart(e,t,n,i,r,o){var a=this,c=a.__id();return a.__execute_stream({id:c,responder:o,request:{method:"attachment/start",id:c,identity:e,space:t,key:n,filename:i,"content-type":r},append:function(e,t,n){var i=a.__id();a.__execute_rr({id:i,responder:n,request:{method:"attachment/append",id:i,upload:c,"chunk-md5":e,"base64-bytes":t}})},finish:function(e){var t=a.__id();a.__execute_rr({id:t,responder:e,request:{method:"attachment/finish",id:t,upload:c}})}})}SuperCheckIn(e,t){var n=this.__id();return this.__execute_rr({id:n,responder:t,request:{method:"super/check-in",id:n,identity:e}})}SuperListAutomaticDomains(e,t,n){var i=this.__id();return this.__execute_stream({id:i,responder:n,request:{method:"super/list-automatic-domains",id:i,identity:e,timestamp:t}})}SuperSetDomainCertificate(e,t,n,i,r){var o=this.__id();return this.__execute_rr({id:o,responder:r,request:{method:"super/set-domain-certificate",id:o,identity:e,domain:t,certificate:n,timestamp:i}})}}var Adama={Production:"aws-us-east-2.adama-platform.com",Connection:WebSocketAdamaConnection},RxHTML=function(){function a(e){return e.startsWith("/")?i+e.substring(1):e}function d(e){var d;return e in I?I[e]:((d={name:e,ptr:null,tree:new AdamaTree,outstanding:{},decisions:{},choice_subs:{},resets:{},connection_events:{},sync_events:{},id:0,connection_state:!1,choices:{},bound:"",viewstate_sent:!0,synced:!1,filter:{},vsseq:0,debugger:function(e){},raw:D}).nuke=function(){this.connection_events={},this.sync_events={},this.choice_subs={}}.bind(d),Adama.Debugger&&(d.debugger=Adama.Debugger.register(d)),d.sync=function(){var e=this.connection_state&&this.viewstate_sent;if(this.synced!=e){this.synced=e;var t,n=[];for(t in d.sync_events)d.sync_events[t](e)||n.push(t);for(var i=0;i<n.length;i++)delete d.sync_events[n[i]]}}.bind(d),d.subscribe_sync=function(e){var t="-|"+this.id++;return(this.sync_events[t]=e)(this.synced),function(){delete this.sync_events[t]}.bind(this)},d.set_connected=function(e){if(this.connection_state!=e){this.connection_state=e;var t,n=[];for(t in d.connection_events)d.connection_events[t](e)||n.push(t);for(var i=0;i<n.length;i++)delete d.connection_events[n[i]];this.sync()}}.bind(d),d.connected=function(e){var t="-|"+this.id++;return(this.connection_events[t]=e)(this.connection_state),function(){delete this.connection_events[t]}.bind(this)}.bind(d),d.subscribe_any=function(e){var t="-|"+this.id++;return this.decisions[t]=e,function(){delete this.decisions[t]}.bind(this)}.bind(d),d.subscribe=function(e,t){var n=e+"|"+this.id++;return this.decisions[n]=t,function(){delete this.decisions[n]}.bind(this)}.bind(d),d.subscribe_reset=function(e){var t="reset|"+this.id++;return this.resets[t]=e,function(){delete this.resets[t]}.bind(this)}.bind(d),d.subscribe_choice=function(e,t){var n=e+"|"+this.id++;return this.choice_subs[n]=t,function(){delete this.choice_subs[n]}.bind(this)}.bind(d),d.onchoices=function(e,t){var n,i=[];for(n in d.choice_subs)!n.startsWith(e+"|")||d.choice_subs[n](t)||i.push(n);for(var r=0;r<i.length;r++)delete d.choice_subs[i[r]]},d.ondecide=function(e){var t,n=[];for(t in d.resets)(0,d.resets[t])()||n.push(t);for(var i=0;i<n.length;i++)delete d.resets[n[i]];for(r in d.outstanding)d.outstanding[r]={options:[]};for(var r,o=e.length,i=0;i<o;i++){var a=e[i];d.outstanding[a.channel]=a}for(r in d.outstanding){var c,s=d.outstanding[r],u=[];for(c in d.decisions)!c.startsWith(r+"|")&&!c.startsWith("-|")||d.decisions[c](s,r)||u.push(c);for(i=0;i<u.length;i++)delete d.decisions[u[i]]}},I[e]=d)}function f(e,t,n){e=q.pI(e,t),"@e"in(t=e[e.current]).delta?t.delta["@e"].push(n):t.delta["@e"]=[n]}function c(e){return{tree:new AdamaTree,delta:{},parent:null,path:null,where:e}}function l(e){if(null==e)return null;var t=null,n={};return null!=(t=null!=e.parent?l(e.parent):t)&&(t.delta[e.path]=n),{tree:e.tree,parent:t,delta:n,path:e.path}}function s(e,t){var n;return null!=e.parent?((n={})[e.path]=t,s(e.parent,n)):t}function h(e){for(var t=e;null!=t.parent;)t=t.parent;return t}function _(e){for(var t=e.lastChild;t;)e.removeChild(t),t=e.lastChild}function p(e,t){var n={inflight:!1,timeout:null,inflight:!1};return function(){n.inflight||(n.inflight=!0,n.timeout=window.setTimeout(function(){n.inflight=!1,n.timeout=null,t()},e))}}function v(){return{__data:function(){},__view:function(){}}}function m(e){e.__data=function(){},e.__view=function(){}}function g(e){e.__data(),e.__view()}function y(e,t){null!=e.data?t.__data=e.data.tree.subscribe(h(e.data).delta):t.__data=function(){},null!=e.view?t.__view=e.view.tree.subscribe(h(e.view).delta):t.__view=function(){}}function w(e){var t={service:e.service,data:l(e.data),view:l(e.view),current:e.current};return null!=t.data&&(t.data.connection=e.data.connection),t}function b(e,t,n,i){if(t in e.data.connection.outstanding)for(var r=e.data.connection.outstanding[t].options,o=0;o<r.length;o++){var a=r[o];if(n in a&&a[n]==i)return a}return null}function u(e){e.dispatchEvent(new Event("success"))}function x(e,t){var n=new Event("failure");n.message=t,console.log("FAILURE:"+t),e.dispatchEvent(n)}function k(e,t){var n,e=e.data.connection.choices,i=(t in e||(e[t]={}),e[t]),r=[];for(n in i)r.push(i[n]);return r}function S(o,a,e,c){return c.unexpected_errors=0,c.backoff=1,{next:function(e){var t;if(o.debugger(e),o.set_connected(!0),c.unexpected_errors=0,c.backoff=1,"data"in e.delta&&o.tree.update(e.delta.data),"outstanding"in e.delta&&o.ondecide(e.delta.outstanding),"viewstate"in e.delta&&a.view.tree.update(e.delta.viewstate),"log"in e.delta&&console.log(e.delta.log),"viewport"in e.delta&&"message"in e.delta&&((t=e.delta.viewport)in o.handlers&&o.handlers[t](e.delta.message,o)),"view-state-filter"in e.delta){for(var n={},i=e.delta["view-state-filter"],r=0;r<i.length;r++)n[i[r]]=!0;o.filter=n}"goto"in e.delta&&q.goto(e.delta.goto)},complete:function(){o.set_connected(!1),o.ptr=null},failure:function(e){o.set_connected(!1),o.ptr=null;var t=999==e,n=c.unexpected_errors<10;n&&c.unexpected_errors++,t||n?(c.backoff=Math.ceil(Math.min(c.backoff+Math.random()*c.backoff+(n?100:1),1e3)),console.log("connect-failure|retrying... ("+c.backoff+"); reason="+e+(n?" [blind]":"")),window.setTimeout(function(){c.go()},c.backoff)):console.log("gave up connection;"+o.label+"; reason="+e)}}}function A(e,t,n,i){function r(){var e=i.view.tree.copy(),t=(n.viewstate_sent=!1,n.vsseq++,n.sync(),function(){n.vsseq==this.bound&&(n.viewstate_sent=!0,n.sync())}.bind({bound:n.vsseq}));n.ptr.update(e,{success:t,failure:t})}function o(e){t.view=i.view.tree.subscribe(function(){null!=n.ptr&&r()}),e&&r()}return null!=n.ptr&&n.bound==e?(o(!0),!1):(null!=n.ptr&&(n.ptr.end({success:function(){},failure:function(){}}),n.ptr=null),o)}function r(e){if("INPUT"==e.tagName.toUpperCase())"email"==e.type&&"email"==e.name&&(e.value=localStorage.getItem("email_remember"));else if("children"in e)for(var t=e.children.length,n=0;n<t;n++)r(e.children[n])}function F(r,e,o,a,c){a.__=function(){},r.onsubmit=function(e){e.preventDefault();function t(){var e=c(a),t=new XMLHttpRequest;t.onreadystatechange=function(){var e;4==this.readyState&&(200==this.status?"text/error"==(e=this.getResponseHeader("Content-Type"))?x(r,this.responseText):("application/json"==e&&"identity"in(e=JSON.parse(this.responseText))&&(L[o]=e.identity,localStorage.setItem("identity_"+o,e.identity)),q.goto(a.rx_forward),u(r)):x(r,"Failed to communicate to server"))},t.open("PUT",e,!0),t.withCredentials=!0,t.send(JSON.stringify(n))}var n=O(r,!1),i=G(r);null!==i?D.DocumentsHashPassword(i[1],{success:function(e){n[i[0]]=e.passwordHash,t()},failed:function(){x(r,"Failed to hash password")}}):t()}}var e,n,q={},W={},C={},D=new Adama.Connection(Adama.Production),I={},T=document.createElement("div"),i=(D.onstatuschange=function(e){function n(e){return'<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="'+e+'" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M8.288 15.038a5.25 5.25 0 017.424 0M5.106 11.856c3.807-3.808 9.98-3.808 13.788 0M1.924 8.674c5.565-5.565 14.587-5.565 20.152 0M12.53 18.22l-.53.53-.53-.53a.75.75 0 011.06 0z" /></svg>\n'}e?D.onping=function(e,t){T.innerHTML=n(100<=t?"orange":"green")}:(T.innerHTML=n("red"),D.onping=function(e,t){})},T.style="position:fixed; bottom:0px; right:0px",Adama.Debugger&&(T.onclick=Adama.Debugger.toggle),D.start(),"/"),t=function(e){return e},o=(window.location.hostname.endsWith(".adama-platform.com")&&!window.location.hostname.endsWith("ide.adama-platform.com")&&(e=window.location.pathname.split("/"),i=[e[0],e[1],e[2],""].join("/"),n=e[0].length+e[1].length+e[2].length+2,t=function(e){return e.substring(n)}),q.getConnectionByName=d,q.make=function(){return new AdamaTree},q.subscribe=f,q.fresh=c,q.makeDeltaCopy=l,q.pathTo=s,q.rootOf=h,q.nuke=_,q.debounce=p,q.pV=function(e){return{service:e.service,data:e.data,view:e.view,current:"view"}},q.newStateViewOf=q.pV,q.pD=function(e){return{service:e.service,data:e.data,view:e.view,current:"data"}},q.newStateDataOf=q.pD,q.pR=function(e){for(var t={service:e.service,data:e.data,view:e.view,current:e.current},n=t[e.current];null!=n;)n=(t[e.current]=n).parent;return t},q.newStateRootOf=q.pR,q.pU=function(e){var t={service:e.service,data:e.data,view:e.view,current:e.current},n=t[e.current];return null!=n.parent&&(t[e.current]=n),t},q.newStateParentOf=q.pU,q.pI=function(e,t){var n=e[e.current],i=(t in n.delta||(n.delta[t]={}),{service:e.service,data:e.data,view:e.view,current:e.current});return i[e.current]={tree:n.tree,delta:n.delta[t],parent:n,path:t},"data"==i.current&&(i.data.connection=n.connection),i},q.newStateDiveInto=q.pI,q.pEV=function(e,t){return t in e.view.delta||(e.view.delta[t]={}),{service:e.service,data:e.data,view:{tree:e.view.tree,delta:e.view.delta[t],parent:e.view,path:t},current:e.current}},q.newStateCreateViewChild=q.pEV,q.pIE=function(e,t,n){e=q.pI(e,t);return n?q.pEV(e,t):e},q.Y=function(e,t,n,i){f(e,n,function(e){t[n]=e,i()})},q.Y2=function(e,n,i,r,o){f(e,r,function(e){var t=n._[i];return t[r]=e,o(t),!0})},q.RX=function(e){for(var t={_:{}},n=0;n<e.length;n++)t._[e[n]]={};return t.__=function(){},t},q.YS=function(e,t,n){f(e,n,function(e){return t[n]=e,!0})},q.T=function(e){return document.createTextNode(e)},q.L=function(e,t){var n=document.createTextNode("");return f(e,t,function(e){n.nodeValue=e}),n},q.LT=function(e,t,n){var i=document.createTextNode("");return f(e,t,function(e){null!=e&&(i.nodeValue=n(e))}),i},q.E=function(e,t){return null==t?document.createElement(e):((e=document.createElementNS(t,e)).setAttribute("xmlns",t),e)},q.P=function(i,r,e,o,a){var c=v();e.__=function(){var n;"name"in e&&this.name!=e.name&&(n=d(e.name),this.name=e.name,n.connected(function(e){_(i),g(c);var t={service:r.service,data:{connection:n,tree:n.tree,delta:{},parent:null,path:null},view:l(r.view),current:"data"};return(e?o:a)(i,t),y(t,c),!0}))}.bind({name:""})},q.TP=function(e,t){W[e]=t},q.HREF=function(e,n){e.setAttribute("href",a(n)),e.onclick=function(e){var t=(n.startsWith("/")?n.substring(1):n).split("/");return!R(t,0,C,{})||(e.preventDefault(),q.run(document.body,n,!0),!1)}},q.ACLASS=function(e,t){e.setAttribute("class",t)},q.ASRC=function(e,t){e.setAttribute("src",t)},q.UT=function(e,t,n,i){(0,W[n])(e,t,i)},q.SW=function(n,i,e,r){var t={prior:null},t=(m(t),function(e){var t;e!=this.prior&&(this.prior=e,g(this),_(n),t=w(i),r(n,t,""+e),y(t,this))}.bind(t));f(i,e,t)},q.IT=function(r,e,t,o,a){var c=q.pIE(e,t,o),s={},u={};f(e,t,{"+":function(e){var t={service:(t=q.pIE(c,e,o)).service,data:t.data,view:l(t.view),current:t.current},n=v(),i=a(t);return s[e]=i,u[e]=n,r.append(i),e=n,null!=(i=t).view?e.__view=i.view.tree.subscribe(h(i.view).delta):e.__view=function(){},t[t.current].delta},"-":function(e){e in s&&(r.removeChild(s[e]),delete s[e]),e in u&&(g(u[e]),delete u[e])},"~":function(e){if(_(r),null==e){for(var t in u)g(u[t]),delete u[t];for(var t in s)delete s[t]}else for(var n=0;n<e.length;n++)r.append(s[e[n]])}})},{}),E=(q.PRCUAC=function(e,t){o[e]=t},q.exCC=function(e,t,n,i){E(n,e,t,function(){i in o&&o[i](n,e)})},q.fire_success=u,q.fire_failure=x,q.aCC=function(t,n,i){function r(e){null==r?u(t):x(t,e)}t.onsubmit=function(e){i in o?(e.preventDefault(),e=O(t,!1),o[i](e,n,r,q),u(t)):x(t,"Failed to find '"+i+"'")}},q.exFIN=function(e,t,n,i){E(n,e,t,function(){function t(){delete n.data.connection.choices[i],n.data.connection.onchoices(i,{})}var e=k(n,i);n.data.connection.ptr.send(i,e,{failure:function(e){t(),console.log("failed:"+e)},success:function(e){t(),console.log("Success|"+e.seq)}})})},q.FIN=function(n,i,r,o,e,a,c){var s={owner:n,shown:!1};m(s);s.update=function(){var e,t=i.data.connection.outstanding[r];t&&(e=k(i,r),e=!("min"in t&&"max"in t)||t.min<=e.length&&e.length<=t.max,s.eval!=e&&(s.eval=e,t=s.eval,g(s),_(n),e=w(i),(t===o?a:c)(n,e),y(e,s)))},i.data.connection.subscribe_choice(r,function(){return s.update(),!0})},q.exCH=function(e,t,i,n,r,o){var a={value:null};E(i,e,t,function(){var e,t,n=b(i,r,o,a.value);null!=n&&(e=i.data.connection.choices,r in e||(e[r]={}),e=e[r],(t=n[o])in e?delete e[t]:e[t]=n,i.data.connection.onchoices(r,e))}),f(i,n,function(e){a.value=e})},q.exD=function(e,t,n,i,r,o){var a={value:null};E(n,e,t,function(){var e=b(n,r,o,a.value);if(null!=e){let t=performance.now();n.data.connection.ptr.send(r,e,{failure:function(e){},success:function(e){console.log("Success|"+e.seq+";latency="+(performance.now()-t))}})}}),f(n,i,function(e){a.value=e})},function(e,t,n,i){"load"==n?window.setTimeout(i,1):t.addEventListener(n,i)}),N=(q.onFR=function(e,t,n,i){E(e,t,n,function(){console.log(e.data),e.data.connection.ptr.send(i,{},{success:function(){console.log("SENT '"+i+"'")},failure:function(e){console.log("FAILED TO FIRE '"+i+"' DUE:"+e)}})})},q.onFORCE_AUTH=function(e,t,n,i){E(null,e,t,function(){L[n]=i,localStorage.setItem("identity_"+n,i)})},q.onS=function(e,t,n,i,r){E(n,e,t,function(){var e={},e=(e[i]="function"==typeof r?r():r,s(n[n.current],e));n[n.current].tree.update(e)})},q.onTE=function(e,t,n,i){E(n,e,t,function(e){var t={},e=(t[i]=e.message,s(n[n.current],t));n[n.current].tree.update(e)})},q.oRST=function(t,e,n){E(n,t,e,function(e){t.reset()})},q.onGO=function(e,t,n,i){E(n,e,t,function(){var e="function"==typeof i?i():i;q.goto(e)})},q.onT=function(e,t,n,i){var r={value:!1};E(n,e,t,function(){var e={},e=(e[i]=!r.value,s(n[n.current],e));n[n.current].tree.update(e)}),f(n,i,function(e){r.value=1==e})},q.onD=function(e,t,n,i,r){var o={value:0};E(n,e,t,function(){var e={},e=(e[i]=o.value+r,s(n[n.current],e));n[n.current].tree.update(e)}),f(n,i,function(e){"number"==typeof e?o.value=e:(e=parseFloat(e),isNaN(e)||(o.value=e))})},q.CSEN=function(n,i,e,r,t,o,a,c,s,u){var d={value:"",owner:n,shown:!1,eval:null};m(d);d.update=function(){var e,t;i.data.connection.outstanding[r]&&(e=i.data.connection.choices,r in e||(e[r]={}),e=e[r],e=d.value in e,d.eval!=e&&(d.eval=e,e=d.eval,g(d),_(n),t=w(i),(e===a?s:u)(n,t),y(t,d)))},i.data.connection.subscribe_choice(r,function(){return d.update(),!0}),f(e,o,function(e){d.value=e,d.update()})},q.DE=function(n,i,e,r,o,t,a,c,s,u){var d={value:"",owner:n,shown:!1,eval:null};m(d);d.update=function(){var e,t=null!=b(i,r,o,d.value);d.eval!=t&&(d.eval=t,t=d.eval,g(d),_(n),e=w(i),(t===a?s:u)(n,e),y(e,d))},i.data.connection.subscribe(r,function(){return d.update(),!0}),f(e,t,function(e){d.value=e,d.update()})},q.IF=function(r,o,a,c,s,u,d){var l=v(),e=function(e){var t,n,i=!!e===c;this.shown!=i&&(this.shown=i,_(r),g(l),n=t=w(o),"object"==typeof e&&(n=q.pI(n,a),s&&(n=q.pEV(n,a))),(i?u:d)(r,n),y(t,l))}.bind({shown:"no"});f(o,a,e)},q.aCP=function(n,i,r){n.onsubmit=function(e){e.preventDefault();var e=O(n,!1),t=("."!=r&&""!=r&&((t={})[r]=e,e=t),s(i.view,e));i.view.tree.update(t),u(n)}},q.SY=function(n,i,r,e){function t(e){(t={})[r]=n.value;var t=s(i.view,t);i.view.tree.update(t)}var o="type"in n?n.type.toUpperCase():"text";"CHECKBOX"==o?n.onchange=p(e,function(e){t(n.checked)}):"RADIO"==o?n.onchange=p(e,function(e){n.checked&&t(n.value)}):(n.onchange=p(e,function(e){t(n.value)}),n.onkeyup=n.onchange,window.setTimeout(function(){t(n.value)},1))},function(e,t,n){var i="TEXTAREA"==e.tagName.toUpperCase()||"SELECT"==e.tagName.toUpperCase(),r="INPUT"==e.tagName.toUpperCase(),o="FIELDSET"==e.tagName.toUpperCase(),a="name"in e,c=t,s=function(e){};if(a&&(i||r||o)){for(var u="",d=(u=e.name).indexOf(".");0<d;){var l=u.substring(0,d);l in c||(c[l]={}),c=c[l],d=(u=u.substring(d+1)).indexOf(".")}s=u.endsWith("+")?(u=u.substring(0,u.length-1),function(e){u in c?c[u].push(e):c[u]=[e]}):function(e){c[u]=e}}if(i)s(e.value);else if(o){var f={};if("children"in e)for(var h=e.children.length,_=0;_<h;_++){var p=e.children[_];N(p,f,n)}s(f)}else if(r){a="type"in e?e.type.toUpperCase():"text";"SUBMIT"==a||"RESET"==a||"PASSWORD"==a&&!n||("CHECKBOX"==a?s(!!e.checked):"RADIO"==a&&!e.checked||s(e.value))}else if("children"in e)for(h=e.children.length,_=0;_<h;_++){p=e.children[_];N(p,t,n)}}),O=function(e,t){var n={};return N(e,n,t),n},G=function(e){if("INPUT"==e.tagName.toUpperCase()&&"PASSWORD"==e.type.toUpperCase())return[e.name,e.value];if("children"in e)for(var t=e.children.length,n=0;n<t;n++){var i=e.children[n],i=G(i);if(null!==i)return i}return null},U={},P={},R=(q.PRWP=function(e,t){if(U[e]=t,e in P)for(var n=P[e],i=0;i<n.length;i++)n[i]()},q.WP=function(e,t,n,i,r){var o;n in U?U[n](e,t,i,r,q):(o=function(){U[n](e,t,i,r,q)},n in P?P[n].push(o):P[n]=[o])},q.PG=function(e,t){for(var n=C,i=0;i<e.length;i++){var r=e[i];r in n||(n[r]={}),n=n[r]}n["@"]=t},function(e,t,n,i){if(t<e.length){if("number"in n){var r=n.number,o=parseFloat(e[t]);if(!isNaN(o))for(var a in r){if(i[a]=o,null!==(c=R(e,t+1,r[a],i)))return c;delete i[a]}}if("text"in n){r=n.text,o=e[t];for(a in r){if(i[a]=o,null!==(c=R(e,t+1,r[a],i)))return c;delete i[a]}}var c;if("fixed"in n)for(a in r=n.fixed)if(a==e[t])if(null!==(c=R(e,t+1,r[a],i)))return c}else if("@"in n)return n["@"]}),L=(q.goto=function(e){console.log("going to:"+e),window.setTimeout(function(){e.startsWith("/")?q.run(document.body,e,!0):window.location.href=a(e)},10)},q.init=function(){q.run(document.body,t(window.location.pathname+window.location.hash),!1),window.onpopstate=function(){q.run(document.body,t(window.location.pathname+window.location.hash),!1)}},q.run=function(e,t,n){for(conKey in I)I[conKey].tree.nuke(),I[conKey].nuke();var i,r=(t.startsWith("/")?t.substring(1):t).split("/"),o={__session_id:"R"+Math.random()},r=R(r,0,C,o);_(e),null!=r?((i={service:D,data:null,view:c(e),current:"view"}).view.init=o,r(e,i),i.view.tree.subscribe(i.view.delta),i.view.tree.update(o),n&&window.history.pushState({},"",a(t))):"/404"!=t&&q.run(e,"/404"),e.appendChild(T)},{}),M=(q.SIGNOUT=function(){L={},localStorage.removeItem("identity_default");var e,t=[];for(e in I){var n=I[e];null!=n.ptr&&n.ptr.end({success:function(){},failure:function(){}}),t.push(e)}for(var i=0;i<t.length;i++)delete I[t[i]];q.goto("/")},q.GOOGLE_SIGN_ON=function(e){D.InitConvertGoogleUser(e,{success:function(e){L.default=e.identity,localStorage.setItem("identity_default",e.identity),q.goto("/")},failure:function(e){console.log("Google failure: "+e)}})},q.aRDp=function(e,t){return function(){return t(e.view.init)}},q.aRDz=function(e){return function(){return e}},q.ID=function(e,t){!0===e&&(e="default");var n=null,i=function(){},r=localStorage.getItem("identity_"+e);if(r&&(L[e]=r),e.startsWith("direct:"))n=e.substring(7);else{if(!(e in L))return window.setTimeout(function(){q.goto(t())},10),{abort:!0};n=L[e],i=function(){delete L[e],localStorage.removeItem("identity_"+e),q.goto(t())}}return{abort:!1,cleanup:i,identity:n}},q.FIDCL=function(t,n){return{success:function(e){t.success(e)},next:function(e){t.next(e)},complete:function(){t.complete()},failure:function(e){t.failure(e),966671==e&&n.cleanup()}}},{});q.PRCUDA=function(e,t){M[e]=t},q.CUDA=function(n,i,r,o,a,c){var s=v();o.__=p(10,function(){g(s),_(n);var e=!1,t=(r in M&&("function"==typeof(t=M[r])&&(e=t(o,i,a,q))),e=e||new AdamaTree,{service:i.service,data:{connection:i.connection,tree:e,delta:{},parent:null,path:null},view:l(i.view),current:"data"});c(t),y(t,s)})},q.ST=function(e){document.title=e.value,e.__=p(1,function(){document.title=e.value})},q.VSy=function(n,i,r,o){var a=v();i.data.connection.subscribe_sync(function(e){_(n),g(a);var t=w(i);return(e?r:o)(n,t),y(t,a),!0})},q.DCONNECT=function(c,s){var u={view:function(){}};s.__=p(5,function(){var e,t,n,i,r,o,a;"name"in s&&((e=q.ID(s.identity,function(){return s.redirect})).abort||(t=d(s.name),i=(n=location.host.split(":")[0])+e.identity,!1!==(r=A(i,u,t,c))&&(t.bound=i,o=e.identity,e.cleanup,u.view(),t.identity=o,t.handlers={},t.viewstate_sent=!0,t.label="Domain:"+n+" [ "+s.name+"]",(a={}).responder=S(t,c,0,a),a.go=function(){t.ptr=D.ConnectionCreateViaDomain(o,n,c.view.tree.copy(),a.responder)},a.go(),t.tree.update({}),r(!1))))})},q.CONNECT=function(a,c){var s={view:function(){}};c.__=p(5,function(){var e,t,n,i,r,o;"key"in c&&"space"in c&&"name"in c&&((e=q.ID(c.identity,function(){return c.redirect})).abort||(t=d(c.name),n=c.space+"/"+c.key+"/"+e.identity,!1!==(i=A(n,s,t,a))&&(t.bound=n,r=e.identity,e.cleanup,s.view(),t.space=c.space,t.key=c.key,t.identity=r,t.handlers={},t.viewstate_sent=!0,t.label=t.space+"/"+t.key+" [ "+c.name+"]",(o={}).responder=S(t,a,0,o),o.go=function(){t.ptr=D.ConnectionCreate(r,c.space,c.key,a.view.tree.copy(),o.responder)},o.go(),t.tree.update({}),i(!1))))})},q.INTERNAL=function(e){return{service:e.service,data:{connection:null,tree:new AdamaTree,delta:{},parent:null,path:null},view:l(e.view),current:"data"}},q.aUP=function(e,t,n,i){var r,n=q.ID(n,function(){return i.rx_forward});n.abort||(e.action="https://aws-us-east-2.adama-platform.com/~upload",e.method="post",e.enctype="multipart/form-data",(r=document.createElement("input")).type="hidden",r.name="identity",r.value=n.identity,e.appendChild(r),(n=document.createElement("iframe")).name="UPLOAD_"+Math.random(),n.width="1",n.height="1",e.appendChild(n),e.target=n.name)},q.adDSO=function(e,t,n,i){},q.aDSO=function(t,e,n,i){i.__=function(){},t.onsubmit=function(e){e.preventDefault();e=O(t,!0);D.DocumentAuthorize(e.space,e.key,e.username,e.password,{success:function(e){L[n]=e.identity,localStorage.setItem("identity_"+n,e.identity),q.goto(i.rx_forward),u(t)},failure:function(e){x(t,"Failed signing into document:"+e)}})}},q.fdusa=function(e){return"USA["+e+"]"};return q.adDPUT=function(e,t,n,i){F(e,0,n,i,function(e){return location.protocol+"//"+location.host+"/"+e.path})},q.aDPUT=function(e,t,n,i){F(e,0,n,i,function(e){return"https://"+D.host+"/"+e.space+"/"+e.key+"/"+e.path})},q.aSO=function(t,e,n,i){i.__=function(){},window.setTimeout(function(){r(t)},1),t.onsubmit=function(e){e.preventDefault();e=O(t,!0);e.remember?localStorage.setItem("email_remember",e.email):localStorage.setItem("email_remember",""),D.AccountLogin(e.email,e.password,{success:function(e){L[n]=e.identity,localStorage.setItem("identity_"+n,e.identity),q.goto(i.rx_forward),u(t)},failure:function(e){x(t,"AccountLogin Failed:"+e)}})}},q.aSU=function(n,e,i){n.onsubmit=function(e){e.preventDefault();var t=O(n);D.InitSetupAccount(t.email,{success:function(){localStorage.setItem("email",t.email),q.goto(i),u(n)},failure:function(e){x(n,"InitSetupAccount Failed:"+e)}})}},q.aSP=function(i,e,r){i.onsubmit=function(e){e.preventDefault();var n=O(i,!0);"email"in n||(n.email=localStorage.getItem("email")),D.InitCompleteAccount(n.email,!1,n.code,{success:function(e){var t=e.identity;D.AccountSetPassword(e.identity,n.password,{success:function(){localStorage.setItem("identity_default",t),q.goto(r),u(i)},failure:function(e){x(i,"Failed AccountSetPassword:"+e)}})},failure:function(e){x(i,"Failed InitCompleteAccount:"+e)}})}},q.aSD=function(t,n,i){t.onsubmit=function(e){e.preventDefault(),n.data.connection.ptr.send(i,O(t,!1),{success:function(){u(t)},failure:function(e){x(t,"Send failed:"+e)}})}},window.rxhtml=q}();
