%find inverse matrix using pivots
function [] = inverse_matrix(A)

[m,n] = size(A);

if m ~= n
    return;
end

pivots_done = [];
depended_row = -1;
column_index = [1:n];
rank = 0;

T = totbl(A,zeros(1,n)');

%calculate rank making pivots
for i = 1:n
    done = 0;
    for j = 1:size(column_index, 2)
       if T.val(i,column_index(j)) ~= 0
          T = ljx(T, i, column_index(j));
          pivots_done = [pivots_done;[i,column_index(j)]];
          column_index(j) = [];
          rank = rank + 1;
          done = 1;
          break;
       end
    end
    if done == 0
        depended_row = i;
    end
end
pivots_done
rank
%A has inverse
if rank == n
    %make proper exchanges
    exchanges_done = zeros(1,n);
    for i = 1:n
       k1 = pivots_done(i, 1);
       k2 = pivots_done(i, 2);
       if exchanges_done(k1) == 0 && exchanges_done(k2) == 0 && k1 ~= k2
           %exchange labels
           temp = T.bas(k1);
           T.bas(k1) = T.bas(k2);
           T.bas(k2) = temp;
           %exchange nonbas labels
           temp = T.nonbas(k1);
           T.nonbas(k1) = T.nonbas(k2);
           T.nonbas(k2) = temp;
           %exchange columns
           temp = T.val(:, k1);
           T.val(:,k1) = T.val(:,k2);
           T.val(:,k2) = temp;
           %exchange rows
           temp = T.val(k1,:);
           T.val(k1,:) = T.val(k2,:);
           T.val(k2,:) = temp;
     
           exchanges_done(k1) = 1; 
           exchanges_done(k2) = 1; 
       end
    end
    inverse = T.val(:,1:n)
    %verification
    verification = A*inverse - eye(n)
else
    %print dependency
    s = T.bas(depended_row);
    s = strcat(s, {' = '});
    N = size(pivots_done, 1);
    for i = 1:N-1
       s = strcat(s, {' '}, num2str(T.val(depended_row, pivots_done(i,2))));
       s = strcat(s, {'*'}, T.nonbas(pivots_done(i,2)), {' + '});
    end
    s = strcat(s, num2str(T.val(depended_row, pivots_done(N,2))));
    s = strcat(s, {'*'}, T.nonbas(pivots_done(N,2)));
    dependency = s
end


