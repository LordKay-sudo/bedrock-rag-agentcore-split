# Infra

- **CI**: `.github/workflows/ci.yml` — Java 17 + Maven for `services/rag-gateway` and `services/orchestration-edge`, Python 3.12 + pytest for `services/agent-runtime`.
- **Future**: GitHub Actions OIDC to AWS, ECR push, AgentCore deploy (see root `README` references). Terraform or CloudFormation can live here.
