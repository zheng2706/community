package com.heng.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);

    // 敏感词替换符
    private static final String REPLACEMENT="***";

    // 根节点
    private TireNode root=new TireNode();

    @PostConstruct
    public void init(){
        try (InputStream is=this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while ((keyword=reader.readLine())!=null){
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败："+e.getMessage());
        }
    }

    // 过滤敏感词
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        //指针1
        TireNode tempNode = root;
        //指针2
        int begin =0;
        //指针3
        int position =0;
        // 结果
        StringBuilder sb=new StringBuilder();
        while (position<text.length()){
            char c=text.charAt(position);
            //跳过符号
            if (isSymbol(c)){
                //若指针1处于根节点,将此符号计入结果，让指针2向下走一步
                if (tempNode==root){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }
            //检查下级节点
            tempNode=tempNode.getSubNode(c);
            if (tempNode==null){
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                position=++begin;
                tempNode=root;
            }else if (tempNode.isKeyWordEnd()){
                //发现敏感词，将begin-position字符串替换
                sb.append(REPLACEMENT);
                begin=++position;
                tempNode=root;
            }else {
                //检查下一个字符
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        // 0x2E80-0X9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0X9FFF);
    }

    //将一个敏感词添加到前缀树当中去
    private void addKeyword(String keyword){
        TireNode tempNode =root;
        for (int i=0;i<keyword.length();i++){
            char c =keyword.charAt(i);
            TireNode subNode=tempNode.getSubNode(c);
            if (subNode==null){
                subNode =new TireNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode=subNode;
            if(i==keyword.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    // 前缀树
    private class TireNode{
        // 关键词结束标识
        private boolean isKeyWordEnd =false;

        // 子节点(key是下级字符，value是下级节点)
        private Map<Character,TireNode> subNodes=new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点的方法
        public  void addSubNode(Character c,TireNode node){
            subNodes.put(c,node);
        }

        // 获取子节点
        public TireNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
