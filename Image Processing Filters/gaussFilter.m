function gaussFilter(degraded, normal)

[M,N] = size(degraded);
fixedImage = degraded;

D0 = 35;
H = zeros(M,N);
center = [round(M/2), round(N/2)];
for i = 1:M
    for j = 1:N
       D  = (i - center(1))^2 + (j - center(2))^2;
       H(i,j) = exp(-D/(2*D0^2));
    end 
end

F = fftshift(fft2(degraded));

dFiltered = F.*H;
fixedImage = uint8(real(ifft2(ifftshift(dFiltered))));
MSE = (1/(M*N))*sum(sum((fixedImage-normal).^2))
figure
imshow(uint8(ifft2(ifftshift(dFiltered))));
title(strcat('Gauss D0 = ', num2str(D0)));
xlabel(strcat('MSE = ', num2str(MSE)));