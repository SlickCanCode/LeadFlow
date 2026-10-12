package com.slick.lead_revenue_automation_system.entities;

import com.slick.lead_revenue_automation_system.entities.enums.*;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "pipeline_stage", uniqueConstraints = {
    @UniqueConstraint(name = "uq_pipeline_stage_position", columnNames = { "pipeline_id", "position" }),
    @UniqueConstraint(name = "uq_pipeline_stage_name", columnNames = { "pipeline_id", "name" })
})
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PipelineStage {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "pipeline_id", nullable = false)
    private Pipeline pipeline;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "position", nullable = false)
    private int position;
    @Enumerated(EnumType.STRING) @Column(name = "type", nullable = false, length = 20)
    private PipelineStageType type;
}