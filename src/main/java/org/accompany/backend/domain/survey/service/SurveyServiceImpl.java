package org.accompany.backend.domain.survey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.entity.UserDocumentChecklist;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.procedure.entity.DueDateType;
import org.accompany.backend.domain.procedure.entity.DueDateUnit;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.procedure.entity.SurveyAnswerProcedure;
import org.accompany.backend.domain.checklist.repository.ChecklistBulkRepository;
import org.accompany.backend.domain.procedure.repository.ProcedureRepository;
import org.accompany.backend.domain.survey.dto.request.SurveyAnswerIdReq;
import org.accompany.backend.domain.survey.dto.request.SurveySaveReq;
import org.accompany.backend.domain.survey.dto.response.*;
import org.accompany.backend.domain.survey.entity.SurveyAnswer;
import org.accompany.backend.domain.survey.entity.SurveyQuestion;
import org.accompany.backend.domain.survey.entity.SurveyResponse;
import org.accompany.backend.domain.survey.repository.SurveyAnswerRepository;
import org.accompany.backend.domain.survey.repository.SurveyBulkRepository;
import org.accompany.backend.domain.survey.repository.SurveyQuestionRepository;
import org.accompany.backend.domain.survey.repository.SurveyResponseRepository;
import org.accompany.backend.domain.user.entity.SurveyStatus;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyServiceImpl implements SurveyService {

    private final SurveyQuestionRepository surveyQuestionRepository;
    private final ProcedureRepository procedureRepository;
    private final ChecklistBulkRepository checklistBulkRepository;
    private final SurveyBulkRepository surveyBulkRepository;
    private final UserRepository userRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final SurveyResponseRepository surveyResponseRepository;


    @Override
    public SurveyListRes getSurveyList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        DeceasedProfile deceasedProfile = user.getActiveDeceasedProfile();
        if(deceasedProfile == null) {
            throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
        }

        if(deceasedProfile.getSurveyStatus() == SurveyStatus.COMPLETED || deceasedProfile.getSurveyStatus() == SurveyStatus.SKIPPED){
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_COMPLETED);
        }

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
        List<Long> selectedAnswerIds = List.of();
        if(deceasedProfile.getSurveyStatus() == SurveyStatus.IN_PROGRESS){
            selectedAnswerIds = surveyResponseRepository.findAllByDeceasedProfile(deceasedProfile).stream()
                    .map(response -> response.getSurveyAnswer().getSurveyAnswerId())
                    .toList();
        }

        return new SurveyListRes(
                deceasedProfile.getSurveyStatus().name(),
                surveyQuestionResList,
                selectedAnswerIds
        );
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
                        .isChecked(false)
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

    @Override
    public LocalDateTime calculateDueDate(Procedure procedure, LocalDate dateOfDeath) {
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

    @Override
    @Transactional
    public SurveyTempSaveRes saveTempSurvey(Long userId, SurveySaveReq request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        DeceasedProfile deceasedProfile = user.getActiveDeceasedProfile();
        if (deceasedProfile == null) {
            throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
        }

        if (deceasedProfile.getSurveyStatus() == SurveyStatus.COMPLETED || deceasedProfile.getSurveyStatus() == SurveyStatus.SKIPPED) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_COMPLETED);
        }

        deceasedProfile.updateStatus(SurveyStatus.IN_PROGRESS);
        surveyResponseRepository.deleteAllByDeceasedProfile(deceasedProfile);

        List<SurveyResponse> responses = (request.answers() != null ? request.answers() : List.<SurveyAnswerIdReq>of()).stream()
                .map(answer -> SurveyResponse.builder()
                        .deceasedProfile(deceasedProfile)
                        .surveyAnswer(surveyAnswerRepository.getReferenceById(answer.surveyAnswerId()))
                        .build())
                .toList();

        surveyBulkRepository.bulkInsertSurveyResponses(responses);

        List<SurveyResponseRes> responseResList = responses.stream()
                .map(response -> new SurveyResponseRes(
                        response.getSurveyAnswer().getSurveyAnswerId()
                ))
                .toList();

        return new SurveyTempSaveRes(
                deceasedProfile.getSurveyStatus().name(),
                responseResList
        );
    }

    @Override
    @Transactional
    public SurveySubmitRes submitSurvey(Long userId, SurveySaveReq request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        DeceasedProfile deceasedProfile = user.getActiveDeceasedProfile();
        if (deceasedProfile == null) {
            throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
        }
        if(deceasedProfile.getSurveyStatus() == SurveyStatus.COMPLETED || deceasedProfile.getSurveyStatus() == SurveyStatus.SKIPPED) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_COMPLETED);
        }

        surveyResponseRepository.deleteAllByDeceasedProfile(deceasedProfile);

        List<Long> answerIds = request.answers().stream()
                .map(SurveyAnswerIdReq::surveyAnswerId)
                .toList();

        List<SurveyResponse> responses = request.answers().stream()
                .map(answer -> SurveyResponse.builder()
                        .deceasedProfile(deceasedProfile)
                        .surveyAnswer(surveyAnswerRepository.getReferenceById(answer.surveyAnswerId()))
                        .build())
                .toList();
        surveyBulkRepository.bulkInsertSurveyResponses(responses);

        List<SurveyAnswer> submittedAnswers =
                surveyAnswerRepository.findAllWithProceduresAndQuestionByIds(answerIds);

        Set<Long> procedureIds = new HashSet<>();
        Set<Long> unsureQuestionIds = new HashSet<>();

        for (SurveyAnswer answer : submittedAnswers) {
            String text = answer.getSurveyAnswerText();

            for (SurveyAnswerProcedure sap : answer.getSurveyAnswerProcedures()) {
                procedureIds.add(sap.getProcedure().getProcedureId());
            }
        }

        if (!unsureQuestionIds.isEmpty()) {
            List<SurveyAnswer> unsureAnswers = surveyAnswerRepository
                    .findAllWithProceduresByQuestionIds(unsureQuestionIds);
            for (SurveyAnswer a : unsureAnswers) {
                for (SurveyAnswerProcedure sap : a.getSurveyAnswerProcedures()) {
                    procedureIds.add(sap.getProcedure().getProcedureId());
                }
            }
        }

        List<Procedure> procedures = procedureIds.isEmpty()
                ? List.of()
                : procedureRepository.findAllWithDocumentsByIds(procedureIds);

        LocalDate dateOfDeath = deceasedProfile.getDateOfDeath();

        List<UserProcedureChecklist> procedureChecklists = procedures.stream()
                .map(procedure -> UserProcedureChecklist.builder()
                        .dueDate(calculateDueDate(procedure, dateOfDeath))
                        .deceasedProfile(deceasedProfile)
                        .procedure(procedure)
                        .isChecked(false)
                        .build())
                .toList();

        List<UserDocumentChecklist> documentChecklists = procedures.stream()
                .flatMap(procedure -> procedure.getProcedureDocuments().stream())
                .map(document -> UserDocumentChecklist.builder()
                        .deceasedProfile(deceasedProfile)
                        .procedureDocument(document)
                        .isChecked(false)
                        .build())
                .toList();

        checklistBulkRepository.bulkInsertProcedureChecklists(procedureChecklists);
        checklistBulkRepository.bulkInsertDocumentChecklists(documentChecklists);

        deceasedProfile.updateStatus(SurveyStatus.COMPLETED);

        return new SurveySubmitRes(
                deceasedProfile.getSurveyStatus().name(),
                procedureChecklists.size(),
                documentChecklists.size()
        );
    }

}
