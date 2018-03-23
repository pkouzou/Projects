function LPG_Convert(c, A, a, B, b, J)
    [m, n] = size(A);
    [k, n] = size(B);
    low_bounds(1:n) = -Inf;
    upper_bounds(1:n) = Inf;
    low_bounds(1:J) = 0;
    
    x = linprog(c, -B, -b, A, a, low_bounds, upper_bounds)
    %x = linprog(c, B, b, A, a, low_bounds, upper_bounds)
    
    %sign unsigned variables
    [jrows, jcolumns] = size(J);
    N = n - jcolumns;
    
    unsigned_vars = [1:N];
    unsigned_vars(J) = [];
    
    low_bounds(1:n+N) = 0;
    upper_bounds(1:n+N) = Inf;
   
    A = [A, -A(:,unsigned_vars)];
    B = [B, -B(:,unsigned_vars)];
    c = [c, -c(:,unsigned_vars)];
    
    %convert into inequetion program
    tempB = [B;A;-A];
    tempb = [b,a,-a];
    
    x = linprog(c, -tempB, -tempb, [], [], low_bounds, upper_bounds)
    %x = linprog(c, tempB, tempb, [], [], low_bounds, upper_bounds)
    
    %convert into equation program
    low_bounds = [low_bounds, zeros(1,k)];
    upper_bounds(n+N+1:n+N+k) = Inf;
    
   
    B = horzcat(B, -eye(k));
    %B = horzcat(B, eye(k));
    A = horzcat(A, zeros(m,k));
    A = [A;B];
    a = [a,b];
    c = [c, zeros(1,k)];
   
    x = linprog(c, [], [], -A, -a, low_bounds, upper_bounds)
    %x = linprog(c, [], [], A, a, low_bounds, upper_bounds)
    
%input data
%B = [1 1
%1 1/4
%1 -1
%-1/4 -1
%-1 -1
%-1 1];
%b = [2 1 2 1 -1 2];
%A = [1 1/4];
%a = 1/2;
%c = [-1 -1/3];
%J = [];
    