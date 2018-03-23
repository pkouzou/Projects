function [r] = LexMin(T, s, m, n, bas_index) %m,n size of array

row_index = [1:m];
row_index(find(T([1:m],s) >= 0)) = [];

vals = -T(row_index, size(T,2))./T(row_index,s);
minim = min(vals);
row_index = row_index(find(vals == minim));

if size(row_index, 2) == 1
    r = row_index(1);
    return;
end
slacks = T([1:m], [n+1:m+n]);

for i = 1:size(bas_index, 2)
    vals = slacks(row_index, i)./T(row_index,s);
    minim = min(vals);
    row_index = row_index(find(vals == minim));
    if size(row_index, 2) == 1
        r = row_index(1);
        return;
    end 
end