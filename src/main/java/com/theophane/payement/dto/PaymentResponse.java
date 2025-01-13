package com.theophane.payement.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String message;
    private PaymentData data;

    @Data
    public static class PaymentData {
        private Long id;
        private String createtime;
        private String subscriberMsisdn;
        private Integer amount;
        private String payToken;
        private String txnid;
        private String txnmode;
        private String inittxnmessage;
        private String inittxnstatus;
        private String confirmtxnstatus;
        private String confirmtxnmessage;
        private String status;
        private String notifUrl;
        private String description;
        private String channelUserMsisdn;
    }
}
