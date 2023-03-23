package authorization;

import java.util.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;

public class AbacUtils {
    public static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public static List<String> parsePolicy(String text) {
        List<String> res = new ArrayList<>();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(text.getBytes("UTF-8"));
            Document doc = builder.parse(input);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("rule");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                String textContent = nNode.getTextContent().trim();
                if (!textContent.isBlank()) {
                    res.add(textContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Set<String> extractSubjectAttrKeys(List<List<String>> policies) {
        Set<String> res = new HashSet<>();
        for (List<String> policy : policies) {
            for (String rule : policy) {
                String[] tokens = rule.split(" ");
                for (String token : tokens) {
                    if (token.startsWith("subject.")) {
                        res.add(token);
                    }
                }
            }
        }
        return res;
    }

    public static Set<String> extractObjectAttrKeys(List<List<String>> policies) {
        Set<String> res = new HashSet<>();
        for (List<String> policy : policies) {
            for (String rule : policy) {
                String[] tokens = rule.split(" ");
                for (String token : tokens) {
                    if (token.startsWith("object.")) {
                        res.add(token);
                    }
                }
            }
        }
        return res;
    }

    public static boolean evaluatePolicies(List<List<String>> policies,
                                           Map<String, String> subjectAttributes,
                                           Map<String, String> objectAttributes) {
        for(List<String> policy: policies){
            if(evaluateAPolicy(policy,subjectAttributes,objectAttributes )){
                return true;
            }
        }
        return false;
    }

    private static boolean evaluateAPolicy(List<String> policy, Map<String, String> subjectAttributes,
                                           Map<String, String> objectAttributes) {
        if (policy.isEmpty()) {
            return false;
        }
        boolean access = true;
        for (String rule : policy) {
            String[] tokens = rule.split(" ");
            if (tokens.length != 3) {
                access = false;
                break;
            }
            if (!tokens[1].equals("=")) {
                access = false;
                break;
            }
            String left = tokens[0];
            String right = tokens[2];
            if (left.startsWith("subject.")) {
                left = subjectAttributes.get(left);
            } else if (left.startsWith("object.")) {
                left = objectAttributes.get(left);
            }
            if (right.startsWith("subject.")) {
                right = subjectAttributes.get(right);
            } else if (right.startsWith("object.")) {
                right = objectAttributes.get(right);
            }
            if (left == null || !left.equals(right)) {
                access = false;
                break;
            }
        }
        return access;
    }


    public static void main(String[] args) {
        String text = "<policy> <rule> subject.ID = object.ID          </rule> <rule> subject.ID = object.ID          </rule> </policy>";
        System.out.println(parsePolicy(text));
    }
}
