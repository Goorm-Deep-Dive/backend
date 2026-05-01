package org.accompany.backend.domain.survey.repository;

import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.survey.entity.SurveyResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SurveyBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsertSurveyResponses(List<SurveyResponse> responses) {
        String sql = """
                insert into ending_schema.survey_responses (deceased_profile_id, survey_answer_id)
                values (?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, responses, responses.size(),
                (ps, response) -> {
                    ps.setLong(1, response.getDeceasedProfile().getDeceasedProfileId());
                    ps.setLong(2, response.getSurveyAnswer().getSurveyAnswerId());
                });
    }
}
