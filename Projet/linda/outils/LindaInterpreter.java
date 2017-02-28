package linda.outils;

import linda.Linda;
import linda.Tuple;
import linda.server.LindaClient;
import linda.shm.CentralizedLinda;
import linda.shm.MultiLinda;

import java.util.Arrays;
import java.util.Scanner;

public class LindaInterpreter {
    private static Linda linda;

    private static void parseInput(String args) {
        String[] tokens = args.split(" ");
        switch (tokens[0]) {
            case "write":
                try {
                    linda.write(tupleDeclaration(Arrays.copyOfRange(tokens, 1, tokens.length)));
                } catch (TupleDeclarationException e) {
                    e.printStackTrace();
                }
                break;
            case "read":
                try {
                    Tuple t = linda.tryRead(tupleDeclaration(Arrays.copyOfRange(tokens, 1, tokens.length)));
                    if (t != null) {
                        System.out.println(t);
                    } else {
                        System.out.println("No such tuple");
                    }
                } catch (TupleDeclarationException e) {
                    e.printStackTrace();
                }
                break;
            case "take":
                try {
                    Tuple t = linda.tryTake(tupleDeclaration(Arrays.copyOfRange(tokens, 1, tokens.length)));
                    if (t != null) {
                        System.out.println(t);
                    } else {
                        System.out.println("No such tuple");
                    }
                } catch (TupleDeclarationException e) {
                    e.printStackTrace();
                }
                break;
            case "display":
                linda.debug("(1)");
                break;
            default:
                System.out.println("Unknown operation");
                break;
        }
    }

    private static Tuple tupleDeclaration(String[] declaration) throws TupleDeclarationException {
        if (!declaration[0].equals("[") || !declaration[declaration.length - 1].equals("]")) {
            throw new TupleDeclarationException("Invalid tuple declaration");
        }

        Tuple t = new Tuple();

        for (String s : Arrays.copyOfRange(declaration, 1, declaration.length - 1)) {
            if (s.equals("true")) {
                t.add(true);
            } else if (s.equals("false")) {
                t.add(false);
            } else {
                t.add(Integer.parseInt(s));
            }
        }

        return t;
    }

    public static void main(String[] args) {
        System.out.println("Starting up Linda interpreter...");
        System.out.println("Choose a version of Linda kernel :");
        System.out.println("1 - Centralized");
        System.out.println("2 - Multi-activity");
        System.out.println("3 - Remote server");

        Scanner in = new Scanner(System.in);
        String input;
        int choice = -1;
        Boolean valid = false;
        while (!valid) {
            try {
                input = in.nextLine();
                choice = Integer.parseInt(input);
                if (choice > 0 || choice < 4) {
                    valid = true;
                }
            } catch (Exception e) {
                System.out.println("Invalid choice. Please choose a valid option");
            }
        }

        switch (choice) {
            case 1:
                linda = new CentralizedLinda();
                break;
            case 2:
                linda = new MultiLinda(4);
                break;
            case 3:
                System.out.println("Enter server URI");
                System.out.print("> ");
                String servURI = in.nextLine();
                System.out.println("Enter client ID");
                System.out.print("> ");
                String clientID = in.nextLine();
                linda = new LindaClient(servURI, clientID);
                break;
            default:
                throw new RuntimeException("Uh oh... Something went unexpectedly wrong. Now shutting down.");
        }

        System.out.println("Setup complete. Have fun.");
        System.out.print("> ");
        while (!(input = in.nextLine()).equals("quit")) {
            parseInput(input);
            System.out.print("> ");
        }
    }
}
