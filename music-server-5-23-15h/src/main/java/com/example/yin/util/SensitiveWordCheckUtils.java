package com.example.yin.util;

import com.example.yin.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description : 敏感词过滤工具
 */
@Component
@Slf4j
public class SensitiveWordCheckUtils {

    //最小匹配规则
    private final Integer MIN_MATCH_TYPE = 0;

    //最大匹配规则
    private final Integer MAX_MATCH_TYPE = -1;

    //敏感词DFA树关系标记key
    private final String IS_END = "isEnd";

    //不是敏感词的最后一个字符
    private final String END_FALSE = "0";

    //是敏感词的最后一个字符
    private final String END_TRUE = "1";

    //所有敏感词DFA树的列表
    private Map sensitiveWordMap = null;

    //敏感词文件存放路径
    private final String SENSITIVE_File_PATH = Constants.ASSETS_PATH + File.separator + Constants.SENSITIVE_FILE_NAME;

    //敏感词文件默认编码格式
    private final String DEFAULT_ENCODING = "utf-8";

    //忽略特殊字符的正则表达式   符号就忽略了
    private final String IGNORE_SPECIAL_CHAR_REGEX = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\s*";

    /**
     * 启动前将敏感词文件中的敏感词构建成DFA树
     */
    @PostConstruct
    private void initSensitiveWords() {
        //1、初始化前缀树存储的数据结构
        sensitiveWordMap = new ConcurrentHashMap();

        //2、遍历敏感词汇文件夹
        File dir = new File(SENSITIVE_File_PATH);

        //3、遍历文件夹下的所有敏感词文件
        if (dir.isDirectory() && dir.exists()) {
            for (File file : dir.listFiles()) {

                //4、将文件中的评论转成字符串
                Set<String> strings = readSensitiveWordFileToSet(file);

                if (!ObjectUtils.isEmpty(strings)) {
                    //5、文件里面有东西的话，就开始建树咯
                    createDFATree(strings);
                }
                System.out.println("将敏感词文件" + file.getName() + "加载到DFA树列表成功");
            }
            System.out.println("将敏感词文件全部加载完毕");
        } else {
            throw new RuntimeException(String.format("敏感词文件目录不存在{%s}", dir));
        }
    }

