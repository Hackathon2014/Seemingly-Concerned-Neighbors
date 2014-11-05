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

xamax=10000;       %m

xamin=perc*xamax;  %m
v1a=vamin:vamax;   %m/s
v2a=vamin:vamax;   %m/s
zta=ztamin:ztamax; %m
Ta=Tamin:Tamax;    %m
fa=famin:famax;    %m
xa=xamin:xamax;    %m


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

v1=2200; % velocity upper layer
v2=2400; % velocity lower layer
zt=5000;
T=1100;
f=25; %characteristic frequency
thetac=asind(v1/v2);

% x=8000; %offset
nsim=100;
while j<nsim
    %random values for parameter you want to test
    rxi =randi(length(xa));
    x=xa(rxi);

    %Compute depth bounds
    %Two-way travel time in a zero offset case
    t01=2*zt/v1;
    t02=t01+2*T/v2;

    %Calculating RMS velocities. 
    vr1=v1;
    vr2=sqrt((v1*v1*t01+v2*v2*t02)/(t01+t02));
    
    %Calculating travel time as a function of offset. 
    t1x=sqrt(t01*t01+(x*x/vr1/vr1));
    t2x=sqrt(t02*t02+(x*x/vr2/vr2));
    A1=4; A2=4;

    %Equation set 5 calculating Delta Vrms
    dvrms1=(4*t1x*power(vr1,3))/(f*power(x,2));
    dvrms2=(4*t2x*power(vr2,3))/(f*power(x,2));
    
    %Equation set 6 calculating r values
    r1=(t1x*vr1)/2;
    r2=(t2x*vr2)/2;
    
    %Equation set 7 calculating Theta values
    theta1=asind(x/(2*r1));
    theta2=asind(x/(2*r2));
    
    %calculating angles for snells interface
    test=(v2/v1)*sind(theta1);
    stheta2=asind((v2*sind(theta1))/v1);%Angle of transmitted ray
    t02s=((2*T)/(v2*cosd(stheta2)));%Two way travel time in second layer
    t2xs=t1x+t02s;%Two way travel time in both layers
    dv2=(4*t2xs*power(v2,3))/(f*power(x,2)); %Uncertainty based on second layer
    %above is comparable to dvrms2, but only using second interval velocity
    %Should uncertainty only be from the layer?
 
    
    
    ztc=((t1x*cosd(theta1))/2)*(vr1);
    ztu=((t1x*cosd(theta1))/2)*(vr1+dvrms1);
    ztl=((t1x*cosd(theta1))/2)*(vr1-dvrms1);
    %zbc=((t2x*cosd(theta2))/2)*(vr2);
    %zbu=((t2x*cosd(theta2))/2)*(vr2+dvrms2);
    %zbl=((t2x*cosd(theta2))/2)*(vr2-dvrms2);
    
    %This is where new calculations happen for bottom layer
    disp(theta1);
    zbc=((t2xs*cosd(stheta2))/2)*(v2);
    zbu=((t2xs*cosd(stheta2))/2)*(v2+dv2);
    zbl=((t2xs*cosd(stheta2))/2)*(v2-dv2);

%     Issues with critical angle will arise when theta1>thetac (when angle
%     of incidence is greater than critical angle). This will show up with
%     complex values of the inverse sine function. Where this  offset
%     occurs is dependent on depth, velocities, frequency, and thickness.
%     The bounds for the bottom layer, calculated from angles beyond the
%     critical angle, are placed incorrectly. For now, we might want to
%     ignore calculations for offsets beyond when theta1>thetac.
    
    
%Plot everything
    figure(1); hold on; 

%     plot(x,ztc,'ks')
%     plot(x,zbc,'ko')
    plot([x,x],[ztu,ztl],'-gs','markerfacecolor','g','linewidth',0.9)
    plot([x,x],[zbu,zbl],'-bs','markerfacecolor','b','linewidth',0.9)

      plot(x,ztc,'ko')
      plot(x,zbc,'ko')
%      plot([x,x],[ztu,ztl],'-gx')
%      plot([x,x],[zbu,zbl],'-bx')

    j=j+1;
end
figure(1); 
lwl=1.1;
plot(xa,zt  ,'k','linewidth',lwl); hold on;
plot(xa,zt+T,'k','linewidth',lwl); hold off
set(gca,'Ydir','reverse')
xlabel('Offset [m]'); ylabel('Depth [m]');
title('Depth estimate error vs. offset');
disp(thetac);

