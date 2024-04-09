package com.soprasteria.ai.devs.api.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soprasteria.ai.devs.api.model.aidevs.TaskAnswerResponse;
import com.soprasteria.ai.devs.api.model.aidevs.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import static com.soprasteria.ai.devs.api.util.AIDevsAPIUtil.*;
import static com.soprasteria.ai.devs.api.util.SecretsUtil.getOpenAIAPIKey;

@Slf4j
public class APITask15Tools {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final OpenAiApi openAiApi = new OpenAiApi(getOpenAIAPIKey());

    private static final String TODO_FUNCTION_NAME = "TODO";

    private static final String TODO_FUNCTION_DESC = """
                                                     Add new task to the list of things which must be done in the future without particular date defined.
                                                     """;

    private static final String TODO_FUNCTION_SCHEMA = """
                 {
                    "type": "object",
                    "properties": {
                        "desc": {
                            "type": "string",
                            "description": "Concise description of the task which needs to be done."
                        }
                    },
                    "required": ["desc"]
                }
            """;

    private static final String CALENDAR_FUNCTION_NAME = "CALENDAR";

    private static final String CALENDAR_FUNCTION_DESC = """
                                                     Add something to the calendar for particular date.
                                                     """;

    private static final String CALENDAR_FUNCTION_SCHEMA = """
                 {
                    "type": "object",
                    "properties": {
                        "desc": {
                            "type": "string",
                            "description": "Concise description of the event which needs to be added to the calendar."
                        },
                        "date": {
                            "type": "string",
                            "description": "Date for which the event should be added to the calendar in the format yyyy-MM-dd."
                        }
                    },
                    "required": ["desc", "date"]
                }
            """;
    private static final String SYSTEM_PROMPT = """
                Hello! I'm Alice. My only job is to categorize user messages and use available tools to help.
                If user gives a date, I'll add it to the calendar. Otherwise I'll add it to the todo list.
                Context### today is %s ###
            """.formatted(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TokenResponse tokenResponse = fetchToken("tools");
        APITaskResponse taskResponse = fetchTask(tokenResponse.token(), APITaskResponse.class);
        log.info("Task response: {}", taskResponse);

        ResponseEntity<OpenAiApi.ChatCompletion> response =
                openAiApi.chatCompletionEntity(buildCompletionRequest(SYSTEM_PROMPT, taskResponse.question(), "gpt-4", List.of(
                        createFunctionTool(TODO_FUNCTION_NAME, TODO_FUNCTION_DESC, TODO_FUNCTION_SCHEMA),
                        createFunctionTool(CALENDAR_FUNCTION_NAME, CALENDAR_FUNCTION_DESC, CALENDAR_FUNCTION_SCHEMA))
        ));

        log.info("Response body: {}", response.getBody());

        List<OpenAiApi.ChatCompletionMessage.ToolCall> toolCalls = response.getBody().choices().get(0).message().toolCalls();
        if (!CollectionUtils.isEmpty(toolCalls)) {
            String toolName = toolCalls.get(0).function().name();
            String arguments = toolCalls.get(0).function().arguments();
            log.info("Tool name: {}, args: {}", toolName, arguments);

            ToolArgs toolArgs = mapper.readValue(arguments, ToolArgs.class);
            ToolChoice toolChoice = ToolChoice.valueOf(toolName);

            Object toolToReturn = toolChoice.getTool(toolArgs);
            log.info("Returned tool: {}", mapper.writeValueAsString(toolToReturn));

            TaskAnswerResponse answerResponse = submitTaskAnswer(tokenResponse.token(), new APITaskAnswerRequest(toolToReturn));
            log.info("Answer response: {}", answerResponse);
        }
    }

    private static OpenAiApi.ChatCompletionRequest buildCompletionRequest(String systemPrompt, String userMessage,
                                                                          String model, List<OpenAiApi.FunctionTool> tools) {
        return new OpenAiApi.ChatCompletionRequest(List.of(
                new OpenAiApi.ChatCompletionMessage(systemPrompt, OpenAiApi.ChatCompletionMessage.Role.SYSTEM),
                new OpenAiApi.ChatCompletionMessage(userMessage, OpenAiApi.ChatCompletionMessage.Role.USER)),
                model, tools, OpenAiApi.ChatCompletionRequest.ToolChoiceBuilder.AUTO);
    }

    private static OpenAiApi.FunctionTool createFunctionTool(String name, String description, String schema) {
        return new OpenAiApi.FunctionTool(new OpenAiApi.FunctionTool.Function(description, name, schema));
    }

    private enum ToolChoice {
        TODO(args -> new TodoTool("ToDo", args.desc())),
        CALENDAR(args -> new CalendarTool("Calendar", args.desc(), args.date()));

        ToolChoice(Function<ToolArgs, Object> toolMapper) {
            this.toolMapper = toolMapper;
        }

        private final Function<ToolArgs, Object> toolMapper;

        private Object getTool(ToolArgs args) {
            return toolMapper.apply(args);
        }
    }

    private record ToolArgs(String desc, String date) {}

    private record TodoTool(String tool, String desc) {}

    private record CalendarTool(String tool, String desc, String date) {}

    private record APITaskResponse(int code, String msg, String question) {}

    private record APITaskAnswerRequest(Object answer) {}
}