    /**
     * 读取文件中的敏感词
     *
     * @param file 敏感词文件
     * @return 敏感词set集合
     */
    private Set<String> readSensitiveWordFileToSet(File file) {
        Set<String> words = new HashSet<>();
        //1、读文件
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                //指定编码格式
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), DEFAULT_ENCODING));
                String line = "";
                while ((line = reader.readLine()) != null) {

                    //2、把每行放进集合中
                    String trim = line.trim();

                    //3、排除空行
                    if (!ObjectUtils.isEmpty(trim)) {
                        words.add(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("-----------从文件" + file.getName() + "中读取到" + words.size() + "个敏感词汇，已放入集合中------------");
        return words;
    }

    /**
     * 将敏感词构建成DFA树
     *
     * @param sensitiveWords 敏感词列表
     */
    private void createDFATree(Set<String> sensitiveWords) {
        //1、拿到一个文件中全部敏感词汇的迭代器
        Iterator<String> it = sensitiveWords.iterator();

        //2、开始遍历
        while (it.hasNext()) {

            //3、拿到敏感词汇
            String word = it.next();

            //4、java是引用方式，这里拿到地址下面改变的就是全局的  sensitiveWordMap
            Map currentMap = sensitiveWordMap; //拿到根

            //5、解决这个敏感词汇后面是标点符号的问题，防止标记不了叶子节点
            int last = word.length();
            for (int i = word.length() - 1; i >= 0; i--) {
                char key = word.charAt(i);
                if (isIgnore(key)) {
                    last--;
                } else {
                    break;
                }
            }

            // 6、检测每一个字符
            for (int i = 0; i < last; i++) {

                // 7、获取字符
                char key = word.charAt(i);

                // 8、那种标点符号就通过，因为检测的时候也是跳过的
                if (isIgnore(key)) {
                    continue;
                }

                // 9、看一下现在有没有 以 key  为根的子树
                Object oldValueMap = currentMap.get(key);

                // 10、如果没有，那么就创建一个，然后”递归“进去
                if (oldValueMap == null) {
                    // 不存在以key字符的DFA树则需要创建一个
                    Map newValueMap = new ConcurrentHashMap();

                    //由于这里是第一次放进去，默认不是叶子节点
                    newValueMap.put(IS_END, END_FALSE);

                    //创建子树成功
                    currentMap.put(key, newValueMap);

                    //引用方式真不错 ”递归“进去
                    currentMap = newValueMap;
                } else {

                    //”递归“进去
                    currentMap = (Map) oldValueMap;
                }

                // 11、如果当前是这个敏感词汇最后一个节点，那么说明他是叶子节点
                if (i == word.length() - 1) {
                    // 给最后一个字符添加结束标识
                    currentMap.put(IS_END, END_TRUE);
                }
            }
        }
    }

    /**
     * 按照最小规则获取文本中的敏感词(例如敏感词有[wcnm,wc],则在此规则下,获取到的敏感词为[wc])
     *
     * @param content 文本内容
     * @return 文本中所包含的敏感词set列表
     */
    public Set<String> getSensitiveWordMinMatch(String content) {
        return getSensitiveWord(content, MIN_MATCH_TYPE);
    }

    /**
     * 按照最大规则获取文本中的敏感词(例如敏感词有[wcnm,wc],则在此规则下,获取到的敏感词为[wc])
     *
     * @param content 文本内容
     * @return 文本中所包含的敏感词set列表
     */
    public Set<String> getSensitiveWordMaxMatch(String content) {
        return getSensitiveWord(content, MAX_MATCH_TYPE);
    }

    /**
     * 按照指定规则获取文本中的敏感词
     *
     * @param content 文本内容
     * @return 文本中所包含的敏感词set列表
     */
    private Set<String> getSensitiveWord(String content, int matchType) {
        //1、准备一个set放敏感词
        Set<String> sensitiveWordList = new HashSet<>();

        //2、开始检查
        for (int i = 0; i < content.length(); i++) {
            //3、检查敏感词,长度为0则表示文本中不包含敏感词  content中  以content[i]开头的敏感词
            SensitiveWordPosition sensitiveWordPosition = checkSensitiveWord(content, i, matchType);

            Integer length = sensitiveWordPosition.getLength();

            //4、说明找到敏感词
            if (length > 0) {
                //存起来
                sensitiveWordList.add(String.valueOf(sensitiveWordPosition.getSensitiveWord()));

                System.out.println("敏感词：" + sensitiveWordPosition.getSensitiveWord());

                // TODO 如果需要将敏感词换成**的话，可以在这里进行操作

                //i跳过敏感词那段
                i = i + length - 1;
            }
        }
        return sensitiveWordList;
    }

    /**
     * 从指定索引处检查文本中的敏感词
     *
     * @param content    文本内容
     * @param beginIndex 起始索引
     * @return 检索到的敏感词的长度
     */
    private SensitiveWordPosition checkSensitiveWord(String content, int beginIndex, int matchType) {
        // 敏感词结束标识位:用于敏感词只有一个字符的情况
        boolean flag = false;

        SensitiveWordPosition sensitiveWordPosition = new SensitiveWordPosition();

        //记录敏感词汇
        StringBuffer matchString = new StringBuffer();

        // 1、匹配到的敏感词的长度
        int matchedLength = 0;

        // 2、获取全局DFA树
        Map currentWordMap = sensitiveWordMap;

        for (int i = beginIndex; i < content.length(); i++) {
            char key = content.charAt(i);

            // 3、解决空格等特殊字符造成的漏匹配,比如   【 s,///,b歌手  】
            if (isIgnore(key)) {
                matchedLength++;
                continue;
            }

            // 4、获取当前字符的DFA树,树为空则表示不存在包含该字符的敏感词
            currentWordMap = (Map) currentWordMap.get(key);

            // 5、如果不存在key为标识的这棵子树，意味着全部敏感词在这个匹配阶段没有key开头的敏感词  直接返回
            if (currentWordMap == null) {
                break;
            } else {
                matchString.append(key);

                // 6、匹配到了，则匹配长度+1
                matchedLength++;

                // 7、判断是否是匹配中的敏感词的最后一位
                if (END_TRUE.equals(currentWordMap.get(IS_END))) {

                    //意味着当前存在敏感词汇
                    flag = true;

                    // 如果是最小匹配规则则直接返回(例如敏感词中有[出售,出售军刀],当匹配到"售"字符时,如果是最小规则则不继续向下匹配)
                    if (matchType == MIN_MATCH_TYPE) {
                        break;
                    }

                }
            }
        }

        // 8、长度小于1  或者  flag是false  意味着遍历完都没找到叶子节点
        if (matchedLength < 1 || !flag) {
            matchedLength = 0;
            sensitiveWordPosition.setSensitiveWord("");
            sensitiveWordPosition.setLength(0);
        } else {
            sensitiveWordPosition.setSensitiveWord(String.valueOf(matchString));
            sensitiveWordPosition.setLength(matchedLength);
        }

        // 9、返回长度
        return sensitiveWordPosition;
    }

    /**
     * 判断是否是要忽略的字符(忽略所有特殊字符以及空格)
     *
     * @param specificChar 指定字符
     * @return 特殊字符或空格true否则false
     */
    private boolean isIgnore(char specificChar) {
        //1、获得Pattern 实例
        Pattern pattern = Pattern.compile(IGNORE_SPECIAL_CHAR_REGEX);

        //2、返回Matcher 实例
        Matcher matcher = pattern.matcher(String.valueOf(specificChar));

        //3、对整个字符串进行匹配
        return matcher.matches();
    }
}