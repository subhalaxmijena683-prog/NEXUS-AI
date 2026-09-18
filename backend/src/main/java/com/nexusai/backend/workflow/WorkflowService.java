package com.nexusai.backend.workflow;

import com.nexusai.agent.DataAnalystAgent;
import com.nexusai.agent.RagKnowledgeAgent;
import com.nexusai.agent.ResearchAgent;
import com.nexusai.agent.DecisionAgent;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    private final ResearchAgent researchAgent;
    private final DataAnalystAgent dataAnalystAgent;
    private final RagKnowledgeAgent ragKnowledgeAgent;
    private final DecisionAgent decisionAgent;

    public  WorkflowService(
        ResearchAgent researchAgent,
        DataAnalystAgent dataAnalystAgent,
        RagKnowledgeAgent ragKnowledgeAgent,
        DecisionAgent decisionAgent) {

        this.researchAgent = researchAgent;
        this.dataAnalystAgent = dataAnalystAgent;
        this.ragKnowledgeAgent = ragKnowledgeAgent;
        this.decisionAgent = decisionAgent;
    }

    public WorkflowResponse executeWorkflow(String problem) {

        // Step 1: Research
        String researchResult =
                researchAgent.research(problem);

        // Step 2: Data Analysis
        String analysisTask =
                "Analyze the following research findings and identify "
                + "important trends, insights, risks and opportunities:\n\n"
                + researchResult;

        String analysisResult =
                dataAnalystAgent.analyze(analysisTask);

        // Step 3: Enterprise Knowledge
        String knowledgeQuestion =
                "Using the uploaded enterprise knowledge, provide "
                + "relevant information for this problem:\n\n"
                + problem;

        String knowledgeResult =
                ragKnowledgeAgent.ask(knowledgeQuestion);
            

                String decisionResult =
        decisionAgent.makeDecision(
                problem,
                researchResult,
                analysisResult,
                knowledgeResult
        );
        // Step 4: Combine results
        String finalResult =
                "AUTONOMOUS ENTERPRISE INTELLIGENCE WORKFLOW\n\n"
                + "PROBLEM:\n"
                + problem
                + "\n\n"
                + "RESEARCH AGENT:\n"
                + researchResult
                + "\n\n"
                + "DATA ANALYST AGENT:\n"
                + analysisResult
                + "\n\n"
                + "RAG KNOWLEDGE AGENT:\n"
                + knowledgeResult
                + "\n\n"
                + "DECISION AGENT:\n"
                + decisionResult;

        return new WorkflowResponse(
                "Enterprise Intelligence Workflow",
                finalResult
        );
    }
}