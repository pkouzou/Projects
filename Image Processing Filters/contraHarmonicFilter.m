function contraHarmonicFilter(degraded, normal)

[M,N] = size(degraded);
Q = [-1.5, 1.5, 3];
degraded_ = im2double(degraded);
normal = im2double(normal);

for index = 1:size(Q,2)
    for i = 1:M
        for j = 1:N
           ti = i - (i >= 2):i + (i < M);
           tj = j - (j >= 2):j + (j < N);
           fixedImage(i,j) = sum(sum(degraded_(ti,tj).^(Q(index)+1)))...
                                /sum(sum(degraded_(ti,tj).^(Q(index))));
        end
    end 
    
    figure
    imshow(fixedImage);
    title(strcat('contraharmonic Q = ', num2str(Q(index))));
end