%Test parameters for velocity uncertainty
close all
clear all

%Quick test for set values
A=4; %constant
v=3500; %stacking velocity 
f=25; %characteristic frequency
t=2; %time
x=8000; %offset
dv=A*v*v*v*t/f/x/x; %stacking velocity error

%% More robust testing
perc=0.1;
%Bounds on parameters
vamin=1500;        %m/s
vamax=2500;        %m/s 2000 and 2200 used in app
ztamin=1;          %m
ztamax=10000;      %m
Tamin=1;           %m
Tamax=1000;        %m
famin=8;           %Hz
famax=80;          %Hz
xamax=10000;       %m (max offset = 700:10000)
xamin=perc*xamax;  %m
v1a=vamin:vamax;   %m/s
v2a=vamin:vamax;   %m/s
zta=ztamin:ztamax; %m
Ta=Tamin:Tamax;    %m
fa=famin:famax;    %m
nxa=75;
dxa=(xamax-xamin)/nxa;
xa=xamin:dxa:xamax;    %m
nxa=length(xa);


%get some randome values in our ranges
rv1i=randi(length(v1a));
rv2i=randi(length(v2a));
rzti=randi(length(zta));
rTi =randi(length(Ta));
rfi =randi(length(fa));
rxi =randi(length(xa));
%simulate user inputs
v1=v1a(rv1i);
v2=v1a(rv1i);
zt=zta(rzti);
T=Ta(rTi);
f=fa(rfi);
x=xa(rxi);

v1=2000; % velocity upper layer
v2=2200; % velocity lower layer
zt=1000;
T=100;
f=80; %characteristic frequency
% x=8000; %offset
nsim=100;
dxa=100
j=0;
clay=[123,146,168];
figure(1); fill([min(xa),max(xa),max(xa),min(xa)],[zt,zt,zt+T,zt+T],...
                'w')
hold on
for x=linspace(xamin,xamax,length(xa))
    j=j+1;
    %Compute depth bounds
    t01=2*zt/v1;
    t02=t01+2*T/v2;
    vr1=v1;
    vr2=sqrt((v1*v1*t01+v2*v2*t02)/(t01+t02));
    t1x=sqrt(t01*t01+(x*x/vr1/vr1));
    t2x=sqrt(t02*t02+(x*x/vr2/vr2));
    dvrms1=(4*t1x*power(vr1,3))/(f*power(x,2));
    dvrms2=(4*t2x*power(vr2,3))/(f*power(x,2));
    r1=(t1x*vr1)/2;
    r2=(t2x*vr2)/2;
    theta1=asind(x/(2*r1));
    theta2=asind(x/(2*r2));
    ztc(j)=((t1x*cosd(theta1))/2)*(vr1);
    ztu=((t1x*cosd(theta1))/2)*(vr1+dvrms1);
    ztl=((t1x*cosd(theta1))/2)*(vr1-dvrms1);
    zbc(j)=((t2x*cosd(theta2))/2)*(vr2);
    zbu=((t2x*cosd(theta2))/2)*(vr2+dvrms2);
    zbl=((t2x*cosd(theta2))/2)*(vr2-dvrms2);
    %display bounds for this user input
    figure(1);
    plot([x,x],[ztu,ztl],'-gs','markerfacecolor','g','linewidth',0.9)
    plot([x,x],[zbu,zbl],'-bs','markerfacecolor','b','linewidth',0.9)
end
figure(1); 
lwl=1.1;
% plot(xa,zt*ones(length(xa))      ,'w','linewidth',lwl); hold on;
% plot(xa,(zt+T)*(ones(length(xa))),'w','linewidth',lwl);
% plot(xa,ztc,'k')
% plot(xa,zbc,'k')
hold off
set(gca,'Ydir','reverse')
xlabel('Offset [m]'); ylabel('Depth [m]');
title('Depth estimate error vs. offset');
set(findall(gcf,'type','text'),'FontSize',16,'FontName','Arial')


