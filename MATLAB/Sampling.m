%Sampling class

classdef Sampling < handle
    properties(SetAccess=public)
        n; %total number
        d; %spacing
        f; %first
    end   
    methods 
        %returns array representing sampling
        function a=getA(s)
            a=s.f:s.d:s.f+s.d*(s.n-1); 
        end   
        %set properties of sampling s
        function set(s,n,d,f)
            s.n=n;
            s.d=d;
            s.f=f;
        end
    end
end
        
        