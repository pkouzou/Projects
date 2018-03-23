function [opti_sol, z] = BranchAndBound(c, A, a, lb, ub)

opti_sol = [];
z = Inf;
bounds = [lb;ub];

while(size(bounds,1) ~= 0)
    tempb = [];
    for i = 1:2:size(bounds,1)
        lb = bounds(i,:);
        ub = bounds(i+1,:);
        [x,fval,exitflag] = linprog(c,A,a,[],[],lb,ub); %todo -A,-a
        
        if exitflag == -2  || fval > z %if exitflag == -2 then infeasible
            continue;
        end
        %check if all solutions are integer
        for j = 1:size(x,1)
           k = abs(round(x)-x) <= 10^-8;
           all_int = all(k);
           if abs(x(j) - floor(x(j))) > 10^-8 %is not an integer
               lb1 = lb; ub1 = ub; lb2 = lb; ub2 = ub;
               ub1(j) = floor(x(j));
               lb2(j) = ub1(j) + 1;
               tempb = [tempb;lb1;ub1;lb2;ub2];
               break;
           end
        end
        
        if all_int
            if fval < z
                z = fval;
                opti_sol = x;
            end 
        end 
    end
    bounds = tempb;
end