package com.bitharmony.comma.global.init;

import com.bitharmony.comma.credit.charge.repository.ChargeRepository;
import com.bitharmony.comma.credit.charge.service.ChargeService;
import com.bitharmony.comma.credit.creditLog.entity.CreditLog;
import com.bitharmony.comma.credit.creditLog.service.CreditLogService;
import com.bitharmony.comma.credit.withdraw.service.WithdrawService;
import com.bitharmony.comma.member.entity.Member;
import com.bitharmony.comma.member.repository.MemberRepository;
import com.bitharmony.comma.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.ApplicationArguments;

@Profile("!prod")
@Configuration
@RequiredArgsConstructor
public class InitData {


    private final ChargeService chargeService;
    private final ChargeRepository chargeRepository;
    private final CreditLogService creditLogService;
    private final WithdrawService withdrawService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;


    @Bean
    public ApplicationRunner run() {
        return new ApplicationRunner() {
            @Override
            @Transactional
            @SneakyThrows
            public void run(ApplicationArguments args) {

                if(memberRepository.count() > 0) {return;}

                memberService.join("admin", "1234", "1234", "admin@abc.com", "admin");
                memberService.join("user1", "1234", "1234", "user1@abc.com", "nick1");
                memberService.join("user2", "1234", "1234", "user2@abc.com", "nick2");
                Member member = memberService.getMemberByUsername("user1");
                creditLogService.addCreditLog(member, CreditLog.EventType.충전__토스페이먼츠, 10000);
                creditLogService.addCreditLog(member, CreditLog.EventType.충전__토스페이먼츠, 10000);
                creditLogService.addCreditLog(member, CreditLog.EventType.충전__토스페이먼츠, 10000);

            }
        };
    }
}
