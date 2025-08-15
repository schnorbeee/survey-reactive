package com.dynata.survayhw.services;

import com.dynata.survayhw.dtos.MemberDto;
import com.dynata.survayhw.mappers.MemberMapper;
import com.dynata.survayhw.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    @Autowired
    public MemberService(MemberRepository memberRepository, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    public List<MemberDto> saveMemberDtos(List<MemberDto> memberDtos) {
        return memberDtos.stream()
                .map(memberMapper::toEntity)
                .map(memberRepository::save)
                .map(memberMapper::toDto)
                .toList();
    }

    public List<MemberDto> getBySurveyIdAndIsCompleted(Long surveyId) {
        return memberRepository.findBySurveyIdAndIsCompleted(surveyId).stream()
                .map(memberMapper::toDto).toList();
    }

    public List<MemberDto> getByNotParticipatedInSurveyAndIsActive(Long surveyId) {
        return memberRepository.findByNotParticipatedSurveyAndIsActive(surveyId).stream()
                .map(memberMapper::toDto).toList();
    }
}
