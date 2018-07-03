import java.util.List;

public class Agent {
    String name;
    List<AgentResponse> responses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AgentResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<AgentResponse> responses) {
        this.responses = responses;
    }
}