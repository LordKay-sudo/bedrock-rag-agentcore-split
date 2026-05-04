from fastapi.testclient import TestClient

from agent_runtime.main import app

client = TestClient(app)


def test_health():
    r = client.get("/health")
    assert r.status_code == 200
    assert r.json()["status"] == "UP"


def test_invoke_agent_path():
    r = client.post("/v1/invoke", json={"message": "hello"})
    assert r.status_code == 200
    data = r.json()
    assert data["pathUsed"] == "agent"
    assert "hello" in data["text"]


def test_invoke_rag_hint():
    r = client.post("/v1/invoke", json={"message": "x", "preferredPath": "rag"})
    assert r.status_code == 200
    assert r.json()["pathUsed"] == "rag"
