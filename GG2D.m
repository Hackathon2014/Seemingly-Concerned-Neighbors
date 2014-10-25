function [XX,XZ,ZZ] = GG2D(xs,zs,xo,zo,unpack1)
if nargin <5
    unpack1=1;
end
%Sensitivity matrix for a 2D GG inversion
%Code modified based on a version written by Joe Capriotti
%
%Outputs 
% XX - Sensitivity matrix for Gxx
% XZ - Sensitivity matrix for Gxz
% ZZ - Sensitivity matrix for Gzz
%Inputs
% xs,zs = source node locations (edges of each cube in 2D model)
% xos, zos = observation locations (z is positive down)
nx=length(xs)-1; %since number of nodes is one greater than cells in x
nz=length(zs)-1;
n=nx*nz;
c=2*6.67384E1;   %twice the gravitational constant
nd=length(zo);
XX=zeros(nd,n);
XZ=XX;
ZZ=XX;
L=1;
if unpack1 %first dimension is fastest
    x1=xs(1)-xo; %update left 
    for ix = 1:nx
        x2=xs(ix+1)-xo; %update right 
        h1=zs(1)-zo; %update top
        for iz=1:nz
            h2=zs(iz+1)-zo; %update bottom
            [XX(:,L),XZ(:,L),ZZ(:,L)]=ggCube(x1,x2,h1,h2);
            h1=h2;
            L=L+1;
        end
        x1=x2; %update left as new right
    end 
else %second dimension is fastest
    h1=zs(1)-zo; %update top
    for iz=1:nz
        h2=zs(iz+1)-zo; %update bottom
        x1=xs(1)-xo; %update left
        for ix=1:nx
            x2=xs(ix+1)-xo;
            [XX(:,L),XZ(:,L),ZZ(:,L)]=ggCube(x1,x2,h1,h2);
            x1=x2;
            L=L+1;
        end
        h1=h2;
    end
end
XX=c*XX;
XZ=c*XZ;
ZZ=c*ZZ;
end

%% 'private' methods 

%Computes gg response based on vectors pointing from observation location
%to two vertices defining cube.
%Outputs
% gg response (xx,xz,zz)
%Inputs
% x1,z1 - vector pointint from obs to top-left corner of cube 
% x2,z2 - vector pointing from obs to bottom-right
function [xx,xz,zz] = ggCube(x1,x2,z1,z2)
    xx=-dxxnd(x1,z2)+dxxnd(x1,z1)-dxxnd(x2,z1)+dxxnd(x2,z2);
    xz=-dxznd(x1,z2)+dxznd(x1,z1)-dxznd(x2,z1)+dxznd(x2,z2);
    zz=-dzznd(x1,z2)+dzznd(x1,z1)-dzznd(x2,z1)+dzznd(x2,z2);
end
%help with xx
function d=dxxnd(x,z)
    d=atan2(x,z);
end
%help with zz
function d=dzznd(x,z)
    d=atan2(z,x);
end
%help with xz
function d=dxznd(x,z)
    r=sqrt(x.*x+z.*z);
    d=log(x./r);
end





