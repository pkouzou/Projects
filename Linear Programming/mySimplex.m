function [] = mySimplex(c,A,a)

[m,n] = size(A);
[T,bas_index] = simplex_phase_one(c,A,a);
[T,x,y] = simplex_phase_two(T, m, n, bas_index);

x
primal_cost = sum(x.*c')
y
dual_cost = sum(y.*a')