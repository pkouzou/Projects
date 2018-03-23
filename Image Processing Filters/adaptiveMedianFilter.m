function adaptiveMedianFilter(degraded, normal)

[M,N] = size(degraded);
fixedImage = degraded;

Smax = 15;

for i = 1:M
    for j = 1:N
        size_ = 3;
        while (size_ <= Smax)
            ti = [i - sum([i-floor(size_/2):i-1] >= 2):i + sum([i+1:i+floor(size_/2)] < M)];
            tj = [j - sum([j-floor(size_/2):j-1] >= 2):j + sum([j+1:j+floor(size_/2)] < N)];
            Sxy = degraded(ti,tj);
            Zmin = min(min(Sxy));
            Zmax = max(max(Sxy));
            Zmedian = median(median(Sxy));
            A1 = Zmedian - Zmin;
            A2 = Zmedian - Zmax;
            if (A1 > 0 & A2 < 0)
                B1 = degraded(i,j) - Zmin;
                B2 = degraded(i,j) - Zmax;
                if ~(B1 > 0 & B2 < 0)
                   fixedImage(i,j) = Zmedian;
                end
                break;
            else
                size_ = size_ + 2;
            end
        end
        if (size_ > Smax)
           fixedImage(i,j) = Zmedian; 
        end
    end
end

MSE = (1/(M*N))*sum(sum((fixedImage-normal).^2))
figure
imshow(fixedImage)
title('adaptive median maxSize = 15');
xlabel(strcat('MSE = ', num2str(MSE)));


