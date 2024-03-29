package com.bitharmony.comma.credit.creditLog.service;

import com.bitharmony.comma.credit.creditLog.entity.CreditLog;
import com.bitharmony.comma.credit.creditLog.repository.CreditLogRepository;
import com.bitharmony.comma.global.exception.credit.CreditLogNotFoundException;
import com.bitharmony.comma.member.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditLogService {

    private final CreditLogRepository creditLogRepository;


    public CreditLog getCreditLogById(long id){
        Optional<CreditLog> creditLog = this.creditLogRepository.findById(id);

        if (creditLog.isEmpty()) {
            throw new CreditLogNotFoundException();
        }

        return creditLog.get();
    }

    public Page<CreditLog> getMyCreditLogs(Long id, Pageable pageable) {
        Page<CreditLog> creditLogs = creditLogRepository.findByMemberId(id, pageable);

        return creditLogs;
    }

    @Transactional
    public void addCreditLog(Member member, CreditLog.EventType eventType, long creditChangeAmount) {
        CreditLog creditLog = CreditLog.builder()
                .member(member)
                .eventType(eventType)
                .creditChangeAmount(creditChangeAmount)
                .createDate(LocalDateTime.now())
                .restCredit(member.getCredit())
                .build();

        creditLogRepository.save(creditLog);
    }
}
