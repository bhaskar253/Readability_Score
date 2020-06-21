package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;

public class Main {

    private static Integer wordsCount;
    private static Integer sentencesCount;
    private static Integer charactersCount;
    private static Integer syllablesCount;
    private static Integer pollySyllablesCount;


    private static String[] agesGroup = {
            "6", "7", "9", "10", "11", "12", "13", "14",
            "15", "16", "17", "18", "24", "25"
    };

    private static boolean isVowel(char ch) {
        return String.format("%c", ch)
                .matches("[aeiouy]");
    }

    private static int countSyllable(String word) {
        int count = 0;
        for (int i = 0; i < word.length() - 1; i++) {
            char c = word.charAt(i);
            char d = word.charAt(i + 1);
            if (isVowel(c) && !isVowel(d)) {
                count++;
            }
        }
        char ch = word.charAt(word.length()-1);
        if (ch != 'e' && isVowel(ch)) {
            count++;
        }
        return count == 0 ? 1 : count;
    }

    public static void main(String[] args) {
        var file = new File(args[0]);
        try(var sc = new Scanner(file)) {
            var sb = new StringBuilder();
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine());
            }
            String text = sb.toString();
            System.out.println(String.format("The text is: \n%s\n", text));
            String[] words = Arrays.stream(text.split("\\s"))
                    .toArray(String[]::new);
            String[] characters = Arrays.stream(words)
                .flatMap(word -> Arrays.stream(word.split("")))
                .toArray(String[]::new);
            String[] sentences = Arrays.stream(text.split("[.?!]"))
                .map(String::trim)
                .toArray(String[]::new);

            wordsCount = words.length;
            sentencesCount = sentences.length;
            charactersCount = characters.length;
            syllablesCount = 0;
            pollySyllablesCount = 0;
            for (String word: words) {
                word = word.toLowerCase();
                int count = countSyllable(word);
                if (count > 2) {
                    pollySyllablesCount++;
                }
                syllablesCount += count;
            }
            System.out.println(String.format("Words: %d\nSentences: %d\nCharacters: %d\nSyllables: %d\nPolysyllables: %d\n",
                    wordsCount, sentencesCount, charactersCount, syllablesCount, pollySyllablesCount
            ));

            System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            var scc = new Scanner(System.in);
            var in = scc.next();
            switch (in) {
                case "ARI": ARI();
                    break;
                case "FK": FK();
                    break;
                case "SMOG": SMOG();
                    break;
                case "CL": CL();
                    break;
                case "all": all();
                    break;
                default:
            }
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (Exception e) {
        }
    }

    private static int ARI() {
        double score = 4.71 * (charactersCount / (wordsCount * 1.0))
                + 0.5 * (wordsCount / (sentencesCount * 1.0)) - 21.43;
        System.out.println(String.format("Automated Readability Index: %.2f (about %s year olds).",
                score, agesGroup[(int)score - 1]));
        return Integer.parseInt(agesGroup[(int)score - 1]);
    }

    private static int FK() {
        double score = 0.39 * (wordsCount / (sentencesCount * 1.0))
                + 11.8 * (syllablesCount / (wordsCount * 1.0)) - 15.59;
        System.out.println(String.format("Flesch–Kincaid readability tests: %.2f (about %s year olds).",
                score, agesGroup[(int)score - 1]));
        return Integer.parseInt(agesGroup[(int)score - 1]);
    }

    private static int SMOG() {
        double score = 1.043 * Math.sqrt(pollySyllablesCount * 30 / (sentencesCount * 1.0)) + 3.1291;
        System.out.println(String.format("Simple Measure of Gobbledygook: %.2f (about %s year olds).",
                score, agesGroup[(int)score - 1]));
        return Integer.parseInt(agesGroup[(int)score - 1]);
    }

    private static int CL() {
        double L = (charactersCount * 1.0) / wordsCount * 100.0;
        double S = (sentencesCount * 1.0) / wordsCount * 100.0;
        double score = 0.0588 * L - 0.296 * S - 15.8;
        System.out.println(String.format("Coleman–Liau index: %.2f (about %s year olds).",
                score, agesGroup[(int)score - 1]));
        return Integer.parseInt(agesGroup[(int)score - 1]);
    }

    private static void all() {
        int a = ARI();
        int b = FK();
        int c = SMOG();
        int d = CL();

        System.out.println(String.format("\nThis text should be understood in average by %.2f year olds.",
                (1.0 * (a + b + c + d)) / 4.0));
    }

}
