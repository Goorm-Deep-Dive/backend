package org.accompany.backend.domain.procedure.repository;

import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.checklist.entity.UserDocumentChecklist;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChecklistBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsertProcedureChecklists(List<UserProcedureChecklist> checklists) {
        String sql = """
                insert into user_procedure_checklists (deceased_profile_id, procedure_id, is_checked, due_date)
                values (?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, checklists, checklists.size(),
                (ps, checklist) -> {
                    ps.setLong(1,checklist.getDeceasedProfile().getDeceasedProfileId());
                    ps.setLong(2, checklist.getProcedure().getProcedureId());
                    ps.setBoolean(3, checklist.isChecked());
                    ps.setObject(4, checklist.getDueDate());
        });
    }

    public void bulkInsertDocumentChecklists(List<UserDocumentChecklist> checklists) {
        String sql = """
                insert into user_document_checklists (deceased_profile_id, procedure_document_id, is_checked)
                values (?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, checklists, checklists.size(),
                (ps, checklist) ->{
                    ps.setLong(1, checklist.getDeceasedProfile().getDeceasedProfileId());
                    ps.setLong(2, checklist.getProcedureDocument().getProcedureDocumentId());
                    ps.setBoolean(3, checklist.isChecked());
                });
    }
}
