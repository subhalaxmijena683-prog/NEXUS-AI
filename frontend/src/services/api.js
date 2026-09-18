const API_BASE_URL = "http://localhost:8081";

// Check Spring Boot backend
export async function checkBackendHealth() {
  const response = await fetch(`${API_BASE_URL}/api/health`);

  if (!response.ok) {
    throw new Error("Backend is not responding");
  }

  return response.json();
}

// Send message to NEXUS AI
export async function sendAiMessage(message) {
  if (!message || !message.trim()) {
    throw new Error("Message cannot be empty");
  }

  const response = await fetch(`${API_BASE_URL}/api/ai/chat`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      message: message.trim(),
    }),
  });

  if (!response.ok) {
    throw new Error("AI request failed");
  }

  return response.json();
}