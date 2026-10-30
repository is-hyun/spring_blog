package com.tenco.spring_blog.refund;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog.payment.PaymentResponse;
import com.tenco.spring_blog.payment.PortOneApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PortOneRefundService {

    private final RefundService refundService;
    private final PortOneApiClient portOneApiClient;

    public void approve(Long refundId) {
        // 1. 승인 가능 여부
        RefundResponse.ApproveDto approveDto = refundService.checkApprove(refundId);

        // 2. 포트원 결제 취소 요청
        PaymentResponse.PortOneCancel portOneCancel;
        try {
            portOneCancel = portOneApiClient.cancelPayment(approveDto.getPaymentId(), approveDto.getAmount());
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }

        // 3. 취소 완료 상태 확인
        if (portOneCancel == null
                || portOneCancel.getCancellation() == null
                || !"SUCCEEDED".equals(portOneCancel.getCancellation().getStatus()) ) {
            throw new Exception400("결제취소가 완료되지 않았습니다. 잠시 후 포트원 콘솔에서 확인해주세요");
        }

        // 4. DB 반영
        refundService.approve(refundId);
    }
    
}
