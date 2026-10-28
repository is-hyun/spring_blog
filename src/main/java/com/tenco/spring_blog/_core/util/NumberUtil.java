package com.tenco.spring_blog._core.util;

import java.text.DecimalFormat;

// 숫자 관련 금액/포인트 유틸리티 클래스
// static 메서드로 구성하여 객체 생성 없이 바로 사용 가능
public class NumberUtil {

    // 숫자를 천 단위 콤마(,) 포맷의 문자열로 변환
    public static String numberFormat(int number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }
}
