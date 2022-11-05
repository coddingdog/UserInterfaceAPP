package com.echo.echofarm.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.echo.echofarm.Data.Dto.GetPostDto;
import com.echo.echofarm.Data.Dto.SendPostDto;
import com.echo.echofarm.Interface.GetImgUrlListener;
import com.echo.echofarm.Interface.StoreImgListener;
import com.echo.echofarm.R;
import com.echo.echofarm.Service.Impl.PostServiceImpl;
import com.echo.echofarm.Service.PostService;

import java.util.Arrays;

public class MakePostActivity extends AppCompatActivity {
    PostService postService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postService = new PostServiceImpl();

        setContentView(R.layout.activity_make_post);

        //Test용 작업들.
        //테스트 이미지들 업로드임! 실제로는 리소스가 아닌 갤러기같은데서 받도록하기!
        //일단 png만 업로드 되도록함ㅅ
        Uri uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.test1);
        Uri uri2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.test2);

        SendPostDto sendPostDto = new SendPostDto("TestUid", "TestTitle", Arrays.asList(uri1, uri2), "테스트 콘텐츠 입니다.",
                "Pencil", Arrays.asList("tag1", "tag2"), "ball", Arrays.asList("tag3", "tag4"), false);

        //어디까지나 사용 예시임! 아래는 각 함수 사용법 활용을 위한 예제. 리스너에 인텐트 달아서 연결시키거나 하는방식으로 사용
        postService.sendPostDto(sendPostDto, new StoreImgListener() {
            //각 이미지들의 저장이 끝났을 때 호출되는 콜백
            @Override
            public void onSuccess(String postId) {
                //getPostDto의 사용예시임! postId는 랜덤?생성임.
                postService.getPostDto(postId, new GetImgUrlListener() {
                    //각 이미지의 url을 불러오는것이 성공했을 때 호출되는 콜백
                    @Override
                    public void onSuccess(GetPostDto getPostDto) {
                        //getPostDto를 이용해 ㅅ인텐트로 새창 열거나 액티비티에 대입해 활용하는 형식으로 사용하면 됨.
                        //그런데 uri리스트가 객체 반환 이후에 이미지가 조금씩 추가되서 늦게 화면을 띄우거나 새로고침이 필요함.
                        System.out.println(getPostDto);
                    }

                    @Override
                    public void onFailed() {

                    }
                });

            }

            @Override
            public void onFailed() {

            }
        });
    }

}