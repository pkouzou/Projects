function medianFilter(degraded, normal)

[M,N] = size(degraded);
fixedImage = degraded;

for i = 1:M
    for j = 1:N
       ti = i - (i >= 2):i + (i < M);
       tj = j - (j >= 2):j + (j < N);
       fixedImage(i, j) = median(median(degraded(ti,tj)));
    end
end

MSE = (1/(M*N))*sum(sum((fixedImage-normal).^2))
imshow(fixedImage);
title('median 3x3');
xlabel(strcat('MSE = ', num2str(MSE)));