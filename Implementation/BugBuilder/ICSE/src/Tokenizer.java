import java.io.*;
import java.util.ArrayList;

public class Tokenizer {

    static BufferedReader br = null;
    static BufferedWriter bw;

    public static int state = 0; // 记录DFA的状态
    public static String result = ""; // 记录结果token
    public static ArrayList<String> error = new ArrayList<String>(); // 记录错误

    public static String info = ""; // 用于添加进result 或 error的基本单位
    public static String token = ""; // 记录输入的字符

    /**
     * 判断c是否是a-Z中的字母
     *
     * @param c
     * @return
     */
    public static boolean isLetter(int c) {
        if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
            if (state == 0 || state == 2) {
                token += (char) c;
            }
            return true;
        } else
            return false;
    }

    /**
     * 判断c是否是0-9中的数字
     *
     * @param c
     * @return
     */
    public static boolean isDigit(int c) {
        if (c >= 48 && c <= 57) {
            if (state == 0 || state == 1 || state == 2) {
                token += (char) c;
            }
            return true;
        } else
            return false;
    }

    /**
     * 判断c是否是下划线_
     *
     * @param c
     * @return
     */
    public static boolean isUnderline(int c) {
        if (c == 95) {
            if (state == 2) {
                token += (char) c;
            }
            return true;
        } else
            return false;
    }

    /**
     * 判断c是否是符号
     *
     * @param c
     * @return
     */
    public static boolean isSymbol(int c, BufferedReader br) throws IOException {
        if (c == 91 || c == 93 || c == 123 || c == 125 || c == 59 || c == 61 || c == 60 || c == 33 || (c >= 40 && c <= 45) || c == 16) {
            if (state == 0) { // 上一状态是0
                token += (char) c;
                addInfo(); // 提交符号
            } else if (state == 1) { // 上一状态是1
                addInfo(); // 提交上一个状态的token，并清零
                token += (char) c;
                state = 0;
                addInfo(); // 提交符号
            } else if (state == 2) {
                addInfo(); // 提交上一个状态的token，并清零
                token += (char) c;
                state = 0;
                addInfo(); // 提交符号
            }
            return true;
        } else if (c == 38) {
            if (state == 0) { // 上一状态是0
                token += (char) c;
            } else if (state == 1) { // 上一状态是1
                addInfo(); // 提交上一个状态的token，并清零
                token += (char) c;
            } else if (state == 2) {
                addInfo(); // 提交上一个状态的token，并清零
                token += (char) c;
            }
            if ((c = br.read()) == 38) {
                if (state == 0) {
                    token += (char) c;
                    state = 0;
                    addInfo(); // 提交符号
                }
                return true;
            } else return false;
        } else if (c == 46) { // . 特殊处理
            if (state == 0) { // 上一状态是0
                token += (char) c;
                c = br.read(); // 查询下一符号
                if (isDigit(c)) { //识别.01错误
                    token += br.readLine();
                    zeroing();
                } else addInfo();
            } else if (state == 1) { // 上一状态是1（数字）
                token += (char) c; // 临时存储
                token += br.readLine();
                error.add(info);
                zeroing();

            } else if (state == 2 && (token.equals("System") || token.equals("System.out"))) {
                token += (char) c;
            } else if (state == 2 && !token.equals("System") && !token.equals("System.out")) {
                addInfo(); // 提交上一个状态的token，并清零
                token += (char) c;
                state = 0;
                addInfo(); // 提交符号
            }
            return true;
        } else
            return false;
    }

    /**
     * 处理空格、换行符和其他字符（错误）
     *
     * @param c
     * @param br
     * @throws IOException
     */
    public static void isEnd(int c, BufferedReader br) throws IOException {
        if (c == 32 || c == 9) { // 空格
            addInfo();
            state = 0; // 状态归零
        } else if (c == 13 || c == 10) { // 记录换行，行号++
            addInfo();
            state = 0; // 状态归零
        } else if (state == 0) {
            state = 3;
            token += (char) c;
            token += br.readLine();
            error.add(info);
            zeroing();
        } else if (state == 2) {
            state = 3;
            token += (char) c;
            token += br.readLine();
            error.add(info);
            zeroing();
        }
    }

    /**
     * 判断token是否是关键字
     *
     * @param token
     * @return
     */
    public static boolean isKey(String token) {
        if (token.equals("class") || token.equals("public") || token.equals("static") || token.equals("void") ||
                token.equals("main") || token.equals("String") || token.equals("extends") || token.equals("return") ||
                token.equals("int") || token.equals("boolean") || token.equals("if") || token.equals("else") ||
                token.equals("while") || token.equals("System.out.println") || token.equals("length") ||
                token.equals("true") || token.equals("false") || token.equals("this") || token.equals("new")) {
            return true;
        } else return false;
    }

    /**
     * 清空info token
     */
    public static void zeroing() {
        info = "";
        token = "";
    }

    /**
     * 添加info
     */
    public static void addInfo() {
        if (token.length() != 0 && state == 2 && isKey(token)) {
            info = token + ",";//关键字
            result+= info;
            zeroing();
        } else if (token.length() != 0 && state == 2) {
            info = token + ",";//标识符
            result+= info;
            zeroing();
        } else if (token.length() != 0 && state == 1) {
            info = token + ",";//数字
            result+= info;
            zeroing();
        } else if (token.length() != 0) {
            info = token + ","; //专用符号
            result+= info;
            zeroing();
        }
    }

    /**
     * 确定性有穷自动机
     *
     * @param c  当前读入的字符
     * @param br 文件读入
     * @throws IOException
     */
    public static void DFA(int c, BufferedReader br, BufferedWriter bw) throws IOException {
        switch (state) {
            case 0:
                if (isLetter(c)) {
                    state = 2;
                    break;
                } else if (isDigit(c)) {
                    state = 1;
                    break;
                } else if (isSymbol(c, br)) {
                    state = 3;
                    break;
                } else {
                    isEnd(c, br);
                    break;
                }

            case 1:
                if (isDigit(c)) {
                    state = 1;
                    break;
                } else if (isSymbol(c, br)) {
                    state = 3; // 结束状态1，进入状态3
                    break;
                } else if (isLetter(c)) {
                    state = 3; // 出错
                    token += (char) c;
                    token += br.readLine();
                    error.add(info);
                    zeroing();
                    break;
                } else {
                    isEnd(c, br);
                    break;
                }

            case 2:
                if (isUnderline(c) || isLetter(c) || isDigit(c)) {
                    state = 2;
                } else if (isSymbol(c, br)) {
                    state = 3;
                } else {
                    isEnd(c, br);
                }
                break;

            case 3:
                state = 0;
                if (isLetter(c)) {
                    state = 2;
                    break;
                } else if (isDigit(c)) {
                    state = 1;
                    break;
                } else if (isSymbol(c, br)) {
                    state = 3;
                    break;
                } else {
                    isEnd(c, br);
                    break;
                }
        }
    }

    public static BufferedReader StringToBufferedReader(String source) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(source.getBytes());
        InputStream inputStream = byteArrayInputStream;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader;

    }

    public static void token(String sourceCode) throws IOException {

        br = StringToBufferedReader(sourceCode);

        int c = 0;
        while (true) {
            c = br.read();
            if (c == -1) {
                addInfo();
                break;
            }
            DFA(c, br, bw);
        }
        br.close();

//       return result;
    }



    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
       token("setName(name);\n" +
               "String myName = getName();\n" +
               "String otherName = other.getName();\n" +
               "(myName\n" +
               "(otherName\n" +
               "(!myName.equals(otherName))");

           System.out.println(result);

    }

}

