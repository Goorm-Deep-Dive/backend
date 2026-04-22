package org.accompany.backend.domain.procedure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "procedure_channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcedureChannel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long procedureChannelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ChannelType channelType;

    @Column(length = 255)
    private String description;

    @Builder
    private ProcedureChannel(
            Procedure procedure,
            ChannelType channelType,
            String description
    ) {
        this.procedure = procedure;
        this.channelType = channelType;
        this.description = description;
    }
}
