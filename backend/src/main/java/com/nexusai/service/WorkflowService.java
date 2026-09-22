package com.nexusai.service;

import com.nexusai.agent.DataAnalystAgent;
import com.nexusai.agent.DecisionAgent;
import com.nexusai.agent.JudgeAgent;
import com.nexusai.agent.MLPredictionAgent;
import com.nexusai.agent.RagKnowledgeAgent;
import com.nexusai.agent.ResearchAgent;
import com.nexusai.agent.SolutionAgent;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    private final ResearchAgent researchAgent;
    private final DataAnalystAgent dataAnalystAgent;
    private final RagKnowledgeAgent ragKnowledgeAgent;
    private final MLPredictionAgent predictionAgent;
    private final DecisionAgent decisionAgent;
    private final SolutionAgent solutionAgent;
    private final JudgeAgent judgeAgent;

    public WorkflowService(
            ResearchAgent researchAgent,
            DataAnalystAgent dataAnalystAgent,
            RagKnowledgeAgent ragKnowledgeAgent,
            MLPredictionAgent predictionAgent,
            DecisionAgent decisionAgent,
            SolutionAgent solutionAgent,
            JudgeAgent judgeAgent
    ) {
        this.researchAgent = researchAgent;
        this.dataAnalystAgent = dataAnalystAgent;
        this.ragKnowledgeAgent = ragKnowledgeAgent;
        this.predictionAgent = predictionAgent;
        this.decisionAgent = decisionAgent;
        this.solutionAgent = solutionAgent;
        this.judgeAgent = judgeAgent;
    }

    public String execute(String problem) {

        if (problem == null || problem.isBlank()) {
            throw new IllegalArgumentException(
                    "Workflow problem cannot be empty"
            );
        }

        String research =
                researchAgent.research(problem);

        String analysis =
                dataAnalystAgent.analyze(
                        """
                        ENTERPRISE PROBLEM:
                        %s

                        RESEARCH AGENT OUTPUT:
                        %s
                        """.formatted(
                                problem,
                                research
                        )
                );

        String knowledge;

        try {

            knowledge =
                    ragKnowledgeAgent.ask(problem);

        } catch (Exception e) {

            knowledge =
                    "RAG Knowledge Agent unavailable: "
                            + e.getMessage();
        }

        String prediction =
                predictionAgent.predict(
                        """
                        ENTERPRISE PROBLEM:
                        %s

                        RESEARCH:
                        %s

                        DATA ANALYSIS:
                        %s
                        """.formatted(
                                problem,
                                research,
                                analysis
                        )
                );

        String decision =
                decisionAgent.makeDecision(
                        problem,
                        research,
                        analysis,
                        knowledge
                );

        String solution =
                solutionAgent.designSolution(
                        """
                        ENTERPRISE PROBLEM:
                        %s

                        DECISION:
                        %s

                        ML PREDICTION:
                        %s
                        """.formatted(
                                problem,
                                decision,
                                prediction
                        )
                );

        String judge =
                judgeAgent.evaluate(
                        problem,
                        decision,
                        solution,
                        prediction
                );

        return """
                # NEXUS AI AUTONOMOUS ENTERPRISE WORKFLOW

                ## 1. Research Agent

                %s


                ## 2. Data Analyst Agent

                %s


                ## 3. RAG Knowledge Agent

                %s


                ## 4. ML Prediction Agent

                %s


                ## 5. Decision-Making Agent

                %s


                ## 6. Solution Agent

                %s


                ## 7. Judge Agent

                %s
                """.formatted(
                research,
                analysis,
                knowledge,
                prediction,
                decision,
                solution,
                judge
        );
    }
}
