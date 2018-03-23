function [T, bas_index] = simplex_phase_one(c, A, a)

[m,n] = size(A);

bas_index = [n+1:m+n];

a = -a;

if a > 0
    T = [[A, -eye(m), a]; [c 0 zeros(1,m)]];
    return;
end

indexes = [1:m];

%slack variables
art_index = find(a < 0);
art = zeros(m, size(art_index,1));
k = 1;
for i = 1:size(art_index, 1)
   art(art_index(i), k) = 1;
   k = k + 1;
end

indexes(art_index) = [];

art_cost = zeros(1, m+n+size(art_index,1)+1);
art_cost([m+n+1:m+n+size(art_index,1)]) = 1;
T = [[A, -eye(m), art, a];[c zeros(1,m+size(art_index,1)) 0]];
T = [T;art_cost];

T(art_index, :) = -T(art_index,:);
for i = 1:size(art_index,1)
    T(size(T,1),:) = T(size(T,1), :) + T(art_index(i),:);
    r = art_index(i);
    s = m+n+i;
    bas_index(r) = s;
end

PreviousPivot = -1;

while (1 == 1)
    
    negative = find(T(size(T,1),[1:size(T,2)-1]) < 0);
   
    if size(negative, 2) == 0
        break;
    end
    s = negative(1); %first negative column
    
    if sum(T([1:m],s) >= 0) == m
        T = [];
        bas_index = [];
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

T(size(T,1), :) = [];
T(:,[n+m+1:n+m+size(art_index, 1)]) = [];

art_index = [art_index+m+n];

for i = 1:size(art_index,1)
   if sum(find(bas_index == art_index(i))) > 0
       disp('No solution');
       T = [];
       return;
   end
end