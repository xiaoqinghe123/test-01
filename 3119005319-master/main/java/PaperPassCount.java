import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PaperPassCount {
    public static void main(String[] args) {

        String originalPath; //原始论文路径
        String addPath;      //查重论文路径
        String ansPath;      //答案储存路径
        String[] originalArray;
        String[] addArray;

        Scanner in = new Scanner(System.in);
        System.out.println("请输入原文路径:");
        originalPath = in.nextLine();
        originalArray = TxtToArray(originalPath);
        System.out.println("请输入查重论文路径:");
        addPath = in.nextLine();
        addArray = TxtToArray(addPath);
        System.out.println("请输入答案储存路径:");
        ansPath = in.nextLine();
        PaperPass(originalArray, addArray, ansPath);

    }

    private static int JudgeType(int tempchar) {
        if ((char) tempchar == ' ' || (char) tempchar == '，' || (char) tempchar == '\r' || (char) tempchar == '\t' ||
                (char) tempchar == '、' || (char) tempchar == '《' || (char) tempchar == '.' || (char) tempchar == '-'
                || (char) tempchar == '”' || (char) tempchar == '“' || (char) tempchar == '》' || (char) tempchar == '：'
                || (char) tempchar == '—' || (char) tempchar == '；')
            return 0;   //忽略
        else if ((char) tempchar == '。' || (char) tempchar == '!' || (char) tempchar == '？' || (char) tempchar == '\n'
                || (char) tempchar == ';' || (char) tempchar == '>')
            return 1;   //判定为句子
        else return 2;
    }

    private static String[] TxtToArray(String paperPath) {
        String[] sentenceArray = new String[2000];
        try {
            Reader reader = null;
            reader = new InputStreamReader(new FileInputStream(new File(paperPath)));
            int tempchar;
            int n = 0;
            String sentence = "";
            while ((tempchar = reader.read()) != -1) {
                switch (JudgeType(tempchar)) {
                    case 1:
                        if (sentence.equals("")) break;
                        if (sentence.length() > 5) sentenceArray[n++] = sentence;
                        sentence = "";
                        break;
                    case 2:
                        sentence = sentence + (char) (tempchar);
                    default:
                        break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentenceArray;
    }

    private static void PaperPass(String[] originalArray, String[] addArray, String ansPath) {
        double similarityPercentage = 0;
        double sentencePercentage;
        double wordNum = 0;
        for (String doc1 : originalArray
        ) {
            sentencePercentage = 0;
            if (doc1 == null) break;
            wordNum += doc1.length();
            for (String doc2 : addArray
            ) {
                if (doc2 == null) break;
                Map<Character, int[]> algMap = new HashMap<Character, int[]>();
                for (int i = 0; i < doc1.length(); i++) {
                    char d1 = doc1.charAt(i);
                    int[] fq = algMap.get(d1);
                    if (fq != null && fq.length == 2) {
                        fq[0]++;
                    } else {
                        fq = new int[2];
                        fq[0] = 1;
                        fq[1] = 0;
                        algMap.put(d1, fq);
                    }
                }
                for (int i = 0; i < doc2.length(); i++) {
                    char d2 = doc2.charAt(i);
                    int[] fq = algMap.get(d2);
                    if (fq != null && fq.length == 2) {
                        fq[1]++;
                    } else {
                        fq = new int[2];
                        fq[0] = 0;
                        fq[1] = 1;
                        algMap.put(d2, fq);
                    }
                }
                double sqdoc1 = 0;
                double sqdoc2 = 0;
                double denuminator = 0;
                for (Map.Entry entry : algMap.entrySet()) {
                    int[] c = (int[]) entry.getValue();
                    denuminator += c[0] * c[1];
                    sqdoc1 += c[0] * c[0];
                    sqdoc2 += c[1] * c[1];
                }
                double similarPercentage = denuminator / Math.sqrt(sqdoc1 * sqdoc2);
                if (similarPercentage > sentencePercentage)
                    sentencePercentage = similarPercentage;
            }
            similarityPercentage += (sentencePercentage * doc1.length());
        }
        similarityPercentage = similarityPercentage / wordNum * 100;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        df.format(similarityPercentage);
        System.out.println("论文重复率为" + similarityPercentage + "%");
        File file = new File(ansPath);
        try {
            Writer writer = new FileWriter(file,false);
            writer.write("论文重复率为" + similarityPercentage + "%");
            writer.close();
        }
        catch (IOException e) {
            System.out.println("路径不对，重新输入");;
        }
    }
}

