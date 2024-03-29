package com.bitharmony.comma.credit.charge.controller;

import com.bitharmony.comma.credit.charge.dto.*;
import com.bitharmony.comma.credit.charge.entity.Charge;
import com.bitharmony.comma.credit.charge.service.ChargeService;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/credit")
public class ChargeController {

    private final ChargeService chargeService;
    private final MemberService memberService;

    @GetMapping("/charges/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChargeGetResponse> getCharge(@PathVariable long id) {

        Charge charge = chargeService.getChargeById(id);

        return new ResponseEntity<>(
                ChargeGetResponse.builder()
                        .username(charge.getCharger().getUsername())
                        .chargeAmount(charge.getChargeAmount())
                        .chargeCode(charge.getCode())
                        .createDate(charge.getCreateDate())
                        .payDate(charge.getPayDate())
                        .paymentKey(charge.getPaymentKey())
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping("/charges/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChargeGetListResponse> getMyChargeList(Principal principal) {

        Member member = memberService.getMemberByUsername(principal.getName());
        List<Charge> charges = chargeService.getChargeListByMemberId(member.getId());

        ChargeGetListResponse chargeGetListResponse = ChargeGetListResponse.builder()
                .chargeDtos(charges.stream().map(ChargeDto::new).toList())
                .build();

        return new ResponseEntity<>(chargeGetListResponse, HttpStatus.OK);
    }

    @GetMapping("/charges")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ChargeGetListResponse> getAllChargeList() {

        List<Charge> charges = chargeService.getChargeList();

        ChargeGetListResponse chargeGetListResponse = ChargeGetListResponse.builder()
                .chargeDtos(charges.stream().map(ChargeDto::new).toList())
                .build();

        return new ResponseEntity<>(chargeGetListResponse, HttpStatus.OK);
    }

    // Charge 객체 생성 & 저장 후 해당 객체를 Response에 실어 보냄
    // Response의 chargeId 값으로 "/charge/pay/{id}"로 리다이렉트하여 결제 진행
    @PostMapping("/charges")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChargeCreateResponse> createCharge(
            @RequestBody ChargeCreateRequest chargeCreateRequest,
            Principal principal) {

        Member member = memberService.getMemberByUsername(principal.getName());
        Charge charge = chargeService.createCharge(member, chargeCreateRequest.chargeAmount());

        return new ResponseEntity<>(
                ChargeCreateResponse.builder()
                        .chargeId(charge.getId())
                        .chargeCode(charge.getCode())
                        .username(charge.getCharger().getUsername())
                        .build(),
                HttpStatus.CREATED
        );
    }

    // 결제 요청 정보와 서버에 저장된 주문서 정보가 일치하는 지 확인하는 메서드
    @PostMapping(value = "/confirm")
    public ResponseEntity<ChargeConfirmResponse> confirmPayment(@RequestBody ChargeConfirmRequest chargeConfirmRequest) {

        String orderId = chargeConfirmRequest.orderId();
        String amount = chargeConfirmRequest.amount();
        String paymentKey = chargeConfirmRequest.paymentKey();

        ChargeConfirmResponse chargeConfirmResponse =
                chargeService.confirmPayment(orderId, amount, paymentKey);

        return new ResponseEntity<>(chargeConfirmResponse, HttpStatus.OK);
    }
}
