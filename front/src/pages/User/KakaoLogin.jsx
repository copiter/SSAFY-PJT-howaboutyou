import React from 'react'
import { useEffect } from 'react';
import axios from 'axios';
import {useNavigate} from 'react-router-dom';
import { useDispatch } from 'react-redux';
import api from '../../api/api';
import jwt_decode from "jwt-decode";
import setAuthorizationToken from './AuthorizationToken';
import { userAction } from "../../_slice/UserSlice";


function KakaoLogin() {
    const dispatch = useDispatch();

    const PARAMS = new URL(document.location).searchParams;
    const KAKAO_CODE = PARAMS.get('code');

    const navigate = useNavigate();

    function getCurrentLocation() {
        axios.get(api.getCurrentLocation())
        .then((Response) => {
          const result = Response.data.response;
        //   console.log(result);

          // nickname, location 정보 store에 저장
          dispatch(userAction.latitude(result.lat));
          dispatch(userAction.longitude(result.lng));
          dispatch(userAction.areaName(result.address));

          navigate("/main");
        })
    }

    useEffect(() => {
        axios.get(api.login(), {params:{code:KAKAO_CODE}})
        .then((Response)=> {
            const result = Response.data.response.extraData;
            if (result) {
                //로그인 정보 저장
                console.log("로그인 성공")
                const token = Response.data.response.accessToken;
                localStorage.setItem('jwtToken' , token);

                //axios 헤더 설정 
                setAuthorizationToken(token);
                
                //유저 정보 추출
                const decode = jwt_decode(token);

                // 닉네임 저장
                dispatch(userAction.nickname(decode.nickname));

                // console.log(decode)
                
                getCurrentLocation();
            } else{
                //추가 정보 입력 페이지 이동
                console.log("추가정보 페이지 이동")
                const kakaoId = Response.data.response.kakaoId;
                navigate("/signup", {state: {"kakaoId": kakaoId}});
            }
        })
        .catch((Error)=>{alert('로그인 오류'); navigate('/')})
    });

}

export default KakaoLogin