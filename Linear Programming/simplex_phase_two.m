function [T, x, y] = simplex_phase_two(tabl, m, n, bas_index) %m,n size of array

T = tabl;
x = zeros(1,n);

PreviousPivot = -1;

while (1 == 1)
    negative = find(T(size(T,1), [1:size(T,2)-1]) < 0);
    
    if size(negative, 2) == 0
        break;
    end
    
    s = negative(1); %first negative column
    
    if sum(T([1:m],s) >= 0) == m
        disp('Unbounded');
        return;
    end
    r = LexMin(T, s, m, n, bas_index);
    index = [1:size(T,1)]; index(r) = [];
    Tnext = T;
    Tnext(index,:) = (-T(r,s)*T(index,:) + T(index,s)*T(r,:))/abs(PreviousPivot);
    T = Tnext;
    PreviousPivot = T(r,s);
    bas_index(r) = s;
end

x = zeros(1,n);
for i = 1:size(bas_index,2)
   j = bas_index(i);
   if j <= n
       x(j) = -T(i,size(T,2))/T(i,j);
   end
end

x = x';

%dual solution
y = zeros(1, m);
k = 1;
l = 0;
ar = T([1:m],bas_index);
for i = n+1:n+m
    if sum(find(i == bas_index)) == 0
        y(k) = T(size(T,1), i)/(-sum(ar(:,k-l)));   
    else
       l = l + 1; 
    end
    k = k + 1;
end