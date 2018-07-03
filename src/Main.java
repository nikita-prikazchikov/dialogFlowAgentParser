import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVWriter;

public class Main {

    public static void main(String[] args) {

        String dir = "src/resourses/intents";
        List<Path> files = new ArrayList<>();
        Gson gson = new Gson();
        HashMap<String, List<UserSays>> userSaysHashMap = new HashMap<>();
        HashMap<String, Agent> agentHashMap = new HashMap<>();

        try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(files::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Path path : files) {
            try {
                if (path.toString().contains("usersays")) {
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
                        Type targetClassType = new TypeToken<ArrayList<UserSays>>() { }.getType();

                        List<UserSays> userSays = gson.fromJson(bufferedReader, targetClassType);
                        userSaysHashMap.put(path.toFile().getName().replace(".json", "").replace(".", "_"), userSays);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {

                        Agent agent = gson.fromJson(bufferedReader, Agent.class);
                        agentHashMap.put(path.toFile().getName().replace(".json", "").replace(".", "_"), agent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
                System.out.println("File to update: " + path);
            }
        }

        String directory = "results";
        String INTENTS = "intents.csv";
        String TEST_DATA = "tests.csv";

        File file = new File(directory);
        if (!file.exists()) {
            file.mkdir();
        }

        File intents = new File(file, INTENTS);

        try(StringWriter writer = new StringWriter()) {
            try(CSVWriter csvWriter = new CSVWriter(writer)) {

                for (String key: agentHashMap.keySet()) {
                    String[] values;

                    List<String> list = new ArrayList<>();
                    list.add(key);
                    Agent agent = agentHashMap.get(key);
                    for(AgentResponse agentResponse: agent.getResponses()){
                        for(AgentMessage agentMessage: agentResponse.getMessages()){
                            list.addAll(agentMessage.getSpeech());
                        }
                    }
                    csvWriter.writeNext(list.toArray(new String[list.size()]));
                }
                //Write down
                System.out.println(writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println();
        }

//        System.out.println(userSaysHashMap);
//        System.out.println("==========");
//        System.out.println(agentHashMap);
    }
}
