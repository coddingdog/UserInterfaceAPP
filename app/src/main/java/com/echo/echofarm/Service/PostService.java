package com.echo.echofarm.Service;

import com.echo.echofarm.Data.Dto.SendPostDto;
import com.echo.echofarm.Interface.GetImgUrlListener;
import com.echo.echofarm.Interface.StoreImgListener;

public interface PostService {

    void sendPostDto(SendPostDto sendPostDto, StoreImgListener sendPostListener);

    void getPostDto(String postId, GetImgUrlListener getPostListener);

}
