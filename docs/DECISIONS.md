# Architecture decisions

Short log of significant choices. Add entries as the design hardens.

| ID | Date | Decision | Rationale |
|----|------|----------|-----------|
| ADR-001 | — | Split RAG and agent into separate deployables | Different scaling, IAM, and blast-radius profiles; keeps document grounding separate from tool execution. |
