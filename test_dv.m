%Test parameters for velocity uncertainty

A=4; %constant
v=3500; %stacking velocity 
f=25; %characteristic frequency
t=2; %time
x=8000; %offset
dv=A*v*v*v/f/t/x/x %stacking velocity error


vmin=1500; 
vmax=2500;
dt2min=10; 
dt2max=10000;
thickmin=1;
thickmax=1000;


v1=vmin:vmax;
v2=vmin:vmax;
d2t=dt2min:dt2max;
thickness=thickmin:thickmax;

t01=2*zt/v1;
t02=t01+thickness/v2;
vr1=v1;
vr2=v2
v=1500:3500;
f=8:80;
x=10:10000;
d2t=1:8000;
thickness=