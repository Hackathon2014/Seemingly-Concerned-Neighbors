close all
clear all


%% Input from seismic uncertainty (or user) goes here
zt=600; %true depth to top
h=75;  %true thickness
ztm=500; %z-top-minus
zbp=777; %z-bot-plus
xmax=1000; %max offset
dm1=25; %spacing in z we want
dm2=50; %spacing in x we want

%% Section 0: Define coordinates for observations and model

%observations
so1=Sampling;
so2=Sampling;
no1=100; do1=10; fo1=50;
no2=1000; do2=0; fo2=-100; %constant elevation
so1.set(no1,do1,fo1);
so2.set(no2,do2,fo2);
xobs=so2.getA;
zobs=fo2*ones(no2);

%model
%center of cells
fm2=dm2/2;
sm2a=dm2/2:dm2:xmax-dm2;
sm2=Sampling;
sm2.set(length(sm2a),dm2,sm2a(1));
at=0.1; %percentage pad above ztm
ab=0.1; %percentage pad below ztp
min1=(1-at)*ztm;
max1=(1+ab)*zbp;
sm1a=min1:dm1:max1;
max1=max(sm1a); %avoid having to round
sm1=Sampling;
sm1.set(length(sm1a),dm1,sm1a(1));
%node points
snd1=Sampling;
snd2=Sampling;
snd1.set(sm1.n+1,sm1.d,sm1.f-sm1.d/2);
snd2.set(sm2.n+1,sm2.d,sm2.f-sm2.d/2);
snd1a=getA(snd1);
snd2a=getA(snd2);

%% Section 1: Forward model data
big=100;
ztrue=[zt;zt+h];
xtrue=[-(big+1)*xmax;(big)*xmax];
[XX,XZ,ZZ]=GG2D(xtrue,ztrue,xobs,zobs);



% %% Section 1: Simulate data
% obs=Obs2D;
% pad=0.75;
% obs.dx2 =cello;
% p2=floor(mt.n2*pad)*mt.dx2; %padding
% obs.xobs=(mt.o2-p2 :obs.dx2: mt.o2+mt.dx2*(mt.n2-1)+p2)';
% obs.n2  =length(obs.xobs);
% obs.o2  =min(obs.xobs);
% obs.zobs=-100*ones(length(obs.xobs),1);
% n=obs.n2;
% [zS,xS]=getNodes(mt);
% [GXX,GXZ,GZZ]=GG2D(xS,zS,obs.xobs,obs.zobs);
% G=GZZ; %for now just deal with Tzz
% noise=0+sd*randn(obs.n2,1); 
% obs.data=G*mod_true+noise;
% disp('Finished simulating data')