function minFilter(degraded, normal)

[M,N] = size(degraded);
fixedImage = degraded;

for i = 1:M
    for j = 1:N
       ti = i - (i >= 2):i + (i < M);
       tj = j - (j >= 2):j + (j < N);
       fixedImage(i, j) = min(min(degraded(ti,tj)));
    end
end

MSE = (1/(M*N))*sum(sum((fixedImage-normal).^2))
imshow(fixedImage);
title('min 3x3');
xlabel(strcat('MSE = ', num2str(MSE)));