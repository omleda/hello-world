$wnd.showcase.runAsyncCallback11("function TFb(){}\nfunction VFb(){}\nfunction NFb(a,b){a.b=b}\nfunction OFb(a){if(a==DFb){return true}yA();return a==GFb}\nfunction PFb(a){if(a==CFb){return true}yA();return a==BFb}\nfunction UFb(a){this.b=(yHb(),tHb).a;this.e=(DHb(),CHb).a;this.a=a}\nfunction LFb(a,b){var c;c=LC(a.fb,152);c.b=b.a;!!c.d&&tAb(c.d,b)}\nfunction MFb(a,b){var c;c=LC(a.fb,152);c.e=b.a;!!c.d&&vAb(c.d,b)}\nfunction HFb(){HFb=PX;AFb=new TFb;DFb=new TFb;CFb=new TFb;BFb=new TFb;EFb=new TFb;FFb=new TFb;GFb=new TFb}\nfunction QFb(){HFb();xAb.call(this);this.b=(yHb(),tHb);this.c=(DHb(),CHb);zp((fxb(),this.e),mcc,0);zp(this.e,ncc,0)}\nfunction IFb(a,b,c){var d;if(c==AFb){if(b==a.a){return}else if(a.a){throw new XYb('Only one CENTER widget may be added')}}Rh(b);VRb(a.j,b);c==AFb&&(a.a=b);d=new UFb(c);b.fb=d;LFb(b,a.b);MFb(b,a.c);KFb(a);Th(b,a)}\nfunction JFb(a,b){var c,d,e,f,g,h,i;BRb((fxb(),a.hb),'',b);h=new C4b;i=new dSb(a.j);while(i.b<i.c.c){c=bSb(i);g=LC(c.fb,152).a;e=LC(h.Pg(g),86);d=!e?1:e.a;f=g==EFb?'north'+d:g==FFb?'south'+d:g==GFb?'west'+d:g==BFb?'east'+d:g==DFb?'linestart'+d:g==CFb?'lineend'+d:bcc;BRb(Tp(c.hb),b,f);h.Qg(g,jZb(d+1))}}\nfunction KFb(a){var b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r;b=(fxb(),a.d);while(Kyb(b)>0){dp(b,Jyb(b,0))}o=1;e=1;for(i=new dSb(a.j);i.b<i.c.c;){d=bSb(i);f=LC(d.fb,152).a;f==EFb||f==FFb?++o:(f==BFb||f==GFb||f==DFb||f==CFb)&&++e}p=DC(dS,Y7b,259,o,0,1);for(g=0;g<o;++g){p[g]=new VFb;p[g].b=$doc.createElement(kcc);_o(b,mxb(p[g].b))}k=0;l=e-1;m=0;q=o-1;c=null;for(h=new dSb(a.j);h.b<h.c.c;){d=bSb(h);j=LC(d.fb,152);r=$doc.createElement(lcc);j.d=r;Ap(j.d,Xbc,j.b);Nq(j.d.style,Ybc,j.e);Ap(j.d,n8b,j.f);Ap(j.d,m8b,j.c);if(j.a==EFb){ixb(p[m].b,r,p[m].a);_o(r,mxb(d.hb));zp(r,$cc,l-k+1);++m}else if(j.a==FFb){ixb(p[q].b,r,p[q].a);_o(r,mxb(d.hb));zp(r,$cc,l-k+1);--q}else if(j.a==AFb){c=r}else if(OFb(j.a)){n=p[m];ixb(n.b,r,n.a++);_o(r,mxb(d.hb));zp(r,Sdc,q-m+1);++k}else if(PFb(j.a)){n=p[m];ixb(n.b,r,n.a);_o(r,mxb(d.hb));zp(r,Sdc,q-m+1);--l}}if(a.a){n=p[m];ixb(n.b,c,n.a);_o(c,mxb(dh(a.a)))}}\nOX(402,1,Bac);_.xc=function Afb(){var a,b,c;b_(this.a,(a=new QFb,wp((fxb(),a.hb),'cw-DockPanel'),zp(a.e,mcc,4),NFb(a,(yHb(),sHb)),IFb(a,new mEb(Mdc),(HFb(),EFb)),IFb(a,new mEb(Ndc),FFb),IFb(a,new mEb(Odc),BFb),IFb(a,new mEb(Pdc),GFb),IFb(a,new mEb(Qdc),EFb),IFb(a,new mEb(Rdc),FFb),b=new mEb('\\u8FD9\\u4E2A\\u793A\\u4F8B\\u4E2D\\u5728<code>DockPanel<\\/code> \\u7684\\u4E2D\\u95F4\\u4F4D\\u7F6E\\u6709\\u4E00\\u4E2A<code>ScrollPanel<\\/code>\\u3002\\u5982\\u679C\\u5728\\u4E2D\\u95F4\\u653E\\u5165\\u5F88\\u591A\\u5185\\u5BB9\\uFF0C\\u5B83\\u5C31\\u4F1A\\u53D8\\u6210\\u9875\\u9762\\u5185\\u7684\\u53EF\\u6EDA\\u52A8\\u533A\\u57DF\\uFF0C\\u65E0\\u9700\\u4F7F\\u7528IFRAME\\u3002<br><br>\\u6B64\\u5904\\u4F7F\\u7528\\u4E86\\u76F8\\u5F53\\u591A\\u65E0\\u610F\\u4E49\\u7684\\u6587\\u5B57\\uFF0C\\u4E3B\\u8981\\u662F\\u4E3A\\u4E86\\u53EF\\u4EE5\\u6EDA\\u52A8\\u81F3\\u53EF\\u89C6\\u533A\\u57DF\\u7684\\u5E95\\u90E8\\u3002\\u5426\\u5219\\uFF0C\\u60A8\\u6050\\u6015\\u4E0D\\u5F97\\u4E0D\\u628A\\u5B83\\u7F29\\u5230\\u5F88\\u5C0F\\u624D\\u80FD\\u770B\\u5230\\u90A3\\u5C0F\\u5DE7\\u7684\\u6EDA\\u52A8\\u6761\\u3002'),c=new ABb(b),c.hb.style[n8b]='400px',c.hb.style[m8b]='100px',IFb(a,c,AFb),JFb(a,'cwDockPanel'),a))};OX(864,252,s8b,QFb);_.Mb=function RFb(a){JFb(this,a)};_.dc=function SFb(a){var b;b=qzb(this,a);if(b){a==this.a&&(this.a=null);KFb(this)}return b};var AFb,BFb,CFb,DFb,EFb,FFb,GFb;var eS=DYb(q8b,'DockPanel',864);OX(151,1,{},TFb);var bS=DYb(q8b,'DockPanel/DockLayoutConstant',151);OX(152,1,{152:1},UFb);_.c='';_.f='';var cS=DYb(q8b,'DockPanel/LayoutData',152);OX(259,1,{259:1},VFb);_.a=0;var dS=DYb(q8b,'DockPanel/TmpRow',259);D7b(Kl)(11);\n//# sourceURL=showcase-11.js\n")
