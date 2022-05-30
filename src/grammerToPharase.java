import java.util.*;

public class grammerToPharase {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<String, ArrayList<String>> grammarsMap = new HashMap<>();
        ArrayList<String> keys = new ArrayList<>();
        String grammar;
        System.out.println("Enter your grammars.");
        while (true) {
            grammar = sc.nextLine();
            if (grammar.equals("$"))
                break;
            else {
                String variable = grammar.split("#")[0];
                String grammarRule = grammar.split("#")[1];
                ArrayList<String> strings;
                if (grammarsMap.containsKey(variable)) {
                    strings = grammarsMap.get(variable);
                    if (strings == null)
                        strings = new ArrayList<>();
                    strings.add(grammarRule);
                } else {
                    strings = new ArrayList<>();
                    strings.add(grammarRule);
                }
                grammarsMap.put(variable, strings);
                if (!keys.contains(variable))
                    keys.add(variable);
                if (grammar.contains("$")) {
                    break;
                }
            }
        }
        grammarsMap.values().forEach(values -> values.removeIf(value -> !grammarsMap.containsKey(String.valueOf(value.charAt(value.length() - 1)))));
        for (String variable : keys) {
            for (int i = 0; i < grammarsMap.get(variable).size() - 1; i++) {
                for (int j = i + 1; j < grammarsMap.get(variable).size(); j++) {
                    String lastCharacterOfFirstRule = String.valueOf(grammarsMap.get(variable).get(i).charAt(grammarsMap.get(variable).get(i).length() - 1));
                    String lastCharacterOfSecondRule = String.valueOf(grammarsMap.get(variable).get(j).charAt(grammarsMap.get(variable).get(j).length() - 1));
                    if (lastCharacterOfFirstRule.equals(lastCharacterOfSecondRule)) {
                        String s = "(" + grammarsMap.get(variable).get(i) + "U" + grammarsMap.get(variable).get(i + 1) + ")";
                        grammarsMap.get(variable).add(i, s.replaceAll(lastCharacterOfFirstRule, "") + lastCharacterOfFirstRule);
                        grammarsMap.get(variable).remove(j + 1);
                        grammarsMap.get(variable).remove(i + 1);
                        break;
                    }
                }
            }
        }
        ArrayList<String> pastVariables = new ArrayList<>();
        for (String presentVariable : keys) {
            pastVariables.add(presentVariable);
            for (String ruleOfPresentVariable : grammarsMap.get(presentVariable)) {
                String lastCharacterOfPresentVariable = String.valueOf(ruleOfPresentVariable.charAt(ruleOfPresentVariable.length() - 1));
                if (pastVariables.contains(lastCharacterOfPresentVariable) && !lastCharacterOfPresentVariable.equals(presentVariable)) {
                    for (String ruleOfNextVariable : grammarsMap.get(lastCharacterOfPresentVariable))
                        if (ruleOfNextVariable.contains(presentVariable)) {
                            grammarsMap.get(lastCharacterOfPresentVariable).add(grammarsMap.get(lastCharacterOfPresentVariable).indexOf(ruleOfNextVariable), ruleOfNextVariable.replaceAll(presentVariable, ruleOfPresentVariable));
                            grammarsMap.get(lastCharacterOfPresentVariable).remove(ruleOfNextVariable);
                            grammarsMap.get(presentVariable).remove(ruleOfPresentVariable);
                            if (grammarsMap.get(lastCharacterOfPresentVariable).size() == 1)
                                grammarsMap.get(lastCharacterOfPresentVariable).add(1, ruleOfNextVariable);
                            break;
                        }
                }
            }
        }

        System.out.println("ph: " + mapToPharase(grammarsMap, "S"));
    }

    static String mapToPharase(Map<String, ArrayList<String>> inputMap, String StartState) {
        StringBuilder ph = new StringBuilder();
        boolean q = false;
        if (inputMap.get(StartState).contains("$")) {
            return "";
        } else {
            for (String rule : inputMap.get(StartState)) {
                String lastCharacter = String.valueOf(rule.charAt(rule.length() - 1));
                if (rule.contains(StartState)) {
                    ph.append("(").append(rule.replace(lastCharacter, "")).append(")*");
                    inputMap.get(StartState).remove(rule);
                    ph.append(mapToPharase(inputMap, lastCharacter));
                    break;
                } else {
                    if (!q) {
                        ph.append(rule).append(mapToPharase(inputMap, lastCharacter));
                        q = true;
                    } else ph.append(" U ").append(rule).append(mapToPharase(inputMap, lastCharacter));
                }
            }
            for (String variable : inputMap.keySet())
                ph = new StringBuilder(ph.toString().replaceAll(variable, ""));
            return ph.toString();
        }
    }
}