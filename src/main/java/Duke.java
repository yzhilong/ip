import Tasks.Deadline;
import Tasks.Event;
import Tasks.Task;
import Tasks.Todo;

import Exceptions.DukeException;
import Exceptions.EmptyDescriptionException;
import Exceptions.EmptyTimestampException;
import Exceptions.InvalidTaskNumberException;
import Exceptions.UnknownCommandException;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Duke {

    private static final String SEPARATOR = "_".repeat(50);
    private static final String WELCOME_MESSAGE = "Hello! I'm Duke\nWhat can I do for you?";
    private static final String BYE_MESSAGE = "Bye. Hope to see you again soon!";
    private static final String TAB = " ".repeat(4);

    // keywords should have length 1 or 2
    private static String[] extractCommandsFromInput(String input, String[] keywords) {
        if (keywords.length == 2) {
            String[] commands = input.split(keywords[1], 2);
            String taskDescription = commands[0].split(" ", 2)[1].trim();
            String timeInfo = commands[1].trim();
            return new String[] { taskDescription, timeInfo };
        } else {
            String taskDescription = input.split(keywords[0],2)[1].trim();
            return new String[] { taskDescription };
        }
    }

    private static void parseInput(String input, List<Task> tasks) throws DukeException {
        Task newTask = null;
        String message = "";

        if (input.matches("list")) {
            int i = 1;
            for (Task task : tasks) {
                message += String.format("%d. %s\n", i, task);
                i++;
            }
            message = message.length() > 0
                    ? message.substring(0, message.length() - 1)
                    : "No tasks added so far!";
        } else if (input.matches("event\\s.{1,}\\s\\/at\\s.{1,}")) {
            String[] commands = extractCommandsFromInput(input, new String[] { "event", "/at" });
            if (commands[0].length() == 0) {
                throw new EmptyDescriptionException();
            } else if (commands[1].length() == 0) {
                throw new EmptyTimestampException();
            }
            newTask = new Event(commands[0], commands[1]);
        } else if (input.matches("deadline\\s.{1,}\\s\\/by\\s.{1,}")) {
            String[] commands = extractCommandsFromInput(input, new String[] { "deadline", "/by" });
            if (commands[0].length() == 0) {
                throw new EmptyDescriptionException();
            } else if (commands[1].length() == 0) {
                throw new EmptyTimestampException();
            }
            newTask = new Deadline(commands[0], commands[1]);
        } else if (input.matches("todo\\s.{1,}")) {
            String[] commands = extractCommandsFromInput(input, new String[] { "todo" });
            if (commands[0].length() == 0) {
                throw new EmptyDescriptionException();
            }
            newTask = new Todo(commands[0]);
        } else if (input.matches("done [0-9]{1,}")) {
            String[] commands = extractCommandsFromInput(input, new String[] { "done" });
            int index = Integer.parseInt(commands[0]);
            if (index > tasks.size() || index == 0) {
                throw new InvalidTaskNumberException(tasks.size());
            }
            Task task = tasks.get(index - 1);
            message = task.markDone()
                    ? String.format("Nice! I've marked this task as done:\n    %s", task)
                    : String.format("This was completed previously!:\n    %s", task);
        } else if (input.matches("delete [0-9]{1,}")) {
            String[] commands = extractCommandsFromInput(input, new String[] { "delete" });
            int index = Integer.parseInt(commands[0]);
            if (index > tasks.size() || index == 0) {
                throw new InvalidTaskNumberException(tasks.size());
            }
            Task task = tasks.get(index - 1);
            tasks.remove(index - 1);
            message = String.format(
                    "Noted. I've removed this task:\n    %s\nNow you have %d tasks in the list.",
                    task,
                    tasks.size()
            );
        } else {
            throw new UnknownCommandException();
        }

        if (newTask != null) {
            tasks.add(newTask);
            message = String.format(
                    "Got it. I've added this task:\n    %s\nNow you have %d tasks in the list.",
                    newTask,
                    tasks.size()
            );
        }

        printOut(message);
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Task> tasks = new ArrayList<>();

        printOut(WELCOME_MESSAGE);
        String input = scanner.nextLine();
        while (!input.equals("bye")) {
            try {
                parseInput(input, tasks);
            } catch (DukeException e) {
                printOut(e.getMessage());
            }
            input = scanner.nextLine();
        }
        printOut(BYE_MESSAGE);
    }

    private static void printOut(String message) {
        String[] lines = message.split("\n");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = TAB + lines[i];
        }
        System.out.println(String.format("%s\n%s\n%s", TAB+SEPARATOR, String.join("\n", lines), TAB+SEPARATOR));
    }
}
