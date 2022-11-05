package com.echo.echofarm.Interface;

import com.echo.echofarm.Data.Dto.GetPostDto;

public interface GetImgUrlListener {
    void onSuccess(GetPostDto getPostDto);
    void onFailed();
}
