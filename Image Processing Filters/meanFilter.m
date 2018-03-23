function meanFilter(degraded, normal)

[M,N] = size(degraded);
fixedImage = degraded;

%στο πεδίο του χώρου
for i = 1:M
    for j = 1:N
       ti = i - (sum([i-2, i-1]) >= 2):i + (sum([i+2, i+1]) < M);
       tj = j - (sum([j-2, j-1]) >= 2):j + (sum([j+2, j+1]) < N);
       fixedImage(i, j) = mean(mean(degraded(ti,tj)));
    end
end

MSE = (1/(M*N))*sum(sum((fixedImage-normal).^2))
figure
imshow(fixedImage)
title('mean 5x5');
xlabel(strcat('MSE = ', num2str(MSE)));