function [] = solve_with_pivots(A,a)
[m,n] = size(A);

T = totbl(A,a);
pivots_done = [];
column_index = [1:n];
solvable = 1;
temp= '';

%make pivots
for i = 1:m
    done = 0;
    for j = 1:size(column_index, 2)
        if abs(T.val(i, column_index(j))) > 0.001
            T = ljx(T, i, column_index(j));
            pivots_done = [pivots_done;[i, column_index(j)]];
            column_index(j) = [];
            done = 1;
            break;
        end
    end
    if done == 0
       if abs(T.val(i, n+1)) > 0.001 
           solvable = 0;
           temp= 'No solution';
           disp(char(10));
           disp(temp);
           break;
       end
    end
end

pivots_not_done = column_index;

disp(char(10))

%print the solution
if solvable == 1
    for i = 1:size(pivots_done,1)
       temp= '';
       temp= strcat(temp, T.bas(pivots_done(i,1)), {' = '});
       for j = 1:size(pivots_not_done, 2)
           if j < size(pivots_not_done,2)
               s = ' + ';
           else
               s = '';
           end
           temp= strcat(temp,... 
           num2str(T.val(pivots_done(i,1), pivots_not_done(j))), '*',...
           T.nonbas(pivots_not_done(j)), s);
       end
       temp= strcat(temp, {' + '}, num2str(T.val(pivots_done(i,1)...
       , n+1)));
       disp(temp)
    end
    for j = 1:size(pivots_not_done, 2)
       temp= '';
       temp= strcat(temp, T.nonbas(pivots_not_done(j)),...
           {'åR'});
       disp(temp)
    end
end
