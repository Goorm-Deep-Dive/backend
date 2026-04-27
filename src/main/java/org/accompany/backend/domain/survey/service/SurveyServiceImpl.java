package org.accompany.backend.domain.survey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.entity.UserDocumentChecklist;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.procedure.entity.DueDateType;
import org.accompany.backend.domain.procedure.entity.DueDateUnit;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.procedure.repository.ChecklistBulkRepository;
import org.accompany.backend.domain.procedure.repository.ProcedureRepository;
import org.accompany.backend.domain.survey.dto.response.SurveyAnswerRes;
import org.accompany.backend.domain.survey.dto.response.SurveyListRes;
import org.accompany.backend.domain.survey.dto.response.SurveyQuestionRes;
import org.accompany.backend.domain.survey.entity.SurveyQuestion;
import org.accompany.backend.domain.survey.repository.SurveyQuestionRepository;
import org.accompany.backend.domain.user.entity.SurveyStatus;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyServiceImpl implements SurveyService {

    private final SurveyQuestionRepository surveyQuestionRepository;
    private final ProcedureRepository procedureRepository;
    private final ChecklistBulkRepository checklistBulkRepository;
    private final UserRepository userRepository;


    @Override
    public SurveyListRes getSurveyList() {
        List<SurveyQuestion> questions = surveyQuestionRepository.findAllByOrderBySurveyQuestionIdAsc();

        List<SurveyQuestionRes> surveyQuestionResList = questions.stream()
                .map(question -> new SurveyQuestionRes(
                        question.getSurveyQuestionId(),
                        question.getSurveyQuestionText(),
                        question.getQuestionType().name(),
                        question.getRequirementType().name(),
                        question.getDescription(),
                        question.getSurveyAnswers().stream()
                                .map(answer -> new SurveyAnswerRes(
                                        answer.getSurveyAnswerId(),
                                        answer.getSurveyAnswerText(),
                                        answer.getNextQuestion() != null
                                                ? answer.getNextQuestion().getSurveyQuestionId()
                                                : null
                                ))
                                .toList()
                ))
                .toList();

        return new SurveyListRes(surveyQuestionResList);
    }

    @Override
    @Transactional
    public void skipSurvey(Long userId) {
        log.info("[Survey] 설문조사 스킵 시작 - userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[Survey] 사용자 조회 실패 - userId={}", userId );
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        DeceasedProfile deceasedProfile = user.getActiveDeceasedProfile();
        if(deceasedProfile == null){
            log.error("[Survey] 활성 고인 프로필 없음 - userId={}", userId);
            throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
        }

        if(deceasedProfile.getSurveyStatus() == SurveyStatus.COMPLETED
            || deceasedProfile.getSurveyStatus() == SurveyStatus.SKIPPED ) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_COMPLETED);
        }

        deceasedProfile.updateStatus(SurveyStatus.SKIPPED);

        LocalDate dateOfDeath = deceasedProfile.getDateOfDeath();

        List<Procedure> allProcedures = procedureRepository.findAllWithDocuments();

        List<UserProcedureChecklist> procedureChecklists = allProcedures.stream()
                .map(procedure -> UserProcedureChecklist.builder()
                        .dueDate(calculateDueDate(procedure, dateOfDeath))
                        .deceasedProfile(deceasedProfile)
                        .procedure(procedure)
                        .isCheck(false)
                        .build())
                .toList();

        List<UserDocumentChecklist> documentChecklists = allProcedures.stream()
                .flatMap(procedure -> procedure.getProcedureDocuments().stream())
                .map(document -> UserDocumentChecklist.builder()
                        .deceasedProfile(deceasedProfile)
                        .procedureDocument(document)
                        .isChecked(false)
                        .build())
                .toList();

        checklistBulkRepository.bulkInsertProcedureChecklists(procedureChecklists);
        checklistBulkRepository.bulkInsertDocumentChecklists(documentChecklists);

        log.info("[Survey] 설문조사 스킵 완료 - userId={}, deceasedProfilelId={}, 생성된 체크리스트 수 ={}",
                userId, deceasedProfile.getDeceasedProfileId(), allProcedures.size());
    }
    private LocalDateTime calculateDueDate(Procedure procedure, LocalDate dateOfDeath) {
        DueDateType type = procedure.getDueDateType();
        DueDateUnit unit = procedure.getDueDateUnit();
        Integer dueDate = procedure.getDueDate();

        return switch(type) {
            case IMMEDIATE -> dateOfDeath.atStartOfDay();
            case RELATIVE -> addDuration(dateOfDeath, unit, dueDate).atStartOfDay();
            case DEATH_MONTH -> dateOfDeath.withDayOfMonth(1).plusMonths(1).minusDays(1).atStartOfDay();
            case DEATH_END_DAY -> {
                LocalDate endOfMonth = dateOfDeath.withDayOfMonth(1).plusMonths(1).minusDays(1);
                yield addDuration(endOfMonth, unit, dueDate).atStartOfDay();
            }
            case NONE -> null;
        };
    }

    private LocalDate addDuration(LocalDate base, DueDateUnit unit, Integer amount) {
        return switch (unit) {
            case YEAR -> base. plusYears(amount);
            case MONTH -> base.plusMonths(amount);
            case DAY -> base.plusDays(amount);
        };
    }
}